package com.al375502.covid_19data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface DAO {
    @Query("SELECT * FROM country WHERE continent = :nameC ORDER BY name")
    List<Country> allCountriesInAContinent(String nameC);

    @Query("SELECT DISTINCT continent FROM country")
    List<String> allContinents();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCountry(List<Country> countries);
}
