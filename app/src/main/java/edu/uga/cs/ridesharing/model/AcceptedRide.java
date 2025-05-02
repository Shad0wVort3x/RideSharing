package edu.uga.cs.ridesharing.model;
/**
 * Ride that has been accepted by both a driver and a rider.
 * Model stores all key ride details such as date, time, route, and confirmation status.
 */
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
    /**
     * Default constructor required for Firebase.
     */
    public AcceptedRide() {

    }
    /**
     * Gets unique ID of accepted ride.
     * @return Accepted ride ID.
     */
    public String getAcceptedRideID() {
        return acceptedRideID;
    }
    /**
     * Sets  unique ID of this accepted ride.
     * @param acceptedRideID  ride ID to set.
     */
    public void setAcceptedRideID(String acceptedRideID) {
        this.acceptedRideID = acceptedRideID;
    }
    /**
     * Gets  driver's UID.
     * @return UID of driver.
     */
    public String getDriverUID() {
        return driverUID;
    }
    /**
     * Sets  driver's UID.
     * @param driverUID  driver's UID.
     */
    public void setDriverUID(String driverUID) {
        this.driverUID = driverUID;
    }
    /**
     * Gets  rider's UID.
     * @return  UID of rider.
     */
    public String getRiderUID() {
        return riderUID;
    }
    /**
     * Sets rider's UID.
     * @param riderUID rider's UID.
     */
    public void setRiderUID(String riderUID) {
        this.riderUID = riderUID;
    }

    /**
     * Gets date of ride.
     * @return date string.
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets date of ride.
     * @param date ride date.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets time of ride.
     * @return time string
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets time of  ride.
     * @param time ride time.
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Gets starting point of  ride.
     * @return "from" location.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets starting point of ride.
     * @param from "from" location.
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Gets destination of ride.
     * @return  "to" location.
     */
    public String getTo() {
        return to;
    }

    /**
     * Sets destination of ride.
     * @param to  "to" location.
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * Gets point cost of ride.
     * @return  number of points deducted from rider.
     */
    public int getPointsCost() {
        return pointsCost;
    }

    /**
     * Sets point cost of ride.
     * @param pointsCost  number of points to deduct.
     */
    public void setPointsCost(int pointsCost) {
        this.pointsCost = pointsCost;
    }

    /**
     * Checks if driver has confirmed ride.
     * @return True if confirmed by driver, false orwise.
     */
    public boolean isConfirmedByDriver() {
        return confirmedByDriver;
    }

    /**
     * Sets confirmation status for driver.
     * @param confirmedByDriver True if confirmed by driver.
     */
    public void setConfirmedByDriver(boolean confirmedByDriver) {
        this.confirmedByDriver = confirmedByDriver;
    }

    /**
     * Checks if rider has confirmed ride.
     * @return True if confirmed by rider, false orwise.
     */
    public boolean isConfirmedByRider() {
        return confirmedByRider;
    }

    /**
     * Sets confirmation status for rider.
     * @param confirmedByRider True if confirmed by rider.
     */
    public void setConfirmedByRider(boolean confirmedByRider) {
        this.confirmedByRider = confirmedByRider;
    }
}