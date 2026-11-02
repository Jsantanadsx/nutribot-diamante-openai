# NutriBot API — Projeto Diamante, FIAP Java Advanced

Implementação a partir da **base oficial**: https://github.com/joaocarloslima/nutribot

## Equipe

| Integrante | RM |
| --- | --- |
| João Santana | 566063 |

**Modelo principal:** `gpt-4o-mini` (OpenAI API). **Embeddings:** `text-embedding-3-small`.

## Requisitos

- JDK 21 (validado no Windows), Maven 3.9.x e acesso à internet.
- **Chave de API da OpenAI válida, faturamento habilitado e créditos suficientes.**
- Nunca publicar chaves, arquivos `.env` com credenciais, cartões ou tokens. Uma assinatura do ChatGPT Plus **não** inclui créditos de API.

## Preparação

1. Clone este repositório:

   ```bash
   git clone https://github.com/Jsantanadsx/nutribot-diamante-openai.git
   ```

2. Acesse a pasta do projeto:

   ```bash
   cd nutribot-diamante-openai
   ```

3. No PowerShell, execute os testes automatizados:

   ```powershell
   powershell -ExecutionPolicy Bypass -File .\scripts\testar-projeto.ps1
   ```

   Esse script seleciona o Java 21 e executa os testes sem consumir créditos da OpenAI.

4. Configure sua chave de API da OpenAI como variável de ambiente ou utilize o script de inicialização, que solicita a chave sem mostrá-la na tela.

5. Inicie a API a partir da pasta do projeto:

   ```powershell
   powershell -ExecutionPolicy Bypass -File .\scripts\iniciar-openai.ps1
   ```

6. Confirme nos logs o início do servidor na porta `8080` e a mensagem `RAG pronto: ... documentos de 2 arquivos.`

7. Execute os testes de `test-ai.http` ou, em outro terminal PowerShell, rode:

   ```powershell
   powershell -ExecutionPolicy Bypass -File .\scripts\testar-api.ps1
   ```

   **Atenção:** esse teste realiza chamadas pagas à OpenAI e registra uma refeição de teste no banco H2 em memória.

## Funcionalidades implementadas

- **`POST /chat`:** `ChatClient` com system prompt em português e orientações para recusar assuntos fora do escopo clínico/nutricional; memória por `conversationId` utilizando `MessageChatMemoryAdvisor`.
- **Roteamento de contexto:** o controlador ativa RAG somente para perguntas sobre regras ou documentos; disponibiliza uma ferramenta específica para buscas, resumo ou registro de refeições. Mensagens sobre nomes não recebem ferramentas nem RAG. A memória é utilizada em todos os casos.
- **Três ferramentas com `@Tool`:** `buscarAlimento`, `resumoDoDia` e `registrarRefeicao`, utilizando os serviços de alimentos e refeições.
- **RAG:** dois arquivos em `src/main/resources/docs`, indexados na inicialização com `SimpleVectorStore` e embeddings da OpenAI. As respostas sobre regras da Clínica Equilíbrio utilizam esses documentos como contexto.
- **`POST /meals/analyze`:** resposta estruturada com Java records e `.entity(MealAnalysisResponse.class)`, sem parsing manual e sem persistência no diário alimentar.
- **Endpoints originais:** alimentos, refeições, resumo diário e perfil, mantidos na porta `8080`.

## Contrato da API

| Método | Caminho | Status esperado |
| --- | --- | --- |
| POST | `/chat` | 200 com `{ "answer": "..." }`; 400 para entrada inválida |
| POST | `/meals/analyze` | 200 com objeto de estimativa; 400 para entrada inválida |
| GET | `/foods?name=` | 200 com alimentos |
| GET | `/meals?date=` | 200 com diário |
| POST | `/meals` | 201 com registro |
| DELETE | `/meals/{id}` | 204 após exclusão |
| GET | `/summary?date=` | 200 com resumo |
| GET, PUT | `/profile` | 200 com perfil |

Exemplo ilustrativo de resposta de `/meals/analyze`:

```json
{
  "items": [
    { "food": "arroz", "grams": 150, "calories": 192 }
  ],
  "totalCalories": 192,
  "protein": 4.0,
  "carbs": 42.0,
  "fat": 0.5
}
```

Os valores acima são apenas ilustrativos; as estimativas variam de acordo com a descrição da refeição e a resposta do modelo.

## Pontos de atenção

- **O script de teste da API consome créditos** e registra uma porção de tapioca no diário H2 em memória.
- O banco H2, a memória das conversas e o vector store ficam em RAM; reiniciar a aplicação limpa o histórico e os registros.
- O roteamento utiliza palavras-chave para as intenções principais do trabalho. Perguntas ambíguas podem exigir validação adicional; não se trata de um classificador universal.
- Respostas e estimativas do modelo podem variar. O sistema tem finalidade didática e não substitui assistência profissional.
- Em caso de erro `401`, confira a chave da API. Um erro `429` pode indicar limite ou insuficiência de créditos. O carregamento inicial do RAG depende dos embeddings da OpenAI.

## Entrega

O código-fonte completo do NutriBot está disponível neste repositório, incluindo a API Java Spring Boot, integração com Spring AI, documentos utilizados no RAG, ferramentas de consulta e registro alimentar, testes automatizados e scripts de execução.

**Entrega:** enviar o link deste repositório GitHub na plataforma **geminidev**, conforme as instruções do professor:

https://github.com/Jsantanadsx/nutribot-diamante-openai

**Aplicativo oficial fornecido pelo professor:** https://github.com/joaocarloslima/nutribot-app

## Validação funcional (02/11/2026)

A versão Java 21 / Spring Boot 4.1.1 compilou com **9 testes JUnit aprovados, 0 erros e 0 falhas**, no Windows 11.

Validações observadas com OpenAI API e com o aplicativo oficial do professor:

- **Conversa e memória:** o assistente lembrou o nome Rafael no mesmo `conversationId` e não revelou essa informação em outra conversa.
- **RAG:** respondeu que a Clínica Equilíbrio recomenda 10 copos de 200 ml de água por dia.
- **Ferramenta de alimentos:** consultou tapioca, com 240 kcal por 100 g.
- **Ferramenta de resumo:** consultou o saldo calórico do perfil de exemplo.
- **Ferramenta de registro:** gravou tapioca, 100 g, `BREAKFAST`, confirmada via `GET /meals` e exibida no diário do aplicativo.
- **Structured Output:** `POST /meals/analyze` devolveu `items`, `totalCalories`, `protein`, `carbs` e `fat` com valores numéricos; a tela **Analisar** exibiu esses resultados.
- **Aplicativo oficial:** conexão bem-sucedida com 66 alimentos e exibição das telas **Diário**, **Alimentos**, **Analisar** e **Perfil**.

**Verificações adicionais ainda não documentadas como concluídas:** recusa a perguntas fora do escopo, orientações clínicas seguras, persistência de alterações do perfil e respostas HTTP 400 para JSON inválido. O funcionamento no navegador não comprova, por si só, a execução em um dispositivo físico.
