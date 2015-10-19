package fragment.License.Renew;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import RestAPI.JSONConstants;
import RestAPI.RestMessages;
import RestAPI.SFResponseManager;
import RestAPI.SoqlStatements;
import cloudconcept.dwc.R;
import fragmentActivity.LicenseActivity;
import utilities.Utilities;

/**
 * Created by M_Ghareeb on 9/15/2015.
 */
public class InitialPage extends Fragment {
    LicenseActivity activity;
    EditText servicePrice;
    public String eServiceAdmin = "SELECT ID, No_of_Upload_Docs__c,Name, Display_Name__c, Service_Identifier__c, Amount__c, Total_Amount__c, (SELECT ID, Name, Type__c, Language__c, Document_Type__c, Authority__c FROM eServices_Document_Checklists__r WHERE Document_Required_for_Branch_or_LLC__c != '%s' AND Document_Type__c = 'Upload') FROM Receipt_Template__c WHERE Is_Active__c = true AND Service_Identifier__c = 'Annual License Renewal'";
    private RestRequest restRequest;

    public static Fragment newInstance() {
        InitialPage temp = new InitialPage();
        return temp;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_initial_licence_renew, container, false);
        activity = (fragmentActivity.LicenseActivity) getActivity();
        InitializeViews(view);
        return view;
    }

    private void InitializeViews(View view) {
        servicePrice = (EditText) view.findViewById(R.id.servicePrice);
        Utilities.showloadingDialog(activity);
        new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
            @Override
            public void authenticatedRestClient(final RestClient client) {
                if (client == null) {
                    System.exit(0);
                } else {
                    String Value = "";

                    if (activity.getUser().get_contact().get_account().getLegalForm().equals("DWC-LLC")) {
                        Value = "Branch";
                    } else if (activity.getUser().get_contact().get_account().getLegalForm().equals("DWC-Branch")) {
                        Value = "LLC";
                    }
                    String SoqlEServiceQuery = String.format(eServiceAdmin, Value);
                    try {

                        // Getting E-Service Administration
                        restRequest = RestRequest.getRequestForQuery(getActivity().getString(R.string.api_version), SoqlEServiceQuery);
                        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {

                            @Override
                            public void onSuccess(RestRequest request, RestResponse result) {

                                activity.seteServiceAdministration(SFResponseManager.parseReceiptObjectResponse(result.toString()).get(0));
                                servicePrice.setText(activity.geteServiceAdministration().getTotal_Amount__c() + "AED");
                                getRecordType();

                            }

                            @Override
                            public void onError(Exception exception) {
                                VolleyError volleyError = (VolleyError) exception;
                                NetworkResponse response = volleyError.networkResponse;
                                String json = new String(response.data);
                                Log.d("", json);
                                Utilities.dismissLoadingDialog();
                                getActivity().finish();
                            }
                        });
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }

    private void getRecordType() {
        //Getting  Case record Type
        String soql ="SELECT Id, Name, DeveloperName, SobjectType FROM RecordType WHERE SObjectType = 'Case' AND DeveloperName = 'License_Request'";
        try {
            restRequest = RestRequest.getRequestForQuery(getActivity().getString(R.string.api_version), soql);
            new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
                @Override
                public void authenticatedRestClient(RestClient client) {
                    if (client == null) {
                        SalesforceSDKManager.getInstance().logout(getActivity());
                        return;
                    } else {
                        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {

                            @Override
                            public void onSuccess(RestRequest request, RestResponse result) {
                                try {
                                    JSONObject jsonObject = new JSONObject(result.toString());
                                    JSONArray jsonArrayRecords = jsonObject.getJSONArray(JSONConstants.RECORDS);
                                    for (int i = 0; i < jsonArrayRecords.length(); i++) {
                                        JSONObject jsonRecord = jsonArrayRecords.getJSONObject(i);
                                        String objectType = jsonRecord.getString("SobjectType");
                                        String DeveloperName = jsonRecord.getString("DeveloperName");
                                        if (objectType.equals("Case") && DeveloperName.equals("License_Request")) {
                                            activity.setCaseRecordTypeId(jsonRecord.getString("Id"));
                                            getCase();
                                        }

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Exception exception) {
                                VolleyError volleyError = (VolleyError) exception;
                                NetworkResponse response = volleyError.networkResponse;
                                String json = new String(response.data);
                                Log.d("", json);
                                Utilities.dismissLoadingDialog();
                                getActivity().finish();
                            }
                        });
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void getCase() {

        //Creating Case and save id in the running Activity

        if (activity.geteServiceAdministration() != null) {
            activity.setCaseFields(new HashMap<String, Object>());
            Map<String, Object> caseFields = activity.getCaseFields();
            caseFields.put("Service_Requested__c", activity.geteServiceAdministration().getID());
            caseFields.put("AccountId", activity.getUser().get_contact().get_account().getID());
            caseFields.put("RecordTypeId", activity.getCaseRecordTypeId());
            caseFields.put("Status", "Draft");
            caseFields.put("Type", "License Services");
            caseFields.put("Origin", "Mobile");
            caseFields.put("License_Ref__c",activity.getUser().get_contact().get_account().get_currentLicenseNumber().getId());
            caseFields.put("isCourierRequired__c",true);
            activity.setCaseFields(caseFields);
            if (activity.getInsertedCaseId() != null && !activity.getInsertedCaseId().equals("")) {
                try {
                    restRequest = RestRequest.getRequestForUpdate(getActivity().getString(R.string.api_version), "Case", activity.getInsertedCaseId(), activity.getCaseFields());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    restRequest = RestRequest.getRequestForCreate(getActivity().getString(R.string.api_version), "Case", activity.getCaseFields());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
                @Override
                public void authenticatedRestClient(final RestClient client) {
                    if (client == null) {
                        SalesforceSDKManager.getInstance().logout(getActivity());
                        return;
                    } else {
                        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                            @Override
                            public void onSuccess(RestRequest request, RestResponse response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.toString());
                                    activity.setInsertedCaseId(jsonObject.getString("id"));
                                        // Getting other information Related to Created Case
                                    try {
                                        restRequest = RestRequest.getRequestForQuery(getString(R.string.api_version), SoqlStatements.getCaseNumberQuery(activity.getInsertedCaseId()));
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                                        @Override
                                        public void onSuccess(RestRequest request, RestResponse response) {
                                            JSONObject jsonObject = null;
                                            Utilities.dismissLoadingDialog();
                                            try {
                                                jsonObject = new JSONObject(response.toString());
                                                JSONArray jsonArray = jsonObject.getJSONArray(JSONConstants.RECORDS);
                                                JSONObject jsonRecord = jsonArray.getJSONObject(0);
                                                Log.d("resultcase", response.toString());
                                                activity.setCaseNumber(jsonRecord.getString("CaseNumber"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }

                                        @Override
                                        public void onError(Exception exception) {

                                        }
                                    });
                                    Log.d("", response.toString());

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Exception exception) {
                                VolleyError volleyError = (VolleyError) exception;
                                NetworkResponse response = volleyError.networkResponse;
                                String json = new String(response.data);
                                Log.d("", json);
                                Utilities.dismissLoadingDialog();
                                getActivity().finish();
                            }
                        });


                    }
                }
            });

        }
    }
}
