# Execute este script a partir da pasta nutribot-openai-pronto.
# Nao faz chamadas para a OpenAI, nao envia nada ao GitHub e nao manipula chaves.
$ErrorActionPreference = 'Stop'
$root = (Get-Location).Path
$pom = Join-Path $root 'pom.xml'
$readme = Join-Path $root 'README.md'
$yml = Join-Path $root 'src\main\resources\application.yml'
$start = Join-Path $root 'scripts\iniciar-openai.ps1'
$envExample = Join-Path $root '.env.example'
$gitignore = Join-Path $root '.gitignore'

foreach ($required in @($pom, $readme, $yml, $start, $gitignore)) {
    if (-not (Test-Path -LiteralPath $required -PathType Leaf)) {
        throw "Arquivo esperado nao encontrado: $required. Abra o projeto nutribot-openai-pronto e tente novamente."
    }
}

function Update-IfChanged([string]$path, [string]$before, [string]$after) {
    $content = [System.IO.File]::ReadAllText($path)
    $updated = $content.Replace($before, $after)
    if ($updated -ne $content) {
        [System.IO.File]::WriteAllText($path, $updated, ([System.Text.UTF8Encoding]::new($false)))
        Write-Host "Atualizado: $path" -ForegroundColor Green
    } else {
        Write-Host "Ja conferido: $path"
    }
}

Write-Host '=== REVISAO FINAL DO NUTRIBOT ===' -ForegroundColor Cyan
Update-IfChanged $start "'https://api.openai.com'" "'https://api.openai.com/v1'"
Update-IfChanged $yml 'OPENAI_BASE_URL:https://api.openai.com}' 'OPENAI_BASE_URL:https://api.openai.com/v1}'
if (Test-Path -LiteralPath $envExample -PathType Leaf) {
    if ([System.IO.File]::ReadAllText($envExample) -notmatch 'OPENAI_BASE_URL=https://api\.openai\.com/v1') {
        Update-IfChanged $envExample 'OPENAI_BASE_URL=https://api.openai.com' 'OPENAI_BASE_URL=https://api.openai.com/v1'
    } else {
        Write-Host 'Ja conferido: .env.example'
    }
}

$gitignoreText = [System.IO.File]::ReadAllText($gitignore)
$lines = @($gitignoreText -split "`r?`n")
$missing = @('/target/', '.env', '.env.*', '!.env.example', '*.key', '*.pem') | Where-Object { $lines -notcontains $_ }
if ($missing.Count -gt 0) {
    [System.IO.File]::AppendAllText($gitignore, "`n" + ($missing -join "`n") + "`n", ([System.Text.UTF8Encoding]::new($false)))
    Write-Host 'Gitignore atualizado para ignorar arquivos sensiveis.' -ForegroundColor Green
}

$readmeText = [System.IO.File]::ReadAllText($readme)
$mark = '## Validacao funcional (02/11/2026)'
if (-not $readmeText.Contains($mark)) {
    $append = @'

## Validacao funcional (02/11/2026)

A versao Java 21 / Spring Boot 4.1.1 compilou com **9 testes JUnit aprovados (0 erros, 0 falhas)** no Windows 11.

Validacoes observadas com OpenAI API e aplicativo oficial do professor:

- Conversa e memoria: lembrou o nome Rafael no mesmo `conversationId` e nao o revelou em outra conversa.
- RAG: respondeu que a Clinica Equilibrio recomenda 10 copos de 200 ml de agua por dia.
- Tool de alimentos: consultou tapioca (240 kcal por 100 g).
- Tool de resumo: consultou o saldo calorico do perfil de exemplo.
- Tool de registro: gravou tapioca, 100 g, `BREAKFAST`, confirmada via `GET /meals` e exibida no diario do aplicativo.
- Structured output: `POST /meals/analyze` devolveu `items`, `totalCalories`, `protein`, `carbs`, `fat` com numeros; a tela Analisar exibiu os resultados.
- App oficial: conexao com 66 alimentos e exibicao das telas de Diario, Alimentos, Analise e Perfil.

**A confirmar na revisao final:** recusa de perguntas fora do escopo, orientacoes clinicas seguras, salvar alteracoes do perfil e erros 400 por JSON invalido. O funcionamento no navegador nao comprova por si so um teste em dispositivo fisico.
'@
    [System.IO.File]::AppendAllText($readme, $append, ([System.Text.UTF8Encoding]::new($false)))
    Write-Host 'README atualizado com evidencias reais da validacao.' -ForegroundColor Green
} else {
    Write-Host 'README ja contem a secao de validacao.'
}

$readmeText = [System.IO.File]::ReadAllText($readme)
if ($readmeText -notmatch '566063' -or $readmeText -notmatch 'gpt-4o-mini') {
    Write-Warning 'Revise o README: RM 566063 e modelo gpt-4o-mini precisam constar.'
}

# Pesquisa por padroes de chaves reais nos arquivos-fonte; nao imprime valores secretos.
$extensions = @('.java', '.yml', '.yaml', '.properties', '.md', '.ps1', '.json', '.http', '.xml', '.env')
$sourceFiles = @(Get-ChildItem -LiteralPath $root -File -Recurse -Force | Where-Object {
    $_.FullName -notmatch '[\\/](\.git|target|node_modules)[\\/]' -and
    ($extensions -contains $_.Extension -or $_.Name -eq '.env')
})
$secretFiles = @()
foreach ($file in $sourceFiles) {
    $content = [System.IO.File]::ReadAllText($file.FullName)
    if ($content -match 'sk-(?:proj-)?[A-Za-z0-9_-]{20,}') { $secretFiles += $file.FullName }
}
if ($secretFiles.Count -gt 0) {
    Write-Warning 'ATENCAO: possiveis chaves identificadas. NAO FACA PUSH antes de revisar os arquivos abaixo:'
    $secretFiles | ForEach-Object { Write-Host $_ -ForegroundColor Yellow }
} else {
    Write-Host 'Nenhuma chave com padrao sk- identificada nos arquivos-fonte analisados.' -ForegroundColor Green
}

Write-Host ''
Write-Host '=== PROXIMOS PASSOS ===' -ForegroundColor Cyan
Write-Host '1. Execute: powershell -ExecutionPolicy Bypass -File .\scripts\testar-projeto.ps1'
Write-Host '2. Confira os testes finais de persona e perfil no app.'
Write-Host '3. Crie um repositorio VAZIO no GitHub e publique somente a pasta deste projeto.'
Write-Host 'Este script NAO executou chamadas pagas nem publicou arquivos.'
