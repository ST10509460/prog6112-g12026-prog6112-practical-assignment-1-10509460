package com.mycompany.airport.gate.scheduling;

import java.util.List;
import java.util.Scanner;

// Shows the menu and reads choices from the keyboard.
public class Main {
    private static final Scanner IN = new Scanner(System.in);
    private static final AirportSystem SYSTEM = new AirportSystem();
    // Repeats the menu until the user chooses Exit.
    public static void main(String[] args) {
        System.out.println("=== AeroLink Airport Gate Scheduling ===");
        boolean running = true;
        while (running) {
            System.out.println("\n1 Register flight  2 Search flight  3 Update flight  4 Delete flight\n"+
                    "5 List flights     6 Allocate gate  7 Release gate  8 Reschedule flight\n"+
                    "9 Display schedule 10 Available slots 11 Occupied slots 12 Reports\n0 Exit");
            try {
                switch (readInt("Choice: ")) {
                    case 1 -> register(); case 2 -> search(); case 3 -> update(); case 4 -> delete();
                    case 5 -> printFlights(SYSTEM.getAllFlights()); case 6 -> allocate(); case 7 -> release();
                    case 8 -> reschedule(); case 9 -> System.out.print(SYSTEM.getGateSchedule().displaySchedule());
                    case 10 -> SYSTEM.getGateSchedule().availableSlots().forEach(System.out::println);
                    case 11 -> SYSTEM.getGateSchedule().occupiedSlots().forEach(System.out::println);
                    case 12 -> reports(); case 0 -> running = false;
                    default -> System.out.println("Choose a listed option.");
                }
            } catch (RuntimeException ex) { System.out.println("Operation could not be completed: " + ex.getMessage()); }
        }
        System.out.println("Goodbye.");
    }
    private static String read(String prompt) { System.out.print(prompt); return IN.nextLine().trim(); }
    private static int readInt(String prompt) { return Integer.parseInt(read(prompt)); }
    private static <E extends Enum<E>> E readEnum(String prompt, Class<E> type) {
        return Enum.valueOf(type, read(prompt).toUpperCase().replace(' ', '_'));
    }
    // Reads flight details and creates the correct type of flight.
    private static void register() {
        String id = read("Flight ID: "), airline = read("Airline: "), dest = read("Destination: "), time = read("Departure time (HH:mm): ");
        int cap = readInt("Passenger capacity (0 allowed for cargo): "), booked = readInt("Booked passengers: ");
        FlightCategory category = readEnum("Category (DOMESTIC, INTERNATIONAL, CARGO): ", FlightCategory.class);
        FlightStatus status = readEnum("Status (SCHEDULED, BOARDING, DELAYED, DEPARTED, CANCELLED): ", FlightStatus.class);
        Flight f;
        if (category == FlightCategory.INTERNATIONAL) {
            String terminal = read("Departure terminal: "), boardingGate = read("Boarding gate label: ");
            boolean customs = Boolean.parseBoolean(read("Customs clearance required? (true/false): "));
            f = new InternationalFlight(id, airline, dest, time, cap, booked, status, terminal, boardingGate, customs);
        } else f = new Flight(id, airline, dest, time, cap, booked, category, status);
        SYSTEM.registerFlight(f); System.out.println("Flight registered.");
    }
    private static void search() { Flight f = SYSTEM.findFlight(read("Flight ID: ")); System.out.println(f == null ? "Flight not found." : f.displayDetails()); }
    private static void update() {
        String id = read("Flight ID to update: ");
        if (SYSTEM.findFlight(id) == null) { System.out.println("Flight not found."); return; }
        SYSTEM.updateFlight(id, read("Airline: "), read("Destination: "), read("Departure time (HH:mm): "),
                readInt("Capacity: "), readInt("Booked passengers: "), readEnum("Category: ", FlightCategory.class),
                readEnum("Status: ", FlightStatus.class)); System.out.println("Flight updated.");
    }
    private static void delete() { System.out.println(SYSTEM.deleteFlight(read("Flight ID: ")) ? "Flight deleted." : "Flight not found."); }
    private static void allocate() { SYSTEM.allocateGate(read("Flight ID: "), readInt("Gate (1-5): "), readInt("Time slot (1-8): ")); System.out.println("Gate allocated."); }
    private static void release() { System.out.println(SYSTEM.releaseGate(read("Flight ID: ")) ? "Gate released." : "Flight had no gate allocation."); }
    private static void reschedule() { SYSTEM.rescheduleFlight(read("Flight ID: "), readInt("New gate (1-5): "), readInt("New slot (1-8): ")); System.out.println("Flight rescheduled."); }
    // Shows flight lists and summary information about airport use.
    private static void reports() {
        System.out.println("Scheduled flights:"); printFlights(SYSTEM.scheduledFlights());
        System.out.println("Delayed flights:"); printFlights(SYSTEM.delayedFlights());
        System.out.println("Busiest gate: " + SYSTEM.busiestGate());
        System.out.println("Busiest departure slot: " + SYSTEM.busiestTimeSlot());
        System.out.println("Highest passenger load: " + SYSTEM.highestPassengerLoadFlight().map(Flight::displayDetails).orElse("No passenger flights"));
        System.out.printf("Average passenger load: %.1f%%%n", SYSTEM.averagePassengerLoadPercentage());
        System.out.println("Sort flights by: 1 ID, 2 departure time");
        List<Flight> sorted = readInt("Choice: ") == 2 ? SYSTEM.sortByDepartureTime() : SYSTEM.sortByFlightId(); printFlights(sorted);
    }
    private static void printFlights(List<Flight> flights) { if (flights.isEmpty()) System.out.println("(none)"); else flights.forEach(f -> System.out.println(f.displayDetails())); }
}
