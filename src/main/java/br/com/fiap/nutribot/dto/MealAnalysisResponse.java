package br.com.fiap.nutribot.dto;

import java.util.List;

/** Formato exato requerido pelo app: números são tipos numéricos, não String. */
public record MealAnalysisResponse(
        List<FoodEstimate> items,
        double totalCalories,
        double protein,
        double carbs,
        double fat
) {
    public record FoodEstimate(String food, double grams, double calories) {}
}
