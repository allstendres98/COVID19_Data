package com.al375502.covid_19data;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;

public class GraphActivity extends AppCompatActivity {
    public static final String COUNTRY = "Country";
    BarChart barChart;
    Spinner spinner;
    CheckBox deaths, confirmed, recovered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        barChart = findViewById(R.id.bargraph);
        spinner = findViewById(R.id.yearspinner);


        Intent intent = getIntent();
        String country = intent.getStringExtra(COUNTRY);

    }
}
