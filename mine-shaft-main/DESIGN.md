# Get in the Mine Shaft — Software Design Document

## 1. Overview

**Get in the Mine Shaft** is an Android productivity app built with **Kotlin** and **Jetpack Compose**. It combines a task system (with recurrence, reminders, and completion tracking), statistics dashboards with custom Canvas-based charts, calendar integration, and an avatar/gamification screen, using a modern Android stack: Compose UI, Room, Hilt, ViewModels, and WorkManager.

| Attribute | Value |
|-----------|--------|
| **Project type** | Native Android application |
| **Language** | Kotlin |
| **UI** | Jetpack Compose |
| **Navigation** | Compose Navigation (NavHost, bottom bar) |
| **State** | ViewModels + Flow |
| **Persistence** | Room (SQLite) |
| **DI** | Hilt |
| **Background** | WorkManager (e.g. reminders) |

---

## 2. Goals and Scope

- **Primary goal**: Provide a task-management and productivity app with filtering, recurrence, reminders, statistics visualization, and a light gamification (avatar) experience.
- **Scope**: Task CRUD, task list with filters, add/edit task flows, calendar view, statistics screen (tasks completed, completion rate, streaks, habits), avatar screen. Local-only data.
- **Out of scope**: Cloud sync, multi-device, user accounts, server backend.

---

## 3. System Context

- **Users**: Single device user (no login).
- **External systems**: Calendar (READ_CALENDAR / WRITE_CALENDAR), notifications (POST_NOTIFICATIONS), exact alarm (SCHEDULE_EXACT_ALARM) for reminders.
- **Deployment**: Android devices/emulators; built with Gradle/AGP.

---

## 4. Architecture

### 4.1 Layered Architecture

- **UI layer**: Compose screens in `ui/screens/`, reusable components in `ui/components/`, theme in `ui/theme/`. Single Activity; navigation via `AppNavigation` and `Screen` sealed type.
- **State / presentation**: ViewModels in `ui/viewmodels/`; Hilt-injected; expose state via `StateFlow`/`Flow` and handle user actions.
- **Domain / data layer**: Repositories in `data/` (`TaskRepository`, `AvatarRepository`) wrap DAOs and expose suspend/Flow APIs. No separate domain use-case classes.
- **Persistence**: Room database (`AppDatabase`), entities (`Task`, `Avatar`), DAOs (`TaskDao`, `AvatarDao`). Type converters for `Date`, `Set<DayOfWeek>` etc. in `Converters`.
- **DI**: Hilt modules (`di/AppModule`, `di/WorkerModule`) provide database, DAOs, repositories; ViewModels and Workers are injected.
- **Workers**: WorkManager (e.g. `ReminderWorker`) for scheduled reminders.

### 4.2 Navigation Structure

- **Start**: Home (`Screen.Home`).
- **Tabs / main destinations**: Home, Task List, Calendar, Avatar, Statistics (bottom bar).
- **Nested**: Add Task, Edit Task (with `taskId` argument). Back stack: pop after add/edit.

Routes are defined in `navigation/Screen.kt` and composed in `navigation/AppNavigation.kt` with `NavHost` and `composable()`.

### 4.3 Data Model (Core)

- **Task**: `id`, `title`, `description`, `dueDate`, `priority`, `isCompleted`, `category`, `reminderTime`, `createdAt`, `updatedAt`, `frequency`, `scheduledDays`, `streak`, `lastCompletedDate`. Stored in table `tasks`.
- **Avatar**: Separate entity/table for gamification state (e.g. tied to completion rate).
- **Enums**: `Priority`, `TaskFrequency`; persistence via Room type converters where needed.

---

## 5. Key Design Decisions

| Decision | Rationale |
|----------|------------|
| Compose-first UI | Modern declarative UI; single toolkit for all screens. |
| Hilt for DI | Clear dependency graph; testability; standard Android approach. |
| Repository over DAO | Single place for data access; can add caching or offline logic later. |
| Flow from Repository/DAO | Reactive UI updates when data changes. |
| Custom Canvas charts | No external chart library; full control over axes, grid, labels (Tasks Completed, Completion Rate, Streaks, Habits). |
| Room with fallbackToDestructiveMigration | Simplifies early development; migrations can be added later. |
| Seed data in DB callback | First launch has sample tasks for demo/UX. |

---

## 6. Component Overview

| Component | Responsibility |
|-----------|----------------|
| `MainActivity` | Hosts Compose; sets content to `AppNavigation` + bottom bar. |
| `AppNavigation` | Defines `NavHost` and all `Screen` composables. |
| `*ViewModel` | Loads data via repositories, handles events, exposes UI state. |
| `TaskRepository` / `AvatarRepository` | CRUD and Flow streams; used by ViewModels. |
| `AppDatabase` | Room DB; provides `TaskDao`, `AvatarDao`. |
| `ReminderWorker` | WorkManager worker for reminder notifications. |

---

## 7. Permissions and Configuration

- **AndroidManifest**: `POST_NOTIFICATIONS`, `READ_CALENDAR`, `WRITE_CALENDAR`, `SCHEDULE_EXACT_ALARM`.
- **Build**: Gradle; KSP for Room; Compose BOM; Hilt.

---

## 8. Diagram Reference

- **How the project works**: See `Mine-Shaft-Architecture.puml` (PlantUML). The diagram is a **sequence diagram** showing: app launch (UI ← ViewModel ← Repository ← DAO ← Room), adding a task (user → Screen → ViewModel → Repository → DAO → DB, then list updates via Flow), viewing statistics, and background reminders. Use a PlantUML renderer to generate the image.
