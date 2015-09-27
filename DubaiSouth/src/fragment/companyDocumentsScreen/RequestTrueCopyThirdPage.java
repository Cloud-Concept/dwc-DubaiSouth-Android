package fragment.companyDocumentsScreen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import cloudconcept.dwc.R;
import fragmentActivity.RequestTrueCopyActivity;

/**
 * Created by Abanoub Wagdy on 9/12/2015.
 */
public class RequestTrueCopyThirdPage extends Fragment {

    TextView tvThankYou;
    Button btnClose;
    RequestTrueCopyActivity activity;

    public static Fragment newInstance(String s) {
        RequestTrueCopyThirdPage fragment = new RequestTrueCopyThirdPage();
        Bundle bundle = new Bundle();
        bundle.putString("text", s);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.thank_you, container, false);
        activity = (RequestTrueCopyActivity) getActivity();
        InitializeViews(view);
        return view;
    }

    private void InitializeViews(View view) {
        tvThankYou = (TextView) view.findViewById(R.id.tvThankyou);
        btnClose = (Button) view.findViewById(R.id.btnClose);
        btnClose.setVisibility(View.GONE);
        String Message = String.format(getString(R.string.ServiceThankYouMessage), activity.getCaseNumber()) + "\n" + String.format(getString(R.string.ServiceThankYouMessagePayment), activity.geteServices_document_checklist__c().geteService_Administration__r().getTotal_Amount__c() + " including Knowledge Fee of AED 10");
        tvThankYou.setText(Message);
        RequestTrueCopyFragment fragment = (RequestTrueCopyFragment) getParentFragment();
        fragment.setTitle("Thank You");
    }
}
