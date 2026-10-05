package com.mycompany.airport.gate.scheduling;

import java.util.Objects;

// Stores the details that are shared by all flights.
public class Flight {
    private final String flightId;
    private String airlineName;
    private String destination;
    private String departureTime;
    private int passengerCapacity;
    private int bookedPassengers;
    private FlightCategory category;
    private FlightStatus status;

    // Creates a flight and checks each value as it is saved.
    public Flight(String flightId, String airlineName, String destination, String departureTime,
                  int passengerCapacity, int bookedPassengers, FlightCategory category, FlightStatus status) {
        this.flightId = requireText(flightId, "Flight ID");
        setAirlineName(airlineName); setDestination(destination); setDepartureTime(departureTime);
        setPassengerCapacity(passengerCapacity); setBookedPassengers(bookedPassengers);
        setCategory(category); setStatus(status);
    }
    // Stops required text fields from being empty.
    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required.");
        return value.trim();
    }
    public String getFlightId() { return flightId; }
    public String getAirlineName() { return airlineName; }
    public void setAirlineName(String v) { airlineName = requireText(v, "Airline name"); }
    public String getDestination() { return destination; }
    public void setDestination(String v) { destination = requireText(v, "Destination"); }
    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String v) { departureTime = requireText(v, "Departure time"); }
    public int getPassengerCapacity() { return passengerCapacity; }
    public void setPassengerCapacity(int v) {
        if (v < 0) throw new IllegalArgumentException("Capacity cannot be negative.");
        if (v < bookedPassengers) throw new IllegalArgumentException("Capacity cannot be below booked passengers.");
        passengerCapacity = v;
    }
    public int getBookedPassengers() { return bookedPassengers; }
    public void setBookedPassengers(int v) {
        if (v < 0 || v > passengerCapacity) throw new IllegalArgumentException("Booked passengers must be between 0 and capacity.");
        bookedPassengers = v;
    }
    public FlightCategory getCategory() { return category; }
    public void setCategory(FlightCategory v) { category = Objects.requireNonNull(v, "Category is required."); }
    public FlightStatus getStatus() { return status; }
    public void setStatus(FlightStatus v) { status = Objects.requireNonNull(v, "Status is required."); }
    // Returns zero for flights with no passenger seats, such as cargo flights.
    public double getPassengerLoadPercentage() {
        return passengerCapacity == 0 ? 0.0 : bookedPassengers * 100.0 / passengerCapacity;
    }
    // Builds one readable line with the flight information.
    public String displayDetails() {
        return String.format("%s | %s | %s | %s | Capacity: %d | Booked: %d | Load: %.1f%% | %s | %s",
                flightId, airlineName, destination, departureTime, passengerCapacity, bookedPassengers,
                getPassengerLoadPercentage(), category, status);
    }
    @Override public String toString() { return displayDetails(); }
}
