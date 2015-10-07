package adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cloudconcept.dwc.R;

/**
 * Created by M_Ghareeb on 10/6/2015.
 */
public class formfieldAdapter extends ArrayAdapter<String> {

        Context context;
        String[] objects;

public formfieldAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.objects=objects;
        }


@Override
public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
        LayoutInflater mInflater = (LayoutInflater)
        context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent,false);
        }

        TextView tv = (TextView)convertView;
        tv.setText(objects[position]);

        return convertView;
        }

@Override
public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
        LayoutInflater mInflater = (LayoutInflater)
        context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.customtextnationality, parent,false);
        }

        TextView tv = (TextView)convertView.findViewById(R.id.spinnertext);
        tv.setText(objects[position]);

        return convertView;

        }
        }
