package com.mosemos.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
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
        new FetchWeatherTask().execute(cityId);

    }


    // an AsyncTask to fetch the weather from openweathermap
    // takes a string of a cityId as per openweathermap documentation
    // and passes it to the doInBackground() through execute()

    class FetchWeatherTask extends AsyncTask<String, Void, String[]> {


        public FetchWeatherTask() {

        }

        protected String[] doInBackground(String... cityIds) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String forecastJsonStr = null;

            String mode = "json";
            String unit = "metric";
            int days = 7;


            try {

                Uri.Builder builder = new Uri.Builder();

                final String CITY_PARAM = "id";
                final String MODE_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "appid";

                builder.scheme("http")
                        .authority("api.openweathermap.org")
                        .appendPath("data")
                        .appendPath("2.5")
                        .appendPath("forecast")
                        .appendPath("daily")
                        .appendQueryParameter(CITY_PARAM, cityIds[0])
                        .appendQueryParameter(MODE_PARAM, mode)
                        .appendQueryParameter(UNITS_PARAM, unit)
                        .appendQueryParameter(DAYS_PARAM, new Integer(days).toString())
                        .appendQueryParameter(APPID_PARAM, "c2d8e9251e41ecf14923ac7d63326c20");

                URL url = new URL(builder.build().toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    Log.w("mainActivityFragment", "nothing in inputStream");

                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    Log.w("mainActivityFragment", "Empty stream from openweathermap");
                    return null;
                }

                forecastJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e("mainActivityFragment", "Error in opening connection with openweathermap", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("mainActivityFragment", "Error closing stream", e);
                    }
                }
            }

            String[] forecasts = {};
            try {
                forecasts = getWeatherDataFromJson(forecastJsonStr, days);
            } catch (JSONException e) {
                Log.e("FetchWeatherTask", "error in parsing JSON from openweathermap", e);
            }


            return forecasts;
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String tempUnitKey = getResources().getString(R.string.pref_tempUnit_key);
            String tempUnitDefaultValue = getResources().getString(R.string.pref_tempUnit_metric);

            String tempUnitPref = prefs.getString(tempUnitKey,tempUnitDefaultValue);

            // change the units to imperial according to the SharedPreferences
            if(tempUnitPref.equals(getString(R.string.pref_tempUnit_imperial))){
                high = (high * 9.0 / 5.0) + 32;
                low = (low * 9.0 / 5.0) + 32;
            }


            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";
            final String OWM_CITY_OBJ = "city";
            final String OWM_COORD_OBJ = "coord";
            final String OWM_LONG = "lon";
            final String OWM_LATI = "lat";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONObject coordJson = forecastJson.getJSONObject(OWM_CITY_OBJ).getJSONObject(OWM_COORD_OBJ);
            longitude = coordJson.getString(OWM_LONG);
            latitude = coordJson.getString(OWM_LATI);

            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);


            String[] resultStrs = new String[numDays];
            for (int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                //create a Gregorian Calendar, which is in current date
                GregorianCalendar gc = new GregorianCalendar();
                //add i dates to current date of calendar
                gc.add(GregorianCalendar.DATE, i);
                //get that date, format it, and "save" it on variable day
                Date time = gc.getTime();
                SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
                day = shortenedDateFormat.format(time);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            return resultStrs;
        }

        @Override
        protected void onPostExecute(String[] strings) {

            // add the forecasts string array taken from doInBackground to the forecast adapter
            if(strings != null){

                // clear the adapter first
                forecastListAdapter.clear();

                forecastListAdapter.addAll(strings);
            }

        }


    }

}