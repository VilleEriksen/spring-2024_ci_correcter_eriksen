package com.tests;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.json.JSONObject;


/**
 * Test client for O3 tests
 */
public final class TestClient {

    String username;
    String password;
    String auth;
    String fullAddress;
    static Client client;

    private static final int CONNECT_TIMEOUT = 10 * 1000;
	private static final int REQUEST_TIMEOUT = 30 * 1000;

    private static final String USER_AGENT = "Mozilla/5.0";

    public TestClient(String address){

        client = new Client(address);
        System.out.println("Test client created");

    }

    public TestClient(String keystore, String address, String newUser, String newPassword) {

        client = new Client(keystore, address);
        username = newUser;
        password = newPassword;
        auth = username + ":" + password;

    }

  
    public synchronized int testConnection() throws IOException, URISyntaxException{

        int responseCode = 200;

        fullAddress = client.getServerAddress();
        fullAddress += client.getMessageContext();

        URL url = new URI(fullAddress).toURL();

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		responseCode = con.getResponseCode();

        return responseCode;
    }

    public synchronized int testHTTPSConnection() throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException{

        int responseCode = 0;

        fullAddress = client.getServerAddress();
        fullAddress += client.getMessageContext();

        URL url = new URI(fullAddress).toURL();

        HttpURLConnection con = createTrustingConnection(url);
		con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "text/plain");

        responseCode = con.getResponseCode();

