package edu.uga.cs.ridesharing.model;

public class RideOffer {
    private String rideID;
    private String driverUID;
    private String date;
    private String time;
    private String from;
    private String to;
    private String acceptedBy;

    public RideOffer() {
        // empty constructor needed for Firebase
    }

    // Getters and Setters
    public String getRideID() { return rideID; }
    public void setRideID(String rideID) { this.rideID = rideID; }
    public String getDriverUID() { return driverUID; }
    public void setDriverUID(String driverUID) { this.driverUID = driverUID; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
    public String getAcceptedBy() { return acceptedBy; }
    public void setAcceptedBy(String acceptedBy) { this.acceptedBy = acceptedBy; }
}
