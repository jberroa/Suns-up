package com.backpack.development.sunsup;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    static String cityName = "Orloando,us";
    static String appId = "17c6df35bd04026e529e265af29d455c";
    static String units = "metric";
    public ArrayList<String> myDataset = new ArrayList<>();
    public static final String API_URL = "http://api.openweathermap.org";
    private DividerItemDecoration mDividerItemDecoration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initviews();

        mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(mDividerItemDecoration);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    private void initviews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        LoadData();

    }

    private void LoadData() {

        final GregorianCalendar gc = new GregorianCalendar();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherApi api = retrofit.create(WeatherApi.class);

        Call<Forecast> call = api.getWeatherFromApi(cityName, units, appId);

        call.enqueue(new Callback<Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                Forecast forecast = response.body();

                // making sure that response is retrieving the right information

                System.out.println(call.request().url());
                System.out.println(response.raw());
                System.out.println(response.body().toString());
                System.out.println(new Gson().toJson(response.body()));
                String date = null;
                String day;
                int numberday;
                String month;


                // parsing the date text to ignore the 3 hour forecast intervals and only get
                // the current weather for each day

                for (WeatherList list : forecast.getList()) {
                    day = gc.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
                    month = gc.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
                    numberday = gc.get(Calendar.DAY_OF_MONTH);

                    System.out.println("date = " + numberday);

                    if (date == null) {
                        date = getdate(list.getDtTxt());
                        produceForecast(day, list, gc, month, numberday);

                    } else if (!date.contains(getdate(list.getDtTxt()))) {
                        date = getdate(list.getDtTxt());
                        produceForecast(day, list, gc, month, numberday);
                    }


                }
                mAdapter = new MyAdapter(myDataset);
                mRecyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {
                System.out.println("nothing homie uufff");


            }


        });

    }

    private void produceForecast(String day, WeatherList list, GregorianCalendar gc, String month, int date) {
        String description;
        int high;
        int low;
        String highAndLow;
        description = list.getWeather().get(0).getDescription();
        high = Math.round((list.getMain().getTempMax().intValue()));
        low = Math.round(list.getMain().getTempMin().intValue());
        highAndLow = high + "/" + low;


        myDataset.add(day + ", " + month + " " + date + " " + " - " + description + " - " + highAndLow);
        gc.add(Calendar.DAY_OF_WEEK, 1);
    }

    public String getdate(String date) {

        String realdate[] = date.split(" ", 2);

        return realdate[0];
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
