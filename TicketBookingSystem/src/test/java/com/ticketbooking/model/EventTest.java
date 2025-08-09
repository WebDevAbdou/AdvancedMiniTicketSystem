package com.ticketbooking.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Unit tests for Event class
 */
public class EventTest {
    
    @Test
    public void testHasAvailableSeatsWithEnoughSeats() {
        Event event = new Event();
        event.setAvailableSeats(10);
        
        assertTrue(event.hasAvailableSeats(5),
            "Event should have enough seats when requested seats are less than available");
        assertTrue(event.hasAvailableSeats(10),
            "Event should have enough seats when requested seats equal available");
    }
    
    @Test
    public void testHasAvailableSeatsWithNotEnoughSeats() {
        Event event = new Event();
        event.setAvailableSeats(10);
        
        assertFalse(event.hasAvailableSeats(11),
            "Event should not have enough seats when requested seats are more than available");
    }
    
    @Test
    public void testHasAvailableSeatsWithZeroSeats() {
        Event event = new Event();
        event.setAvailableSeats(0);
        
        assertFalse(event.hasAvailableSeats(1),
            "Event with zero available seats should not have enough seats");
        assertTrue(event.hasAvailableSeats(0),
            "Event with zero available seats should have enough seats when zero requested");
    }
    
    @Test
    public void testUpdateAvailableSeatsSuccess() {
        Event event = new Event();
        event.setAvailableSeats(10);
        
        boolean result = event.updateAvailableSeats(5);
        
        assertTrue(result, "Update should be successful when enough seats are available");
        assertEquals(5, event.getAvailableSeats(), "Available seats should be reduced by the booked amount");
    }
    
    @Test
    public void testUpdateAvailableSeatsFailure() {
        Event event = new Event();
        event.setAvailableSeats(10);
        
        boolean result = event.updateAvailableSeats(11);
        
        assertFalse(result, "Update should fail when not enough seats are available");
        assertEquals(10, event.getAvailableSeats(), "Available seats should not change when update fails");
    }
    
    @Test
    public void testUpdateAvailableSeatsToZero() {
        Event event = new Event();
        event.setAvailableSeats(10);
        
        boolean result = event.updateAvailableSeats(10);
        
        assertTrue(result, "Update should be successful when booking all available seats");
        assertEquals(0, event.getAvailableSeats(), "Available seats should be zero after booking all seats");
    }
    
    @Test
    public void testEventConstructor() {
        String name = "Test Event";
        String description = "Test Description";
        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(18, 0);
        String venue = "Test Venue";
        int totalSeats = 100;
        int availableSeats = 100;
        BigDecimal basePrice = new BigDecimal("50.00");
        
        Event event = new Event(name, description, date, time, venue, totalSeats, availableSeats, basePrice);
        
        assertEquals(name, event.getName(), "Event name should match constructor parameter");
        assertEquals(description, event.getDescription(), "Event description should match constructor parameter");
        assertEquals(date, event.getDate(), "Event date should match constructor parameter");
        assertEquals(time, event.getTime(), "Event time should match constructor parameter");
        assertEquals(venue, event.getVenue(), "Event venue should match constructor parameter");
        assertEquals(totalSeats, event.getTotalSeats(), "Event total seats should match constructor parameter");
        assertEquals(availableSeats, event.getAvailableSeats(), "Event available seats should match constructor parameter");
        assertEquals(0, basePrice.compareTo(event.getBasePrice()), "Event base price should match constructor parameter");
    }
    
    @Test
    public void testEventEqualsAndHashCode() {
        Event event1 = new Event(1, "Test Event", "Description", LocalDate.now(), LocalTime.now(),
                                "Venue", 100, 100, new BigDecimal("50.00"));
        Event event2 = new Event(1, "Test Event", "Description", LocalDate.now(), LocalTime.now(),
                                "Venue", 100, 100, new BigDecimal("50.00"));
        Event event3 = new Event(2, "Different Event", "Description", LocalDate.now(), LocalTime.now(),
                                "Venue", 100, 100, new BigDecimal("50.00"));
        
        assertEquals(event1, event2, "Events with same properties should be equal");
        assertNotEquals(event1, event3, "Events with different IDs should not be equal");
        assertEquals(event1.hashCode(), event2.hashCode(), "Equal events should have same hash code");
    }
}
