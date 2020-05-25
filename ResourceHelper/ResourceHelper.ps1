Param(
    [string]$ProjectRoot = '../Scheduler',

    [string]$ResouresFolder = 'resources',

    [string]$SourceFolder = 'src',

    [string]$DllPath = 'bin\Debug\ResourceHelper.dll'
)

Add-Type -Path ($PSScriptRoot | Join-Path -ChildPath $DllPath) -ErrorAction Stop;

$ResourcesRoot = ((($PSScriptRoot | Join-Path -ChildPath $ProjectRoot) | Join-Path -ChildPath $ResouresFolder) | Resolve-Path).Path;
$SourceRoot = ((($PSScriptRoot | Join-Path -ChildPath $ProjectRoot) | Join-Path -ChildPath $SourceFolder) | Resolve-Path).Path;

[ResourceHelper.BundleInfo[]]$AllBundleInfos = [ResourceHelper.BundleInfo]::Create($ResourcesRoot);

[ResourceHelper.BundleInfo]$TargetInfo = $AllBundleInfos | Out-GridView -Title 'Select Target Bundle' -OutputMode Single;

if ($null -ne $TargetInfo) {
    $TargetBundle = [ResourceHelper.ResourceBundle]::new();
    $TargetBundle.Load($ResourcesRoot, $TargetInfo.ToBaseName());
    [ResourceHelper.ResourceBundle[]]$OtherBundles = $AllBundleInfos | Where-Object { -not $_.Equals($TargetInfo) } | ForEach-Object {
        $ResourceBundle = [ResourceHelper.ResourceBundle]::new();
        $ResourceBundle.Load($ResourcesRoot, $_.ToBaseName());
        $ResourceBundle | Write-Output;
    };
    [System.Collections.ObjectModel.Collection[System.Management.Automation.Host.ChoiceDescription]]$RootChoices = @(
        [System.Management.Automation.Host.ChoiceDescription]::new('Edit', 'Edit items'),
        [System.Management.Automation.Host.ChoiceDescription]::new('Add', 'Add new item'),
        [System.Management.Automation.Host.ChoiceDescription]::new('Copy', 'Copy from another bundle'),
        [System.Management.Automation.Host.ChoiceDescription]::new('Move', 'Move from another bundle'),
        [System.Management.Automation.Host.ChoiceDescription]::new('Import', 'Import from all bundles'),
        [System.Management.Automation.Host.ChoiceDescription]::new('Save', 'Save changes and exit')
        [System.Management.Automation.Host.ChoiceDescription]::new('Abort', 'Exit without saving changes')
    );
    [System.Collections.ObjectModel.Collection[System.Management.Automation.Host.ChoiceDescription]]$EditChoices = @(
        [System.Management.Automation.Host.ChoiceDescription]::new('Change'),
        [System.Management.Automation.Host.ChoiceDescription]::new('Delete'),
        [System.Management.Automation.Host.ChoiceDescription]::new('Cancel')
    );
    [System.Collections.ObjectModel.Collection[System.Management.Automation.Host.ChoiceDescription]]$YesNoChoices = @(
        [System.Management.Automation.Host.ChoiceDescription]::new('Yes'),
        [System.Management.Automation.Host.ChoiceDescription]::new('No'),
        [System.Management.Automation.Host.ChoiceDescription]::new('Cancel')
    );
    $Modified = @{};
    $ChoiceIndex = $Host.UI.PromptForChoice('Modify bundle', 'Select action', $RootChoices, 0);
    while ($null -ne $ChoiceIndex -and $ChoiceIndex -ge 0 -and $ChoiceIndex -lt 5) {
        switch ($ChoiceIndex) {
            0 { # Edit
                $Items = @(($TargetBundle.Keys | Select-Object -Property @{
                    Label = 'Name';
                    Expression = { $_ }
                }, @{
                    Label = 'en';
                    Expression = { $TargetBundle.Get($_, [ResourceHelper.LanguageCode]::en) }
                }, @{
                    Label = 'de';
                    Expression = { $TargetBundle.Get($_, [ResourceHelper.LanguageCode]::de) }
                }, @{
                    Label = 'es';
                    Expression = { $TargetBundle.Get($_, [ResourceHelper.LanguageCode]::es) }
                }, @{
                    Label = 'hi';
                    Expression = { $TargetBundle.Get($_, [ResourceHelper.LanguageCode]::hi) }
                }) | Out-GridView -Title 'Select items' -OutputMode Multiple);
                while ($Items.Count -gt 0) {
                    $ChoiceIndex = $Host.UI.PromptForChoice('Modify items', 'Select action', $EditChoices, 2);
                    if ($null -ne $ChoiceIndex) {
                        switch ($ChoiceIndex) {
                            0 {
                                $Items | ForEach-Object {
                                    "$($_.Name)=$($_.en)" | Write-Host;
                                    $EnValue = Read-Host -Prompt 'English (blank to copy from clipboard)';
                                    if ([string]::IsNullOrEmpty($Value)) {
                                        $EnValue = [System.Windows.Forms.Clipboard]::GetText();
                                    } else {
                                        [System.Windows.Forms.Clipboard]::SetText($EnValue);
                                    }
                                    $DeValue = Read-Host -Prompt 'German (blank to copy from clipboard)';
                                    if ([string]::IsNullOrEmpty($DeValue)) {
                                        $DeValue = [System.Windows.Forms.Clipboard]::GetText();
                                    }
                                    [System.Windows.Forms.Clipboard]::SetText($EnValue);
                                    $EsValue = Read-Host -Prompt 'Spanish (blank to copy from clipboard)';
                                    if ([string]::IsNullOrEmpty($EsValue)) {
                                        $EsValue = [System.Windows.Forms.Clipboard]::GetText();
                                    }
                                    [System.Windows.Forms.Clipboard]::SetText($EnValue);
                                    $HiValue = Read-Host -Prompt 'Hindi (blank to copy from clipboard)';
                                    if ([string]::IsNullOrEmpty($EsValue)) {
                                        $HiValue = [System.Windows.Forms.Clipboard]::GetText();
                                    }
                                    $TargetBundle.Set($Key, $EnValue, [ResourceHelper.LanguageCode]::en);
                                    $TargetBundle.Set($Key, $DeValue, [ResourceHelper.LanguageCode]::de);
                                    $TargetBundle.Set($Key, $EsValue, [ResourceHelper.LanguageCode]::es);
                                    $TargetBundle.Set($Key, $HiValue, [ResourceHelper.LanguageCode]::hi);
                                }
                                break;
                            }
                            1 {
                                $TargetBundle.Remove($Key);
                                break;
                            }
                        }
                    }
                    $Items = @(($TargetBundle.Keys | Select-Object -Property @{
                        Label = 'Name';
                        Expression = { $_ }
                    }, @{
                        Label = 'en';
                        Expression = { $TargetBundle.Get($_, [ResourceHelper.LanguageCode]::en) }
                    }, @{
                        Label = 'de';
                        Expression = { $TargetBundle.Get($_, [ResourceHelper.LanguageCode]::de) }
                    }, @{
                        Label = 'es';
                        Expression = { $TargetBundle.Get($_, [ResourceHelper.LanguageCode]::es) }
                    }, @{
                        Label = 'hi';
                        Expression = { $TargetBundle.Get($_, [ResourceHelper.LanguageCode]::hi) }
                    }) | Out-GridView -Title 'Select items' -OutputMode Multiple);
                }
                break;
            }
            1 { # Add
                $Key = Read-Host -Prompt 'Property name';
                if ([string]::IsNullOrWhiteSpace($Key)) {
                    Write-Warning -Message 'No key provided';
                } else {
                    $Key = $Key.Trim();
                    if ($TargetBundle.ContainsKey($Key)) {
                        Write-Warning -Message 'Property already defined';
                    } else {
                        $EnValue = Read-Host -Prompt 'English (blank to copy from clipboard)';
                        if ([string]::IsNullOrEmpty($EnValue)) {
                            $EnValue = [System.Windows.Forms.Clipboard]::GetText();
                        } else {
                            [System.Windows.Forms.Clipboard]::SetText($EnValue);
                        }
                        $DeValue = Read-Host -Prompt 'German (blank to copy from clipboard)';
                        if ([string]::IsNullOrEmpty($DeValue)) {
                            $DeValue = [System.Windows.Forms.Clipboard]::GetText();
                        }
                        [System.Windows.Forms.Clipboard]::SetText($EnValue);
                        $EsValue = Read-Host -Prompt 'Spanish (blank to copy from clipboard)';
                        if ([string]::IsNullOrEmpty($EsValue)) {
                            $EsValue = [System.Windows.Forms.Clipboard]::GetText();
                        }
                        [System.Windows.Forms.Clipboard]::SetText($EnValue);
                        $HiValue = Read-Host -Prompt 'Hindi (blank to copy from clipboard)';
                        if ([string]::IsNullOrEmpty($HiValue)) {
                            $HiValue = [System.Windows.Forms.Clipboard]::GetText();
                        }
                        $TargetBundle.Set($Key, $EnValue, [ResourceHelper.LanguageCode]::en);
                        $TargetBundle.Set($Key, $DeValue, [ResourceHelper.LanguageCode]::de);
                        $TargetBundle.Set($Key, $EsValue, [ResourceHelper.LanguageCode]::es);
                        $TargetBundle.Set($Key, $HiValue, [ResourceHelper.LanguageCode]::hi);
                    }
                }
                break;
            }
            2 { # Copy
                [ResourceHelper.ResourceBundle]$SourceBundle = $OtherBundles | Out-GridView -Title 'Select Source Bundle' -OutputMode Single;
                if ($null -ne $SourceBundle) {
                    $Items = @(($SourceBundle.Keys | Select-Object -Property @{
                        Label = 'Name';
                        Expression = { $_ }
                    }, @{
                        Label = 'en';
                        Expression = { $SourceBundle.Get($_, [ResourceHelper.LanguageCode]::en) }
                    }, @{
                        Label = 'de';
                        Expression = { $SourceBundle.Get($_, [ResourceHelper.LanguageCode]::de) }
                    }, @{
                        Label = 'es';
                        Expression = { $SourceBundle.Get($_, [ResourceHelper.LanguageCode]::es) }
                    }, @{
                        Label = 'hi';
                        Expression = { $SourceBundle.Get($_, [ResourceHelper.LanguageCode]::hi) }
                    }) | Out-GridView -Title 'Select items to copy' -OutputMode Multiple);
                    if ($Items.Count -gt 0) {
                        $Existing = @($Items | ForEach-Object { $_.Name } | Where-Object { $TargetBundle.ContainsKey($_); } );
                        $Force = $false;
                        if ($Existing.Count -gt 0) {
                            $ChoiceIndex = $Host.UI.PromptForChoice('Overwrite', "Overwrite $($Existing -join ', ')?", $YesNoChoices, 2);
                            if ($null -eq $ChoiceIndex) {
                                $Items = @();
                            } else {
                                if ($ChoiceIndex -eq 0) {
                                    $Force = $true;
                                } else {
                                    if ($ChoiceIndex -ne 1) {
                                        $Items = @();
                                    }
                                }
                            }
                        }
                        if ($Items.Count -gt 0) {
                            $Items | ForEach-Object {
                                $SourceBundle.CopyTo($_.Name, $TargetBundle, $Force);
                            }
                        }
                    }
                }
                break;
            }
            3 { # Move
                [ResourceHelper.ResourceBundle]$SourceBundle = $OtherBundles | Out-GridView -Title 'Select Source Bundle' -OutputMode Single;
                if ($null -ne $SourceBundle) {
                    $Items = @(($SourceBundle.Keys | Select-Object -Property @{
                        Label = 'Name';
                        Expression = { $_ }
                    }, @{
                        Label = 'en';
                        Expression = { $SourceBundle.Get($_, [ResourceHelper.LanguageCode]::en) }
                    }, @{
                        Label = 'de';
                        Expression = { $SourceBundle.Get($_, [ResourceHelper.LanguageCode]::de) }
                    }, @{
                        Label = 'es';
                        Expression = { $SourceBundle.Get($_, [ResourceHelper.LanguageCode]::es) }
                    }, @{
                        Label = 'hi';
                        Expression = { $SourceBundle.Get($_, [ResourceHelper.LanguageCode]::hi) }
                    }) | Out-GridView -Title 'Select items to move' -OutputMode Multiple);
                    if ($Items.Count -gt 0) {
                        $Existing = @($Items | ForEach-Object { $_.Name } | Where-Object { $TargetBundle.ContainsKey($_); } );
                        $Force = $false;
                        if ($Existing.Count -gt 0) {
                            $ChoiceIndex = $Host.UI.PromptForChoice('Overwrite', "Overwrite $($Existing -join ', ')?", $YesNoChoices, 2);
                            if ($null -eq $ChoiceIndex) {
                                $Items = @();
                            } else {
                                if ($ChoiceIndex -eq 0) {
                                    $Force = $true;
                                } else {
                                    if ($ChoiceIndex -ne 1) {
                                        $Items = @();
                                    }
                                }
                            }
                        }
                        if ($Items.Count -gt 0) {
                            $Items | ForEach-Object {
                                $SourceBundle.MoveTo($_.Name, $TargetBundle, $Force);
                            }
                            $Modified[$SourceBundle.BaseName] = $SourceBundle;
                        }
                    }
                }
                break;
            }
            4 { # Import
                $map = @{};
                $ToImport = @(@($OtherBundles | ForEach-Object {
                    $bn = $_.BaseName;
                    $map[$bn] = $_;
                    $sb = $_;
                    $_.Keys | Select-Object -Property @{
                        Label = 'BaseName';
                        Expression = { $bn }
                    }, @{
                        Label = 'Name';
                        Expression = { $_ }
                    }, @{
                        Label = 'Value';
                        Expression = { $sb.Get($_, [ResourceHelper.LanguageCode]::en) }
                    }
                }) | Out-GridView -Title 'Select items to import' -OutputMode Multiple);
                if ($ToImport.Count -gt 0) {
                    $Existing = @($GridView | ForEach-Object { $_.Name } | Where-Object { $TargetBundle.ContainsKey($_); } );
                    $Force = $false;
                    if ($Existing.Count -gt 0) {
                        $ChoiceIndex = $Host.UI.PromptForChoice('Overwrite', "Overwrite $($Existing -join ', ')?", $YesNoChoices, 2);
                        if ($null -eq $ChoiceIndex) {
                            $GridView = @();
                        } else {
                            if ($ChoiceIndex -eq 0) {
                                $Force = $true;
                            } else {
                                if ($ChoiceIndex -ne 1) {
                                    $GridView = @();
                                }
                            }
                        }
                    }
                    $ToImport | ForEach-Object {
                        $map[$_.BaseName].CopyTo($_.Name, $TargetBundle, $Force);
                    }
                }
                break;
            }
        }
        $ChoiceIndex = $Host.UI.PromptForChoice('Modify bundle', 'Select action', $RootChoices, 0);
    }
    
    if ($ChoiceIndex -eq 5) {
        $TargetBundle.Save();
        $TargetBundle.SaveCode($SourceRoot);
        if ($Modified.Count -gt 0) {
            $Modified.Values | ForEach-Object {
                $_.Save();
                $_.SaveCode($SourceRoot);
            }
        }
    }
}