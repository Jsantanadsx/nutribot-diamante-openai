package br.com.fiap.nutribot.tool;

import br.com.fiap.nutribot.dto.SummaryResponse;
import br.com.fiap.nutribot.model.Food;
import br.com.fiap.nutribot.model.MealEntry;
import br.com.fiap.nutribot.model.MealType;
import br.com.fiap.nutribot.service.FoodService;
import br.com.fiap.nutribot.service.MealService;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class NutriBotToolsTest {
    private final FoodService foods = mock(FoodService.class);
    private final MealService meals = mock(MealService.class);

    @Test void buscarAlimentoUsaDadosDaBase() {
        Food food = Food.builder().name("Tapioca").category("Cereais")
                .calories(240).protein(0).carbs(60).fat(0).build();
        when(foods.findByName("tapioca")).thenReturn(food);
        var info = new FoodLookupTool(foods).buscarAlimento("tapioca");
        assertEquals(240, info.caloriesPer100g());
        verify(foods).findByName("tapioca");
    }

    @Test void resumoUsaMealService() {
        LocalDate today = LocalDate.now();
        when(meals.summary(today)).thenReturn(new SummaryResponse(
                today, 500, 20, 60, 12, 2000, 1500, List.of()));
        assertEquals(1500, new DaySummaryTool(meals).resumoDoDia().remainingCalories());
        verify(meals).summary(today);
    }

    @Test void registroRealmenteUsaServicoDePersistencia() {
        Food food = Food.builder().id(10L).name("Tapioca").calories(240).build();
        MealEntry entry = MealEntry.builder().id(25L).date(LocalDate.now())
                .mealType(MealType.BREAKFAST).food(food).grams(100).build();
        when(meals.log("tapioca", 100, MealType.BREAKFAST)).thenReturn(entry);
        var result = new MealRegistrationTool(meals)
                .registrarRefeicao("tapioca", 100, MealType.BREAKFAST);
        assertEquals(25L, result.id());
        assertEquals(240, result.calories());
        verify(meals).log("tapioca", 100, MealType.BREAKFAST);
    }
}
