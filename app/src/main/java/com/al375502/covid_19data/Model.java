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
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class Model {
    public static final String URL_COUNTRY = "https://raw.githubusercontent.com/samayo/country-json/master/src/country-by-continent.json";
    public static final String URL_COVID_DATA = "https://raw.githubusercontent.com/pomber/covid19/master/docs/timeseries.json";
    public static final String URL_FLAGS = "https://raw.githubusercontent.com/pomber/covid19/master/docs/countries.json";
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

    public void getCountries(final Listener<ArrayList<Country>> countryResponse, final String continent)
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

    public void getContinents(final Listener<ArrayList<String>> continentResponse)
    {
        new AsyncTask<Void, Void, ArrayList<String>>(){

            @Override
            protected ArrayList<String> doInBackground(Void... voids) {
                return new ArrayList<>(dao.allContinents());
            }

            @Override
            protected void onPostExecute(ArrayList<String> continents) {
                continentResponse.onResponse(continents);
            }
        }.execute();
    }

    public void updateCountries(final Listener<ArrayList<Country>> listener, final Response.ErrorListener errorListener)
    {
        JsonArrayRequest ArrayRequest = new JsonArrayRequest(Request.Method.GET, URL_COUNTRY, null, new Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray response){
                final JSONArray Countries = response;
                JsonObjectRequest ArrayFlagRequest = new JsonObjectRequest(Request.Method.GET, URL_FLAGS, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        FillDatabaseWithCountries(Countries, response, listener);
                    }
                }, errorListener){};
                requestQueue.add(ArrayFlagRequest);
            }
        }, errorListener){};
        requestQueue.add(ArrayRequest);
    }

    private void FillDatabaseWithCountries(JSONArray countries, JSONObject flags, Response.Listener<ArrayList<Country>> listener) {
        ArrayList<Country> country_list = new ArrayList<>();
        try{
            for(int i = 0; i < countries.length(); i++)
            {
                JSONObject extractedCountry = countries.getJSONObject(i);
                String country, continent, flag = "";
                country = extractedCountry.getString("country");
                continent = extractedCountry.getString("continent");
                if(!flags.isNull(country)) {
                    JSONObject country_flags = flags.getJSONObject(country);
                    flag = country_flags.getString("flag");
                    country_list.add(new Country(country, continent, flag));
                }
            }
            insertCountriesInDao(country_list, listener);
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
