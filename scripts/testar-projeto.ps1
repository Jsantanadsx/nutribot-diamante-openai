$ErrorActionPreference = 'Stop'
Set-Location (Split-Path -Parent $PSScriptRoot)
$java21 = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.8.9-hotspot'
if (Test-Path $java21) {
    $env:JAVA_HOME = $java21
    $env:Path = "$env:JAVA_HOME\bin;$env:Path"
}
java -version
mvn -version
mvn clean test
if ($LASTEXITCODE -ne 0) { throw "Falha no teste Maven: $LASTEXITCODE" }
