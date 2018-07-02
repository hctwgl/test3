package com.ald.fanbei.api.dal.domain.dto;

public class BoluomeAirplaneFlightDto {

    private String arrivalAirport;
    private String arrivalCity;
    private String arrivalDate;
    private String arrivalTime;
    private String cabinType;
    private String carrierName;
    private String correct;
    private String departureAirport;
    private String departureCity;
    private String departureDate;
    private String departureTerminal;
    private String departureTime;
    private String duration;
    private String flightNum;
    private String flightTypeFullName;

    public String getArrivalAirport() {
	return arrivalAirport;
    }

    public void setArrivalAirport(String arrivalAirport) {
	this.arrivalAirport = arrivalAirport;
    }

    public String getArrivalCity() {
	return arrivalCity;
    }

    public void setArrivalCity(String arrivalCity) {
	this.arrivalCity = arrivalCity;
    }

    public String getArrivalDate() {
	return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
	this.arrivalDate = arrivalDate;
    }

    public String getArrivalTime() {
	return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
	this.arrivalTime = arrivalTime;
    }

    public String getCabinType() {
	return cabinType;
    }

    public void setCabinType(String cabinType) {
	this.cabinType = cabinType;
    }

    public String getCarrierName() {
	return carrierName;
    }

    public void setCarrierName(String carrierName) {
	this.carrierName = carrierName;
    }

    public String getCorrect() {
	return correct;
    }

    public void setCorrect(String correct) {
	this.correct = correct;
    }

    public String getDepartureAirport() {
	return departureAirport;
    }

    public void setDepartureAirport(String departureAirport) {
	this.departureAirport = departureAirport;
    }

    public String getDepartureCity() {
	return departureCity;
    }

    public void setDepartureCity(String departureCity) {
	this.departureCity = departureCity;
    }

    public String getDepartureDate() {
	return departureDate;
    }

    public void setDepartureDate(String departureDate) {
	this.departureDate = departureDate;
    }

    public String getDepartureTerminal() {
	return departureTerminal;
    }

    public void setDepartureTerminal(String departureTerminal) {
	this.departureTerminal = departureTerminal;
    }

    public String getDepartureTime() {
	return departureTime;
    }

    public void setDepartureTime(String departureTime) {
	this.departureTime = departureTime;
    }

    public String getDuration() {
	return duration;
    }

    public void setDuration(String duration) {
	this.duration = duration;
    }

    public String getFlightNum() {
	return flightNum;
    }

    public void setFlightNum(String flightNum) {
	this.flightNum = flightNum;
    }

    public String getFlightTypeFullName() {
	return flightTypeFullName;
    }

    public void setFlightTypeFullName(String flightTypeFullName) {
	this.flightTypeFullName = flightTypeFullName;
    }

    @Override
    public String toString() {
	return "BoluomeAirplaneFlightDto [arrivalAirport=" + arrivalAirport + ", arrivalCity=" + arrivalCity + ", arrivalDate=" + arrivalDate + ", arrivalTime=" + arrivalTime + ", cabinType=" + cabinType + ", carrierName=" + carrierName + ", correct=" + correct + ", departureAirport=" + departureAirport + ", departureCity=" + departureCity + ", departureDate=" + departureDate + ", departureTerminal=" + departureTerminal + ", departureTime=" + departureTime + ", duration=" + duration + ", flightNum=" + flightNum + ", flightTypeFullName=" + flightTypeFullName + "]";
    }

}
