package com.mycompany.airport.gate.scheduling;

import java.util.*;
import java.util.stream.Collectors;

// Runs the flight and gate tasks used by the menu and reports.
public class AirportSystem {
    private final Map<String, Flight> flights = new LinkedHashMap<>();
    private final GateSchedule gateSchedule = new GateSchedule();
    // Saves a flight if its ID has not already been used.
    public void registerFlight(Flight flight) {
        Objects.requireNonNull(flight);
        if (flights.containsKey(flight.getFlightId())) throw new IllegalArgumentException("Flight ID already exists.");
        flights.put(flight.getFlightId(), flight);
    }
    public Flight findFlight(String id) { return flights.get(id); }
    public List<Flight> getAllFlights() { return new ArrayList<>(flights.values()); }
    // Deletes the flight and frees its gate if it had one.
    public boolean deleteFlight(String id) {
        if (flights.remove(id) == null) return false;
        gateSchedule.release(id); return true;
    }
    public void allocateGate(String id, int gate, int slot) {
        Flight f = requireFlight(id);
        if (f.getStatus() == FlightStatus.CANCELLED) throw new IllegalStateException("Cancelled flights cannot be scheduled.");
        gateSchedule.allocate(id, gate, slot);
    }
    public boolean releaseGate(String id) { return gateSchedule.release(id); }
    public void rescheduleFlight(String id, int gate, int slot) { requireFlight(id); gateSchedule.reschedule(id, gate, slot); }
    // Changes flight details and frees the gate if the flight is cancelled.
    public void updateFlight(String id, String airline, String destination, String time, int capacity,
                             int booked, FlightCategory category, FlightStatus status) {
        Flight f = requireFlight(id);
        f.setAirlineName(airline); f.setDestination(destination); f.setDepartureTime(time);
        // Adjust bookings first when shrinking capacity; expand capacity first otherwise.
        if (capacity < f.getBookedPassengers()) {
            f.setBookedPassengers(booked); f.setPassengerCapacity(capacity);
        } else {
            f.setPassengerCapacity(capacity); f.setBookedPassengers(booked);
        }
        f.setCategory(category); f.setStatus(status);
        if (status == FlightStatus.CANCELLED) gateSchedule.release(id);
    }
    private Flight requireFlight(String id) {
        Flight f = findFlight(id); if (f == null) throw new IllegalArgumentException("Flight not found: " + id); return f;
    }
    public List<Flight> scheduledFlights() {
        return gateSchedule.occupiedSlots().stream().map(x -> x.substring(x.indexOf(": ") + 2))
                .map(flights::get).filter(Objects::nonNull).collect(Collectors.toList());
    }
    public List<Flight> delayedFlights() { return flights.values().stream().filter(f -> f.getStatus() == FlightStatus.DELAYED).collect(Collectors.toList()); }
    // Counts scheduled flights at each gate and returns the largest count.
    public String busiestGate() {
        int bestGate = -1, best = 0;
        for (int g = 1; g <= GateSchedule.GATE_COUNT; g++) {
            int count = 0; for (int s = 1; s <= GateSchedule.SLOT_COUNT; s++) if (gateSchedule.getFlightAt(g, s) != null) count++;
            if (count > best) { best = count; bestGate = g; }
        }
        return bestGate < 0 ? "No scheduled flights" : "Gate " + bestGate + " (" + best + " flights)";
    }
    // Counts scheduled flights in each time slot and returns the largest count.
    public String busiestTimeSlot() {
        int bestSlot = -1, best = 0;
        for (int s = 1; s <= GateSchedule.SLOT_COUNT; s++) {
            int count = 0; for (int g = 1; g <= GateSchedule.GATE_COUNT; g++) if (gateSchedule.getFlightAt(g, s) != null) count++;
            if (count > best) { best = count; bestSlot = s; }
        }
        return bestSlot < 0 ? "No scheduled flights" : "Slot " + bestSlot + " (" + best + " flights)";
    }
    public Optional<Flight> highestPassengerLoadFlight() {
        return flights.values().stream().filter(f -> f.getPassengerCapacity() > 0)
                .max(Comparator.comparingDouble(Flight::getPassengerLoadPercentage));
    }
    // Finds the average load for flights that have passenger seats.
    public double averagePassengerLoadPercentage() {
        return flights.values().stream().filter(f -> f.getPassengerCapacity() > 0)
                .mapToDouble(Flight::getPassengerLoadPercentage).average().orElse(0.0);
    }
    public List<Flight> sortByFlightId() { return flights.values().stream().sorted(Comparator.comparing(Flight::getFlightId)).collect(Collectors.toList()); }
    public List<Flight> sortByDepartureTime() { return flights.values().stream().sorted(Comparator.comparing(Flight::getDepartureTime).thenComparing(Flight::getFlightId)).collect(Collectors.toList()); }
    public GateSchedule getGateSchedule() { return gateSchedule; }
}
