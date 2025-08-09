package com.ticketbooking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a booking in the ticket booking system
 */
public class Booking {
    private int id;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private int eventId;
    private String seatType;
    private int quantity;
    private BigDecimal totalPrice;
    private LocalDateTime bookingTime;
    
    // For displaying event details in booking view
    private transient Event event;

    // Seat type constants
    public static final String SEAT_TYPE_STANDARD = "Standard";
    public static final String SEAT_TYPE_VIP = "VIP";
    public static final String SEAT_TYPE_PREMIUM = "Premium";
    
    // Price multipliers for different seat types
    public static final BigDecimal STANDARD_MULTIPLIER = BigDecimal.ONE;
    public static final BigDecimal VIP_MULTIPLIER = new BigDecimal("1.5");
    public static final BigDecimal PREMIUM_MULTIPLIER = new BigDecimal("2.0");

    // Default constructor
    public Booking() {
        this.bookingTime = LocalDateTime.now();
    }

    // Constructor with all fields except id and bookingTime
    public Booking(String customerName, String customerEmail, String customerPhone, 
                  int eventId, String seatType, int quantity, BigDecimal totalPrice) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.eventId = eventId;
        this.seatType = seatType;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.bookingTime = LocalDateTime.now();
    }

    // Constructor with all fields including id
    public Booking(int id, String customerName, String customerEmail, String customerPhone, 
                  int eventId, String seatType, int quantity, BigDecimal totalPrice, 
                  LocalDateTime bookingTime) {
        this.id = id;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.eventId = eventId;
        this.seatType = seatType;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.bookingTime = bookingTime;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * Calculate total price based on event base price, seat type, and quantity
     * @param basePrice the base price of the event
     * @param seatType the type of seat
     * @param quantity the number of tickets
     * @return the calculated total price
     */
    public static BigDecimal calculateTotalPrice(BigDecimal basePrice, String seatType, int quantity) {
        BigDecimal multiplier;
        
        switch (seatType) {
            case SEAT_TYPE_VIP:
                multiplier = VIP_MULTIPLIER;
                break;
            case SEAT_TYPE_PREMIUM:
                multiplier = PREMIUM_MULTIPLIER;
                break;
            case SEAT_TYPE_STANDARD:
            default:
                multiplier = STANDARD_MULTIPLIER;
                break;
        }
        
        return basePrice.multiply(multiplier).multiply(new BigDecimal(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return id == booking.id &&
               eventId == booking.eventId &&
               quantity == booking.quantity &&
               Objects.equals(customerName, booking.customerName) &&
               Objects.equals(customerEmail, booking.customerEmail) &&
               Objects.equals(customerPhone, booking.customerPhone) &&
               Objects.equals(seatType, booking.seatType) &&
               Objects.equals(totalPrice, booking.totalPrice) &&
               Objects.equals(bookingTime, booking.bookingTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerName, customerEmail, customerPhone, eventId, 
                           seatType, quantity, totalPrice, bookingTime);
    }

    @Override
    public String toString() {
        return "Booking{" +
               "id=" + id +
               ", customerName='" + customerName + '\'' +
               ", eventId=" + eventId +
               ", seatType='" + seatType + '\'' +
               ", quantity=" + quantity +
               ", totalPrice=" + totalPrice +
               ", bookingTime=" + bookingTime +
               '}';
    }
}
