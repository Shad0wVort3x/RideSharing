package edu.uga.cs.ridesharing.model;

public class RideRequest {
    private String requestID;
    private String riderUID;
    private String date;
    private String time;
    private String from;
    private String to;
    private String acceptedBy;

    public RideRequest() {

    }

    public String getRequestID() { return requestID; }
    public void setRequestID(String requestID) { this.requestID = requestID; }
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
    public String getAcceptedBy() { return acceptedBy; }
    public void setAcceptedBy(String acceptedBy) { this.acceptedBy = acceptedBy; }
}
