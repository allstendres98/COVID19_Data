package com.al375502.covid_19data;

import android.widget.Toast;

import com.al375502.covid_19data.database.CovidDayData;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;

public class GraphPresenter {
    GraphActivity view;
    Model model;

    public GraphPresenter(GraphActivity view, Model model) {
        this.view = view;
        this.model = model;
    }

    public void GetCountryCovidData(final String actualCountry){
        model.updateCovidDayData(actualCountry, new Response.Listener<ArrayList<CovidDayData>>() {
            @Override
            public void onResponse(ArrayList<CovidDayData> response) {
                view.FillGraph(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                processError(error.getMessage());

            }
        });
    }

    public void processError(String e){
        Toast toast = Toast.makeText(view, e, Toast.LENGTH_LONG);
        toast.show();
    }
}
