package com.tests;

public class messageSettings {

    private boolean plainText = false;
    private boolean secureConnection = false;
    private boolean jsonType = false;
    private String requestType = "";

    public messageSettings (boolean plain, boolean secure, boolean json, String type){
        this.plainText = plain;
        this.secureConnection = secure;
        this.jsonType = json;
        this.requestType = type;
    }

    public boolean getPlainSetting (){
        return plainText;
    }

    public boolean getSecureConnection (){
        return secureConnection;
    }

    public boolean getJsonType (){
        return jsonType;
    }

    public String getRequestType(){
        return requestType;
    }
    
}
