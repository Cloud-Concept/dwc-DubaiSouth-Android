package fragmentActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import RestAPI.SFResponseManager;
import cloudconcept.dwc.R;
import dataStorage.StoreData;
import fragment.LicenseCancellationThankYouFragment;
import model.User;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 9/13/2015.
 */
public class LicenseCancellationActivity extends FragmentActivity implements View.OnClickListener {

    TextView tvLicenseName, tvLicenseNumber, tvIssueDate, tvKnowledgeFees, tvTotalAmount, tvErrorMessage;
    Button btnCancel, btnPayAndSubmit;
    ImageView imageBack;
    String soql = "select Service_Identifier__c , Amount__c , Display_Name__c , Require_Knowledge_Fee__c , Knowledge_Fee__r.Amount__c from Receipt_Template__c where Service_Identifier__c in ('License Cancellation' , 'License Cancellation')";
    private RestRequest restRequest;
    private String result;
    User _user;
    RelativeLayout relativeAllViews;
    private Gson gson;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cancel_license);
        InitializeViews();
        DoDataRetrieveRequest();
    }

    private void DoDataRetrieveRequest() {
        try {
            Utilities.showloadingDialog(this);
            restRequest = RestRequest.getRequestForQuery(
                    getApplicationContext().getString(R.string.api_version), soql);
            new ClientManager(this, SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(this, new ClientManager.RestClientCallback() {
                @Override
                public void authenticatedRestClient(final RestClient client) {
                    if (client == null) {
                        SalesforceSDKManager.getInstance().logout(LicenseCancellationActivity.this);
                        return;
                    } else {
                        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                            @Override
                            public void onSuccess(RestRequest request, RestResponse result) {
                                try {
                                    client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                                        @Override
                                        public void onSuccess(RestRequest request, RestResponse response) {
                                            Log.d("", response.toString());
                                            Utilities.dismissLoadingDialog();
                                            JSONObject jsonObject = null;
                                            try {
                                                jsonObject = new JSONObject(response.toString());
                                                JSONArray jArrayRecords = jsonObject.getJSONArray("records");
                                                jsonObject = jArrayRecords.getJSONObject(0);
                                                tvLicenseName.setText(_user.get_contact().get_account().get_currentLicenseNumber().getCommercial_Name());
                                                tvLicenseNumber.setText(_user.get_contact().get_account().get_currentLicenseNumber().getLicense_Number_Value());
                                                tvIssueDate.setText(_user.get_contact().get_account().get_currentLicenseNumber().getLicense_Issue_Date());
                                                tvTotalAmount.setText(jsonObject.getString("Amount__c"));
                                                tvKnowledgeFees.setText(jsonObject.getJSONObject("Knowledge_Fee__r").getString("Amount__c"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onError(Exception exception) {
                                            Utilities.dismissLoadingDialog();
                                        }
                                    });

                                } catch (Exception e) {
                                    onError(e);
                                }
                            }

                            @Override
                            public void onError(Exception exception) {
                                Utilities.dismissLoadingDialog();
                            }
                        });
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void InitializeViews() {
        gson = new Gson();
        _user = gson.fromJson(new StoreData(getApplicationContext()).getUserDataAsString(), User.class);
        tvLicenseName = (TextView) findViewById(R.id.tvLicenseName);
        tvLicenseNumber = (TextView) findViewById(R.id.tvLicenseNumber);
        tvIssueDate = (TextView) findViewById(R.id.tvIssueDate);
        tvKnowledgeFees = (TextView) findViewById(R.id.tvKnowledgeFees);
        tvTotalAmount = (TextView) findViewById(R.id.tvTotalAmount);
        imageBack = (ImageView) findViewById(R.id.imageBack);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnPayAndSubmit = (Button) findViewById(R.id.btnNext);
        btnCancel.setOnClickListener(this);
        btnPayAndSubmit.setOnClickListener(this);
        imageBack.setOnClickListener(this);
        tvErrorMessage = (TextView) findViewById(R.id.tvErrorMessage);
        relativeAllViews = (RelativeLayout) findViewById(R.id.relativeAllViews);
    }

    @Override
    public void onClick(View v) {
        if (v == btnCancel) {
            Utilities.showCustomNiftyDialog("Cancel Process", this, listenerOk1, "Are you sure want to cancel the process ?");
        } else if (v == btnPayAndSubmit) {
            new ClientManager(this, SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(this, new ClientManager.RestClientCallback() {
                @Override
                public void authenticatedRestClient(final RestClient client) {
                    if (client == null) {
                        SalesforceSDKManager.getInstance().logout(LicenseCancellationActivity.this);
                        return;
                    } else {
                        new DoRequest(client, "validateRequestLicenseCancellation").execute();
                    }
                }
            });
        } else if (v == imageBack) {
            Utilities.showCustomNiftyDialog("Cancel Process", this, listenerOk1, "Are you sure want to cancel the process ?");
        }
    }


    private View.OnClickListener listenerOk1 = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            finish();
        }
    };


    public class DoRequest extends AsyncTask<Void, Void, Void> {

        private String actionType;
        private RestClient client;

        public DoRequest(RestClient client, String actionType) {
            this.client = client;
            this.actionType = actionType;
        }

        @Override
        protected void onPreExecute() {
            if (actionType.equals("SubmitRequestLicenseCancellation")) {
                Utilities.showloadingDialog(LicenseCancellationActivity.this, "Paying for request");
            } else {
                Utilities.showloadingDialog(LicenseCancellationActivity.this, "Validating");
            }

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String attUrl = client.getClientInfo().resolveUrl("/services/apexrest/MobileServiceUtilityWebService").toString();
            HttpClient tempClient = new DefaultHttpClient();
            URI theUrl = null;
            try {
                JSONObject parent = new JSONObject();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("AccountId", _user.get_contact().get_account().getID());
                    jsonObject.put("licenseId", _user.get_contact().get_account().get_currentLicenseNumber().getId());
                    jsonObject.put("actionType", actionType);
                    parent.put("wrapper", jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                theUrl = new URI(attUrl);
                HttpPost getRequest = new HttpPost();

                getRequest.setURI(theUrl);
                getRequest.setHeader("Authorization", "Bearer " + client.getAuthToken());
                HttpResponse httpResponse = null;
                StringEntity se = null;
                try {
                    se = new StringEntity(parent.toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                getRequest.setEntity(se);
                try {
                    httpResponse = tempClient.execute(getRequest);
                    StatusLine statusLine = httpResponse.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        HttpEntity httpEntity = httpResponse.getEntity();
                        Log.d("response", httpEntity.toString());
                        if (httpEntity != null) {
                            result = EntityUtils.toString(httpEntity);
                        }
                    } else {
                        httpResponse.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (result.contains("Success")) {
                Utilities.dismissLoadingDialog();
                if (actionType.equals("validateRequestLicenseCancellation")) {
                    actionType = "SubmitRequestLicenseCancellation";
                    new DoRequest(client, actionType).execute();
                } else {
                    relativeAllViews.removeAllViews();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.relativeAllViews, LicenseCancellationThankYouFragment.newInstance("ThankYou", tvTotalAmount.getText().toString()))
                            .commit();
                }
            } else {
                String error = "";
                String[] resultStr = result.substring(1, result.length() - 1).split(",");
                for (int i = 0; i < resultStr.length; i++) {
                    error += resultStr[i] + "\n";
                }
                tvErrorMessage.setVisibility(View.VISIBLE);
                tvErrorMessage.setText(error);
                Utilities.dismissLoadingDialog();
            }
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
