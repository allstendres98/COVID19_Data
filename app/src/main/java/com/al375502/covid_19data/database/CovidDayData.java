package com.al375502.covid_19data.database;

public class CovidDayData {
    public String date;
    public int confirmed, death, recovered;

    public CovidDayData(String date, int confirmed, int death, int recovered) {
        this.date = date;
        this.confirmed = confirmed;
        this.death = death;
        this.recovered = recovered;
    }
}
