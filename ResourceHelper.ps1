
$Script:BaseResourcesPath = 'C:\Users\lerwi\OneDrive\Documents\NetBeansProjects\C195\Scheduler\resources\scheduler';
$Script:BaseCodePath = 'C:\Users\lerwi\OneDrive\Documents\NetBeansProjects\C195\Scheduler\src\scheduler';
$Script:AllLocales = @('en', 'es', 'de', 'hi');

Function Convert-RelativePathToFullPath {
    [CmdletBinding(DefaultParameterSetName = 'Resource')]
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
        [string[]]$InputPath,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'Resource')]
        [switch]$Resource,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'Code')]
        [switch]$Code
    )

    Begin {
        $BasePath = $Script:BaseResourcesPath;
        if ($Code.IsPresent) { $BasePath = $Script:BaseCodePath }
    }

    Process {
        $InputPath | ForEach-Object { $BasePath | Join-Path -ChildPath $_ }
    }
}

Function Test-PropertiesFilePath {
    [CmdletBinding(DefaultParameterSetName = 'Resource')]
    Param(
        [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
        [AllowNull()]
        [AllowEmptyString()]
        [AllowEmptyCollection()]
        [string[]]$InputPath,
        
        [Parameter(ParameterSetName = 'Resource')]
        # Path is relative to the resources folder.
        [switch]$Resource,
        
        [Parameter(Mandatory = $true, ParameterSetName = 'Code')]
        # Path is relative to the code folder.
        [switch]$Code
    )
    
    Begin {
        $Success = $null;
    }

    Process {
        if ($null -eq $Success) { $Success = $true }
        if ($Success) {
            if ($null -eq $InputPath -or $InputPath.Length -eq 0) {
                $Success = $false;
            } else {
                $FullPaths = @();
                if ($Resource.IsPresent) {
                    $FullPaths = @($InputPath | ForEach-Object {
                        if ([string]::IsNullOrWhiteSpace($_)) { '' } else { $Script:BaseResourcesPath | Join-Path -ChildPath $_ }
                    });
                } else {
                    if ($Code.IsPresent) {
                        $FullPaths = @($InputPath | ForEach-Object {
                            if ([string]::IsNullOrWhiteSpace($_)) { '' } else { $Script:BaseCodePath | Join-Path -ChildPath $_ }
                        });
                    } else {
                        $FullPaths = @($InputPath | ForEach-Object {
                            if ([string]::IsNullOrWhiteSpace($_)) { '' } else { $_ }
                        });
                    }
                }
                foreach ($f  in $FullPaths) {
                    if ($f.Length -eq 0) {
                        $Success = $false;
                        break;
                    }
                    if (-not ($f | Test-Path -PathType Leaf)) {
                        if ($f | Test-Path -PathType Container) {
                            $Success = $false;
                            break;
                        }
                        $p = $f | Split-Path -Parent;
                        if ([string]::IsNullOrEmpty($p) -or -not ($p | Test-Path -PathType Container)) {
                            $Success = $false;
                            break;
                        }
                    }
                }
            }
        }
    }

    End { ($Success -eq $true) | Write-Output }
}

$Script:LineBreakRegex = [System.Text.RegularExpressions.Regex]::new('\r\n?|\n');
$Script:StripCommentRegex = [System.Text.RegularExpressions.Regex]::new('^\s*# ?');
$Script:PropertyLineRegex = [System.Text.RegularExpressions.Regex]::new('(?=\s*#)(?<c>\s*#.*)|(?<k>(\\.|[^=\\]+)*(=(?==))?)=(?<v>(\\.|[^=\\]+)*)(?<p>\\)?');
$Script:UnescapeRegex = [System.Text.RegularExpressions.Regex]::new('\G(\\((?<c>[bntrf])|u(?<u>[a-fA-F\d]{4})|(?<o>[0-3]([0-7][0-7]?)?|[0-7][0-7]?)|(?<l>.[^=\\]*))|(?<l>[^=\\]+|=$))');

Function Test-PropertiesFile {
    [CmdletBinding(DefaultParameterSetName = 'Relative')]
    Param(
        [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
        [AllowNull()]
        [AllowEmptyString()]
        [AllowEmptyCollection()]
        [Object[]]$InputObject
    )
    
    Begin {
        $Success = $null;
    }

    Process {
        if ($null -eq $Success) { $Success = $true }
        if ($Success) {
            if ($null -eq $InputObject -or $InputObject.Length -eq 0) {
                $Success = $false;
            } else {
                foreach ($o in $InputObject) {
                    if ($null -eq $o -or $null -eq $o.Properties -or $null -eq $o.Path -or $null -eq $o.Footer `
                        -or $o.Properties -isnot [System.Collections.Generic.Dictionary[System.String, [System.Tuple[System.String,System.String[]]]]] -or $o.Path -isnot [string] `
                        -or $o.Footer -isnot [string[]] -or -not ($o.Path | Test-PropertiesFilePath)) {
                        $Success = $false;
                        break;
                    }
                }
            }
        }
    }

    End { ($Success -eq $true) | Write-Output }
}

Function ConvertFrom-EscapedPropertiesText {
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $true)]
        [AllowEmptyString()]
        [string]$Text
    )

    if ($Text.Trim().Length -eq 0) {
        New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
            Text = $Text;
            IsContinued = $false;
        }
    } else {
        if ($Text -eq '\') {
            New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
                Text = '';
                IsContinued = $true;
            }
        } else {
            $Unescaped = '';
            $e = 1;
            $mc = $Script:UnescapeRegex.Matches($Text);
            if ($mc.Count -eq 0) {
                if ($Text.Length -gt 1) {
                    $Unescaped = $Text.Substring(0, 1);
                } else {
                    $Unescaped = $Text;
                }
            } else {
                $Unescaped = -join ($mc | ForEach-Object {
                    if ($_.Groups['c'].Success) {
                        switch ($_.Groups['c'].Value) {
                            'b' {
                                "`b";
                                break;
                            }
                            'n' {
                                "`n";
                                break;
                            }
                            't' {
                                "`t";
                                break;
                            }
                            'r' {
                                "`r";
                                break;
                            }
                            'f' {
                                "`f";
                                break;
                            }
                        }
                    } else {
                        if ($_.Groups['u'].Success) {
                            ([char]([int]::Parse($_.Groups['u'].Value, [System.Globalization.NumberStyles]::HexNumber)));
                        } else {
                            if ($_.Groups['o'].Success) {
                                $o = $_.Groups['o'].Value;
                                if ($o.Length -eq 1) {
                                    ([char]([int]::Parse($_.Groups['o'].Value)));
                                } else {
                                    $d = ([int]::Parse($_.Groups['o'].Value.Substring(0, 1)) * 8) + [int]::Parse($_.Groups['o'].Value.Substring(1, 1));
                                    if ($o.Length -gt 2) {
                                        ([char](($d * 8) + [int]::Parse($_.Groups['o'].Value.Substring(2))));
                                    } else {
                                        ([char]$d);
                                    }
                                }
                            } else {
                                $_.Groups['l'].Value;
                            }
                        }
                    }
                });
                $e = $mc[$mc.Count - 1].Index + $mc[$mc.Count - 1].Length;
            }
            if ($e -lt $Text.Length) {
                $s = $Text.Substring($e);
                if ($s -eq '\') {
                    New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
                        Text = $Unescaped;
                        IsContinued = $true;
                    }
                } else {
                    if ($s.Length -eq 1) {
                        New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
                            Text = $Unescaped + $s;
                            IsContinued = $true;
                        }
                    } else {
                        $Unescaped += $s.Substring(0, 1);
                        $UnescapedPropertiesText = ConvertFrom-EscapedPropertiesText -Text $s.Substring(1);
                        New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
                            Text = $Unescaped + $UnescapedPropertiesText.Text;
                            IsContinued = $UnescapedPropertiesText.IsContinued;
                        }
                    }
                }
            } else {
                New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
                    Text = $Unescaped;
                    IsContinued = $false;
                }
            }
        }
    }
}

