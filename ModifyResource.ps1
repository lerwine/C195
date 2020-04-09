<#
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope CurrentUser;
#>
$Script:Enc = New-Object -TypeName 'System.Text.UTF8Encoding' -ArgumentList $false, $false;
$Script:ResourcesBasePath = $PSScriptRoot | Join-Path -ChildPath 'Scheduler\resources\scheduler';
$Script:ConstantsBasePath = $PSScriptRoot | Join-Path -ChildPath 'Scheduler\src\scheduler';
$Script:Locales = @('en', 'de', 'hi', 'es');
$Script:LocaleRegex = New-Object -TypeName 'System.Text.RegularExpressions.Regex' -ArgumentList '^(?<b>(?<r>.+?)_(?<l>[a-z][a-z]+(_[a-z\d]+)*))(\.(?<p>properties)|(?<j>java))?$', ([System.Text.RegularExpressions.RegexOptions]::IgnoreCase);

Function Split-LocaleString {
    [CmdletBinding(DefaultParameterSetName = 'Locale')]
    Param(
        [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
        [String]$InputPath,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'ResourceBase')]
        # Resource name when locale is present
        [switch]$ResourceBase,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'BaseName')]
        # File name without extension
        [switch]$BaseName,
        
        [Parameter(ParameterSetName = 'Locale')]
        # Locale
        [switch]$Locale,
        
        [Parameter(ParameterSetName = 'BaseName')]
        [switch]$RequireLocale,
        
        [Parameter(ParameterSetName = 'ResourceBase')]
        [Parameter(ParameterSetName = 'BaseName')]
        [switch]$IncludeDirectory,
        
        [Parameter(ParameterSetName = 'Locale')]
        [Parameter(ParameterSetName = 'ResourceBase')]
        [switch]$AssumeNoExt
    )

    Process {
        $BN = $FileName = $InputPath | Split-Path -Leaf;
        $m = $Script:LocaleRegex.Match($BN);
        if ($BaseName.IsPresent) {
            if ($m.Success) {
                if ($m.Groups['p'].Success -or $m.Groups['j'].Success) {
                    $BN = $m.Groups['b'].Value;
                }
            } else {
                if ($RequireLocale.IsPresent) { $BN = '' } else { $BN = [System.IO.Path]::GetFileNameWithoutExtension($BN) }
            }
        } else {
            if ($m.Success -and -not ($m.Groups['j'].Success -or ($AssumeNoExt.IsPresent -and $m.Groups['p'].Success))) {
                if ($ResourceBase.IsPresent) {
                    $BN = $m.Groups['r'].Value;
                } else {
                    $BN = $m.Groups['l'].Value;
                }
            } else {
                $BN = '';
            }
        }
        if ($BN.Length -gt 0 -and $IncludeDirectory.IsPresent) {
            $p = $InputPath | Split-Path -Parent;
            if ([string]::IsNullOrEmpty($p)) {
                $BN | Write-Output;
            } else {
                ($p | Join-Path -ChildPath $BN) | Write-Output;
            }
        } else {
            $BN | Write-Output;
        }
    }
}

