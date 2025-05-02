package edu.uga.cs.ridesharing.model;

/**
 * A ride request submitted by a rider.
 * Contains info about the request's origin, destination, timing, and acceptance status.
 */
public class RideRequest {

    private String requestID;
    private String riderUID;
    private String date;
    private String time;
    private String from;
    private String to;
    private String acceptedBy;

    /**
     * Default constructor required for Firebase.
     */
    public RideRequest() {}

    /**
     * Gets unique ID of ride request.
     * @return request ID.
     */
    public String getRequestID() {
        return requestID;
    }

    /**
     * Sets unique ID of ride request.
     * @param requestID request ID to set.
     */
    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    /**
     * Gets UID of rider who made request.
     * @return rider's UID.
     */
    public String getRiderUID() {
        return riderUID;
    }

    /**
     * Sets UID of rider who made request.
     * @param riderUID rider's UID.
     */
    public void setRiderUID(String riderUID) {
        this.riderUID = riderUID;
    }

    /**
     * Gets date of requested ride.
     * @return date string.
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets date of requested ride.
     * @param date date string to set.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets time of requested ride.
     * @return time string.
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets time of requested ride.
     * @param time time string to set.
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Gets starting location of ride.
     * @return origin location.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets starting location of ride.
     * @param from origin location to set.
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Gets destination of ride.
     * @return destination location.
     */
    public String getTo() {
        return to;
    }

    /**
     * Sets destination of ride.
     * @param to destination location to set.
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * Gets UID of the user who accepted this ride request.
     * @return UID of accepting user, or null if not yet accepted.
     */
    public String getAcceptedBy() {
        return acceptedBy;
    }

    /**
     * Sets UID of the user who accepted this ride request.
     * @param acceptedBy UID of accepting user.
     */
    public void setAcceptedBy(String acceptedBy) {
        this.acceptedBy = acceptedBy;
    }
}
