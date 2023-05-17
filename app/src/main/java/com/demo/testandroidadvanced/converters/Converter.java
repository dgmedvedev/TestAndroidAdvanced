package com.demo.testandroidadvanced.converters;

import androidx.room.TypeConverter;

import com.demo.testandroidadvanced.pojo.Specialty;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Converter {
    @TypeConverter
    public String listSpecialtyToJson(List<Specialty> specialties) {
        return new Gson().toJson(specialties);
    }

    @TypeConverter
    public List<Specialty> stringToListSpecialty(String specialtiesAsString) {
        Gson gson = new Gson();
        ArrayList objects = gson.fromJson(specialtiesAsString, ArrayList.class);
        ArrayList<Specialty> specialties = new ArrayList<>();
        for (Object object : objects) {
            specialties.add(gson.fromJson(object.toString(), Specialty.class));
        }
        return specialties;
    }
}