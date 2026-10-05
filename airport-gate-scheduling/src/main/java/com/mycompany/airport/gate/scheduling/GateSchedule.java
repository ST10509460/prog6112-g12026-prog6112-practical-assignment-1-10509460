package com.mycompany.airport.gate.scheduling;

import java.util.ArrayList;
import java.util.List;

// Keeps track of which flight uses each gate and time slot.
public class GateSchedule {
    public static final int GATE_COUNT = 5, SLOT_COUNT = 8;
    // Each row is a gate and each column is a time slot.
    private final String[][] schedule = new String[GATE_COUNT][SLOT_COUNT];
    // Adds a flight only when both the gate and flight are available.
    public void allocate(String flightId, int gate, int slot) {
        checkPosition(gate, slot);
        if (findFlight(flightId) != null) throw new IllegalStateException("This flight is already scheduled.");
        if (schedule[gate - 1][slot - 1] != null) throw new IllegalStateException("That gate and time slot are occupied.");
        schedule[gate - 1][slot - 1] = flightId;
    }
    // Removes a flight from its current gate and slot.
    public boolean release(String flightId) {
        for (int g = 0; g < GATE_COUNT; g++) for (int s = 0; s < SLOT_COUNT; s++)
            if (flightId.equals(schedule[g][s])) { schedule[g][s] = null; return true; }
        return false;
    }
    // Moves a flight after checking that the new slot is free.
    public void reschedule(String flightId, int gate, int slot) {
        checkPosition(gate, slot);
        int[] old = findFlight(flightId);
        if (old == null) throw new IllegalStateException("Flight is not currently scheduled.");
        if (schedule[gate - 1][slot - 1] != null && !(old[0] == gate - 1 && old[1] == slot - 1))
            throw new IllegalStateException("That gate and time slot are occupied.");
        schedule[old[0]][old[1]] = null;
        schedule[gate - 1][slot - 1] = flightId;
    }
    public int[] findFlight(String id) {
        for (int g = 0; g < GATE_COUNT; g++) for (int s = 0; s < SLOT_COUNT; s++)
            if (id.equals(schedule[g][s])) return new int[]{g, s};
        return null;
    }
    public String getFlightAt(int gate, int slot) { checkPosition(gate, slot); return schedule[gate - 1][slot - 1]; }
    public List<String> availableSlots() {
        List<String> result = new ArrayList<>();
        for (int g = 0; g < GATE_COUNT; g++) for (int s = 0; s < SLOT_COUNT; s++)
            if (schedule[g][s] == null) result.add("Gate " + (g + 1) + ", Slot " + (s + 1));
        return result;
    }
    public List<String> occupiedSlots() {
        List<String> result = new ArrayList<>();
        for (int g = 0; g < GATE_COUNT; g++) for (int s = 0; s < SLOT_COUNT; s++)
            if (schedule[g][s] != null) result.add("Gate " + (g + 1) + ", Slot " + (s + 1) + ": " + schedule[g][s]);
        return result;
    }
    // Prints the full schedule by checking every row and column.
    public String displaySchedule() {
        StringBuilder out = new StringBuilder("Gate\\Slot");
        for (int s = 1; s <= SLOT_COUNT; s++) out.append(String.format(" | S%-5d", s));
        out.append('\n');
        for (int g = 0; g < GATE_COUNT; g++) {
            out.append(String.format("Gate %-2d", g + 1));
            for (int s = 0; s < SLOT_COUNT; s++) out.append(String.format(" | %-6s", schedule[g][s] == null ? "--" : schedule[g][s]));
            out.append('\n');
        }
        return out.toString();
    }
    private static void checkPosition(int gate, int slot) {
        if (gate < 1 || gate > GATE_COUNT || slot < 1 || slot > SLOT_COUNT)
            throw new IllegalArgumentException("Gate must be 1-5 and slot must be 1-8.");
    }
}
