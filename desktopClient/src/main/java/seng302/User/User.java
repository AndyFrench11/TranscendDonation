package seng302.User;

import seng302.generic.Debugger;
import seng302.User.Attribute.*;
import seng302.User.Medication.Medication;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * This class contains information about organ users.
 */
public class User {
    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy"), dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");
    private String[] name, preferredName;
    private LocalDate dateOfBirth = null;
    private LocalDateTime dateOfDeath, creationTime, lastModified = null;
    private Gender gender = null, genderIdentity = null;
    private double height = -1, weight = -1;
    private BloodType bloodType = null;
    private long id;
    private String nhi;
    private EnumSet<Organ> organs = EnumSet.noneOf(Organ.class);
    private int zipCode=0;
    private String currentAddress = "", region = "", city="", country="", homePhone="", mobilePhone="", username, email, passphrase, bloodPressure = "", profileImageType = "";
    private SmokerStatus smokerStatus;
    private AlcoholConsumption alcoholConsumption;
    private ArrayList<Medication> currentMedications = new ArrayList<>(), historicMedications = new ArrayList<>();
    private ArrayList<Disease> currentDiseases = new ArrayList<>(), curedDiseases = new ArrayList<>();
    private ArrayList<Procedure> pendingProcedures = new ArrayList<>(), previousProcedures = new ArrayList<>();
    private ArrayList<WaitingListItem> waitingListItems = new ArrayList<>();
    private ArrayList<HistoryItem> userHistory = new ArrayList<>();

    private String cityOfDeath = "";
    private String regionOfDeath = "";
    private String countryOfDeath = "";

    public User(String name, LocalDate dateOfBirth) {
        this.name = name.split(",");
        this.preferredName = this.name;
        this.dateOfBirth = dateOfBirth;
        this.creationTime = LocalDateTime.now();
        // TODO Add functionality to DAOs for getting next id.
        this.id = 1;
    }

    public User(String firstName, String[] middleNames, String lastName, LocalDate dateOfBirth, String username, String email, String nhi, String passphrase) {
        int isLastName = lastName == null || lastName.isEmpty() ? 0 : 1;
        this.name = new String[1 + middleNames.length + isLastName];
        this.name[0] = firstName;
        System.arraycopy(middleNames, 0, this.name, 1, middleNames.length);
        if (isLastName == 1) {
            this.name[this.name.length - 1] = lastName;
        }
        this.preferredName = this.name;
        this.dateOfBirth = dateOfBirth;
        this.creationTime = LocalDateTime.now();
        this.username = username;
        this.email = email;
        this.passphrase = passphrase;
        // TODO Add functionality to DAOs for getting next id.
        this.id = 1;
        this.nhi = nhi;
    }

    // Used by CSV import to form profiles
    public User(String firstName, String lastNames, LocalDate dateOfBirth, LocalDateTime dateOfDeath, Gender gender,
                Gender genderIdentity, BloodType bloodType, int height, int weight, String address, String region,
                String city, int zipCode, String country, String homePhone, String mobilePhone, String email, String nhi) {
        int isLastName = lastNames == null || lastNames.isEmpty() ? 0 : 1;
        this.name = new String[1 + isLastName];
        this.name[0] = firstName;
        if (isLastName == 1) {
            this.name[this.name.length - 1] = lastNames;
        }
        this.username = email;

        this.preferredName = this.name;
        this.dateOfBirth = dateOfBirth;
        this.dateOfDeath = dateOfDeath;

        this.creationTime = LocalDateTime.now();
        this.gender = gender;
        this.genderIdentity = genderIdentity;
        this.bloodType = bloodType;

        // Uses Java automatic type recognition to convert int -> double
        this.height = 1.0 * height;
        this.weight = 1.0 * weight;

        this.currentAddress = address;
        this.region = region;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;

        this.homePhone = homePhone;
        this.mobilePhone = mobilePhone;

        this.email = email;
        this.passphrase = "passphrase";
        // TODO Add functionality to DAOs for getting next id.
        this.id = 1;
        this.nhi= nhi;
    }

    public User(String firstName, String[] middleNames, String lastName, LocalDate dateOfBirth, LocalDateTime dateOfDeath, Gender gender, double height,
                double weight, BloodType bloodType, String region, String currentAddress, String username, String email, String nhi, String passphrase, String country, String cityOfDeath, String regionOfDeath, String countryOfdeath) {
        int isLastName = lastName == null || lastName.isEmpty() ? 0 : 1;
        int lenMiddleNames = middleNames == null ? 0 : middleNames.length;
        this.name = new String[1 + lenMiddleNames + isLastName];
        this.name[0] = firstName;
        if (middleNames != null) {
            System.arraycopy(middleNames, 0, this.name, 1, lenMiddleNames);
        }
        if (isLastName == 1) {
            this.name[this.name.length - 1] = lastName;
        }
        this.preferredName = this.name;
        this.dateOfBirth = dateOfBirth;
        this.dateOfDeath = dateOfDeath;
        this.gender = gender;
        this.genderIdentity = gender;
        this.height = height;
        this.weight = weight;
        this.bloodType = bloodType;
        this.region = region;
        this.currentAddress = currentAddress;
        this.creationTime = LocalDateTime.now();
        this.username = username;
        this.email = email;
        this.nhi = nhi;
        this.passphrase = passphrase;
        this.country = country;
        this.cityOfDeath = cityOfDeath;
        this.regionOfDeath = regionOfDeath;
        this.countryOfDeath = countryOfdeath;
        this.id = 1;
        this.currentMedications = new ArrayList<>();
        this.historicMedications = new ArrayList<>();
        this.waitingListItems = new ArrayList<>();
        this.currentDiseases = new ArrayList<>();
        this.curedDiseases = new ArrayList<>();
        this.pendingProcedures = new ArrayList<>();
        this.previousProcedures = new ArrayList<>();
    }



    /**
     * Used to create a deep copy of the object. Does not copy username, passphrase, or email.
     *
     * @param user The user to make a copy of
     */
    public User(User user) {
        this.name = user.name;
        this.preferredName = user.preferredName;
        this.dateOfBirth = user.dateOfBirth;
        this.dateOfDeath = user.dateOfDeath;
        this.gender = user.gender;
        this.genderIdentity = user.genderIdentity;
        this.height = user.height;
        this.weight = user.weight;
        this.bloodType = user.bloodType;
        this.region = user.region;
        this.country = user.country;
        this.currentAddress = user.currentAddress;
        this.creationTime = user.creationTime;
        this.id = user.id;
        this.nhi = user.nhi;
        this.smokerStatus = user.smokerStatus;
        this.bloodPressure = user.bloodPressure;
        this.alcoholConsumption = user.alcoholConsumption;
        this.organs.addAll(user.organs);
        this.countryOfDeath = user.countryOfDeath;
        this.regionOfDeath = user.regionOfDeath;
        this.cityOfDeath = user.cityOfDeath;
        this.currentMedications.addAll(user.currentMedications);
        this.historicMedications.addAll(user.historicMedications);
        this.waitingListItems.addAll(user.waitingListItems);
        this.currentDiseases.addAll(user.getCurrentDiseases());
        this.curedDiseases.addAll(user.getCuredDiseases());
        this.pendingProcedures.addAll(user.getPendingProcedures());
        this.previousProcedures.addAll(user.getPreviousProcedures());
        this.profileImageType = user.profileImageType;
    }

    public void copyFieldsFrom(User user) {
        nhi = user.getNhi();
        name = user.getNameArray();
        preferredName = user.getPreferredNameArray();
        dateOfBirth = user.getDateOfBirth();
        dateOfDeath = user.getDateOfDeath();
        gender = user.getGender();
        genderIdentity = user.getGenderIdentity();
        bloodType = user.getBloodType();
        height = user.getHeight();
        weight = user.getWeight();
        region = user.getRegion();
        country = user.getCountry();
        currentAddress = user.getCurrentAddress();
        smokerStatus = user.getSmokerStatus();
        bloodPressure = user.getBloodPressure();
        alcoholConsumption = user.getAlcoholConsumption();
        organs.clear();
        organs.addAll(user.getOrgans());
        countryOfDeath = user.getCountryOfDeath();
        regionOfDeath = user.getRegionOfDeath();
        cityOfDeath = user.getCityOfDeath();
    }

    public void copyMedicationListsFrom(User user) {
        currentMedications.clear();
        currentMedications.addAll(user.getCurrentMedications());
        historicMedications.clear();
        historicMedications.addAll(user.getHistoricMedications());
    }

    public void copyProceduresListsFrom(User user) {
        pendingProcedures.clear();
        pendingProcedures.addAll(user.getPendingProcedures());

        previousProcedures.clear();
        previousProcedures.addAll(user.getPreviousProcedures());
    }

    public void copyDiseaseListsFrom(User user) {
        currentDiseases.clear();
        currentDiseases.addAll(user.getCurrentDiseases());

        curedDiseases.clear();
        curedDiseases.addAll(user.getCuredDiseases());
    }

    /**
     * Copies all items in the given users waiting list and adds them to the current user.
     *
     * @param user the user being copied.
     */
    public void copyWaitingListsFrom(User user) {
        waitingListItems.clear();
        waitingListItems.addAll(user.getWaitingListItems());
    }

    /**
     * Checks whether there is any difference between the attributes of this user and a given user. Does NOT compare username, passphrase, email, or any lists.
     *
     * @param user The user to compare to
     * @return Whether they are equal
     */
    public boolean attributeFieldsEqual(User user) {
        return (Arrays.equals(name, user.getNameArray()) &&
                Arrays.equals(preferredName, user.getPreferredNameArray()) &&
                dateOfBirth == user.getDateOfBirth() &&
                dateOfDeath == user.getDateOfDeath() &&
                gender == user.getGender() &&
                genderIdentity == user.genderIdentity &&
                bloodType == user.getBloodType() &&
                height == user.getHeight() &&
                weight == user.getWeight() &&
                stringEqual(region, user.getRegion()) &&
                stringEqual(currentAddress, user.getCurrentAddress()) &&
                smokerStatus == user.getSmokerStatus() &&
                stringEqual(bloodPressure, user.getBloodPressure()) &&
                alcoholConsumption == user.getAlcoholConsumption() &&
                organs.equals(user.getOrgans()) &&
                stringEqual(country, user.getCountry()) &&
                stringEqual(countryOfDeath, user.getCountryOfDeath()) &&
                stringEqual(regionOfDeath, user.getRegionOfDeath()) &&
                stringEqual(cityOfDeath, user.getCityOfDeath())
        );
    }

    /**
     * A null safe String.equals equivalent. Strings are considered equal if both are null.
     *
     * @param s1 The first string to compare
     * @param s2 The second string to compare
     * @return Whether the strings are equal
     */
    private boolean stringEqual(String s1, String s2) {
        if (s1 == null) {
            return s2 == null;
        } else {
            return s1.equals(s2);
        }
    }

    public String getName() {
        return String.join(" ", name);
    }

    public String getPreferredName() {
        String val = "";
        if (preferredName != null) {
            for (String name : preferredName) {
                if (name != null) {
                    val += name + " ";
                }
            }
            if (!val.isEmpty()) {
                val = val.substring(0, val.length()-1);
            }
        }
        return val;
    }

    public void setName(String name) {
        this.name = name.split(",");
        setLastModified();
    }

    public void setNameArray(String[] name) {
        this.name = name;
    }

    public void setPreferredName(String name) {
        this.preferredName = name.split(",");
        setLastModified();
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPreferredNameArray(String[] name) {
        this.preferredName = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public String[] getNameArray() {
        return name;
    }

    public String[] getPreferredNameArray() {
        return preferredName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public EnumSet<Organ> getOrgans() {
        return organs;
    }

    public long getId() {
        return id;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public String getRegion() {
        return region;
    }

    public Gender getGender() {
        return gender;
    }

    public Gender getGenderIdentity() {
        return genderIdentity;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public LocalDateTime getDateOfDeath() {
        return dateOfDeath;
    }

    public ArrayList<WaitingListItem> getWaitingListItems() {
        return waitingListItems;
    }


    public String getAgeString() {
        String age = String.format("%.1f", getAgeDouble());
        return age + " years";
    }

    public double getAgeDouble() {
        long days = Duration.between(dateOfBirth.atStartOfDay(), LocalDate.now().atStartOfDay()).toDays();
        return days / 365.00;

    }


    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        setLastModified();
    }

    public void setDateOfDeath(LocalDateTime dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
        setLastModified();
    }

    public void setHeight(double height) {
        this.height = height;
        setLastModified();
    }

    public void setWeight(double weight) {
        this.weight = weight;
        setLastModified();
    }

    public void setGender(Gender gender) {
        this.gender = gender;
        setLastModified();
    }

    public void setGenderIdentity(Gender gender) {
        this.genderIdentity = gender;
        setLastModified();
    }

    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
        setLastModified();
    }

    public void setRegion(String region) {
        this.region = region;
        setLastModified();
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
        setLastModified();
    }

    public void setOrgan(Organ organ) {
        if (!organs.contains(organ)) {
            this.organs.add(organ);
            Debugger.log("Organ added.");
        } else {
            Debugger.log("Organ already being donated.");
        }
        setLastModified();
    }

    public void removeOrgan(Organ organ) {
        if (organs.contains(organ)) {
            this.organs.remove(organ);
            Debugger.log("Organ removed.");
        } else {
            Debugger.log("Organ not in list.");
        }
        setLastModified();
    }

    public void sortHistory() {
        userHistory.sort(Comparator.comparing(HistoryItem::getDateTime));
    }

    public void setLastModified() {
        lastModified = LocalDateTime.now();
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public SmokerStatus getSmokerStatus() {
        return smokerStatus;
    }

    public void setSmokerStatus(SmokerStatus smokerStatus) {
        this.smokerStatus = smokerStatus;
    }

    public AlcoholConsumption getAlcoholConsumption() {
        return alcoholConsumption;
    }

    public void setAlcoholConsumption(AlcoholConsumption alcoholConsumption) {
        this.alcoholConsumption = alcoholConsumption;
    }

    public ArrayList<Medication> getCurrentMedications() {
        return currentMedications;
    }

    public ArrayList<Medication> getHistoricMedications() {
        return historicMedications;
    }

    public ArrayList<Procedure> getPendingProcedures() {
        return pendingProcedures;
    }

    public ArrayList<Procedure> getPreviousProcedures() {
        return previousProcedures;
    }

    public ArrayList<HistoryItem> getUserHistory() {
        return userHistory;
    }

    /**
     * Get a string containing key information about the user.
     *
     * @return The information string
     */
    public String toString() {
        String dateOfDeathString, dateModifiedString, heightString, weightString;
        if (dateOfDeath != null) {
            dateOfDeathString = dateFormat.format(dateOfDeath);
        } else {
            dateOfDeathString = null;
        }
        if (lastModified == null) {
            dateModifiedString = null;
        } else {
            dateModifiedString = dateTimeFormat.format(lastModified);
        }
        if (height == -1) {
            heightString = null;
        } else {
            heightString = String.format("%.2f", height);
        }
        if (weight == -1) {
            weightString = null;
        } else {
            weightString = String.format("%.2f", weight);
        }
        return String.format("user (ID %d) created at %s "
                + "\n-Name: %s"
                + "\n-Preferred Name: %s"
                + "\n-Date of Birth: %s"
                + "\n-Date of death: %s"
                + "\n-Gender: %s"
                + "\n-Height: %s"
                + "\n-Weight: %s"
                + "\n-Blood type: %s"
                + "\n-Region: %s"
                + "\n-Current address: %s"
                + "\n-Last Modified: %s"
                + "\n-Organs to donate: %s.",
            id, dateTimeFormat.format(creationTime), getName(), getPreferredName(), dateFormat.format(dateOfBirth), dateOfDeathString,
            genderIdentity, heightString, weightString, bloodType, region, currentAddress, dateModifiedString, organs);
    }

    public String getSummaryString() {
        return String.format("%s (preferred name %s), ID %d", getName(), getPreferredName(), id);
    }

    public ArrayList<Disease> getCurrentDiseases() {
        return currentDiseases;
    }

    public void setCurrentDiseases(ArrayList<Disease> currentDiseases) {
        this.currentDiseases = currentDiseases;
    }

    public ArrayList<Disease> getCuredDiseases() {
        return curedDiseases;
    }

    public void setCuredDiseases(ArrayList<Disease> curedDiseases) {
        this.curedDiseases = curedDiseases;
    }

    public Boolean isDonor() {
        return !organs.isEmpty();
    }

    public void setPendingProcedures(ArrayList<Procedure> item) { this.pendingProcedures = item; }

    public void setPreviousProcedures(ArrayList<Procedure> item) { this.previousProcedures = item; }

    public void setHistoricMedications(ArrayList<Medication> item) { this.historicMedications = item; }

    public void setCurrentMedications(ArrayList<Medication> item) { this.currentMedications = item; }

    public boolean isReceiver() {
        for (WaitingListItem item : waitingListItems) {
            if (item.getStillWaitingOn()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the intersection of the organs which are being donated and organs that the
     * user is currently waiting to receive
     * @return The organs which are being donated and the user is currently waiting on
     */
    public Set<Organ> conflictingOrgans(){
        Set<Organ> conflicting = new HashSet<>();
        for(WaitingListItem item: waitingListItems) {
            if(item.getStillWaitingOn()){
                if(organs.contains(item.getOrganType())){
                    conflicting.add(item.getOrganType());
                }
            }
        }
        return conflicting;
    }

    public String getType() {
        if (isDonor() && isReceiver()) {
            return "Donor/Receiver";
        } else if (isDonor() && !isReceiver()) {
            return "Donor";
        } else if (!isDonor() && isReceiver()) {
            return "Receiver";
        } else {
            return "";
        }
    }

    /**
     * Only called by the admin role via the CLI. Removes the waiting list item with code 5, which indicates that it was removed by an administrator.
     * @param toRemove The organ being removed from the waiting list.
     */
    public void removeWaitingListItem(Organ toRemove) {
        for (WaitingListItem item : waitingListItems){
            if (item.getOrganType() == toRemove) {
                item.deregisterOrgan(5);
                break;
            }
        }
    }

    public void addHistoryEntry(String action, String description) {
        userHistory.add(new HistoryItem(action, description));
    }

    public String getProfileImageType() {
        return profileImageType;
    }

    public void setProfileImageType(String type) {
        this.profileImageType = type;
    }
    public String getCityOfDeath() {
        return cityOfDeath;
    }

    public String getRegionOfDeath() {
        return regionOfDeath;
    }

    public void setCityOfDeath(String cityOfDeath) {
        this.cityOfDeath = cityOfDeath;
    }

    public void setRegionOfDeath(String regionOfDeath) {
        this.regionOfDeath = regionOfDeath;
    }

    public String getCountryOfDeath() {
        return countryOfDeath;
    }

    public void setCountryOfDeath(String countryOfDeath) {
        this.countryOfDeath = countryOfDeath;
    }


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    public String getNhi() {
        return nhi;
    }

    public void setNhi(String nhi) {
        this.nhi = nhi;
    }

    /**
     * Checks an NHI is of the correct format:
     *      Starts with 3rd, 6th, 9th... letter of the alphabet
     *      Is of the format cde1234
     * @param nhi A National Health Index number
     */
    public static boolean checkNhi(String nhi){
        nhi = nhi.toLowerCase();
        // Character 'c' has ASCII value 99
        if ((int) nhi.charAt(0) % 3 == 0) {
            return Pattern.matches("[a-z][a-z][a-z]\\d\\d\\d\\d", nhi);
        }
        else return false;
    }

}
