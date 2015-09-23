package fragment.companyDocumentsScreen;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import RestAPI.JSONConstants;
import RestAPI.SoqlStatements;
import cloudconcept.dwc.R;
import custom.customdialog.NiftyDialogBuilder;
import fragment.BaseFragmentThreeSteps;
import fragmentActivity.RequestTrueCopyActivity;
import model.EServices_Document_Checklist__c;
import model.FormField;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 9/12/2015.
 */
public class RequestTrueCopyFragment extends BaseFragmentThreeSteps {

    RequestTrueCopyActivity activity;
    private RestRequest restRequest;
    private String insertedCaseId;
    private String caseNumber;
    private NiftyDialogBuilder builder;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = (RequestTrueCopyActivity) getActivity();
    }

    @Override
    public Fragment getInitialFragment() {
        return RequestTrueCopyInitialPage.newInstance("TrueCopyInitial");
    }

    @Override
    public Fragment getSecondFragment() {
        return RequestTrueCopySecondPage.newInstance("TrueCopySecond");
    }

    @Override
    public Fragment getThirdFragment() {
        return RequestTrueCopyThirdPage.newInstance("TrueCopyThird");
    }

    @Override
    public RelatedServiceType getRelatedService() {
        return RelatedServiceType.RELATED_SERVICE_COMPANY_DOCUMENT_TRUE_COPY;
    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            if (getStatus() == 1) {
                if (required()) {
                    CreateCaseRecord();
                } else {

                }
            } else if (getStatus() == 2) {
                builder = Utilities.showCustomNiftyDialog("Pay Process", getActivity(), listenerOkPay, "Are you sure want to Pay for the service ?");
            } else {
                super.onClick(v);
            }
        } else if (v == btnCancel) {
            super.onClick(v);
        }
    }

    private View.OnClickListener listenerOkPay = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            builder.dismiss();

            new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
                @Override
                public void authenticatedRestClient(final RestClient client) {
                    if (client == null) {
                        System.exit(0);
                    } else {
                        new SubmitRequest(client).execute();
                    }
                }
            });

        }
    };

    private void CreateCaseRecord() {
        Utilities.showloadingDialog(getActivity());
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

                            Log.d("", response.toString());
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                insertedCaseId = jsonObject.getString("id");
                                activity.setInsertedCaseId(insertedCaseId);

                                try {
                                    restRequest = RestRequest.getRequestForQuery(getString(R.string.api_version), SoqlStatements.getCaseNumberQuery(insertedCaseId));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                                    @Override
                                    public void onSuccess(RestRequest request, RestResponse response) {
                                        JSONObject jsonObject = null;
                                        try {
                                            Utilities.dismissLoadingDialog();
                                            jsonObject = new JSONObject(response.toString());
                                            JSONArray jsonArray = jsonObject.getJSONArray(JSONConstants.RECORDS);
                                            JSONObject jsonRecord = jsonArray.getJSONObject(0);
                                            Log.d("resultcase", response.toString());
                                            caseNumber = jsonRecord.getString("CaseNumber");
                                            activity.setCaseNumber(caseNumber);
                                            PerformParentNext(btnNext);
                                        } catch (JSONException e) {
                                            Utilities.dismissLoadingDialog();
                                            e.printStackTrace();
                                        }

                                    }

                                    @Override
                                    public void onError(Exception exception) {
                                        Utilities.dismissLoadingDialog();
                                    }
                                });
                                Log.d("", response.toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Utilities.dismissLoadingDialog();
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

    private void PerformParentNext(Button btnNext) {
        btnNext.setText("Pay/Submit");
        super.onClick(btnNext);
    }

    private boolean required() {
        boolean result = true;
        for (FormField field : activity.getWebForm().get_formFields()) {
            if (field.getName().equals("AccountId")) {
                continue;
            }
            if (field.isRequired()) {
                String name = field.getName();
                String stringValue = "";
                Field[] fields = EServices_Document_Checklist__c.class.getFields();
                for (int j = 0; j < fields.length; j++)
                    if (name.toLowerCase().equals(fields[j].getName().toLowerCase()))
                        try {
                            stringValue = (String) fields[j].get(activity.geteServices_document_checklist__c());
                            if (field.getType().equals("DOUBLE")) {
                                try {
                                    if (fields[j].getDouble(activity.geteServices_document_checklist__c()) == 0.0) {
                                        Utilities.showLongToast(getActivity(), "Please check required fields");
                                        result = false;
                                        return false;
                                    } else {
                                        stringValue = String.valueOf(fields[j].getDouble(activity.geteServices_document_checklist__c()));
                                    }
                                } catch (IllegalAccessException e1) {
                                    e1.printStackTrace();
                                }
                            } else if (field.getType().equals("EMAIL")) {
                                try {
                                    stringValue = (String) fields[j].get(activity.geteServices_document_checklist__c());
                                    if (Utilities.isEmailValid(stringValue) == false) {
                                        Utilities.showLongToast(getActivity(), "Please Enter valid Email");
                                        result = false;
                                        return false;
                                    }
                                } catch (IllegalAccessException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                        }

                if (stringValue == null) {
                    Utilities.showLongToast(getActivity(), "Please check required fields");
                    result = false;
                    return false;
                } else if (stringValue.equals("")) {
                    Utilities.showLongToast(getActivity(), "Please check required fields");
                    result = false;
                    return false;
                }
            }
        }
        return result;
    }

    public static Fragment newInstance(String s) {
        RequestTrueCopyFragment fragment = new RequestTrueCopyFragment();
        Bundle bundle = new Bundle();
        bundle.putString("text", s);
        fragment.setArguments(bundle);
        return fragment;
    }

    public class SubmitRequest extends AsyncTask<String, Void, String> {

        private final RestClient client;
        String result;

        public SubmitRequest(RestClient client) {
            this.client = client;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utilities.showloadingDialog(getActivity());
        }

        @Override
        protected String doInBackground(String... params) {
            String attUrl = client.getClientInfo().resolveUrl("/services/apexrest/MobilePayAndSubmitWebService").toString();

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(attUrl);
            httppost.setHeader("Authorization", "Bearer " + client.getAuthToken());
            StringEntity entity = null;
            try {
                Map<String, String> map = new HashMap<String, String>();
                map.put("caseId", activity.getInsertedCaseId());
                entity = new StringEntity(new JSONObject(map).toString(), "UTF-8");
                entity.setContentType("application/json");
                httppost.setEntity(entity);
                HttpResponse response = httpclient.execute(httppost);
                result = EntityUtils.toString(response.getEntity());
                Log.d("result", result);
                return result.toLowerCase().contains("success") ? "success" : null;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            Utilities.dismissLoadingDialog();
            if (aVoid.equals("success")) {
                PerformParentNext(btnNext);
            }else{

            }
        }
    }
}
