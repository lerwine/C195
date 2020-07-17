Param(
    [string]$ProjectRoot = '../Scheduler',

    [string]$ResouresFolder = 'resources',

    [string]$SourceFolder = 'src',

    [string]$DllPath = 'bin\Debug\ResourceHelper.dll'
)

Add-Type -AssemblyName 'System.Windows.Forms' -ErrorAction Stop;
Add-Type -AssemblyName 'System.Drawing' -ErrorAction Stop;
Add-Type -Path ($PSScriptRoot | Join-Path -ChildPath $DllPath) -ErrorAction Stop;

$Script:ResourcesRoot = ((($PSScriptRoot | Join-Path -ChildPath $ProjectRoot) | Join-Path -ChildPath $ResouresFolder) | Resolve-Path).Path;
$Script:SourceRoot = ((($PSScriptRoot | Join-Path -ChildPath $ProjectRoot) | Join-Path -ChildPath $SourceFolder) | Resolve-Path).Path;

[ResourceHelper.BundleInfo[]]$Script:AllBundleInfos = [ResourceHelper.BundleInfo]::Create($Script:ResourcesRoot);

[System.Collections.ObjectModel.Collection[System.Management.Automation.Host.ChoiceDescription]]$Script:__SelectBundleAction = 
[System.Collections.ObjectModel.Collection[System.Management.Automation.Host.ChoiceDescription]]$Script:EditChoices = @(
    [System.Management.Automation.Host.ChoiceDescription]::new('Change'),
    [System.Management.Automation.Host.ChoiceDescription]::new('Delete'),
    [System.Management.Automation.Host.ChoiceDescription]::new('Cancel')
);
[System.Collections.ObjectModel.Collection[System.Management.Automation.Host.ChoiceDescription]]$Script:YesNoChoices = @(
    [System.Management.Automation.Host.ChoiceDescription]::new('Yes'),
    [System.Management.Automation.Host.ChoiceDescription]::new('No'),
    [System.Management.Automation.Host.ChoiceDescription]::new('Cancel')
);

Function Open-ResourceBundle {
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $true, ValueFromPipeline = $true)]
        [ResourceHelper.BundleInfo]$BundleInfo
    )

    Process {
        $ResourceBundle = [ResourceHelper.ResourceBundle]::new();
        $ResourceBundle.Load($Script:ResourcesRoot, $BundleInfo.ToBaseName());
        $ResourceBundle | Write-Output;
    }
}

Function Select-ResourceBundle {
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $true)]
        [string]$Title,
        [switch]$Multi
    )
    [ResourceHelper.BundleInfo]$BundleInfo = $null;
    if ($Multi.IsPresent) {
        $BundleInfo = $Script:AllBundleInfos | Out-GridView -Title $Title -OutputMode Multiple;
    } else {
        $BundleInfo = $Script:AllBundleInfos | Out-GridView -Title $Title -OutputMode Single;
    }

    if ($null -ne $BundleInfo) {
        Open-ResourceBundle -BundleInfo $BundleInfo;
    }
}

Function Select-BundleKey {
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $true)]
        [string]$Title,
        [Parameter(Mandatory = $true)]
        [ResourceHelper.ResourceBundle]$ResourceBundle,
        [switch]$Multi
    )
    Process {
        $Items = @($ResourceBundle.Keys | Select-Object -Property @{
            Label = 'Name';
            Expression = { $_ }
        }, @{
            Label = 'en';
            Expression = { $ResourceBundle.Get($_, [ResourceHelper.LanguageCode]::en) }
        }, @{
            Label = 'de';
            Expression = { $ResourceBundle.Get($_, [ResourceHelper.LanguageCode]::de) }
        }, @{
            Label = 'es';
            Expression = { $ResourceBundle.Get($_, [ResourceHelper.LanguageCode]::es) }
        }, @{
            Label = 'hi';
            Expression = { $ResourceBundle.Get($_, [ResourceHelper.LanguageCode]::hi) }
        });
        $Selected = $null;
        if ($Multi.IsPresent) {
            $Selected = $Items | Out-GridView -Title 'Select items' -OutputMode Multiple;
        } else {
            $Selected = $Items | Out-GridView -Title 'Select items' -OutputMode Single;
        }
        if ($null -ne $Selected) {
            $Selected | Select-Object -ExpandProperty 'Name';
        }
    }
}

