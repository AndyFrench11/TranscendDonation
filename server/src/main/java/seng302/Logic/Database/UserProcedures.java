package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Attribute.Organ;
import seng302.Model.Procedure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserProcedures {

    public ArrayList<Procedure> getAllProcedures(int userId) throws SQLException{
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            ArrayList<Procedure> allProcedures = new ArrayList<>();
            String query = "SELECT * FROM PROCEDURES WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                allProcedures.add(getProcedureFromResultSet(resultSet));
            }
            return allProcedures;
        }
    }

    public void insertProcedure(Procedure procedure, int userId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String insertProceduresQuery = "INSERT INTO PROCEDURES (summary, description, date, organs_affected, user_id) " +
                    "VALUES(?, ?, ?, ?, ?)";
            PreparedStatement insertProceduresStatement = connection.prepareStatement(insertProceduresQuery);

            insertProceduresStatement.setString(1, procedure.getSummary());
            insertProceduresStatement.setString(2, procedure.getDescription());
            insertProceduresStatement.setDate(3, java.sql.Date.valueOf(procedure.getDate()));

            String organsAffected = "";
            for (Organ organ : procedure.getOrgansAffected()) {
                organsAffected += organ.toString() + ",";
            }
            if (!organsAffected.isEmpty()) {
                organsAffected = organsAffected.substring(0, organsAffected.length() - 1);
            }

            insertProceduresStatement.setString(4, organsAffected);
            insertProceduresStatement.setInt(5, userId);

            System.out.println("Inserting new procedure -> Successful -> Rows Added: " + insertProceduresStatement.executeUpdate());
        }
    }

    public Procedure getProcedureFromId(int procedureId, int userId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            // SELECT * FROM PROCEDURES id = id;
            String query = "SELECT * FROM PROCEDURES WHERE id = ? AND user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, procedureId);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();


            //If response is empty then return null
            if (!resultSet.next()) {
                return null;
            } else {
                //If response is not empty then return a indication that fields arent empty
                return getProcedureFromResultSet(resultSet);
            }
        }
    }

    public void updateProcedure(Procedure procedure, int procedureId, int userId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "UPDATE PROCEDURES SET summary = ?, description = ?, date = ?, organs_affected = ? WHERE user_id = ? AND id = ?";
            PreparedStatement statement = connection.prepareStatement(update);

            statement.setString(1, procedure.getSummary());
            statement.setString(2, procedure.getDescription());
            statement.setDate(3, java.sql.Date.valueOf(procedure.getDate()));

            String organsAffected = "";
            for (Organ organ : procedure.getOrgansAffected()) {
                organsAffected += organ.toString() + ",";
            }
            organsAffected = organsAffected.substring(0, organsAffected.length() - 1);

            statement.setString(4, organsAffected);

            statement.setInt(5, userId);
            statement.setInt(6, procedureId);
            System.out.println("Update Procedure - ID: " + procedureId + " USERID: " + userId + " -> Successful -> Rows Updated: " + statement.executeUpdate());
        }
    }

    public void removeProcedure(int userId, int procedureId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "DELETE FROM PROCEDURES WHERE id = ? AND user_id = ?";
            PreparedStatement statement = connection.prepareStatement(update);
            statement.setInt(1, procedureId);
            statement.setInt(2, userId);
            System.out.println("Deletion of Procedure - ID: " + procedureId + " USERID: " + userId + " -> Successful -> Rows Removed: " + statement.executeUpdate());
        }
    }

    public Procedure getProcedureFromResultSet(ResultSet proceduresResultSet) throws SQLException {

        ArrayList<Organ> procedureOrgans = new ArrayList<>();
        if (!proceduresResultSet.getString("organs_affected").isEmpty()) {
            for (String organ : proceduresResultSet.getString("organs_affected").split(",")) {
                procedureOrgans.add(Organ.parse(organ));
            }
        }
        return new Procedure(
                proceduresResultSet.getString("summary"),
                proceduresResultSet.getString("description"),
                proceduresResultSet.getDate("date").toLocalDate(),
                procedureOrgans,
                proceduresResultSet.getInt("id")
        );
    }

    /**
     * Replace a user's procedures on the database with a new set of procedures.
     *
     * @param newProcedures The list of procedures to replace the old one with
     * @param userId The id of the user to replace procedures of
     * @throws SQLException If there is errors communicating with the database
     */
    public void updateAllProcedures(List<Procedure> newProcedures, int userId) throws SQLException {
        List<Procedure> oldProcedures = getAllProcedures(userId);

        //Ignore all procedures that are already on the database and up to date
        for (int i = oldProcedures.size() - 1; i >= 0; i--) {
            Procedure found = null;
            for (Procedure newProcedure: newProcedures) {
                if (newProcedure.equals(oldProcedures.get(i))) {
                    found = newProcedure;
                    break;
                }
            }
            if (found == null) {
                //Patch edited procedures
                for (Procedure newProcedure: newProcedures) {
                    if (newProcedure.getId() == oldProcedures.get(i).getId()) {
                        updateProcedure(newProcedure, oldProcedures.get(i).getId(), userId);
                        found = newProcedure;
                        break;
                    }
                }
            }
            if (found != null) {
                newProcedures.remove(found);
                oldProcedures.remove(i);
            }
        }

        //Delete all procedures from the database that are no longer up to date
        for (Procedure procedure: oldProcedures) {
            removeProcedure(userId, procedure.getId());
        }

        //Upload all new procedures
        for (Procedure procedure: newProcedures) {
            insertProcedure(procedure, userId);
        }
    }
}
