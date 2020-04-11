Param(
    [string]$SourcePath = 'view\user\EditUser',
    [string]$TargetPath = 'view\customer\EditCustomer',
    [string[]]$Keys = @('currentAppointments', 'pastAppointments', 'allAppointments'),
    [boolean]$Move = $false
)
$Script:BaseResourcesPath = 'C:\Users\lerwi\OneDrive\Documents\NetBeansProjects\C195\Scheduler\resources\scheduler';
$Script:BaseCodePath = 'C:\Users\lerwi\OneDrive\Documents\NetBeansProjects\C195\Scheduler\src\scheduler';

$AllLocales = @('en', 'es', 'de', 'hi');

$SourceProperties = @{};
$TargetProperties = @{};

foreach ($Locale in $AllLocales) {
    $h = @{};
    $Path = $Script:BaseResourcesPath | Join-Path -ChildPath "$($SourcePath)_$Locale.properties";
    if ($Path | Test-Path -PathType Leaf) {
        [System.IO.File]::ReadAllLines($Path) | ForEach-Object { $_.Trim() } | Where-Object { $_.Length -gt 0 } | ForEach-Object {
            $a = $_.Split('=', 2);
            if ($a.Length -gt 1) {
                $h[$a[0]] = $a[1];
            }
        }
    }
    $SourceProperties[$Locale] = $h;
    $h = @{};
    $Path = $Script:BaseResourcesPath | Join-Path -ChildPath "$($TargetPath)_$Locale.properties";
    if ($Path | Test-Path -PathType Leaf) {
        [System.IO.File]::ReadAllLines($Path) | ForEach-Object { $_.Trim() } | Where-Object { $_.Length -gt 0 } | ForEach-Object {
            $a = $_.Split('=', 2);
            if ($a.Length -gt 1) {
                $h[$a[0]] = $a[1];
            }
        }
    }
    $TargetProperties[$Locale] = $h;
}
foreach ($Locale in $AllLocales) {
    $k = @($SourceProperties[$Locale].Keys);
    $SourceProperties.Keys | Where-Object { $_ -ine $Locale } | ForEach-Object {
        $h = $SourceProperties[$_];
        $k | ForEach-Object {
            if (-not $h.ContainsKey($_)) { $h[$_] = '' }
        }
    }
    $k = @($TargetProperties[$Locale].Keys);
    $TargetProperties.Keys | Where-Object { $_ -ine $Locale } | ForEach-Object {
        $h = $TargetProperties[$_];
        $k | ForEach-Object {
            if (-not $h.ContainsKey($_)) { $h[$_] = '' }
        }
    }
}
$Encoding = [System.Text.UTF8Encoding]::new($false, $false);
foreach ($Locale in $AllLocales) {
    $s = $SourceProperties[$Locale];
    $t = $TargetProperties[$Locale];
    foreach ($k in $Keys) {
        if ($s.ContainsKey($k)) {
            $t[$k] = $s[$k];
            if ($Move) { $s.Remove($k) }
        } else {
            $t[$k] = '';
        }
    }
    $Path = $Script:BaseResourcesPath | Join-Path -ChildPath "$($TargetPath)_$Locale.properties";
    [System.IO.File]::WriteAllLines($Path, ([string[]]@($t.Keys | Sort-Object | ForEach-Object { "$_=$($t[$_])" })), $Encoding);
    if ($Move) {
        $Path = $Script:BaseResourcesPath | Join-Path -ChildPath "$($SourcePath)_$Locale.properties";
        [System.IO.File]::WriteAllLines($Path, ([string[]]@($s.Keys | Sort-Object | ForEach-Object { "$_=$($s[$_])" })), $Encoding);
    }
}

$h = $TargetProperties[$AllLocales[0]];
$Path = $Script:BaseCodePath | Join-Path -ChildPath "$($TargetPath)ResourceKeys.java";
$Lines = @();
if ($Path | Test-Path -PathType Leaf) {
    $FileLines = [System.IO.File]::ReadAllLines($Path);
    for ($i = 0; $i -lt $Lines.Length; $i++) {
        if ($FileLines[$i].startsWith('public interface ')) {
            $Lines = $FileLines[0..($i - 1)];
            break;
        }
    }
}
if ($Lines.Length -eq 0) {
    $p = $TargetPath | Split-Path -Parent;
    $ns = @();
    while ($null -ne $p -and $p.Length -gt 0) {
        $ns = @($p | Split-Path -Leaf) + $ns;
        $p = $p | Split-Path -Parent;
    }
    $ns = @('scheduler') + $ns;
    $loc = Get-Location;
    Set-Location -Path ($Path | Split-Path -Parent);
    $rp = '..' | Join-Path -ChildPath (Resolve-Path -Path ($Script:BaseResourcesPath | Join-Path -ChildPath "$($TargetPath)_$Locale.properties") -Relative);
    Set-Location -Path $loc;
    $Lines = @(
        "package $($ns -join '.');",
        "",
        "/**",
        " * Resource bundle keys for {@code resources/scheduler/$($TargetPath.Replace('\', '/'))}.",
        " *",
        " * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;",
        " */"
    );
}

Add-Type -AssemblyName 'System.Web' -ErrorAction Stop;

$Lines += "public interface $([System.IO.Path]::GetFileNameWithoutExtension($Path)) {";
$Lines += @($h.Keys | Sort-Object | ForEach-Object {
    @"

    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the locale-specific text for {@code "$($h[$_].Replace('"', '\"'))"}.
     */
    public static final String RESOURCEKEY_$($_.ToUpper()) = "$_";
"@
})
$Lines += "}";
[System.IO.File]::WriteAllLines($Path, $Lines, $Encoding);

if ($Move) {
    $h = $SourceProperties[$AllLocales[0]];
    $Path = $Script:BaseCodePath | Join-Path -ChildPath "$($SourcePath)ResourceKeys.java";
    $Lines = @();
    if ($Path | Test-Path -PathType Leaf) {
        $FileLines = [System.IO.File]::ReadAllLines($Path);
        for ($i = 0; $i -lt $Lines.Length; $i++) {
            if ($FileLines[$i].startsWith('public interface ')) {
                $Lines = $FileLines[0..($i - 1)];
                break;
            }
        }
    }
    if ($Lines.Length -eq 0) {
        $p = $SourcePath | Split-Path -Parent;
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
            " * Resource bundle keys for {@code resources/scheduler/$($SourcePath.Replace('\', '/'))}.",
            " *",
            " * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;",
            " */"
        );
    }

    $Lines += "public interface $([System.IO.Path]::GetFileNameWithoutExtension($Path)) {";
    $Lines += @($h.Keys | Sort-Object | ForEach-Object {
        @"

        /**
         * Resource key in the current {@link java.util.ResourceBundle} that contains the locale-specific text for {@code "$($h[$_].Replace('"', '\"'))"}.
         */
        public static final String RESOURCEKEY_$($_.ToUpper()) = "$_";
"@
    })
    $Lines += "}";
    [System.IO.File]::WriteAllLines($Path, $Lines, $Encoding);
}