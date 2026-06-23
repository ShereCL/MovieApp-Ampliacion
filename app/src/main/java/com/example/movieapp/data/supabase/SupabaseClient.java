package com.example.movieapp.data.supabase;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SupabaseClient {

    private static final String BASE_URL = "https://namzgkjglxpiwveqfufj.supabase.co";
    static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5hbXpna2pnbHhwaXd2ZXFmdWZqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzk2OTc3NjAsImV4cCI6MjA5NTI3Mzc2MH0.4KBU_O_8V-D48cpXrkPYe30-1e1o48OfDUuEivy0rAs";
    public static final String BUCKET = "recuerdos";

    public static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if(retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new SupabaseAuthInterceptor(API_KEY))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
