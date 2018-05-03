package seng302.User;

import seng302.User.Attribute.LoginType;
import seng302.Generic.IO;

/**
 * This class contains information about clinicians.
 */
public class Clinician {
    private String name, workAddress, region, username, password;
    private long staffID;
    private LoginType accountType;

    public Clinician(String username, String password, String name){
        this.username = username;
        this.password = password;
        this.name = name;
        this.staffID = IO.getNextId(true, LoginType.CLINICIAN);
        this.region = null;
        this.workAddress = null;
        this.accountType = LoginType.CLINICIAN;
    }

    /**
     * Used by Admin to pick its own ID
     * @param username The username of the clinician
     * @param password The clinician's password
     * @param name The clinician's name
     * @param accountType The type of account
     * @param staffID The clinician's ID
     */
    public Clinician(String username, String password, String name, LoginType accountType, long staffID){
        this.username = username;
        this.password = password;
        this.name = name;
        this.staffID = staffID;
        this.region = null;
        this.workAddress = null;
        this.accountType = accountType;
    }

    /**
     * Used by Admin to pick its own ID
     * @param username The username of the clinician
     * @param password The clinician's password
     * @param name The clinician's name
     * @param accountType The type of account
     */
    public Clinician(String username, String password, String name, LoginType accountType){
        this.username = username;
        this.password = password;
        this.name = name;
        this.staffID = IO.getNextId(true, LoginType.CLINICIAN);
        this.region = null;
        this.workAddress = null;
        this.accountType = accountType;
    }

    /**
     * Constructor used when making a deep copy of a clinician.
     * @param clinician the original clinician object being copied.
     */
    public Clinician(Clinician clinician){
        this.username = clinician.username;
        this.password = clinician.password;
        this.name = clinician.name;
        this.staffID = clinician.staffID;
        this.region = clinician.region;
        this.workAddress = clinician.workAddress;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public long getStaffID() { return staffID; }

    public String getWorkAddress() { return workAddress; }

    public void setWorkAddress(String workAddress) { this.workAddress = workAddress; }

    public String getRegion() { return region; }

    public void setRegion(String region) { this.region = region; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public void copyFieldsFrom(Clinician clinician) {
        this.name = clinician.name;
        this.region = clinician.region;
        this.workAddress = clinician.workAddress;
    }

    @Override
    public String toString() {
        return "Clinician{" +
                "name='" + name + '\'' +
                ", staffID='" + staffID + '\'' +
                ", workAddress='" + workAddress + '\'' +
                ", region='" + region + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
