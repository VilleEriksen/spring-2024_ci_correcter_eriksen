package com.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
import org.junit.jupiter.api.RepeatedTest;

public class Week5 {

    private static TestClient testClient = null;
    private static TestSettings testSettings = null;

    Week5() {
        testSettings = new TestSettings();
        TestSettings.readSettingsXML("testconfigw5.xml");
        testClient = new TestClient(testSettings.getCertificate(), testSettings.getServerAddress(),
                testSettings.getNick(), testSettings.getPassword());

    }

    @Test
    @BeforeAll
    @DisplayName("Setting up the test environment")
    public static void initialize() {
        System.out.println("initialized week 5 test set");
    }

    @Test
    @AfterAll
    public static void teardown() {
        System.out.println("Testing finished.");
    }

    @Test
    @Order(1)
    @DisplayName("Testing server connection")
    void testHTTPServerConnection() throws IOException, KeyManagementException, KeyStoreException, CertificateException,
            NoSuchAlgorithmException, URISyntaxException {
        System.out.println("Testing server connection");
        int result = testClient.testHTTPSConnection();
        assertTrue(result > 1);
    }

    @Test
    @Order(2)
    @DisplayName("Testing registering an user")
    void testRegisterUser() throws IOException, KeyManagementException, KeyStoreException, CertificateException,
            NoSuchAlgorithmException, URISyntaxException {
        System.out.println("Testing registering an user");
        JSONObject obj = new JSONObject();
        obj.put("username", "jokurandom");
        obj.put("password", "jokurandompsw");
        obj.put("email", "joku@random.com");
        obj.put("userNickname", "Pekka");

        int result = testClient.testRegisterUserJSON(obj);
        System.out.println(result);
        assertTrue(200 <= result && result <= 299, "Test failed, user registration returned code " + result);
    }

    @Test
    @Order(3)
    @DisplayName("Testing registering same user again - must fail")
    void testRegisterUserAgain() throws IOException, KeyManagementException, KeyStoreException, CertificateException,
            NoSuchAlgorithmException, URISyntaxException {
                JSONObject obj = new JSONObject();
        obj.put("username", "jokurandom1");
        obj.put("password", "jokurandompsw1");
        obj.put("email", "joku@random1.com");
        obj.put("userNickname", "Pekka1");
        
        testClient.testRegisterUserJSON(obj);
        int result = testClient.testRegisterUserJSON(obj);
        
        System.out.println(result);
        assertFalse(200 <= result && result <= 299, "Test failed, user registration returned code " + result);
    }

