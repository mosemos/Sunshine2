package com.mosemos.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mosemos.sunshine.data.WeatherContract;
import com.mosemos.sunshine.data.WeatherContract.WeatherEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int DETAIL_LOADER_ID = 1;
    private Uri forecastUri = null;

    private ShareActionProvider mShareActionProvider;
    private String mForecast;

    private static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_WEATHER_ID,

            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };


    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_HUMIDITY = 5;
    public static final int COL_WEATHER_PRESSURE = 6;
    public static final int COL_WEATHER_WIND_SPEED = 7;
    public static final int COL_WEATHER_DEGREES = 8;
    public static final int COL_WEATHER_CONDITION_ID = 9;

    public DetailActivityFragment() {
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);

        // locate the MenuItem with ShareActionProvider to set its intent
        MenuItem item = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if(mForecast != null){
            mShareActionProvider.setShareIntent(createShareIntent());
        }

    }

    public void onLocationChanged(String location){
        String date = WeatherEntry.getDateFromUri(forecastUri);
        forecastUri = WeatherEntry.buildWeatherLocationWithDate(location, date);
        getLoaderManager().restartLoader(DETAIL_LOADER_ID, null, this);
    }

    private Intent createShareIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        // this flag is very important! to make the system return to our app when the sharing activity is done
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);

        // get the weather forecast info
        String weatherText = mForecast + " #SunshineApp";
        shareIntent.putExtra(Intent.EXTRA_TEXT, weatherText);

        return shareIntent;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        if(intent != null && intent.getData() != null){
            forecastUri = intent.getData();
        }
        else if(getArguments() != null) {
            forecastUri = Uri.parse(getArguments().getString("dateUri"));
        }

        // declaring that it has optionsMenu
        this.setHasOptionsMenu(true);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // if the settings button is selected, the SettingsActivity is launched
        if(id == R.id.detail_action_settings){
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
    }


    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

       if(forecastUri != null){
           return new CursorLoader(
                   getActivity(),
                   forecastUri,
                   FORECAST_COLUMNS,
                   null,
                   null,
                   null);
       }
       return null;
    }
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(!data.moveToFirst()){
            return;
        }

        int conditionId = data.getInt(COL_WEATHER_CONDITION_ID);
        int conditionImageId = Utility.getArtResourceForWeatherCondition(conditionId);
        ImageView weatherImage = (ImageView) getView().findViewById(R.id.detail_item_icon);

        weatherImage.setImageResource(conditionImageId);

        String date = Utility.formatDate(data.getString(COL_WEATHER_DATE));
        TextView dateView = (TextView) getView().findViewById(R.id.detail_item_date_textview);
        dateView.setText(date);

        String weatherDescription = data.getString(COL_WEATHER_DESC);
        TextView weatherView = (TextView) getView().findViewById(R.id.detail_item_forecast_textview);
        weatherView.setText(weatherDescription);

        boolean isMetric = Utility.isMetric(getActivity());
        String maxTemp = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        TextView maxTempView = (TextView) getView().findViewById(R.id.detail_item_high_textview);
        maxTempView.setText(maxTemp);

        String minTemp = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
        TextView lowTempView = (TextView) getView().findViewById(R.id.detail_item_low_textview);
        lowTempView.setText(minTemp);

        float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
        String humidityText = getString(R.string.format_humidity, humidity);
        TextView humidityView = (TextView) getView().findViewById(R.id.detail_item_humidity_textview);
        humidityView.setText(humidityText);

        float pressure = data.getFloat(COL_WEATHER_PRESSURE);
        String pressureText = getString(R.string.format_pressure, pressure);
        TextView pressureView = (TextView) getView().findViewById(R.id.detail_item_pressure_textview);
        pressureView.setText(pressureText);

        float windSpeed = data.getFloat(COL_WEATHER_WIND_SPEED);
        float windDegrees = data.getFloat(COL_WEATHER_DEGREES);
        String windText = Utility.formatWind(getActivity(), windSpeed, windDegrees);
        TextView windView = (TextView) getView().findViewById(R.id.detail_item_wind_textview);
        windView.setText(windText);


        mForecast = String.format("%s - %s - %s/%s", date, weatherDescription, maxTemp, minTemp);

        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
