package com.deadlocksim;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.deadlocksim.UiKit.*;

/**
 * Screen 1 — draws the Resource Allocation Graph for the fixed scenario and
 * highlights the circular-wait cycle in red, plus a summary table.
 */
public class DeadlockScenePane extends VBox {

    private final DeadlockScenario scenario;

    public DeadlockScenePane(DeadlockScenario scenario) {
        this.scenario = scenario;
        setSpacing(16);
        setFillWidth(true);
        setMaxWidth(980);

        getChildren().add(h2("Resource Allocation Graph"));
        getChildren().add(body("Four processes contend for three resources. " +
                "P1, P2, and P3 form a circular wait — a deadlock. P4 is blocked waiting on R2 but is not part of the cycle."));

        getChildren().add(buildGraphCard());
        getChildren().add(buildBannerAndMetrics());
        getChildren().add(buildAllocationTable());
        getChildren().add(buildConditionsCard());
    }

    private VBox buildGraphCard() {
        VBox card = card("Graph");
        Pane graphPane = buildGraph();
        card.getChildren().add(graphPane);
        return card;
    }

    private Pane buildGraph() {
        Pane pane = new Pane();
        pane.setPrefSize(900, 340);
        pane.setMinSize(900, 340);
        pane.setMaxSize(900, 340);
        pane.setStyle("-fx-background-color: #14161a; -fx-background-radius: 8;");

        Rectangle clip = new Rectangle(900, 340);
        pane.setClip(clip);

        // Node positions
        Map<String, double[]> procPos = new LinkedHashMap<>();
        procPos.put("P1", new double[]{160, 90});
        procPos.put("P2", new double[]{460, 60});
        procPos.put("P3", new double[]{760, 90});
        procPos.put("P4", new double[]{460, 280});

        Map<String, double[]> resPos = new LinkedHashMap<>();
        resPos.put("R1", new double[]{160, 250});
        resPos.put("R2", new double[]{460, 180});
        resPos.put("R3", new double[]{760, 250});

        // Edges: [fromX, fromY, toX, toY, isCycle]
        // P1 -> R2 (request), R2 -> P2 (alloc), P2 -> R3 (request),
        // R3 -> P3 (alloc), P3 -> R1 (request), R1 -> P1 (alloc), P4 -> R2 (request)
        drawArrow(pane, procPos.get("P1"), resPos.get("R2"), true, 26, 22);
        drawArrow(pane, resPos.get("R2"), procPos.get("P2"), true, 22, 26);
        drawArrow(pane, procPos.get("P2"), resPos.get("R3"), true, 26, 22);
        drawArrow(pane, resPos.get("R3"), procPos.get("P3"), true, 22, 26);
        drawArrow(pane, procPos.get("P3"), resPos.get("R1"), true, 26, 22);
        drawArrow(pane, resPos.get("R1"), procPos.get("P1"), true, 22, 26);
        drawArrow(pane, procPos.get("P4"), resPos.get("R2"), false, 26, 22);

        // Resource boxes
        for (Map.Entry<String, double[]> e : resPos.entrySet()) {
            pane.getChildren().add(resourceNode(e.getKey(), e.getValue()[0], e.getValue()[1]));
        }
        // Process circles
        for (Map.Entry<String, double[]> e : procPos.entrySet()) {
            boolean inCycle = scenario.get(e.getKey()).inCycle;
            pane.getChildren().add(processNode(e.getKey(), e.getValue()[0], e.getValue()[1], inCycle));
        }

        // Legend
        pane.getChildren().add(legendItem(20, 320, "#ef4444", "Deadlock cycle edge"));
        pane.getChildren().add(legendItem(230, 320, "#5b6370", "Non-cycle edge"));

        return pane;
    }

    private void drawArrow(Pane pane, double[] from, double[] to, boolean isCycle, double fromRadius, double toRadius) {
        double dx = to[0] - from[0];
        double dy = to[1] - from[1];
        double len = Math.sqrt(dx * dx + dy * dy);
        double ux = dx / len;
        double uy = dy / len;

        double sx = from[0] + ux * fromRadius;
        double sy = from[1] + uy * fromRadius;
        double ex = to[0] - ux * toRadius;
        double ey = to[1] - uy * toRadius;

        String color = isCycle ? "#ef4444" : "#5b6370";

        Line line = new Line(sx, sy, ex, ey);
        line.setStroke(Color.web(color));
        line.setStrokeWidth(isCycle ? 2.2 : 1.6);
        pane.getChildren().add(line);

        // Arrowhead
        double arrowSize = 9;
        double angle = Math.atan2(ey - sy, ex - sx);
        double a1 = angle + Math.toRadians(150);
        double a2 = angle - Math.toRadians(150);
        Polygon head = new Polygon(
                ex, ey,
                ex + arrowSize * Math.cos(a1), ey + arrowSize * Math.sin(a1),
                ex + arrowSize * Math.cos(a2), ey + arrowSize * Math.sin(a2)
        );
        head.setFill(Color.web(color));
        pane.getChildren().add(head);
    }

