package seng302.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import seng302.Logic.Database.OrgansDatabase;
import seng302.Logic.OrganMatching;
import seng302.Model.Attribute.Organ;
import seng302.Model.DonatableOrgan;
import seng302.NotificationManager.PushAPI;
import seng302.Server;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.*;

public class OrgansController {

    private OrgansDatabase model;

    public OrgansController() {
        model = new OrgansDatabase();
    }

    /**
     * gets all the DonatableOrgans and parses it into a json string
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return Json object containing all the DonatableOrgan objects, or an error message
     */
    public String getAllDonatableOrgans(Request request, Response response) {
        List<DonatableOrgan> allDonatableOrgans;
        try {
            allDonatableOrgans = model.getAllDonatableOrgans();
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return e.getMessage();
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serializedOrgans = gson.toJson(allDonatableOrgans);

        response.type("application/json");
        response.status(200);
        return serializedOrgans;
    }

    public String queryOrgans(Request request, Response response) {
        List<DonatableOrgan> allDonatableOrgans;
        Map<String, String> params = new HashMap<String, String>();
        List<String> possibleParams = new ArrayList<String>(Arrays.asList(
                "userRegion","organ","receiverName","country",
                "startIndex","count"
        ));
        for(String param:possibleParams){
            if(request.queryParams(param) != null){
                params.put(param,request.queryParams(param));
            }
        }

        System.out.println(request.queryParams());
        try {
            allDonatableOrgans = model.queryOrgans(params);
            OrganMatching organMatching = new OrganMatching();

            if (!Objects.equals(params.get("receiverName"),"")) {
                String receiverNameQuery = params.get("receiverName");
                for(DonatableOrgan organ: allDonatableOrgans){
                    organ.setTopReceivers(organMatching.getTop5Matches(organ, receiverNameQuery));
                }
                for(DonatableOrgan organ: new ArrayList<>(allDonatableOrgans)){
                    if(organ.getTopReceivers().size() == 0){
                        allDonatableOrgans.remove(organ);
                    }
                }
            } else {
                System.out.println("brah2");
                for(DonatableOrgan organ: allDonatableOrgans){
                    organ.setTopReceivers(organMatching.getTop5Matches(organ, ""));
                }
                System.out.println("brah3");
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String serializedOrgans = gson.toJson(allDonatableOrgans);
            response.type("application/json");
            response.status(200);
            System.out.println("brah");
            return serializedOrgans;
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return e.getMessage();
        }
    }

    /**
     * parses a donatable organ from a json string and inserts it into the database
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String response if the operation was successful
     */
    public String insertOrgan(Request request, Response response) {
        Gson gson = new Gson();

        DonatableOrgan organ = gson.fromJson(request.body(), DonatableOrgan.class);
        if (organ == null) {
            response.status(400);
            return "Missing Organ Body";
        } else {
            try {
                model.insertOrgan(organ);
                response.status(201);
                PushAPI.getInstance().sendTextNotification((int)organ.getDonorId(), "Organ added to donations.",
                        Organ.capitalise(organ.getOrganType().toString()) + " was added to your organ donations.");
                return "ORGAN INSERTED FOR USER ID: " + organ.getDonorId();
            } catch (SQLException e) {
                response.status(500);
                return "Internal Server Error";
            }

        }
    }

    /**
     * parses a donatable organ from a json string and removes it from the database
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String response if the operation was successful
     */
    public String removeOrgan(Request request, Response response) {
        Gson gson = new Gson();

        DonatableOrgan organ = gson.fromJson(request.body(), DonatableOrgan.class);
        if (organ == null) {
            response.status(400);
            return "Missing Organ Body";
        } else {
            try {
                model.removeOrgan(organ);
                response.status(201);
                PushAPI.getInstance().sendTextNotification((int)organ.getDonorId(), "Organ removed from donations.",
                        Organ.capitalise(organ.getOrganType().toString()) + " was removed from your organ donations.");
                return "ORGAN REMOVED FOR USER ID: " + organ.getDonorId();
            } catch (SQLException e) {
                response.status(500);
                return "Internal Server Error";
            }

        }
    }

    /**
     * parses a donatable organ from a json string and updates it into the database
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String response if the operation was successful
     */
    public String updateOrgan(Request request, Response response) {
        Gson gson = new Gson();

        DonatableOrgan organ = gson.fromJson(request.body(), DonatableOrgan.class);
        if (organ == null) {
            response.status(400);
            return "Missing Organ Body";
        } else {
            try {
                model.updateOrgan(organ);
                response.status(201);
                PushAPI.getInstance().sendTextNotification((int)organ.getDonorId(), "Organ donation updated.",
                        Organ.capitalise(organ.getOrganType().toString()) + " from your organ donations was updated.");
                return "ORGAN UPDATED FOR USER ID: " + organ.getDonorId();
            } catch (SQLException e) {
                response.status(500);
                return "Internal Server Error";
            }

        }
    }

    public String setInTransfer(Request request, Response response) {
        int organId = Integer.parseInt(request.params(":id"));
        int transferType = Integer.parseInt(request.params(":transferType"));
        try {
            model.inTransfer(organId, transferType);
            response.status(201);
            return "ORGAN  " + organId + " SET IN TRANSFER";
        } catch (SQLException e) {
            response.status(500);
            return "Internal Server Error";
        }
    }

    public String getUsersOrgans(Request request, Response response) {
        int userId = Integer.parseInt(request.params(":id"));
        List<DonatableOrgan> allDonatableOrgans;
        try {
            allDonatableOrgans = model.getSingUsersDonatableOrgans(userId);
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return e.getMessage();
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serializedOrgans = gson.toJson(allDonatableOrgans);

        response.type("application/json");
        response.status(200);
        return serializedOrgans;
    }

}
