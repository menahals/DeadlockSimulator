# Deadlock Simulator (JavaFX)

A desktop GUI that visualizes a deadlock scenario and walks through three
resolution/prevention techniques:

1. **Deadlock Scenario** — a resource allocation graph (P1, P2, P3, P4 vs
   R1, R2, R3) with a circular wait highlighted in red, an allocation
   table, and the four necessary conditions for deadlock.
2. **Dynamic Priority Allocation** — step through priority assignment,
   aging on contention, and scheduling by highest current priority.
3. **Victim Selection** — weighted cost-to-abort scoring across the
   deadlocked processes, then an animated resolution log after you click
   "Run victim selection."
4. **Wait-Die Scheme** — an interactive walkthrough of the WAIT vs DIE
   rule across three timestamp-based request scenarios.

Use the four buttons in the header to switch screens.

## Requirements

- JDK 17 or newer
- JavaFX SDK 21 (the project pulls JavaFX via Maven, so you don't need to
  download the SDK separately if you use Maven)
- Maven 3.6+

## Project layout

```
deadlock-sim/
  pom.xml
  src/main/java/com/deadlocksim/
    DeadlockSimulatorApp.java     - entry point, header nav, scene swap
    DeadlockScenario.java         - shared data model (processes/resources)
    DeadlockScenePane.java        - Screen 1: resource allocation graph
    PriorityAllocationPane.java   - Screen 2: dynamic priority allocation
    VictimSelectionPane.java      - Screen 3: victim selection
    WaitDiePane.java              - Screen 4: wait-die scheme
    UiKit.java                    - shared styled UI helpers (cards, badges, etc.)
  src/main/resources/
    app.css                       - optional small stylesheet refinements
```

## Run with Maven (recommended)

From the `deadlock-sim` directory:

```bash
mvn javafx:run
```

This downloads the correct JavaFX modules for your OS automatically and
launches the app — no manual SDK setup needed.

## Run without Maven (manual JavaFX SDK)

If you'd rather not use Maven:

1. Download the JavaFX SDK 21 for your OS from https://openjfx.io
2. Compile:
   ```bash
   javac --module-path /path/to/javafx-sdk-21/lib --add-modules javafx.controls \
     -d out $(find src/main/java -name "*.java")
   ```
3. Run:
   ```bash
   java --module-path /path/to/javafx-sdk-21/lib --add-modules javafx.controls \
     -cp out com.deadlocksim.DeadlockSimulatorApp
   ```

## Notes

- The app is a single fixed scenario by design (P1→R2→P2→R3→P3→R1→P1
  cycle, plus P4 blocked outside it) so all three techniques operate on
  the same data and are easy to compare directly.
- Dynamic Priority Allocation and Victim Selection are **detection and
  recovery** techniques (the deadlock already happened; the system picks
  who proceeds / who gets aborted). Wait-Die is a **prevention**
  technique (the circular wait is never allowed to form in the first
  place) — the UI calls this distinction out on the Wait-Die screen.
- No external runtime dependencies beyond JavaFX itself — no database,
  no network calls.
