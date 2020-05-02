package com.al375502.covid_19data;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.al375502.covid_19data.database.Country;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Spinner spinner;
    Button buttonData;
    Presenter presenter;
    ListView listView;
    //CustomAdapter
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new Presenter(this, Model.getInstance(getApplicationContext()));
        spinner = findViewById(R.id.spinner);
        buttonData = findViewById(R.id.buttonData);
        listView = findViewById(R.id.listView);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String Continent = parent.getSelectedItem().toString();
                presenter.GetCountries(Continent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void FillListView(ArrayList<Country> countries) {

    }

    public void FillSpinner(ArrayList<String> continents) {
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                continents);
        spinner.setAdapter(spinnerArrayAdapter);
    }
}
