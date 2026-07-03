package com.deadlocksim;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Shared, fixed scenario used by all four screens of the simulator.
 *
 * Scenario:
 *   P1 holds R1, requests R2
 *   P2 holds R2, requests R3
 *   P3 holds R3, requests R1   -> circular wait: P1 -> P2 -> P3 -> P1
 *   P4 holds nothing, requests R2   -> blocked, but not part of the cycle
 *
 * Each process also carries simulation attributes used by the three
 * resolution techniques (age/timestamp, base priority, CPU time used,
 * rollback cost) so the techniques can be demonstrated on the same data.
 */
public class DeadlockScenario {

    public static class Proc {
        public final String id;
        public final String holds;     // resource id this process currently holds, or null
        public final String wants;     // resource id this process is requesting
        public final int timestampMs;  // lower = older/started earlier (used by Wait-Die)
        public final int basePriority; // used by Dynamic Priority Allocation
        public final int cpuTimeMs;    // work already invested - used by Victim Selection
        public final String rollbackCost; // Low / Medium / High - used by Victim Selection
        public final boolean inCycle;

        public Proc(String id, String holds, String wants, int timestampMs,
                     int basePriority, int cpuTimeMs, String rollbackCost, boolean inCycle) {
            this.id = id;
            this.holds = holds;
            this.wants = wants;
            this.timestampMs = timestampMs;
            this.basePriority = basePriority;
            this.cpuTimeMs = cpuTimeMs;
            this.rollbackCost = rollbackCost;
            this.inCycle = inCycle;
        }
    }

    private final Map<String, Proc> processes = new LinkedHashMap<>();
    private final List<String> resources = List.of("R1", "R2", "R3");

    public DeadlockScenario() {
        // timestamp: smaller = older (arrived first)
        processes.put("P1", new Proc("P1", "R1", "R2", 50, 5, 60, "Medium", true));
        processes.put("P2", new Proc("P2", "R2", "R3", 20, 3, 20, "Low", true));
        processes.put("P3", new Proc("P3", "R3", "R1", 15, 4, 45, "High", true));
        processes.put("P4", new Proc("P4", null, "R2", 5, 8, 5, "Low", false));
    }

    public Map<String, Proc> getProcesses() {
        return processes;
    }

    public List<Proc> getProcessList() {
        return new ArrayList<>(processes.values());
    }

    public List<String> getResources() {
        return resources;
    }

    public List<Proc> getCycleProcesses() {
        List<Proc> cycle = new ArrayList<>();
        for (Proc p : processes.values()) {
            if (p.inCycle) cycle.add(p);
        }
        return cycle;
    }

    public Proc get(String id) {
        return processes.get(id);
    }

    /** Process holding a given resource, or null if free. */
    public Proc holderOf(String resourceId) {
        for (Proc p : processes.values()) {
            if (resourceId.equals(p.holds)) return p;
        }
        return null;
    }
}
