package com.deadlocksim;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

import static com.deadlocksim.UiKit.*;

/**
 * Screen 2 — Dynamic Priority Allocation.
 *
 * Each process gets a base priority. Because aging is applied (longer wait
 * = bigger boost), priorities change dynamically as time passes, and the
 * scheduler grants the resource to whichever process currently has the
 * highest score. This breaks the deadlock because the cycle is unwound in
 * priority order instead of strict hold/request order.
 */
public class PriorityAllocationPane extends VBox {

    private final DeadlockScenario scenario;
    private int step = 0;
    private static final int MAX_STEP = 2;

    private final VBox dynamicArea = new VBox(14);

    public PriorityAllocationPane(DeadlockScenario scenario) {
        this.scenario = scenario;
        setSpacing(16);
        setMaxWidth(980);

        getChildren().add(h2("Dynamic Priority Allocation"));
        getChildren().add(body("Processes are assigned priority scores that change over time. " +
                "A process that has waited longer gets an aging bonus, preventing starvation and " +
                "letting the scheduler break the deadlock by always granting resources to the " +
                "highest current priority."));

        getChildren().add(dynamicArea);
        refresh();
    }

    public void refresh() {
        step = 0;
        render();
    }

    private void render() {
        dynamicArea.getChildren().clear();

        String[] titles = {
                "Step 1 — Assign dynamic priorities",
                "Step 2 — Apply aging on contention",
                "Step 3 — Schedule highest priority"
        };
        String[] descs = {
                "Each process starts with a base priority influenced by resource demand and estimated remaining work.",
                "Processes waiting the longest receive an aging bonus added to their base priority, so no process starves indefinitely.",
                "The scheduler grants the contended resource to the highest-scoring process; lower scoring processes are temporarily suspended, which breaks the cycle."
        };

        dynamicArea.getChildren().add(banner(titles[step] + "  —  " + descs[step], COLOR_WARN, COLOR_WARN_BG));
        dynamicArea.getChildren().add(buildPriorityTable());

        if (step == MAX_STEP) {
            dynamicArea.getChildren().add(banner(
                    "✓ Deadlock broken — P4 (priority 8) runs first and never touches the cycle, then P1 (priority 7, boosted) proceeds, releasing the rest of the chain.",
                    COLOR_SUCCESS, COLOR_SUCCESS_BG));
        }

        HBox nav = new HBox(10);
        Button prev = new Button("← Previous step");
        Button next = new Button(step == MAX_STEP ? "Restart" : "Next step →");
        styleNavButton(prev, false);
        styleNavButton(next, true);
        prev.setDisable(step == 0);

        prev.setOnAction(e -> { step = Math.max(0, step - 1); render(); });
        next.setOnAction(e -> {
            if (step == MAX_STEP) step = 0;
            else step = Math.min(MAX_STEP, step + 1);
            render();
        });

        HBox.setHgrow(prev, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(next, javafx.scene.layout.Priority.ALWAYS);
        prev.setMaxWidth(Double.MAX_VALUE);
        next.setMaxWidth(Double.MAX_VALUE);
        nav.getChildren().addAll(prev, next);
        dynamicArea.getChildren().add(nav);
    }

    private VBox buildPriorityTable() {
        VBox card = card("Priority table (live scores)");

        GridPane grid = new GridPane();
        grid.setHgap(18);
        grid.setVgap(8);

        String[] headers = {"Process", "Base priority", "Age bonus", "Final score", "Decision"};
        for (int i = 0; i < headers.length; i++) {
            Label h = new Label(headers[i]);
            h.setStyle("-fx-text-fill: " + COLOR_MUTED + "; -fx-font-size: 11px; -fx-font-weight: bold;");
            grid.add(h, i, 0);
        }

        // waitMs, ageBonus per process for the demo
        List<int[]> waitData = new ArrayList<>(); // {waitMs, ageBonus}
        waitData.add(new int[]{40, 2}); // P1
        waitData.add(new int[]{20, 1}); // P2
        waitData.add(new int[]{25, 1}); // P3
        waitData.add(new int[]{0, 0});  // P4

        int row = 1;
        int idx = 0;
        for (DeadlockScenario.Proc p : scenario.getProcessList()) {
            int waitMs = waitData.get(idx)[0];
            int ageBonus = waitData.get(idx)[1];
            int finalScore = p.basePriority + ageBonus;

            grid.add(mono(p.id), 0, row);
            grid.add(mono(String.valueOf(p.basePriority)), 1, row);
            grid.add(mono(ageBonus > 0 ? ("+" + ageBonus + " (waiting " + waitMs + "ms)") : "+0"), 2, row);

            Label scoreLabel = mono(String.valueOf(finalScore));
            scoreLabel.setStyle(scoreLabel.getStyle() + " -fx-font-weight: bold;");
            grid.add(scoreLabel, 3, row);

            Label decision;
            if (step < MAX_STEP) {
                decision = badge("Pending", "#c8cdd4", "#262a31");
            } else {
                boolean isWinner = p.id.equals("P4") || p.id.equals("P1");
                decision = p.id.equals("P4")
                        ? badge("Runs first", "#a7f3c1", COLOR_SUCCESS_BG)
                        : p.id.equals("P1")
                            ? badge("Granted R2", "#a7f3c1", COLOR_SUCCESS_BG)
                            : badge("Suspended", "#ffd699", COLOR_WARN_BG);
            }
            grid.add(decision, 4, row);

            row++;
            idx++;
        }

        card.getChildren().add(grid);
        return card;
    }

    private void styleNavButton(Button b, boolean primary) {
        if (primary) {
            b.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-background-radius: 6; -fx-padding: 8 14 8 14;");
        } else {
            b.setStyle("-fx-background-color: #262a31; -fx-text-fill: #c8cdd4; " +
                    "-fx-background-radius: 6; -fx-padding: 8 14 8 14;");
        }
    }
}
