package adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
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
import java.util.ArrayList;

import RestAPI.SFResponseManager;
import cloudconcept.dwc.R;
import dataStorage.StoreData;
import model.Card_Management__c;
import model.Case;
import model.Company_Documents__c;
import model.Contract_DWC__c;
import model.Directorship;
import model.EServices_Document_Checklist__c;
import model.LegalRepresentative;
import model.ManagementMember;
import model.ServiceItem;
import model.ShareOwnership;
import model.User;
import model.Visa;
import utilities.ActivitiesLauncher;
import utilities.Utilities;

/**
 * Created by Abanoub on 6/26/2015.
 */
public class HorizontalListViewAdapter extends BaseAdapter {

    private Activity activity;
    Object object;
    Gson gson;
    String objectAsString = "";
    private Context context;
    private ArrayList<ServiceItem> _items;
    private static int RESULT_LOAD_IMG_FROM_GALLERY = 2;
    private static int RESULT_LOAD_IMG_FROM_CAMERA = 3;
    private RestRequest restRequest;

    public boolean isInflated() {
        return inflated;
    }

    private boolean inflated;

    public HorizontalListViewAdapter(Object object, Activity activity, Context context, ArrayList<ServiceItem> _items) {
        this._items = _items;
        this.context = context;
        this.object = object;
        inflated = false;
        this.activity = activity;
    }

    public ArrayList<ServiceItem> getServiceItems() {
        return this._items;
    }

    public Object getObject() {
        return this.object;
    }


    @Override
    public int getCount() {
        return _items.size();
    }