        return responseCode;
    }

    public synchronized int testMessage(String message) throws IOException, URISyntaxException {
        int responseCode = 400;
        byte[] msgBytes;
        fullAddress = client.getServerAddress();
        fullAddress += client.getMessageContext();

        System.out.println(fullAddress);

        URL url = new URI(fullAddress).toURL();

        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        msgBytes = message.getBytes("UTF-8");
        con.setRequestProperty("Content-Type", "text/plain");

		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Length", String.valueOf(msgBytes.length));

		OutputStream writer = con.getOutputStream();
		writer.write(msgBytes);
		writer.close();


		responseCode = con.getResponseCode();

        return responseCode;
    }



    public synchronized String getMessages() throws IOException, URISyntaxException {

        int responseCode = 400;

        fullAddress = client.getServerAddress();
        fullAddress += client.getMessageContext();
        
        URL url = new URI(fullAddress).toURL();

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "text/plain");


		responseCode = con.getResponseCode();

        ArrayList<String> response = new ArrayList<String>();
        String input;
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));

        while ((input = in.readLine()) != null) {
            response.add(input);
        }

        String result = response.get(response.size()-1);

        return result;
    }

    public int testRegisterUser(String newUsername, String newPassword) throws KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, FileNotFoundException, IOException, URISyntaxException {
        int responseCode = 400;
        byte[] msgBytes;

        fullAddress = client.getServerAddress();
        fullAddress += client.getRegisterContext();

        URL url = new URI(fullAddress).toURL();

        HttpURLConnection con = createTrustingConnection(url);
	
        String userPayload = newUsername + ":" + newPassword;
        msgBytes = userPayload.getBytes("UTF-8");
        con.setRequestProperty("Content-Type", "text/plain");

		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Length", String.valueOf(msgBytes.length));

        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
		String authHeaderValue = "Basic " + new String(encodedAuth);
		con.setRequestProperty("Authorization", authHeaderValue);

		OutputStream writer = con.getOutputStream();
		writer.write(msgBytes);
		writer.close();


		responseCode = con.getResponseCode();

        return responseCode;
    }



    public synchronized int testHTTPSMessage(String message) throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        int responseCode = 400;
        byte[] msgBytes;

        fullAddress = client.getServerAddress();
        fullAddress += client.getMessageContext();

        URL url = new URI(fullAddress).toURL();

        HttpURLConnection con = createTrustingConnection(url);
	
        msgBytes = message.getBytes("UTF-8");
        con.setRequestProperty("Content-Type", "text/plain");

		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Length", String.valueOf(msgBytes.length));

        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
		String authHeaderValue = "Basic " + new String(encodedAuth);
		con.setRequestProperty("Authorization", authHeaderValue);

		OutputStream writer = con.getOutputStream();
		writer.write(msgBytes);
		writer.close();


		responseCode = con.getResponseCode();

        return responseCode;
    }

    public synchronized String getHTTPSmessages() throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        int responseCode = 400;

        fullAddress = client.getServerAddress();
        fullAddress += client.getMessageContext();

        URL url = new URI(fullAddress).toURL();

        HttpURLConnection con = createTrustingConnection(url);
		con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");

        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
		String authHeaderValue = "Basic " + new String(encodedAuth);
		con.setRequestProperty("Authorization", authHeaderValue);

        System.out.println("done setting GET");
        responseCode = con.getResponseCode();
        System.out.println(responseCode);

        ArrayList<String> response = new ArrayList<String>();
        String input;
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));

        while ((input = in.readLine()) != null) {
            response.add(input);
        }

        String result = response.get(response.size()-1);

        return result;
    }

    public synchronized String getHTTPSmessages(JSONObject auth) throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        int responseCode = 400;

        fullAddress = client.getServerAddress();
        fullAddress += client.getMessageContext();

        URL url = new URI(fullAddress).toURL();

        HttpURLConnection con = createTrustingConnection(url);
		con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");

        String authString = auth.getString("username") + ":" + auth.getString("password");

        byte[] encodedAuth = Base64.getEncoder().encode(authString.getBytes(StandardCharsets.UTF_8));
		String authHeaderValue = "Basic " + new String(encodedAuth);
		con.setRequestProperty("Authorization", authHeaderValue);

        System.out.println("done setting GET");
        responseCode = con.getResponseCode();
        System.out.println(responseCode);

        ArrayList<String> response = new ArrayList<String>();
        String input;
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));

        while ((input = in.readLine()) != null) {
            response.add(input);
        }

        String result = response.get(response.size()-1);

        return result;
    }

    public synchronized String getTours(JSONObject auth) throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        int responseCode = 400;

        fullAddress = client.getServerAddress();
        fullAddress += client.getPathsContext();

        URL url = new URI(fullAddress).toURL();

        HttpURLConnection con = createTrustingConnection(url);
		con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");

        String authString = auth.getString("username") + ":" + auth.getString("password");

        byte[] encodedAuth = Base64.getEncoder().encode(authString.getBytes(StandardCharsets.UTF_8));
		String authHeaderValue = "Basic " + new String(encodedAuth);
		con.setRequestProperty("Authorization", authHeaderValue);

        System.out.println("done setting GET");
        responseCode = con.getResponseCode();
        System.out.println(responseCode);

        ArrayList<String> response = new ArrayList<String>();
        String input;
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));

        while ((input = in.readLine()) != null) {
            response.add(input);
        }

        String result = response.get(response.size()-1);

        return result;
    }

    public synchronized String getTopFive(JSONObject auth) throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        int responseCode = 400;

        fullAddress = client.getServerAddress();
        fullAddress += client.getTopFiveContext();

        URL url = new URI(fullAddress).toURL();

        HttpURLConnection con = createTrustingConnection(url);
		con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");

        String authString = auth.getString("username") + ":" + auth.getString("password");

        byte[] encodedAuth = Base64.getEncoder().encode(authString.getBytes(StandardCharsets.UTF_8));
		String authHeaderValue = "Basic " + new String(encodedAuth);
		con.setRequestProperty("Authorization", authHeaderValue);

        System.out.println("done setting GET");
        responseCode = con.getResponseCode();
        System.out.println(responseCode);

        ArrayList<String> response = new ArrayList<String>();
        String input;
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));

        while ((input = in.readLine()) != null) {
            response.add(input);
        }

        String result = response.get(response.size()-1);

        return result;
    }

    public int testRegisterGet() throws KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, FileNotFoundException, IOException, URISyntaxException {
        int responseCode = 400;

        fullAddress = client.getServerAddress();
        fullAddress += client.getRegisterContext();

        URL url = new URI(fullAddress).toURL();

        HttpURLConnection con = createTrustingConnection(url);

        con.setRequestProperty("Content-Type", "text/plain");

		con.setRequestMethod("GET");
		con.setDoOutput(true);
		con.setDoInput(true);

        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
		String authHeaderValue = "Basic " + new String(encodedAuth);
		con.setRequestProperty("Authorization", authHeaderValue);

		responseCode = con.getResponseCode();

        return responseCode;
    }

    public int testRegisterUserJSON(String newUsername, String newPassword, String newEmail) throws KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, FileNotFoundException, IOException, URISyntaxException {
        int responseCode = 400;
        byte[] msgBytes;

        fullAddress = client.getServerAddress();
        fullAddress += client.getRegisterContext();

        URL url = new URI(fullAddress).toURL();

        HttpURLConnection con = createTrustingConnection(url);
	
        JSONObject obj = new JSONObject();
        obj.put("username", newUsername);
        obj.put("password", newPassword);
        obj.put("email", newEmail);
        String userPayload = obj.toString();
        msgBytes = userPayload.getBytes("UTF-8");
        con.setRequestProperty("Content-Type", "application/json");

		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Length", String.valueOf(msgBytes.length));

        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
		String authHeaderValue = "Basic " + new String(encodedAuth);
		con.setRequestProperty("Authorization", authHeaderValue);

		OutputStream writer = con.getOutputStream();
		writer.write(msgBytes);
		writer.close();


		responseCode = con.getResponseCode();
        System.out.println(responseCode);

        return responseCode;
    }

    public int testRegisterUserJSON(JSONObject obj) throws KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, FileNotFoundException, IOException, URISyntaxException {
        int responseCode = 400;
        byte[] msgBytes;

        fullAddress = client.getServerAddress();
        fullAddress += client.getRegisterContext();

        URL url = new URI(fullAddress).toURL();

        HttpURLConnection con = createTrustingConnection(url);
	
        String userPayload = obj.toString();
        msgBytes = userPayload.getBytes("UTF-8");
        con.setRequestProperty("Content-Type", "application/json");

		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Length", String.valueOf(msgBytes.length));

        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
		String authHeaderValue = "Basic " + new String(encodedAuth);
		con.setRequestProperty("Authorization", authHeaderValue);

		OutputStream writer = con.getOutputStream();
		writer.write(msgBytes);
		writer.close();


		responseCode = con.getResponseCode();
        System.out.println(responseCode);

        return responseCode;
    }

    public synchronized int testJSONHTTPSMessage(JSONObject obj) throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        int responseCode = 400;
        byte[] msgBytes;

        fullAddress = client.getServerAddress();
        fullAddress += client.getMessageContext();

        URL url = new URI(fullAddress).toURL();

        HttpURLConnection con = createTrustingConnection(url);
	
        String message = obj.toString();
        msgBytes = message.getBytes("UTF-8");
        con.setRequestProperty("Content-Type", "application/json");

		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Length", String.valueOf(msgBytes.length));

        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
		String authHeaderValue = "Basic " + new String(encodedAuth);
		con.setRequestProperty("Authorization", authHeaderValue);
        System.out.println("sending coords");
		OutputStream writer = con.getOutputStream();
		writer.write(msgBytes);
		writer.close();


		responseCode = con.getResponseCode();
        System.out.println("got response code: "+responseCode);

        return responseCode;
    }

    public synchronized int testJSONHTTPSMessage(JSONObject obj, String authNew) throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        int responseCode = 400;
        byte[] msgBytes;

        fullAddress = client.getServerAddress();
        fullAddress += client.getMessageContext();

        URL url = new URI(fullAddress).toURL();

        HttpURLConnection con = createTrustingConnection(url);
	
        String message = obj.toString();
        msgBytes = message.getBytes("UTF-8");
        con.setRequestProperty("Content-Type", "application/json");

		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Length", String.valueOf(msgBytes.length));

        byte[] encodedAuth = Base64.getEncoder().encode(authNew.getBytes(StandardCharsets.UTF_8));
		String authHeaderValue = "Basic " + new String(encodedAuth);
		con.setRequestProperty("Authorization", authHeaderValue);
        System.out.println("sending coords");
		OutputStream writer = con.getOutputStream();
		writer.write(msgBytes);
		writer.close();


		responseCode = con.getResponseCode();
        System.out.println("got response code: "+responseCode);

        return responseCode;
    }

    public synchronized int testJSONHTTPSMessage(JSONObject obj, JSONObject auth) throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        int responseCode = 400;
        byte[] msgBytes;

        fullAddress = client.getServerAddress();
        fullAddress += client.getMessageContext();

        URL url = new URI(fullAddress).toURL();

        HttpURLConnection con = createTrustingConnection(url);
	
        String message = obj.toString();
        msgBytes = message.getBytes("UTF-8");
        con.setRequestProperty("Content-Type", "application/json");

		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Length", String.valueOf(msgBytes.length));

        String authString = auth.getString("username") + ":" + auth.getString("password");

        byte[] encodedAuth = Base64.getEncoder().encode(authString.getBytes(StandardCharsets.UTF_8));
		String authHeaderValue = "Basic " + new String(encodedAuth);
		con.setRequestProperty("Authorization", authHeaderValue);
        System.out.println("sending");
		OutputStream writer = con.getOutputStream();
		writer.write(msgBytes);
		writer.close();


		responseCode = con.getResponseCode();
        
        System.out.println("got response code: "+responseCode);

        return responseCode;
    }

    public synchronized int createSightseeingPath(JSONObject obj, JSONObject auth) throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        int responseCode = 400;
        byte[] msgBytes;

        fullAddress = client.getServerAddress();
        fullAddress += client.getPathsContext();

        URL url = new URI(fullAddress).toURL();

        HttpURLConnection con = createTrustingConnection(url);
	
        String message = obj.toString();
        msgBytes = message.getBytes("UTF-8");
        con.setRequestProperty("Content-Type", "application/json");

		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Length", String.valueOf(msgBytes.length));

        String authString = auth.getString("username") + ":" + auth.getString("password");

        byte[] encodedAuth = Base64.getEncoder().encode(authString.getBytes(StandardCharsets.UTF_8));
		String authHeaderValue = "Basic " + new String(encodedAuth);
		con.setRequestProperty("Authorization", authHeaderValue);
        System.out.println("sending coords");
		OutputStream writer = con.getOutputStream();
		writer.write(msgBytes);
		writer.close();


		responseCode = con.getResponseCode();
        System.out.println("got response code: "+responseCode);

        return responseCode;
    }

    public synchronized int testJSONHTTPSMessage(String obj, JSONObject auth) throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        int responseCode = 400;
        byte[] msgBytes;

        fullAddress = client.getServerAddress();
        fullAddress += client.getMessageContext();

        URL url = new URI(fullAddress).toURL();

        HttpURLConnection con = createTrustingConnection(url);
	
        String message = obj.toString();
        msgBytes = message.getBytes("UTF-8");
        con.setRequestProperty("Content-Type", "application/json");

		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Length", String.valueOf(msgBytes.length));

        String authString = auth.getString("username") + ":" + auth.getString("password");

        byte[] encodedAuth = Base64.getEncoder().encode(authString.getBytes(StandardCharsets.UTF_8));
		String authHeaderValue = "Basic " + new String(encodedAuth);
		con.setRequestProperty("Authorization", authHeaderValue);
        System.out.println("sending coords");
		OutputStream writer = con.getOutputStream();
		writer.write(msgBytes);
		writer.close();


		responseCode = con.getResponseCode();
        System.out.println("got response code: "+responseCode);

        return responseCode;
    }

    public synchronized int testJSONHTTPSMessage(String obj) throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        int responseCode = 400;
        byte[] msgBytes;

        fullAddress = client.getServerAddress();
        fullAddress += client.getMessageContext();

        URL url = new URI(fullAddress).toURL();

        HttpURLConnection con = createTrustingConnection(url);
	
        msgBytes = obj.getBytes("UTF-8");
        con.setRequestProperty("Content-Type", "application/json");

		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Length", String.valueOf(msgBytes.length));

        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
		String authHeaderValue = "Basic " + new String(encodedAuth);
		con.setRequestProperty("Authorization", authHeaderValue);
        System.out.println("sending coords");
		OutputStream writer = con.getOutputStream();
		writer.write(msgBytes);
		writer.close();


		responseCode = con.getResponseCode();
        System.out.println("got response code: "+responseCode);

        return responseCode;
    }

    private HttpURLConnection createTrustingConnection(URL url) throws KeyStoreException, CertificateException,
    NoSuchAlgorithmException, FileNotFoundException, KeyManagementException, IOException {

    Certificate certificate = CertificateFactory.getInstance("X.509").generateCertificate(new FileInputStream(client.getKeystore()));
    KeyStore keyStore = KeyStore.getInstance("JKS");
    keyStore.load(null, null);
    keyStore.setCertificateEntry("localhost", certificate);

    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
    trustManagerFactory.init(keyStore);

    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    connection.setSSLSocketFactory(sslContext.getSocketFactory());
    // All requests use these common timeouts.
    connection.setConnectTimeout(CONNECT_TIMEOUT);
    connection.setReadTimeout(REQUEST_TIMEOUT);
    return connection;

}

