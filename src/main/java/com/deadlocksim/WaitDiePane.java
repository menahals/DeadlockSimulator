package com.deadlocksim;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import static com.deadlocksim.UiKit.*;

/**
 * Screen 4 — Wait-Die scheme (a deadlock PREVENTION technique, unlike the
 * previous two which are detection + recovery techniques).
 *
 * Rule, using timestamps where a smaller timestamp means an older process:
 *   - If the REQUESTING process is OLDER than the holder -> it WAITS.
 *   - If the REQUESTING process is YOUNGER than the holder -> it DIES
 *     (is aborted and restarts later with its ORIGINAL timestamp, so it
 *     eventually becomes the oldest and is guaranteed to proceed).
 *
 * Because a younger process can never end up holding a resource that an
 * older process needs while also waiting on that older process, no cycle
 * can ever form.
 */
public class WaitDiePane extends VBox {

    private final DeadlockScenario scenario;
    private int scenarioIndex = 0;
    private static final int SCENARIO_COUNT = 3;

    private final VBox dynamicArea = new VBox(14);

    private static class Scenario {
        String title, requestLine, decisionLine, detail, outcome; // outcome: "wait" or "die"
        String requesterId, holderId;

        Scenario(String title, String requestLine, String decisionLine, String detail,
                 String outcome, String requesterId, String holderId) {
            this.title = title;
            this.requestLine = requestLine;
            this.decisionLine = decisionLine;
            this.detail = detail;
            this.outcome = outcome;
            this.requesterId = requesterId;
            this.holderId = holderId;
        }
    }

    private final Scenario[] scenarios = {
            new Scenario(
                    "Scenario 1 — older process requests a resource held by a younger one",
                    "P1 (timestamp 50, older) requests R2, held by P2 (timestamp 20, younger)",
                    "P1 is older than P2  ->  P1 WAITS for P2 to release R2",
                    "Old requesting from young is allowed to wait. This is safe: the older process can never end up " +
                            "indirectly waiting on something the younger one needs, because of how timestamps order requests.",
                    "wait", "P1", "P2"
            ),
            new Scenario(
                    "Scenario 2 — younger process requests a resource held by an older one",
                    "P3 (timestamp 15, younger) requests R1, held by P1 (timestamp 50, older)",
                    "P3 is younger than P1  ->  P3 DIES (aborted, restarts with its original timestamp 15)",
                    "Young requesting from old must die. P3 is rolled back immediately. It restarts carrying its " +
                            "original timestamp, so it grows relatively \"older\" over time and is guaranteed to eventually win.",
                    "die", "P3", "P1"
            ),
            new Scenario(
                    "Scenario 3 — newest process requests a resource held by an older one",
                    "P4 (timestamp 5, newest) requests R2, held by P2 (timestamp 20, older)",
                    "P4 is younger than P2  ->  P4 DIES, re-queued keeping timestamp 5",
                    "Wait-Die prevents circular wait by construction: a younger process never holds a resource while " +
                            "waiting on an older one, so a cycle made entirely of \"younger waits on older\" edges can never close.",
                    "die", "P4", "P2"
            )
    };

    public WaitDiePane(DeadlockScenario scenario) {
        this.scenario = scenario;
        setSpacing(16);
        setMaxWidth(980);

        getChildren().add(h2("Wait-Die Scheme"));
        getChildren().add(buildRuleCard());
        getChildren().add(dynamicArea);

        refresh();
    }

    public void refresh() {
        scenarioIndex = 0;
        render();
    }

    private HBox buildRuleCard() {
        HBox row = new HBox(10);

        VBox wait = new VBox(6);
        wait.setPadding(new Insets(12, 14, 12, 14));
        wait.setStyle("-fx-background-color: " + COLOR_INFO_BG + "; -fx-background-radius: 8;");
        Label waitTitle = new Label("WAIT  (older requests from younger)");
        waitTitle.setStyle("-fx-text-fill: #9cc0ff; -fx-font-weight: bold; -fx-font-size: 12px;");
        Label waitBody = new Label("If the requesting process is older than the holder, it waits patiently.");
        waitBody.setWrapText(true);
        waitBody.setStyle("-fx-text-fill: #9cc0ff; -fx-font-size: 12px;");
        wait.getChildren().addAll(waitTitle, waitBody);
        HBox.setHgrow(wait, javafx.scene.layout.Priority.ALWAYS);
        wait.setMaxWidth(Double.MAX_VALUE);

        VBox die = new VBox(6);
        die.setPadding(new Insets(12, 14, 12, 14));
        die.setStyle("-fx-background-color: " + COLOR_DANGER_BG + "; -fx-background-radius: 8;");
        Label dieTitle = new Label("DIE  (younger requests from older)");
        dieTitle.setStyle("-fx-text-fill: #ff8a8a; -fx-font-weight: bold; -fx-font-size: 12px;");
        Label dieBody = new Label("If the requesting process is younger, it is aborted and restarts with its original timestamp.");
        dieBody.setWrapText(true);
        dieBody.setStyle("-fx-text-fill: #ff8a8a; -fx-font-size: 12px;");
        die.getChildren().addAll(dieTitle, dieBody);
        HBox.setHgrow(die, javafx.scene.layout.Priority.ALWAYS);
        die.setMaxWidth(Double.MAX_VALUE);

        row.getChildren().addAll(wait, die);
        return row;
    }

