package com.tests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;

public class Feature8 {
    
    private static TestClient testClient = null;
    private static TestSettings testSettings = null;


    Feature8(){
        testSettings = new TestSettings();
        TestSettings.readSettingsXML("testconfigw5.xml");
        testClient = new TestClient(testSettings.getCertificate(), testSettings.getServerAddress(), testSettings.getNick(), testSettings.getPassword());

    }

    @Test
    @BeforeAll
    @DisplayName("Setting up the test environment")
    public static void initialize() {
        System.out.println("initialized Feature 8 tests");
    }
    
    @Test
    @AfterAll
    public static void teardown() {
        System.out.println("Testing finished.");
    }


    @Test 
    @Order(1)
    @DisplayName("Testing visiting locations")
    void testSendCoordinates() throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        JSONObject user = new JSONObject();

        user.put("username", "Seppo");
        user.put("password", "SeponSalainenSalasana");
        user.put("email", "Seppo@Seppo.com");
        user.put("userNickname", "Tallaaja_123");
        
        testClient.testRegisterUserJSON(user);

        JSONObject place1 = new JSONObject();
        JSONObject place2 = new JSONObject();
        JSONObject place3 = new JSONObject();
        JSONObject place4 = new JSONObject();
        JSONObject place5 = new JSONObject();

        ZonedDateTime now =ZonedDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd'T'HH:mm:ss.SSSX");

        String dateText = now.format(formatter);

        auxiliaryLib.generateJSONObject(place1, "Kaijon Kipsa", "Paras snägäri oulussa",
        "Oulu", "Finland", "Emmäämuista", dateText);

        int result = testClient.testJSONHTTPSMessage(place1, user);
        System.out.println("Recieved code from server after sending a message witouth originalPoster: " + result);

        place1.put("originalPoster", "Tallaaja_123");
        if(!(200 <= result && result <= 299)){
            result = testClient.testJSONHTTPSMessage(place1, user);
            System.out.println("Recieved code from server after sending a message with originalPoster: " + result);
        }

        assertTrue(200 <= result && result <= 299, "Sending a regular message failed to the server, check that your server accepts messages specified in minimum requirements");

        auxiliaryLib.generateJSONObject(place2, "Oulunsalon Lentokenttä", "airport",
        "Oulu", "Finland", "oulunsalo", dateText);

        result = testClient.testJSONHTTPSMessage(place2, user);
        System.out.println("Recieved code from server after sending a message witouth originalPoster: " + result);

        place2.put("originalPoster", "Tallaaja_123");
        if(!(200 <= result && result <= 299)){
            result = testClient.testJSONHTTPSMessage(place2, user);
            System.out.println("Recieved code from server after sending a message with originalPoster: " + result);
        }

        assertTrue(200 <= result && result <= 299, "Sending a regular message failed to the server, check that your server accepts messages specified in minimum requirements");

        auxiliaryLib.generateJSONObject(place3, "Nallikari Beach", "nallikarin ranta",
        "Oulu", "Finland", "nallikari", dateText);

        result = testClient.testJSONHTTPSMessage(place3, user);
        System.out.println("Recieved code from server after sending a message witouth originalPoster: " + result);

        place3.put("originalPoster", "Tallaaja_123");
        if(!(200 <= result && result <= 299)){
            result = testClient.testJSONHTTPSMessage(place3, user);
            System.out.println("Recieved code from server after sending a message with originalPoster: " + result);
        }

        assertTrue(200 <= result && result <= 299, "Sending a regular message failed to the server, check that your server accepts messages specified in minimum requirements");

        auxiliaryLib.generateJSONObject(place4, "Vanha kaupungintalo", "kaupungintalo remontissa",
        "Oulu", "Finland", "city center", dateText);

        result = testClient.testJSONHTTPSMessage(place4, user);
        System.out.println("Recieved code from server after sending a message witouth originalPoster: " + result);

        place4.put("originalPoster", "Tallaaja_123");
        if(!(200 <= result && result <= 299)){
            result = testClient.testJSONHTTPSMessage(place4, user);
            System.out.println("Recieved code from server after sending a message with originalPoster: " + result);
        }

        assertTrue(200 <= result && result <= 299, "Sending a regular message failed to the server, check that your server accepts messages specified in minimum requirements");

        auxiliaryLib.generateJSONObject(place5, "Domkyrka", "kirkko keskellä kaupunkia",
        "Oulu", "Finland", "city center", dateText);

        result = testClient.testJSONHTTPSMessage(place5, user);
        System.out.println("Recieved code from server after sending a message witouth originalPoster: " + result);

        place5.put("originalPoster", "Tallaaja_123");
        if(!(200 <= result && result <= 299)){
            result = testClient.testJSONHTTPSMessage(place5, user);
            System.out.println("Recieved code from server after sending a message with originalPoster: " + result);
        }

        assertTrue(200 <= result && result <= 299, "Sending a regular message failed to the server, check that your server accepts messages specified in minimum requirements");

