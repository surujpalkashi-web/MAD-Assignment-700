# Smart Pantry Manager - MAD 700 Assignment

Java Android application that suggests recipes based strictly on leftover ingredients to cut food waste.

## Database
SQLite (local) using SQLiteOpenHelper + DataSource pattern (as taught in the module).

## Theme
Professional blue colour scheme.

## Structure
- `db/` - DatabaseHelper and PantryDataSource
- `model/` - PantryItem, Recipe, RecipeIngredient, CalorieEntry
- Activities for pantry CRUD, suggested recipes, recipe detail, settings, and calorie tracker

## Setup
1. Open in Android Studio
2. Sync Gradle
3. Run on emulator or device (minSdk 24)
