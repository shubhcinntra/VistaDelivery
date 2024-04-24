package com.cinntra.vistadelivery.webservices;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {
    private String headerName;
    private String headerValue;

    public HeaderInterceptor(String headerName, String headerValue) {
        this.headerName = headerName;
        this.headerValue = headerValue;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder requestBuilder = originalRequest.newBuilder()
                .header(headerName, headerValue);
        Request newRequest = requestBuilder.build();
        return chain.proceed(newRequest);
    }
}

