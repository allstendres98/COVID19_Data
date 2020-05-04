package com.al375502.covid_19data;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.al375502.covid_19data.database.CovidDayData;
import com.github.mikephil.charting.charts.BarChart;

import java.util.ArrayList;

public class GraphActivity extends AppCompatActivity {
    public static final String COUNTRY = "Country";
    BarChart barChart;
    Spinner mspinner, yspinner;
    ArrayList<CovidDayData> covidDayData;
    CheckBox deaths, confirmed, recovered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        barChart = findViewById(R.id.bargraph);
        yspinner = findViewById(R.id.yearspinner);
        mspinner = findViewById(R.id.monthspinner);


        Intent intent = getIntent();
        String country = intent.getStringExtra(COUNTRY);
        final GraphPresenter presenter = new GraphPresenter(this, Model.getInstance(getApplicationContext()));
        presenter.GetCountryCovidData(country);


    }

    public void FillGraph(ArrayList<CovidDayData> response) {
        FillMonthYear(response);
        DrawGraph();
    }

    private void DrawGraph() {
    }

    public void FillMonthYear(ArrayList<CovidDayData> response)
    {
        covidDayData = response;
        ArrayList<String> Months = new ArrayList<>();
        ArrayList<String> Years = new ArrayList<>();

        for(int i = 0; i < response.size(); i++)
        {
            String year = "", month = "";
            boolean yearDone = false;
            boolean monthDone = false;
            for (char c : response.get(i).date.toCharArray()) {
                if(Character.toString(c).equals("-")){
                    if(yearDone) break;
                    yearDone = true;
                }
                else if(!yearDone) year += c;
                else if(!monthDone && yearDone) month += c;
            }
            if(!Years.contains(year)) Years.add(year);
            switch (month){
                case "1": month = "January";
                break;
                case "2": month = "February";
                    break;
                case "3": month = "March";
                    break;
                case "4": month = "April";
                    break;
                case "5": month = "May";
                    break;
                case "6": month = "June";
                    break;
                case "7": month = "July";
                    break;
                case "8": month = "August";
                    break;
                case "9": month = "September";
                    break;
                case "10": month = "October";
                    break;
                case "11": month = "November";
                    break;
                case "12": month = "December";
                    break;
                default: break;
            }
            if(!Months.contains(month)) Months.add(month);
        }
        ArrayAdapter madapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                Months);
        ArrayAdapter yadapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                Years);
        mspinner.setAdapter(madapter);
        yspinner.setAdapter(yadapter);

    }
}
