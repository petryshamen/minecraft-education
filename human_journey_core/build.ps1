param([Parameter(Mandatory=$true)][string]$JavaHome)
$ErrorActionPreference = 'Stop'
$projectRoot = $PSScriptRoot
$javaExecutable = Join-Path $JavaHome 'bin/java.exe'
$compilerExecutable = Join-Path $JavaHome 'bin/javac.exe'
if (!(Test-Path -LiteralPath $javaExecutable) -or !(Test-Path -LiteralPath $compilerExecutable)) {
    throw 'Supply a complete JDK 21 directory, not a JRE.'
}
$javaVersion = (& $javaExecutable -version 2>&1 | Out-String)
if ($javaVersion -notmatch 'version "21[.\"]') { throw "JDK 21 required: $javaVersion" }
$saved = @{}
$variables = @('JAVA_HOME','GRADLE_USER_HOME','TEMP','TMP','JAVA_TOOL_OPTIONS')
foreach ($name in $variables) { $saved[$name] = [Environment]::GetEnvironmentVariable($name, 'Process') }
try {
    foreach ($name in @('.tmp','.gradle-user-home','.java-user-home')) {
        New-Item -ItemType Directory -Force -Path (Join-Path $projectRoot $name) | Out-Null
    }
    $env:JAVA_HOME = (Resolve-Path -LiteralPath $JavaHome).Path
    $env:GRADLE_USER_HOME = Join-Path $projectRoot '.gradle-user-home'
    $env:TEMP = Join-Path $projectRoot '.tmp'
    $env:TMP = $env:TEMP
    $userHome = Join-Path $projectRoot '.java-user-home'
    $env:JAVA_TOOL_OPTIONS = '-Djava.io.tmpdir="' + $env:TEMP + '" -Duser.home="' + $userHome + '"'
    Push-Location -LiteralPath $projectRoot
    try {
        & (Join-Path $projectRoot 'gradlew.bat') --no-daemon --project-cache-dir .gradle-project-cache test build
        if ($LASTEXITCODE -ne 0) { throw "Gradle failed with exit code $LASTEXITCODE" }
        $jarPath = Join-Path $projectRoot 'build/libs/human_journey_core-0.1.0.jar'
        if (!(Test-Path -LiteralPath $jarPath)) { throw 'Runtime JAR is missing after build.' }
        Get-FileHash -LiteralPath $jarPath -Algorithm SHA256
    } finally { Pop-Location }
} finally {
    foreach ($name in $variables) { [Environment]::SetEnvironmentVariable($name, $saved[$name], 'Process') }
}
