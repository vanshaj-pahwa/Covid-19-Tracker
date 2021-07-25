package com.vanshaj.covid_19tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import readData.CountryData;
import readData.callApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView totalConfirm, totalActive, totalRecovered, totalDeath, totalSamples, todaySamples, todayConfirmed, todayRecovered, todayDeath, updatedTime, updatedDate;
    private PieChart pieChart;
    private List<CountryData> list;

    private CardView cardView;
    private ProgressDialog dialog;

    String country = "India";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.show();

        list = new ArrayList<>();
        if (getIntent().getStringExtra("country") != null){
            country = getIntent().getStringExtra("country");
        }

        init();

        TextView countryName = findViewById(R.id.countryName);
        countryName.setText(country);

        countryName.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CountryActivity.class)));

        TextView world = findViewById(R.id.world_text);

        world.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CountryActivity.class)));

        CardView cardView = findViewById(R.id.world_card);

        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),CountryActivity.class);
            startActivity(intent);
        });


        callApi.getApiInterface().getCountryData()
                .enqueue(new Callback<List<CountryData>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Call<List<CountryData>> call, Response<List<CountryData>> response) {
                        list.addAll(response.body());

                        for(int i = 0; i<list.size(); i++){
                            if(list.get(i).getCountry().equals(country)){
                                int confirm = Integer.parseInt(list.get(i).getCases());
                                int active = Integer.parseInt(list.get(i).getActive());
                                int recovered = Integer.parseInt(list.get(i).getRecovered());
                                int death = Integer.parseInt(list.get(i).getDeaths());

                                totalConfirm.setText(NumberFormat.getInstance().format(confirm));
                                totalActive.setText(NumberFormat.getInstance().format(active));
                                totalRecovered.setText(NumberFormat.getInstance().format(recovered));
                                totalDeath.setText(NumberFormat.getInstance().format(death));
                                totalSamples.setText(NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTests())));

                                todayConfirmed.setText("( +" + NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayCases())) + ")");
                                todayRecovered.setText("( +" + NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayRecovered())) + ")");
                                todayDeath.setText("( +" + NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayDeaths())) + ")");


                                setText(list.get(i).getUpdated());

                               // pieChart.addPieSlice(new PieModel("Confirm", confirm, getResources().getColor(R.color.yellow)));
                                pieChart.addPieSlice(new PieModel("Active", active, getResources().getColor(R.color.blue_pie)));
                                pieChart.addPieSlice(new PieModel("Recovered", recovered, getResources().getColor(R.color.green_pie)));
                                pieChart.addPieSlice(new PieModel("Deaths", death, getResources().getColor(R.color.red_pie)));

                                pieChart.startAnimation();
                            }
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<List<CountryData>> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setText(String updated) {
        DateFormat format = new SimpleDateFormat("MMM dd, yyyy");

        long milliseconds = Long.parseLong(updated);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        updatedTime.setText(format.format(calendar.getTime()));
    }

    private void init() {
        totalConfirm = findViewById(R.id.totalConfirmed);
        todayConfirmed = findViewById(R.id.todayConfirmed);

        totalActive = findViewById(R.id.totalActive);

        totalRecovered = findViewById(R.id.totalRecovered);
        todayRecovered = findViewById(R.id.todayRecovered);

        totalDeath = findViewById(R.id.totalDeath);
        todayDeath = findViewById(R.id.todayDeath);

        totalSamples = findViewById(R.id.totalSamples);
        todaySamples = findViewById(R.id.todaySamples);

        pieChart = findViewById(R.id.piechart);

        updatedDate = findViewById(R.id.updatedDate);
        updatedTime = findViewById(R.id.updatedTime);
    }

}