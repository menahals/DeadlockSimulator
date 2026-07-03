package com.deadlocksim;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/** Small reusable styled building blocks shared across all screens. */
public final class UiKit {

    private UiKit() {}

    public static final String COLOR_BG = "#14161a";
    public static final String COLOR_CARD = "#1c1f24";
    public static final String COLOR_BORDER = "#2a2e35";
    public static final String COLOR_TEXT = "#e6e8eb";
    public static final String COLOR_MUTED = "#9aa0a6";
    public static final String COLOR_DANGER = "#ef4444";
    public static final String COLOR_DANGER_BG = "#3a1d1d";
    public static final String COLOR_WARN = "#f59e0b";
    public static final String COLOR_WARN_BG = "#3a2e14";
    public static final String COLOR_SUCCESS = "#22c55e";
    public static final String COLOR_SUCCESS_BG = "#163a22";
    public static final String COLOR_INFO = "#3b82f6";
    public static final String COLOR_INFO_BG = "#16233a";

    public static VBox card(String title) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(16, 18, 16, 18));
        box.setStyle("-fx-background-color: " + COLOR_CARD + "; -fx-background-radius: 10; " +
                "-fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 10; -fx-border-width: 1;");
        if (title != null) {
            Label l = sectionLabel(title);
            box.getChildren().add(l);
        }
        return box;
    }

    public static Label sectionLabel(String text) {
        Label l = new Label(text.toUpperCase());
        l.setStyle("-fx-text-fill: " + COLOR_MUTED + "; -fx-font-size: 11px; -fx-font-weight: bold;");
        return l;
    }

    public static Label h2(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.BOLD, 18));
        l.setStyle("-fx-text-fill: " + COLOR_TEXT + ";");
        return l;
    }

    public static Label body(String text) {
        Label l = new Label(text);
        l.setWrapText(true);
        l.setStyle("-fx-text-fill: " + COLOR_MUTED + "; -fx-font-size: 13px;");
        return l;
    }

    public static Label mono(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + COLOR_TEXT + "; -fx-font-family: 'Consolas', 'Menlo', monospace; -fx-font-size: 12px;");
        return l;
    }

    public static Label badge(String text, String fg, String bg) {
        Label l = new Label(text);
        l.setPadding(new Insets(3, 9, 3, 9));
        l.setStyle("-fx-text-fill: " + fg + "; -fx-background-color: " + bg +
                "; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");
        return l;
    }

    public static HBox banner(String text, String fg, String bg) {
        Label l = new Label(text);
        l.setWrapText(true);
        l.setStyle("-fx-text-fill: " + fg + "; -fx-font-size: 13px; -fx-font-weight: bold;");
        HBox box = new HBox(l);
        box.setPadding(new Insets(10, 14, 10, 14));
        box.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 8;");
        box.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(l, Priority.ALWAYS);
        return box;
    }

    public static VBox metricCell(String label, String value) {
        Label lab = new Label(label.toUpperCase());
        lab.setStyle("-fx-text-fill: " + COLOR_MUTED + "; -fx-font-size: 10px; -fx-font-weight: bold;");
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: " + COLOR_TEXT + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        VBox box = new VBox(4, lab, val);
        box.setPadding(new Insets(10, 12, 10, 12));
        box.setStyle("-fx-background-color: #20242b; -fx-background-radius: 8;");
        return box;
    }

    public static ScrollPane logBox(VBox content) {
        content.setPadding(new Insets(10, 12, 10, 12));
        content.setStyle("-fx-background-color: #0f1115; -fx-background-radius: 8;");
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setPrefHeight(140);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return sp;
    }

    public static Label logLine(String text, String color) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + color + "; -fx-font-family: 'Consolas', 'Menlo', monospace; -fx-font-size: 12px;");
        l.setWrapText(true);
        return l;
    }
}
