Param(
    [ValidateSet('Debug', 'Release')]
    [string]$Configuration = 'Debug',

    [string]$Platform = 'Any CPU',

    [ValidateSet('Test', 'Deploy', 'None')]
    [string]$Action = 'None',

    # Resources;Compile
    [ValidateSet('Build', 'Resources', 'Compile', 'Rebuild', 'Clean', 'Publish')]
    [string[]]$Targets = @('Build')
)

$MSBuildExePath = 'C:\Program Files (x86)\MSBuild\14.0\Bin\MSBuild.exe';
$MSBuildExePath = 'C:\Program Files (x86)\Microsoft Visual Studio\2017\BuildTools\MSBuild\15.0\Bin\MSBuild.exe';
. $MSBuildExePath "/t:$($Targets -join ';')" "/p:GenerateFullPaths=true;Configuration=`"$Configuration`";Platform=`"$Platform`"" 'ResourceHelper.sln';