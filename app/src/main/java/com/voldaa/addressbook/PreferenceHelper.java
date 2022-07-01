package com.voldaa.addressbook;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class PreferenceHelper extends AppCompatActivity {

     public static void saveData(){
        SharedPreferences sharedPreferences = FragmentFirst.sharedPreferences;
        try{
            ArrayList<String> strPlaces = new ArrayList<>();
            ArrayList<String> latitudes = new ArrayList<>();
            ArrayList<String> longitudes = new ArrayList<>();
            ArrayList<String> newTitle = new ArrayList<>();

            for(StaticRVModel t:FragmentFirst.title){
                newTitle.add(t.getText());
            }

            for(StaticRVModel placeModel:FragmentFirst.places){
                strPlaces.add(placeModel.getText());
            }

            for(LatLng cord:FragmentFirst.locations){
                latitudes.add(Double.toString(cord.latitude));
                longitudes.add(Double.toString(cord.longitude));
            }

            sharedPreferences.edit().putString("places",ObjectSerializer.serialize(strPlaces)).apply();
            sharedPreferences.edit().putString("lats",ObjectSerializer.serialize(latitudes)).apply();
            sharedPreferences.edit().putString("lons",ObjectSerializer.serialize(longitudes)).apply();
            sharedPreferences.edit().putString("title",ObjectSerializer.serialize(newTitle)).apply();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
