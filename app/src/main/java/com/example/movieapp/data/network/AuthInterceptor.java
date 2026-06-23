package com.example.movieapp.data.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    /// Clase necesaria al usar token para la API
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJhNjYyMTNjNzVhMjk2MTkyNWM5OTQ2NDczZTZhNWIwYiIsIm5iZiI6MTc2Nzg4OTUzMS4wNjMsInN1YiI6IjY5NWZkYTdiYTgyNjVkOWJkZTY2MWY4ZiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.XIXSyBVf_zlk83D1WmagZEsZQYrfq9SZZgMaquya82k";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer " + TOKEN)
                .addHeader("Accept", "application/json")
                .addHeader("Accept-Encoding", "identity")
                .build();

        return chain.proceed(request);
    }
}
