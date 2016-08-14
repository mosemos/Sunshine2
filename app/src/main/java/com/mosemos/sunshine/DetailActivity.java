package com.mosemos.sunshine;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private final static String LOG_TAG = "DetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weather_detail_container, new DetailActivityFragment())
                    .commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




    }

}
