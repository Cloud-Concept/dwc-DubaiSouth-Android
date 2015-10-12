package fragment.License.Renew;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import cloudconcept.dwc.R;
import custom.customdialog.NiftyDialogBuilder;
import fragment.BaseFragmentFiveSteps;
import fragment.BaseFragmentFourStepsNew;
import fragment.License.NOCAttachmentPage;
import fragmentActivity.LicenseActivity;
import fragmentActivity.NOCScreen.ThankYou;
import utilities.Utilities;

/**
 * Created by M_Ghareeb on 9/15/2015.
 */
public class MainRenew extends BaseFragmentFourStepsNew {
LicenseActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity= (LicenseActivity) getActivity();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTitle.setText(activity.getType());
    }



    @Override
    public Fragment getInitialFragment() {
        return SecondFragment.newInstance();
    }

    @Override
    public Fragment getThirdFragment() {
        return NOCAttachmentPage.newInstance("");
    }

    @Override
    public Fragment getFourthFragment() {
        return PayAndSubmit.newInstance();
    }
    @Override
    public Fragment getFifthFragment(String msg, String fee, String mail) {
        return ThankYou.newInstance(msg, fee, mail);
    }

    @Override
    public RelatedServiceType getRelatedService() {
        return null;
    }

    public static Fragment newInstance() {
        MainRenew temp=new MainRenew();
        return temp;
    }

    @Override
    public void onClick(View v) {
        if (v == btnNext) {
             if (getStatus() == 1) {

                    updateCase();

            } else if (getStatus() == 3) {
                if (!isValidAttachments()) {
                    Utilities.showLongToast(activity, "Please fill all attachments");
                } else {
                    super.onClick(v);
                }
            }else if(getStatus()==4)
                Utilities.showCustomNiftyDialog("Pay Process", getActivity(), listenerOkPay, "Are you sure want to Pay for the service ?");
            else {
                super.onClick(v);
            }
        } else if (v == btnBack || v == btnBackTransparent) {

            if (getStatus() == 3) {

            } else if (getStatus() == 4) {
                if (activity.getCompanyDocuments() == null || activity.getCompanyDocuments().size() == 0) {
                    setStatus(3);

                    btnNOC3.setBackground(getActivity().getResources().getDrawable(R.drawable.noc_selector));
                    btnNOC3.setSelected(false);
                    btnNOC3.setTextColor(getActivity().getResources().getColor(R.color.white));
                    btnNOC3.setGravity(Gravity.CENTER);
                    btnNOC3.setText("3");
                    btnNext.setText(("Next"));
                    tvTitle.setText(activity.getType());
                    btnNOC4.setBackground(getActivity().getResources().getDrawable(R.drawable.noc_selector));
                    btnNOC4.setSelected(false);
                    btnNOC4.setTextColor(getActivity().getResources().getColor(R.color.white));
                    btnNOC4.setGravity(Gravity.CENTER);
                    btnNOC4.setText("4");
                }


            }
            super.onClick(v);
        } else {
            super.onClick(v);
        }

    }

    private void updateCase() {


        new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
            @Override
            public void authenticatedRestClient(final RestClient client) {
                if (client == null) {
                    System.exit(0);
                } else {

                    new UpdatecaseFields(client).execute();
//


                }
            }
        });
    }

    private boolean isValidAttachments() {
        if (activity.getCompanyDocuments() != null && activity.getCompanyDocuments().size() > 0) {
            for (int i = 0; i < activity.getCompanyDocuments().size(); i++) {
                if (activity.getCompanyDocuments().get(i).getAttachment_Id__c() == null || activity.getCompanyDocuments().get(i).getAttachment_Id__c().equals("")) {
                    return false;
                }
            }
        } else {
            return false;
        }

        return true;
    }
    private View.OnClickListener listenerOkPay = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
                @Override
                public void authenticatedRestClient(final RestClient client) {
                    if (client == null) {
                        System.exit(0);
                    } else {

                        new GetPickLists(client).execute();
//


                    }
                }
            });

        }
    };
    public class GetPickLists extends AsyncTask<String, Void, String> {

        private final RestClient client;
        String result;

        public GetPickLists(RestClient client) {
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
                tvTitle.setText("Thank You");
                NiftyDialogBuilder
                        .getInstance(getActivity()).dismiss();
                getfifthfragment("", activity.getCaseNumber());
            }

        }
    }

    public class UpdatecaseFields extends AsyncTask<String, Void, String> {

        private final RestClient client;
        String result;

        public UpdatecaseFields(RestClient client) {
            this.client = client;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utilities.showloadingDialog(getActivity());
        }

        @Override
        protected String doInBackground(String... params) {
            String attUrl = client.getClientInfo().resolveUrl("/services/apexrest/MobileRenewLicensetWebService").toString();

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(attUrl);
            httppost.setHeader("Authorization", "Bearer " + client.getAuthToken());
            StringEntity entity = null;
            try {
                Map<String, String> map = new HashMap<String, String>();
                map.put("caseId", activity.getInsertedCaseId());
                map.put("licenseId", activity.getUser().get_contact().get_account().get_currentLicenseNumber().getId());
                entity = new StringEntity("{\"wrapper\":" + new JSONObject(map).toString() + "}", "UTF-8");
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
                performParentNext();
            }

        }
    }
    private void performParentNext() {
        super.onClick(btnNext);
    }
}
