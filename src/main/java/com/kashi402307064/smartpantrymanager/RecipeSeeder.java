package com.kashi402307064.smartpantrymanager;

import com.kashi402307064.smartpantrymanager.db.PantryDataSource;
import com.kashi402307064.smartpantrymanager.model.Recipe;
import com.kashi402307064.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Loads a fixed set of recipes into SQLite the first time the app runs.
 * Only seeds when the recipes table is empty.
 */
public class RecipeSeeder {

    private final PantryDataSource dataSource;

    public RecipeSeeder(PantryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void seedIfNeeded() {
        if (dataSource.getRecipeCount() > 0) {
            return;
        }
        for (Recipe recipe : buildRecipes()) {
            dataSource.insertRecipe(recipe);
        }
    }

    private List<Recipe> buildRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        recipes.add(recipe("Tomato Egg Stir-Fry",
                ingredients(
                        ing("egg", 2, "pieces"),
                        ing("tomato", 2, "pieces"),
                        ing("salt", 1, "pinch"),
                        ing("oil", 1, "tbsp")
                ),
                steps(
                        "Beat the eggs with a pinch of salt.",
                        "Heat oil in a pan and scramble the eggs, then set aside.",
                        "Fry chopped tomatoes until soft.",
                        "Return eggs to the pan, mix and serve."
                )));

        recipes.add(recipe("Cheese Omelette",
                ingredients(
                        ing("egg", 3, "pieces"),
                        ing("cheese", 50, "g"),
                        ing("butter", 1, "tbsp"),
                        ing("salt", 1, "pinch")
                ),
                steps(
                        "Beat eggs with salt.",
                        "Melt butter in a pan.",
                        "Pour in eggs and cook until almost set.",
                        "Add grated cheese, fold and serve."
                )));

        recipes.add(recipe("Garlic Butter Pasta",
                ingredients(
                        ing("pasta", 200, "g"),
                        ing("garlic", 3, "cloves"),
                        ing("butter", 2, "tbsp"),
                        ing("salt", 1, "tsp")
                ),
                steps(
                        "Boil pasta in salted water until al dente.",
                        "Melt butter and cook minced garlic until fragrant.",
                        "Toss drained pasta in the garlic butter."
                )));

        recipes.add(recipe("Simple Fried Rice",
                ingredients(
                        ing("rice", 2, "cups"),
                        ing("egg", 2, "pieces"),
                        ing("oil", 2, "tbsp"),
                        ing("soy sauce", 2, "tbsp"),
                        ing("onion", 1, "piece")
                ),
                steps(
                        "Cook rice and let it cool slightly.",
                        "Scramble eggs in oil and set aside.",
                        "Fry chopped onion, add rice and soy sauce.",
                        "Mix in the eggs and serve."
                )));

        recipes.add(recipe("Banana Smoothie",
                ingredients(
                        ing("banana", 2, "pieces"),
                        ing("milk", 250, "ml"),
                        ing("honey", 1, "tbsp")
                ),
                steps(
                        "Peel and slice the bananas.",
                        "Blend with milk and honey until smooth.",
                        "Pour into a glass and drink cold."
                )));

        recipes.add(recipe("Avocado Toast",
                ingredients(
                        ing("bread", 2, "slices"),
                        ing("avocado", 1, "piece"),
                        ing("salt", 1, "pinch"),
                        ing("lemon", 0.5, "piece")
                ),
                steps(
                        "Toast the bread.",
                        "Mash avocado with salt and a squeeze of lemon.",
                        "Spread on toast and serve."
                )));

        recipes.add(recipe("Vegetable Soup",
                ingredients(
                        ing("carrot", 2, "pieces"),
                        ing("potato", 2, "pieces"),
                        ing("onion", 1, "piece"),
                        ing("salt", 1, "tsp"),
                        ing("water", 1, "litre")
                ),
                steps(
                        "Chop all vegetables.",
                        "Simmer in water with salt until soft.",
                        "Blend if desired, or serve chunky."
                )));

        recipes.add(recipe("Grilled Cheese Sandwich",
                ingredients(
                        ing("bread", 2, "slices"),
                        ing("cheese", 60, "g"),
                        ing("butter", 1, "tbsp")
                ),
                steps(
                        "Butter one side of each bread slice.",
                        "Place cheese between the unbuttered sides.",
                        "Grill in a pan until golden and cheese melts."
                )));

        recipes.add(recipe("Chicken Stir-Fry",
                ingredients(
                        ing("chicken", 300, "g"),
                        ing("soy sauce", 2, "tbsp"),
                        ing("garlic", 2, "cloves"),
                        ing("oil", 2, "tbsp"),
                        ing("onion", 1, "piece")
                ),
                steps(
                        "Slice chicken and onion.",
                        "Stir-fry garlic in oil, then add chicken.",
                        "Add onion and soy sauce, cook until done."
                )));

        recipes.add(recipe("Pancakes",
                ingredients(
                        ing("flour", 150, "g"),
                        ing("egg", 1, "piece"),
                        ing("milk", 200, "ml"),
                        ing("sugar", 2, "tbsp"),
                        ing("butter", 1, "tbsp")
                ),
                steps(
                        "Mix flour, egg, milk and sugar into a batter.",
                        "Cook spoonfuls in a buttered pan until golden on both sides."
                )));

        recipes.add(recipe("Tuna Salad",
                ingredients(
                        ing("tuna", 1, "can"),
                        ing("mayonnaise", 2, "tbsp"),
                        ing("onion", 0.5, "piece"),
                        ing("salt", 1, "pinch")
                ),
                steps(
                        "Drain the tuna.",
                        "Mix with mayonnaise, chopped onion and salt.",
                        "Serve on bread or with salad leaves."
                )));

        recipes.add(recipe("Mashed Potatoes",
                ingredients(
                        ing("potato", 4, "pieces"),
                        ing("butter", 2, "tbsp"),
                        ing("milk", 100, "ml"),
                        ing("salt", 1, "tsp")
                ),
                steps(
                        "Boil potatoes until soft.",
                        "Mash with butter, milk and salt until smooth."
                )));

        recipes.add(recipe("Tomato Pasta",
                ingredients(
                        ing("pasta", 200, "g"),
                        ing("tomato", 3, "pieces"),
                        ing("garlic", 2, "cloves"),
                        ing("oil", 2, "tbsp"),
                        ing("salt", 1, "tsp")
                ),
                steps(
                        "Cook pasta in salted water.",
                        "Fry garlic in oil, add chopped tomatoes.",
                        "Simmer into a sauce and toss with pasta."
                )));

        recipes.add(recipe("Egg Fried Noodles",
                ingredients(
                        ing("noodles", 200, "g"),
                        ing("egg", 2, "pieces"),
                        ing("soy sauce", 2, "tbsp"),
                        ing("oil", 2, "tbsp"),
                        ing("onion", 1, "piece")
                ),
                steps(
                        "Cook noodles and drain.",
                        "Scramble eggs, fry onion, then add noodles and soy sauce."
                )));

        recipes.add(recipe("Greek Yogurt Bowl",
                ingredients(
                        ing("yogurt", 200, "g"),
                        ing("honey", 1, "tbsp"),
                        ing("banana", 1, "piece")
                ),
                steps(
                        "Spoon yogurt into a bowl.",
                        "Top with sliced banana and drizzle with honey."
                )));

        recipes.add(recipe("Lentil Curry",
                ingredients(
                        ing("lentils", 200, "g"),
                        ing("onion", 1, "piece"),
                        ing("tomato", 2, "pieces"),
                        ing("garlic", 2, "cloves"),
                        ing("oil", 2, "tbsp"),
                        ing("salt", 1, "tsp")
                ),
                steps(
                        "Cook lentils until soft.",
                        "Fry onion and garlic, add tomatoes.",
                        "Mix with lentils, season and simmer."
                )));

        recipes.add(recipe("Cheese Quesadilla",
                ingredients(
                        ing("tortilla", 2, "pieces"),
                        ing("cheese", 80, "g"),
                        ing("oil", 1, "tbsp")
                ),
                steps(
                        "Place cheese between two tortillas.",
                        "Cook in a lightly oiled pan until cheese melts and outsides are crisp."
                )));

        recipes.add(recipe("Boiled Egg Breakfast",
                ingredients(
                        ing("egg", 2, "pieces"),
                        ing("bread", 2, "slices"),
                        ing("butter", 1, "tsp"),
                        ing("salt", 1, "pinch")
                ),
                steps(
                        "Boil eggs for 7 to 9 minutes.",
                        "Toast and butter the bread.",
                        "Peel eggs, season with salt and serve with toast."
                )));

        recipes.add(recipe("Cucumber Salad",
                ingredients(
                        ing("cucumber", 1, "piece"),
                        ing("salt", 1, "pinch"),
                        ing("lemon", 0.5, "piece"),
                        ing("oil", 1, "tbsp")
                ),
                steps(
                        "Slice the cucumber thinly.",
                        "Toss with salt, lemon juice and oil.",
                        "Chill briefly before serving."
                )));

        recipes.add(recipe("Peanut Butter Banana Toast",
                ingredients(
                        ing("bread", 2, "slices"),
                        ing("peanut butter", 2, "tbsp"),
                        ing("banana", 1, "piece")
                ),
                steps(
                        "Toast the bread.",
                        "Spread peanut butter on each slice.",
                        "Top with sliced banana."
                )));

        return recipes;
    }

    private static Recipe recipe(String name, List<RecipeIngredient> ingredients, List<String> steps) {
        return new Recipe(name, ingredients, steps);
    }

    private static List<RecipeIngredient> ingredients(RecipeIngredient... items) {
        return new ArrayList<>(Arrays.asList(items));
    }

    private static RecipeIngredient ing(String name, double quantity, String unit) {
        String normalized = name.toLowerCase().trim();
        return new RecipeIngredient(name, normalized, quantity, unit);
    }

    private static List<String> steps(String... lines) {
        return new ArrayList<>(Arrays.asList(lines));
    }
}