//general register user function
public int registerUser(String newUsername, String newPassword, String newEmail, messageSettings msgStngs) throws KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, FileNotFoundException, IOException, URISyntaxException {
    int responseCode = 400;
    byte[] msgBytes;

    fullAddress = client.getServerAddress();
    fullAddress += client.getRegisterContext();

    URL url = new URI(fullAddress).toURL();

    HttpURLConnection con = createTrustingConnection(url);

    String userPayload = "";

    if(msgStngs.getJsonType() && !msgStngs.getRequestType().equalsIgnoreCase("GET")){


    JSONObject obj = new JSONObject();
        obj.put("username", newUsername);
        obj.put("password", newPassword);
        obj.put("email", newEmail);
        userPayload = obj.toString();
        con.setRequestProperty("Content-Type", "application/json");
    }

    msgBytes = userPayload.getBytes("UTF-8");
    con.setRequestMethod(msgStngs.getRequestType());
    con.setDoOutput(true);
    //con.setDoInput(true);
    OutputStream writer = con.getOutputStream();
    if(msgStngs.getRequestType().equalsIgnoreCase("POST")){
        con.setRequestProperty("Content-Length", String.valueOf(msgBytes.length));
        writer.write(msgBytes);
    }
    writer.close();
    byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
    String authHeaderValue = "Basic " + new String(encodedAuth);
    con.setRequestProperty("Authorization", authHeaderValue);


    responseCode = con.getResponseCode();
    System.out.println(responseCode);

    return responseCode;
}

