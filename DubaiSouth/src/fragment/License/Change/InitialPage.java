package fragment.License.Change;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
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
public class InitialPage extends Fragment {
    TextView newActivity;
    fragmentActivity.LicenseActivity activity;
    LinearLayout currentLicenseActivities, newLicenseActivities;

    public static Fragment newInstance() {
        InitialPage temp = new InitialPage();
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
        newActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity.getType().equals("Change License Activity")) {
                    if (activity.get_licenses().size() < 4) {
                        //add Activity
                        addActivity();

                    } else {
                        Utilities.showLongToast(activity, "You can not add more than 4 activities");
                    }
                } else if (activity.getType().equals("License Renewal")) {
                    //add Activity
                    addActivity();

                }
            }
        });
        currentLicenseActivities = (LinearLayout) view.findViewById(R.id.currentLicenseActivities);
        newLicenseActivities = (LinearLayout) view.findViewById(R.id.newLicenseActivities);

        activity.setRemoved(new HashSet<model.LicenseActivity>());
        activity.set_licenses(new ArrayList<OriginalBusinessActivity>());
        //Handling Current Activities
        for (int i = 0; i < LicenseInfoFragment._licenses.size(); i++) {

            View viewHeader = LayoutInflater.from(getActivity()).inflate(R.layout.licence_activity, null);
            TextView tvLabelItem = (TextView) viewHeader.findViewById(R.id.tvLabelItem);
            TextView tvValue = (TextView) viewHeader.findViewById(R.id.tvValueItem);
            final CheckBox licenseselected = (CheckBox) viewHeader.findViewById(R.id.licenseselected);
            tvLabelItem.setText(LicenseInfoFragment._licenses.get(i).get_originalBusinessActivity().getName());
            tvValue.setText(LicenseInfoFragment._licenses.get(i).get_originalBusinessActivity().getBusinessActivityName());
            licenseselected.setVisibility(View.VISIBLE);
            licenseselected.setTag(i);
            licenseselected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    int lactvity = (int) compoundButton.getTag();
                    if (b) {

                        activity.getRemoved().add(LicenseInfoFragment._licenses.get(lactvity));

                    } else {
                        if (activity.getRemoved().contains(LicenseInfoFragment._licenses.get(lactvity)))
                            activity.getRemoved().remove(LicenseInfoFragment._licenses.get(lactvity));
                    }
                }
            });
            viewHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    licenseselected.setChecked(!licenseselected.isChecked());
                }
            });
            currentLicenseActivities.addView(viewHeader);

            viewHeader = LayoutInflater.from(getActivity()).inflate(R.layout.generic_view_line_item, null);
            currentLicenseActivities.addView(viewHeader);
        }

    }

    private void addActivity() {
        Intent intent = new Intent(getActivity(), ActivitiesActivity.class);
        startActivityForResult(intent, 55);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Handling returned Activities
        if (requestCode == 55) {
            if (resultCode == Activity.RESULT_OK) {
                OriginalBusinessActivity oBA = (OriginalBusinessActivity) data.getExtras().get("data");
                boolean flag = true;
                for (int i = 0; i < activity.get_licenses().size(); i++) {
                    if (oBA.getName().equals(activity.get_licenses().get(i).getName()))
                        flag = false;
                }
                if (flag)
                    activity.get_licenses().add(oBA);
                else
                    Utilities.showLongToast(activity, "This Activity has been selected before");
              reload();
            }
        }
    }

    void reload(){
//        reload List of Activities and setting Actions
        newLicenseActivities.removeAllViews();
        for (int i = 0; i < activity.get_licenses().size(); i++) {

            final View viewHeader = LayoutInflater.from(getActivity()).inflate(R.layout.licence_activity, null);
            TextView tvLabelItem = (TextView) viewHeader.findViewById(R.id.tvLabelItem);
            TextView tvValue = (TextView) viewHeader.findViewById(R.id.tvValueItem);
            tvLabelItem.setText(activity.get_licenses().get(i).getName());
            tvValue.setText(activity.get_licenses().get(i).getBusinessActivityName());
            viewHeader.setTag(activity.get_licenses().get(i));
            final View cancel =  viewHeader.findViewById(R.id.cancel);
            cancel.setVisibility(View.VISIBLE);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OriginalBusinessActivity license = (OriginalBusinessActivity) viewHeader.getTag();
                    for (int j = 0; j < activity.get_licenses().size(); j++) {
                        if (license.getID().equals(activity.get_licenses().get(j).getID())) {
                            activity.get_licenses().remove(j);
                        }
                    }
                    reload();
                }


            });
            newLicenseActivities.addView(viewHeader);

            View viewHeader1 = LayoutInflater.from(getActivity()).inflate(R.layout.generic_view_line_item, null);
            newLicenseActivities.addView(viewHeader1);

        }
    }
}
