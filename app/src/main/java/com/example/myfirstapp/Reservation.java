package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

// TO DO
/*
* 1. increase image quality
* 2. figure out how to share google api key
* */

public class Reservation extends AppCompatActivity implements View.OnClickListener {

    boolean outdoorSelected = true;

    boolean thisWeek = true;

    HashMap<String, Object> prevRes = new HashMap<>();

    String building_name = "";

    boolean resWeekThis = true;

    HashMap<String, Object> avails_outdoor = new HashMap<>();
    HashMap<String, Object> avails_indoor = new HashMap<>();

    HashMap<Integer, String> times_outdoor = new HashMap<>();

    HashMap<Integer, String> days_outdoor = new HashMap<>();

    HashMap<Integer, String> times_indoor = new HashMap<>();

    HashMap<Integer, String> days_indoor = new HashMap<>();



//    String building_desc = "";

    Building building = new Building(null, null, null, null, 0, 0, null, null, null);
//    List<Integer> building_img = new ArrayList<>();

    private ArrayList<TextView> cell_tvs;

    private ArrayList<TextView> cell_tvs_indoor;

    private ArrayList<String> selected_cells_idx;

    private ArrayList<String> selected_cells_idx_indoor;

    private static final int COLUMN_COUNT = 6;

    private boolean newRes = false;

    private String uscId = "1111111111";


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

        ImageView b_img = (ImageView) findViewById(R.id.buildingImg);


        b_name.setText(building_name);

        b_desc.setText(building.description);

        String img_str = building.buildingImage;
        img_str = img_str.replaceAll("%", "\n");
        if (img_str != null) {
            byte[] im = Base64.decode(img_str, Base64.DEFAULT);
            System.out.println(im);
            Bitmap bmp = BitmapFactory.decodeByteArray(im, 0, im.length);
            b_img.setImageBitmap(Bitmap.createScaledBitmap(bmp, 62, 80, false));
        }

        try {
            getBuildingAvailability();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        if (newRes == false) {
            Button b = (Button) findViewById(R.id.buttonReserve);
            b.setText("Edit Reservation");
        }

        outdoorSelected = true;

        cell_tvs = new ArrayList<>();
        selected_cells_idx = new ArrayList<>();
        selected_cells_idx_indoor = new ArrayList<>();
        cell_tvs_indoor = new ArrayList<>();

        populateGrids();

        if (!newRes) {
            populateRes();
        }

//        if (!newRes) {
//            try {
//                loadRes();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }


    }

    public void loadRes() throws IOException {
        String url_string = "http://34.125.226.6:8080/getCurrentReservation?documentId=" + uscId;
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

        Gson gson = new Gson();
//
        prevRes = new Gson().fromJson(content.toString(), HashMap.class);

//        populateRes();

        System.out.println("FOUND PREVIOUS RES:" + prevRes);

    }

    public void populateRes() {
        List<String> timeBlocks = (List<String>) prevRes.get("timeBlocks");
        int j = 0;
        String str = timeBlocks.get(0);
        if (str.charAt(0) == 'M') {
            j = 1;
        }
        else if (str.charAt(0) == 'T' && str.charAt(1) == 'h') {
            j = 4;
        }
        else if (str.charAt(0) == 'T') {
            j = 2;
        }
        else if (str.charAt(0) == 'W') {
            j = 3;
        }
        else if (str.charAt(0) == 'F') {
            j = 5;
        }

        List<String> times = new ArrayList<>();

        for (int i = 0; i < timeBlocks.size(); i++) {
            String st = timeBlocks.get(i);
            times.add(st.substring(2));
        }

        List<Integer> rows = new ArrayList<>();


        if ((prevRes.get("indoor").toString()).equals("true")) {
            for (int i = 0; i < times.size(); i++) {
                for (Map.Entry<Integer, String> entry : times_indoor.entrySet()) {
                    if (entry.getValue().equals(times.get(i))) {
                        rows.add(entry.getKey());
                    }
                }
            }

            androidx.gridlayout.widget.GridLayout grid2 = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.gridLayout02);
            androidx.gridlayout.widget.GridLayout grid = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.gridLayout01);

