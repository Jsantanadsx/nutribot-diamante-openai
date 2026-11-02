package br.com.fiap.nutribot.tool;

import br.com.fiap.nutribot.dto.SummaryResponse;
import br.com.fiap.nutribot.service.MealService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class DaySummaryTool {
    private final MealService meals;
    public DaySummaryTool(MealService meals) { this.meals = meals; }

    @Tool(description = "Consulta o resumo do diário de HOJE: consumido, meta, calorias "
            + "restantes e macronutrientes. Use para perguntas sobre o consumo do paciente hoje.")
    public SummaryResponse resumoDoDia() {
        return meals.summary(LocalDate.now());
    }
}
