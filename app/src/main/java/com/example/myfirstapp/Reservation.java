package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class Reservation extends AppCompatActivity implements View.OnClickListener {

    boolean outdoorSelected = true;

    String building_name = "";

    HashMap<String, Object> avails_outdoor = new HashMap<>();
    HashMap<String, Object> avails_indoor = new HashMap<>();

    HashMap<Integer, String> times_outdoor = new HashMap<>();

    HashMap<Integer, String> days_outdoor = new HashMap<>();

//    String building_desc = "";

    Building building = new Building(null, null, null, null, 0, 0, null, null, null);
//    List<Integer> building_img = new ArrayList<>();

    private ArrayList<TextView> cell_tvs;

    private ArrayList<TextView> cell_tvs_indoor;

    private ArrayList<String> selected_cells_idx;

    private ArrayList<String> selected_cells_idx_indoor;

    private static final int COLUMN_COUNT = 6;

    private boolean newRes = true;

    // IF FALSE: change the header to 'edit reservation' and load past reservation

    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }

    private int findIndexofCellTextViewIndoor(TextView tv) {
        for (int n=0; n<cell_tvs_indoor.size(); n++) {
            if (cell_tvs_indoor.get(n) == tv)
                return n;
        }
        return -1;
    }

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        Intent intent = getIntent();
        building_name = intent.getStringExtra("building_name");
//        building_desc = intent.getStringExtra("building_desc");

        try {
            getBuilding();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        TextView b_name = (TextView) findViewById(R.id.buildingTitle);
        TextView b_desc = (TextView) findViewById(R.id.buildingDescription);
        // do same for building image

        b_name.setText(building_name);

        b_desc.setText(building.description);

        try {
            getBuildingAvailability();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }


        outdoorSelected = true;

        cell_tvs = new ArrayList<>();
        selected_cells_idx = new ArrayList<>();
        selected_cells_idx_indoor = new ArrayList<>();
        cell_tvs_indoor = new ArrayList<>();

        androidx.gridlayout.widget.GridLayout grid = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.gridLayout01);

        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 6; j++) {
                TextView tv = new TextView(this);
                tv.setHeight( dpToPixel(32) );
                tv.setWidth( dpToPixel(55) );
                tv.setTextSize( 16 );//dpToPixel(32) );
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.parseColor("#8A00C2"));
                tv.setOnClickListener(this);

                tv.getBackground().setAlpha(125);
                tv.setText("1");

                if (i == 0 && j == 0) {
                    tv.setText("");
                    tv.setBackgroundColor(Color.WHITE);
                }
                else if (i == 0) {
                    tv.setBackgroundColor(Color.WHITE);
                    tv.setTextColor(Color.parseColor("#737373"));
                    if (j == 1) {
                        tv.setText("M");
                        days_outdoor.put(1, "M");
                    }
                    if (j == 2) {
                        tv.setText("T");
                        days_outdoor.put(2, "T");
                    }
                    if (j == 3) {
                        tv.setText("W");
                        days_outdoor.put(3, "W");
                    }
                    if (j == 4) {
                        tv.setText("Th");
                        days_outdoor.put(4, "Th");
                    }
                    if (j == 5) {
                        tv.setText("F");
                        days_outdoor.put(5, "F");
                    }
                }
                else if (j == 0) {
                    if (i == 1) {
                        tv.setText("08:00-08:30");
                        times_outdoor.put(1, "08:00:00");
                    }
                    else if (i == 2) {
                        tv.setText("08:30-09:00");
                        times_outdoor.put(2, "08:30:00");
                    }
                    else if (i == 3) {
                        tv.setText("09:00-09:30");
                        times_outdoor.put(3, "09:00:00");
                    }
                    else if (i == 4) {
                        tv.setText("09:30-10:00");
                        times_outdoor.put(4, "09:30:00");
                    }
                    else if (i == 5) {
                        tv.setText("10:00-10:30");
                        times_outdoor.put(5, "10:00:00");
                    }
                    else if (i == 6) {
                        tv.setText("10:30-11:00");
                        times_outdoor.put(6, "10:30:00");
                    }
                    else if (i == 7) {
                        tv.setText("11:00-11:30");
                        times_outdoor.put(7, "11:00:00");
                    }
                    else if (i == 8) {
                        tv.setText("11:30-12:00");
                        times_outdoor.put(8, "11:30:00");
                    }
                    else if (i == 9) {
                        tv.setText("12:00-12:30");
                        times_outdoor.put(9, "12:00:00");
                    }
                    else if (i == 10) {
                        tv.setText("12:30-13:00");
                        times_outdoor.put(10, "12:30:00");
                    }
                    else if (i == 11) {
                        tv.setText("13:00-13:30");
                        times_outdoor.put(11, "13:00:00");
                    }
                    else if (i == 12) {
                        tv.setText("13:30-14:00");
                        times_outdoor.put(12, "13:30:00");
                    }
                    else if (i == 13) {
                        tv.setText("14:00-14:30");
                        times_outdoor.put(13, "14:00:00");
                    }
                    else if (i == 14) {
                        tv.setText("14:30-15:00");
                        times_outdoor.put(14, "14:30:00");
                    }
                    else if (i == 15) {
                        tv.setText("15:00-15:30");
                        times_outdoor.put(15, "15:00:00");
                    }
                    else if (i == 16) {
                        tv.setText("15:30-16:00");
                        times_outdoor.put(16, "15:30:00");
                    }
                    else if (i == 17) {
                        tv.setText("16:00-16:30");
                        times_outdoor.put(17, "16:00:00");
                    }
                    else if (i == 18) {
                        tv.setText("16:30-17:00");
                        times_outdoor.put(18, "16:30:00");
                    }
                    else if (i == 19) {
                        tv.setText("17:00-17:30");
                        times_outdoor.put(19, "17:00:00");
                    }
                    else if (i == 20) {
                        tv.setText("17:30-18:00");
                        times_outdoor.put(20, "17:30:00");
                    }
                    else if (i == 21) {
                        tv.setText("18:00-18:30");
                        times_outdoor.put(21, "18:00:00");
                    }
                    else if (i == 22) {
                        tv.setText("18:30-19:00");
                        times_outdoor.put(22, "18:30:00");
                    }
                    else if (i == 23) {
                        tv.setText("19:00-19:30");
                        times_outdoor.put(23, "19:00:00");
                    }
                    else if (i == 24) {
                        tv.setText("19:30-20:00");
                        times_outdoor.put(24, "19:30:00");
                    }

                    tv.setBackgroundColor(Color.WHITE);
                    tv.setTextColor(Color.parseColor("#737373"));
                    tv.setWidth( dpToPixel(100) );
                    tv.setTextSize( 15 );
                }

                // ADD FUNCTIONALITY FOR WHEN NO SEATS AVAILABLE
