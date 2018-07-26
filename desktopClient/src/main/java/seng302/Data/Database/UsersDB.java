package seng302.Data.Database;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.HttpResponseException;
import seng302.Data.Interfaces.UsersDAO;
import seng302.Generic.APIResponse;
import seng302.Generic.APIServer;
import seng302.User.User;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersDB implements UsersDAO {
    private final APIServer server;

    public UsersDB(APIServer server) {
        this.server = server;
    }

    /**
     * gets a users id from the username
     * @param username the username of the user
     * @param token the users token
     * @return returns the id of the user
     * @throws HttpResponseException throws if cannot connect to the server
     */
    @Override
    public int getUserId(String username, String token) throws HttpResponseException {
        for (User user : getAllUsers(token)) {
            if (user.getUsername().equals(username)) {
                return (int) user.getId();
            }
        }
        return -1;
    }

    /**
     * inserts a new user into the server
     * @param user the user to insert
     * @throws HttpResponseException throws if cannot connect to the server
     */
    @Override
    public void insertUser(User user) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        JsonObject userJson = jp.parse(new Gson().toJson(user)).getAsJsonObject();
        userJson.remove("id");
        APIResponse response = server.postRequest(userJson, new HashMap<>(), null, "users");
    }

    /**
     * updates a user on the database
     * @param user the user to update
     * @param token the users token
     * @throws HttpResponseException throws if cannot connect to the server
     */
    @Override
    public void updateUser(User user, String token) throws HttpResponseException {
        JsonParser jp = new JsonParser();
        JsonObject userJson = jp.parse(new Gson().toJson(user)).getAsJsonObject();
        APIResponse response = server.patchRequest(userJson, new HashMap<>(), token, "users", String.valueOf(user.getId()));
        if (response.getStatusCode() != 201)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    /**
     * Used for searching, takes a hashmap of keyvalue pairs and searches the DB for them.
     * eg. "age", "10" returns all users aged 10.
     *
     * @param searchMap The hashmap with associated key value pairs
     * @return a JSON array of users.
     */
    @Override
    public List<User> queryUsers(Map<String, String> searchMap, String token) throws HttpResponseException {
        APIResponse response =  server.getRequest(searchMap, token, "users");
        if(response.isValidJson()){
            JsonArray searchResults = response.getAsJsonArray();
            Type type = new TypeToken<ArrayList<User>>() {
            }.getType();
            return new Gson().fromJson(searchResults, type);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * gets a specific user from the server
     * @param id the id of the user to get
     * @param token the users token
     * @return returns a user
     * @throws HttpResponseException throws if cannot connect to the server
     */
    @Override
    public User getUser(long id, String token) throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<>(), token, "users", String.valueOf(id));
        if (response.isValidJson()) {
            return new Gson().fromJson(response.getAsJsonObject(), User.class);
        }
        return null;
    }// Now uses API server!

    /**
     * get all the users from the server
     * @param token the users token
     * @return returns a list of all users
     * @throws HttpResponseException throws if cannot connect to the server
     */
    @Override
    public List<User> getAllUsers(String token) throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<>(), token, "users");
        if (response.isValidJson()) {
            List<User> responses = new Gson().fromJson(response.getAsJsonArray(), new TypeToken<List<User>>() {
            }.getType());
            return responses;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * removes a user from the server
     * @param id the id of the users to remove
     * @param token the users token
     * @throws HttpResponseException throws if cannot connect to the server
     */
    @Override
    public void removeUser(long id, String token) throws HttpResponseException {
        APIResponse response = server.deleteRequest(new HashMap<>(), token, "users", String.valueOf(id));
        if (response.getStatusCode() != 201)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
    }

    /**
     * gets the total number of users
     * @param token the users token
     * @return returns the number of users
     * @throws HttpResponseException throws if cannot connect to the server
     */
    @Override
    public int count(String token) throws HttpResponseException {
        APIResponse response = server.getRequest(new HashMap<>(), token, "usercount");
        if (response.getStatusCode() != 200)
            throw new HttpResponseException(response.getStatusCode(), response.getAsString());
        return Integer.parseInt(response.getAsString());
    }
}