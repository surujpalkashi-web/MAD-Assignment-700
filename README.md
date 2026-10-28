# Smart Pantry Manager

**Student:** Kashi Surujpal  
**Student number:** 402307064  
**Module:** Mobile App Development 700

Java Android app that tracks pantry ingredients and suggests recipes only when every required ingredient is already available (strict matching).

## Database

SQLite via `SQLiteOpenHelper` and `PantryDataSource` (local on-device). Chosen because it matches the module content, works offline, and supports full CRUD with data that persists after the app is closed.

Tables: `pantry`, `recipes`, `recipe_ingredients`, `calories`.

## Features

- Pantry CRUD (RecyclerView)
- Seeded recipe set (20 recipes)
- Strict recipe matching + optional Almost There list
- Recipe detail screen
- Calorie tracker
- Settings (expiry highlight, sort, clear pantry)
- Bottom navigation

## How to run

1. Clone this repository
2. Open the project in Android Studio (Giraffe or newer recommended)
3. Sync Gradle
4. Run on an emulator or device (API 24+)

Main branch for development work: `feature/recipe-matching`

## GitHub

https://github.com/surujpalkashi-web/MAD-Assignment-700
