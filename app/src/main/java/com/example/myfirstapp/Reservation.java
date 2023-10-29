package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class Reservation extends AppCompatActivity {

    boolean outdoorSelected = true;

    private ArrayList<TextView> cell_tvs;

    private ArrayList<String> selected_cells_idx;

    private ArrayList<String> selected_cells_idx_indoor;

    private static final int COLUMN_COUNT = 6;

    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
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
        outdoorSelected = true;

        cell_tvs = new ArrayList<>();
        selected_cells_idx = new ArrayList<>();
        selected_cells_idx_indoor = new ArrayList<>();

        androidx.gridlayout.widget.GridLayout grid = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.gridLayout01);

        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 6; j++) {
                TextView tv = new TextView(this);
                tv.setHeight( dpToPixel(32) );
                tv.setWidth( dpToPixel(55) );
                tv.setTextSize( 16 );//dpToPixel(32) );
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.parseColor("#8A00C2"));
                tv.setOnClickListener(this::onClickTV);

                tv.getBackground().setAlpha(125);
                tv.setText("1");

                if (i == 0 && j == 0) {
                    tv.setText("");
                    tv.setBackgroundColor(Color.WHITE);
                }
                else if (i == 0) {
                    tv.setBackgroundColor(Color.WHITE);
                    tv.setTextColor(Color.BLACK);
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
                    tv.setText("8:00-8:30am");
                    tv.setBackgroundColor(Color.WHITE);
                    tv.setTextColor(Color.BLACK);
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

        for (int i = 0; i < 13; i++) {
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
                tv.getBackground().setAlpha(125);
                tv.setText("1");

                if (i == 0 && j == 0) {
                    tv.setText("");
                    tv.setBackgroundColor(Color.WHITE);
                }
                else if (i == 0) {
                    tv.setBackgroundColor(Color.WHITE);
                    tv.setTextColor(Color.BLACK);
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
                    tv.setText("8:00-8:30am");
                    tv.setBackgroundColor(Color.WHITE);
                    tv.setTextColor(Color.BLACK);
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
            }
        }

    }

    public void onClickTV_indoor(View view) {
        TextView tv = (TextView) view;

        int n = findIndexOfCellTextView(tv);
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

                TextView tv_selected = findTextView(i, j);
                String tv_str = tv_selected.getText().toString();

                int num_seats = Integer.parseInt(tv_str);
                num_seats++;
                String seats = String.valueOf(num_seats);
                tv_selected.setText(seats);
                tv_selected.setBackgroundColor(Color.parseColor("#8A00C2"));
                tv_selected.getBackground().setAlpha(125);

            }
            selected_cells_idx_indoor.clear();
        }
    }

    public void onClickTV(View view) {
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
            selected_cells_idx.clear();
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

    public void toggle(View view) {
        ImageView iv = (ImageView) view;
        if (outdoorSelected) {
            outdoorSelected = false;
            iv.setImageResource(R.drawable.indoor_selected);

            androidx.gridlayout.widget.GridLayout grid = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.gridLayout01);
            androidx.gridlayout.widget.GridLayout grid2 = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.gridLayout02);

            grid.setVisibility(View.GONE);
            grid2.setVisibility(View.VISIBLE);
        }
        else {
            outdoorSelected = true;
            iv.setImageResource(R.drawable.outdoor_selected);

            androidx.gridlayout.widget.GridLayout grid = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.gridLayout01);
            androidx.gridlayout.widget.GridLayout grid2 = (androidx.gridlayout.widget.GridLayout) findViewById(R.id.gridLayout02);

            grid.setVisibility(View.VISIBLE);
            grid2.setVisibility(View.GONE);
        }
    }

}