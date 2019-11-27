<#
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process
#>

Function Optimize-PropertiesFile {
    [CmdletBinding()]
    [OutputType([System.Collections.Generic.Dictionary[System.String,System.Collections.ObjectModel.Collection[System.String]]])]
    Param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    $Properties = New-Object -TypeName 'System.Collections.Generic.Dictionary[System.String,System.Collections.ObjectModel.Collection[System.String]]';
    $LineNumber = 0;
    $ErrorCount = 0;
    $HasContinuation = $false;
    $OriginalPropertyOrder = New-Object -TypeName 'System.Collections.ObjectModel.Collection[System.String]';
    $Collection = $null;
    (((Get-Content -LiteralPath $Path -Encoding UTF8 -ErrorAction Stop) | Out-String) -split '\r\n?|\n') | ForEach-Object {
        $LineNumber++;
        if ($HasContinuation) {
            if ($_.EndsWith('\')) {
                $Collection.Add($_.Substring($_.Length - 1));
            } else {
                $Collection.Add($_);
                $IsContinuation = $false;
            }
        } else {
            $kvp = $_.Split('=', 2);
            if ($kvp.Length -eq 2) {
                $key = $kvp[0].Trim();
                if ($key.Trim().Length -eq 0) {
                    Write-Information -MessageData "Empty key on line $LineNumber" -InformationAction Continue;
                    $ErrorCount++;
                } else {
                    if ($Properties.ContainsKey($key)) {
                        Write-Information -MessageData "Duplicate key `"$Key`" on line $LineNumber" -InformationAction Continue;
                        $ErrorCount++;
                    } else {
                        if ($kvp[0] -imatch '^[a-z][a-z\d_]+$') {
                            $OriginalPropertyOrder.Add($kvp[0]);
                            $Collection = New-Object -TypeName 'System.Collections.ObjectModel.Collection[System.String]';
                            if ($kvp[1].EndsWith('\')) {
                                $Collection.Add($kvp[1].Substring($kvp[1].Length - 1));
                                $HasContinuation = $true;
                            } else {
                                $Collection.Add($kvp[1]);
                            }
                            $Properties[$key] = $Collection;
                        } else {
                            Write-Information -MessageData "Suspicious key format `"$Key`" on line $LineNumber" -InformationAction Continue;
                            $ErrorCount++;
                        }
                    }
                }
            } else {
                if ($_.Trim().Length -gt 0) {
                    Write-Information -MessageData "Empty key on line $LineNumber" -InformationAction Continue;
                    $ErrorCount++;
                }
            }
        }
    }
    if ($HasContinuation) {
        Write-Information -MessageData 'Last line ends with \, but is not followed by another line.' -InformationAction Continue;
        $ErrorCount++;
    }
    $ncMatch = @($Properties.Keys | Group-Object | Where-Object { $_.Count -gt 1 });
    if ($ncMatch.Count -gt 0) {
        $ErrorCount += $ncMatch.Count;
        $ncMatch | ForEach-Object { Write-Information -MessageData "Keys differing only in case: $($_.Group -join ', ')" -InformationAction Continue };
    }
    if ($ErrorCount -gt 0) {
        Write-Warning -Message "$ErrorCount error(s) encountered. File $Path not modified.";
    } else {
        if ($Properties.Count -eq 0) {
            Write-Warning -Message "No properties found. File $Path not modified.";
        } else {
            if ($Properties.Count -eq 1) {
                Write-Information -MessageData "Only 1 property found. File $Path does not need to be modified." -InformationAction Continue;
            } else {
                $SortedProperyNames = @($Properties.Keys | Sort-Object);
                if (($SortedProperyNames -join ',') -ceq ($OriginalPropertyOrder -join ',')) {
                    Write-Information -MessageData "Properties were already sorted. File $Path does not need to be modified." -InformationAction Continue;
                } else {
                    $Destination = $PSScriptRoot | Join-Path -ChildPath "$($Path | Split-Path -Leaf).bak";
                    Copy-Item -LiteralPath $Path -Destination $Destination -ErrorAction Stop -Force;
                    Write-Information -MessageData "$Path backed up to $Destination" -InformationAction Continue;
                    $LineCount = 0;
                    [System.IO.File]::WriteAllLines($Path, ($SortedProperyNames | ForEach-Object {
                        $Collection = $Properties[$_];
                        $LineCount += $Collection.Count;
                        if ($Collection.Count -gt 1) {
                            "$_=$($Collection[0])\";
                            if ($Collection.Count -gt 2) {
                                (($Collection | Select-Object -Skip 1) | Select-Object -SkipLast 1) | ForEach-Object { "$_\" }
                            }
                            $Collection | Select-Object -Last 1;
                        } else {
                            "$_=$($Collection[0])";
                        }
                    }), (New-Object -TypeName 'System.Text.UTF8Encoding' -ArgumentList $false, $false));
                    "Sorted $($Properties.Count) properties ($LineCount lines) to $Path" | Write-Host;
                }
            }
        }
        return $Properties;
    }
}

$ResourceBundles = New-Object -TypeName 'System.Collections.Generic.Dictionary[System.String,System.Collections.ObjectModel.Collection[System.Management.Automation.PSObject]]' -ArgumentList ([System.StringComparer]::InvariantCultureIgnoreCase);
(Get-ChildItem -Path ($PSScriptRoot | Join-Path -ChildPath 'Scheduler\src') -Filter '*.properties' -ErrorAction Stop) | ForEach-Object {
    $i = $_.BaseName.LastIndexOf('_');
    if ($i -gt 0 -and $i -lt $_.BaseName.Length - 1) {
        $RbColl = $null;
        $BundleName = $_.BaseName.Substring(0, $i);
        if ($ResourceBundles.ContainsKey($BundleName)) {
            $RbColl = $ResourceBundles[$BundleName];
        } else {
            $RbColl = New-Object -TypeName 'System.Collections.ObjectModel.Collection[System.Management.Automation.PSObject]';
            $ResourceBundles.Add($BundleName, $RbColl);
        }
        $RbColl.Add((New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
            Path = $_.FullName;
            Locale = $_.BaseName.Substring($i + 1);
        }));
    } else {
        Write-Warning -Message "Skipping $(), which does seem to have locale encoded into the name";
    }
}
$ResourceBundles.Keys | ForEach-Object {
    $Dictionaries = @($ResourceBundles[$_] | ForEach-Object {
        $d = Optimize-PropertiesFile -Path $_.Path;
        if ($null -ne $d) {
            New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
                Source = $_;
                Dictionary = $d;
            };
        }
    });
    if ($Dictionaries.Count -gt 0) {
        $AllKeys = @($Dictionaries | ForEach-Object { $_.Dictionary.Keys } | Select-Object -Unique);
        foreach ($key in $AllKeys) {
            $Dictionaries | ForEach-Object {
                if (-not $_.Dictionary.ContainsKey($Key)) {
                    Write-Warning -Message "$($_.Source.Path) does not contain property name $Key";
                }
            }
        }
    }
}
Write-Information -MessageData "Finished." -InformationAction Continue;