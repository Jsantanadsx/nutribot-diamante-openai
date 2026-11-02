package br.com.fiap.nutribot.tool;

import br.com.fiap.nutribot.model.Food;
import br.com.fiap.nutribot.service.FoodService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class FoodLookupTool {
    private final FoodService foods;

    public FoodLookupTool(FoodService foods) { this.foods = foods; }

    public record FoodInformation(String name, String category, double caloriesPer100g,
                                  double proteinPer100g, double carbsPer100g, double fatPer100g) {}

    @Tool(description = "Busca na tabela oficial da clínica um alimento pelo nome. "
            + "Retorna energia e macronutrientes por 100 gramas. Use para perguntas sobre alimentos.")
    public FoodInformation buscarAlimento(
            @ToolParam(description = "Nome do alimento, por exemplo tapioca ou ovo") String nome) {
        Food food = foods.findByName(nome);
        return new FoodInformation(food.getName(), food.getCategory(), food.getCalories(),
                food.getProtein(), food.getCarbs(), food.getFat());
    }
}
