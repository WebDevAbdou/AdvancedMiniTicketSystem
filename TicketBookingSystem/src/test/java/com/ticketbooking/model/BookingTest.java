package com.ticketbooking.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

/**
 * Unit tests for Booking class
 */
public class BookingTest {
    
    @Test
    public void testCalculateTotalPriceStandard() {
        BigDecimal basePrice = new BigDecimal("50.00");
        String seatType = Booking.SEAT_TYPE_STANDARD;
        int quantity = 2;
        
        BigDecimal expectedTotal = new BigDecimal("100.00");
        BigDecimal actualTotal = Booking.calculateTotalPrice(basePrice, seatType, quantity);
        
        assertEquals(0, expectedTotal.compareTo(actualTotal),
            "Standard seat total price should be basePrice * quantity");
    }
    
    @Test
    public void testCalculateTotalPriceVIP() {
        BigDecimal basePrice = new BigDecimal("50.00");
        String seatType = Booking.SEAT_TYPE_VIP;
        int quantity = 2;
        
        BigDecimal expectedTotal = new BigDecimal("150.00");
        BigDecimal actualTotal = Booking.calculateTotalPrice(basePrice, seatType, quantity);
        
        assertEquals(0, expectedTotal.compareTo(actualTotal),
            "VIP seat total price should be basePrice * 1.5 * quantity");
    }
    
    @Test
    public void testCalculateTotalPricePremium() {
        BigDecimal basePrice = new BigDecimal("50.00");
        String seatType = Booking.SEAT_TYPE_PREMIUM;
        int quantity = 2;
        
        BigDecimal expectedTotal = new BigDecimal("200.00");
        BigDecimal actualTotal = Booking.calculateTotalPrice(basePrice, seatType, quantity);
        
        assertEquals(0, expectedTotal.compareTo(actualTotal),
            "Premium seat total price should be basePrice * 2.0 * quantity");
    }
    
    @Test
    public void testCalculateTotalPriceWithZeroQuantity() {
        BigDecimal basePrice = new BigDecimal("50.00");
        String seatType = Booking.SEAT_TYPE_STANDARD;
        int quantity = 0;
        
        BigDecimal expectedTotal = BigDecimal.ZERO;
        BigDecimal actualTotal = Booking.calculateTotalPrice(basePrice, seatType, quantity);
        
        assertEquals(0, expectedTotal.compareTo(actualTotal),
            "Total price with zero quantity should be zero");
    }
    
    @Test
    public void testCalculateTotalPriceWithNegativeQuantity() {
        BigDecimal basePrice = new BigDecimal("50.00");
        String seatType = Booking.SEAT_TYPE_STANDARD;
        int quantity = -1;
        
        BigDecimal expectedTotal = new BigDecimal("-50.00");
        BigDecimal actualTotal = Booking.calculateTotalPrice(basePrice, seatType, quantity);
        
        assertEquals(0, expectedTotal.compareTo(actualTotal),
            "Total price with negative quantity should be negative");
    }
    
    @Test
    public void testCalculateTotalPriceWithZeroBasePrice() {
        BigDecimal basePrice = BigDecimal.ZERO;
        String seatType = Booking.SEAT_TYPE_STANDARD;
        int quantity = 2;
        
        BigDecimal expectedTotal = BigDecimal.ZERO;
        BigDecimal actualTotal = Booking.calculateTotalPrice(basePrice, seatType, quantity);
        
        assertEquals(0, expectedTotal.compareTo(actualTotal),
            "Total price with zero base price should be zero");
    }
    
    @Test
    public void testCalculateTotalPriceWithNullSeatType() {
        BigDecimal basePrice = new BigDecimal("50.00");
        String seatType = null;
        int quantity = 2;
        
        BigDecimal expectedTotal = new BigDecimal("100.00");
        BigDecimal actualTotal = Booking.calculateTotalPrice(basePrice, seatType, quantity);
        
        assertEquals(0, expectedTotal.compareTo(actualTotal),
            "Total price with null seat type should default to standard pricing");
    }
    
    @Test
    public void testCalculateTotalPriceWithInvalidSeatType() {
        BigDecimal basePrice = new BigDecimal("50.00");
        String seatType = "Invalid";
        int quantity = 2;
        
        BigDecimal expectedTotal = new BigDecimal("100.00");
        BigDecimal actualTotal = Booking.calculateTotalPrice(basePrice, seatType, quantity);
        
        assertEquals(0, expectedTotal.compareTo(actualTotal),
            "Total price with invalid seat type should default to standard pricing");
    }
}
