package com.example.appmusictest.service;

public class ApiService {
    private static String base_url = "https://hoanganhnth.000webhostapp.com/server/";
    public static DataService getService() {
        return APIRetrofitClient.getClient(base_url).create(DataService.class);
    }
}