public synchronized int testMessage(JSONObject obj, messageSettings msgStngs) throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
    int responseCode = 400;
    byte[] msgBytes;

    fullAddress = client.getServerAddress();
    fullAddress += client.getMessageContext();

    URL url = new URI(fullAddress).toURL();

    HttpURLConnection con = createTrustingConnection(url);

    String coordinates = obj.toString();
    msgBytes = coordinates.getBytes("UTF-8");
    con.setRequestProperty("Content-Type", "application/json");

    con.setRequestMethod(msgStngs.getRequestType());
    con.setDoOutput(true);
    con.setDoInput(true);
    con.setRequestProperty("Content-Length", String.valueOf(msgBytes.length));

    byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
    String authHeaderValue = "Basic " + new String(encodedAuth);
    con.setRequestProperty("Authorization", authHeaderValue);
    System.out.println("sending coords");
    OutputStream writer = con.getOutputStream();
    writer.write(msgBytes);
    writer.close();


    responseCode = con.getResponseCode();
    System.out.println("got response code: "+responseCode);

    return responseCode;
}

public synchronized String getMessages(messageSettings msgStngs) throws IOException, KeyManagementException, KeyStoreException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
    int responseCode = 400;

    fullAddress = client.getServerAddress();
    fullAddress += client.getMessageContext();

    URL url = new URI(fullAddress).toURL();

    HttpURLConnection con = createTrustingConnection(url);
    con.setRequestMethod("GET");
    con.setRequestProperty("Content-Type", "application/json");

    byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
    String authHeaderValue = "Basic " + new String(encodedAuth);
    con.setRequestProperty("Authorization", authHeaderValue);

    System.out.println("done setting GET");
    responseCode = con.getResponseCode();
    System.out.println(responseCode);

    StringBuilder coordinates = new StringBuilder();
    String input;
    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));

    while ((input = in.readLine()) != null) {
        coordinates.append(input);
    }

    String result = coordinates.toString();
    System.out.println(result);

    return result;

}

    /**
     * Main
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {


        // client.setupClient(args[0], args[1]);



    }

}
