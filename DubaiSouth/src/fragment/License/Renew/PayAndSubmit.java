package fragment.License.Renew;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import cloudconcept.dwc.R;
import fragmentActivity.CardActivity;
import fragmentActivity.LicenseActivity;

/**
 * Created by M_Ghareeb on 9/16/2015.
 */
public class PayAndSubmit extends Fragment{
    private LicenseActivity activity;
    private LinearLayout nocDetails;

    public static PayAndSubmit newInstance() {
        PayAndSubmit temp=new PayAndSubmit();
        return temp;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = ((LicenseActivity) getActivity());
        View view = inflater.inflate(R.layout.fragment_noc_pay_and_submit, container, false);
        nocDetails = (LinearLayout) view.findViewById(R.id.nocDetails);
        // Inflate the layout for this fragment
        ImageView image = (ImageView) view.findViewById(R.id.imageEmpoyeeNOC);
        TextView labelPerson = (TextView) view.findViewById(R.id.labelPerson);
        labelPerson.setVisibility(View.GONE);
        TextView date = (TextView) view.findViewById(R.id.date);
        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        Date resultdate = new Date(yourmilliseconds);
        date.setText(sdf.format(resultdate));

        TextView total = (TextView) view.findViewById(R.id.total_Amount);
        total.setText(activity.getTotal()==null?"0":activity.getTotal() + " AED");
        TextView person = (TextView) view.findViewById(R.id.personName);

        person.setVisibility(View.GONE);

        TextView ref = (TextView) view.findViewById(R.id.refnumber);

        ref.setText(activity.getCaseNumber());
      image.setImageResource(R.mipmap.renew_license);

        DrawLayout(inflater);
        return view;
    }

    private void DrawLayout(LayoutInflater inflater) {

        //Initiating UI Fields
        View view = inflater.inflate(R.layout.wizard_form_field_pay_header, null, false);
        TextView tvHeader = (TextView) view.findViewById(R.id.pay_header);
        tvHeader.setText("License Details");
        nocDetails.addView(view);
        TextView tvLabel;
        TextView tvValue;
        view = inflater.inflate(R.layout.wizards_form_field_details, null, false);
        tvLabel = (TextView) view.findViewById(R.id.pay_title);
        tvValue = (TextView) view.findViewById(R.id.pay_text);
        tvLabel.setText("Issue Date" + "\t:");
        tvValue.setText(activity.getUser().get_contact().get_account().get_currentLicenseNumber().getLicense_Issue_Date());
        nocDetails.addView(view);
        view = inflater.inflate(R.layout.wizards_form_field_details, null, false);
        tvLabel = (TextView) view.findViewById(R.id.pay_title);
        tvValue = (TextView) view.findViewById(R.id.pay_text);
        tvLabel.setText("Expire Date" + "\t:");
        tvValue.setText(activity.getUser().get_contact().get_account().get_currentLicenseNumber().getLicense_Expiry_Date());
        nocDetails.addView(view);
        view = inflater.inflate(R.layout.wizards_form_field_details, null, false);
        tvLabel = (TextView) view.findViewById(R.id.pay_title);
        tvValue = (TextView) view.findViewById(R.id.pay_text);
        tvLabel.setText("Commercial Name" + "\t:");
        tvValue.setText(activity.getUser().get_contact().get_account().get_currentLicenseNumber().getCommercial_Name());
        nocDetails.addView(view);
        view = inflater.inflate(R.layout.wizards_form_field_details, null, false);
        tvLabel = (TextView) view.findViewById(R.id.pay_title);
        tvValue = (TextView) view.findViewById(R.id.pay_text);
        tvLabel.setText("Commercial Name Arabic" + "\t:");
        tvValue.setText(activity.getUser().get_contact().get_account().get_currentLicenseNumber().getCommercial_Name_Arabic());
        nocDetails.addView(view);
        view = inflater.inflate(R.layout.wizards_form_field_details, null, false);
        tvLabel = (TextView) view.findViewById(R.id.pay_title);
        tvValue = (TextView) view.findViewById(R.id.pay_text);
        tvLabel.setText("License Number" + "\t:");
        tvValue.setText(activity.getUser().get_contact().get_account().get_currentLicenseNumber().getLicense_Number_Value());
        nocDetails.addView(view);

    }
}
