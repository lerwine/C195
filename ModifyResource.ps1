$Script:Enc = New-Object -TypeName 'System.Text.UTF8Encoding' -ArgumentList $false, $false;

Function Open-ResourceFile {
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )
    [string[]]$Heading = @();
    $Hash = @{};
    [string[]]$CommentLines = @();
    if ([System.IO.File]::Exists($Path)) {
        $Lines = @([System.IO.File]::ReadLines($Path, $Script:Enc));
        while ($Lines.Count -gt 0) {
            $s = $Lines[0].Trim();
            if ($s.Length -eq 0) {
                [string[]]$Heading = $Heading + $CommentLines + @($_);
                [string[]]$CommentLines = @();
            } else {
                if ($s.StartsWith('#') -or $s.StartsWith('=') -or -not $s.Contains('=')) {
                    [string[]]$CommentLines = $CommentLines + @($_);
                } else {
                    break;
                }
            }
            if ($Lines.Count -eq 1) {
                $Lines = @();
                break;
            }
            $Lines = @($Lines | Select-Object -Skip 1);
        }
        if ($Lines.Count -gt 0) {
            $Lines | ForEach-Object {
                if ($_.Trim().Length -eq 0) {
                    [string[]]$CommentLines = $CommentLines + @($_);
                } else {
                    $i = $_.IndexOf('=');
                    if ($i -lt 1) {
                        [string[]]$CommentLines = $CommentLines + @($_);
                    } else {
                        $k = $_.Substring(0, $i);
                        $v = $k.TrimStart();
                        if ($v.Length -eq 0 -or $v.StartsWith('#')) {
                            [string[]]$CommentLines = $CommentLines + @($_);
                        } else {
                            if ($i -eq $_.Length - 1) {
                                $Hash[$k] = New-Object -TypeName 'System.Management.Automation.PSObject' -ArgumentList @{
                                    Comments = $CommentLines;
                                    Value = '';
                                };
                            } else {
                                $Hash[$k] = New-Object -TypeName 'System.Management.Automation.PSObject' -ArgumentList @{
                                    Comments = $CommentLines;
                                    Value = $_.Substring($i + 1);
                                };
                            }
                        }
                    }
                }
            }
        }
    }
    New-Object -TypeName 'System.Management.Automation.PSObject' -ArgumentList @{
        Heading = $Heading;
        Content = $Hash;
        Tail = $CommentLines;
    };
}

Function Save-ResourceFile {
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $true)]
        [string]$Path,
        [Parameter(Mandatory = $true)]
        [Object]$ResourceFile
    )

    
    [System.IO.File]::WriteAllLines($Path, ([string[]]@($ResourceFile.Heading + @($ResourceFile.Content.Keys | Sort-Object | ForEach-Object {
        if ($ResourceFile.Content[$_].Comments.Length -gt 0) { $ResourceFile.Content[$_].Comments }
        "$_=$($ResourceFile.Content[$_].Value)";
    }) + $ResourceFile.Tail)));
}

Function Copy-ResourceKeys {
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $true)]
        [string]$SourceBaseName,
        
        [Parameter(Mandatory = $true)]
        [string]$TargetBaseName,

        [Parameter(Mandatory = $true)]
        [string[]]$Properties,

        [string[]]$Locale = @('en', 'de', 'hi', 'es'),

        [switch]$Move
    )

    $SourceHash = @{};
    $TargetHash = @{};
    $Locale | ForEach-Object {
        $SourceHash[$_] = Open-ResourceFile -Path "$SourceBaseName`_$_.properties";
        $TargetHash[$_] = Open-ResourceFile -Path "$TargetBaseName`_$_.properties";
    }
    $Properties = @($Properties | Where-Object {
        $n = $_;
        @($Locale | Where-Object { $SourceHash[$_].Content.ContainsKey($n) }).Count -gt 0;
    });
    if ($Properties.Count -gt 0) {
        $Locale | ForEach-Object {
            $s = $SourceHash[$_];
            $t = $TargetHash[$_];
            $Properties | ForEach-Object {
                if ($s.Content.ContainsKey($_)) {
                    $t.Content[$_] = $s.Content[$_];
                    if ($Move.IsPresent) {
                        $s.Content.Remove($_);
                    }
                } else {
                    $t.Content[$_] = New-Object -TypeName 'System.Management.Automation.PSObject' -ArgumentList @{
                        Comments = ([string[]]@());
                        Value = '';
                    };
                }
            }
            if ($Move.IsPresent) { Save-ResourceFile -Path "$SourceBaseName`_$_.properties2" -ResourceFile $SourceHash[$_] }
            Save-ResourceFile -Path "$TargetBaseName`_$_.properties2" -ResourceFile $TargetHash[$_];
        }
    }
    $Hash = $TargetHash[$Locale[0]].Content;
    $Lines = @($Hash.Keys | Sort-Object | ForEach-Object {
        '';
        "    /**";
        "     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code `"$($Hash[$_].Value)`"}.";
        "     */";
        "    public static final String RESOURCEKEY_$($_.ToUpper()) = `"$_`";";
    });
    if ($Move.IsPresent) {
        $Hash = $SourceHash[$Locale[0]].Content;
        $Lines = @("    //<editor-fold defaultstate=`"collapsed`" desc=`"$($SourceBaseName | Split-Path -Leaf) Resource bundle keys`">") + @($Hash.Keys | Sort-Object | ForEach-Object {
            '';
            "    /**";
            "     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code `"$($Hash[$_].Value)`"}.";
            "     */";
            "    public static final String RESOURCEKEY_$($_.ToUpper()) = `"$_`";";
        }) + @('', '    //</editor-fold>', '', "    //<editor-fold defaultstate=`"collapsed`" desc=`"$($TargetBaseName | Split-Path -Leaf) Resource bundle keys`">") + $Lines +  + @('', '    //</editor-fold>');
    }
    [System.Windows.Clipboard]::SetText(($Lines | Out-String).Trim());
}

Copy-ResourceKeys -SourceBaseName ($PSScriptRoot | Join-Path -ChildPath 'Scheduler\src\scheduler\App') `
    -TargetBaseName ($PSScriptRoot | Join-Path -ChildPath 'Scheduler\src\scheduler\view\appointment\ManageAppointments') `
    -Properties 'all', 'appointmentType_customer', 'appointmentType_DE', 'appointmentType_HN', 'appointmentType_IN', 'appointmentType_other', 'appointmentType_phone',
    'appointmentType_US', 'appointmentType_virtual', 'cancel', 'none', 'type';