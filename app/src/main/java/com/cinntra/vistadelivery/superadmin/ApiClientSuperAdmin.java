package com.cinntra.vistadelivery.superadmin;


import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.util.Log;

import com.cinntra.vistadelivery.activities.Login;
import com.cinntra.vistadelivery.globals.Globals;
import com.cinntra.vistadelivery.globals.MyApp;
import com.cinntra.vistadelivery.network.NetworkConnectionInterceptor;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClientSuperAdmin {
    static ApiClientSuperAdmin ourInstance = new ApiClientSuperAdmin();
    private final boolean SHOW_LOGS = false;
    private final int TIME_OUT = 1500000;
    private SuperAdminServices apiServices = null;
    private Retrofit.Builder mRetrofitBuilder = null;
    private OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private OkHttpClient.Builder httpClientForCache = new OkHttpClient.Builder();
    private Dispatcher mDispatcher;


    public ApiClientSuperAdmin() {
    }

    public static ApiClientSuperAdmin getInstance() {
        return ourInstance;
    }

    public Retrofit.Builder getBuilder() {
        if (mRetrofitBuilder == null) {

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(new NetworkConnectionInterceptor(MyApp.mInstance))
                    .addInterceptor(provideOfflineCacheInterceptor())
                    .addNetworkInterceptor(provideCacheInterceptor())
                    .cache(provideCache()).build();


            mRetrofitBuilder = new Retrofit.Builder()
                    .baseUrl(Globals.NewBaseUrlSuperAdmin).client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create());


        }
        return mRetrofitBuilder;
    }


    public SuperAdminServices getApiService(Context context) {
        if (apiServices == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            CookieHandler cookieHandler = new CookieManager();
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            httpClient.cookieJar(new JavaNetCookieJar(cookieManager));
          /*  File httpCacheDirectory = new File(MyApp.getInstance().getApplicationContext().getCacheDir(), "offlineCache");
             //10 MB
            Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
*/

            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {

                   /* Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder()
                            .addHeader("Authorization", sessionManagement.getToken() != null ? sessionManagement.getToken() : "")
                            .addHeader("Content-Type", "application/json;charset=UTF-8");

                    Request request = requestBuilder.build();

                    Response response = chain.proceed(request);
                    if (response.code() == 401) {
                        sessionManagement.ClearSession();
                        logoutScreen(context);
                        return response;
                    }
                    return response;*/

                    Request originalRequest = chain.request();
                    Request.Builder builder = new Request.Builder();

                    if (Prefs.getString(Globals.Token, "").equalsIgnoreCase("")) {
                        Log.e(TAG, "intercept: " + "tokenApi" );
                        builder = originalRequest.newBuilder()
                                .header("content-type", "application/json").cacheControl(CacheControl.FORCE_NETWORK);
                    }
                    else if (!Prefs.getString(Globals.Token, "").equalsIgnoreCase("")){
                        Log.e(TAG, "intercept3: " + "Else" );
                        builder = originalRequest.newBuilder()
                                .addHeader("Authorization",  Prefs.getString(Globals.Token, "") != "" ?  Prefs.getString(Globals.Token, "") : "")
                                .header("content-type", "application/json").cacheControl(CacheControl.FORCE_NETWORK);
                    }
                    Request request = builder.build();

                    Response response = chain.proceed(request);
                    Log.e("URL=>", request.toString());
                    if (response.code() == 301 || response.code() == 401) {
                        Prefs.clear();
                        Prefs.putString(Globals.Token, "");
                        logoutScreen(context);
                        return response;
                    }

                    Request newRequest = builder.build();
                    return response; //chain.proceed(newRequest)

                }
            });

            httpClient.addInterceptor(interceptor);
            httpClient.readTimeout(60, TimeUnit.SECONDS);
            httpClient.connectTimeout(60, TimeUnit.SECONDS);
            httpClient.writeTimeout(60, TimeUnit.SECONDS);
            if (SHOW_LOGS)
                httpClient.addInterceptor(new LoggingInterceptor());

            OkHttpClient client = httpClient.build();
            Retrofit retrofit = getBuilder().client(client).build();


            apiServices = retrofit.create(SuperAdminServices.class);
        }
        return apiServices;
    }

    /* else if (Globals.APILog.equalsIgnoreCase("APILog") && !Prefs.getString(Globals.Token, "").equalsIgnoreCase("")) {
                        Log.e(TAG, "intercept2: " + "APiLog" );
                        builder = originalRequest.newBuilder()
                                .addHeader("Authorization", Prefs.getString(Globals.Token, ""))
                                .header("content-type", "application/json").cacheControl(CacheControl.FORCE_NETWORK);
                        Globals.APILog = "Not";
                    }*/

    private void logoutScreen(Context context) {
        Intent mainIntent = new Intent(context, Login.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(mainIntent);
        ((Activity) context).finish();
    }

    public SuperAdminServices getApiServiceSimple() {
        if (apiServices == null) {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            httpClient.addInterceptor(interceptor);
            httpClient.readTimeout(TIME_OUT, TimeUnit.SECONDS);
            httpClient.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
            if (SHOW_LOGS)
                httpClient.addInterceptor(new LoggingInterceptor());

            OkHttpClient client = httpClient.build();

            mDispatcher = client.dispatcher();

            Retrofit retrofit = getBuilder().client(client).build();

            apiServices = retrofit.create(SuperAdminServices.class);
        }
        return apiServices;
    }

    public SuperAdminServices getApiServiceWithCacheAbility() {
        httpClientForCache.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request originalRequest = chain.request();

                Request.Builder builder = originalRequest.newBuilder()
                        .header("User-Agent", "android")
                        .header("Cache-Control", String.format("max-age=%d", 50000));

                Request newRequest = builder.build();

                return chain.proceed(newRequest);
            }
        });

        OkHttpClient client = httpClientForCache.build();
        Retrofit retrofit = getBuilder().client(client).build();
        return retrofit.create(SuperAdminServices.class);
    }

    public Dispatcher getAPIsDispatcher() {
        return mDispatcher;
    }

    class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {


            Request request = chain.request();

            long t1 = System.nanoTime();

            Log.d("AVIS_NW", String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            Log.d("AVIS_NW", String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));


            final String responseString = new String(response.body().bytes());

            Log.d("AVIS_NW", "Response: " + responseString);

            return response.newBuilder()
                    .body(ResponseBody.create(response.body().contentType(), responseString))
                    .build();
        }
    }

    /************* Offline Work Manager ****************/
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_PRAGMA = "Cinntra";

    private Context mContext;

    private Cache provideCache() {
        Cache cache = null;

        try {
            cache = new Cache(new File(mContext.getCacheDir(), "http-cache"),
                    10 * 1024 * 1024); // 10 MB
        } catch (Exception e) {
            Log.e(TAG, "Could not create Cache!");
        }

        return cache;
    }

    private Interceptor provideCacheInterceptor() {
        return chain -> {
            Response response = chain.proceed(chain.request());

            CacheControl cacheControl;

            if (isConnected()) {
                cacheControl = new CacheControl.Builder()
                        .maxAge(0, TimeUnit.SECONDS)
                        .build();
            } else {
                cacheControl = new CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build();
            }

            return response.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                    .build();

        };
    }

    private Interceptor provideOfflineCacheInterceptor() {
        return chain -> {
            Request request = chain.request();

            if (!isConnected()) {
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build();

                request = request.newBuilder()
                        .removeHeader(HEADER_PRAGMA)
                        .removeHeader(HEADER_CACHE_CONTROL)
                        .cacheControl(cacheControl)
                        .build();
            }

            return chain.proceed(request);
        };
    }

    public boolean isConnected() {
        try {
            android.net.ConnectivityManager e = (android.net.ConnectivityManager) mContext.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = e.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }

        return false;
    }


}