    @Override
    public Object getItem(int position) {
        return _items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.horizontal_item_row, null);
        }

        ImageView ivServiceDrawable;
        final TextView tvServiceName;

        ivServiceDrawable = (ImageView) convertView.findViewById(R.id.ivServiceDrawable);
        tvServiceName = (TextView) convertView.findViewById(R.id.tvServiceName);

        ivServiceDrawable.setImageResource(_items.get(position).getDrawableIcon());
        tvServiceName.setText(_items.get(position).getTvServiceName());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (object instanceof Visa) {
                    Visa _visa = (Visa) object;
                    gson = new Gson();
                    objectAsString = gson.toJson(_visa);
                    if (tvServiceName.getText().toString().equals("Show Details")) {
                        ActivitiesLauncher.openShowDetailsActivity(context, "Visa Details", objectAsString, "Visa");
                    } else if (tvServiceName.getText().toString().equals("New NOC")) {
                        ActivitiesLauncher.openNOCActivity(context, objectAsString, "Visa");
                    } else if (tvServiceName.getText().toString().equals("Renew Visa")) {
                        ActivitiesLauncher.openVisaActivity(context, _visa, "renew");
                    } else if (tvServiceName.getText().toString().equals("Cancel Visa")) {
                        ActivitiesLauncher.openVisaActivity(context, _visa, "Cancel");
                    } else if (tvServiceName.getText().toString().equals("Renew Passport")) {
                        ActivitiesLauncher.openVisaActivity(context, _visa, "Passport");
                    }
                } else if (object instanceof Card_Management__c) {
                    Card_Management__c card = (Card_Management__c) object;
                    if (tvServiceName.getText().toString().equals("Cancel Card")) {
                        ActivitiesLauncher.openCardActivity(context, card, "2");
                    } else if (tvServiceName.getText().toString().equals("Renew Card")) {
                        ActivitiesLauncher.openCardActivity(context, card, "3");
                    } else if (tvServiceName.getText().toString().equals("Replace Card")) {
                        ActivitiesLauncher.openCardActivity(context, card, "4");
                    } else if (tvServiceName.getText().toString().equals("Show Details")) {
                        ActivitiesLauncher.openAccessCardShowDetailsActivity(context, card);
                    }
                } else if (object instanceof ShareOwnership) {
                    ShareOwnership shareHolder = (ShareOwnership) object;
                    if (tvServiceName.getText().toString().equals("Share Transfer")) {
                        ActivitiesLauncher.openShareHolderActivity(context, shareHolder, "2", _items.get(position).getObjects());
                    } else if (tvServiceName.getText().toString().equals("Show Details")) {
                        ActivitiesLauncher.openShareHolderShowDetailsActivity(context, shareHolder);
                    }
                } else if (object instanceof EServices_Document_Checklist__c) {
                    EServices_Document_Checklist__c eServices_document_checklist__c = (EServices_Document_Checklist__c) object;
                    if (tvServiceName.getText().toString().equals("Preview")) {
                        ActivitiesLauncher.openCustomerDocumentsPreviewActivity(context, eServices_document_checklist__c);
                    } else if (tvServiceName.getText().toString().equals("Request True Copy")) {
                        ActivitiesLauncher.openCompanyDocumentsRequestTrueCopyActivity(context, eServices_document_checklist__c);
                    }
                } else if (object instanceof Company_Documents__c) {
                    final Company_Documents__c company_documents__c = (Company_Documents__c) object;
                    if (tvServiceName.getText().toString().equals("Preview")) {
                        ActivitiesLauncher.openCustomerDocumentsPreviewActivity(context, company_documents__c);
                    } else if (tvServiceName.getText().toString().equals("Edit")) {
                        new BottomSheet.Builder(activity).title("Choose Edit Option").sheet(R.menu.customer_document_list).listener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case R.id.camera:
                                        loadImagefromCamera(position, company_documents__c);
                                        break;
                                    case R.id.gallery:
                                        loadImagefromGallery(position, company_documents__c);
                                        break;
                                }
                            }
                        }).show();
                    }
                } else if (object instanceof User) {
                    final User _user = (User) object;
                    if (tvServiceName.getText().toString().equals("Cancel License")) {
                        ActivitiesLauncher.openLicenseCancellationActivity(context);
                    } else if (tvServiceName.getText().toString().equals("Change License Activity")) {
                        ActivitiesLauncher.openChangeAndRemovalLicenceActivity(context, _user, "Change License Activity");
                    } else if (tvServiceName.getText().toString().equals("Renew License Activity")) {
                        ActivitiesLauncher.openChangeAndRemovalLicenceActivity(context, _user, "License Renewal");
                    } else if (tvServiceName.getText().toString().equals("License Renewal")) {
                        ActivitiesLauncher.openChangeAndRemovalLicenceActivity(context, _user, "Renewal License");
                    } else if (tvServiceName.getText().toString().equals("New NOC")) {
                        ActivitiesLauncher.openCompanyNocActivity(context);
                    } else if (tvServiceName.getText().toString().equals("Address Change")) {
                        ActivitiesLauncher.openGenericChangeAndRemovalActivity(context, "Address Change", object);
                    } else if (tvServiceName.getText().toString().equals("Name Change")) {
                        ActivitiesLauncher.openGenericChangeAndRemovalActivity(context, "Name Change", object);
                    } else if (tvServiceName.getText().toString().equals("Change Capital")) {
                        ActivitiesLauncher.openGenericChangeAndRemovalActivity(context, "Change Capital", object);
                    } else if (tvServiceName.getText().toString().equals("Reserve Name")) {
                        ActivitiesLauncher.openNameReservationActivity(context);
                    } else if (tvServiceName.getText().toString().equals("Renew Card")) {
                        ActivitiesLauncher.openGenericChangeAndRemovalActivity(context, "Renew Card", object);
                    } else if (tvServiceName.getText().toString().equals("Lost Card")) {
                        ActivitiesLauncher.openGenericChangeAndRemovalActivity(context, "Lost Card", object);
                    } else if (tvServiceName.getText().toString().equals("Cancel Card")) {
                        ActivitiesLauncher.openGenericChangeAndRemovalActivity(context, "Cancel Card", object);
                    }
                } else if (object instanceof Contract_DWC__c) {
                    final Contract_DWC__c contract_dwc__c = (Contract_DWC__c) object;
                    if (tvServiceName.getText().toString().equals("Show Details")) {
                        ActivitiesLauncher.openLeasingShowDetailsActivity(context, contract_dwc__c);

                    } else if (tvServiceName.getText().toString().equals("Renew Contract")) {
                        //Not BC Contract
                        new ClientManager(activity, SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(activity, new ClientManager.RestClientCallback() {
                            @Override
                            public void authenticatedRestClient(final RestClient client) {
                                if (client == null) {
                                    SalesforceSDKManager.getInstance().logout(activity);
                                    return;
                                } else {
                                    new DoRenewRequest(client, "CreateNonBCContractRenewalRequest", contract_dwc__c).execute();
                                }
                            }
                        });
                    } else if (tvServiceName.getText().toString().equals("Renew BC Contract")) {
                        //BC Contract
                        new ClientManager(activity, SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(activity, new ClientManager.RestClientCallback() {
                            @Override
                            public void authenticatedRestClient(final RestClient client) {
                                if (client == null) {
                                    SalesforceSDKManager.getInstance().logout(activity);
                                    return;
                                } else {
                                    new DoRenewRequest(client, "CreateBCContractRenewalRequest", contract_dwc__c).execute();
                                }
                            }
                        });
                    }
                } else if (object instanceof Directorship) {
                    final Directorship directorship = (Directorship) object;
//                    final Directorship _user = (Directorship) object;
                    if (tvServiceName.getText().toString().equals("Show Details")) {
//                        ActivitiesLauncher.openLicenseCancellationActivity(context);
                        ActivitiesLauncher.openDirectorShowDetailsActivity(context, directorship);
                    } else if (tvServiceName.getText().toString().equals("Remove Director")) {
                        ActivitiesLauncher.openGenericChangeAndRemovalActivity(context, "Remove Director", object);
                    }
                } else if (object instanceof LegalRepresentative) {
                    final LegalRepresentative legalRepresentative = (LegalRepresentative) object;
                    if (tvServiceName.getText().toString().equals("Show Details")) {
                        ActivitiesLauncher.openLegalRepresentativesShowDetailsActivity(context, legalRepresentative);
                    }
                } else if (object instanceof ManagementMember) {
                    final ManagementMember managementMember = (ManagementMember) object;
                    if (tvServiceName.getText().toString().equals("Show Details")) {
                        ActivitiesLauncher.openGeneralManagersShowDetailsActivity(context, managementMember);
                    }
                }
            }
        });

        return convertView;
    }


    public class DoRenewRequest extends AsyncTask<Void, Void, Void> {

        private String actionType;
        private RestClient client;
        User _user;
        Gson gson;
        Contract_DWC__c contract_dwc__c;
        private String result;

        public DoRenewRequest(RestClient client, String actionType, Contract_DWC__c contract_dwc__c) {
            this.client = client;
            this.actionType = actionType;
            gson = new Gson();
            _user = gson.fromJson(new StoreData(context).getUserDataAsString(), User.class);
            this.contract_dwc__c = contract_dwc__c;
        }

        @Override
        protected void onPreExecute() {
            Utilities.showloadingDialog(activity, "Renewing Contract....");
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
                    jsonObject.put("contractId", contract_dwc__c.getID());
                    if (contract_dwc__c.IS_BC_Contract__c()) {
                        jsonObject.put("QuoteId", contract_dwc__c.getQuote().getId());
                    }
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
                new ClientManager(activity, SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(activity, new ClientManager.RestClientCallback() {
                    @Override
                    public void authenticatedRestClient(final RestClient client) {
                        if (client == null) {
                            SalesforceSDKManager.getInstance().logout(activity);
                            return;
                        } else {
                            getCaseInfo(result.substring(8, result.length() - 1), client);
                        }
                    }
                });

            } else {
                Utilities.dismissLoadingDialog();
            }
        }
    }


    private void getCaseInfo(String caseId, final RestClient client) {
        String soql = "select id , CaseNumber , Service_Requested__c , Registration_Amendment__r.Service_Identifier__c , Registration_Amendment__c , Registration_Amendment__r.Require_Fees__c , Invoice__c , Invoice__r.Amount__c  from Case where Id=" + "\'" + caseId + "\'";
        try {
            restRequest = RestRequest.getRequestForQuery(activity.getString(R.string.api_version), soql);
            client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                @Override
                public void onSuccess(RestRequest request, RestResponse response) {
                    Utilities.dismissLoadingDialog();
                    Case _case = SFResponseManager.parseCaseObject(response.toString());
                    String message = String.format(activity.getString(R.string.ServiceThankYouReservation), _case.getCaseNumber());
                    ActivitiesLauncher.openThankYouActivity(activity, context, message);
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


    public void loadImagefromGallery(int position, Company_Documents__c company_documents__c) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        gson = new Gson();
        new StoreData(context).setCompanyDocumentObject(gson.toJson(company_documents__c));
        new StoreData(context).setCompanyDocumentPosition(position);
        activity.startActivityForResult(galleryIntent, RESULT_LOAD_IMG_FROM_GALLERY);
    }

    public void loadImagefromCamera(int position, Company_Documents__c company_documents__c) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        gson = new Gson();
        new StoreData(context).setCompanyDocumentObject(gson.toJson(company_documents__c));
        new StoreData(context).setCompanyDocumentPosition(position);
        activity.startActivityForResult(cameraIntent, RESULT_LOAD_IMG_FROM_CAMERA);
    }

    public void setInflated(boolean b) {
        this.inflated = b;
    }

    public boolean getInflated() {
        return this.inflated;
    }
}
