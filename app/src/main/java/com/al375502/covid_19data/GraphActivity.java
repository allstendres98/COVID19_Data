package com.al375502.covid_19data;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class GraphActivity extends AppCompatActivity {
    public static final String COUNTRY = "Country";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Intent intent = getIntent();
        String country = intent.getStringExtra(COUNTRY);
    }
}
