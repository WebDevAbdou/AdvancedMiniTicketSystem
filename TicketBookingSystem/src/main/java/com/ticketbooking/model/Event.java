package com.ticketbooking.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Represents an event in the ticket booking system
 */
public class Event {
    private int id;
    private String name;
    private String description;
    private LocalDate date;
    private LocalTime time;
    private String venue;
    private int totalSeats;
    private int availableSeats;
    private BigDecimal basePrice;

    // Default constructor
    public Event() {
    }

    // Constructor with all fields except id
    public Event(String name, String description, LocalDate date, LocalTime time, 
                 String venue, int totalSeats, int availableSeats, BigDecimal basePrice) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.basePrice = basePrice;
    }

    // Constructor with all fields including id
    public Event(int id, String name, String description, LocalDate date, LocalTime time, 
                 String venue, int totalSeats, int availableSeats, BigDecimal basePrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.basePrice = basePrice;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    /**
     * Check if the event has enough available seats
     * @param requestedSeats number of seats requested
     * @return true if enough seats are available, false otherwise
     */
    public boolean hasAvailableSeats(int requestedSeats) {
        return availableSeats >= requestedSeats;
    }

    /**
     * Update available seats after a booking
     * @param bookedSeats number of seats booked
     * @return true if update was successful, false otherwise
     */
    public boolean updateAvailableSeats(int bookedSeats) {
        if (hasAvailableSeats(bookedSeats)) {
            availableSeats -= bookedSeats;
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id == event.id &&
               totalSeats == event.totalSeats &&
               availableSeats == event.availableSeats &&
               Objects.equals(name, event.name) &&
               Objects.equals(description, event.description) &&
               Objects.equals(date, event.date) &&
               Objects.equals(time, event.time) &&
               Objects.equals(venue, event.venue) &&
               Objects.equals(basePrice, event.basePrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, date, time, venue, totalSeats, availableSeats, basePrice);
    }

    @Override
    public String toString() {
        return "Event{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", date=" + date +
               ", time=" + time +
               ", venue='" + venue + '\'' +
               ", availableSeats=" + availableSeats +
               ", basePrice=" + basePrice +
               '}';
    }
}
