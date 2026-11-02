$ErrorActionPreference = 'Stop'
Set-Location (Split-Path -Parent $PSScriptRoot)

$java21 = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.8.9-hotspot'
if (Test-Path $java21) {
    $env:JAVA_HOME = $java21
    $env:Path = "$env:JAVA_HOME\bin;$env:Path"
}

$env:OPENAI_BASE_URL = 'https://api.openai.com/v1'
$env:OPENAI_COMPLETIONS_PATH = '/v1/chat/completions'
$env:OPENAI_EMBEDDINGS_PATH = '/v1/embeddings'
$env:OPENAI_MODEL = 'gpt-4o-mini'
$env:OPENAI_EMBEDDING_MODEL = 'text-embedding-3-small'

if ([string]::IsNullOrWhiteSpace($env:OPENAI_API_KEY) -or $env:OPENAI_API_KEY -eq 'ollama') {
    Write-Host 'Digite sua chave OpenAI API (entrada oculta, somente nesta sessao):'
    $secureKey = Read-Host -AsSecureString
    $ptr = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($secureKey)
    try {
        $env:OPENAI_API_KEY = [System.Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr)
    } finally {
        [System.Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr)
    }
}
if ([string]::IsNullOrWhiteSpace($env:OPENAI_API_KEY)) { throw 'Nenhuma chave API foi fornecida.' }

Write-Host 'Validando ambiente...'
java -version
mvn -version
Write-Host 'Iniciando NutriBot com a OpenAI API; o carregamento RAG utiliza embeddings pagos.'
mvn spring-boot:run
if ($LASTEXITCODE -ne 0) { throw "Maven encerrou com codigo $LASTEXITCODE" }

