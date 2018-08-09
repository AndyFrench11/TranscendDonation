package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Attribute.Organ;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

public class UserDonations {

    public Set<Organ> getAllUserDonations(int userId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            Set<Organ> organs = new HashSet<>();
            String query = "SELECT * FROM DONATION_LIST_ITEM WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                organs.add(getOrganFromResultSet(resultSet));
            }
            return organs;
        }
    }

    public Set<Organ> getAllDonations() throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            Set<Organ> organs = new HashSet<>();
            String query = "SELECT * FROM DONATION_LIST_ITEM";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                organs.add(getOrganFromResultSet(resultSet));
            }
            return organs;
        }
    }

    public void insertDonation(Organ organ, int userId, LocalDateTime deathDate) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String insertDonationQuery = "INSERT INTO DONATION_LIST_ITEM (name, user_id, timeOfDeath) " +
                    "VALUES (?,?,?)";
            PreparedStatement insertDonationStatement = connection.prepareStatement(insertDonationQuery);

            insertDonationStatement.setString(1, organ.toString());
            insertDonationStatement.setInt(2, userId);
            if (deathDate == null) {
                insertDonationStatement.setNull(3, Types.BIGINT);
            } else {
                insertDonationStatement.setLong(3, deathDate.toEpochSecond(OffsetDateTime.now().getOffset()));
            }

            System.out.println("Inserting new donation -> Successful -> Rows Added: " + insertDonationStatement.executeUpdate());
        }
    }

    public Organ getOrganFromResultSet(ResultSet resultSet) throws SQLException {
        return Organ.valueOf(resultSet.getString("name").toUpperCase());
    }

    public Organ getDonationListItemFromName(String donationListItemName, int userId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            // SELECT * FROM DONATION_LIST_ITEM id = id;
            String query = "SELECT * FROM DONATION_LIST_ITEM WHERE name = ? AND user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, donationListItemName);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();

            //If response is empty then return null
            if (!resultSet.next()) {
                return null;
            } else {
                //If response is not empty then return a indication that fields arent empty
                return getOrganFromResultSet(resultSet);
            }
        }
    }

    public void removeDonationListItem(int userId, String donationListItemName) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "DELETE FROM DONATION_LIST_ITEM WHERE name = ? AND user_id = ?";
            PreparedStatement statement = connection.prepareStatement(update);
            statement.setString(1, donationListItemName);
            statement.setInt(2, userId);
            System.out.println("Deletion of Donation List Item - NAME: " + donationListItemName + " USERID: " + userId + " -> Successful -> Rows Removed: " + statement.executeUpdate());
        }
    }

    public void removeAllUserDonations(int userId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "DELETE FROM DONATION_LIST_ITEM WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(update);
            statement.setInt(1, userId);
            System.out.println("Deletion of all Donation List Items for - " + " USERID: " + userId + " -> Successful -> Rows Removed: " + statement.executeUpdate());
        }
    }


}
