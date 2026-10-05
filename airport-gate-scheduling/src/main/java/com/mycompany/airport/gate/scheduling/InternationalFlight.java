package com.mycompany.airport.gate.scheduling;

// Adds the details that only international flights need.
public class InternationalFlight extends Flight {
    private String departureTerminal;
    private String boardingGate;
    private boolean customsClearanceRequired;
    // Uses the Flight constructor for shared details, then saves extra details.
    public InternationalFlight(String flightId, String airlineName, String destination, String departureTime,
                               int passengerCapacity, int bookedPassengers, FlightStatus status,
                               String departureTerminal, String boardingGate, boolean customsClearanceRequired) {
        super(flightId, airlineName, destination, departureTime, passengerCapacity, bookedPassengers,
                FlightCategory.INTERNATIONAL, status);
        setDepartureTerminal(departureTerminal); setBoardingGate(boardingGate);
        this.customsClearanceRequired = customsClearanceRequired;
    }
    public String getDepartureTerminal() { return departureTerminal; }
    public void setDepartureTerminal(String v) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException("Departure terminal is required.");
        departureTerminal = v.trim();
    }
    public String getBoardingGate() { return boardingGate; }
    public void setBoardingGate(String v) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException("Boarding gate is required.");
        boardingGate = v.trim();
    }
    public boolean isCustomsClearanceRequired() { return customsClearanceRequired; }
    public void setCustomsClearanceRequired(boolean v) { customsClearanceRequired = v; }
    // Adds international details to the information from Flight.
    @Override public String displayDetails() {
        return super.displayDetails() + String.format(" | Terminal: %s | Boarding gate: %s | Customs clearance: %s",
                departureTerminal, boardingGate, customsClearanceRequired ? "Required" : "Not required");
    }
}
