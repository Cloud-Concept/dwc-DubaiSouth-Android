package fragment.companyInfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import RestAPI.JSONConstants;
import cloudconcept.dwc.R;
import dataStorage.StoreData;
import model.DWCView;
import model.EstablishmentCard;
import model.ItemType;
import model.User;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 10/1/2015.
 */
public class CardEstablishmentFragment extends Fragment {

    LinearLayout linearLayout;
    static ArrayList<DWCView> _views;
    String establishment_card_soql = "select Current_Establishment_Card__c , Current_Establishment_Card__r.Status__c , Current_Establishment_Card__r.Establishment_Card_Number__c , Current_Establishment_Card__r.Issue_Date__c , Current_Establishment_Card__r.Expiry_Date__c from Account where id = " + "\'" + "%s" + "\'";
    private RestRequest restRequest;
    private static User _user;

    public static CardEstablishmentFragment newInstance(String text) {
        CardEstablishmentFragment fragment = new CardEstablishmentFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        _views = new ArrayList<DWCView>();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.company_info_inner_fragment, container, false);
        linearLayout = (LinearLayout) view.findViewById(R.id.content_linear);
        Gson gson = new Gson();
        _user = gson.fromJson(new StoreData(getActivity().getApplicationContext()).getUserDataAsString(), User.class);
        if (_user.get_contact().get_account().getEstablishmentCard() == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        establishment_card_soql = String.format(establishment_card_soql, _user.get_contact().get_account().getID());
                        restRequest = RestRequest.getRequestForQuery(
                                getActivity().getString(R.string.api_version), establishment_card_soql);
                        new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {

                            @Override
                            public void authenticatedRestClient(RestClient client) {
                                if (client == null) {
                                    SalesforceSDKManager.getInstance().logout(getActivity());
                                    return;
                                } else {
                                    client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                                        @Override
                                        public void onSuccess(RestRequest request, RestResponse response) {
                                            Log.d("", response.toString());
                                            try {
                                                JSONObject json = new JSONObject(response.toString());
                                                JSONArray jRecords = json.getJSONArray(JSONConstants.RECORDS);
                                                JSONObject jsonRecord = jRecords.getJSONObject(0);
                                                EstablishmentCard establishmentCard = new EstablishmentCard();
                                                establishmentCard.setCurrent_Establishment_Card__c(jsonRecord.getString("Current_Establishment_Card__c"));
                                                establishmentCard.setIssue_Date__c(jsonRecord.getJSONObject("Current_Establishment_Card__r").getString("Issue_Date__c"));
                                                establishmentCard.setExpiry_Date__c(jsonRecord.getJSONObject("Current_Establishment_Card__r").getString("Expiry_Date__c"));
                                                establishmentCard.setStatus(jsonRecord.getJSONObject("Current_Establishment_Card__r").getString("Status__c"));
                                                establishmentCard.setEstablishment_Card_Number__c(jsonRecord.getJSONObject("Current_Establishment_Card__r").getString("Establishment_Card_Number__c"));
                                                _user.get_contact().get_account().setEstablishmentCard(establishmentCard);
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        InitializeLayout();
                                                    }
                                                });
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onError(Exception exception) {

                                        }
                                    });
                                }
                            }
                        });
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            InitializeLayout();
        }
        return view;
    }

    private void InitializeLayout() {
        _views.clear();
        _views.add(new DWCView("Card Information", ItemType.HEADER));
        _views.add(new DWCView("Card Number", ItemType.LABEL));
        _views.add(new DWCView(Utilities.stringNotNull(_user.get_contact().get_account().getEstablishmentCard().getEstablishment_Card_Number__c()), ItemType.VALUE));
        _views.add(new DWCView("", ItemType.LINE));
        _views.add(new DWCView("Issue Date", ItemType.LABEL));
        _views.add(new DWCView(Utilities.stringNotNull(_user.get_contact().get_account().getEstablishmentCard().getIssue_Date__c()), ItemType.VALUE));
        _views.add(new DWCView("", ItemType.LINE));
        _views.add(new DWCView("Expiry Date", ItemType.LABEL));
        _views.add(new DWCView(Utilities.stringNotNull(_user.get_contact().get_account().getEstablishmentCard().getExpiry_Date__c()), ItemType.VALUE));
        _views.add(new DWCView("Renew Card,Lost Card,Cancel Card", ItemType.HORIZONTAL_LIST_VIEW));
        View viewItems = Utilities.drawViewsOnLayout(getActivity(), _user, getActivity().getApplicationContext(), _views);
        linearLayout.removeAllViews();
        linearLayout.addView(viewItems);
    }
}