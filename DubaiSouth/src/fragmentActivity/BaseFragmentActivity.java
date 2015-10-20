package fragmentActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import RestAPI.RestMessages;
import cloudconcept.dwc.R;
import dataStorage.StoreData;
import fragment.AttachmentPage;
import fragment.License.NOCAttachmentPage;
import model.Company_Documents__c;
import model.Receipt_Template__c;
import model.User;
import model.WebForm;
import utilities.AutomaticUtilities;
import utilities.Utilities;

/**
 * Created by M_Ghareeb on 8/25/2015.
 */
public class BaseFragmentActivity extends FragmentActivity {
    public static final int RESULT_LOAD_IMG_FROM_GALLERY = 2;
    public static final int RESULT_LOAD_IMG_FROM_CAMERA = 3;
    private WebForm _webForm;
    private String insertedServiceId = null;
    public String getInsertedServiceId() {
        return insertedServiceId;
    }

    public void setInsertedServiceId(String insertedServiceId) {
        this.insertedServiceId = insertedServiceId;
    }

    public WebForm get_webForm() {
        return _webForm;
    }

    public void set_webForm(WebForm _webForm) {
        this._webForm = _webForm;
    }
     Map<String, Object> caseFields = new HashMap<String, Object>();
    public Map<String, Object> getCaseFields() {
        return caseFields;
    }

