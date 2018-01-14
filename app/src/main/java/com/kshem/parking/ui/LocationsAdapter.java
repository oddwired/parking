package com.kshem.parking.ui;

import android.app.Activity;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kshem.parking.R;
import com.kshem.parking.models.LocationModel;

import java.util.ArrayList;

/**
 * Created by kshem on 12/19/17.
 */

public class LocationsAdapter extends BaseAdapter {

    private ArrayList<LocationModel> list;
    private Activity activity;

    public LocationsAdapter(Activity activity, ArrayList<LocationModel> list){
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public LocationModel getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView parking_lot;
        if(convertView == null){
            convertView = activity.getLayoutInflater()
                    .inflate(R.layout.location_layout, parent, false);
        }
        parking_lot = (TextView) convertView.findViewById(R.id.lot);
        parking_lot.setText(getItem(position).getName());
        return convertView;
    }
}