$Script:EncodeKeyRegex = [System.Text.RegularExpressions.Regex]::new('(?<u>[\u0000-\u0019\u007f-\uffff])|(?<c>[\b\n\t\r\f])|(?<e>[\\ ]|=(?=.))');
$Script:EncodeValueRegex = [System.Text.RegularExpressions.Regex]::new('(?<u>[\u0000-\u0019\u007f-\uffff])|(?<c>[\b\n\t\r\f\\])');

Function ConvertTo-EscapedPropertiesText {
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $true)]
        [AllowEmptyString()]
        [string]$Text,

        [switch]$Key
    )
    if ($Key.IsPresent) {
        switch ($Text) {
            "" {
                "" | Write-Output;
                break;
            }
            "=" {
                "==" | Write-Output;
                break;
            }
            "#" {
                "\#" | Write-Output;
                break;
            }
            "#=" {
                "\#=" | Write-Output;
                break;
            }
            default {
                $Result = $Text;
                if ($Result.EndsWith('=')) { $Result = $Result.Substring(0, $Result.Length - 1) }
                if ($Result.StartsWith('#')) { $Result = $Result.Substring(1) }
                $Result = $Script:EncodeKeyRegex.Replace($Result, {
                    Param(
                        [System.Text.RegularExpressions.Match]$m
                    )
                    if ($m.Groups['c'].Success) {
                        switch ($m.Groups['c'].Value) {
                            "`b" {
                                return '\b';
                            }
                            "`t" {
                                return '\t';
                            }
                            "`r" {
                                return '\r';
                            }
                            "`f" {
                                return '\f';
                            }
                        }
                        return '\n';
                    }
                    if ($m.Groups['e'].Success) {
                        return "\$($m.Groups['e'].Value)";
                    }
                    return "\u$(([int]([char]($m.Groups['u'].Value))).ToString('x4'))";
                });

                if ($Text.EndsWith('=')) { $Result += "=" }
                if ($Text.StartsWith('#')) { "\#" + $Result } else { $Result }

                break;
            }
        }
    } else {
        $Script:EncodeValueRegex.Replace($Text, {
            Param(
                [System.Text.RegularExpressions.Match]$m
            )
            if ($m.Groups['c'].Success) {
                switch ($m.Groups['c'].Value) {
                    "`b" {
                        return '\b';
                    }
                    "`t" {
                        return '\t';
                    }
                    "`r" {
                        return '\r';
                    }
                    "`f" {
                        return '\f';
                    }
                }
                return '\n';
            }
            if ($m.Groups['e'].Success) {
                return "\$($m.Groups['e'].Value)";
            }
            return "\u$(([int]([char]($m.Groups['u'].Value))).ToString('x4'))";
        });
    }
}

