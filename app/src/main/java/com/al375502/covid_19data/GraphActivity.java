package com.al375502.covid_19data;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.al375502.covid_19data.database.CovidDayData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class GraphActivity extends AppCompatActivity {
    public static final String COUNTRY = "Country";
    BarChart barChart;
    Spinner mspinner, yspinner;
    ArrayList<CovidDayData> covidDayData;
    int selectionCurrent, mselectionCurrent;
    CheckBox deathsCB, confirmedCB, recoveredCB;
    TextView countryName;
    ProgressBar progressBar;
    Button total;
    Boolean draw_total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Intent intent = getIntent();
        String country = intent.getStringExtra(COUNTRY);
        final GraphPresenter presenter = new GraphPresenter(this, Model.getInstance(getApplicationContext()));
        presenter.GetCountryCovidData(country);

        countryName = findViewById(R.id.countryName);
        countryName.setText(country+" Evolution");
        barChart = findViewById(R.id.bargraph);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        //barChart.setMaxVisibleValueCount(500);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(true);

        yspinner = findViewById(R.id.cspinner);
        mspinner = findViewById(R.id.monthspinner);
        deathsCB = findViewById(R.id.deaths);
        confirmedCB = findViewById(R.id.confirmed);
        recoveredCB = findViewById(R.id.recovered);
        progressBar = findViewById(R.id.progressBar);
        total = findViewById(R.id.Total_button);
        draw_total = false;

        total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draw_total = !draw_total;
                if(draw_total)total.setText("Show Per Day");
                else total.setText("Show Total");
                DrawGraph();
            }
        });

        deathsCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawGraph();
            }
        });

        confirmedCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawGraph();
            }
        });

        recoveredCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawGraph();
            }
        });

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
        response.add(new CovidDayData("2019-12-31",0,0,0)); //Data added just for you to see the implementation of the top method
        FillMonthYear(response);
        DrawGraph();
    }

    private void DrawGraph() {
        progressBar.setAlpha(1);
        String currentYear = yspinner.getSelectedItem().toString();
        String currentMonth = getNumberMonth(mspinner.getSelectedItem().toString());
        ArrayList<BarEntry> deaths = new ArrayList<>(), confirmed = new ArrayList<>(), recovereds = new ArrayList<>();
        BarDataSet deathsDS, confirmedDS, recoveredsDS;
        ArrayList<String> days_array = new ArrayList();

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
                days_array.add(day);
                deaths.add(new BarEntry(days_array.size(), draw_total? covidDayData.get(i).death : i != 0? covidDayData.get(i).death - covidDayData.get(i-1).death : covidDayData.get(i).death));
                confirmed.add(new BarEntry(days_array.size(), draw_total? covidDayData.get(i).confirmed : i != 0? covidDayData.get(i).confirmed - covidDayData.get(i-1).confirmed : covidDayData.get(i).confirmed));
                recovereds.add(new BarEntry(days_array.size(), draw_total? covidDayData.get(i).recovered : i != 0? covidDayData.get(i).recovered - covidDayData.get(i-1).recovered : covidDayData.get(i).recovered));
            }
        }
        String[] days = new String[days_array.size()];
        for(int i = 0; i < days.length; i++){
            days[i] = "Day " + days_array.get(i);
        }

        deathsDS = new BarDataSet(deaths, "Deaths");
        deathsDS.setColor(Color.RED);
        confirmedDS = new BarDataSet(confirmed, "Confirmed");
        confirmedDS.setColor(Color.BLUE);
        recoveredsDS = new BarDataSet(recovereds, "Recovered");
        recoveredsDS.setColor(Color.GREEN);

        BarData theData = new BarData();
        int cont = 0;
        if(confirmedCB.isChecked()){theData.addDataSet(confirmedDS); cont++;}
        if(recoveredCB.isChecked()){theData.addDataSet(recoveredsDS); cont++;}
        if(deathsCB.isChecked()){ theData.addDataSet(deathsDS); cont++;}

        barChart.animateX(1000);
        barChart.animateY(1000);

        barChart.setData(theData);

        XAxis xAxis = barChart.getXAxis();
        barChart.setVisibleXRangeMaximum(days.length);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);

        barChart.setDragEnabled(true);
        theData.setBarWidth(0.2f);

        float barSpace = 0.05f;
        float groupSpace = 1 - (0.2f + barSpace)*cont;

        if(cont > 1) {
            barChart.getXAxis().setAxisMinimum(0);
            barChart.getXAxis().setAxisMaximum(0+barChart.getBarData().getGroupWidth(groupSpace, barSpace) * days.length);
            barChart.getAxisLeft().setAxisMinimum(0);
            barChart.groupBars(0, groupSpace, barSpace);
        }
        else{
            //Cuando solo selecionas una sola no sale centrada, pero con multiples sale de lujo :D
            //PD: Hay que hacer algo
        }

        barChart.invalidate();

        progressBar.setAlpha(0);
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
