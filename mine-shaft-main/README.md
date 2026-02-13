# Get in the Mine Shaft (Android / Kotlin)

An Android productivity app built with **Kotlin** + **Jetpack Compose**. The app combines a task system (with recurrence fields), stats dashboards, and a multi-screen navigation structure to demonstrate a modern Android architecture.

## Why this project (portfolio framing)

This project demonstrates:

- **Compose-first UI** + navigation architecture
- **Local persistence** via Room with a real data model (`Task`)
- **Dependency injection** with Hilt (repositories, DAOs, workers)
- Practical state management via **ViewModels** + `Flow`

## Key contributions (what I built)

- Implemented a multi-screen **Compose navigation** flow with a persistent bottom bar
- Built a real **Room data model** for tasks (including recurrence/reminders fields)
- Wired up **Hilt DI** for database, DAOs, repositories, and ViewModels
- Added **stats + visualization** support with custom Canvas-based charts featuring:
  - X and Y axes with labeled scales
  - Grid lines for better readability
  - Dynamic data range calculation (tasks completed: 0-N, completion rate: 0-100%)
  - Historical data visualization spanning from first to last task completion

## Features (high level)

- **Task list** with filtering and completion state
- **Add/Edit task** flows
- **Calendar** view (permissions declared for calendar access)
- **Statistics** screen with interactive charts showing:
  - Tasks Completed: Daily task completion counts (0, 1, 2, etc.) over time
  - Completion Rate: Cumulative percentage (0-100%) showing progress over time
  - Current/Longest Streak: Streak progression over the last 7 days
  - Active Habits: Number of recurring tasks
  - All charts feature X and Y axes with proper labels and grid lines
- **Avatar / gamification**-style screen (ties into completion rate)

> The UI is composed of multiple screens routed via Compose Navigation.

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Navigation**: `androidx.navigation:navigation-compose`
- **DI**: Hilt
- **Persistence**: Room (KSP compiler)
- **Background work**: WorkManager
- **Charts**: Custom Canvas-based line charts with axis rendering (no external chart library)
- **Build**: Gradle / Android Gradle Plugin

## Permissions

Declared in `app/src/main/AndroidManifest.xml`:

- `POST_NOTIFICATIONS`
- `READ_CALENDAR` / `WRITE_CALENDAR`
- `SCHEDULE_EXACT_ALARM`

## Architecture (how the app is structured)

- **UI**: Compose screens in `ui/screens/`
- **Navigation**: `navigation/AppNavigation.kt` defines routes and `NavHost`
- **State**: `ui/viewmodels/*ViewModel.kt` (Hilt injected) expose `Flow`-based state
- **Data**:
  - Room entities like `data/Task.kt` (`@Entity(tableName = "tasks")`)
  - `data/TaskRepository.kt` wraps DAO access and provides `Flow` streams + suspend CRUD
- **DI**:
  - `di/AppModule.kt` provides DB/DAOs/repositories and seeds the DB with sample tasks on first run

## Data Model (Task)

The `Task` entity includes:

- core: title, description, due date, priority, completed
- metadata: created/updated timestamps, category
- reminders: reminder time
- recurrence: frequency, scheduled days, streak, last completed date

## Run (Android Studio)

1. Open Android Studio → **Open** → select the `get_in_the_mine_shaft` folder
2. Let Gradle sync
3. Run on an emulator or device

## Build from the command line (Windows)

From the project root:

```bat
gradlew.bat assembleDebug
```

Useful tasks:

```bat
gradlew.bat test
gradlew.bat lint
```

## Handy scripts in this repo

- `check_java.bat` — checks your Java setup
- `use_java11.bat` — helps switch to Java 11 (if needed)
- `fix_and_build.bat` / `compile_only.bat` / `reset_gradle.bat` — build helpers

## Project Structure (high level)

- `app/src/main/` — app source
- `app/schemas/` — Room schema exports
- `gradle/wrapper/` — Gradle wrapper

## Demo Script (quick walkthrough)

If you’re demoing this live:

1. Open the app → show navigation tabs (bottom bar)
2. Go to Task List → toggle completion state, change filters
3. Add a task → set priority/due date/reminder
4. Open Statistics → show the interactive charts with axes:
   - Tasks Completed chart shows daily completion counts (Y-axis: 0, 1, 2...)
   - Completion Rate chart shows cumulative percentage (Y-axis: 0-100%)
   - X-axis shows days from first to last task completion
   - Explain how the data spans the full history of task completions
5. Open Avatar → show how completion rate affects the avatar experience

## Limitations / Improvements (portfolio honesty)

- Expand test coverage (unit tests + UI tests)
- Improve theming consistency (Material 3 migration)
- Background reminders/notifications polish (exact alarm + permission flows)
- Add chart interactivity (tap to see exact values, zoom/pan for longer time periods)
- Consider adding export functionality for statistics data

## Screenshots

Add screenshots/GIFs to: `docs/screenshots/`

- `docs/screenshots/task_list.png`
- `docs/screenshots/add_task.png`
- `docs/screenshots/statistics.png`
- `docs/screenshots/avatar.png`