Function New-TableLayoutPanel {
    [CmdletBinding(DefaultParameterSetName = 'Dock')]
    Param(
        [Parameter(Mandatory = $true)]
        [float[]]$Rows,
        [Parameter(Mandatory = $true)]
        [float[]]$Columns,
        [Parameter(ParameterSetName = 'Dock')]
        [System.Windows.Forms.DockStyle]$Dock = [System.Windows.Forms.DockStyle]::Fill,
        [Parameter(Mandatory = $true, ParameterSetName = 'Anchor')]
        [System.Windows.Forms.AnchorStyles[]]$Anchor
    )
    
    [System.Windows.Forms.TableLayoutPanel]$TableLayoutPanel = New-Object -TypeName 'System.Windows.Forms.TableLayoutPanel' -Property @{
        AutoSize = $true;
    };
    $TableLayoutPanel.SuspendLayout();
    if ($PSCmdlet.ParameterSetName -eq 'Anchor') {
        $TableLayoutPanel.Dock = [System.Windows.Forms.DockStyle]::None;
        [System.Windows.Forms.AnchorStyles]$a = $Anchor[0];
        if ($Anchor.Length -gt 1) { ($Anchor | Select-Object -Skip 1) | ForEach-Object { [System.Windows.Forms.AnchorStyles]$a = $a -bor $_ } }
        $TableLayoutPanel.Anchor = $a;
    } else {
        $TableLayoutPanel.Anchor = [System.Windows.Forms.AnchorStyles]::None;
        $TableLayoutPanel.Dock = $Dock;
    }
    $Rows | ForEach-Object {
        if ($_ -gt 0.0) {
            $TableLayoutPanel.RowStyles.Add((New-Object -TypeName 'System.Windows.Forms.RowStyle' -ArgumentList ([System.Windows.Forms.SizeType]::AutoSize))) | Out-Null;
        } else {
            $TableLayoutPanel.RowStyles.Add((New-Object -TypeName 'System.Windows.Forms.RowStyle' -ArgumentList ([System.Windows.Forms.SizeType]::Percent, $_))) | Out-Null;
        }
    }
    $Columns | ForEach-Object {
        if ($_ -gt 0.0) {
            $TableLayoutPanel.ColumnStyles.Add((New-Object -TypeName 'System.Windows.Forms.ColumnStyle' -ArgumentList ([System.Windows.Forms.SizeType]::AutoSize))) | Out-Null;
        } else {
            $TableLayoutPanel.ColumnStyles.Add((New-Object -TypeName 'System.Windows.Forms.ColumnStyle' -ArgumentList ([System.Windows.Forms.SizeType]::Percent, $_))) | Out-Null;
        }
    }
    $TableLayoutPanel.ColumnCount = $Columns.Length;
    $TableLayoutPanel.RowCount = $Rows.Length;
    $TableLayoutPanel | Write-Output;
}

Function New-Label {
    [CmdletBinding(DefaultParameterSetName = 'Dock')]
    Param(
        [Parameter(Mandatory = $true)]
        [string]$Text,
        [Parameter(ParameterSetName = 'Dock')]
        [System.Windows.Forms.DockStyle]$Dock = [System.Windows.Forms.DockStyle]::Fill,
        [Parameter(Mandatory = $true, ParameterSetName = 'Anchor')]
        [System.Windows.Forms.AnchorStyles[]]$Anchor,
        [System.Drawing.ContentAlignment]$TextAlign = [System.Drawing.ContentAlignment]::MiddleLeft,
        [switch]$Bold
    )
    $Label = (New-Object -TypeName 'System.Windows.Forms.Label' -Property @{
        AutoSize = $true;
        Text = $Text;
        TextAlign = $TextAlign;
    });
    $Label.SuspendLayout();
    if ($PSCmdlet.ParameterSetName -eq 'Anchor') {
        $Label.Dock = [System.Windows.Forms.DockStyle]::None;
        [System.Windows.Forms.AnchorStyles]$a = $Anchor[0];
        if ($Anchor.Length -gt 1) { ($Anchor | Select-Object -Skip 1) | ForEach-Object { [System.Windows.Forms.AnchorStyles]$a = $a -bor $_ } }
        $TableLayoutPanel.Anchor = $a;
    } else {
        $Label.Anchor = [System.Windows.Forms.AnchorStyles]::None;
        $Label.Dock = $Dock;
    }
    if ($Bold.IsPresent) {
        $Label.Font = New-Object -TypeName 'System.Drawing.Font' -ArgumentList $Label.Font, ([System.Drawing.FontStyle]::Bold);
    }
    $Label | Write-Output;
}