        String response = testClient.getHTTPSmessages(user);

        JSONArray selectedPlaces = new JSONArray();

        selectedPlaces.put(place1);
        selectedPlaces.put(place2);
        selectedPlaces.put(place3);
        selectedPlaces.put(place4);
        selectedPlaces.put(place5);

        JSONArray placesForTour = new JSONArray(response);

        //getting id's from server

        for(int i=0; i<selectedPlaces.length(); i++){

            for(int ii = 0; ii<placesForTour.length(); ii++){

                if(placesForTour.getJSONObject(ii).get("locationName").equals(selectedPlaces.getJSONObject(i).get("locationName"))){
                    selectedPlaces.getJSONObject(i).put("locationID", placesForTour.getJSONObject(ii).getString("locationID"));
                    break;
                }
                
            }

        }

        //Setup some visits to locations

        JSONObject oneTimeVisit = new JSONObject();

        oneTimeVisit.put("locationID", selectedPlaces.getJSONObject(0).getString("locationID"));
        oneTimeVisit.put("locationVisitor", "Seppo");
        Integer timesToVisit = 10;

        for (int i = 0; i<timesToVisit; i++){
            result = testClient.testJSONHTTPSMessage(oneTimeVisit, user);
        }

        oneTimeVisit.clear();
        oneTimeVisit.put("locationID", selectedPlaces.getJSONObject(1).getString("locationID"));
        oneTimeVisit.put("locationVisitor", "Seppo");
        timesToVisit = 7;

        for (int i = 0; i<timesToVisit; i++){
            result = testClient.testJSONHTTPSMessage(oneTimeVisit, user);
        }

        oneTimeVisit.clear();
        oneTimeVisit.put("locationID", selectedPlaces.getJSONObject(2).getString("locationID"));
        oneTimeVisit.put("locationVisitor", "Seppo");
        timesToVisit = 5;

        for (int i = 0; i<timesToVisit; i++){
            result = testClient.testJSONHTTPSMessage(oneTimeVisit, user);
        }

        oneTimeVisit.clear();
        oneTimeVisit.put("locationID", selectedPlaces.getJSONObject(3).getString("locationID"));
        oneTimeVisit.put("locationVisitor", "Seppo");
        timesToVisit = 4;

        for (int i = 0; i<timesToVisit; i++){
            result = testClient.testJSONHTTPSMessage(oneTimeVisit, user);
        }

        oneTimeVisit.clear();
        oneTimeVisit.put("locationID", selectedPlaces.getJSONObject(4).getString("locationID"));
        oneTimeVisit.put("locationVisitor", "Seppo");
        timesToVisit = 2;

        for (int i = 0; i<timesToVisit; i++){
            result = testClient.testJSONHTTPSMessage(oneTimeVisit, user);
        }

        String topFive = testClient.getTopFive(user);

        JSONArray topFiveArray = new JSONArray(topFive);

        JSONArray topFiveTestComparison = new JSONArray();

        for(int i = 0; topFiveArray.length() > i; i++){

            if(topFiveArray.getJSONObject(i).getString("locationName").equals(place1.getString("locationName"))){

                topFiveArray.getJSONObject(i).remove("locationID");
                topFiveArray.getJSONObject(i).remove("timesVisited");
                JSONObject place = new JSONObject();
                place.put("locationName", place1.getString("locationName"));
                topFiveTestComparison.put(place);
                
            }
            if(topFiveArray.getJSONObject(i).getString("locationName").equals(place2.getString("locationName"))){

                topFiveArray.getJSONObject(i).remove("locationID");
                topFiveArray.getJSONObject(i).remove("timesVisited");
                JSONObject place = new JSONObject();
                place.put("locationName", place2.getString("locationName"));
                topFiveTestComparison.put(place);
                
            }
            if(topFiveArray.getJSONObject(i).getString("locationName").equals(place3.getString("locationName"))){

                topFiveArray.getJSONObject(i).remove("locationID");
                topFiveArray.getJSONObject(i).remove("timesVisited");
                JSONObject place = new JSONObject();
                place.put("locationName", place3.getString("locationName"));
                topFiveTestComparison.put(place);
                
            }
            if(topFiveArray.getJSONObject(i).getString("locationName").equals(place4.getString("locationName"))){

                topFiveArray.getJSONObject(i).remove("locationID");
                topFiveArray.getJSONObject(i).remove("timesVisited");
                JSONObject place = new JSONObject();
                place.put("locationName", place4.getString("locationName"));
                topFiveTestComparison.put(place);
                
            }
            if(topFiveArray.getJSONObject(i).getString("locationName").equals(place5.getString("locationName"))){

                topFiveArray.getJSONObject(i).remove("locationID");
                topFiveArray.getJSONObject(i).remove("timesVisited");
                JSONObject place = new JSONObject();
                place.put("locationName", place5.getString("locationName"));
                topFiveTestComparison.put(place);
                
            }

        }

        assertTrue(topFiveArray.similar(topFiveTestComparison));
    }


}
