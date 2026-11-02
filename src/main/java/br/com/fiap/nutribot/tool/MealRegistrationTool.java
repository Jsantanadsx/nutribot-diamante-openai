package br.com.fiap.nutribot.tool;

import br.com.fiap.nutribot.dto.MealEntryResponse;
import br.com.fiap.nutribot.model.MealType;
import br.com.fiap.nutribot.service.MealService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class MealRegistrationTool {
    private final MealService meals;
    public MealRegistrationTool(MealService meals) { this.meals = meals; }

    @Tool(description = "GRAVA uma refeição do usuário no diário alimentar. Execute somente "
            + "quando o usuário informar que comeu algo ou pedir explicitamente para registrar. "
            + "Para 2 ovos médios, use aproximadamente 100 g e avise que é estimativa. "
            + "Categorias: BREAKFAST, LUNCH, DINNER, SNACK. Não invente alimentos.")
    public MealEntryResponse registrarRefeicao(
            @ToolParam(description = "Nome do alimento cadastrado (por exemplo, ovo)") String alimento,
            @ToolParam(description = "Quantidade do alimento em gramas, estritamente positiva") double gramas,
            @ToolParam(description = "Tipo de refeição: BREAKFAST, LUNCH, DINNER ou SNACK") MealType tipoRefeicao) {
        return MealEntryResponse.from(meals.log(alimento, gramas, tipoRefeicao));
    }
}