Function New-PropertiesFile {
    [CmdletBinding(DefaultParameterSetName = 'Relative')]
    Param(
        [Parameter(Mandatory = $true, ParameterSetName = 'Relative')]
        [ValidateScript({ $_ | Test-PropertiesFilePath -Resource })]
        [string]$Path
    )

    $FullPath = $Path | Convert-RelativePathToFullPath -Resource;
    Write-Host -Object $FullPath;
    $Dictionary = [System.Collections.Generic.Dictionary[System.String, [System.Tuple[System.String,System.String[]]]]]::new([System.StringComparer]::InvariantCulture);
    if (Test-Path -Path $FullPath) {
        Write-Host -Object 'Exists';
        $PreviousItem = $null;
        $PreviousBlankLine = $false;
        [string[]]$CommentBlocks = @();
        $CommentLines = @();
        $LineNumber = 0;
        $Script:LineBreakRegex.Split(((Get-Content -Path $FullPath -Encoding UTF8) | Out-String)) | ForEach-Object {
            $LineNumber++;
            if ($null -ne $PreviousItem) {
                $UnescapedPropertiesText = ConvertFrom-EscapedPropertiesText -Text $_;
                $PreviousItem = New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
                    Key = $PreviousItem.Key;
                    Text = $PreviousItem.Text + $UnescapedPropertiesText.Text;
                };
                if (-not $UnescapedPropertiesText.IsContinued) {
                    if ($CommentLines.Count -gt 0) {
                        [string[]]$CommentBlocks + $CommentBlocks + @($CommentLines -join "`n");
                        if ($PreviousBlankLine) {
                            [string[]]$CommentBlocks + $CommentBlocks + @('');
                        }
                    }
                    $CommentLines = @();
                    $Dictionary.Add($PreviousItem.Key,[System.Tuple[System.String,System.String[]]]::new($PreviousItem.Text, $CommentBlocks));
                    $PreviousItem = $null;
                    $CommentBlocks = @();
                }
                $PreviousBlankLine = $false;
            } else {
                $m = $Script:PropertyLineRegex.Match($_);
                if ($m.Success) {
                    if ($m.Groups['c'].Success) {
                        if ($CommentLines.Count -gt 0 -and $PreviousBlankLine) {
                            [string[]]$CommentBlocks + $CommentBlocks + @($CommentLines -join "`n");
                            $CommentLines = @($Script:StripCommentRegex.Replace($_ , ''));
                        } else {
                            $CommentLines += @($Script:StripCommentRegex.Replace($_ , ''));
                        }
                    } else {
                        $UnescapedPropertiesText = ConvertFrom-EscapedPropertiesText -Text $m.Groups['v'].Value;
                        $PreviousItem = New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
                            Key = (ConvertFrom-EscapedPropertiesText -Text $m.Groups['k'].Value).Text;
                            Text = $UnescapedPropertiesText.Text;
                        };
                        if (-not $UnescapedPropertiesText.IsContinued) {
                            if ($CommentLines.Count -gt 0) {
                                [string[]]$CommentBlocks + $CommentBlocks + @($CommentLines -join "`n");
                                if ($PreviousBlankLine) {
                                    [string[]]$CommentBlocks + $CommentBlocks + @('');
                                }
                            }
                            $CommentLines = @();
                            $Dictionary.Add($PreviousItem.Key, [System.Tuple[System.String,System.String[]]]::new($PreviousItem.Text, $CommentBlocks));
                            $PreviousItem = $null;
                            $CommentBlocks = @();
                        }
                    }
                    $PreviousBlankLine = $false;
                } else {
                    if ([string]::IsNullOrWhiteSpace($_)) {
                        $PreviousBlankLine = $true;
                    } else {
                        Write-Warning -Message "Parse error on line $LineNumber";
                        if ($CommentLines.Count -gt 0 -and $PreviousBlankLine) {
                            [string[]]$CommentBlocks + $CommentBlocks + @($CommentLines -join "`n");
                            $CommentLines = @("Parse error: $_");
                        } else {
                            $CommentLines += @("Parse error: $_");
                        }
                        $PreviousBlankLine = $false;
                    }
                }
            }
        }
    }

    New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
        Properties = $Dictionary;
        Path = $Path;
        Footer = $CommentBlocks;
    };
}

