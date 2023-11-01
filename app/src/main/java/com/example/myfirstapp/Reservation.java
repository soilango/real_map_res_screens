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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Reservation extends AppCompatActivity implements View.OnClickListener {

    boolean outdoorSelected = true;

    String building_name = "";

//    HashMap<String, Integer> buildings_avail = new HashMap<>();

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

        // GET REST OF BUILDING INFO






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
                else if (i%2 == 0 && j%2 == 0) {
                    tv.setBackgroundColor(Color.LTGRAY);
                    tv.setText("0");
                    tv.getBackground().setAlpha(255);
                }


                androidx.gridlayout.widget.GridLayout.LayoutParams lp = new androidx.gridlayout.widget.GridLayout.LayoutParams();
                lp.setMargins(dpToPixel(1), dpToPixel(1), dpToPixel(1), dpToPixel(1));
                lp.rowSpec = androidx.gridlayout.widget.GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);

                cell_tvs.add(tv);
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

    public void toggle(View view) {
        ImageView iv = (ImageView) view;
        if (outdoorSelected) {
            outdoorSelected = false;
            iv.setImageResource(R.drawable.indoor_selected);

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
            iv.setImageResource(R.drawable.outdoor_selected);

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
        String url_string = "http://172.20.10.2:8080/getBuilding?documentId=" + building_name;
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

}