# NutriBot API — Projeto Diamante, FIAP Java Advanced

Implementação a partir da **base oficial**: https://github.com/joaocarloslima/nutribot

## Equipe
| Integrante | RM |
| --- | --- |
| João Santana | 566063 |

*Modelo principal:* `gpt-4o-mini` (OpenAI API). *Embeddings:* `text-embedding-3-small`.

## Requisitos
- JDK 21 (confirmado no Windows), Maven 3.9.x e acesso à internet.
- **Chave de API da OpenAI válida, faturamento habilitado e créditos suficientes.**
- Nunca publicar chave, `.env`, cartão ou token. ChatGPT Plus **não** inclui créditos de API.

## Preparação

1. Gere o projeto completo com `montar-projeto.ps1` do pacote de entrega. O script clona a base do professor, sobrepõe esta implementação e remove o remoto de origem para evitar push no repositório do professor.
2. Abra a pasta gerada `nutribot-openai-pronto`.
3. No PowerShell: `powershell -ExecutionPolicy Bypass -File .\scripts\testar-projeto.ps1`. Esse script ativa Java 21 e roda testes sem consumir créditos da OpenAI.
4. Configure sua API key em um terminal, ou use o script de inicialização que solicita a chave sem mostrá-la na tela.
5. Rode `powershell -ExecutionPolicy Bypass -File .\scripts\iniciar-openai.ps1` dentro do projeto.
6. Confirme nos logs: `Tomcat started on port 8080` e `RAG pronto: ... documentos de 2 arquivos.`
7. Execute os testes de `test-ai.http` ou `powershell -ExecutionPolicy Bypass -File .\scripts\testar-api.ps1` (o script faz chamadas pagas).

## Funcionalidades implementadas
- `POST /chat`: ChatClient + system prompt em português; recusa assuntos fora do escopo clínico/nutricional; memória por `conversationId` usando `MessageChatMemoryAdvisor`.
- **Roteamento de contexto:** o controlador ativa RAG somente para perguntas sobre regras/documentos; ativa uma única tool para buscas, resumo ou registros; mensagens de nome não têm tools nem RAG. A memória funciona em todos os casos.
- **Três tools @Tool:** `buscarAlimento`, `resumoDoDia`, `registrarRefeicao`, utilizando `FoodService` e `MealService` originais.
- **RAG:** 2 arquivos de `src/main/resources/docs` do professor, indexados na inicialização usando `SimpleVectorStore` e embeddings da OpenAI. Respostas baseadas no documento da clínica.
- `POST /meals/analyze`: records com `.entity(MealAnalysisResponse.class)` sem parse manual, sem persistência.
- Todos os endpoints legados de alimentos, refeições, resumo e perfil do professor são mantidos, na porta `8080`.

## Contrato

| Método | Caminho | Status esperado |
| --- | --- | --- |
| POST | `/chat` | 200 `{ "answer": "..." }`; 400 inválido |
| POST | `/meals/analyze` | 200 objeto de estimativa; 400 inválido |
| GET | `/foods?name=` | 200 alimentos |
| GET | `/meals?date=` | 200 diário |
| POST | `/meals` | 201 registro |
| DELETE | `/meals/{id}` | 204 exclusão |
| GET | `/summary?date=` | 200 resumo |
| GET, PUT | `/profile` | 200 perfil |

O payload da análise possui `{ "items": [{"food":"...", "grams":150, "calories":192}], "totalCalories":557, "protein":48.2, "carbs":56.0, "fat":14.1 }` (valores meramente ilustrativos).

## Pontos de atenção
- **O script de teste da API consome créditos** e realiza 1 registro de tapioca no diário H2.
- O banco H2, a memória e o vector store ficam em RAM. Reiniciar limpa o histórico e os registros.
- Roteamento usa palavras-chave para as intenções obrigatórias do trabalho. Perguntas ambíguas devem ser verificadas; não é um classificador universal.
- Respostas e estimativas do modelo variam. Uso exclusivamente didático, não substitui assistência profissional.
- Se aparecer erro `401`, confira chave API. `429` pode ser crédito/limite. Sem créditos, o carregamento RAG falhará antes de finalizar o startup.

## Entrega

Publique o código completo gerado na pasta `nutribot-openai-pronto` em repositório GitHub público ou com acesso do professor. No **geminidev**, envie **somente o link do GitHub**.

App fornecido pelo professor: https://github.com/joaocarloslima/nutribot-app

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