Function New-TextBox {
    [CmdletBinding(DefaultParameterSetName = 'Dock')]
    Param(
        [Parameter(Mandatory = $true)]
        [int]$TabIndex,
        [Parameter(ParameterSetName = 'Dock')]
        [System.Windows.Forms.DockStyle]$Dock = [System.Windows.Forms.DockStyle]::Top,
        [Parameter(Mandatory = $true, ParameterSetName = 'Anchor')]
        [System.Windows.Forms.AnchorStyles[]]$Anchor,
        [switch]$Multiline
    )
    $TextBox = (New-Object -TypeName 'System.Windows.Forms.TextBox' -Property @{
        AcceptsReturn = $Multiline.IsPresent;
        AcceptsTab = $Multiline.IsPresent;
        AutoSize = $true;
        Multiline = $Multiline.IsPresent;
        TabIndex = $TabIndex;
    });
    $TextBox.SuspendLayout();
    if ($Multiline.IsPresent) {
        $TextBox.ScrollBars = [System.Windows.Forms.ScrollBars]::Both;
    }
    if ($PSCmdlet.ParameterSetName -eq 'Anchor') {
        $TextBox.Dock = [System.Windows.Forms.DockStyle]::None;
        [System.Windows.Forms.AnchorStyles]$a = $Anchor[0];
        if ($Anchor.Length -gt 1) { ($Anchor | Select-Object -Skip 1) | ForEach-Object { [System.Windows.Forms.AnchorStyles]$a = $a -bor $_ } }
        $TableLayoutPanel.Anchor = $a;
    } else {
        $TextBox.Anchor = [System.Windows.Forms.AnchorStyles]::None;
        if ($PSBoundParameters.ContainsKey('Dock') -or -not $Multiline.IsPresent) {
            $TextBox.Dock = $Dock;
        } else {
            $TextBox.Dock = [System.Windows.Forms.DockStyle]::Fill;
        }
    }
    $TextBox | Write-Output;
}

Function New-Button {
    [CmdletBinding(DefaultParameterSetName = 'Anchor')]
    Param(
        [Parameter(Mandatory = $true)]
        [string]$Text,
        [Parameter(Mandatory = $true)]
        [int]$TabIndex,
        [Parameter(Mandatory = $true, ParameterSetName = 'Dock')]
        [System.Windows.Forms.DockStyle]$Dock,
        [Parameter(ParameterSetName = 'Anchor')]
        [System.Windows.Forms.AnchorStyles[]]$Anchor,
        [System.Windows.Forms.DialogResult]$DialogResult
    )
    $Button = (New-Object -TypeName 'System.Windows.Forms.Button' -Property @{
        TabIndex = $TabIndex;
        Text = $Text;
    });
    $Button.SuspendLayout();
    if ($PSBoundParameters.ContainsKey('DialogResult')) {
        $Button.DialogResult = $DialogResult;
    }
    if ($PSBoundParameters.ContainsKey('Dock')) {
        $Button.Anchor = [System.Windows.Forms.AnchorStyles]::None;
        $Button.Dock = $Dock;
    } else {
        if ($PSBoundParameters.ContainsKey('Anchor')) {
            $Button.Dock = [System.Windows.Forms.DockStyle]::None;
            [System.Windows.Forms.AnchorStyles]$a = $Anchor[0];
            if ($Anchor.Length -gt 1) { ($Anchor | Select-Object -Skip 1) | ForEach-Object { [System.Windows.Forms.AnchorStyles]$a = $a -bor $_ } }
            $TableLayoutPanel.Anchor = $a;
        }
    }
    $Button | Write-Output;
}