            grid.setVisibility(View.GONE);
            grid2.setVisibility(View.VISIBLE);

            ImageView iv1 = (ImageView) findViewById(R.id.toggle);
            iv1.setImageResource(R.drawable.indoor_long);

            for (int i = 0; i < rows.size(); i++) {
                int row = rows.get(i);
                TextView tv = findTextViewIndoor(row, j);
                String tv_str = (String) tv.getText();
                if (tv_str.equals("")  == false) {
                    tv.setBackgroundColor(Color.parseColor("#8A00C2"));
                    tv.getBackground().setAlpha(255);
                    String idx = String.valueOf(row) + "," + String.valueOf(j);
                    selected_cells_idx_indoor.add(idx);
                }
            }

        }
        else {

            for (int i = 0; i < times.size(); i++) {
                for (Map.Entry<Integer, String> entry : times_outdoor.entrySet()) {
                    if (entry.getValue().equals(times.get(i))) {
                        rows.add(entry.getKey());
                    }
                }
            }

            androidx.gridlayout.widget.GridLayout grid2 = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.gridLayout02);
            androidx.gridlayout.widget.GridLayout grid = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.gridLayout01);

            grid.setVisibility(View.VISIBLE);
            grid2.setVisibility(View.GONE);

            ImageView iv1 = (ImageView) findViewById(R.id.toggle);
            iv1.setImageResource(R.drawable.outdoor_long);

            for (int i = 0; i < rows.size(); i++) {
                int row = rows.get(i);
                TextView tv = findTextView(row, j);
                String tv_str = (String) tv.getText();
                if (tv_str.equals("")  == false) {
                    tv.setBackgroundColor(Color.parseColor("#8A00C2"));
                    tv.getBackground().setAlpha(255);
                    String idx = String.valueOf(row) + "," + String.valueOf(j);
                    selected_cells_idx.add(idx);
                }
            }

        }

        if (resWeekThis == false) {
            ImageView iv = (ImageView) findViewById(R.id.week_toggle);
            iv.setImageResource(R.drawable.next_week);
        }

    }

    public void populateGrids() {
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
                tv.getBackground().setAlpha(125);
                tv.setOnClickListener(this::onClickTV_indoor);
                tv.setClickable(true);
                tv.setFocusable(true);


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
                        days_indoor.put(1, "M");
                    }
                    if (j == 2) {
                        tv.setText("T");
                        days_indoor.put(2, "T");
                    }
                    if (j == 3) {
                        tv.setText("W");
                        days_indoor.put(3, "W");
                    }
                    if (j == 4) {
                        tv.setText("Th");
                        days_indoor.put(4, "Th");
                    }
                    if (j == 5) {
                        tv.setText("F");
                        days_indoor.put(5, "F");
                    }
                }
                else if (j == 0) {
                    if (i == 1) {
                        tv.setText("08:00-08:30");
                        times_indoor.put(1, "08:00:00");
                    }
                    else if (i == 2) {
                        tv.setText("08:30-09:00");
                        times_indoor.put(2, "08:30:00");
                    }
                    else if (i == 3) {
                        tv.setText("09:00-09:30");
                        times_indoor.put(3, "09:00:00");
                    }
                    else if (i == 4) {
                        tv.setText("09:30-10:00");
                        times_indoor.put(4, "09:30:00");
                    }
                    else if (i == 5) {
                        tv.setText("10:00-10:30");
                        times_indoor.put(5, "10:00:00");
                    }
                    else if (i == 6) {
                        tv.setText("10:30-11:00");
                        times_indoor.put(6, "10:30:00");
                    }
                    else if (i == 7) {
                        tv.setText("11:00-11:30");
                        times_indoor.put(7, "11:00:00");
                    }
                    else if (i == 8) {
                        tv.setText("11:30-12:00");
                        times_indoor.put(8, "11:30:00");
                    }
                    else if (i == 9) {
                        tv.setText("12:00-12:30");
                        times_indoor.put(9, "12:00:00");
                    }
                    else if (i == 10) {
                        tv.setText("12:30-13:00");
                        times_indoor.put(10, "12:30:00");
                    }
                    else if (i == 11) {
                        tv.setText("13:00-13:30");
                        times_indoor.put(11, "13:00:00");
                    }
                    else if (i == 12) {
                        tv.setText("13:30-14:00");
                        times_indoor.put(12, "13:30:00");
                    }
                    else if (i == 13) {
                        tv.setText("14:00-14:30");
                        times_indoor.put(13, "14:00:00");
                    }
                    else if (i == 14) {
                        tv.setText("14:30-15:00");
                        times_indoor.put(14, "14:30:00");
                    }
                    else if (i == 15) {
                        tv.setText("15:00-15:30");
                        times_indoor.put(15, "15:00:00");
                    }
                    else if (i == 16) {
                        tv.setText("15:30-16:00");
                        times_indoor.put(16, "15:30:00");
                    }
                    else if (i == 17) {
                        tv.setText("16:00-16:30");
                        times_indoor.put(17, "16:00:00");
                    }
                    else if (i == 18) {
                        tv.setText("16:30-17:00");
                        times_indoor.put(18, "16:30:00");
                    }
                    else if (i == 19) {
                        tv.setText("17:00-17:30");
                        times_indoor.put(19, "17:00:00");
                    }
                    else if (i == 20) {
                        tv.setText("17:30-18:00");
                        times_indoor.put(20, "17:30:00");
                    }
                    else if (i == 21) {
                        tv.setText("18:00-18:30");
                        times_indoor.put(21, "18:00:00");
                    }
                    else if (i == 22) {
                        tv.setText("18:30-19:00");
                        times_indoor.put(22, "18:30:00");
                    }
                    else if (i == 23) {
                        tv.setText("19:00-19:30");
                        times_indoor.put(23, "19:00:00");
                    }
                    else if (i == 24) {
                        tv.setText("19:30-20:00");
                        times_indoor.put(24, "19:30:00");
                    }
                    tv.setBackgroundColor(Color.WHITE);
                    tv.setTextColor(Color.parseColor("#737373"));
                    tv.setWidth( dpToPixel(100) );
                    tv.setTextSize( 15 );
                }
