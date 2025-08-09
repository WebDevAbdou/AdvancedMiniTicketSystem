package com.ticketbooking.controller;

import com.ticketbooking.dao.EventDAO;
import com.ticketbooking.model.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Controller for Event-related operations
 */
public class EventController {
    private static final Logger logger = LogManager.getLogger(EventController.class);
    private final EventDAO eventDAO;
    
    public EventController() {
        this.eventDAO = new EventDAO();
    }
    
    /**
     * Get all events
     * @return List of all events
     */
    public List<Event> getAllEvents() {
        logger.info("Getting all events");
        return eventDAO.getAllEvents();
    }
    
    /**
     * Get events by date range
     * @param startDate start date of the range
     * @param endDate end date of the range
     * @return List of events within the date range
     */
    public List<Event> getEventsByDateRange(LocalDate startDate, LocalDate endDate) {
        logger.info("Getting events between {} and {}", startDate, endDate);
        return eventDAO.getEventsByDateRange(startDate, endDate);
    }
    
    /**
     * Get event by ID
     * @param id event ID
     * @return Event object if found, null otherwise
     */
    public Event getEventById(int id) {
        logger.info("Getting event with ID: {}", id);
        return eventDAO.getEventById(id);
    }
    
    /**
     * Create a new event
     * @param name event name
     * @param description event description
     * @param date event date
     * @param time event time
     * @param venue event venue
     * @param totalSeats total number of seats
     * @param basePrice base price per ticket
     * @return true if successful, false otherwise
     */
    public boolean createEvent(String name, String description, LocalDate date, LocalTime time, 
                              String venue, int totalSeats, BigDecimal basePrice) {
        logger.info("Creating new event: {}", name);
        
        // Validate input
        if (name == null || name.trim().isEmpty()) {
            logger.error("Event name cannot be empty");
            return false;
        }
        
        if (date == null || date.isBefore(LocalDate.now())) {
            logger.error("Event date must be in the future");
            return false;
        }
        
        if (time == null) {
            logger.error("Event time cannot be null");
            return false;
        }
        
        if (venue == null || venue.trim().isEmpty()) {
            logger.error("Event venue cannot be empty");
            return false;
        }
        
        if (totalSeats <= 0) {
            logger.error("Total seats must be greater than zero");
            return false;
        }
        
        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Base price must be greater than zero");
            return false;
        }
        
        Event event = new Event(name, description, date, time, venue, totalSeats, totalSeats, basePrice);
        return eventDAO.addEvent(event);
    }
    
    /**
     * Update an existing event
     * @param id event ID
     * @param name event name
     * @param description event description
     * @param date event date
     * @param time event time
     * @param venue event venue
     * @param totalSeats total number of seats
     * @param availableSeats available number of seats
     * @param basePrice base price per ticket
     * @return true if successful, false otherwise
     */
    public boolean updateEvent(int id, String name, String description, LocalDate date, LocalTime time, 
                              String venue, int totalSeats, int availableSeats, BigDecimal basePrice) {
        logger.info("Updating event with ID: {}", id);
        
        // Get existing event
        Event existingEvent = eventDAO.getEventById(id);
        if (existingEvent == null) {
            logger.error("Event with ID {} not found", id);
            return false;
        }
        
        // Validate input
        if (name == null || name.trim().isEmpty()) {
            logger.error("Event name cannot be empty");
            return false;
        }
        
        if (date == null) {
            logger.error("Event date cannot be null");
            return false;
        }
        
        if (time == null) {
            logger.error("Event time cannot be null");
            return false;
        }
        
        if (venue == null || venue.trim().isEmpty()) {
            logger.error("Event venue cannot be empty");
            return false;
        }
        
        if (totalSeats <= 0) {
            logger.error("Total seats must be greater than zero");
            return false;
        }
        
        if (availableSeats < 0 || availableSeats > totalSeats) {
            logger.error("Available seats must be between 0 and total seats");
            return false;
        }
        
        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Base price must be greater than zero");
            return false;
        }
        
        // Update event
        existingEvent.setName(name);
        existingEvent.setDescription(description);
        existingEvent.setDate(date);
        existingEvent.setTime(time);
        existingEvent.setVenue(venue);
        existingEvent.setTotalSeats(totalSeats);
        existingEvent.setAvailableSeats(availableSeats);
        existingEvent.setBasePrice(basePrice);
        
        return eventDAO.updateEvent(existingEvent);
    }
    
    /**
     * Delete an event
     * @param id event ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteEvent(int id) {
        logger.info("Deleting event with ID: {}", id);
        return eventDAO.deleteEvent(id);
    }
    
    /**
     * Check if an event has enough available seats
     * @param eventId event ID
     * @param requestedSeats number of seats requested
     * @return true if enough seats are available, false otherwise
     */
    public boolean hasAvailableSeats(int eventId, int requestedSeats) {
        Event event = eventDAO.getEventById(eventId);
        if (event == null) {
            logger.error("Event with ID {} not found", eventId);
            return false;
        }
        
        return event.hasAvailableSeats(requestedSeats);
    }
}
