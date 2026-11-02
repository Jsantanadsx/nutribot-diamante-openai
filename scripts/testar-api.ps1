$ErrorActionPreference = 'Stop'
$baseUrl = 'http://localhost:8080'
$run = [Guid]::NewGuid().ToString('N').Substring(0, 10)
function PostJson([string]$path, [hashtable]$payload) {
    $body = $payload | ConvertTo-Json -Depth 8 -Compress
    $response = Invoke-WebRequest -UseBasicParsing -Method Post `
        -Uri "$baseUrl$path" -ContentType 'application/json; charset=utf-8' `
        -Body ([Text.Encoding]::UTF8.GetBytes($body))
    return ([Text.Encoding]::UTF8.GetString($response.RawContentStream.ToArray()) | ConvertFrom-Json)
}
function Display($name, $value) {
    Write-Host "`n=== $name ===" -ForegroundColor Cyan
    $value | ConvertTo-Json -Depth 10
}
Write-Host 'Cada teste com IA consome tokens pagos. O registro altera o diario H2 em memoria.' -ForegroundColor Yellow

$food = Invoke-RestMethod -Uri "$baseUrl/foods?name=tapioca" -Method Get
Display '1. GET /foods' $food

$id = "teste-memoria-$run"
Display '2. Memoria - apresentar nome' (PostJson '/chat' @{conversationId=$id;message='Meu nome é Rafael.'})
Display '3. Memoria - perguntar mesmo ID' (PostJson '/chat' @{conversationId=$id;message='Qual é o meu nome?'})
Display '4. Memoria - outro ID' (PostJson '/chat' @{conversationId="outro-$run";message='Qual é o meu nome?'})
Display '5. RAG - agua' (PostJson '/chat' @{conversationId="rag-$run";message='Quantos copos de agua por dia a Clinica Equilibrio recomenda?'})
Display '6. Tool - buscar alimento' (PostJson '/chat' @{conversationId="food-$run";message='Quantas calorias tem 100 g de tapioca?'})
Display '7. Tool - resumo' (PostJson '/chat' @{conversationId="summary-$run";message='Quantas calorias ainda posso comer hoje?'})
Display '8. Tool - registrar 100g' (PostJson '/chat' @{conversationId="register-$run";message='Registre 100 gramas de tapioca no cafe da manha, por favor.'})
Display '9. Confirmar registro no diario' (Invoke-RestMethod -Uri "$baseUrl/meals" -Method Get)
Display '10. Structured output (nao registra)' (PostJson '/meals/analyze' @{description='arroz, feijao e bife'})
Write-Host "`nFim dos testes. Confirme os resultados e eventuais erros acima." -ForegroundColor Green
