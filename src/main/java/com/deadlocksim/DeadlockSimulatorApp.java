package com.deadlocksim;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Deadlock Simulator GUI.
 *
 * Flow:
 *   1) Deadlock Scenario screen - shows a resource allocation graph with a
 *      circular wait deadlock among processes P1, P2, P3 (and P4 blocked
 *      outside the cycle).
 *   2) Three resolution technique tabs:
 *        - Dynamic Priority Allocation
 *        - Victim Selection
 *        - Wait-Die Scheme
 */
public class DeadlockSimulatorApp extends Application {

    // Shared scenario data model used by every screen.
    private final DeadlockScenario scenario = new DeadlockScenario();

    private BorderPane root;
    private VBox contentHost;

    private DeadlockScenePane scenarioPane;
    private PriorityAllocationPane priorityPane;
    private VictimSelectionPane victimPane;
    private WaitDiePane waitDiePane;

    @Override
    public void start(Stage stage) {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #14161a;");

        root.setTop(buildHeader());

        contentHost = new VBox();
        contentHost.setPadding(new Insets(20));
        contentHost.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(contentHost);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #14161a; -fx-background-color: transparent;");
        root.setCenter(scrollPane);

        // Build each screen once; swap visible content via navigation.
        scenarioPane = new DeadlockScenePane(scenario);
        priorityPane = new PriorityAllocationPane(scenario);
        victimPane = new VictimSelectionPane(scenario);
        waitDiePane = new WaitDiePane(scenario);

        showScreen(scenarioPane);

        Scene scene = new Scene(root, 1080, 760);
        var cssUrl = getClass().getResource("/app.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
        stage.setTitle("Deadlock Simulator");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(640);
        stage.show();
    }

    private void showScreen(Region pane) {
        contentHost.getChildren().setAll(pane);
        VBox.setVgrow(pane, javafx.scene.layout.Priority.ALWAYS);
    }

    private HBox buildHeader() {
        Label title = new Label("Deadlock Simulator");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        Label subtitle = new Label("Detection  →  Resolution techniques");
        subtitle.setStyle("-fx-text-fill: #9aa0a6; -fx-font-size: 12px;");

        VBox titleBox = new VBox(2, title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        ToggleGroup nav = new ToggleGroup();

        ToggleButton btnScenario = navButton("1. Deadlock Scenario", nav, true);
        ToggleButton btnPriority = navButton("2. Dynamic Priority", nav, false);
        ToggleButton btnVictim = navButton("3. Victim Selection", nav, false);
        ToggleButton btnWaitDie = navButton("4. Wait-Die", nav, false);

        btnScenario.setOnAction(e -> showScreen(scenarioPane));
        btnPriority.setOnAction(e -> { priorityPane.refresh(); showScreen(priorityPane); });
        btnVictim.setOnAction(e -> { victimPane.refresh(); showScreen(victimPane); });
        btnWaitDie.setOnAction(e -> { waitDiePane.refresh(); showScreen(waitDiePane); });

        HBox navBox = new HBox(8, btnScenario, btnPriority, btnVictim, btnWaitDie);
        navBox.setAlignment(Pos.CENTER_RIGHT);

        HBox header = new HBox(20, titleBox, spacer, navBox);
        header.setPadding(new Insets(16, 24, 16, 24));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #1c1f24; -fx-border-color: #2a2e35; -fx-border-width: 0 0 1 0;");
        return header;
    }

    private ToggleButton navButton(String text, ToggleGroup group, boolean selected) {
        ToggleButton b = new ToggleButton(text);
        b.setToggleGroup(group);
        b.setSelected(selected);
        b.setStyle(navButtonStyle(selected));
        b.selectedProperty().addListener((obs, was, isNow) -> b.setStyle(navButtonStyle(isNow)));
        return b;
    }

    private String navButtonStyle(boolean selected) {
        if (selected) {
            return "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-size: 12px; " +
                   "-fx-background-radius: 6; -fx-padding: 8 14 8 14; -fx-font-weight: bold;";
        }
        return "-fx-background-color: #262a31; -fx-text-fill: #c8cdd4; -fx-font-size: 12px; " +
               "-fx-background-radius: 6; -fx-padding: 8 14 8 14;";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