Function Show-EditBundlePropertyWindow {
    [CmdletBinding(DefaultParameterSetName = 'Add')]
    Param(
        [Parameter(Mandatory = $true, ParameterSetName = 'Edit')]
        [ResourceHelper.ResourceBundle]$ResourceBundle,
        [Parameter(Mandatory = $true, ParameterSetName = 'Edit')]
        [string]$Key,
        [Parameter(ParameterSetName = 'Add')]
        [object]$Values
    )
    [System.Windows.Forms.Form]$Form = New-Object -TypeName 'System.Windows.Forms.Form' -Property @{
        Modal = $true;
        ShowIcon = $false;
        ShowInTaskbar = $true;
        StartPosition = [System.Windows.Forms.FormStartPosition]::CenterScreen;
        TopMost = $true;
        Width = 800;
        Height = 600;
    };
    try {
        [System.Collections.ObjectModel.Collection[System.Windows.Forms.Control]]$LayoutSuspended = @();
        $TableLayoutPanel = New-TableLayoutPanel -Rows 0, 25, 25, 25, 25, 0 -Columns 0, 100;
        $LayoutSuspended.Add($TableLayoutPanel);
        $Form.Controls.Add($TableLayoutPanel);
        $Label = New-Label -Text 'Key:' -TextAlign MiddleRight -Bold;
        $LayoutSuspended.Add($Label);
        $TableLayoutPanel.Controls.Add($Label, 0, 0);
        $KeyTextBox = New-TextBox -TabIndex 0;
        $LayoutSuspended.Add($KeyTextBox);
        $TableLayoutPanel.Controls.Add($KeyTextBox, 1, 0);
        $RowIndex = 0;
        $ValueTextBoxes = @{};
        [Enum]::GetValues([ResourceHelper.LanguageCode]) | ForEach-Object {
            $Label = New-Label -Text "$([Enum]::GetName([ResourceHelper.LanguageCode],$_)):" -TextAlign MiddleRight -Bold;
            $LayoutSuspended.Add($Label);
            $TableLayoutPanel.Controls.Add($Label, 0, ++$RowIndex);
            $TextBox = New-TextBox -TabIndex $RowIndex -Multiline;
            $LayoutSuspended.Add($TextBox);
            $TableLayoutPanel.Controls.Add($TextBox, 1, $RowIndex);
            $ValueTextBoxes[$_] = $TextBox;
        }

        [System.Windows.Forms.FlowLayoutPanel]$FlowLayoutPanel = New-Object -TypeName 'System.Windows.Forms.FlowLayoutPanel' -Property @{
            AutoSize = $true;
            Dock = [System.Windows.Forms.DockStyle]::Right;
        };
        $FlowLayoutPanel.SuspendLayout();
        $LayoutSuspended.Add($FlowLayoutPanel);
        $TableLayoutPanel.Controls.Add($FlowLayoutPanel, 0, ++$RowIndex);
        $Button = New-Button -Text 'OK' -TabIndex ++$RowIndex -Anchor Bottom, Right -DialogResult OK;
        $LayoutSuspended.Add($Button);
        $FlowLayoutPanel.Controls.Add($Button);
        $Form.AcceptButton = $Button;
        $Button = New-Button -Text 'Cancel' -TabIndex ++$RowIndex -Anchor Bottom, Right -DialogResult Cancel;
        $LayoutSuspended.Add($Button);
        $FlowLayoutPanel.Controls.Add($Button);
        $Form.CancelButton = $Button;
        if ($PSBoundParameters.ContainsKey('Key')) {
            $Form.Text = "Edit Property $Key";
            $KeyTextBox.Text = $Key;
            [Enum]::GetValues([ResourceHelper.LanguageCode]) | ForEach-Object {
                $ValueTextBoxes[$_].Text = $ResourceBundle.Get($Key, $_);
            }
        } else {
            $Form.Text = "Add New Property";
            if ($null -ne $Values) {
                if ($null -ne $Values.Key -and $Values.Key -is [string]) { $KeyTextBox.Text = $Values.Key }
                [Enum]::GetValues([ResourceHelper.LanguageCode]) | ForEach-Object {
                    $s = $Values.([Enum]::GetName([ResourceHelper.LanguageCode],$_));
                    if ($null -ne $s -and $s -is [string]) { $ValueTextBoxes[$_].Text = $s }
                }
            }
        }

        $LayoutSuspended | ForEach-Object { $_.ResumeLayout($false); }
        $Form.ResumeLayout($true);
        if ($Form.ShowDialog() -eq [System.Windows.Forms.DialogResult]::OK) {
            $Result = New-Object -TypeName 'System.Management.Automation.PSObject' -Property @{ Key = $KeyTextBox.Text };
            [Enum]::GetValues([ResourceHelper.LanguageCode]) | ForEach-Object {
                $Result | Add-Member -MemberType NoteProperty -Name ([Enum]::GetName([ResourceHelper.LanguageCode],$_)) -Value $ValueTextBoxes[$_];
            }
            $Result | Write-Output;
        }
    } finally {
        $Form.Dispose();
    }
}