    private javafx.scene.Node processNode(String id, double cx, double cy, boolean inCycle) {
        Circle c = new Circle(cx, cy, 26);
        c.setFill(Color.web(inCycle ? "#3b1d1d" : "#1d2a3b"));
        c.setStroke(Color.web(inCycle ? "#ef4444" : "#3b82f6"));
        c.setStrokeWidth(1.6);

        Text t = new Text(id);
        t.setFill(Color.web(inCycle ? "#ff8a8a" : "#9cc0ff"));
        t.setFont(Font.font("System", FontWeight.BOLD, 14));
        t.setX(cx - (id.length() * 4.2));
        t.setY(cy + 5);

        Pane group = new Pane(c, t);
        return group;
    }

    private javafx.scene.Node resourceNode(String id, double cx, double cy) {
        double w = 50, h = 38;
        Rectangle r = new Rectangle(cx - w / 2, cy - h / 2, w, h);
        r.setArcWidth(8);
        r.setArcHeight(8);
        r.setFill(Color.web("#20242b"));
        r.setStroke(Color.web("#5b6370"));
        r.setStrokeWidth(1.2);

        Text t = new Text(id);
        t.setFill(Color.web("#e6e8eb"));
        t.setFont(Font.font("System", FontWeight.BOLD, 13));
        t.setX(cx - (id.length() * 4.2));
        t.setY(cy + 5);

        return new Pane(r, t);
    }

    private javafx.scene.Node legendItem(double x, double y, String color, String label) {
        Line line = new Line(x, y, x + 24, y);
        line.setStroke(Color.web(color));
        line.setStrokeWidth(2);
        Text t = new Text(label);
        t.setX(x + 32);
        t.setY(y + 4);
        t.setFill(Color.web("#9aa0a6"));
        t.setFont(Font.font("System", 11));
        return new Pane(line, t);
    }

    private VBox buildBannerAndMetrics() {
        VBox box = new VBox(12);
        box.getChildren().add(banner("⚠ Deadlock detected — circular wait: P1 → R2 → P2 → R3 → P3 → R1 → P1",
                COLOR_DANGER, COLOR_DANGER_BG));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(metricCell("Processes", "P1, P2, P3, P4"), 0, 0);
        grid.add(metricCell("Resources", "R1, R2, R3"), 1, 0);
        grid.add(metricCell("Cycle length", "3 processes"), 2, 0);
        grid.add(metricCell("Blocked outside cycle", "P4 (wants R2)"), 3, 0);
        for (int i = 0; i < 4; i++) {
            javafx.scene.layout.ColumnConstraints cc = new javafx.scene.layout.ColumnConstraints();
            cc.setPercentWidth(25);
            grid.getColumnConstraints().add(cc);
        }
        box.getChildren().add(grid);
        return box;
    }

    private VBox buildAllocationTable() {
        VBox card = card("Allocation state");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(8);

        String[] headers = {"Process", "Holds", "Waiting for", "Status"};
        for (int i = 0; i < headers.length; i++) {
            Label h = new Label(headers[i]);
            h.setStyle("-fx-text-fill: " + COLOR_MUTED + "; -fx-font-size: 11px; -fx-font-weight: bold;");
            grid.add(h, i, 0);
        }

        int row = 1;
        for (DeadlockScenario.Proc p : scenario.getProcessList()) {
            grid.add(mono(p.id), 0, row);
            grid.add(mono(p.holds == null ? "—" : p.holds), 1, row);
            grid.add(mono(p.wants), 2, row);
            Label status = p.inCycle
                    ? badge("Deadlocked", "#ffb4b4", "#3a1d1d")
                    : badge("Blocked", "#ffd699", "#3a2e14");
            grid.add(status, 3, row);
            row++;
        }

        card.getChildren().add(grid);
        return card;
    }

    private VBox buildConditionsCard() {
        VBox card = card("Necessary conditions for deadlock (all four present)");

        String[] conditions = {
                "Mutual exclusion — R1, R2, R3 are each held exclusively by one process",
                "Hold and wait — every process in the cycle holds one resource while requesting another",
                "No preemption — resources are not forcibly taken away from holders",
                "Circular wait — P1 → P2 → P3 → P1 forms a closed cycle in the graph"
        };

        for (String c : conditions) {
            Label l = new Label(c);
            l.setWrapText(true);
            l.setPadding(new Insets(8, 12, 8, 12));
            l.setStyle("-fx-text-fill: #ffb4b4; -fx-background-color: #2a1818; -fx-background-radius: 6; -fx-font-size: 12px;");
            card.getChildren().add(l);
        }
        return card;
    }
}
