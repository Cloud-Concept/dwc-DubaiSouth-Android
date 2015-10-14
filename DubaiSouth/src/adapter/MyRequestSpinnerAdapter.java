package adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cloudconcept.dwc.R;

/**
 * Created by Abanoub Wagdy on 10/13/2015.
 */
public class MyRequestSpinnerAdapter extends ArrayAdapter<String> {

    String[] objects;
    Context context;

    public MyRequestSpinnerAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
        super(context, resource, textViewResourceId, objects);
        this.objects=objects;
        this.context=context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.myrequest_spinner_item, parent,false);
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
            convertView = mInflater.inflate(R.layout.myrequest_dropdown_item, parent,false);
        }

        TextView tv = (TextView)convertView.findViewById(R.id.spinnertext);
        tv.setText(objects[position]);

        return convertView;

    }
}
