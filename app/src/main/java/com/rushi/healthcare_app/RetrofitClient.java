package com.rushi.healthcare_app;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // CHANGE YOUR IP ADDRESS ONLY HERE
    public static final String IP_ADDRESS = "192.168.1.15";

    public static final String BASE_URL = "http://" + IP_ADDRESS + "/healthcare-backend/api/";
    public static final String IMAGE_BASE_URL = "http://" + IP_ADDRESS + "/healthcare-backend/";

    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}