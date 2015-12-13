package com.ashishjha.weather.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashishjha.weather.R;
import com.ashishjha.weather.model.Weather;
import com.ashishjha.weather.utils.WeatherItemToIndexMapper;

/**
 * Created by ashish.jha on 1/12/15.
 */
public class WeatherListAdapter extends BaseAdapter {

    private Context mContext;

    private Weather mWeather;

    public WeatherListAdapter(Context context) {
        mContext = context;
    }

    public void setWeather(Weather weather) {
        mWeather = weather;
    }

    @Override
    public int getCount() {
        return Weather.VISIBLE_ITEM_COUNT;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.weather_list_item, null);
        }
        TextView descTV = (TextView) convertView.findViewById(R.id.icon_desc);
        ImageView iconView = (ImageView) convertView.findViewById(R.id.icon_indicator);
        int code = -1;
        if (mWeather != null && mWeather.getCondition() != null) {
            code = mWeather.getCondition().getCode();
            iconView.setBackgroundResource(WeatherItemToIndexMapper.getDrawableIcon(position, code));
            descTV.setText(WeatherItemToIndexMapper.getTextDesc(position, mWeather));
        } else {
            iconView.setBackgroundResource(WeatherItemToIndexMapper.getDrawableIcon(position, code));
            descTV.setText(mContext.getResources().getString(R.string.loading));
        }
        return convertView;

    }
}

