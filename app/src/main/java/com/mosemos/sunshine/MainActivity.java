package com.mosemos.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.mosemos.sunshine.data.WeatherContract;
import com.mosemos.sunshine.sync.SunshineSyncAdapter;

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
    public void onStartTwoPaneSelect() {
        if(isTwoPaneLayout){
            String location = Utility.getPreferredLocation(this);
            Uri todayUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(location);
            DetailActivityFragment detailActivityFragment = new DetailActivityFragment();
            Bundle bundle = new Bundle();
            bundle.putString("dateUri", todayUri.toString());
            detailActivityFragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.weather_detail_container, detailActivityFragment, DETAILFRAGMENT_TAG);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentLocation = Utility.getPreferredLocation(this);

        if(findViewById(R.id.weather_detail_container) != null){
            // in tablet mode
            isTwoPaneLayout = true;

            ForecastFragment forecastFragment = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);

            forecastFragment.setAdapterTabletMode(true);

        }

        SunshineSyncAdapter.initializeSyncAdapter(this);
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