Function Prompt-EditBundleProperty {
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $true)]
        [ResourceHelper.ResourceBundle]$ResourceBundle,
        [string]$Key
    )
    
    $Result = $null;
    if ($PSBoundParameters.ContainsKey('Key')) {
        $Result = Show-EditBundlePropertyWindow -ResourceBundle $ResourceBundle -Key $Key -ErrorAction Stop;
    } else {
        $Result = Show-EditBundlePropertyWindow -ErrorAction Stop;
    }
    while ($null -ne $Result) {
        if ($Result.Key.Trim().Length -eq 0) {
            Write-Warning -Message 'Key cannot be empty.';
        } else {
            if ($ResourceBundle.ContainsKey($Result.Key)) {
                if ($PSBoundParameters.ContainsKey('Key') -and $Result -ieq $Key) {
                    [Enum]::GetValues([ResourceHelper.LanguageCode]) | ForEach-Object {
                        $ResourceBundle.Set($Result.Key, $Result.([Enum]::GetName([ResourceHelper.LanguageCode],$_)), $_);
                    }
                    return;
                }
                $ChoiceIndex = $Host.UI.PromptForChoice('Overwrite', "A key already exists with that name. Overwrite?", $Script:YesNoChoices, 2);
                if ($null -eq $ChoiceIndex -or $ChoiceIndex -gt 1 -or $ChoiceIndex -lt 0) { return }
                if ($ChoiceIndex -eq 0) {
                    if ($PSBoundParameters.ContainsKey('Key')) { $ResourceBundle.Remove($Key) }
                    [Enum]::GetValues([ResourceHelper.LanguageCode]) | ForEach-Object {
                        $ResourceBundle.Set($Result.Key, $Result.([Enum]::GetName([ResourceHelper.LanguageCode],$_)), $_);
                    }
                    return;
                }
            } else {
                if ($PSBoundParameters.ContainsKey('Key')) { $ResourceBundle.Remove($Key) }
                [Enum]::GetValues([ResourceHelper.LanguageCode]) | ForEach-Object {
                    $ResourceBundle.Set($Result.Key, $Result.([Enum]::GetName([ResourceHelper.LanguageCode],$_)), $_);
                }
                return;
            }
        }
        $Result = Show-EditBundlePropertyWindow -Values $Result -ErrorAction Stop;
    }
}

