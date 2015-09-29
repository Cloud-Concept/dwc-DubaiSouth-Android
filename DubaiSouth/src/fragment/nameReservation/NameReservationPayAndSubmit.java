package fragment.nameReservation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import RestAPI.JSONConstants;
import cloudconcept.dwc.R;
import fragmentActivity.NameReservationActivity;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 8/31/2015.
 */
public class NameReservationPayAndSubmit extends Fragment {
    private static final String ARG_TEXT = "Second";
    TextView tvChoice1, tvChoice2, tvChoice3, tvTotal, tvDate, tvStatus;
    NameReservationActivity activity;
    private RestRequest restRequest;
    private JSONObject jsonObject1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.name_reservation_pay_and_submit, container, false);
        InitializeViews(view);
        Utilities.showloadingDialog(getActivity());
        new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
            @Override
            public void authenticatedRestClient(final RestClient client) {
                if (client == null) {
                    System.exit(0);
                    Utilities.dismissLoadingDialog();
                } else {
                    CallNameReservationAmountService(client);
                }
            }
        });
        return view;
    }

    private void CallNameReservationAmountService(RestClient client) {

        String soql = "select id , Total_Amount__c from Receipt_Template__c where Service_Identifier__c = 'Reservation of Business Name'";
        try {
            restRequest = RestRequest.getRequestForQuery(activity.getString(R.string.api_version), soql);
            client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                @Override
                public void onSuccess(RestRequest request, RestResponse response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.toString());
                        JSONArray jsonArray = jsonObject.getJSONArray(JSONConstants.RECORDS);
                        jsonObject1 = jsonArray.getJSONObject(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Utilities.dismissLoadingDialog();
                    tvChoice1.setText(activity.getChoice1Text());
                    tvChoice2.setText(activity.getChoice2Text());
                    tvChoice3.setText(activity.getChoice3Text());
                    try {
                        tvTotal.setText(jsonObject1.getString("Total_Amount__c") + " AED.");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar calendar = Calendar.getInstance();
                    Date now = calendar.getTime();
                    String strDate = sdfDate.format(now);
                    tvDate.setText(strDate);
                    tvStatus.setText("Draft");
                }

                @Override
                public void onError(Exception exception) {
                    Utilities.dismissLoadingDialog();
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void InitializeViews(View view) {
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvStatus = (TextView) view.findViewById(R.id.tvStatus);

        tvChoice1 = (TextView) view.findViewById(R.id.tvChoice1);
        tvChoice2 = (TextView) view.findViewById(R.id.tvChoice2);
        tvChoice3 = (TextView) view.findViewById(R.id.tvChoice3);
        tvTotal = (TextView) view.findViewById(R.id.tvTotalAmount);
        activity = (NameReservationActivity) getActivity();
    }

    public static NameReservationPayAndSubmit newInstance(String text) {

        NameReservationPayAndSubmit fragment = new NameReservationPayAndSubmit();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TEXT, text);
        fragment.setArguments(bundle);
        return fragment;
    }
}
