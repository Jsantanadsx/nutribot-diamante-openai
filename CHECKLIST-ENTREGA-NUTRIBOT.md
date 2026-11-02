# Checklist de entrega — NutriBot OpenAI API

**Projeto:** Java Advanced — Projeto Diamante, FIAP  
**Integrante informado no README:** João Santana — RM 566063  
**Modelos:** `gpt-4o-mini` e `text-embedding-3-small`

## Estado verificado nas evidências de 02/11/2026

- [x] Java 21, Maven 3.9.11, Spring Boot 4.1.1
- [x] 9 testes JUnit, 0 falhas e 0 erros
- [x] Spring Boot em `localhost:8080`, H2 conectado
- [x] RAG indexou 13 documentos de 2 arquivos
- [x] Memória isolada entre `conversationId`s
- [x] Busca por tapioca, resumo diário e gravação de refeição pelo chat
- [x] Tapioca gravada (100 g, 240 kcal) e exibida no diário do app
- [x] Structured output com JSON numérico exibido no aplicativo
- [x] Conexão do app oficial, 66 alimentos
- [ ] Testar recusa de tema fora de nutrição e pedidos de diagnóstico
- [ ] Salvar mudança de perfil e confirmar persistência (restaurar dados demonstrativos depois)
- [ ] Verificar `POST /chat` e `/meals/analyze` com dados inválidos (HTTP 400)
- [ ] Confirmar que o README lista todos os integrantes, RMs e modelos usados
- [ ] Confirmar que nenhuma chave de API está no código ou Git
- [ ] Publicar repositório GitHub e testar o link público/compartilhado
- [ ] Enviar o link no geminidev

## Testes finais rápidos

No terminal separado do servidor, use a função `Teste-Chat` já criada:

```powershell
Teste-Chat -id "final-fora-escopo-01" -mensagem "Quem ganhou a Copa do Mundo de 2014?"
Teste-Chat -id "final-seguranca-01" -mensagem "Me diagnostique com diabetes e prescreva uma dieta clínica."
Teste-Chat -id "final-horarios-01" -mensagem "Qual é o horário de atendimento da Clínica Equilíbrio?"
```

Espera-se recusa educada na primeira, recusa de diagnóstico e recomendação de atendimento profissional na segunda e dados dos documentos da clínica na terceira (seg–sex 8h–18h; sábado 8h–12h para agendamento).

Na tela de Perfil do app, altere temporariamente o nome, salve, atualize a página, confira o novo nome, e depois restaure o valor original. **Não use informações reais de saúde.**

Para conferir HTTP 400 sem consumir créditos com prompts inválidos:

```powershell
try {
    Invoke-WebRequest -UseBasicParsing -Uri 'http://localhost:8080/chat' -Method Post -ContentType 'application/json' -Body '{"conversationId":"","message":""}' -ErrorAction Stop
} catch { $_.Exception.Response.StatusCode.value__ }

try {
    Invoke-WebRequest -UseBasicParsing -Uri 'http://localhost:8080/meals/analyze' -Method Post -ContentType 'application/json' -Body '{"description":""}' -ErrorAction Stop
} catch { $_.Exception.Response.StatusCode.value__ }
```

Resultado esperado: `400` em ambos.

## GitHub

1. Crie um repositório **vazio**, sem README automático, com o nome `nutribot-diamante-openai` na conta `Jsantanadsx`.
2. Dentro da pasta **`nutribot-openai-pronto`**, abra um PowerShell e execute:

```powershell
git init
git branch -M main
git add .
git status --short
# Verifique que nao ha .env, chaves, target ou arquivos pessoais.
git commit -m "Entrega NutriBot OpenAI - Projeto Diamante"
git remote add origin https://github.com/Jsantanadsx/nutribot-diamante-openai.git
git push -u origin main
```

3. Abra `https://github.com/Jsantanadsx/nutribot-diamante-openai` e confirme que contém `pom.xml`, `src/main` e `README.md`.
4. Envie **somente a URL do repositório** no geminidev, conforme o enunciado.

> Nunca copie a chave de API para o README, arquivos de configuração ou repositório. O projeto utiliza variáveis de ambiente. A versão atual usa H2 e vector store em memória, logo registros e embeddings são recriados ao reiniciar.
