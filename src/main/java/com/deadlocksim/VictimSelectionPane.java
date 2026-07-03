package com.deadlocksim;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.deadlocksim.UiKit.*;

/**
 * Screen 3 — Victim Selection.
 *
 * Among the processes in the deadlock cycle, compute a weighted "cost to
 * abort" score from: resources held, CPU time invested, priority, and
 * rollback cost. The process with the LOWEST cost is chosen as the victim
 * and aborted, releasing its resources and breaking the cycle.
 */
public class VictimSelectionPane extends VBox {

    private final DeadlockScenario scenario;
    private final VBox logContent = new VBox(4);
    private final VBox dynamicArea = new VBox(14);
    private Timeline timeline;
    private boolean resolved = false;

    // weighted cost scores, precomputed for the fixed demo data
    private final Map<String, Double> costScores = new LinkedHashMap<>();
    private String victimId;

    public VictimSelectionPane(DeadlockScenario scenario) {
        this.scenario = scenario;
        computeCosts();

        setSpacing(16);
        setMaxWidth(980);

        getChildren().add(h2("Victim Selection"));
        getChildren().add(body("Among the processes caught in the deadlock cycle, the resolver computes a " +
                "weighted cost-to-abort score and aborts whichever process is cheapest to roll back. " +
                "Lower score = cheaper to sacrifice."));

        getChildren().add(dynamicArea);
        refresh();
    }

    public void refresh() {
        resolved = false;
        if (timeline != null) timeline.stop();
        logContent.getChildren().clear();
        render();
    }

    private void computeCosts() {
        // Weights: resources held 30%, CPU time 25%, priority 25%, rollback cost 20%
        // Scaled into a 0-10 "cost" scale for display purposes (fixed demo numbers).
        costScores.put("P1", 6.4);
        costScores.put("P2", 3.1);
        costScores.put("P3", 7.8);

        victimId = "P2";
        double min = Double.MAX_VALUE;
        for (Map.Entry<String, Double> e : costScores.entrySet()) {
            if (e.getValue() < min) {
                min = e.getValue();
                victimId = e.getKey();
            }
        }
    }

    private void render() {
        dynamicArea.getChildren().clear();
        dynamicArea.getChildren().add(buildCriteriaTable());
        dynamicArea.getChildren().add(buildScoreCells());

        if (resolved) {
            dynamicArea.getChildren().add(banner("🎯 Victim selected: " + victimId +
                    " — lowest cost to abort and roll back", COLOR_DANGER, COLOR_DANGER_BG));
            dynamicArea.getChildren().add(buildLogCard());
        } else {
            Button run = new Button("Run victim selection →");
            run.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-background-radius: 6; -fx-padding: 10 18 10 18;");
            run.setOnAction(e -> animateResolution());
            dynamicArea.getChildren().add(run);
        }
    }

    private VBox buildCriteriaTable() {
        VBox card = card("Victim selection criteria");

        GridPane grid = new GridPane();
        grid.setHgap(18);
        grid.setVgap(8);

        String[] headers = {"Criterion", "Weight", "P1", "P2", "P3"};
        for (int i = 0; i < headers.length; i++) {
            Label h = new Label(headers[i]);
            h.setStyle("-fx-text-fill: " + COLOR_MUTED + "; -fx-font-size: 11px; -fx-font-weight: bold;");
            grid.add(h, i, 0);
        }

        String[][] rows = {
                {"Resources held", "30%", "1", "2", "1"},
                {"CPU time used", "25%", "60ms", "20ms", "45ms"},
                {"Priority level", "25%", "5 (med)", "3 (low)", "4 (med)"},
                {"Rollback cost", "20%", "Medium", "Low", "High"}
        };

        int row = 1;
        for (String[] r : rows) {
            for (int col = 0; col < r.length; col++) {
                Label cell = mono(r[col]);
                if (col == 3 && r[0].equals("Rollback cost")) {
                    cell.setStyle(cell.getStyle() + " -fx-font-weight: bold;");
                }
                grid.add(cell, col, row);
            }
            row++;
        }

        card.getChildren().add(grid);
        return card;
    }

    private HBox buildScoreCells() {
        HBox row = new HBox(10);
        for (Map.Entry<String, Double> e : costScores.entrySet()) {
            boolean isVictim = resolved && e.getKey().equals(victimId);
            VBox cell = new VBox(4);
            cell.setPadding(new Insets(10, 14, 10, 14));
            String bg = isVictim ? COLOR_DANGER_BG : "#20242b";
            String border = isVictim ? "-fx-border-color: " + COLOR_DANGER + "; -fx-border-width: 1.5; -fx-border-radius: 8;" : "";
            cell.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 8; " + border);

            Label lab = new Label(e.getKey() + " cost score");
            lab.setStyle("-fx-text-fill: " + (isVictim ? "#ff8a8a" : COLOR_MUTED) + "; -fx-font-size: 10px; -fx-font-weight: bold;");
            Label val = new Label(String.valueOf(e.getValue()));
            val.setStyle("-fx-text-fill: " + (isVictim ? COLOR_DANGER : COLOR_TEXT) + "; -fx-font-size: 18px; -fx-font-weight: bold;");
            Label hint = new Label(isVictim ? "← lowest cost" : "higher = costlier");
            hint.setStyle("-fx-text-fill: " + (isVictim ? "#ff8a8a" : COLOR_MUTED) + "; -fx-font-size: 10px;");

            cell.getChildren().addAll(lab, val, hint);
            HBox.setHgrow(cell, javafx.scene.layout.Priority.ALWAYS);
            cell.setMaxWidth(Double.MAX_VALUE);
            row.getChildren().add(cell);
        }
        return row;
    }

    private VBox buildLogCard() {
        VBox card = card("Resolution sequence");
        card.getChildren().add(logBox(logContent));
        return card;
    }

    private void animateResolution() {
        resolved = true;
        logContent.getChildren().clear();
        render();

        String[][] lines = {
                {"● " + victimId + " selected as victim (cost score " + costScores.get(victimId) + ")", COLOR_DANGER},
                {"● " + victimId + " aborted — releasing held resources", COLOR_WARN},
                {"● Freed resource now available to its waiter", COLOR_SUCCESS},
                {"● Next process in the former cycle can proceed", COLOR_SUCCESS},
                {"● P4 unblocked once its required resource is free", COLOR_SUCCESS},
                {"● Cycle dissolved. System resumes normal operation.", COLOR_INFO}
        };

        timeline = new Timeline();
        for (int i = 0; i < lines.length; i++) {
            final String text = lines[i][0];
            final String color = lines[i][1];
            KeyFrame kf = new KeyFrame(Duration.millis(450 * (i + 1)), e ->
                    logContent.getChildren().add(logLine(text, color)));
            timeline.getKeyFrames().add(kf);
        }
        timeline.play();
    }
}
