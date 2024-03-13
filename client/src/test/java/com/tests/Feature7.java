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
import java.util.concurrent.ThreadLocalRandom;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;

public class Feature7 {
    
    private static TestClient testClient = null;
    private static TestSettings testSettings = null;
    private static auxiliaryLib auxLib = null;


    Feature7(){
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
    @DisplayName("Testing if messages can be edited")
    void testEditCoordinate() throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        JSONObject user = new JSONObject();

        user.put("username", "Seppo");
        user.put("password", "SeponSalainenSalasana");
        user.put("email", "Seppo@Seppo.com");
        user.put("userNickname", "Tallaaja_123");
        
        testClient.testRegisterUserJSON(user);

        JSONObject obj = new JSONObject();

        ZonedDateTime now =ZonedDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd'T'HH:mm:ss.SSSX");

        String dateText = now.format(formatter);

        auxiliaryLib.generateJSONObject(obj, "Editointipuisto", "joku puisto",
        "Jossain", "Finland", "joku paikka", dateText);

        int result = testClient.testJSONHTTPSMessage(obj, user);
        System.out.println("Recieved code from server after sending a message witouth originalPoster: " + result);

        obj.put("originalPoster", "Tallaaja_123");
        if(!(200 <= result && result <= 299)){
            result = testClient.testJSONHTTPSMessage(obj, user);
            System.out.println("Recieved code from server after sending a message with originalPoster: " + result);
        }
        
        assertTrue(200 <= result && result <= 299);


        String response = testClient.getHTTPSmessages(user);

        JSONArray responseArray = new JSONArray(response);

        JSONObject obj3 = new JSONObject();
        boolean hasBeenModified = false;

        for(int i=0; i<responseArray.length(); i++){
            obj3 = responseArray.getJSONObject(i);
            JSONObject comparison = auxLib.generateSpecificJSONObject(obj3);

            int storedID = comparison.getInt("locationID");
            //removing the id field to allow comparison
            comparison.remove("locationID");

            if(obj.similar(comparison))
            {

                JSONObject objModified = new JSONObject();
        
                //using the removed id if found similar what was sent
                auxiliaryLib.generateJSONObject(objModified, "Seponpuisto Edit", "joku puisto",
                "Seppomaa", "Finland", "Seponkatu", dateText);

                objModified.put("locationID", storedID);
                objModified.put("originalPoster", "Tallaaja_123");
                objModified.put("updatereason", "I now remember what was this place");
        
                int responseDelete = testClient.testJSONHTTPSMessage(objModified, user);
                assertTrue(200 <= responseDelete && responseDelete <= 299);
        
                String response2 = testClient.getHTTPSmessages(user);
                JSONArray modifyResponse = new JSONArray(response2);

                System.out.println("Response from server: " + modifyResponse);

                JSONObject obj4 = new JSONObject();
                for(int ii=0; ii<modifyResponse.length(); ii++){
                    obj4 = modifyResponse.getJSONObject(i);
                    System.out.println(obj4.toString());
                    JSONObject comparison2 = auxLib.generateSpecificJSONObject(obj4);

                    System.out.println(comparison2.toString());
                    
                    if(comparison2.has("modified")){
                        //modified field found, removing the modified field to allow comparison to original
                        comparison2.remove("modified");

                        if(objModified.similar(comparison2))
                        {hasBeenModified = true;break;}
                    }

                }

                break; //break the for loop if match found
            }
        }

        assertTrue(hasBeenModified);
    }

}
