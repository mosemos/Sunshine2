package com.mosemos.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {
    private ArrayAdapter<String> forecastListAdapter = null;
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


        List<String> forecasts = new ArrayList<String>();

        forecastListAdapter = new ArrayAdapter<String>(getActivity(),         // context: the parent activity
                R.layout.list_item_forecast,                                  // layout of the items
                R.id.list_item_forecast_textview,                             // id of the list item
                forecasts);                                                   // data



        ListView listView = (ListView) view.findViewById(R.id.listview_forecast);
        listView.setAdapter(forecastListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               String weatherInfo = (String) parent.getItemAtPosition(position);

                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                detailIntent.putExtra(Intent.EXTRA_TEXT, weatherInfo);  // add the weatherInfo to the intent
                startActivity(detailIntent);


            }
        });

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
        new FetchWeatherTask(getActivity(), forecastListAdapter).execute(cityId);
    }




}