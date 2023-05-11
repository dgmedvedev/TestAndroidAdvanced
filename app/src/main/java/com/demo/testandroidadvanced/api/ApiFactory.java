package com.demo.testandroidadvanced.api;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFactory {
    private static ApiFactory apiFactory;
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://gitlab.65apps.com/65gb/static/raw/master/";

    private ApiFactory() {
        retrofit = new Retrofit.Builder()
                // добавляем преобразователь Gson, который преобразует json в объекты
                .addConverterFactory(GsonConverterFactory.create())
                // добавляем CallAdapter, чтобы следить, преобразование данных прошло успешно или нет
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                // добавляем базовый url, который обязательно должен заканчиваться "/"
                .baseUrl(BASE_URL)
                .build();
    }

    public static ApiFactory getInstance() {
        if (apiFactory == null) {
            apiFactory = new ApiFactory();
        }
        return apiFactory;
    }

    public ApiService getApiService(){
        return retrofit.create(ApiService.class);
    }
}