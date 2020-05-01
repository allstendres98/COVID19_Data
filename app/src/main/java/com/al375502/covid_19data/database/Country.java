package com.al375502.covid_19data.database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Country")
public class Country implements Comparable<Country>, Parcelable {
    @PrimaryKey
    public String name;

    @ColumnInfo(name = "Continent")
    public String continent;

    protected Country(Parcel in) {
        name = in.readString();
        continent = in.readString();
    }

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(continent);
    }

    @Override
    public int compareTo(Country o) {
        return 0;
    }

    public Country(String name, String continent) {
        this.name = name;
        this.continent = continent;
    }
}
