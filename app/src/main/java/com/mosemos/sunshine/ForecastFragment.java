package com.mosemos.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.mosemos.sunshine.data.WeatherContract;
/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ForecastAdapter forecastAdapter = null;
    private static final int FORECAST_LOADER_ID = 1;
    private String longitude;
    private String latitude;

    public ForecastFragment() {
    }

    public void onStart(){
        super.onStart();
        updateWeather();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // declaring that it has optionsMenu (only the refresh button)
        this.setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);

    }

    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        if(id == R.id.action_show_map){
            Uri uri = Uri.parse("geo:" + latitude + "," + longitude);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);

            // check if there is an app on the device that can handle ACTION_VIEW
            if(intent.resolveActivity(getActivity().getPackageManager()) != null){
                startActivity(intent);
            }
            else{
                Toast.makeText(getActivity(), "No available application can open your location on a map", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_main, container, false);


        String locationSetting = Utility.getPreferredLocation(getActivity());

        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(locationSetting);
        Cursor cursor = getActivity().getContentResolver().query(weatherForLocationUri,
                null, null, null, sortOrder);

        forecastAdapter = new ForecastAdapter(getActivity(), cursor, 0);

        ListView listView = (ListView) view.findViewById(R.id.listview_forecast);
        listView.setAdapter(forecastAdapter);



        return view;
    }


    // a method that is called whenever the weather data is to be updated
    private void updateWeather () {
        // get the cityId from the SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String key = getResources().getString(R.string.pref_location_key);
        String defaultValue = getResources().getString(R.string.pref_location_default);
        String cityId = prefs.getString(key, defaultValue);
        // start a fetchWeatherTask using the cityId specified in the SharedPreferences
        new FetchWeatherTask(getActivity()).execute(cityId);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(FORECAST_LOADER_ID, savedInstanceState, this);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){
        String locationSetting = Utility.getPreferredLocation(getActivity());
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri uri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(locationSetting);

        return new CursorLoader(getActivity(), uri, null, null, null, sortOrder);
    }

    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor){
        forecastAdapter.swapCursor(cursor);
    }

    public void onLoaderReset(Loader<Cursor> cursorLoader){
        forecastAdapter.swapCursor(null);
    }


}