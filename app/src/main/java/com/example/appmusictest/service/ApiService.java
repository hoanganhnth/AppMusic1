package com.example.appmusictest.service;

public class ApiService {
//    private static String base_url = "https://hoanganhnth.000webhostapp.com/server/";
    private static final String base_url = "https://hoanganhnth2k3.000webhostapp.com/musicApp/server/";
    public static DataService getService() {
        return APIRetrofitClient.getClient(base_url).create(DataService.class);
    }
}
