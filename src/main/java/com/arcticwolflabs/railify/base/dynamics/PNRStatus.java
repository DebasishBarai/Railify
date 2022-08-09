package com.arcticwolflabs.railify.base.dynamics;

import java.util.ArrayList;

public class PNRStatus {
    private long pnr_num;
    private String trainNo;
    private String trainName;
    private String trainJourneyDate;
    private String destination;
    private String embarkPoint;
    private String boardingPoint;
    private String ticketClass;
    private String chartStatus;
    private ArrayList<PNRPassenger> passengers;

    public void setPnr_num(long _pnr_num) {
        this.pnr_num = _pnr_num;
    }

    public long getPnr_num() {
        return pnr_num;
    }

    public void setTrainNo(String _trainNo) {
        this.trainNo = _trainNo;
    }

    public String getTrainNo() {
        return trainNo;
    }

    public void setTrainName(String _trainName) {
        this.trainName = _trainName;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainJourneyDate(String _trainJourneyDate) {
        this.trainJourneyDate = _trainJourneyDate;
    }

    public String getTrainJourneyDate() {
        return trainJourneyDate;
    }

    public void setDestination(String _destination) {
        this.destination = _destination;
    }

    public String getDestination() {
        return destination;
    }

    public void setEmbarkPoint(String _embarkPoint) {
        this.embarkPoint = _embarkPoint;
    }

    public String getEmbarkPoint() {
        return embarkPoint;
    }

    public void setBoardingPoint(String _boardingPoint) {
        this.boardingPoint = _boardingPoint;
    }

    public String getBoardingPoint() {
        return boardingPoint;
    }

    public void setTicketClass(String _ticketClass) {
        this.ticketClass = _ticketClass;
    }

    public String getTicketClass() {
        return ticketClass;
    }

    public void setChartStatus(String _chartStatus) {
        this.chartStatus = _chartStatus;
    }

    public String getChartStatus() {
        return chartStatus;
    }

    public void setPassengers(ArrayList<PNRPassenger> _passengers) {
        this.passengers = _passengers;
    }

    public ArrayList<PNRPassenger> getPassengers() {
        return passengers;
    }
}