Function Test-IsValidPropertiesPath {
    [CmdletBinding(DefaultParameterSetName = 'Base')]
    Param(
        [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
        [AllowNull()]
        [AllowEmptyString()]
        [String]$InputPath,
        
        [Parameter(ParameterSetName = 'Base')]
        [switch]$Base,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'Globalization')]
        [switch]$Globalization,

        [Parameter(Mandatory = $true, ParameterSetName = 'Constants')]
        [switch]$Constants,

        [switch]$Warn
    )

    Begin {
        $Success = $null;
    }
    Process {
        if ($null -eq $Success) { $Success = $true }
        if ($null -eq $InputPath -or $InputPath.Trim().Length -eq 0) {
            if ($Warn.IsPresent) { Write-Warning -Message 'Path is empty' }
            $Success = $false;
        } else {
            $FullPath = '';
            $Ext = '.properties';
            if ($Constants.IsPresent) {
                $Ext = '.java';
                $FullPath = $Script:ConstantsBasePath | Join-Path -ChildPath $InputPath;
                if ([System.IO.Path]::GetExtension($InputPath).Length -eq 0) { $InputPath += $Ext }
            } else {
                $FullPath = $Script:ResourcesBasePath | Join-Path -ChildPath $InputPath;
                if ($Globalization.IsPresent -and [System.IO.Path]::GetExtension($InputPath).Length -eq 0) { $InputPath += $Ext }
            }
                
            if ($FullPath | Test-Path) {
                if (-not ($FullPath | Test-Path -PathType Leaf)) {
                    if ($Warn.IsPresent) { Write-Warning -Message "`"$InputPath`" is not a file." }
                    $Success = $false;
                }
                if ($base.IsPresent) {
                    if ($Warn.IsPresent) { Write-Warning -Message "`"$InputPath`" refers to an actual file and not a base name." }
                    $Success = $false;
                }
            } else {
                $p = $FullPath | Split-Path -Parent;
                if ([string]::IsNullOrEmpty($p) -or -not ($p | Test-Path -PathType Container)) {
                    if ($Warn.IsPresent) { Write-Warning -Message "Parent directory of `"$InputPath`" does not exist." }
                    $Success = $false;
                }
            }
                
            if ($Success) {
                if ($Base.IsPresent) {
                    if (($InputPath | Split-LocaleString -AssumeNoExt).Length -gt 0) {
                        if ($Warn.IsPresent) { Write-Warning -Message "`"$InputPath`" ends with a locale string" }
                        $Success = $false;
                    } else {
                        if ($InputPath.EndsWith('_')) {
                            if ($Warn.IsPresent) { Write-Warning -Message "`"$InputPath`" ends with an underscore" }
                            $Success = $false;
                        }
                    }
                } else {
                    if ([System.IO.Path]::GetExtension($InputPath) -ieq $Ext) {
                        if ($Globalization.IsPresent) {
                            $n = $InputPath | Split-LocaleString;
                            if ([string]::IsNullOrEmpty($n)) {
                                if ($Warn.IsPresent) { Write-Warning -Message "Base name of `"$InputPath`" does specify a locale string" }
                                $Success = $false;
                            } else {
                                if ($Script:Locales -inotcontains $n) {
                                    if ($Warn.IsPresent) { Write-Warning -Message "Base name of `"$InputPath`" does specify a supported locale string" }
                                    $Success = $false;
                                }
                            }
                        } else {
                            if ($Constants.IsPresent -and -not [string]::IsNullOrEmpty(($InputPath | Split-LocaleString))) {
                                if ($Warn.IsPresent) { Write-Warning -Message "Base name of `"$InputPath`" ends with a locale string" }
                                $Success = $false;
                            }
                        }
                    } else {
                        if ($Warn.IsPresent) { Write-Warning -Message "`"$InputPath`" is not a `"$Ext`" file." }
                        $Success = $false;
                    }
                }
            }
        }
    }
    End { ($null -ne $Success -and $Success) | Write-Output }
}

Function Open-PropertiesFile {
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $true)]
        [ValidateScript({ $_ | Test-IsValidPropertiesPath  })]
        [string]$Path
    )
    
    if ([string]::IsNullOrEmpty([System.IO.Path]::GetExtension($Path))) { $Path = "$Path.properties" }
    $FullPath = $Script:ResourcesBasePath | Join-Path -ChildPath $Path;
    [string[]]$Heading = @();
    $Hash = @{};
    [string[]]$CommentLines = @();
    if ([System.IO.File]::Exists($FullPath)) {
        $Lines = @([System.IO.File]::ReadLines($FullPath, $Script:Enc));
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

Function Save-PropertiesFile {
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $true)]
        [ValidateScript({ $_ | Test-IsValidPropertiesPath })]
        [string]$Path,

        [Parameter(Mandatory = $true)]
        [Object]$PropertiesFile
    )
    
    if ([string]::IsNullOrEmpty([System.IO.Path]::GetExtension($Path))) { $Path = "$Path.properties" }
    $FullPath = $Script:ResourcesBasePath | Join-Path -ChildPath $Path;

    if ($FullPath | Test-Path -PathType Leaf) {
        $Dir = $FullPath | Split-Path -Parent;
        $b = [System.IO.Path]::GetFileNameWithoutExtension(($FullPath | Split-Path -Leaf));
        $i = 0;
        $p = $Dir | Join-Path -ChildPath "$b.properties.bak";
        while ($p | Test-Path) {
            $i++;
            $p = $Dir | Join-Path -ChildPath "$b.properties.bak$i";
        }
        [System.IO.File]::Copy($FullPath, $p);
    }
    
    [System.IO.File]::WriteAllLines(($Script:ResourcesBasePath | Join-Path -ChildPath $Path), ([string[]]@($PropertiesFile.Heading + @($PropertiesFile.Content.Keys | Sort-Object | ForEach-Object {
        if ($PropertiesFile.Content[$_].Comments.Length -gt 0) { $PropertiesFile.Content[$_].Comments }
        "$_=$($PropertiesFile.Content[$_].Value)";
    }) + $PropertiesFile.Tail)));
}

