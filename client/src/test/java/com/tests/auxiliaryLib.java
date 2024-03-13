package com.tests;

import org.json.JSONObject;

public class auxiliaryLib {
    
    public static JSONObject generateJSONObject(JSONObject obj, String stringLoc, String stringDesc, String stringCity, String stringCountry, String stringStreet, String stringDate){

        genericJsonMessage(obj, stringLoc, stringDesc, stringCity, stringCountry, stringStreet, stringDate);

        return obj;

    }

    public static JSONObject generateJSONObject(JSONObject obj, String stringLoc, String stringDesc, String stringCity,
     String stringCountry, String stringStreet, String stringDate, double doubleLon, double doubleLat, String weather){

        generateJSONObject(obj, stringLoc, stringDesc, stringCity, stringCountry, stringStreet, stringDate, doubleLon, doubleLat);

        obj.put("weather", "");

        return obj;

    }

    public static JSONObject generateJSONObject(JSONObject obj, String stringLoc, String stringDesc, String stringCity, String stringCountry, String stringStreet, String stringDate, double doubleLon, double doubleLat){

        genericJsonMessage(obj, stringLoc, stringDesc, stringCity, stringCountry, stringStreet, stringDate);

        obj.put("longitude", doubleLon);

        obj.put("latitude", doubleLat);

        return obj;

    }

    public static JSONObject generateJSONObject(JSONObject obj, String stringLoc, String stringDesc, String stringCity, String stringCountry, String stringStreet, String stringDate, double doubleLon, double doubleLat, int visits){

        generateJSONObject(obj, stringLoc, stringDesc, stringCity, stringCountry, stringStreet, stringDate, doubleLon, doubleLat);
        
        obj.put("timesVisited", visits);

        return obj;

    }

    private static void genericJsonMessage (JSONObject obj, String stringLoc, String stringDesc, String stringCity, String stringCountry, String stringStreet, String stringDate){
        if(!stringLoc.isEmpty()){
            obj.put("locationName", stringLoc);
        }
        if(!stringDesc.isEmpty()){
            obj.put("locationDescription", stringDesc);
        }
        if(!stringCity.isEmpty()){
            obj.put("locationCity", stringCity);
        }
        if(!stringCountry.isEmpty()){
            obj.put("locationCountry", stringCountry);
        }
        if(!stringStreet.isEmpty()){
            obj.put("locationStreetAddress", stringStreet);
        }
        if(!stringDate.isEmpty()){
            obj.put("originalPostingTime", stringDate);
        }
    }

    public JSONObject generateSpecificJSONObject (JSONObject originalObject){

        JSONObject specificObject = new JSONObject();

        specificObject.put("locationName", originalObject.getString("locationName"));
        specificObject.put("locationDescription", originalObject.getString("locationDescription"));
        specificObject.put("locationCity", originalObject.getString("locationCity"));
        specificObject.put("locationCountry", originalObject.getString("locationCountry"));
        specificObject.put("locationStreetAddress", originalObject.getString("locationStreetAddress"));
        specificObject.put("originalPostingTime", originalObject.getString("originalPostingTime"));
        specificObject.put("originalPoster", originalObject.getString("originalPoster"));

        if(originalObject.has("longitude") && originalObject.has("latitude")){
            specificObject.put("longitude", originalObject.get("longitude"));
            specificObject.put("latitude", originalObject.get("latitude"));
        }

        //Weather compliency
        if(originalObject.has("weather")){
            specificObject.put("weather", originalObject.get("weather"));
        }

        //If item has an id
        if(originalObject.has("locationID")){
            specificObject.put("locationID", originalObject.getString("locationID"));
        }

        //Update feature
        if(originalObject.has("updatereason")){
            specificObject.put("updatereason", originalObject.getString("updatereason"));
        }
        if(originalObject.has("modified")){
            specificObject.put("modified", originalObject.get("modified"));
        }

        return specificObject;
    }


}

