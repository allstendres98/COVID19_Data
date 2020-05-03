package com.al375502.covid_19data;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.FontRequest;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.al375502.covid_19data.database.Country;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Spinner spinner;
    Button buttonData, buttonInfo;
    Presenter presenter;
    ListView listView;
    MyAdapter myAdapter;
    TextView countrySelected;
    ArrayList<Country> countriesInThatContinent;
    ArrayList<String> continentsSpinner;
    int selectionCurrent;
    MainActivity thisContext;

    //CustomAdapter
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new Presenter(this, Model.getInstance(getApplicationContext()));
        buttonData = findViewById(R.id.buttonData);
        buttonData.setEnabled(false);
        buttonInfo = findViewById(R.id.info);
        buttonInfo.setAlpha(0);
        buttonInfo.setEnabled(false);
        spinner = findViewById(R.id.spinner);
        listView = findViewById(R.id.listView);
        countrySelected = findViewById(R.id.selectedCountry);
        spinner = findViewById(R.id.spinner);
        selectionCurrent = spinner.getSelectedItemPosition();
        thisContext = this;


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (selectionCurrent != position){
                    Log.d("Continent", parent.getSelectedItem().toString());
                    presenter.GetCountries(parent.getItemAtPosition(position).toString());
                    countrySelected.setText("");
                    buttonData.setEnabled(false);
                    buttonInfo.setAlpha(0f);
                    buttonInfo.setEnabled(false);
                }
                selectionCurrent = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                countrySelected.setText(countriesInThatContinent.get(position).name);
                buttonData.setEnabled(true);
                buttonInfo.setAlpha(1f);
                buttonInfo.setEnabled(true);
            }
        });

        buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < countriesInThatContinent.size(); i++)
                {
                    if(countrySelected.getText().equals(countriesInThatContinent.get(i).name))
                    {
                        InfoDialog infoDialog = new InfoDialog (countriesInThatContinent.get(i), thisContext);
                        infoDialog.show(getSupportFragmentManager(),"info dialog");
                    }
                }
            }
        });
    }

    public void FillListView(ArrayList<Country> countries) {
        countriesInThatContinent = countries;
        myAdapter = new MyAdapter(this, countries);
        listView.setAdapter(myAdapter);
    }

    public void FillSpinner(ArrayList<String> continents) {
        continentsSpinner = continents;
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                continents);
        spinner.setAdapter(spinnerArrayAdapter);

    }
}
