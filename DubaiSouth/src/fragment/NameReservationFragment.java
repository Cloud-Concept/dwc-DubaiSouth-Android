package fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import RestAPI.SFResponseManager;
import cloudconcept.dwc.R;
import custom.customdialog.NiftyDialogBuilder;
import dataStorage.StoreData;
import fragment.nameReservation.NameReservationInitialPage;
import fragment.nameReservation.NameReservationPayAndSubmit;
import fragment.nameReservation.NameReservationThankYou;
import model.Case;
import model.User;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 8/31/2015.
 */
public class NameReservationFragment extends BaseFragmentThreeSteps {

    private String result;
    private User user;
    private RestRequest restRequest;
    private NiftyDialogBuilder builder;

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
            if (getStatus() == 1) {
                if (activity.getChoice1Text().equals("") || activity.getChoice2Text().equals("") || activity.getChoice1Text().equals("")) {
                    Utilities.showToast(activity, "Please fill all required fields");
                } else if (activity.getChoice1Text().equals(activity.getChoice2Text()) || activity.getChoice2Text().equals(activity.getChoice3Text()) || activity.getChoice1Text().equals(activity.getChoice3Text())) {
                    Utilities.showToast(activity, "Please define 3 different names for your reservation company");
                } else {
                    DoMobileServiceUtilityWebService();
                }
            } else if (getStatus() == 2) {
                builder = Utilities.showCustomNiftyDialog("Pay Process", getActivity(), listenerOkPay, "Are you sure want to Pay for the service ?");
            }
        } else {
            super.onClick(v);
        }
    }

    private View.OnClickListener listenerOkPay = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            builder.dismiss();
            Gson gson = new Gson();
            user = gson.fromJson(new StoreData(getActivity().getApplicationContext()).getUserDataAsString(), User.class);
            DoSubmitRequestNameReservation();
        }
    };

    private void DoSubmitRequestNameReservation() {

        new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
            @Override
            public void authenticatedRestClient(final RestClient client) {
                if (client == null) {
                    System.exit(0);
                } else {
                    new SubmitRequestNameReservationTask(client).execute();
                }
            }
        });
    }

    private void DoMobileServiceUtilityWebService() {
        new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
            @Override
            public void authenticatedRestClient(final RestClient client) {
                if (client == null) {
                    System.exit(0);
                } else {
                    new MobileServiceUtilityWebServiceTask(client).execute();
                }
            }
        });
    }

    @Override
    public Fragment getInitialFragment() {
        return NameReservationInitialPage.newInstance("Initial");
    }

    @Override
    public Fragment getSecondFragment() {
        return NameReservationPayAndSubmit.newInstance("Second");
    }

    @Override
    public Fragment getThirdFragment() {
        return NameReservationThankYou.newInstance("Final");
    }

    @Override
    public RelatedServiceType getRelatedService() {
        return RelatedServiceType.RELATED_SERVICE_NAME_RESERVATION;
    }

    public class SubmitRequestNameReservationTask extends AsyncTask<Void, Void, Void> {

        private final RestClient client;

        public SubmitRequestNameReservationTask(RestClient client) {
            this.client = client;
        }

        @Override
        protected void onPreExecute() {
            Utilities.showloadingDialog(getActivity());
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
                    jsonObject.put("choice1", activity.getChoice1Text());
                    jsonObject.put("choice2", activity.getChoice2Text());
                    jsonObject.put("choice3", activity.getChoice3Text());
                    jsonObject.put("AccountId", user.get_contact().get_account().getID());
                    jsonObject.put("actionType", "SubmitRequestNameReservation");
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
                getCaseInfo(client, result.substring(8, result.length() - 1));
            } else {
                Utilities.dismissLoadingDialog();
            }
        }
    }

    public class MobileServiceUtilityWebServiceTask extends AsyncTask<Void, Void, Void> {

        private RestClient client;

        public MobileServiceUtilityWebServiceTask(RestClient client) {
            this.client = client;
        }

        @Override
        protected void onPreExecute() {
            Utilities.showloadingDialog(activity);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String attUrl = client.getClientInfo().resolveUrl("/services/apexrest/MobileServiceUtilityWebService").toString();
            HttpClient tempClient = new DefaultHttpClient();
            URI theUrl = null;
            try {
                // Building post parameters, key and value pair
//                String a = "{" + "choice1:" + activity.getChoice1Text() + "," + "choice2:" + activity.getChoice2Text() + "," + "choice3:" + activity.getChoice3Text() + "," + "actionType:" + "validateRequestNameReservation" + "}";
//                List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
////                nameValuePair.add(new BasicNameValuePair("choice1", activity.getChoice1Text()));
////                nameValuePair.add(new BasicNameValuePair("choice2", activity.getChoice2Text()));
////                nameValuePair.add(new BasicNameValuePair("choice3", activity.getChoice3Text()));
//                nameValuePair.add(new BasicNameValuePair("wrapper", a));
//                List<NameValuePair> pairs = new ArrayList<NameValuePair>(1);
//                String pairString = nameValuePair.toString().replace("[", "{");
//                pairString = pairString.replace("]", "}");
//                pairs.add(new BasicNameValuePair("wrapper", pairString));
                JSONObject parent = new JSONObject();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("choice1", activity.getChoice1Text());
                    jsonObject.put("choice2", activity.getChoice2Text());
                    jsonObject.put("choice3", activity.getChoice3Text());
                    jsonObject.put("actionType", "validateRequestNameReservation");
                    parent.put("wrapper", jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                theUrl = new URI(attUrl);
                HttpPost getRequest = new HttpPost();
//                try {
//                    getRequest.setEntity(new StringEntity(parent.toString(), "UTF-8"));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
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
                PerformParentNext();
            } else {
                Utilities.dismissLoadingDialog();
                NameReservationInitialPage.setErrorMessage(result);
            }
        }
    }

    private void PerformParentNext() {
        if (status == 2) {
            tvTitle.setText("Thank You");
        }
        super.onClick(btnNext);
    }


    public static NameReservationFragment newInstance(String text) {

        NameReservationFragment fragment = new NameReservationFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Fragment", text);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void getCaseInfo(final RestClient client, String caseId) {
        String soql = "select id , CaseNumber , Service_Requested__c , Registration_Amendment__r.Service_Identifier__c , Registration_Amendment__c , Registration_Amendment__r.Require_Fees__c , Invoice__c , Invoice__r.Amount__c  from Case where Id=" + "\'" + caseId + "\'";
        try {
            restRequest = RestRequest.getRequestForQuery(activity.getString(R.string.api_version), soql);
            client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                @Override
                public void onSuccess(RestRequest request, RestResponse response) {
                    Case caseDirectorRemoval = SFResponseManager.parseCaseObject(response.toString());
                    activity.setCaseNumber(caseDirectorRemoval.getCaseNumber());
                    activity.setCaseObject(caseDirectorRemoval);
                    Utilities.dismissLoadingDialog();
                    PerformParentNext();
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
}
