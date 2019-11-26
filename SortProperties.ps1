<#
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process
#>
(Get-ChildItem -Path ($PSScriptRoot | Join-Path -ChildPath 'Scheduler\src') -Filter '*.properties' -ErrorAction Stop) | ForEach-Object {
    $Properties = New-Object -TypeName 'System.Collections.Generic.Dictionary[System.String,System.Collections.ObjectModel.Collection[System.String]]';
    $LineNumber = 0;
    $ErrorCount = 0;
    $HasContinuation = $false;
    $OriginalPropertyOrder = New-Object -TypeName 'System.Collections.ObjectModel.Collection[System.String]';
    $Collection = $null;
    (((Get-Content -LiteralPath $_.FullName -Encoding UTF8 -ErrorAction Stop) | Out-String) -split '\r\n?|\n') | ForEach-Object {
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
        Write-Warning -Message "$ErrorCount error(s) encountered. File $($_.FullName) not modified.";
    } else {
        if ($Properties.Count -eq 0) {
            Write-Warning -Message "No properties found. File $($_.FullName) not modified.";
        } else {
            if ($Properties.Count -eq 1) {
                Write-Information -MessageData "Only 1 property found. File $($_.FullName) does not need to be modified." -InformationAction Continue;
            } else {
                $SortedProperyNames = @($Properties.Keys | Sort-Object);
                if (($SortedProperyNames -join ',') -ceq ($OriginalPropertyOrder -join ',')) {
                    Write-Information -MessageData "Properties were already sorted. File $($_.FullName) does not need to be modified." -InformationAction Continue;
                } else {
                    $Destination = $PSScriptRoot | Join-Path -ChildPath "$($_.Name).bak";
                    Copy-Item -LiteralPath $_.FullName -Destination $Destination -ErrorAction Stop;
                    Write-Information -MessageData "$($_.FullName) backed up to $Destination" -InformationAction Continue;
                    $LineCount = 0;
                    Set-Content -Value ($SortedProperyNames | ForEach-Object {
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
                    }) -LiteralPath $_.FullName -Encoding UTF8 -ErrorAction Stop -Force;
                    "Sorted $($Properties.Count) properties ($LineCount lines) to $($_.FullName)" | Write-Host;
                }
            }
        }
    }
}
Write-Information -MessageData "Finished." -InformationAction Continue;