Function Save-ConstantsFile {
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $true)]
        [ValidateScript({ $_ | Test-IsValidPropertiesPath -Constants })]
        [string]$Path,

        [Parameter(Mandatory = $true)]
        [Object]$PropertiesFile
    )
    $p = 'view\appointment\EditAppointment';
    $ns = '';
    $p = $p | Split-Path -Parent;
    while (-not [string]::IsNullOrEmpty($p)) {
        $ns = ".$($p | Split-Path -Leaf)$ns";
        $p = $p | Split-Path -Parent;
    }
    if ([string]::IsNullOrEmpty([System.IO.Path]::GetExtension($Path))) { $Path = "$Path.properties" }
    $FullPath = $Script:ResourcesBasePath | Join-Path -ChildPath $Path;

    if ($FullPath | Test-Path -PathType Leaf) {
        $Dir = $FullPath | Split-Path -Parent;
        $b = [System.IO.Path]::GetFileNameWithoutExtension(($FullPath | Split-Path -Leaf));
        $i = 0;
        $p = $Dir | Join-Path -ChildPath "$b.properties.bak";
        while ($p | Test-Path) {
            $i++;
            $p = $Dir | Join-Path -ChildPath "$b.properties.bak$i";
        }
        [System.IO.File]::Copy($FullPath, $p);
    }
    
    $Lines = @("package scheduler$ns;", '', '/**', " * Defines resource bundle keys for the App resource bundle {@code $Path}.", ' *', ' * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>', ' */',
        "public interface $([System.IO.Path]::GetFileNameWithoutExtension($Path)) {");
    $PropertiesFile.Content.Keys | Sort-Object | ForEach-Object {
        $Lines += @('', '    /**', "     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code `"$($PropertiesFile.Content[$_].Value)`"}.",
        '     */', "    public static final String RESOURCEKEY_$($_.ToUpper()) = `"$_`";");
    }
    $Lines += @('', '}');
    [System.IO.File]::WriteAllLines($FullPath, $Lines);
}

Function Copy-ResourceKeys {
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $true)]
        [ValidateScript({ $_ | Test-IsValidPropertiesPath -Base })]
        [string]$SourceBaseName,
        
        [Parameter(Mandatory = $true)]
        [ValidateScript({ $_ | Test-IsValidPropertiesPath -Base })]
        [string]$TargetBaseName,

        [Parameter(Mandatory = $true)]
        [string[]]$Properties,

        [string[]]$Locale = @('en', 'de', 'hi', 'es'),

        [switch]$Move
    )

    $SourceHash = @{};
    $TargetHash = @{};
    $Locale | ForEach-Object {
        $SourceHash[$_] = Open-PropertiesFile -Path "$SourceBaseName`_$_.properties";
        $TargetHash[$_] = Open-PropertiesFile -Path "$TargetBaseName`_$_.properties";
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
            if ($Move.IsPresent) {
                Save-PropertiesFile -Path "$SourceBaseName`_$_.properties" -PropertiesFile $SourceHash[$_];
                Save-ConstantsFile -Path "$SourceBaseName`ResourceBundleConstants.java" -PropertiesFile $SourceHash[$_];
            }
            Save-PropertiesFile -Path "$TargetBaseName`_$_.properties" -PropertiesFile $TargetHash[$_];
            Save-ConstantsFile -Path "$TargetBaseName`ResourceBundleConstants.java" -PropertiesFile $TargetHash[$_];
        }
    }
}

Copy-ResourceKeys -SourceBaseName 'view\appointment\EditAppointment' -TargetBaseName 'App' -Properties 'loadingAppointments', 'errorLoadingAppointments' -Move;
#'view\appointment\EditAppointment_es.properties' | Test-IsValidPropertiesPath -Warn