package com.al375502.covid_19data;

import android.content.Context;
import android.net.sip.SipSession;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.room.Room;

import com.al375502.covid_19data.database.Country;
import com.al375502.covid_19data.database.CovidDayData;
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
    public static final String URL_LIFE_EXPECT = "https://raw.githubusercontent.com/samayo/country-json/master/src/country-by-life-expectancy.json";
    public static final String URL_POPULATION = "https://raw.githubusercontent.com/samayo/country-json/master/src/country-by-population.json";
    public static final String URL_CAPITAL = "https://raw.githubusercontent.com/samayo/country-json/master/src/country-by-capital-city.json";
    public static final String URL_GOVERN = "https://raw.githubusercontent.com/samayo/country-json/master/src/country-by-government-type.json";
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
                        final JSONObject  Flags = response;
                        JsonArrayRequest ArrayLifeRequest = new JsonArrayRequest(Request.Method.GET, URL_LIFE_EXPECT, null, new Listener<JSONArray>(){
                            @Override
                            public void onResponse(JSONArray response) {
                                final JSONArray LifeExpect = response;
                                JsonArrayRequest ArrayPopulationRequest = new JsonArrayRequest(Request.Method.GET, URL_POPULATION, null, new Listener<JSONArray>(){
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        final JSONArray Population = response;
                                        JsonArrayRequest ArrayCapitalRequest = new JsonArrayRequest(Request.Method.GET, URL_CAPITAL, null, new Listener<JSONArray>(){
                                            @Override
                                            public void onResponse(JSONArray response) {
                                                final JSONArray Capital = response;
                                                JsonArrayRequest ArrayGovernRequest = new JsonArrayRequest(Request.Method.GET, URL_GOVERN, null, new Listener<JSONArray>(){
                                                    @Override
                                                    public void onResponse(JSONArray response) {
                                                        final JSONArray Govern = response;
                                                        JsonObjectRequest ObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_COVID_DATA, null, new Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                FillDatabaseWithCountries(Countries, Flags, LifeExpect, Population, Capital, Govern, response, listener);
                                                            }
                                                        }, errorListener){};
                                                        requestQueue.add(ObjectRequest);
                                                    }
                                                }, errorListener){};
                                                requestQueue.add(ArrayGovernRequest);
                                            }
                                        }, errorListener){};
                                        requestQueue.add(ArrayCapitalRequest);
                                    }
                                }, errorListener){};
                                requestQueue.add(ArrayPopulationRequest);
                            }
                        }, errorListener){};
                        requestQueue.add(ArrayLifeRequest);
                    }
                }, errorListener){};
                requestQueue.add(ArrayFlagRequest);
            }
        }, errorListener){};
        requestQueue.add(ArrayRequest);
    }

    private void FillDatabaseWithCountries(JSONArray countries, JSONObject flags, JSONArray life, JSONArray population, JSONArray capital, JSONArray govern, JSONObject countriesAviable,Response.Listener<ArrayList<Country>> listener) {
        ArrayList<Country> country_list = new ArrayList<>();
        try{
            for(int i = 0; i < countries.length(); i++)
            {
                JSONObject extractedCountry    = countries.getJSONObject(i);
                JSONObject extractedLifeEx;
                JSONObject extractedPopulation;
                JSONObject extractedCapital;
                JSONObject extractedGovern;

                String country, continent, lifeEx = "Unknown", popu = "Unknown", cap = "Unknown", gov = "Unknown", flag = "";
                country   = extractedCountry.getString("country");
                continent = extractedCountry.getString("continent");

                if(!countriesAviable.isNull(country)) {
                    for(int j = 0; j < life.length(); j++){
                        extractedLifeEx = life.getJSONObject(j);
                        if(extractedLifeEx.getString("country").equals(country)){
                            lifeEx    = extractedLifeEx == null || extractedLifeEx.isNull("expectancy") || extractedLifeEx.getString("expectancy").equals("")? "Unkown" : extractedLifeEx.getString("expectancy");
                            break;
                        }
                    }

                    for(int j = 0; j < population.length(); j++){
                        extractedPopulation = population.getJSONObject(j);
                        if(extractedPopulation.getString("country").equals(country)){
                            popu      = extractedPopulation == null || extractedPopulation.isNull("population") || extractedPopulation.getString("population").equals("")? "Unkown" : extractedPopulation.getString("population");
                            break;
                        }
                    }

                    for(int j = 0; j < capital.length(); j++){
                        extractedCapital = capital.getJSONObject(j);
                        if(extractedCapital.getString("country").equals(country)){
                            cap       = extractedCapital == null || extractedCapital.isNull("city") || extractedCapital.getString("city").equals("")? "Unkown" : extractedCapital.getString("city");
                            break;
                        }
                    }

                    for(int j = 0; j < govern.length(); j++){
                        extractedGovern = govern.getJSONObject(j);
                        if(extractedGovern.getString("country").equals(country)){
                            gov       = extractedGovern == null || extractedGovern.isNull("government") || extractedGovern.getString("government").equals("")? "Unkown" : extractedGovern.getString("government");
                            break;
                        }
                    }

                    JSONObject country_flags = flags.getJSONObject(country);
                    flag = country_flags.isNull("flag")? "Unknown" : country_flags.getString("flag");

                    country_list.add(new Country(country, continent, flag, lifeEx, cap, gov, popu));
                }
            }
            insertCountriesInDao(country_list, listener);
        }catch (JSONException e)
        {
            Log.d("Error", e.getMessage());
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

    public void updateCovidDayData(final String actualCountry, final Listener<ArrayList<CovidDayData>> listener, Response.ErrorListener errorListener){

        JsonObjectRequest ObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_COVID_DATA, null, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                GetCovidDayData(actualCountry, response, listener);
            }
        }, errorListener){};
        requestQueue.add(ObjectRequest);
    }

    private void GetCovidDayData(String actualCountry, JSONObject response, Listener<ArrayList<CovidDayData>> listener) {
        ArrayList<CovidDayData> CovidData = new ArrayList<>();

        try{
            JSONArray countryData = response.getJSONArray(actualCountry);
            String date;
            int confirmed, deaths, recovered;
            for(int i = 0; i < countryData.length(); i++){
                JSONObject data = countryData.getJSONObject(i);
                date = data.isNull("date")? "Unkown" : data.getString("date");
                confirmed = data.isNull("confirmed")? 0 : data.getInt("confirmed");
                deaths = data.isNull("deaths")? 0 : data.getInt("deaths");
                recovered = data.isNull("recovered")? 0 : data.getInt("recovered");

                CovidData.add(new CovidDayData(date, confirmed, deaths, recovered));
            }

            listener.onResponse(CovidData);
        }
        catch (JSONException e){
            Log.d("Error", e.getMessage());
        }
    }
}
