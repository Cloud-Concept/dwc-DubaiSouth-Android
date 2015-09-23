package fragment.License.Renew;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import cloudconcept.dwc.R;
import fragmentActivity.LicenseActivity;

/**
 * Created by M_Ghareeb on 9/15/2015.
 */
public class SecondFragment extends Fragment {
    EditText issueDate,expireDate,commercialName,commercialNameArabic,licenseNumber;
    LicenseActivity activity;
    public static Fragment newInstance() {
        SecondFragment temp = new SecondFragment();
        return temp;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_second_renew_license, container, false);
        activity = (fragmentActivity.LicenseActivity) getActivity();
        InitializeViews(view);
        return view;
    }

    private void InitializeViews(View view) {
        issueDate=(EditText)view.findViewById(R.id.issueDate);
        expireDate=(EditText)view.findViewById(R.id.expireDate);
        commercialName=(EditText)view.findViewById(R.id.commercialName);
        commercialNameArabic=(EditText)view.findViewById(R.id.commercialNameArabic);
        licenseNumber=(EditText)view.findViewById(R.id.licenseNumber);

        issueDate.setText(activity.getUser().get_contact().get_account().get_currentLicenseNumber().getLicense_Issue_Date());
        expireDate.setText(activity.getUser().get_contact().get_account().get_currentLicenseNumber().getLicense_Expiry_Date());
        commercialName.setText(activity.getUser().get_contact().get_account().get_currentLicenseNumber().getCommercial_Name());
        commercialNameArabic.setText(activity.getUser().get_contact().get_account().get_currentLicenseNumber().getCommercial_Name_Arabic());
        licenseNumber.setText(activity.getUser().get_contact().get_account().get_currentLicenseNumber().getLicense_Number_Value());
    }

}
