package com.backpack.development.sunsup;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static android.R.id.list;

/**
 * Created by development on 1/29/17.
 */

public interface WeatherApi {

    @GET("/data/2.5/forecast")
    Call<Forecast> getWeatherFromApi(
            @Query("q") String cityName,
            @Query("units") String units,
            @Query("appid") String appId);
}