    @Test 
    @Order(4)
    @DisplayName("Testing sending message to server")
    void testSendMessage() throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        System.out.println("Testing sending message to server");

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
        "Helsingfors", "Finland", "00140 Helsinki", dateText);

        int result = testClient.testJSONHTTPSMessage(obj, user);
        System.out.println("Recieved code from server after sending a message witouth originalPoster: " + result);

        obj.put("originalPoster", "Tallaaja_123");
        if(!(200 <= result && result <= 299)){
            result = testClient.testJSONHTTPSMessage(obj, user);
            System.out.println("Recieved code from server after sending a message with originalPoster: " + result);
        }

        assertTrue(200 <= result && result <= 299);
        String response = testClient.getHTTPSmessages(user);
        JSONArray obj2 = new JSONArray(response);
        System.out.println("Response to compare at :" + response);
        System.out.println("Orignal object is" + obj);
        //Jokin joukko jsonobjekteja, joista pitää tunnistaa lähetetty objekti...
        boolean isSame = false;
        JSONObject obj3 = new JSONObject();
        for(int i=0; i<obj2.length(); i++){
            obj3 = obj2.getJSONObject(i);

                //feature compliancy...
                if(obj3.has("locationID")){
                    obj3.remove("locationID");
                }
                if(obj3.has("timesVisited")){
                    obj3.remove("timesVisited");
                }

            System.out.println(obj3);
            if(obj.similar(obj3))
            {isSame = true; break;}
        }

        assertTrue(isSame);

    }

    @Test 
    @Order(5)
    @DisplayName("Testing characters")
    void testCharacters() throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        System.out.println("Testing characters in message");
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
        
        auxiliaryLib.generateJSONObject(obj, "Kilpisjärvi", "Syksi Suomen pohjoisimmista järvistä",
         "emmäätiiäonkotääkaupunki", "Finland", "ossain kilpisjärvellä", dateText);

         int result = testClient.testJSONHTTPSMessage(obj, user);
        System.out.println("Recieved code from server after sending a message witouth originalPoster: " + result);

        obj.put("originalPoster", "Tallaaja_123");
        if(!(200 <= result && result <= 299)){
            result = testClient.testJSONHTTPSMessage(obj, user);
            System.out.println("Recieved code from server after sending a message with originalPoster: " + result);
        }

        assertTrue(200 <= result && result <= 299);
        String response = testClient.getHTTPSmessages(user);
        JSONArray obj2 = new JSONArray(response);
        System.out.println("Response to compare :" + response);
        System.out.println("Orginal object is: " + obj);
        //Jokin joukko jsonobjekteja, joista pitää tunnistaa lähetetty objekti...
        boolean isSame = false;
        JSONObject obj3 = new JSONObject();
        for(int i=0; i<obj2.length(); i++){
            obj3 = obj2.getJSONObject(i);
                //feature compliancy...
                if(obj3.has("locationID")){
                    obj3.remove("locationID");
                }
                if(obj3.has("timesVisited")){
                    obj3.remove("timesVisited");
                }
            System.out.println(obj3);
            if(obj.similar(obj3))
            {isSame = true; break;}
        }

        assertTrue(isSame);
    }

    @Test
    @Order(6)
    @DisplayName("Testing message with additional information")
    void testCoordinateDescription() throws IOException, KeyManagementException, KeyStoreException,
            CertificateException, NoSuchAlgorithmException, URISyntaxException {
        System.out.println("Testing message with additional information");
        JSONObject user = new JSONObject();

        user.put("username", "Seppo");
        user.put("password", "SeponSalainenSalasana");
        user.put("email", "Seppo@Seppo.com");
        user.put("userNickname", "Tallaaja_123");
        
        testClient.testRegisterUserJSON(user);

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd'T'HH:mm:ss.SSSX");

        String dateText = now.format(formatter);

        double coord1 = ThreadLocalRandom.current().nextDouble(0, 180);
        double coord2 = ThreadLocalRandom.current().nextDouble(0, 180);

        JSONObject obj = new JSONObject();
        
        auxiliaryLib.generateJSONObject(obj, "Place A", "Some random place A",
         "City of B", "In a C country", "Street of D", dateText, coord1, coord2);
        
         int result = testClient.testJSONHTTPSMessage(obj, user);
        System.out.println("Recieved code from server after sending a message witouth originalPoster: " + result);

        obj.put("originalPoster", "Tallaaja_123");
        if(!(200 <= result && result <= 299)){
            result = testClient.testJSONHTTPSMessage(obj, user);
            System.out.println("Recieved code from server after sending a message with originalPoster: " + result);
        }

        assertTrue(200 <= result && result <= 299, "Test failed with additional coordinate information added to the user message. Check if your system can accept messages with or witouth latitude and longitude coordinates");
        String response = testClient.getHTTPSmessages(user);

        JSONArray obj2 = new JSONArray(response);
        System.out.println(response);
        System.out.println("original is " + obj);
        System.out.println("object is " + obj2);
        // Jokin joukko jsonobjekteja, joista pitää tunnistaa lähetetty objekti...
        boolean isSame = false;
        JSONObject obj3 = new JSONObject();
        for (int i = 0; i < obj2.length(); i++) {
            obj3 = obj2.getJSONObject(i);

                //feature compliancy...
                if(obj3.has("locationID")){
                    obj3.remove("locationID");
                }
                if(obj3.has("timesVisited")){
                    obj3.remove("timesVisited");
                }

            // System.out.println(comparison);
            if (obj.similar(obj3)) {
                isSame = true;
                break;
            }
        }

        assertTrue(isSame, "Test failed, Object " + obj.toString() + " could not be found in response");
    }

    @Test 
    @Order(7)
    @DisplayName("Testing faulty time")
    void testFaultyTime() throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        System.out.println("Testing time in message");
        JSONObject user = new JSONObject();

        user.put("username", "Seppo");
        user.put("password", "SeponSalainenSalasana");
        user.put("email", "Seppo@Seppo.com");
        user.put("userNickname", "Tallaaja_123");
        
        testClient.testRegisterUserJSON(user);

        ZonedDateTime now =ZonedDateTime.now(ZoneId.of("UTC"));

        JSONObject obj = new JSONObject();
        
        auxiliaryLib.generateJSONObject(obj, "Place A", "Some random place A",
         "City of B", "In a C country", "Street of D", now.toString());
  
        int result = testClient.testJSONHTTPSMessage(obj, user);
        System.out.println("Recieved code from server after sending a message witouth originalPoster: " + result);

        obj.put("originalPoster", "Tallaaja_123");
        if(!(200 <= result && result <= 299)){
            result = testClient.testJSONHTTPSMessage(obj, user);
            System.out.println("Recieved code from server after sending a message with originalPoster: " + result);
        }

        System.out.println(result);
        assertFalse(200 <= result && result <= 299);
        System.out.println("Test failed, time format was wrong, make sure that you check that the time is correct in your code");
        
    }


    @Test 
    @Order(8)
    @DisplayName("Sending empty string to registration - must fail")
    void testRegisterRubbish() throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        System.out.println("Testing sending empty string to registration");
        int result = testClient.testRegisterUserJSON("", "", "");
        System.out.println(result);
        assertFalse(200 <= result && result <= 299);

    }

    @Test 
    @Order(9)
    @DisplayName("Sending GET to registration - must fail")
    void testRegisterGet() throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        System.out.println("Sending GET to registration - must fail");
        int result = testClient.testRegisterGet();
        System.out.println(result);
        assertFalse(200 <= result && result <= 299);

    }
    @Test
    @Order(7)
    @DisplayName("Testing faulty message")
    void testFaultyMessage() throws IOException, KeyManagementException, KeyStoreException, CertificateException,
            NoSuchAlgorithmException, URISyntaxException {
        System.out.println("Testing faulty attributes in message");
        JSONObject user = new JSONObject();

        user.put("username", "Seppo");
        user.put("password", "SeponSalainenSalasana");
        user.put("email", "Seppo@Seppo.com");
        user.put("userNickname", "Tallaaja_123");
        
        testClient.testRegisterUserJSON(user);
        JSONObject obj = new JSONObject();

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd'T'HH:mm:ss.SSSX");

        String dateText = now.format(formatter);

        obj.put("locationDescription", "Virhe");
        obj.put("latitude", "stuff");
        obj.put("longitude", 1);
        obj.put("originalPostingTime", dateText);
        obj.put("locationCity", "Reindeer");
        obj.put("locationStreetAddress", 2);
        int result = testClient.testJSONHTTPSMessage(obj, user);
                System.out.println("Recieved code from server after sending a message witouth originalPoster: " + result);

                obj.put("originalPoster", "Tallaaja_123");
                if(!(200 <= result && result <= 299)){
                    result = testClient.testJSONHTTPSMessage(obj, user);
                    System.out.println("Recieved code from server after sending a message with originalPoster: " + result);
                }

        assertFalse(200 <= result && result <= 299,
                "Test failed, make sure that you check the jsonobject from user is correct in your code");
    }

    @RepeatedTest(200)
    @Execution(ExecutionMode.CONCURRENT)
    @Order(11)
    @DisplayName("Server load test with large amount of messages")
    void testServerLoading() throws IOException, KeyManagementException, KeyStoreException, CertificateException,
            NoSuchAlgorithmException, URISyntaxException {
        System.out.println("Server load test with large amount of messages");
        JSONObject user = new JSONObject();

        user.put("username", "Seppo");
        user.put("password", "SeponSalainenSalasana");
        user.put("email", "Seppo@Seppo.com");
        user.put("userNickname", "Tallaaja_123");
        
        testClient.testRegisterUserJSON(user);
        JSONObject obj = new JSONObject();

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd'T'HH:mm:ss.SSSX");

        String dateText = now.format(formatter);

        double coord1 = ThreadLocalRandom.current().nextDouble(0, 180);
        double coord2 = ThreadLocalRandom.current().nextDouble(0, 180);

        auxiliaryLib.generateJSONObject(obj, "Place A", "Some random place A",
         "City of B", "In a C country", "Street of D", dateText.toString(), coord1, coord2);
  
        int result = testClient.testJSONHTTPSMessage(obj, user);
        System.out.println("Recieved code from server after sending a message witouth originalPoster: " + result);

        obj.put("originalPoster", "Tallaaja_123");
        if(!(200 <= result && result <= 299)){
            result = testClient.testJSONHTTPSMessage(obj, user);
            System.out.println("Recieved code from server after sending a message with originalPoster: " + result);
        }

        assertTrue(200 <= result && result <= 299,
                "Test failed, posting message returned code " + result + " when loaded");
    }


    @Test
    @Order(13)
    @DisplayName("Testing faulty message 3 - not json")//TODO: Testaa korjaus käyttäjän tunnistukseen
    void testFaultyMessage3() throws IOException, KeyManagementException, KeyStoreException, CertificateException,
            NoSuchAlgorithmException, URISyntaxException {
        System.out.println("Testing faulty json in message");
        JSONObject user = new JSONObject();

        user.put("username", "Seppo");
        user.put("password", "SeponSalainenSalasana");
        user.put("email", "Seppo@Seppo.com");
        user.put("userNickname", "Tallaaja_123");
        
        testClient.testRegisterUserJSON(user);

        int result = testClient.testJSONHTTPSMessage("{I am not a json}", user);
        System.out.println(result);
        assertFalse(200 <= result && result <= 299, "Test failed, a faulty json was supplied");
    }

    @Test
    @Order(14)
    @DisplayName("Testing faulty credentials")
    void testFaultyCredentials() throws IOException, KeyManagementException, KeyStoreException, CertificateException,
            NoSuchAlgorithmException, URISyntaxException {
        System.out.println("Testing faulty credentials in message");
        JSONObject user = new JSONObject();

        user.put("username", "Matti");
        user.put("password", "MatinSalainenSalasana");
        user.put("email", "Matti@Matti.com");
        user.put("userNickname", "Tallaaja_123");
        
        testClient.testRegisterUserJSON(user);
        JSONObject obj = new JSONObject();

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd'T'HH:mm:ss.SSSX");

        String dateText = now.format(formatter);

        double coord1 = ThreadLocalRandom.current().nextDouble(0, 180);
        double coord2 = ThreadLocalRandom.current().nextDouble(0, 180);

       auxiliaryLib.generateJSONObject(obj, "Place A", "Some random place A",
         "City of B", "In a C country", "Street of D", dateText, coord1, coord2);
        
        int result = testClient.testJSONHTTPSMessage(obj, "Matti:NotMattisPassowrd");
        System.out.println("Recieved code from server after sending a message witouth originalPoster: " + result);

        obj.put("originalPoster", "Tallaaja_123");
        if(!(200 <= result && result <= 299)){
            result = testClient.testJSONHTTPSMessage(obj, "Matti:NotMattisPassowrd");
            System.out.println("Recieved code from server after sending a message with originalPoster: " + result);
        }

        System.out.println(result);
        assertFalse(200 <= result && result <= 299, "Test failed, wrong password was used");
    }

}
