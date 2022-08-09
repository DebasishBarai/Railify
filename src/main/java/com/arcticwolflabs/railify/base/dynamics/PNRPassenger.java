package com.arcticwolflabs.railify.base.dynamics;


public class PNRPassenger {
    private String passengerIndex;
    private String currentStatus;
    private String bookingBerth;

    public void setPassengerIndex(String _passengerIndex) {
        this.passengerIndex = _passengerIndex;
    }

    public String getPassengerIndex() {
        return passengerIndex;
    }

    public void setCurrentStatus(String _currentStatus) {
        this.currentStatus = _currentStatus;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setBookingBerth(String _bookingBerth) {
        this.bookingBerth = _bookingBerth;
    }

    public String getBookingBerth() {
        return bookingBerth;
    }
}
