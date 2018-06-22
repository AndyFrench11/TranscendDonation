package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.User;
import seng302.Server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class Administration {
    private Connection connection;
    private String currentDatabase;

    public Administration() {
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        connection = databaseConfiguration.getConnection();
        currentDatabase = databaseConfiguration.getCurrentDatabase();
    }


    public void resample() throws SQLException{
        ArrayList<User> allUsers = new ArrayList<>();
        User user1 = new User("Andy", new String[]{"Robert"}, "French", LocalDate.now(), "andy", "andy@andy.com", "andrew");
        allUsers.add(user1);
        User user2 = new User("Buzz", new String[]{"Buzzy"}, "Knight", LocalDate.now(), "buzz", "buzz@buzz.com", "drowssap");
        allUsers.add(user2);
        User user3 = new User("James", new String[]{"Mozza"}, "Morritt", LocalDate.now(), "mozza", "mozza@mozza.com", "mozza");
        allUsers.add(user3);
        User user4 = new User("Jono", new String[]{"Zilla"}, "Hills", LocalDate.now(), "jonozilla", "zilla@zilla.com", "zilla");
        allUsers.add(user4);
        User user5 = new User("James", new String[]{"Mackas"}, "Mackay", LocalDate.now(), "mackas", "mackas@mackas.com", "mackas");
        allUsers.add(user5);
        User user6 = new User("Nicky", new String[]{"The Dark Horse"}, "Zohrab-Henricks", LocalDate.now(), "nicky", "nicky@nicky.com", "nicky");
        allUsers.add(user6);
        User user7 = new User("Kyran", new String[]{"Playing Fortnite"}, "Stagg", LocalDate.now(), "kyran", "kyran@kyran.com", "fortnite");
        allUsers.add(user7);
        User user8 = new User("Andrew", new String[]{"Daveo"}, "Davidson", LocalDate.now(), "andrew", "andrew@andrew.com", "andrew");
        allUsers.add(user8);

        GeneralUser userDatabase = new GeneralUser();
        for (User user: allUsers) {
            userDatabase.insertUser(user);
        }
    }

    public void reset() throws SQLException{

        String update = "DELETE FROM " + currentDatabase + ".WAITING_LIST_ITEM";
        PreparedStatement statement = connection.prepareStatement(update);
        System.out.println("Reset of database (WAITING_LIST_ITEM): -> Successful -> Rows Removed: " + statement.executeUpdate());

        update = "DELETE FROM " + currentDatabase + ".PROCEDURES";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of database (PROCEDURE): -> Successful -> Rows Removed: " + statement.executeUpdate());

        update = "DELETE FROM " + currentDatabase + ".MEDICATION";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of database (MEDICATION): -> Successful -> Rows Removed: " + statement.executeUpdate());

        update = "DELETE FROM " + currentDatabase + ".DONATION_LIST_ITEM";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of database (DONATION_LIST_ITEM): -> Successful -> Rows Removed: " + statement.executeUpdate());

        update = "DELETE FROM " + currentDatabase + ".DISEASE";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of database (DISEASE): -> Successful -> Rows Removed: " + statement.executeUpdate());

        update = "DELETE FROM " + currentDatabase + ".HISTORY_ITEM";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of database (HISTORY_ITEM): -> Successful -> Rows Removed: " + statement.executeUpdate());

        update = "DELETE FROM " + currentDatabase + ".ADMIN";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of database (ADMIN): -> Successful -> Rows Removed: " + statement.executeUpdate());

        update = "DELETE FROM " + currentDatabase + ".CLINICIAN";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of database (CLINICIAN): -> Successful -> Rows Removed: " + statement.executeUpdate());

        update = "DELETE FROM " + currentDatabase + ".USER";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of database (USER): -> Successful -> Rows Removed: " + statement.executeUpdate());


        update = "ALTER TABLE " + currentDatabase + ".USER AUTO_INCREMENT = 1";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of AutoIncrement(USER): -> Successful -> " + statement.executeUpdate());

        update = "ALTER TABLE " + currentDatabase + ".CLINICIAN AUTO_INCREMENT = 1";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of AutoIncrement(CLINICIAN): -> Successful -> " + statement.executeUpdate());

        update = "ALTER TABLE " + currentDatabase + ".ADMIN AUTO_INCREMENT = 1";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of AutoIncrement(ADMIN): -> Successful -> " + statement.executeUpdate());

        update = "ALTER TABLE " + currentDatabase + ".DISEASE AUTO_INCREMENT = 1";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of AutoIncrement(DISEASE): -> Successful -> " + statement.executeUpdate());

        update = "ALTER TABLE " + currentDatabase + ".DONATION_LIST_ITEM AUTO_INCREMENT = 1";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of AutoIncrement(DONATION LIST ITEM): -> Successful -> " + statement.executeUpdate());

        update = "ALTER TABLE " + currentDatabase + ".MEDICATION AUTO_INCREMENT = 1";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of AutoIncrement(MEDICATION): -> Successful -> " + statement.executeUpdate());

        update = "ALTER TABLE " + currentDatabase + ".PROCEDURES AUTO_INCREMENT = 1";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of AutoIncrement(PROCEDURES): -> Successful -> " + statement.executeUpdate());

        update = "ALTER TABLE " + currentDatabase + ".WAITING_LIST_ITEM AUTO_INCREMENT = 1";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of AutoIncrement(WAITING LIST ITEM): -> Successful -> " + statement.executeUpdate());

        update = "ALTER TABLE " + currentDatabase + ".HISTORY_ITEM AUTO_INCREMENT = 1";
        statement = connection.prepareStatement(update);
        System.out.println("Reset of AutoIncrement(HISTORY_ITEM): -> Successful -> " + statement.executeUpdate());

        String insert = "INSERT INTO " + currentDatabase + ".CLINICIAN(username, password, name, work_address, region, staff_id) " +
                "VALUES(?, ?, ?, ?, ?, ?)";
        statement = connection.prepareStatement(insert);
        statement.setString(1, "default");
        statement.setString(2, "default");
        statement.setString(3, "default");
        statement.setString(4, "default");
        statement.setString(5, "default");
        statement.setInt(6, 1);
        System.out.println("Inserting Default Clinician -> Successful -> Rows Added: " + statement.executeUpdate());

        insert = "INSERT INTO " + currentDatabase + ".ADMIN(username, password, name, work_address, region, staff_id) " +
                "VALUES(?, ?, ?, ?, ?, ?)";
        statement = connection.prepareStatement(insert);
        statement.setString(1, "admin");
        statement.setString(2, "default");
        statement.setString(3, "default");
        statement.setString(4, "default");
        statement.setString(5, "default");
        statement.setInt(6, 1);
        System.out.println("Inserting Default Admin -> Successful -> Rows Added: " + statement.executeUpdate());


    }
}