package com.al375502.covid_19data;

import android.content.Context;
import android.net.sip.SipSession;
import android.os.AsyncTask;

import androidx.room.Room;

import com.al375502.covid_19data.database.Country;
import com.al375502.covid_19data.database.DAO;
import com.al375502.covid_19data.database.Database;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Model {
    public static final String URL_COUNTRY = "https://raw.githubusercontent.com/samayo/country-json/master/src/country-by-continent.json";
    public static final String URL_COVID_DATA = "https://raw.githubusercontent.com/pomber/covid19/master/docs/timeseries.json";
    private static Model model;
    private DAO dao;
    private RequestQueue requestQueue;

    private Model(Context context)
    {
        Database database = Room.databaseBuilder(context, Database.class, "DataBase").build();
        dao = database.getDao();
        requestQueue = Volley.newRequestQueue(context);
    }

    public static Model getInstance(Context context)
    {
        if(model == null)
        {
            model = new Model(context);
        }
        return model;
    }

    public void getCountries(final Response.Listener<ArrayList<Country>> countryResponse, final String continent)
    {
        new AsyncTask<Void, Void, ArrayList<Country>>(){

            @Override
            protected ArrayList<Country> doInBackground(Void... voids) {
                return new ArrayList<>(dao.allCountriesInAContinent(continent));
            }

            @Override
            protected void onPostExecute(ArrayList<Country> countries) {
                countryResponse.onResponse(countries);
            }
        }.execute();
    }

    public ArrayList<String> getContinents(/*final Response.Listener<ArrayList<String>> continentResponse*/)
    {
        return new ArrayList<>(dao.allContinents());
        /*new AsyncTask<Void, Void, ArrayList<String>>(){

            @Override
            protected ArrayList<String> doInBackground(Void... voids) {
                return new ArrayList<>(dao.allContinents());
            }

            @Override
            protected void onPostExecute(ArrayList<String> continents) {
                continentResponse.onResponse(continents);
            }
        }.execute();*/
    }

    public void updateCountries(final Response.Listener<ArrayList<Country>> listener, final Response.ErrorListener errorListener)
    {
        JsonArrayRequest ArrayRequest = new JsonArrayRequest(Request.Method.GET, URL_COUNTRY, null, new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray response){
                FillDatabaseWithCountries(response, listener);
            }
        }, errorListener){};
        requestQueue.add(ArrayRequest);
    }

    private void FillDatabaseWithCountries(JSONArray response, Response.Listener<ArrayList<Country>> listener) {
        ArrayList<Country> countries = new ArrayList<>();
        try{
            for(int i = 0; i < response.length(); i++)
            {
                JSONObject extractedCountry = response.getJSONObject(i);
                String country, continent;
                country = extractedCountry.getString("country");
                continent = extractedCountry.getString("continent");
                countries.add(new Country(country, continent));
            }
            insertCountriesInDao(countries, listener);
        }catch (JSONException e)
        {

        }
    }

    private void insertCountriesInDao(final ArrayList<Country> countries, final Response.Listener<ArrayList<Country>> listener) {
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                dao.insertCountry(countries);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                listener.onResponse(countries);
            }
        }.execute();
    }
}
