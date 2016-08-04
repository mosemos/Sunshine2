package com.mosemos.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;
    private boolean isTablet = false;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public void setIsTablet(boolean isTablet){
        this.isTablet = isTablet;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public int getItemViewType(int position){
        if(position == 0){
            return VIEW_TYPE_TODAY;
        }
        return VIEW_TYPE_FUTURE_DAY;
    }

    /*
        Return the inflated view as needed.
    */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = -1;

        if((getItemViewType(cursor.getPosition()) == VIEW_TYPE_TODAY) && !isTablet){
            layoutId = R.layout.list_item_forecast_today;
        }
        else{
            layoutId = R.layout.list_item_forecast;
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        int conditionId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        int conditionImageId;

        if((getItemViewType(cursor.getPosition()) == VIEW_TYPE_TODAY) && !isTablet){
            conditionImageId = Utility.getArtResourceForWeatherCondition(conditionId);
        }else{
            conditionImageId = Utility.getIconResourceForWeatherCondition(conditionId);
        }

        //weather icon or image
        viewHolder.iconView.setImageResource(conditionImageId);

        // date
        viewHolder.dateView.setText(Utility.formatDate(cursor.getString(ForecastFragment.COL_WEATHER_DATE)));

        // forecast description
        viewHolder.descriptionView.setText(cursor.getString(ForecastFragment.COL_WEATHER_DESC));

        boolean isMetric = Utility.isMetric(context);

        // high temp
        String highTemp = Utility.formatTemperature(context, cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP), isMetric);
        viewHolder.highTempView.setText(highTemp);

        // low temp
        String lowTemp = Utility.formatTemperature(context, cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP), isMetric);
        viewHolder.lowTempView.setText(lowTemp);
    }


    /**
     * A view holder for list items
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view){
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }
}