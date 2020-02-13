[System.Windows.Clipboard]::SetText(((@(@'
    //<editor-fold defaultstate="collapsed" desc="Resource bundle keys">
'@) + @(([System.Windows.Clipboard]::GetText().Trim() -split '[\r\n]+') | ForEach-Object {
    ($k, $t) = $_.Split('=', 2);
    $i = $t.IndexOf('\n');
    if ($i -gt 0) {
        $t = $t.Substring(0, $i);
        if ($t.EndsWith('.')) { $t += ".." } else { $t += "..." }
    }
    @"
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "$t"}.
     */
    public static final String RESOURCEKEY_$($k.ToUpper()) = "$k";
"@
}) + @(
@'

    //</editor-fold>
'@
)) | Out-String).Trim());