Function Prompt-BundleAction {
    [CmdletBinding()]
    Param(
        [Parameter(Mandatory = $true)]
        [ResourceHelper.ResourceBundle]$TargetBundle
    )

    [System.Collections.ObjectModel.Collection[System.Management.Automation.Host.ChoiceDescription]]$ChoiceCollection = @(
        [System.Management.Automation.Host.ChoiceDescription]::new('Edit', 'Edit items'),
        [System.Management.Automation.Host.ChoiceDescription]::new('Add', 'Add new item'),
        [System.Management.Automation.Host.ChoiceDescription]::new('Copy', 'Copy from another bundle'),
        [System.Management.Automation.Host.ChoiceDescription]::new('Move', 'Move from another bundle'),
        [System.Management.Automation.Host.ChoiceDescription]::new('Save', 'Save changes'),
        [System.Management.Automation.Host.ChoiceDescription]::new('Abort', 'Discard changes')
    );
    $Modified = @{};
    $ChoiceIndex = $Host.UI.PromptForChoice('Modify items', 'Select action', $ChoiceCollection, 1);
    while ($null -ne $ChoiceIndex -and $ChoiceIndex -lt $ChoiceCollection.Count - 1 -and $ChoiceIndex -ge 0) {
        switch ($ChoiceIndex) {
            0 {
                $Key = Select-BundleKey -Title 'Select property to edit' -ResourceBundle $TargetBundle;
                while ($null -ne $Key) {
                    Prompt-EditBundleProperty -ResourceBundle $TargetBundle -Key $Key;
                    $Key = Select-BundleKey -Title 'Select property to edit' -ResourceBundle $TargetBundle;
                }
                break;
            }
            1 {
                Prompt-EditBundleProperty -ResourceBundle $TargetBundle;
                break;
            }
            2 {
                $SourceBundle = Select-ResourceBundle -Title 'Select source resource bundle';
                if ($null -ne $SourceBundle) {
                    $Keys = @(Select-BundleKey -Title 'Select properties to copy' -ResourceBundle $SourceBundle -Multi);
                    if ($Keys.Count -gt 0) {
                        $Existing = @($Keys | Where-Object { $TargetBundle.ContainsKey($_); } );
                        $Force = $false;
                        if ($Existing.Count -gt 0) {
                            $ChoiceIndex = $Host.UI.PromptForChoice('Overwrite', "Overwrite $($Existing -join ', ')?", $Script:YesNoChoices, 2);
                            if ($null -eq $ChoiceIndex) {
                                $Keys = @();
                            } else {
                                if ($ChoiceIndex -eq 0) {
                                    $Force = $true;
                                } else {
                                    if ($ChoiceIndex -ne 1) {
                                        $Keys = @();
                                    }
                                }
                            }
                        }
                        if ($Keys.Count -gt 0) {
                            $Keys | ForEach-Object {
                                $SourceBundle.CopyTo($_, $TargetBundle, $Force);
                            }
                        }
                    }
                }
                break;
            }
            3 {
                $SourceBundle = Select-ResourceBundle -Title 'Select source resource bundle';
                if ($null -ne $SourceBundle) {
                    $Keys = @(Select-BundleKey -Title 'Select properties to move' -ResourceBundle $SourceBundle -Multi);
                    if ($Keys.Count -gt 0) {
                        $Existing = @($Keys | Where-Object { $TargetBundle.ContainsKey($_); } );
                        $Force = $false;
                        if ($Existing.Count -gt 0) {
                            $ChoiceIndex = $Host.UI.PromptForChoice('Overwrite', "Overwrite $($Existing -join ', ')?", $Script:YesNoChoices, 2);
                            if ($null -eq $ChoiceIndex) {
                                $Keys = @();
                            } else {
                                if ($ChoiceIndex -eq 0) {
                                    $Force = $true;
                                } else {
                                    if ($ChoiceIndex -ne 1) {
                                        $Keys = @();
                                    }
                                }
                            }
                        }
                        if ($Keys.Count -gt 0) {
                            $Modified[$SourceBundle.BaseName] = $SourceBundle;
                            $Keys | ForEach-Object {
                                $SourceBundle.MoveTo($_, $TargetBundle, $Force);
                            }
                        }
                    }
                }
                break;
            }
            4 {
                $TargetBundle.Save();
                $TargetBundle.SaveCode($Script:SourceRoot);
                if ($Modified.Count -gt 0) {
                    $Modified.Values | ForEach-Object {
                        $_.Save();
                        $_.SaveCode($Script:SourceRoot);
                    }
                }
                return;
            }
        }
        $ChoiceIndex = $Host.UI.PromptForChoice('Modify items', 'Select action', $ChoiceCollection, 1);
    }
}

$TargetBundle = Select-ResourceBundle -Title 'Select target resource bundle';
while ($null -ne $TargetBundle) {
    Prompt-BundleAction -TargetBundle $TargetBundle;
    $TargetBundle = Select-ResourceBundle -Title 'Select target resource bundle';
}

<#
$TargetBundle = $null;
while ($null -ne ($TargetBundle = Select-ResourceBundle -Title 'Select Target Resource Bundle')) {
    [ResourceHelper.ResourceBundle[]]$OtherBundles = $Script:AllBundleInfos | Where-Object { -not $_.Equals($TargetBundle) } | Open-ResourceBundle;
    $Modified = @{};
    $ChoiceIndex = $Host.UI.PromptForChoice('Modify bundle', 'Select action', $Script:__SelectBundleAction, 0);
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
                    $ChoiceIndex = $Host.UI.PromptForChoice('Modify items', 'Select action', $Script:EditChoices, 2);
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
                            $ChoiceIndex = $Host.UI.PromptForChoice('Overwrite', "Overwrite $($Existing -join ', ')?", $Script:YesNoChoices, 2);
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
                            $ChoiceIndex = $Host.UI.PromptForChoice('Overwrite', "Overwrite $($Existing -join ', ')?", $Script:YesNoChoices, 2);
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
                        $ChoiceIndex = $Host.UI.PromptForChoice('Overwrite', "Overwrite $($Existing -join ', ')?", $Script:YesNoChoices, 2);
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
        $ChoiceIndex = $Host.UI.PromptForChoice('Modify bundle', 'Select action', $Script:__SelectBundleAction, 0);
    }
    
    if ($ChoiceIndex -eq 5) {
        $TargetBundle.Save();
        $TargetBundle.SaveCode($Script:SourceRoot);
        if ($Modified.Count -gt 0) {
            $Modified.Values | ForEach-Object {
                $_.Save();
                $_.SaveCode($Script:SourceRoot);
            }
        }
    }
}
#>