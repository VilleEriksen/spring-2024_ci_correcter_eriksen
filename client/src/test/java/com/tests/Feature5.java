package com.tests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
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

public class Feature5 {
    
    private static TestClient testClient = null;
    private static TestSettings testSettings = null;
    private static auxiliaryLib auxLib = null;


    Feature5(){
        testSettings = new TestSettings();
        TestSettings.readSettingsXML("testconfigw5.xml");
        testClient = new TestClient(testSettings.getCertificate(), testSettings.getServerAddress(), testSettings.getNick(), testSettings.getPassword());
        auxLib = new auxiliaryLib();

    }

    @Test
    @BeforeAll
    @DisplayName("Setting up the test environment")
    public static void initialize() {
        System.out.println("initialized Feature 6 tests");
    }
    
    @Test
    @AfterAll
    public static void teardown() {
        System.out.println("Testing finished.");
    }


    @Test 
    @Order(1)
    @DisplayName("Testing sending messages with a weather indicator")
    void testSendCoordinates() throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        System.out.println("Testing sending messages with a weather indicator");
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

        auxiliaryLib.generateJSONObject(obj, "Kaivopuisto", "joku puisto",
        "Helsingfors", "Finland", "00140 Helsinki", dateText, 20.1, 30.1, "");

        int result = testClient.testJSONHTTPSMessage(obj, user);
        System.out.println("Recieved code from server after sending a message witouth originalPoster: " + result);

        obj.put("originalPoster", "Tallaaja_123");
        if(!(200 <= result && result <= 299)){
            result = testClient.testJSONHTTPSMessage(obj, user);
            System.out.println("Recieved code from server after sending a message with originalPoster: " + result);
        }
        
        assertTrue(200 <= result && result <= 299);

        String response = testClient.getHTTPSmessages(user);
        JSONArray arrayResponse = new JSONArray(response);

        JSONObject obj3 = new JSONObject();
        boolean comparisonResult = false;

        for(int i=0; i<arrayResponse.length(); i++){
            obj3 = arrayResponse.getJSONObject(i);

            JSONObject comparison = auxLib.generateSpecificJSONObject(obj3);

            //System.out.println(comparison);
            int weatherValue;
            if(comparison.has("weather"))
            {
                    Object whatDataFormat = comparison.get("weather");
                    if (whatDataFormat instanceof Integer){

                        weatherValue = comparison.getInt("weather");
                        
                    }else if (whatDataFormat instanceof Double){

                        Double val = comparison.getDouble("weather");
                        weatherValue = val.intValue();
                        
                    }else if (whatDataFormat instanceof BigDecimal){
                        BigDecimal val = comparison.getBigDecimal("weather");
                        weatherValue = val.intValue();
                        
                    }else{
                        String weath = comparison.getString("weather");
                        weath = weath.replaceAll("\\D+","");
                        
                        weatherValue = ((Float)Float.parseFloat(weath)).intValue();
                    }
                
                if(weatherValue > -100 && weatherValue < 100)
                {
                    comparisonResult = true;
                    break;
                }
            }

            
        }

        assertTrue(comparisonResult);
    }
}
