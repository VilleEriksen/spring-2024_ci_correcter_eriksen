package com.tests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;

public class Feature6 {
    
    private static TestClient testClient = null;
    private static TestSettings testSettings = null;
    private static auxiliaryLib auxLib = null;


    Feature6(){
        testSettings = new TestSettings();
        TestSettings.readSettingsXML("testconfigw5.xml");
        testClient = new TestClient(testSettings.getCertificate(), testSettings.getServerAddress(), testSettings.getNick(), testSettings.getPassword());
        auxLib = new auxiliaryLib();
    }

    @Test
    @BeforeAll
    @DisplayName("Setting up the test environment")
    public static void initialize() {
        System.out.println("initialized Feature 7 tests");
    }
    
    @Test
    @AfterAll
    public static void teardown() {
        System.out.println("Testing finished.");
    }

    @Test 
    @Order(1)
    @DisplayName("Test for posting a tour information")
    void testRequestUserCoordinates() throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        System.out.println("Testing sending a tour information");
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

        ZonedDateTime now =ZonedDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd'T'HH:mm:ss.SSSX");

        String dateText = now.format(formatter);

        double coord1 = ThreadLocalRandom.current().nextDouble(0, 180);
        double coord2 = ThreadLocalRandom.current().nextDouble(0, 180);

        auxiliaryLib.generateJSONObject(place1, "Snägäri", "Paras snägäri oulussa",
        "Oulu", "Finland", "Emmäämuista", dateText, coord1, coord2, 0);

        int result = testClient.testJSONHTTPSMessage(place1, user);
        System.out.println("Recieved code from server after sending a message witouth originalPoster: " + result);

        place1.put("originalPoster", "Tallaaja_123");
        if(!(200 <= result && result <= 299)){
            result = testClient.testJSONHTTPSMessage(place1, user);
            System.out.println("Recieved code from server after sending a message with originalPoster: " + result);
        }

        assertTrue(200 <= result && result <= 299, "Sending a regular message failed to the server, check that your server accepts messages specified in minimum requirements");

        auxiliaryLib.generateJSONObject(place2, "Kaupungintalo", "Soon joku iso talo",
        "Oulu", "Finland", "siellä pysäkin vieressä, siel on joitain korjausjamppoja", dateText, coord1, coord2, 0);

        result = testClient.testJSONHTTPSMessage(place2, user);
        System.out.println("Recieved code from server after sending a message witouth originalPoster: " + result);

        place2.put("originalPoster", "Tallaaja_123");
        if(!(200 <= result && result <= 299)){
            result = testClient.testJSONHTTPSMessage(place2, user);
            System.out.println("Recieved code from server after sending a message with originalPoster: " + result);
        }

        assertTrue(200 <= result && result <= 299, "Sending a regular message failed to the server, check that your server accepts messages specified in minimum requirements");

        auxiliaryLib.generateJSONObject(place3, "Pallo", "Noon raahannu jonku pallon tänne, kantsii käydä kattoon",
        "Oulu", "Finland", "siellä missä se lava on", dateText, coord1, coord2, 0);

        result = testClient.testJSONHTTPSMessage(place3, user);
        System.out.println("Recieved code from server after sending a message witouth originalPoster: " + result);

        place3.put("originalPoster", "Tallaaja_123");
        if(!(200 <= result && result <= 299)){
            result = testClient.testJSONHTTPSMessage(place3, user);
            System.out.println("Recieved code from server after sending a message with originalPoster: " + result);
        }

        assertTrue(200 <= result && result <= 299, "Sending a regular message failed to the server, check that your server accepts messages specified in minimum requirements");

        auxiliaryLib.generateJSONObject(place4, "Suohalli", "joku pisti pystyyn hallin keskelle suota",
        "Oulu", "Finland", "mä kävelin pohjoseen, siellä se jossain o", dateText, coord1, coord2, 0);

        result = testClient.testJSONHTTPSMessage(place4, user);
        System.out.println("Recieved code from server after sending a message witouth originalPoster: " + result);

        place4.put("originalPoster", "Tallaaja_123");
        if(!(200 <= result && result <= 299)){
            result = testClient.testJSONHTTPSMessage(place4, user);
            System.out.println("Recieved code from server after sending a message with originalPoster: " + result);
        }

        assertTrue(200 <= result && result <= 299, "Sending a regular message failed to the server, check that your server accepts messages specified in minimum requirements");


        String response = testClient.getHTTPSmessages(user);

        JSONArray selectedPlaces = new JSONArray();

        selectedPlaces.put(place1);
        selectedPlaces.put(place2);
        selectedPlaces.put(place3);
        selectedPlaces.put(place4);

        JSONArray placesForTour = new JSONArray(response);

        //getting id's from server

        JSONArray placeIDs = new JSONArray();

        for(int i=0; i<selectedPlaces.length(); i++){

            for(int ii = 0; ii<placesForTour.length(); ii++){

                if(placesForTour.getJSONObject(ii).get("locationName").equals(selectedPlaces.getJSONObject(i).get("locationName"))){
                    selectedPlaces.getJSONObject(i).put("locationID", placesForTour.getJSONObject(ii).getString("locationID"));
                    JSONObject loc = new JSONObject();
                    loc.put("locationID", selectedPlaces.getJSONObject(i).getString("locationID"));
                    placeIDs.put(loc);

                    break;
                }
                
            }

        }



        JSONObject tour = new JSONObject();

        tour.put("tour_name", "Oulu Run");
        tour.put("tourDescription", "Some of my favorite places in Oulu");
        tour.put("locations" , placeIDs);

        testClient.createSightseeingPath(tour, user);


        String tours = testClient.getTours(user);

        JSONArray obj2 = new JSONArray(tours);

        boolean toursOK = false;
        JSONArray listOfLocationsFromServer = new JSONArray();

        

        for(int i=0; i<obj2.length(); i++){

            JSONObject obj3 = obj2.getJSONObject(i);

            if (obj3.getString("tour_name").equals(tour.getString("tour_name")))
            { 
                listOfLocationsFromServer = obj3.getJSONArray("locations");

                toursOK = listOfLocationsFromServer.similar(selectedPlaces);
                break; //found the place, ending loop
            }

        }

        assertTrue(toursOK);

    }

}
