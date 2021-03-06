package fragment.Visa.Cancel;

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
import fragmentActivity.VisaActivity;

/**
 * Created by M_Ghareeb on 9/7/2015.
 */
public class PayAndSubmitCancelVisa extends Fragment {
    private LinearLayout nocDetails;

    public static PayAndSubmitCancelVisa newInstance(String Type) {
        PayAndSubmitCancelVisa fragment = new PayAndSubmitCancelVisa();

        return fragment;
    }
    VisaActivity activity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = ((VisaActivity) getActivity());
        View view = inflater.inflate(R.layout.fragment_cancel_visa_pay_and_submit, container, false);
        nocDetails = (LinearLayout) view.findViewById(R.id.nocDetails);
        // Inflate the layout for this fragment
        ImageView image = (ImageView) view.findViewById(R.id.imageEmpoyeeNOC);


        image.setImageResource(R.mipmap.cancel_visa);




        DrawLayout(inflater);
        return view;
    }

    private void DrawLayout(LayoutInflater inflater) {

        // Creating UI Fields
        View view = inflater.inflate(R.layout.wizard_form_field_pay_header, null, false);
        TextView tvHeader = (TextView) view.findViewById(R.id.pay_header);
        tvHeader.setText("Visa Details");
        nocDetails.addView(view);
        TextView tvLabel;
        TextView tvValue;
        view = inflater.inflate(R.layout.wizards_form_field_details, null, false);
        tvLabel = (TextView) view.findViewById(R.id.pay_title);
        tvValue = (TextView) view.findViewById(R.id.pay_text);

        tvLabel.setText( "Name"+ "\t:");
        tvValue.setText(activity.getVisa().getApplicant_Full_Name__c());
        nocDetails.addView(view);
        view = inflater.inflate(R.layout.wizards_form_field_details, null, false);
        tvLabel = (TextView) view.findViewById(R.id.pay_title);
        tvValue = (TextView) view.findViewById(R.id.pay_text);
        tvLabel.setText( "Passport Number"+ "\t:");
        tvValue.setText(activity.getVisa().getPassport_Number__c());
        nocDetails.addView(view);
        view = inflater.inflate(R.layout.wizards_form_field_details, null, false);
        tvLabel = (TextView) view.findViewById(R.id.pay_title);
        tvValue = (TextView) view.findViewById(R.id.pay_text);
        tvLabel.setText("Visa Expiry Date" + "\t:");
        tvValue.setText(activity.getVisa().getVisa_Expiry_Date__c());
        nocDetails.addView(view);
        view = inflater.inflate(R.layout.wizards_form_field_details, null, false);
        tvLabel = (TextView) view.findViewById(R.id.pay_title);
        tvValue = (TextView) view.findViewById(R.id.pay_text);
        tvLabel.setText("Country of Issue" + "\t:");
        try {
            tvValue.setText(activity.getVisa().getPassport_Issue_Country__r().getName());
            nocDetails.addView(view);
        }catch(NullPointerException e){
            nocDetails.addView(view);
        }

        view = inflater.inflate(R.layout.wizards_form_field_details, null, false);
        tvLabel = (TextView) view.findViewById(R.id.pay_title);
        tvValue = (TextView) view.findViewById(R.id.pay_text);
        tvLabel.setText("Occupation" + "\t:");
        tvValue.setText(activity.getVisa().getJob_Title_at_Immigration__r().getName() );
        nocDetails.addView(view);
        view = inflater.inflate(R.layout.wizards_form_field_details, null, false);
        tvLabel = (TextView) view.findViewById(R.id.pay_title);
        tvValue = (TextView) view.findViewById(R.id.pay_text);
        tvLabel.setText("Qualification" + "\t:");
        tvValue.setText(activity.getVisa().getQualification__r().getName() );
        nocDetails.addView(view);


        view = inflater.inflate(R.layout.wizards_form_field_details, null, false);
        tvLabel = (TextView) view.findViewById(R.id.pay_title);
        tvValue = (TextView) view.findViewById(R.id.pay_text);
        tvLabel.setText("Total Amount" + "\t:");
        tvValue.setText(activity.getTotal()==null?"0":activity.getTotal()+"AED");
        nocDetails.addView(view);


        //Aditions
        view = inflater.inflate(R.layout.wizard_form_field_pay_header, null, false);
        tvHeader = (TextView) view.findViewById(R.id.pay_header);
        tvHeader.setText("Additions");
        nocDetails.addView(view);
        view = inflater.inflate(R.layout.wizards_form_field_details, null, false);
        tvLabel = (TextView) view.findViewById(R.id.pay_title);
        tvValue = (TextView) view.findViewById(R.id.pay_text);
        tvLabel.setText("Urgent Cancellation" + "\t:");
        tvValue.setText(activity.getVisa().Urgent_Stamping_Paid__c ? "Yes" : "No");
        nocDetails.addView(view);
    }
}