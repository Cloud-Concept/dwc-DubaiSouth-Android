package adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import cloudconcept.dwc.R;

/**
 * Created by M_Ghareeb on 10/11/2015.
 */
public class SimpleSpinnerStringAdapter extends ArrayAdapter<String> {
    String[] objects;
    Context context;

    public SimpleSpinnerStringAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
        super(context, resource, textViewResourceId, objects);
        this.objects=objects;
        this.context=context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.spinner_item_wizard, parent,false);
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
