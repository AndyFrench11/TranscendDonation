package seng302.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import seng302.Logic.Database.UserWaitingList;
import seng302.Model.Attribute.Organ;
import seng302.Model.WaitingListItem;
import seng302.NotificationManager.PushAPI;
import seng302.Server;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.*;

public class WaitingListController {
    private UserWaitingList model;

    /**
     * constructor method to create a new waiting list controller object
     * to handle all the waiting list operation requests
     */
    public WaitingListController() {
        model = new UserWaitingList();
    }

    /**
     * method to get all waiting list items
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return Json object containing all waiting list items or a error message
     */
    public String getAllWaitingListItems(Request request, Response response) {

        Map<String, String> params = new HashMap<String, String>();
        List<String> possibleParams = new ArrayList<String>(Arrays.asList(
                "organ","region", "country"
        ));

        for(String param:possibleParams){
            if(request.queryParams(param) != null){
                params.put(param,request.queryParams(param));
            }
        }

        List<WaitingListItem> queriedWaitingListItems;
        try {
            queriedWaitingListItems = model.queryWaitingListItems(params);
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return e.getMessage();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedWaitingListItems = gson.toJson(queriedWaitingListItems);

        response.type("application/json");
        response.status(200);
        return serialQueriedWaitingListItems;
    }



    /**
     * method to get all waiting list items of a single user
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return Json object containing all the waiting list items of a user or an error message
     */
    public String getAllUserWaitingListItems(Request request, Response response) {
        int requestedUserId = Integer.parseInt(request.params(":id"));

        ArrayList<WaitingListItem> queriedUserWaitingListItems;
        try {
            queriedUserWaitingListItems = model.getAllUserWaitingListItems(requestedUserId);
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return e.getMessage();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedUserWaitingListItems = gson.toJson(queriedUserWaitingListItems);

        response.type("application/json");
        response.status(200);
        return serialQueriedUserWaitingListItems;
    }

    /**
     * method to handle getting a single waiting list object from a specific user
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return Json object containing the waiting list item objects information, or an error message
     */
    public String getSingleUserWaitingListItem(Request request, Response response) {
        WaitingListItem queriedWaitingListItem = queryWaitingListItem(request, response);

        if (queriedWaitingListItem == null) {
            return response.body();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedWaitingListItem = gson.toJson(queriedWaitingListItem);

        response.type("application/json");
        response.status(200);
        return serialQueriedWaitingListItem;
    }

    /**
     * method to handle adding a new user waiting list item
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String whether the operation was completed successfully
     */
    public String addNewUserWaitingListItem(Request request, Response response) {

        int requestedUserId = Integer.parseInt(request.params(":id"));

        Gson gson = new Gson();
        WaitingListItem receivedWaitingListItem = gson.fromJson(request.body(), WaitingListItem.class);
        if (receivedWaitingListItem == null) {
            response.status(400);
            return "Missing Waiting List Item Body";
        } else {
            try {
                model.insertWaitingListItem(receivedWaitingListItem, requestedUserId);
                response.status(201);
                PushAPI.getInstance().sendTextNotification(requestedUserId, "Organ added to waiting list.",
                        Organ.capitalise(receivedWaitingListItem.getOrganType().toString()) + " was added to your organ waiting list.");
                return "WAITING LIST ITEM INSERTED FOR USER ID: " + requestedUserId;
            } catch (SQLException e) {
                response.status(500);
                return "Internal Server Error";
            }

        }
    }

    /**
     * Update a user's waiting list items list to a new list.
     *
     * @param request The Java request object, which should contain a list of waiting list items in the body
     * @param response Used to set status code relevant to the operation outcome
     * @return The response body
     */
    public String editAllWaitingListItems(Request request, Response response) {
        int requestedUserId = Integer.parseInt(request.params(":id"));
        List<WaitingListItem> waitingListItems;
        try {
            waitingListItems= new Gson().fromJson(request.body(), new TypeToken<List<WaitingListItem>>(){}.getType());
        } catch (JsonSyntaxException e) {
            response.status(400);
            return "Malformed request body";
        }
        if (waitingListItems == null) {
            response.status(400);
            return "Missing body";
        } else {
            try {
                model.updateAllWaitingListItems(waitingListItems, requestedUserId);
                response.status(200);
                PushAPI.getInstance().sendTextNotification(requestedUserId, "Organ waiting list updated.",
                        "Your waiting list has been updated.");
                return "Success";
            } catch (SQLException e) {
                response.status(500);
                return "Internal Server Error";
            }
        }
    }

    /**
     * method to edit an existing waiting list item
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String whether he operation was completed successfully
     */
    public String editWaitingListItem(Request request, Response response) {
        int requestedWaitingListItemId = Integer.parseInt(request.params(":waitingListItemId"));
        int requestedUserId = Integer.parseInt(request.params(":id"));

        WaitingListItem queriedWaitingListItem = queryWaitingListItem(request, response);

        if (queriedWaitingListItem == null) {
            return response.body();
        }

        Gson gson = new Gson();

        WaitingListItem receivedWaitingListItem = gson.fromJson(request.body(), WaitingListItem.class);
        if (receivedWaitingListItem == null) {
            response.status(400);
            return "Missing Waiting List Item Body";
        } else {
            try {
                model.updateWaitingListItem(receivedWaitingListItem, requestedWaitingListItemId, requestedUserId);
                response.status(201);
                PushAPI.getInstance().sendTextNotification(requestedUserId, "Organ waiting list item updated.",
                        Organ.capitalise(receivedWaitingListItem.getOrganType().toString()) + "from your waiting list has been updated.");
                return "WAITING LIST ITEM WITH ID: "+ requestedWaitingListItemId +" FOR USER ID: "+ requestedUserId +" SUCCESSFULLY UPDATED";
            } catch (SQLException e) {
                response.status(500);
                return "Internal Server Error";
            }
        }
    }

    /**
     * method to delete a specific waiting list item
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String whether the operation was completed successfully or not
     */
    public String deleteWaitingListItem(Request request, Response response) {
        int requestedWaitingListItemId = Integer.parseInt(request.params(":waitingListItemId"));
        int requestedUserId = Integer.parseInt(request.params(":id"));

        WaitingListItem queriedWaitingListItem = queryWaitingListItem(request, response);

        if (queriedWaitingListItem == null) {
            return response.body();
        }

        try {
            model.removeWaitingListItem(requestedUserId, requestedWaitingListItemId);
            response.status(201);
            PushAPI.getInstance().sendTextNotification(requestedUserId, "Organ waiting list item deleted.",
                    "An organ was deleted from your waiting list.");
            return "WAITING LIST ITEM WITH ID: "+ requestedWaitingListItemId +" FOR USER ID: "+ requestedUserId +" DELETED";
        } catch (SQLException e) {
            response.status(500);
            return e.getMessage();
        }
    }

    /**
     * Update a waiting list item to show that its transplant has been completed
     *
     * @param request HTTP request
     * #param response HTTP response
     * @return Whether the operation was successful
     */
    public String transplantCompleted(Request request, Response response) {
        int waitingListId = Integer.parseInt(request.params(":waitingListItemId"));
        try {
            model.transplantWaitingListItem(waitingListId);
            response.status(201);
            return "WAITING LIST ITEM WITH ID: " + waitingListId + " SUCCESSFULLY UPDATED";
        } catch (SQLException e) {
            response.status(500);
            return e.getMessage();
        }
    }

    /**
     * Checks for the validity of the request ID, and returns a WaitingListItem obj
     * @param request HTTP request
     * @param response HTTP response
     * @return A valid WaitingListItem object if the WaitingListItem exists otherwise return null
     */
    private WaitingListItem queryWaitingListItem(Request request, Response response) {
        int requestedWaitingListItemId = Integer.parseInt(request.params(":waitingListItemId"));
        int requestedUserId = Integer.parseInt(request.params(":id"));

        WaitingListItem queriedWaitingListItem;
        try {
            queriedWaitingListItem = model.getWaitingListItemFromId(requestedWaitingListItemId, requestedUserId);
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            response.body("Internal server error");
            return null;
        }

        if (queriedWaitingListItem == null) {
            Server.getInstance().log.warn(String.format("No waiting list item of ID: %d found", requestedWaitingListItemId));
            response.status(404);
            response.body("Not found");
            return null;
        }
        return queriedWaitingListItem;
    }

    /**
     * Gets a waiting list item by id.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @return A valid WaitingListItem object if the WaitingListItem id exists otherwise return null
     */
    public String getWaitingListId(Request request, Response response) {
        int userId = Integer.parseInt(request.params(":id"));
        Organ organType = Organ.parse(request.params(":organType"));
        System.out.println(organType);
        int queriedWaitingListId;
        try {
            queriedWaitingListId = model.getWaitingListId(userId, organType);
        } catch (SQLException e) {
            response.status(500);
            response.body("Internal server error");
            return null;
        }

        if (queriedWaitingListId == 0) {
            response.status(404);
            response.body("Not found");
            return null;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedWaitingListItem = gson.toJson(queriedWaitingListId);

        response.type("application/json");
        response.status(200);
        return serialQueriedWaitingListItem;
    }


}
