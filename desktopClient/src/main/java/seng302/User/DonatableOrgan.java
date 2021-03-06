package seng302.User;

import seng302.generic.WindowManager;
import seng302.User.Attribute.Organ;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.SECONDS;

public class DonatableOrgan {

    private LocalDateTime timeOfDeath;
    private Organ organType;
    private long donorId;
    private int id;
    private Duration timeLeft;
    private String receiverName;
    private String receiverDeathRegion;
    private double timePercent;
    private List<User> topReceivers;
    private boolean expired;
    private int inTransfer;

    public DonatableOrgan(LocalDateTime timeOfExpiry, Organ organType, long donorId, int id, boolean expired, int inTransfer){
        this.timeOfDeath = timeOfExpiry;
        this.donorId = donorId;
        this.organType = organType;
        this.id = id;
        this.expired = expired;
        this.inTransfer = inTransfer;
    }

    public DonatableOrgan(LocalDateTime timeOfExpiry, Organ organType, long donorId, boolean expired, int inTransfer){
        this.timeOfDeath = timeOfExpiry;
        this.donorId = donorId;
        this.organType = organType;
        this.expired = expired;
        this.inTransfer = inTransfer;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public void setReceiverDeathRegion(String receiverDeathRegion) {
        this.receiverDeathRegion = receiverDeathRegion;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverDeathRegion() {
        return receiverDeathRegion;
    }

    public double getTimePercent() {
        return timePercent;
    }

    public LocalDateTime getTimeOfDeath() {
        return timeOfDeath;
    }

    public long getDonorId() {
        return donorId;
    }

    public Organ getOrganType() {
        return organType;
    }

    public int getId() {
        return id;
    }

    public void setDateOfDeath(LocalDateTime timeOfExpiry) {
        this.timeOfDeath = timeOfExpiry;
    }

    public List<User> getTopReceivers() {
        return topReceivers;
    }

    public Duration getTimeLeft(){
        return timeLeft;
    }

    public int getInTransfer() {
        return inTransfer;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setTimeLeft(Duration time) {
        timeLeft = time;
        timePercent = (double) time.toMillis() / (double) getExpiryDuration(organType).toMillis();
    }

    public void tickTimeLeft(){
        timeLeft = timeLeft.minus(1, SECONDS);
        double temp = (double) timeLeft.toMillis() /  (double) getExpiryDuration(organType).toMillis();
        if ((timePercent >= 0.75 && temp < 0.75) || (timePercent >= 0.5 && temp < 0.5) || (timePercent >= 0.25 && temp < 0.25)) {
            timePercent = temp;
            WindowManager.updateAvailableOrgansAutoRefresh(true);
        }
        timePercent = temp;
    }

    /**
     * Returns a duration of how long the organ will last based on the organ type entered.
     * @param organType The organ type being donated
     * @return How long the organ will last
     */
    public Duration getExpiryDuration(Organ organType) {
        Duration duration = null;
        switch(organType){

            case LUNG:
                duration = Duration.parse("PT6H");
                break;
            case HEART:
                duration = Duration.parse("PT6H");
                break;
            case PANCREAS:
                duration = Duration.parse("PT24H");
                break;
            case LIVER:
                duration = Duration.parse("PT24H");
                break;
            case KIDNEY:
                duration = Duration.parse("PT72H");
                break;
            case INTESTINE:
                duration = Duration.parse("PT10H");
                break;
            case CORNEA:
                duration = Duration.parse("P7D");
                break;
            case EAR:
                duration = Duration.parse("P3650D"); //Confirmed by Fabian 14/08
                break;
            case TISSUE:
                duration = Duration.parse("P1825D");
                break;
            case SKIN:
                duration = Duration.parse("P3650D");
                break;
            case BONE:
                duration = Duration.parse("P3650D");
                break;
        }
        return duration;
    }
}
