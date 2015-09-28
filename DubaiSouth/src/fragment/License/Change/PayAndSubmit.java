package fragment.License.Change;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

import cloudconcept.dwc.R;
import fragment.companyInfo.LicenseInfoFragment;
import model.LicenseActivity;
import model.OriginalBusinessActivity;
import utilities.Utilities;

/**
 * Created by M_Ghareeb on 9/14/2015.
 */
public class PayAndSubmit extends Fragment {
    TextView newActivity;
    fragmentActivity.LicenseActivity activity;
    LinearLayout currentLicenseActivities, newLicenseActivities;

    public static Fragment newInstance() {
        PayAndSubmit temp = new PayAndSubmit();
        return temp;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_initial_licence_cancel_renew, container, false);
        activity = (fragmentActivity.LicenseActivity) getActivity();
        InitializeViews(view);
        return view;
    }
    private void InitializeViews(View view) {
        newActivity = (TextView) view.findViewById(R.id.newActivity);
        newActivity.setVisibility(View.GONE);
        currentLicenseActivities = (LinearLayout) view.findViewById(R.id.currentLicenseActivities);
        newLicenseActivities = (LinearLayout) view.findViewById(R.id.newLicenseActivities);


        for (int i = 0; i < LicenseInfoFragment._licenses.size(); i++) {

            View viewHeader = LayoutInflater.from(getActivity()).inflate(R.layout.licence_activity, null);
            TextView tvLabelItem = (TextView) viewHeader.findViewById(R.id.tvLabelItem);
            TextView tvValue = (TextView) viewHeader.findViewById(R.id.tvValueItem);
            tvLabelItem.setText(LicenseInfoFragment._licenses.get(i).get_originalBusinessActivity().getName());
            tvValue.setText(LicenseInfoFragment._licenses.get(i).get_originalBusinessActivity().getBusinessActivityName());

            currentLicenseActivities.addView(viewHeader);

            viewHeader = LayoutInflater.from(getActivity()).inflate(R.layout.generic_view_line_item, null);
            currentLicenseActivities.addView(viewHeader);

        }

        for (int i = 0; i < activity.get_licenses().size(); i++) {

            View viewHeader = LayoutInflater.from(getActivity()).inflate(R.layout.licence_activity, null);
            TextView tvLabelItem = (TextView) viewHeader.findViewById(R.id.tvLabelItem);
            TextView tvValue = (TextView) viewHeader.findViewById(R.id.tvValueItem);
            tvLabelItem.setText(activity.get_licenses().get(i).getName());
            tvValue.setText(activity.get_licenses().get(i).getBusinessActivityName());

            newLicenseActivities.addView(viewHeader);

            viewHeader = LayoutInflater.from(getActivity()).inflate(R.layout.generic_view_line_item, null);
            newLicenseActivities.addView(viewHeader);

        }

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.wizard_form_field_pay_header, null, false);
        TextView tvHeader = (TextView) v.findViewById(R.id.pay_header);
        tvHeader.setText("Additions");
        newLicenseActivities.addView(v);

        v = LayoutInflater.from(getActivity()).inflate(R.layout.wizards_form_field_details, null, false);
        TextView tvLabel = (TextView) v.findViewById(R.id.pay_title);
        TextView tvValue = (TextView) v.findViewById(R.id.pay_text);
        tvLabel.setText("Total Amount" + "\t:");
        tvValue.setText(activity.getTotal()+"AED");
        newLicenseActivities.addView(v);

    }
}