    private void render() {
        dynamicArea.getChildren().clear();
        Scenario sc = scenarios[scenarioIndex];

        dynamicArea.getChildren().add(sectionLabel(
                "Live simulation — scenario " + (scenarioIndex + 1) + " of " + SCENARIO_COUNT));

        String bannerFg = sc.outcome.equals("wait") ? COLOR_WARN : COLOR_DANGER;
        String bannerBg = sc.outcome.equals("wait") ? COLOR_WARN_BG : COLOR_DANGER_BG;
        VBox bannerBox = new VBox(4);
        Label tLabel = new Label(sc.title);
        tLabel.setWrapText(true);
        tLabel.setStyle("-fx-text-fill: " + bannerFg + "; -fx-font-weight: bold; -fx-font-size: 13px;");
        Label rLabel = new Label(sc.requestLine);
        rLabel.setWrapText(true);
        rLabel.setStyle("-fx-text-fill: " + bannerFg + "; -fx-font-size: 12px;");
        bannerBox.getChildren().addAll(tLabel, rLabel);
        bannerBox.setPadding(new Insets(10, 14, 10, 14));
        bannerBox.setStyle("-fx-background-color: " + bannerBg + "; -fx-background-radius: 8;");
        dynamicArea.getChildren().add(bannerBox);

        VBox decisionCard = card(null);
        Label decisionLabel = new Label("Decision");
        decisionLabel.setStyle("-fx-text-fill: " + COLOR_MUTED + "; -fx-font-size: 11px; -fx-font-weight: bold;");
        Label decisionText = new Label(sc.decisionLine);
        decisionText.setWrapText(true);
        decisionText.setStyle("-fx-text-fill: " + COLOR_TEXT + "; -fx-font-size: 13px;");
        decisionCard.getChildren().addAll(decisionLabel, decisionText);
        dynamicArea.getChildren().add(decisionCard);

        Label detail = body(sc.detail);
        dynamicArea.getChildren().add(detail);

        dynamicArea.getChildren().add(buildTimestampTable(sc));

        if (scenarioIndex == SCENARIO_COUNT - 1) {
            dynamicArea.getChildren().add(banner(
                    "✓ Wait-Die ensures no circular wait can ever form — deadlock prevention by design, not detection-and-recovery.",
                    COLOR_SUCCESS, COLOR_SUCCESS_BG));
        }

        HBox nav = new HBox(10);
        Button prev = new Button("← Previous scenario");
        Button next = new Button(scenarioIndex == SCENARIO_COUNT - 1 ? "Restart" : "Next scenario →");
        prev.setStyle("-fx-background-color: #262a31; -fx-text-fill: #c8cdd4; -fx-background-radius: 6; -fx-padding: 8 14 8 14;");
        next.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 14 8 14;");
        prev.setDisable(scenarioIndex == 0);

        prev.setOnAction(e -> { scenarioIndex = Math.max(0, scenarioIndex - 1); render(); });
        next.setOnAction(e -> {
            if (scenarioIndex == SCENARIO_COUNT - 1) scenarioIndex = 0;
            else scenarioIndex = Math.min(SCENARIO_COUNT - 1, scenarioIndex + 1);
            render();
        });
        prev.setMaxWidth(Double.MAX_VALUE);
        next.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(prev, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(next, javafx.scene.layout.Priority.ALWAYS);
        nav.getChildren().addAll(prev, next);
        dynamicArea.getChildren().add(nav);
    }

    private VBox buildTimestampTable(Scenario sc) {
        VBox card = card("Timestamp order (lower = older = arrived first)");

        GridPane grid = new GridPane();
        grid.setHgap(18);
        grid.setVgap(8);

        String[] headers = {"Process", "Timestamp", "Role this scenario", "Action"};
        for (int i = 0; i < headers.length; i++) {
            Label h = new Label(headers[i]);
            h.setStyle("-fx-text-fill: " + COLOR_MUTED + "; -fx-font-size: 11px; -fx-font-weight: bold;");
            grid.add(h, i, 0);
        }

        int row = 1;
        for (DeadlockScenario.Proc p : scenario.getProcessList()) {
            grid.add(mono(p.id), 0, row);
            grid.add(mono("T=" + p.timestampMs + "ms"), 1, row);

            String role = "—";
            Label action = badge("Not involved", "#c8cdd4", "#262a31");
            if (p.id.equals(sc.requesterId)) {
                role = "Requester";
                action = sc.outcome.equals("wait")
                        ? badge("Wait", "#9cc0ff", COLOR_INFO_BG)
                        : badge("Die -> restart", "#ff8a8a", COLOR_DANGER_BG);
            } else if (p.id.equals(sc.holderId)) {
                role = "Holder";
                action = badge("Holds resource", "#c8cdd4", "#262a31");
            }
            grid.add(mono(role), 2, row);
            grid.add(action, 3, row);
            row++;
        }

        card.getChildren().add(grid);
        return card;
    }
}
