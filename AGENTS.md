# Antigravity Global & Project Coding Rules

This document defines the strict architectural, design, and code quality rules that **Antigravity** must follow for every task, refactor, and feature implementation in this codebase.

---

## 1. Clean Architecture & OOP Principles
* **Architecture:** Follow Clean Architecture strictly. Separate code into `data`, `domain`, and `presentation` packages/modules.
* **Modularization:** Never put all code into a single file. Create single-responsibility files placed in their proper directory structure.
* **OOP & Reusability:** Apply SOLID principles. Write modular, extensible, and reusable components.

---

## 2. Material 3 Theming (Light & Dark Mode)
* **Color Usage:** Always reference colors from `MaterialTheme.colorScheme` (e.g., `primary`, `onSurface`, `surfaceVariant`).
* **Theme Support:** Ensure every UI component automatically adapts to both Light and Dark themes without hardcoded hex colors.

---

## 3. Jetpack Compose Responsive Layouts
* **Window Size Classes:** UI layouts must adapt gracefully across all screen form factors:
  * `Compact` (Phones in portrait)
  * `Medium` (Foldables, small tablets, phones in landscape)
  * `Expanded` (Large tablets, desktop displays)
* **Visual Appeal:** Maintain high aesthetic standards, proper spacing (`dp`), elevation, and responsive grids/layouts.

---

## 4. Reusable & Theme-Aware Dialogs
* **Standardized Dialog Component:** Implement a unified, reusable dialog layout/composable used across the entire application.
* **Theme Awareness:** Dialogs must handle light/dark mode, dynamic dimensions, rounded shapes, and Material 3 elevation consistently.

---

## 5. Comprehensive Code Documentation
* **Method Comments:** Every method and function must include KDoc / doc comments explaining what it does, its parameters, return values, and core logic.
* **Clear Intent:** Code must be self-explanatory, complemented by meaningful inline comments for non-obvious logic.

---

## 6 & 7. Strict DRY Principle (No Code Duplication)
* **Audit Before Writing:** Search for existing utility functions, extensions, or composables before creating new ones.
* **Zero Duplication:** Never duplicate code. Extract shared logic into reusable utility classes, extension functions, or common components.

---

## 8. String Externalization (No Hardcoded Strings)
* **Resource Strings:** Never hardcode user-facing strings in Kotlin/Compose code.
* **Localization:** Always define and reference strings from `res/values/strings.xml` (or string resource providers).

---

## 9. Safe & Conditional Logging
* **Production Safety:** Never output logs in release builds.
* **Logger Abstraction:** Always route logs through a custom `Logger` singleton/object (e.g., Timber wrapper) that automatically suppresses debug logs in release configurations.

---

## 10. Toast Extension / Manager (Auto-Canceling)
* **Toast Singleton / Extension:** Use a custom Toast extension or manager for displaying toasts.
* **Immediate Feedback:** Cancel any actively displaying Toast before presenting a new Toast to prevent queued/delayed UI alerts.

---

## 11. Readability & Small File Footprints
* **Single Responsibility:** Keep files concise and focused on one specific task or component.
* **Clean Organization:** Use proper package structure, concise naming conventions, and clean imports to summarize logic.

---

## 12. Consistent Material 3 UI Styling
* **Uniform UI Tokens:** Cards, OutlinedButtons, Buttons, TextFields, and Surfaces must strictly maintain uniform shapes, borders, paddings, and elevations across all screens.
* **Design System Alignment:** Ensure a cohesive visual design language throughout the entire application.

---

## 13. Dependency Injection & Testability
* **Dependency Injection (DI):** Use a DI framework (e.g., Hilt / Koin) for all dependencies, ViewModels, repositories, and use cases.
* **Decoupling:** Keep components loosely coupled and fully unit-testable. Zero tight coupling or static global states.

---

## 14. Execution & Build Control
* **No Automatic Builds or Execution:** Never trigger Gradle builds, build Debug APKs, run compilation commands, or execute code automatically after making changes unless explicitly requested by the user.

---

## 15. Static Data & Provider Package Separation
* **No Static Lists in Composables:** Never define or instantiate static data lists or constant data structures directly inside Composable functions or UI components.
* **Provider Package Architecture:** Move all static data providers, list generators, and menu datasets into dedicated provider classes or objects within a `provider` package.
* **No Hardcoded Strings in Static Data:** Ensure all text entries within static lists reference string resources (`@StringRes` IDs or `strings.xml` references) rather than hardcoded string literals.

---

## 16. Drawable Icon Naming Convention
* **Icon Prefixing:** All icon resources inside `drawable` must start with the `ic_` prefix (e.g., `ic_search.xml`, `ic_settings.png`).

---

## 17. Import Placement & File Structure
* **Top-Level Imports:** All `import` statements must strictly be placed at the top of the file immediately following the `package` declaration.
* **No Mid-File Imports:** Never place `import` statements inline, inside, or directly above functions, methods, or classes.