//                tv.setBackgroundColor(Color.LTGRAY);
//                tv.setText("0");
//                tv.getBackground().setAlpha(255);


                androidx.gridlayout.widget.GridLayout.LayoutParams lp = new androidx.gridlayout.widget.GridLayout.LayoutParams();
                lp.setMargins(dpToPixel(1), dpToPixel(1), dpToPixel(1), dpToPixel(1));
                lp.rowSpec = androidx.gridlayout.widget.GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);

                cell_tvs.add(tv);
            }
        }

        boolean tooEarly = true;
        boolean tooLate = false;
        for (int i = 1; i < 25; i++) {
            for (Map.Entry<Integer, String> entry : times_outdoor.entrySet()) {
                if (entry.getKey().equals(i) && entry.getValue().equals(building.openTime)) {
                    tooEarly = false;
                }
                else if ((entry.getKey().equals(i) && entry.getValue().equals(building.closeTime))) {
                    tooLate = true;
                }
            }

            for (int j = 1; j < 6; j++) {
                TextView tv = findTextView(i, j);

                if (tooEarly || tooLate) {
                    tv.setBackgroundColor(Color.DKGRAY);
                    tv.getBackground().setAlpha(255);
                    continue;
                }

                String time = times_outdoor.get(i);
                String day = days_outdoor.get(j);
                String key = day + " " + time;

                System.out.println(key);


                double temp = (double) avails_outdoor.get(key);

                int seats = (int)Math.round(temp);

                tv.setText(String.valueOf(seats));
            }
        }

        androidx.gridlayout.widget.GridLayout grid2 = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.gridLayout02);

        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 6; j++) {
                TextView tv = new TextView(this);
                tv.setHeight( dpToPixel(32) );
                tv.setWidth( dpToPixel(55) );
                tv.setTextSize( 16 );//dpToPixel(32) );
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.parseColor("#8A00C2"));
                tv.setOnClickListener(this::onClickTV_indoor);
                tv.setClickable(true);
                tv.setFocusable(true);

                tv.getBackground().setAlpha(125);
                tv.setText("1");

                if (i == 0 && j == 0) {
                    tv.setText("");
                    tv.setBackgroundColor(Color.WHITE);
                }
                else if (i == 0) {
                    tv.setBackgroundColor(Color.WHITE);
                    tv.setTextColor(Color.parseColor("#737373"));
                    if (j == 1) {
                        tv.setText("M");
                    }
                    if (j == 2) {
                        tv.setText("T");
                    }
                    if (j == 3) {
                        tv.setText("W");
                    }
                    if (j == 4) {
                        tv.setText("Th");
                    }
                    if (j == 5) {
                        tv.setText("F");
                    }
                }
                else if (j == 0) {
                    if (i == 1) {
                        tv.setText("8:00-8:30am");
                    }
                    else if (i == 2) {
                        tv.setText("8:30-9:00am");
                    }
                    else if (i == 3) {
                        tv.setText("9:00-9:30am");
                    }
                    else if (i == 4) {
                        tv.setText("9:30-10:00am");
                    }
                    else if (i == 5) {
                        tv.setText("10:00-10:30am");
                    }
                    else if (i == 6) {
                        tv.setText("10:30-11:00am");
                    }
                    else if (i == 7) {
                        tv.setText("11:00-11:30am");
                    }
                    else if (i == 8) {
                        tv.setText("11:30-12:00pm");
                    }
                    else if (i == 9) {
                        tv.setText("12:00-12:30pm");
                    }
                    else if (i == 10) {
                        tv.setText("12:30-1:00pm");
                    }
                    else if (i == 11) {
                        tv.setText("1:00-1:30pm");
                    }
                    else if (i == 12) {
                        tv.setText("1:30-2:00pm");
                    }
                    else if (i == 13) {
                        tv.setText("2:00-2:30pm");
                    }
                    else if (i == 14) {
                        tv.setText("2:30-3:00pm");
                    }
                    else if (i == 15) {
                        tv.setText("3:00-3:30pm");
                    }
                    else if (i == 16) {
                        tv.setText("3:30-4:00pm");
                    }
                    else if (i == 17) {
                        tv.setText("4:00-4:30pm");
                    }
                    else if (i == 18) {
                        tv.setText("4:30-5:00pm");
                    }
                    else if (i == 19) {
                        tv.setText("5:00-5:30pm");
                    }
                    else if (i == 20) {
                        tv.setText("5:30-6:00pm");
                    }
                    else if (i == 21) {
                        tv.setText("6:00-6:30pm");
                    }
                    else if (i == 22) {
                        tv.setText("6:30-7:00pm");
                    }
                    else if (i == 23) {
                        tv.setText("7:00-7:30pm");
                    }
                    else if (i == 24) {
                        tv.setText("7:30-8:00pm");
                    }
                    tv.setBackgroundColor(Color.WHITE);
                    tv.setTextColor(Color.parseColor("#737373"));
                    tv.setWidth( dpToPixel(100) );
                    tv.setTextSize( 15 );
                }
                else if (i%3 == 0 || j%3 == 0) {
                    tv.setBackgroundColor(Color.LTGRAY);
                    tv.setText("0");
                    tv.getBackground().setAlpha(255);
                }

                androidx.gridlayout.widget.GridLayout.LayoutParams lp = new androidx.gridlayout.widget.GridLayout.LayoutParams();
                lp.setMargins(dpToPixel(1), dpToPixel(1), dpToPixel(1), dpToPixel(1));
                lp.rowSpec = androidx.gridlayout.widget.GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid2.addView(tv, lp);

                cell_tvs_indoor.add(tv);
            }
        }

    }

    public void onClickTV_indoor(View view) {
        TextView tv = (TextView) view;

        int n = findIndexofCellTextViewIndoor(tv);
        int row = n/COLUMN_COUNT;
        int col = n%COLUMN_COUNT;

        String str = tv.getText().toString();

        if (row == 0 || col == 0) {
            return;
        }

        String idx = String.valueOf(row) + "," + String.valueOf(col);
        if (!selected_cells_idx_indoor.contains(idx)) {
            if (str.equals("0")) {
                return;
            }
            if (selected_cells_idx_indoor.size() == 4) {
                return;
            }
            if (selected_cells_idx_indoor.size() == 0) {
                selected_cells_idx_indoor.add(idx);
                tv.setBackgroundColor(Color.parseColor("#8A00C2"));
                tv.getBackground().setAlpha(255);
                int num_seats = Integer.parseInt(str);
                num_seats--;
                String seats = String.valueOf(num_seats);
                tv.setText(seats);
//                if (num_seats == 0) {
//                    tv.setBackgroundColor(Color.LTGRAY);
//                }
            }
            else {
                for (int l = 0; l < selected_cells_idx_indoor.size(); l++) {
                    String index = selected_cells_idx_indoor.get(l);
                    String string_i = "";
                    String string_j = "";

                    int k = 0;
                    while (index.charAt(k) != ',') {
                        string_i += index.charAt(k);
                        k++;
                    }
                    k++;
                    while (k < index.length()) {
                        string_j += index.charAt(k);
                        k++;
                    }

                    int i = Integer.valueOf(string_i);
                    int j = Integer.valueOf(string_j);

                    if ((i-1 == row && j == col) || (i+1 == row && j == col)) {
                        selected_cells_idx_indoor.add(idx);
                        tv.setBackgroundColor(Color.parseColor("#8A00C2"));
                        tv.getBackground().setAlpha(255);
                        int num_seats = Integer.parseInt(str);
                        num_seats--;
                        String seats = String.valueOf(num_seats);
                        tv.setText(seats);
//                        if (num_seats == 0) {
//                            tv.setBackgroundColor(Color.LTGRAY);
//                        }
                        return;
                    }
                }
            }
        }
        else {
            for (int l = 0; l < selected_cells_idx_indoor.size(); l++) {
                String index = selected_cells_idx_indoor.get(l);
                String string_i = "";
                String string_j = "";

                int k = 0;
                while (index.charAt(k) != ',') {
                    string_i += index.charAt(k);
                    k++;
                }
                k++;
                while (k < index.length()) {
                    string_j += index.charAt(k);
                    k++;
                }

                int i = Integer.valueOf(string_i);
                int j = Integer.valueOf(string_j);

                TextView tv_selected = findTextViewIndoor(i, j);
                String tv_str = tv_selected.getText().toString();

                int num_seats = Integer.parseInt(tv_str);
                num_seats++;
                String seats = String.valueOf(num_seats);
                tv_selected.setText(seats);
                tv_selected.setBackgroundColor(Color.parseColor("#8A00C2"));
                tv_selected.getBackground().setAlpha(125);

            }
            selected_cells_idx_indoor = new ArrayList<>();
        }
    }

    public void onClick(View view) {
        TextView tv = (TextView) view;

        int n = findIndexOfCellTextView(tv);
        int row = n/COLUMN_COUNT;
        int col = n%COLUMN_COUNT;

        String str = tv.getText().toString();

        if (row == 0 || col == 0) {
            return;
        }


        String idx = String.valueOf(row) + "," + String.valueOf(col);
        if (!selected_cells_idx.contains(idx)) {
            if (str.equals("0")) {
                return;
            }
            if (selected_cells_idx.size() == 4) {
                return;
            }
            if (selected_cells_idx.size() == 0) {
                selected_cells_idx.add(idx);
                tv.setBackgroundColor(Color.parseColor("#8A00C2"));
                tv.getBackground().setAlpha(255);
                int num_seats = Integer.parseInt(str);
                num_seats--;
                String seats = String.valueOf(num_seats);
                tv.setText(seats);
//                if (num_seats == 0) {
//                    tv.setBackgroundColor(Color.LTGRAY);
//                }
            }
            else {
                for (int l = 0; l < selected_cells_idx.size(); l++) {
                    String index = selected_cells_idx.get(l);
                    String string_i = "";
                    String string_j = "";

                    int k = 0;
                    while (index.charAt(k) != ',') {
                        string_i += index.charAt(k);
                        k++;
                    }
                    k++;
                    while (k < index.length()) {
                        string_j += index.charAt(k);
                        k++;
                    }

                    int i = Integer.valueOf(string_i);
                    int j = Integer.valueOf(string_j);

                    if ((i-1 == row && j == col) || (i+1 == row && j == col)) {
                        selected_cells_idx.add(idx);
                        tv.setBackgroundColor(Color.parseColor("#8A00C2"));
                        tv.getBackground().setAlpha(255);
                        int num_seats = Integer.parseInt(str);
                        num_seats--;
                        String seats = String.valueOf(num_seats);
                        tv.setText(seats);
//                        if (num_seats == 0) {
//                            tv.setBackgroundColor(Color.LTGRAY);
//                        }
                        return;
                    }
                }
            }
        }
        else {
            for (int l = 0; l < selected_cells_idx.size(); l++) {
                String index = selected_cells_idx.get(l);
                String string_i = "";
                String string_j = "";

                int k = 0;
                while (index.charAt(k) != ',') {
                    string_i += index.charAt(k);
                    k++;
                }
                k++;
                while (k < index.length()) {
                    string_j += index.charAt(k);
                    k++;
                }

                int i = Integer.valueOf(string_i);
                int j = Integer.valueOf(string_j);

                TextView tv_selected = findTextView(i, j);
                String tv_str = tv_selected.getText().toString();

                int num_seats = Integer.parseInt(tv_str);
                num_seats++;
                String seats = String.valueOf(num_seats);
                tv_selected.setText(seats);
                tv_selected.setBackgroundColor(Color.parseColor("#8A00C2"));
                tv_selected.getBackground().setAlpha(125);

            }
            selected_cells_idx = new ArrayList<>();
        }

    }

    private TextView findTextView(int row, int col) {
        int idx = 0;
        idx += (row) * COLUMN_COUNT;
        idx += col;
        for (int i = 0; i < cell_tvs.size(); i++) {
            if (i == idx) {
                return cell_tvs.get(i);
            }
        }
        return cell_tvs.get(0);
    }

    private TextView findTextViewIndoor(int row, int col) {
        int idx = 0;
        idx += (row) * COLUMN_COUNT;
        idx += col;
        for (int i = 0; i < cell_tvs_indoor.size(); i++) {
            if (i == idx) {
                return cell_tvs_indoor.get(i);
            }
        }
        return cell_tvs_indoor.get(0);
    }

    // ADD FUNCTION TO TOGGLE BETWEEN WEEKS -> make the associated backend calls & clear lists

    public void toggle(View view) {
        ImageView iv = (ImageView) view;
        if (outdoorSelected) {
            outdoorSelected = false;
            iv.setImageResource(R.drawable.indoor_long);

            androidx.gridlayout.widget.GridLayout grid = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.gridLayout01);
            androidx.gridlayout.widget.GridLayout grid2 = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.gridLayout02);

            grid.setVisibility(View.GONE);
            grid2.setVisibility(View.VISIBLE);

            for (int l = 0; l < selected_cells_idx.size(); l++) {
                String index = selected_cells_idx.get(l);
                String string_i = "";
                String string_j = "";

                int k = 0;
                while (index.charAt(k) != ',') {
                    string_i += index.charAt(k);
                    k++;
                }
                k++;
                while (k < index.length()) {
                    string_j += index.charAt(k);
                    k++;
                }

                int i = Integer.valueOf(string_i);
                int j = Integer.valueOf(string_j);

                TextView tv_selected = findTextView(i, j);
                String tv_str = tv_selected.getText().toString();

                int num_seats = Integer.parseInt(tv_str);
                num_seats++;
                String seats = String.valueOf(num_seats);
                tv_selected.setText(seats);
                tv_selected.setBackgroundColor(Color.parseColor("#8A00C2"));
                tv_selected.getBackground().setAlpha(125);

            }
            selected_cells_idx = new ArrayList<>();

        }
        else {
            outdoorSelected = true;
            iv.setImageResource(R.drawable.outdoor_long);

            androidx.gridlayout.widget.GridLayout grid = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.gridLayout01);
            androidx.gridlayout.widget.GridLayout grid2 = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.gridLayout02);

            grid.setVisibility(View.VISIBLE);
            grid2.setVisibility(View.GONE);

            for (int l = 0; l < selected_cells_idx_indoor.size(); l++) {
                String index = selected_cells_idx_indoor.get(l);
                String string_i = "";
                String string_j = "";

                int k = 0;
                while (index.charAt(k) != ',') {
                    string_i += index.charAt(k);
                    k++;
                }
                k++;
                while (k < index.length()) {
                    string_j += index.charAt(k);
                    k++;
                }

                int i = Integer.valueOf(string_i);
                int j = Integer.valueOf(string_j);

                TextView tv_selected = findTextViewIndoor(i, j);
                String tv_str = tv_selected.getText().toString();

                int num_seats = Integer.parseInt(tv_str);
                num_seats++;
                String seats = String.valueOf(num_seats);
                tv_selected.setText(seats);
                tv_selected.setBackgroundColor(Color.parseColor("#8A00C2"));
                tv_selected.getBackground().setAlpha(125);

            }
            selected_cells_idx_indoor = new ArrayList<>();
        }
    }

    public void makeReservation(View view) {
        TextView error = (TextView) findViewById(R.id.errorMsg);
        if (selected_cells_idx.size() == 0 && selected_cells_idx_indoor.size() == 0) {
            error.setText("Please select at least one time slot.");
            error.setVisibility(View.VISIBLE);
        }
        // else if user already has a reservation, set error message accordingly
    }

    public void backArrow(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        // add another intent to go to profile screen

        if (newRes == true) {
            // go to map
            startActivity(intent);
        }
        else {
            // go to profile screen
        }
    }

    public void getBuilding() throws IOException {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy gfgPolicy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(gfgPolicy);
        }
        System.out.println("here");
        String url_string = "http://172.20.10.6:8080/getBuilding?documentId=" + building_name;
        URL url = new URL(url_string);
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

        System.out.println(status);

        Gson gson = new Gson();

        building = new Gson().fromJson(content.toString(), Building.class);
        System.out.println(building.description);


        con.disconnect();


    }

    public void getBuildingAvailability() throws IOException, ParseException {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy gfgPolicy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(gfgPolicy);
        }
        System.out.println("here");
        //getBuildingAvailability?buildingName=Leavey Library&isIndoor=true&weekDateStr=2023-10-30
//        boolean isIndoor = true;
//        if (outdoorSelected) {
//            isIndoor = false;
//        }



        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date monday = cal.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        String res_week = sdf.format(monday);

        System.out.println(res_week);

        boolean isIndoor = true;
        String url_string = "http://172.20.10.6:8080/getBuildingAvailability?buildingName=" + building_name + "&isIndoor=" + isIndoor + "&weekDateStr=" + res_week;
        URL url = new URL(url_string);
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

        System.out.println(status);


        Gson gson = new Gson();
//
        avails_indoor = new Gson().fromJson(content.toString(), HashMap.class);

        System.out.println(avails_indoor);

        isIndoor = false;
        url_string = "http://172.20.10.6:8080/getBuildingAvailability?buildingName=" + building_name + "&isIndoor=" + isIndoor + "&weekDateStr=" + res_week;
        url = new URL(url_string);
        con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");
//        status = con.getResponseCode();
        in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String ipline;
        content = new StringBuffer();
        while ((ipline = in.readLine()) != null) {
            content.append(ipline);
        }
        in.close();

        avails_outdoor = new Gson().fromJson(content.toString(), HashMap.class);

        System.out.println(avails_outdoor);

        con.disconnect();


    }

}