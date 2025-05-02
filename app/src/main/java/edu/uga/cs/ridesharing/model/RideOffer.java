package edu.uga.cs.ridesharing.model;

/**
 * Represents a ride offer created by a driver.
 * Contains items such as date, time, locations, and  user who accepted  offer.
 */
public class RideOffer {

    private String rideID;
    private String driverUID;
    private String date;
    private String time;
    private String from;
    private String to;
    private String acceptedBy;

    /**
     * Default constructor required for Firebase.
     */
    public RideOffer() {}

    /**
     * Gets unique ID of ride offer.
     * @return  ride ID.
     */
    public String getRideID() {
        return rideID;
    }

    /**
     * Sets unique ID of  ride offer.
     * @param rideID  ride ID to set.
     */
    public void setRideID(String rideID) {
        this.rideID = rideID;
    }

    /**
     * Gets UID of driver who created offer.
     * @return  driver's UID.
     */
    public String getDriverUID() {
        return driverUID;
    }

    /**
     * Sets UID of driver who created offer.
     * @param driverUID  driver's UID.
     */
    public void setDriverUID(String driverUID) {
        this.driverUID = driverUID;
    }

    /**
     * Gets  date of ride offer.
     * @return  ride date
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets  date of ride offer.
     * @param date  ride date.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets  time of ride offer.
     * @return  ride time.
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets  time of ride offer.
     * @param time  ride time.
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Gets  starting point of ride.
     * @return  "from" location.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets starting point of ride.
     * @param from  "from" location.
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
     * Gets UID of user who accepted offer.
     * @return  UID of accepting user, or null .
     */
    public String getAcceptedBy() {
        return acceptedBy;
    }

    /**
     * Sets UID of user who accepted offer.
     * @param acceptedBy  UID of accepting user.
     */
    public void setAcceptedBy(String acceptedBy) {
        this.acceptedBy = acceptedBy;
    }
}