    public void setCaseFields(Map<String, Object> caseFields) {
        this.caseFields = caseFields;
    }
     Gson gson;
    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }
    private RestRequest restRequest;

    public RestRequest getRestRequest() {
        return restRequest;
    }

    public void setRestRequest(RestRequest restRequest) {
        this.restRequest = restRequest;
    }

    User user;
    private ArrayList<Company_Documents__c> companyDocuments;


    public ArrayList<Company_Documents__c> getCompanyDocuments() {
        return companyDocuments;
    }

    public void setCompanyDocuments(ArrayList<Company_Documents__c> companyDocuments) {
        this.companyDocuments = companyDocuments;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String type="";
    private String insertedCaseId = null;
    public String getInsertedCaseId() {
        return insertedCaseId;
    }

    public void setInsertedCaseId(String insertedCaseId) {
        this.insertedCaseId = insertedCaseId;
    }
    String total;
    public Company_Documents__c company_documents__c;
    public Receipt_Template__c geteServiceAdministration() {
        return eServiceAdministration;
    }

    public void seteServiceAdministration(Receipt_Template__c eServiceAdministration) {
        this.eServiceAdministration = eServiceAdministration;
    }

    private Receipt_Template__c eServiceAdministration;
    @Override
    public void onBackPressed() {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Utilities.showloadingDialog(this, "Uploading ....");
                company_documents__c = new Company_Documents__c();
                String result = data.getStringExtra("result");
                int position = data.getIntExtra("position", -1);
                company_documents__c = gson.fromJson(result, Company_Documents__c.class);
                ConnectAttachmentWithCompanyDocument(company_documents__c, position);
            } else if (resultCode == RESULT_CANCELED) {
                int position = data.getIntExtra("position", -1);
                AttachmentPage.setReturnedCompnayDocument(company_documents__c, position);
            }
        } else if (requestCode == RESULT_LOAD_IMG_FROM_GALLERY) {
            if (resultCode == RESULT_OK) {
                Utilities.showloadingDialog(this, "Uploading ....");
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                File file = new File(picturePath);
                Bitmap myBitmap = Utilities.decodeFile(file, 300, 300);

                String encodedImage = Utilities.encodeTobase64(myBitmap);
                cursor.close();
                int position = Integer.valueOf(new StoreData(getApplicationContext()).getCompanyDocumentPosition());
                String result = new StoreData(getApplicationContext()).getCompanyDocumentObject();
                company_documents__c = gson.fromJson(result, Company_Documents__c.class);
                UploadCompanyDocumentAttachment(file.getName(), company_documents__c, position, encodedImage);
            } else if (resultCode == RESULT_CANCELED) {
                int position = Integer.valueOf(new StoreData(getApplicationContext()).getCompanyDocumentPosition());
                String result = new StoreData(getApplicationContext()).getCompanyDocumentObject();
                company_documents__c = gson.fromJson(result, Company_Documents__c.class);
               AttachmentPage.setReturnedCompnayDocument(company_documents__c, position);
            }

        } else if (requestCode == RESULT_LOAD_IMG_FROM_CAMERA) {
            if (resultCode == RESULT_OK) {
                Utilities.showloadingDialog(this, "Uploading ....");
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                File file = new File(picturePath);
                cursor.close();
//                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Bitmap myBitmap = Utilities.decodeFile(file, 300, 300);
//                photo = Utilities.encodeTobase64(photo);

//                ByteArrayOutputStream bao = new ByteArrayOutputStream();
//                photo.compress(Bitmap.CompressFormat.JPEG, 100, bao);
//                byte[] ba = bao.toByteArray();
                String encodedImage = Utilities.encodeTobase64(myBitmap);
                int position = Integer.valueOf(new StoreData(getApplicationContext()).getCompanyDocumentPosition());
                String result = new StoreData(getApplicationContext()).getCompanyDocumentObject();
                company_documents__c = gson.fromJson(result, Company_Documents__c.class);
                UploadCompanyDocumentAttachment(file.getName(), company_documents__c, position, encodedImage);
            } else if (resultCode == RESULT_CANCELED) {
                int position = Integer.valueOf(new StoreData(getApplicationContext()).getCompanyDocumentPosition());
                String result = new StoreData(getApplicationContext()).getCompanyDocumentObject();
                company_documents__c = gson.fromJson(result, Company_Documents__c.class);
                AttachmentPage.setReturnedCompnayDocument(company_documents__c, position);
            }
        }
    }

    private void ConnectAttachmentWithCompanyDocument(final Company_Documents__c company_documents__c, final int position) {
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("Attachment_Id__c", company_documents__c.getAttachment_Id__c());
        try {
            restRequest = RestRequest.getRequestForUpdate(getString(R.string.api_version), "Company_Documents__c", company_documents__c.getId(), fields);
            new ClientManager(this, SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(this, new ClientManager.RestClientCallback() {
                @Override
                public void authenticatedRestClient(final RestClient client) {
                    if (client == null) {
                        SalesforceSDKManager.getInstance().logout(BaseFragmentActivity.this);
                        return;
                    } else {
                        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                            @Override
                            public void onSuccess(RestRequest request, RestResponse result) {
                                Utilities.dismissLoadingDialog();
                                Log.d("", result.toString());
                                if (company_documents__c.getName().startsWith("Person Photo")) {
                                    Map<String, Object> field = new HashMap<String, Object>();
                                    field.put("Personal_photo__c", company_documents__c.getAttachment_Id__c());
                                    try {
                                        restRequest = RestRequest.getRequestForUpdate(getString(R.string.api_version), get_webForm().getObject_Name(), getInsertedServiceId(), field);
                                        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {

                                            @Override
                                            public void onSuccess(RestRequest request, RestResponse response) {

                                            }

                                            @Override
                                            public void onError(Exception exception) {

                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                                AttachmentPage.setReturnedCompnayDocument(company_documents__c, position);
                            }

                            @Override
                            public void onError(Exception exception) {
                                Utilities.dismissLoadingDialog();
                                Utilities.showToast(BaseFragmentActivity.this, RestMessages.getInstance().getErrorMessage());
                                VolleyError volleyError = (VolleyError) exception;
                                NetworkResponse response = volleyError.networkResponse;
                                String json = new String(response.data);
                                Log.d("", json);
                            }
                        });

//                        String attUrl = client.getClientInfo().resolveUrl("/services/data/v26.0/sobjects/attachment").toString();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void UploadCompanyDocumentAttachment(String filename, final Company_Documents__c company_documents__c, final int position, String encodedImage) {
        caseFields = new HashMap<String, Object>();
        caseFields.put("Name", filename);
        caseFields.put("ContentType", "image");
        caseFields.put("ParentId", company_documents__c.getId());
        caseFields.put("Body", encodedImage);
        try {
            restRequest = RestRequest.getRequestForCreate(getString(R.string.api_version), "Attachment", caseFields);
            new ClientManager(this, SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(this, new ClientManager.RestClientCallback() {
                @Override
                public void authenticatedRestClient(final RestClient client) {
                    if (client == null) {
                        SalesforceSDKManager.getInstance().logout(BaseFragmentActivity.this);
                        return;
                    } else {
                        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                            @Override
                            public void onSuccess(RestRequest request, RestResponse result) {
                                Log.d("", result.toString());
                                try {
                                    JSONObject jsonObject = new JSONObject(result.toString());
                                    company_documents__c.setAttachment_Id__c(jsonObject.getString("id"));
                                    company_documents__c.setHasAttachment(true);
                                    ConnectAttachmentWithCompanyDocument(company_documents__c, position);
//                                    NOCAttachmentPage.setReturnedCompnayDocument(company_documents__c, position);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Exception exception) {
                                Utilities.showToast(BaseFragmentActivity.this, RestMessages.getInstance().getErrorMessage());
                                Utilities.dismissLoadingDialog();
                            }
                        });

//                        String attUrl = client.getClientInfo().resolveUrl("/services/data/v26.0/sobjects/attachment").toString();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Recursion

    public void ConnectAttachmentWithCompanyDocument(final List< Company_Documents__c> company_documents__c, final int position) {
        Log.d("Work With " + position, company_documents__c.get(position).getName());

        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("Attachment_Id__c", company_documents__c.get(position).getAttachment_Id__c());
        try {
            restRequest = RestRequest.getRequestForUpdate(getString(R.string.api_version), "Company_Documents__c", company_documents__c.get(position).getId(), fields);
            new ClientManager(this, SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(this, new ClientManager.RestClientCallback() {
                @Override
                public void authenticatedRestClient(final RestClient client) {
                    if (client == null) {
                        SalesforceSDKManager.getInstance().logout(BaseFragmentActivity.this);
                        return;
                    } else {
                        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                            @Override
                            public void onSuccess(RestRequest request, RestResponse result) {
                                Utilities.dismissLoadingDialog();
                                Log.d("in success", company_documents__c.get(position).getName() + "   " + result.toString());
                                if (company_documents__c.get(position).getName().startsWith("Person Photo")) {
                                    Map<String, Object> field = new HashMap<String, Object>();
                                    field.put("Personal_photo__c", company_documents__c.get(position).getAttachment_Id__c());
                                    try {
                                        restRequest = RestRequest.getRequestForUpdate(getString(R.string.api_version), get_webForm().getObject_Name(), getInsertedServiceId(), field);
                                        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {

                                            @Override
                                            public void onSuccess(RestRequest request, RestResponse response) {

                                            }

                                            @Override
                                            public void onError(Exception exception) {

                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }



                                }

                                if(position!=(company_documents__c.size()-1)){
                                    if(company_documents__c.get((position+1)).getAttachment_Id__c()!=null){

                                        ConnectAttachmentWithCompanyDocument(company_documents__c,(position+1));
                                    }
                                }
                            }

                            @Override
                            public void onError(Exception exception) {
                                Utilities.dismissLoadingDialog();
                                Utilities.showToast(BaseFragmentActivity.this, RestMessages.getInstance().getErrorMessage());
                                VolleyError volleyError = (VolleyError) exception;
                                NetworkResponse response = volleyError.networkResponse;
                                String json = new String(response.data);
                                Log.d("error", json);
                            }
                        });

//                        String attUrl = client.getClientInfo().resolveUrl("/services/data/v26.0/sobjects/attachment").toString();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
