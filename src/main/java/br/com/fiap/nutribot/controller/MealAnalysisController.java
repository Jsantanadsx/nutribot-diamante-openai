package br.com.fiap.nutribot.controller;

import br.com.fiap.nutribot.dto.MealAnalysisRequest;
import br.com.fiap.nutribot.dto.MealAnalysisResponse;
import jakarta.validation.Valid;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MealAnalysisController {

    // Cliente distinto: análise isolada, sem as tools de registro e sem memória.
    private final ChatClient analysisClient;

    public MealAnalysisController(ChatClient.Builder builder) {
        this.analysisClient = builder
                .defaultSystem("""
                        Você estima o valor nutricional APROXIMADO de refeições a partir da descrição.
                        Responda estritamente no esquema solicitado pelo structured output.
                        Inclua cada alimento em items (food, grams, calories) e totais em
                        totalCalories, protein, carbs e fat. Todos os valores numéricos devem ser números.
                        Valores de proteína, carboidratos e gordura são em gramas.
                        Se a porção for ambígua, use estimativas plausíveis, sem prometer precisão clínica.
                        Nunca registre ou modifique refeições do diário alimentar.
                        """)
                .build();
    }

    @PostMapping("/meals/analyze")
    public ResponseEntity<MealAnalysisResponse> analyze(@Valid @RequestBody MealAnalysisRequest request) {
        MealAnalysisResponse result = analysisClient.prompt()
                .user("Analise esta refeição: " + request.description().trim())
                .call()
                .entity(MealAnalysisResponse.class);
        if (result == null) {
            throw new IllegalStateException("Não foi possível analisar a refeição");
        }
        return ResponseEntity.ok(result);
    }
}
