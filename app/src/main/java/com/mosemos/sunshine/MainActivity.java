package com.mosemos.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback {
    private String currentLocation;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean isTwoPaneLayout = false;

    @Override
    public void onItemSelected(Uri dateUri) {
        if(isTwoPaneLayout){
            DetailActivityFragment detailActivityFragment = new DetailActivityFragment();
            Bundle bundle = new Bundle();
            bundle.putString("dateUri", dateUri.toString());
            detailActivityFragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.weather_detail_container, detailActivityFragment, DETAILFRAGMENT_TAG);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        }else{
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(dateUri);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // execute a FetchWeatherTask if the app is run for the first time
        Utility.appFirstLaunchTask(this);

        currentLocation = Utility.getPreferredLocation(this);

        if(findViewById(R.id.weather_detail_container) != null){
            // in tablet mode
            isTwoPaneLayout = true;

//            if(savedInstanceState == null){
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.weather_detail_container, new DetailActivityFragment(), DETAILFRAGMENT_TAG)
//                        .commit();
//            }
        }
    }





    protected void onResume() {
        super.onResume();

        String prefsLocation = Utility.getPreferredLocation(this);

        if(currentLocation != null && !currentLocation.equals(prefsLocation)){
            ForecastFragment forecastFragment = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);

            if(forecastFragment != null){
                forecastFragment.onLocationChanged();
            }

            DetailActivityFragment detailActivityFragment = (DetailActivityFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);

            if(detailActivityFragment != null){
                detailActivityFragment.onLocationChanged(prefsLocation);
            }

            currentLocation = prefsLocation;
        }

    }
}
