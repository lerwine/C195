Param(
    [string]$SourcePath = 'view\user\EditUser',
    [string]$TargetPath = 'view\appointment\ManageAppointments',
    #[string[]]$Keys = @(''),
    [string[]]$Keys = @('status'),
    [boolean]$Move = $false
)
$Script:BaseResourcesPath = 'C:\Users\lerwi\OneDrive\Documents\NetBeansProjects\C195\Scheduler\resources\scheduler';
$Script:BaseCodePath = 'C:\Users\lerwi\OneDrive\Documents\NetBeansProjects\C195\Scheduler\src\scheduler';

<#

java.sql.SQLException: No value specified for parameter 
at scheduler.dao.DataAccessObject$DaoFactory.save(DataAccessObject.java:626)

UPDATE appointment SET customerId=?, userId=?, title=?, description=?, location=?, contact=?, type=?, url=?, start=?, end=?, lastUpdate=?, lastUpdateBy=? WHERE appointmentId=?
Apr 15, 2020 12:05:47 AM scheduler.util.AlertHelper showErrorAlert



INFO: Appending column SQL for column customerId at index 1
INFO: Appending column SQL for userId at index 2
INFO: Appending column SQL for title at index 3
INFO: Appending column SQL for description at index 4
INFO: Appending column SQL for location at index 5
INFO: Appending column SQL for contact at index 6
INFO: Appending column SQL for type at index 7
INFO: Appending column SQL for url at index 8
INFO: Appending column SQL for start at index 9
INFO: Appending column SQL for end at index 10
INFO: Appending column SQL for lastUpdate at index 11
INFO: Appending column SQL for lastUpdateBy at index 12
INFO: Appending column SQL for appointmentId at index 13
INFO: Setting value SQL for column appointmentId at index 1
INFO: Setting value SQL for column appointmentId at index 2
INFO: Setting value SQL for column appointmentId at index 3
INFO: Setting value SQL for column appointmentId at index 4
INFO: Setting value SQL for column appointmentId at index 5
INFO: Setting value SQL for column appointmentId at index 6
INFO: Setting value SQL for column appointmentId at index 7
INFO: Setting value primary key at index 8



@SuppressWarnings("incomplete-switch")
    @SuppressWarnings("unchecked")
    @SuppressWarnings("unused")
    @SuppressWarnings("unusedArgument")
    @SuppressWarnings("varargsCast")
    @SuppressWarnings("ForLoopToFunctionalHint")
    org.netbeans.modules.java.hints.jdk.mapreduce.ForLoopToFunctionalHint
    TernarySelective<ZonedDateTime, Pair<ZonedDateTime, String>, String>
    ofPrimary(ZonedDateTime);
    ofSecondary(Pair<ZonedDateTime, String>);
    ofTertiary(String)

    IntermediaryBinding: BinarySelective<Pair<ZonedDateTime, Pair<Integer, Integer>>, String>
    ofPrimary(Pair<ZonedDateTime, Pair<Integer, Integer>>);
    ofSecondary(String)

     selection | option
       true    |  true  =primary
       true    |  false =secondary
       false   |  true  =tertiary
       false   |  false =none

     * <tr><td>{@code true}</td><td>{@code false}</td><td>{@link #value} contains the secondary option value.</td></tr>
     * <tr><td>{@code false}</td><td>{@code true}</td><td>{@link #value} contains the tertiary option value.</td></tr>
     * <tr><td>{@code false}</td><td>{@code false}</td><td>Contains no value.</td></tr>

    अमान्य पूर्वाह्न / अपराह्न डिज़ाइनर।
    
    अमान्य पूर्वाह्न / अपराह्न डिजाइनर
    अमान्य पूर्वाह्न/बजे डिजाइनर


    पूर्वाह्न / पीएम निर्दिष्ट नहीं है।
    A.M. / PM is not specified.
    
    बजे / पीएम निर्दिष्ट नहीं है।
    प्रातः / पीएम निर्दिष्ट नहीं है।
    हूँ / पीएम निर्दिष्ट नहीं है।

    , रहा हूँ, बजे,

    हूँ / पीएम निर्दिष्ट नहीं है।
    हूँ / रहा निर्दिष्ट नहीं है।
    हूँ / हूँ निर्दिष्ट नहीं है।
    हूँ / बजे निर्दिष्ट नहीं है।


    पूर्वाह्न या अपराह्न। निर्दिष्ट नहीं है।
    Am or afternoon. Not specified.

    a.m./p.m. designator not specified.
    दोपहर से पूर्व दोपहर के बाद। निर्दिष्ट नहीं है।
    a.m. or p.m. designator not specified.
    पूर्वाह्न या अपराह्न। निर्दिष्ट नहीं है।

#>
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