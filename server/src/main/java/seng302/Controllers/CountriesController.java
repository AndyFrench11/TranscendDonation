package seng302.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import seng302.Logic.Database.GeneralCountries;
import seng302.Model.Country;
import spark.Request;
import spark.Response;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CountriesController {

    private GeneralCountries model;

    /**
     * Constructs a new CountriesController
     */
    public CountriesController() {
        model = new GeneralCountries();
    }

    /**
     * Returns all the countries and whether they are valid or not.
     * @param request the request body
     * @param response the response body
     * @return A json string of all the countries and if they are valid or not
     */
    public String getCountries(Request request, Response response){
        List<Country> queriedCountries;
        try {
            queriedCountries = model.getCountries();
        } catch (SQLException e) {
            response.status(500);
            return e.getMessage();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedCountries = gson.toJson(queriedCountries);

        response.type("application/json");
        response.status(200);
        return serialQueriedCountries;
    }

    /**
     * Updates the countries which are valid or not.
     * @param request the request body
     * @param response the response body
     * @return return if it worked or not
     */
    public String patchCountries(Request request, Response response){
        try{
            System.out.println(request.body());
             model.patchCounties(new Gson().fromJson(request.body(), new TypeToken<List<Country>>(){}.getType()));
        } catch (SQLException e) {
            response.status(500);
            System.out.println(e.getMessage());
        }

        response.type("application/json");
        response.status(200);
        return "Done";
    }
}
