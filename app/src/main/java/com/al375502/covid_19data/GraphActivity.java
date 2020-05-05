package com.al375502.covid_19data;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.al375502.covid_19data.database.CovidDayData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {
    public static final String COUNTRY = "Country";
    BarChart barChart;
    Spinner mspinner, yspinner;
    ArrayList<CovidDayData> covidDayData;
    int selectionCurrent, mselectionCurrent;
    CheckBox deaths, confirmed, recovered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        barChart = findViewById(R.id.bargraph);
        yspinner = findViewById(R.id.cspinner);
        mspinner = findViewById(R.id.monthspinner);


        Intent intent = getIntent();
        String country = intent.getStringExtra(COUNTRY);
        final GraphPresenter presenter = new GraphPresenter(this, Model.getInstance(getApplicationContext()));
        presenter.GetCountryCovidData(country);

        selectionCurrent = yspinner.getSelectedItemPosition();
        mselectionCurrent = mspinner.getSelectedItemPosition();

        yspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (selectionCurrent != position){
                    modifySpinnerMonth(parent.getSelectedItem().toString());
                    DrawGraph();
                }
                selectionCurrent = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        mspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mselectionCurrent != position){
                    DrawGraph();
                }
                mselectionCurrent = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });
    }

    private void modifySpinnerMonth(String actualYear) {
        ArrayList<String> Months = new ArrayList<>();
        for(int i = 0; i < covidDayData.size(); i++) {
            String year = "", month = "";
            boolean yearDone = false;
            boolean monthDone = false;
            for (char c : covidDayData.get(i).date.toCharArray()) {
                if (Character.toString(c).equals("-")) {
                    if (yearDone) break;
                    yearDone = true;
                    if(!year.equals(actualYear)) break;
                } else if (!yearDone) year += c;
                else if (!monthDone && yearDone) month += c;
            }
            month = getNameMonth(month);
            if(!Months.contains(month) && !month.equals("null")) Months.add(month);
        }
        ArrayAdapter madapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                Months);
        mspinner.setAdapter(madapter);
    }

    public void FillGraph(ArrayList<CovidDayData> response) {
        FillMonthYear(response);
        DrawGraph();
    }

    private void DrawGraph() {
        String currentYear = yspinner.getSelectedItem().toString();
        String currentMonth = getNumberMonth(mspinner.getSelectedItem().toString());
        ArrayList<BarEntry> deaths = new ArrayList<>(), confirmed = new ArrayList<>(), recovereds = new ArrayList<>();
        BarDataSet deathsDS, confirmedDS, recoveredsDS;
        String[] days = new String[32];

        for(int i = 0; i < covidDayData.size(); i++) {
            String year = "", month = "", day = "";
            boolean yearDone = false;
            boolean monthDone = false;
            for (char c : covidDayData.get(i).date.toCharArray()) {
                if (Character.toString(c).equals("-")) {
                    if (yearDone) monthDone = true;
                    yearDone = true;
                    if(!year.equals(currentYear)) break;
                    if(monthDone && !month.equals(currentMonth)) break;
                } else if (!yearDone) year += c;
                else if (!monthDone && yearDone) month += c;
                else if(monthDone) day += c;
            }
            if(!day.equals("")){
                Log.d("Day", day);
                days[Integer.parseInt(day)-1] = day;
                deaths.add(new BarEntry(covidDayData.get(i).death, Integer.parseInt(day)));
                confirmed.add(new BarEntry(covidDayData.get(i).confirmed, Integer.parseInt(day)));
                recovereds.add(new BarEntry(covidDayData.get(i).recovered, Integer.parseInt(day)));
            }
        }

        deathsDS = new BarDataSet(deaths, "Deaths");
        confirmedDS = new BarDataSet(confirmed, "Confirmed");
        recoveredsDS = new BarDataSet(recovereds, "Recovered");

        ArrayList<BarDataSet> datasets = new ArrayList<>();
        datasets.add(deathsDS);
        datasets.add(confirmedDS);
        datasets.add(recoveredsDS);

        BarData theData = new BarData(confirmedDS, deathsDS, recoveredsDS);
        barChart.setData(theData);
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
            month = getNameMonth(month);
            if(!Months.contains(month) && !month.equals("null")) Months.add(month);
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

    private String getNameMonth(String month)
    {
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
            default: month = "null"; break;
        }
        return month;
    }

    private String getNumberMonth(String month)
    {
        switch (month){
            case "January": month = "1";
                break;
            case "February": month = "2";
                break;
            case "March": month = "3";
                break;
            case "April": month = "4";
                break;
            case "May": month = "5";
                break;
            case "June": month = "6";
                break;
            case "July": month = "7";
                break;
            case "August": month = "8";
                break;
            case "September": month = "9";
                break;
            case "October": month = "10";
                break;
            case "November": month = "11";
                break;
            case "December": month = "12";
                break;
            default: month = "null"; break;
        }
        return month;
    }
}
