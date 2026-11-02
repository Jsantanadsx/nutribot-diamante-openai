package br.com.fiap.nutribot.controller;

import br.com.fiap.nutribot.dto.ChatRequest;
import br.com.fiap.nutribot.dto.ChatResponse;
import br.com.fiap.nutribot.service.ChatIntentRouter;
import br.com.fiap.nutribot.tool.DaySummaryTool;
import br.com.fiap.nutribot.tool.FoodLookupTool;
import br.com.fiap.nutribot.tool.MealRegistrationTool;
import jakarta.validation.Valid;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
    private final ChatClient chatClient;
    private final ChatIntentRouter router;
    private final QuestionAnswerAdvisor ragAdvisor;
    private final FoodLookupTool foodTool;
    private final DaySummaryTool summaryTool;
    private final MealRegistrationTool registrationTool;

    public ChatController(ChatClient nutriBotChatClient, ChatIntentRouter router,
                          QuestionAnswerAdvisor clinicQuestionAnswerAdvisor,
                          FoodLookupTool foodTool, DaySummaryTool summaryTool,
                          MealRegistrationTool registrationTool) {
        this.chatClient = nutriBotChatClient;
        this.router = router;
        this.ragAdvisor = clinicQuestionAnswerAdvisor;
        this.foodTool = foodTool;
        this.summaryTool = summaryTool;
        this.registrationTool = registrationTool;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        String message = request.message().trim();
        String conversationId = request.conversationId().trim();

        ChatClient.ChatClientRequestSpec prompt = chatClient.prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId));

        switch (router.route(message)) {
            case MEAL_REGISTRATION -> prompt.tools(registrationTool);
            case DAY_SUMMARY -> prompt.tools(summaryTool);
            case FOOD_LOOKUP -> prompt.tools(foodTool);
            case CLINIC_RAG -> prompt.advisors(ragAdvisor);
            case GENERAL -> { /* Memória, mas sem tools nem RAG. */ }
        }

        String response = prompt.call().content();
        return ResponseEntity.ok(new ChatResponse(response == null ? "" : response));
    }
}
