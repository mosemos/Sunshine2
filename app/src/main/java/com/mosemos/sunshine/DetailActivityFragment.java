package com.mosemos.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private ShareActionProvider shareActionProvider;

    public DetailActivityFragment() {
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);

        // locate the MenuItem with ShareActionProvider to set its intent
        MenuItem item = menu.findItem(R.id.action_share);

        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        setShareIntent(createShareIntent());
    }

    private void setShareIntent(Intent shareIntent){
        if(shareActionProvider != null){
            shareActionProvider.setShareIntent(shareIntent);
        }
    }

    private Intent createShareIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        // this flag is very important! to make the system return to our app when the sharing activity is done
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);

        // get the weather forecast info
        String weatherText = getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);
        weatherText += " #SunshineApp";
        shareIntent.putExtra(Intent.EXTRA_TEXT, weatherText);

        return shareIntent;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();

        String weatherDetails = intent.getStringExtra(Intent.EXTRA_TEXT);

        if(weatherDetails != null){

            TextView text = (TextView) view.findViewById(R.id.weatherDetails);
            text.setText(weatherDetails);
        }
        else{
            Log.v("DetailActivityFragment", "null weatherDetails from intent");
        }

        return view;
    }
}
