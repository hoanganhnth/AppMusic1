package com.example.appmusictest.service;

public class ApiService {
//    private static String base_url = "https://hoanganhnth.000webhostapp.com/server/";
    private static final String base_url = "http://34.124.226.151:3000";
    public static DataService getService() {
        return APIRetrofitClient.getClient(base_url).create(DataService.class);
    }
}
