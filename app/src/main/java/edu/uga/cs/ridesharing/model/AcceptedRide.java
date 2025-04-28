package edu.uga.cs.ridesharing.model;

public class AcceptedRide {
    private String acceptedRideID;
    private String driverUID;
    private String riderUID;
    private String date;
    private String time;
    private String from;
    private String to;
    private int pointsCost;
    private boolean confirmedByDriver;
    private boolean confirmedByRider;

    public AcceptedRide() {
        // Empty constructor needed for Firebase
    }

    public String getAcceptedRideID() {
        return acceptedRideID;
    }
    public void setAcceptedRideID(String acceptedRideID) {
        this.acceptedRideID = acceptedRideID;
    }
    public String getDriverUID() { return driverUID; }
    public void setDriverUID(String driverUID) { this.driverUID = driverUID; }
    public String getRiderUID() { return riderUID; }
    public void setRiderUID(String riderUID) { this.riderUID = riderUID; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
    public int getPointsCost() { return pointsCost; }
    public void setPointsCost(int pointsCost) { this.pointsCost = pointsCost; }
    public boolean isConfirmedByDriver() { return confirmedByDriver; }
    public void setConfirmedByDriver(boolean confirmedByDriver) { this.confirmedByDriver = confirmedByDriver; }
    public boolean isConfirmedByRider() { return confirmedByRider; }
    public void setConfirmedByRider(boolean confirmedByRider) { this.confirmedByRider = confirmedByRider; }
}