Function New-ResourceBundle {
    [CmdletBinding(DefaultParameterSetName = 'Relative')]
    Param(
        [Parameter(Mandatory = $true, ParameterSetName = 'Relative')]
        [ValidateScript({ $_ | Test-PropertiesFilePath -Resource })]
        [string]$BasePath
    )
    
    $FullPath = $BasePath | Convert-RelativePathToFullPath -Resource;
    $Files = @{};
    $Script:AllLocales | ForEach-Object {
        Write-Host -Object "$BasePath`_$_.properties";
        $Files[$_] = New-PropertiesFile -Path "$BasePath`_$_.properties"
    }

    $ResourceBundle = New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{
        BasePath = $BasePath;
        AllFiles = $Files
    }
    $ResourceBundle | Add-Member -MemberType ScriptMethod -Name 'GetPropertyNames' -Value {
        $this.AllFiles.Keys | ForEach-Object { $this.AllFiles[$_].Properties.Keys } | Select-Object -Unique;
    }
    $ResourceBundle | Add-Member -MemberType ScriptMethod -Name 'ContainsKey' -Value {
        Param(
            [string]$key
        )
        if ($null -ne $key -and $key -is [string]) {
            foreach ($k in $this.AllFiles.Keys) {
                if ($this.AllFiles[$k].Properties.ContainsKey($key)) { return $true }
            }
        }
        return $false;
    }
    $ResourceBundle | Add-Member -MemberType ScriptMethod -Name 'GetValue' -Value {
        Param(
            [string]$key,
            [string]$language
        )
        if ($null -ne $key -and $key -is [string]) {
            if ($null -eq $language -or $language.Length -eq 0) { $language = 'en' }
            if ($this.AllFiles.ContainsKey($language)) {
                if ($this.AllFiles[$language].Properties.ContainsKey($key)) {
                    return $this.AllFiles[$language].Properties[$key];
                }
                foreach ($k in $this.AllFiles.Keys) {
                    if ($this.AllFiles[$k].Properties.ContainsKey($key)) { return "" }
                }
            }
        }
    }
    $ResourceBundle | Add-Member -MemberType ScriptMethod -Name 'Remove' -Value {
        Param(
            [string]$key
        )
        if ($null -ne $key -and $key -is [string]) {
            foreach ($k in $this.AllFiles.Keys) {
                if ($this.AllFiles[$k].Properties.ContainsKey($key)) { $this.AllFiles[$k].Properties.Remove($key) | Out-Null }
            }
        }
    }
    $ResourceBundle | Add-Member -MemberType ScriptMethod -Name 'SetValue' -Value {
        Param(
            [string]$key,
            [string]$value,
            [string]$language
        )
        if ($null -eq $key -or $key -isnot [string]) { throw 'Invalid key' }
        if ($null -eq $value -or $value -isnot [string]) { throw 'Invalid value' }
        if ($null -eq $language -or $language.Length -eq 0) { $language = 'en' }
        if (-not $this.AllFiles.ContainsKey($language)) { throw 'Invalid language' }
        foreach ($k in $this.AllFiles.Keys) {
            if ($k -eq $language) {
                if ($this.AllFiles[$k].Properties.ContainsKey($key)) {
                    $this.AllFiles[$k].Properties[$key] = $value;
                } else {
                    $this.AllFiles[$k].Properties.Add($key, $value);
                }
            } else {
                if (-not $this.AllFiles[$k].Properties.ContainsKey($key)) { $this.AllFiles[$k].Properties.Add($key, "") }
            }
        }
    }
    $ResourceBundle | Add-Member -MemberType ScriptMethod -Name 'SaveChanges' -Value {
        foreach ($n in $this.GetPropertyNames()) {
            foreach ($k in $this.AllFiles.Keys) {
                $d = $this.AllFiles[$k].Properties;
                if (-not $d.ContainsKey($n)) {
                    $d.Add($n, '');
                }
            }
        }
        foreach ($k in $this.AllFiles.Keys) {
            Save-PropertiesFile -PropertiesFile $this.AllFiles[$k] -ErrorAction Stop;
        }
        $FullPath = ($this.BasePath + "ResourceKeys.java") | Convert-RelativePathToFullPath -Code;
        $d = $this.AllFiles['en'].Properties;
        $p = $this.BasePath | Split-Path -Parent;
        $ns = @();
        while ($null -ne $p -and $p.Length -gt 0) {
            $ns = @($p | Split-Path -Leaf) + $ns;
            $p = $p | Split-Path -Parent;
        }
        $ns = @('scheduler') + $ns;
        $Lines = @(
            "package $($ns -join '.');",
            "",
            "/**",
            " * Resource bundle keys for {@code resources/scheduler/$($this.BasePath.Replace('\', '/'))}.",
            " *",
            " * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;",
            " */"
        );
        $Lines += "public interface $([System.IO.Path]::GetFileNameWithoutExtension($FullPath)) {";
        $Lines += @($d.Keys | Sort-Object | ForEach-Object {
            @"

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the locale-specific text for {@code "$($d[$_].Item1.Replace('"', '\"'))"}.
     */
    public static final String RESOURCEKEY_$($_.ToUpper()) = "$_";
"@
        })
        $Lines += "}";
        [System.IO.File]::WriteAllLines($FullPath, $Lines, [System.Text.UTF8Encoding]::new($false, $false));
    } -PassThru;
}

Function Save-PropertiesFile {
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $true)]
        [object]$PropertiesFile
    )
    
    $FullPath = $PropertiesFile.Path | Convert-RelativePathToFullPath -Resource;
    [System.IO.File]::WriteAllLines($FullPath, ($PropertiesFile.Properties.Keys | ForEach-Object {
        $Tuple = $PropertiesFile.Properties[$_];
        if ($Tuple.Item2.Length -gt 0) {
            $Script:LineBreakRegex.Split($Tuple.Item2[0]) | ForEach-Object { "# $_" }
            ($Tuple.Item2 | Select-Object -Skip 1) | ForEach-Object {
                '';
                $Script:LineBreakRegex.Split($_) | ForEach-Object { "# $_" }
            }
        }
        "$(ConvertTo-EscapedPropertiesText -Text $_ -Key)=$(ConvertTo-EscapedPropertiesText -Text $Tuple.Item1)";
    }), [System.Text.UTF8Encoding]::new($false, $false));
}

$ResourceBundle = New-ResourceBundle -BasePath 'view\country\EditCountry' -ErrorAction Stop;
$ResourceBundle.SaveChanges();
