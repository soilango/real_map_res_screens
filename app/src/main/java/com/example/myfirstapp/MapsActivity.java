package com.example.myfirstapp;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myfirstapp.databinding.ActivityMapsBinding;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private ArrayList<Marker> markerList;

    private Map<String, String> building_to_desc;

    private String uscid = "";

    private Building[] buildings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildings = new Building[]{};

        try {
            getBuildings();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        Intent intent = getIntent();
//        if (intent.getStringExtra("uscid") == null) {
//            Button resButton = (Button) findViewById(R.id.buttonReserve);
//            String text = "Create Account";
//            resButton.setText(text);
//        }
//        else {
//            uscid = intent.getStringExtra("uscid");
//        }

    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        markerList = new ArrayList<>();
        building_to_desc = new HashMap<>();


        mMap = googleMap;

        //get latlong for corners for specified place
        LatLng one = new LatLng(34.025967, -118.292250);
        LatLng two = new LatLng(34.018439, -118.280133);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //add them to builder
        builder.include(one);
        builder.include(two);

        LatLngBounds bounds = builder.build();

        //get width and height to current display screen
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        // 20% padding
        int padding = (int) (width * 0.20);

        //set latlong bounds
        mMap.setLatLngBoundsForCameraTarget(bounds);

        //move camera to fill the bound to screen
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));

        //set zoom to level to current so that you won't be able to zoom out viz. move outside bounds
        mMap.setMinZoomPreference(mMap.getCameraPosition().zoom);

        for (int i = 0; i < buildings.length; i++) {
            Building b = buildings[i];
            String b_name = b.buildingName;
            String b_desc = b.description;
            double latitude = b.latitude;
            double longitude = b.longitude;

            LatLng coords = new LatLng(latitude, longitude);
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(coords)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.unselected_marker)));

            marker.setTag(b_name);
            markerList.add(marker);
            building_to_desc.put(b_name, b_desc);
        }
//        LatLng taper = new LatLng(34.022412027200986, -118.28451527064657);
//        Marker taperHall = googleMap.addMarker(new MarkerOptions()
//                .position(taper)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.unselected_marker)));
//        String name = "Taper Hall";
//        taperHall.setTag(name);
//        markerList.add(taperHall);
//        String desc = "Taper Hall is a common classroom and workspace for students. It is a quiet building with 3 floors and located near the main USC campus entrance.";
//        building_to_desc.put(name, desc);
//
//        LatLng jff = new LatLng(34.01871043598973, -118.28239854170198);
//        Marker fertitta = googleMap.addMarker(new MarkerOptions()
//                .position(jff)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.unselected_marker)));
//        name = "Jill & Frank Fertitta Hall";
//        fertitta.setTag(name);
//        markerList.add(fertitta);
//        desc = "Fertitta Hall features 21 classrooms, two lecture halls, 50 breakout rooms, an outdoor courtyard, and advanced technology. The building is located at Figueroa Street and Exposition Drive, close to the main entrance of USC.";
//        building_to_desc.put(name, desc);
//
//        LatLng rth = new LatLng(34.0200292866705, -118.28981517564547);
//        Marker tutor_hall = googleMap.addMarker(new MarkerOptions()
//                .position(rth)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.unselected_marker)));
//        name = "Ronald Tutor Hall";
//        tutor_hall.setTag(name);
//        markerList.add(tutor_hall);
//        desc = "Ronald Tutor Hall is a five-story, 103,000 GSF engineering facility that accommodates undergraduate and graduate studies in information technology, bioengineering, and nanotechnology. The flexible space features labs and research areas extending from a central-core plan as well as the Viterbi Museum.";
//        building_to_desc.put(name, desc);

//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(taper));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(jff));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(rth));

        googleMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
    }

    public boolean onMarkerClick(Marker marker) {
        String selected_name = String.valueOf(marker.getTag());
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.selected_marker));
        for (int i = 0; i < markerList.size(); i++) {
            Marker mk = markerList.get(i);
            String name = String.valueOf(mk.getTag());
//            System.out.println(selected_name);
            if (!name.equals(selected_name)) {
                mk.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.unselected_marker));
            }
        }

        LinearLayout b = findViewById(R.id.overlay);
        String desc = building_to_desc.get(selected_name);

        TextView building_name = (TextView) findViewById(R.id.buildingName);
        TextView building_desc = (TextView) findViewById(R.id.buildingDescription);

        building_name.setText(selected_name);
        building_desc.setText(desc);

        b.setVisibility(View.VISIBLE);
        return false;
    }

    public void clickButton(View view) {
        Intent intent = new Intent(this, Reservation.class);
        // add another intent for creating account/logging in

        Button b = (Button) findViewById(R.id.buttonReserve);
        String text = (String) b.getText();
        if (text.equals("Make Reservation")) {
            TextView tv1 = (TextView) findViewById(R.id.buildingName);
//            TextView tv2 = (TextView) findViewById(R.id.buildingDescription);

            String building_name = (String) tv1.getText();
            intent.putExtra("building_name", building_name);

//            String building_desc = (String) tv2.getText();
//            intent.putExtra("building_desc", building_desc);

            startActivity(intent);

        }
        else {
            // go to sign up / log-in screen
        }

    }

    public void getBuildings() throws IOException {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy gfgPolicy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(gfgPolicy);
        }
//        System.out.println("here");
        URL url = new URL("http://34.125.226.6:8080/getAllBuildings");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

//        System.out.println(status);

        Gson gson = new Gson();

        buildings = new Gson().fromJson(content.toString(), Building[].class);


        con.disconnect();


    }

}