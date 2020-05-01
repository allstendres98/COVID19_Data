package com.al375502.covid_19data.database;

import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {Country.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {
    public abstract DAO getDao();
}