//                else if (i%3 == 0 || j%3 == 0) {
//                    tv.setBackgroundColor(Color.LTGRAY);
//                    tv.setText("0");
//                    tv.getBackground().setAlpha(255);
//                }

                androidx.gridlayout.widget.GridLayout.LayoutParams lp = new androidx.gridlayout.widget.GridLayout.LayoutParams();
                lp.setMargins(dpToPixel(1), dpToPixel(1), dpToPixel(1), dpToPixel(1));
                lp.rowSpec = androidx.gridlayout.widget.GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid2.addView(tv, lp);

                cell_tvs_indoor.add(tv);
            }

        }


        putAvailability();

        if (newRes || resWeekThis) {
            putPastTimes();
        }

    }

    public void putPastTimes() {
        // IMPLEMENT FOR TIMES AS WELL AS DAYS
        Date date = new Date();
        Calendar cal = Calendar.getInstance();

        // CHANGE BACK TO THISS!!!!
        cal.setTime(date);


//        cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        //sunday or saturday
        if (dayOfWeek == 1 || dayOfWeek == 7) {
            for (int i = 1; i < 25; i++) {
                for (int j = 1; j < 6; j++) {
                    TextView tv1 = findTextView(i, j);
                    TextView tv2 = findTextViewIndoor(i, j);

                    tv1.setBackgroundColor(Color.LTGRAY);
                    tv2.setBackgroundColor(Color.LTGRAY);

                    tv1.setText("");
                    tv2.setText("");

                }
            }

        }
        // monday, tuesday, wednesday, thursday, friday
        else {
            for (int i = 1; i < 25; i++) {
                for (int j = 1; j < 6; j++) {
                    if (j+1 < dayOfWeek) {
                        TextView tv1 = findTextView(i, j);
                        TextView tv2 = findTextViewIndoor(i, j);

                        tv1.setBackgroundColor(Color.LTGRAY);
                        tv2.setBackgroundColor(Color.LTGRAY);

                        tv1.setText("");
                        tv2.setText("");
                    }
                }
            }
            int mins = cal.get(Calendar.MINUTE);
//            int hours = cal.get(Calendar.HOUR);

            if (mins < 30) {
                mins = 0;
            }
            else {
                mins = 30;
            }

            cal.set(Calendar.MINUTE, mins);

            date = cal.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:00");
            sdf.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            String time = sdf.format(date);
            System.out.println("CURRENT TIME BLOCK IS " + time);

            // loop through the time blocks INCLUDING the curr time block & set gray
            int row = 0;
            for (Map.Entry<Integer, String> entry : times_indoor.entrySet()) {
                if (entry.getValue().equals(time)) {
                    row = entry.getKey();
                    break;
                }
            }
            for (int i = 1; i <= row; i++) {
                TextView tv1 = findTextView(i, dayOfWeek-1);
                TextView tv2 = findTextViewIndoor(i, dayOfWeek-1);

                tv1.setBackgroundColor(Color.LTGRAY);
                tv2.setBackgroundColor(Color.LTGRAY);

                tv1.setText("");
                tv2.setText("");
            }
        }
    }

    public void putAvailability() {
        ImageView iv = (ImageView) findViewById(R.id.toggle);
        iv.setImageResource(R.drawable.outdoor_long);
        androidx.gridlayout.widget.GridLayout grid2 = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.gridLayout02);
        androidx.gridlayout.widget.GridLayout grid = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.gridLayout01);


        grid.setVisibility(View.VISIBLE);
        grid2.setVisibility(View.GONE);

        outdoorSelected = true;

