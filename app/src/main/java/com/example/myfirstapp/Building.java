package com.example.myfirstapp;

import java.util.HashMap;
import java.util.List;

public class Building {
    public String buildingName;
    public HashMap<String, Integer> indoorAvailability;
    public HashMap<String, Integer> outdoorAvailability;
    public String description;
    public double latitude;
    public double longitude;
    public String openTime;
    public String closeTime;
    public List<Integer> buildingImage;

    public Building(String b_name, HashMap<String, Integer> in_avail, HashMap<String, Integer> out_avail, String desc, double lat, double lon, String o_time, String c_time, List<Integer> b_img) {
        buildingName = b_name;
        indoorAvailability = in_avail;
        outdoorAvailability = out_avail;
        description = desc;
        latitude = lat;
        longitude = lon;
        openTime = o_time;
        closeTime = c_time;
        buildingImage = b_img;
    }
}
