package fragment.companyDocumentsScreen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cloudconcept.dwc.R;
import dataStorage.StoreData;
import fragmentActivity.RequestTrueCopyActivity;
import model.EServices_Document_Checklist__c;
import model.FormField;
import model.User;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 9/12/2015.
 */
public class RequestTrueCopySecondPage extends Fragment {

    TextView tvDate, tvStatus, tvTotalAmount, tvRefNumber;
    LinearLayout linear;
    RequestTrueCopyActivity activity;

    public static Fragment newInstance(String s) {
        RequestTrueCopySecondPage fragment = new RequestTrueCopySecondPage();
        Bundle bundle = new Bundle();
        bundle.putString("text", s);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.true_copy_second_page, container, false);
        activity = (RequestTrueCopyActivity) getActivity();
        InitializeViews(view, inflater);
        return view;
    }

    private void InitializeViews(View view, LayoutInflater inflater) {
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvStatus = (TextView) view.findViewById(R.id.tvStatus);
        tvTotalAmount = (TextView) view.findViewById(R.id.tvTotalAmount);
        tvRefNumber = (TextView) view.findViewById(R.id.tvRefNumber);
        linear = (LinearLayout) view.findViewById(R.id.linear);

        long yourmilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        Date resultdate = new Date(yourmilliseconds);
        tvDate.setText(sdf.format(resultdate));
        tvStatus.setText(String.valueOf(activity.getCaseFields().get("Status")));
        tvTotalAmount.setText(Utilities.processAmount(String.valueOf(activity.geteServices_document_checklist__c().geteService_Administration__r().getTotal_Amount__c()))+" AED.");
        tvRefNumber.setText(activity.getCaseNumber());
        DrawLayout(inflater);
    }

    public void DrawLayout(LayoutInflater inflater) {
        List<FormField> formFields = null;
        formFields = activity.getWebForm().get_formFields();

        for (FormField field : formFields) {
            if (field.getType().equals("CUSTOMTEXT")) {

                View view = inflater.inflate(R.layout.wizard_form_field_pay_header, null, false);
                TextView tvHeader = (TextView) view.findViewById(R.id.pay_header);
                tvHeader.setText(field.getMobileLabel());
                linear.addView(view);

            } else {
                TextView tvLabel;
                TextView tvValue;
                View view = inflater.inflate(R.layout.wizards_form_field_details, null, false);
                tvLabel = (TextView) view.findViewById(R.id.pay_title);
                tvValue = (TextView) view.findViewById(R.id.pay_text);
                tvLabel.setText(field.getMobileLabel() + "\t:");
                String stringValue = "";
                String name = field.getName();
                Field[] fields = EServices_Document_Checklist__c.class.getFields();
                if (field.getMobileLabel().equals("Account ID")) {
                    continue;
                } else if (field.getMobileLabel().equals("Company Name")) {
                    Gson gson = new Gson();
                    User user = gson.fromJson(new StoreData(getActivity().getApplicationContext()).getUserDataAsString(), User.class);
                    stringValue = user.get_contact().get_account().getName();
                } else {
                    for (int j = 0; j < fields.length; j++) {
                        if (name.toLowerCase().equals(fields[j].getName().toLowerCase())) {
                            try {
                                stringValue = String.valueOf(fields[j].get(activity.geteServices_document_checklist__c()));
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (stringValue.equals("")) {
                        Map<String, String> parameters = activity.getParameters();
                        if (parameters.containsKey(field.getTextValue())) {
                            stringValue = parameters.get(field.getTextValue());
                        }
                    }
                }
                tvValue.setText(stringValue);
                linear.addView(view);
            }
        }
    }
}