//        if (!newRes) {
//            if (thisWeek = false) {
//                ImageView imgView = (ImageView) findViewById(R.id.week_toggle);
//                imgView.setImageResource(R.drawable.next_week);
//            }
//            if (prevRes.get("indoor").equals("true")) {
//                outdoorSelected = false;
//                grid.setVisibility(View.GONE);
//                grid2.setVisibility(View.VISIBLE);
//            }
//        }



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
                tv.setBackgroundColor(Color.parseColor("#8A00C2"));
                tv.getBackground().setAlpha(125);

                if (tooEarly || tooLate) {
                    tv.setBackgroundColor(Color.LTGRAY);
                    tv.getBackground().setAlpha(255);
                    tv.setText("");
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

        tooEarly = true;
        tooLate = false;
        for (int i = 1; i < 25; i++) {
            for (Map.Entry<Integer, String> entry : times_indoor.entrySet()) {
                if (entry.getKey().equals(i) && entry.getValue().equals(building.openTime)) {
                    tooEarly = false;
                }
                else if ((entry.getKey().equals(i) && entry.getValue().equals(building.closeTime))) {
                    tooLate = true;
                }
            }

            for (int j = 1; j < 6; j++) {
                TextView tv = findTextViewIndoor(i, j);
                tv.setBackgroundColor(Color.parseColor("#8A00C2"));
                tv.getBackground().setAlpha(125);

                if (tooEarly || tooLate) {
                    tv.setBackgroundColor(Color.LTGRAY);
                    tv.getBackground().setAlpha(255);
                    tv.setText("");
                    continue;
                }

                String time = times_indoor.get(i);
                String day = days_indoor.get(j);
                String key = day + " " + time;

                System.out.println(key);


                double temp = (double) avails_indoor.get(key);

                int seats = (int)Math.round(temp);

                tv.setText(String.valueOf(seats));
            }
        }

    }

    public void clearErr() {
        TextView err = (TextView) findViewById(R.id.errorMsg);
        err.setVisibility(View.INVISIBLE);
    }

    public void onClickTV_indoor(View view) {
        clearErr();

        TextView tv = (TextView) view;

        int n = findIndexofCellTextViewIndoor(tv);
        int row = n/COLUMN_COUNT;
        int col = n%COLUMN_COUNT;

        String str = tv.getText().toString();

        if (row == 0 || col == 0) {
            return;
        }

        if (str.equals("")) {
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
        clearErr();
        TextView tv = (TextView) view;


        int n = findIndexOfCellTextView(tv);
        int row = n/COLUMN_COUNT;
        int col = n%COLUMN_COUNT;

        String str = tv.getText().toString();

        if (row == 0 || col == 0) {
            return;
        }

        if (str.equals("")) {
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
        clearErr();

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

    public void makeReservation(View view) throws IOException {
        TextView error = (TextView) findViewById(R.id.errorMsg);
        if (selected_cells_idx.size() == 0 && selected_cells_idx_indoor.size() == 0) {
            error.setText("Please select at least one time slot.");
            error.setVisibility(View.VISIBLE);
        }
        // else if user already has a reservation, set error message accordingly
        else {
            ArrayList<String> timeBlocks = new ArrayList<>();

            boolean indoor = true;

            String res_day = "";

            if (selected_cells_idx.size() != 0) {
                indoor = false;

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

                    String time = times_outdoor.get(i);
                    String day = days_outdoor.get(j);
                    String key = day + " " + time;
                    res_day = day;

                    timeBlocks.add(key);
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

                    String time = times_indoor.get(i);
                    String day = days_indoor.get(j);
                    String key = day + " " + time;
                    res_day = day;
                    timeBlocks.add(key);
                }



            }

            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 9)
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                //your codes here

            }

            URL url = new URL("http://34.125.226.6:8080/makeReservation");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            if (!newRes) {
                url = new URL("http://34.125.226.6:8080/editReservation");
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("PUT");
            }

            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "application/json");

            String result = "[";
            for (int i = 0; i < timeBlocks.size(); i++) {
                result += "\"" + timeBlocks.get(i) + "\"";
                if (i != (timeBlocks.size() - 1)) {
                    result += ",";
                }
            }
            result += "]";

            Calendar cal = Calendar.getInstance();



            if (res_day.equals("M")) {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            }
            if (res_day.equals("T")) {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
            }
            if (res_day.equals("W")) {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
            }
            if (res_day.equals("Th")) {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
            }
            if (res_day.equals("F")) {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
            }

            if (!thisWeek) {
                cal.add(Calendar.DATE, 7);
            }

            Date date = cal.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            String json_date = sdf.format(date);


            String jsonInputString = "{\"idNumber\": \"" + uscId +"\", \"buildingName\": \"" + building_name + "\", \"timeBlocks\":" + result + ", \"indoor\": \"" + indoor +"\", \"isCanceled\": \"" + false + "\", \"date\": \"" + json_date + "\"}";
            try(OutputStream os = con.getOutputStream()) {
                System.out.println("HEREEEEEEEE");
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());

                TextView err = (TextView) findViewById(R.id.errorMsg);
                if ((response.toString()).equals("false")) {
                    err.setText("Please cancel your current reservation to make a new one.");
                    err.setVisibility(View.VISIBLE);
                }

            }
            catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public void toggle_week(View view) throws IOException {
        clearErr();
        if (thisWeek) {
//            cell_tvs = new ArrayList<>();
//            cell_tvs_indoor = new ArrayList<>();
            ImageView iv = (ImageView) findViewById(R.id.week_toggle);
            iv.setImageResource(R.drawable.next_week);
            thisWeek = false;



            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy gfgPolicy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(gfgPolicy);
            }

            selected_cells_idx = new ArrayList<>();
            selected_cells_idx_indoor = new ArrayList<>();

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            cal.add(Calendar.DATE, 7);
            Date monday = cal.getTime();

            System.out.println(monday);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            String res_week = sdf.format(monday);

            System.out.println(res_week);

            boolean isIndoor = true;
            String url_string = "http://34.125.226.6:8080/getBuildingAvailability?buildingName=" + building_name + "&isIndoor=" + isIndoor + "&weekDateStr=" + res_week;
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

            System.out.println("STATUS CODE FOR NEW THINGY");
            System.out.println(status);


            Gson gson = new Gson();
//
            avails_indoor = new Gson().fromJson(content.toString(), HashMap.class);

            System.out.println(avails_indoor);

            isIndoor = false;
            url_string = "http://34.125.226.6:8080/getBuildingAvailability?buildingName=" + building_name + "&isIndoor=" + isIndoor + "&weekDateStr=" + res_week;
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

            putAvailability();


        }
        else {
            ImageView iv = (ImageView) findViewById(R.id.week_toggle);
            iv.setImageResource(R.drawable.this_week);

            thisWeek = true;


//            cell_tvs = new ArrayList<>();
//            cell_tvs_indoor = new ArrayList<>();

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy gfgPolicy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(gfgPolicy);
            }

            selected_cells_idx = new ArrayList<>();
            selected_cells_idx_indoor = new ArrayList<>();

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
//            cal.add(Calendar.DATE, 7);
            Date monday = cal.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            String res_week = sdf.format(monday);

            System.out.println(res_week);

            boolean isIndoor = true;
            String url_string = "http://34.125.226.6:8080/getBuildingAvailability?buildingName=" + building_name + "&isIndoor=" + isIndoor + "&weekDateStr=" + res_week;
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

            System.out.println("STATUS CODE FOR NEW THINGY");
            System.out.println(status);


            Gson gson = new Gson();
