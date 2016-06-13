package com.mosemos.sunshine;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class DetailActivity extends AppCompatActivity {

    private final String logtag = "DetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.v(logtag, "onCreate");
    }

    protected void onStart(){
        super.onStart();
        Log.v(logtag, "onStart");
    }

    protected void onResume(){
        super.onResume();
        Log.v(logtag, "onResume");
    }

    protected void onPause(){
        super.onPause();
        Log.v(logtag, "onPause");
    }

    protected void onStop(){
        super.onStop();
        Log.v(logtag, "onStop");
    }

    protected void onDestroy(){
        super.onDestroy();
        Log.v(logtag, "onDestroy");
    }

    protected void onRestart(){
        super.onRestart();
        Log.v(logtag, "onRestart");
    }


}
