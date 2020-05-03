package com.al375502.covid_19data;

import android.widget.Toast;

import com.al375502.covid_19data.database.Country;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

public class Presenter {
    MainActivity view;
    Model model;
    int cont = 0;

    public Presenter(MainActivity view, Model model) {
        this.view = view;
        this.model = model;
        GetCountries("");
    }

    public void GetCountries(String continent){
        model.getCountries(new Response.Listener<ArrayList<Country>>(){
            @Override
            public void onResponse(ArrayList<Country> response) {
                onCountryAviable(response);
            }
        }, continent);
    }

    public void GetContinent(){
        model.getContinents(new Response.Listener<ArrayList<String>>() {
            @Override
            public void onResponse(ArrayList<String> response) {
                view.FillSpinner(response);
            }
        });
    }


    private  void onCountryAviable(final ArrayList<Country> countries){
        if(countries.size() == 0){
            model.updateCountries(new Response.Listener<ArrayList<Country>>() {
                @Override
                public void onResponse(ArrayList<Country> countries) {
                    if(cont < 1) {
                        GetContinent();
                        cont++;
                    }
                    view.FillListView(countries);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    processError(error.getMessage());
                }
            });
        }
        else{
            if(cont < 1) {
                GetContinent();
                cont++;
            }
            view.FillListView(countries);
        }
    }

    public void processError(String e){
        Toast toast = Toast.makeText(view, e, Toast.LENGTH_LONG);
        toast.show();
    }

}