//
            avails_indoor = new Gson().fromJson(content.toString(), HashMap.class);

            System.out.println(avails_indoor);

            isIndoor = false;
            url_string = "http://34.125.226.6:8080/getBuildingAvailability?buildingName=" + building_name + "&isIndoor=" + isIndoor + "&weekDateStr=" + res_week;
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
            putAvailability();
            putPastTimes();

        }
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
        String url_string = "http://34.125.226.6:8080/getBuilding?documentId=" + building_name;
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
        System.out.println("GETTING BUILDING AVAILABILITY");
        //getBuildingAvailability?buildingName=Leavey Library&isIndoor=true&weekDateStr=2023-10-30
//        boolean isIndoor = true;
//        if (outdoorSelected) {
//            isIndoor = false;
//        }


        if (!newRes) {
            loadRes();
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);



        Date monday = cal.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));

        String res_week = sdf.format(monday);

        if (!newRes) {
            String d_string = (String) prevRes.get("date");
            Date date = sdf.parse(d_string);
            System.out.println("RES DATE IS:" + date);

            cal.add(Calendar.DATE, 7);
            Date nextMon = cal.getTime();
            String nextWeekMonday = sdf.format(nextMon);
            nextMon = sdf.parse(nextWeekMonday);

            System.out.println("COMPARE RES DATE TO NEXT MONDAY:" + nextMon);

            if (date.compareTo(nextMon) < 0) {
                res_week = sdf.format(nextMon);
                resWeekThis = false;
                System.out.println("RES NOT IN THIS WEEK");
            }

        }



        System.out.println(res_week);

        boolean isIndoor = true;
        String url_string = "http://34.125.226.6:8080/getBuildingAvailability?buildingName=" + building_name + "&isIndoor=" + isIndoor + "&weekDateStr=" + res_week;
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
        url_string = "http://34.125.226.6:8080/getBuildingAvailability?buildingName=" + building_name + "&isIndoor=" + isIndoor + "&weekDateStr=" + res_week;
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