package fragment.companychangeservices;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cloudconcept.dwc.R;
import fragmentActivity.ChangeAndRemovalActivity;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 9/2/2015.
 */
public class PreviewPage extends Fragment {

    private static final String ARG_TEXT = "Preview";
    ChangeAndRemovalActivity activity;
    TextView tvRefNumber, tvDate, tvStatus, tvCurrentMobile, tvCurrentEmail, tvCurrentPoBox, tvCurrentFax, tvNewMobile, tvNewEmail, tvNewFax, tvNewPoVox;
    private TextView tvTotalAmount;
    TextView tvCompanyName, tvCompanyNameArabic, tvNewCompanyName, tvNewCompanyNameArabic;

    TextView tvNewShareCapital;
    TextView tvDirectorName;

    public static PreviewPage newInstance(String text) {
        PreviewPage fragment = new PreviewPage();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TEXT, text);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        activity = (ChangeAndRemovalActivity) getActivity();
        if (activity.getMethodName().equals("CreateRequestAddressChange")) {
            view = inflater.inflate(R.layout.address_change_preview_page, container, false);
            InitializeAddressChangeLayout(view);

        } else if (activity.getMethodName().equals("CreateRequestNameChange")) {
            view = inflater.inflate(R.layout.name_change_preview_page, container, false);
            InitializeNameChangeLayout(view);

        } else if (activity.getMethodName().equals("CreateRequestCapitalChange")) {
            view = inflater.inflate(R.layout.capital_change_preview_page, container, false);
            InitializeCapitalChangeLayout(view);
        } else if (activity.getMethodName().equals("CreateRequestDirectorRemoval")) {
            view = inflater.inflate(R.layout.director_removal_preview_page, container, false);
            InitializeDirectorRemovalLayout(view);
        }

        return view;
    }

    private void InitializeDirectorRemovalLayout(View view) {
        tvDirectorName = (TextView) view.findViewById(R.id.tvDirecotName);
        tvTotalAmount = (TextView) view.findViewById(R.id.tvTotalAmount);

        tvDirectorName.setText(activity.getDirectorship().get_director().getName());
        tvTotalAmount.setText(activity.getCaseObject().getInvoice__c());
    }

    private void InitializeCapitalChangeLayout(View view) {
        tvNewShareCapital = (TextView) view.findViewById(R.id.tvNewShareCapital);
        tvTotalAmount = (TextView) view.findViewById(R.id.tvTotalAmount);

        tvNewShareCapital.setText(activity.getNewShareCapital());
        tvTotalAmount.setText(Utilities.processAmount(activity.getCaseObject().getInvoice__c())+" AED.");
    }

    private void InitializeNameChangeLayout(View view) {
        tvRefNumber = (TextView) view.findViewById(R.id.tvRefNumber);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvStatus = (TextView) view.findViewById(R.id.tvStatus);
        tvTotalAmount = (TextView) view.findViewById(R.id.tvTotalAmount);

        tvCompanyName = (TextView) view.findViewById(R.id.tvCompanyName);
        tvCompanyNameArabic = (TextView) view.findViewById(R.id.tvCompanyNameArabic);
        tvNewCompanyName = (TextView) view.findViewById(R.id.tvNewCompanyName);
        tvNewCompanyNameArabic = (TextView) view.findViewById(R.id.tvNewCompanyNameArabic);

        tvRefNumber.setText(activity.getCaseObject().getCaseNumber());
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        tvDate.setText(strDate);
        tvStatus.setText("Draft");
        tvTotalAmount.setText(Utilities.processAmount(activity.getCaseObject().getInvoice__c())+" AED.");

        tvCompanyName.setText(activity.getCompanyName());
        tvCompanyNameArabic.setText(activity.getCompanyNameArabic());
        tvNewCompanyName.setText(activity.getNewCompanyName());
        tvNewCompanyNameArabic.setText(activity.getNewCompanyNameArabic());
    }

    private void InitializeAddressChangeLayout(View view) {

        tvRefNumber = (TextView) view.findViewById(R.id.tvRefNumber);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvStatus = (TextView) view.findViewById(R.id.tvStatus);
        tvCurrentMobile = (TextView) view.findViewById(R.id.tvCurrentMobile);
        tvCurrentEmail = (TextView) view.findViewById(R.id.tvCurrentEmail);
        tvCurrentPoBox = (TextView) view.findViewById(R.id.tvCurrentPoBox);
        tvCurrentFax = (TextView) view.findViewById(R.id.tvCurrentFax);
        tvNewMobile = (TextView) view.findViewById(R.id.tvNewMobile);
        tvNewEmail = (TextView) view.findViewById(R.id.tvNewEmail);
        tvNewFax = (TextView) view.findViewById(R.id.tvNewFax);
        tvNewPoVox = (TextView) view.findViewById(R.id.tvNewPoBox);

        tvRefNumber.setText(activity.getCaseObject().getCaseNumber());
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        tvDate.setText(strDate);
        tvStatus.setText("Draft");
        tvCurrentMobile.setText(activity.getCurrentMobile());
        tvCurrentEmail.setText(activity.getCurrentEmail());
        tvCurrentPoBox.setText(activity.getCurrentPoBox());
        tvCurrentFax.setText(activity.getCurrentFax());
        tvNewMobile.setText(activity.getNewMobile());
        tvNewEmail.setText(activity.getNewEmail());
        tvNewFax.setText(activity.getNewFax());
        tvNewPoVox.setText(activity.getNewPoBox());
    }
}
