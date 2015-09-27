package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import RestAPI.SFResponseManager;
import adapter.CompanyDocumentsActivityAdapter;
import cloudconcept.dwc.R;
import dataStorage.StoreData;
import exceptionHandling.ExceptionHandler;
import model.Attachment;
import model.Company_Documents__c;
import model.User;
import utilities.CallType;
import utilities.Utilities;

public class CompanyDocumentsActivity extends Activity implements View.OnClickListener {

    ListView lstCompanyDocuments;
    SwipyRefreshLayout mSwipeRefreshLayout;
    Gson gson;
    User user;
    private RestRequest restRequest;
    private String soql;
    ImageView imageBack;
    TextView tvBack;
    Company_Documents__c company_document;
    String parentIds = "";
    Set<Company_Documents__c> company_documents;
    ArrayList<Company_Documents__c> _filteredCompanyDocuments;
    int limit = 50;
    int Mainoffset = 0;
    private RestClient client;
    private ArrayList<Attachment> attachments;
    private CompanyDocumentsActivityAdapter adapter;
    int Docposition;
    boolean isFirstTime = true;

//    String soql = "SELECT Id, Name, Customer_Document__c, Attachment_Id__c, Version__c, CreatedDate, Document_Type__c, Party__r.Id, Party__r.Name, RecordType.Id, RecordType.Name, RecordType.DeveloperName, RecordType.SObjectType, Original_Verified__c, Original_Collected__c, Required_Original__c, Verified_Scan_Copy__c, Uploaded__c, Required_Scan_copy__c FROM Company_Documents__C WHERE Company__c = "+""+" ORDER BY CreatedDate LIMIT %%d OFFSET %%d"


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.company_documents);
        InitializeViews();
        ValidateRequest();
    }

    private void InitializeViews() {

        company_documents = new HashSet<Company_Documents__c>();
        _filteredCompanyDocuments = new ArrayList<Company_Documents__c>();
        attachments = new ArrayList<Attachment>();
        mSwipeRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        lstCompanyDocuments = (ListView) findViewById(R.id.lstCompanyDocuments);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    Mainoffset = 0;
                    company_documents.clear();
                    _filteredCompanyDocuments.clear();
                    DoCompanyDocumentQuery(CallType.REFRESH, limit, Mainoffset);
                } else {
                    Mainoffset += limit;
                    DoCompanyDocumentQuery(CallType.LOADMORE, limit, Mainoffset);
                }
            }
        });

        imageBack = (ImageView) findViewById(R.id.imageBack);
        tvBack = (TextView) findViewById(R.id.tvBack);
        imageBack.setOnClickListener(this);
        tvBack.setOnClickListener(this);
        Docposition = getIntent().getExtras().getInt("position");
    }

    private void ValidateRequest() {
        gson = new Gson();
        company_document = gson.fromJson(getIntent().getExtras().getString("company_documents__c"), Company_Documents__c.class);
        user = gson.fromJson(new StoreData(getApplicationContext()).getUserDataAsString(), User.class);
        DoCompanyDocumentQuery(CallType.FIRSTTIME, limit, Mainoffset);
    }

    private void DoCompanyDocumentQuery(final CallType serviceCall, final int limit, final int offset) {
        DoRequest(serviceCall, limit, offset);
    }

    private void DoRequest(final CallType serviceCall, final int limit, final int offset) {
        if (serviceCall == CallType.FIRSTTIME && !new StoreData(getApplicationContext()).getCompanyDocumentFromAttachment().equals("")) {
            company_documents.addAll(SFResponseManager.parseCompanyDocumentObject(new StoreData(getApplicationContext()).getCompanyDocumentFromAttachment(), true));
            for (Company_Documents__c company_documents__c : company_documents) {
                if (company_documents__c.getAttachment_Id__c() != null && !company_documents__c.getAttachment_Id__c().equals("")) {
                    _filteredCompanyDocuments.add(company_documents__c);
                }
            }
            adapter = new CompanyDocumentsActivityAdapter(getApplicationContext(), _filteredCompanyDocuments);
            lstCompanyDocuments.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            lstCompanyDocuments.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    gson = new Gson();
                    company_document.setAttachment_Id__c(_filteredCompanyDocuments.get(position).getAttachment_Id__c());
                    company_document.setHasAttachment(true);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", gson.toJson(company_document));
                    returnIntent.putExtra("position", Docposition);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            });

        } else {

            soql = "SELECT Id, Name, Customer_Document__c, Attachment_Id__c, Version__c, CreatedDate, Document_Type__c, Party__r.Id, Party__r.Name, RecordType.Id, RecordType.Name, RecordType.DeveloperName, RecordType.SObjectType, Original_Verified__c, Original_Collected__c, Required_Original__c, Verified_Scan_Copy__c, Uploaded__c, Required_Scan_copy__c FROM Company_Documents__C WHERE Company__c = " + "\'" + user.get_contact().get_account().getID() + "\'" + " LIMIT " + limit + " OFFSET " + offset;
            try {
                restRequest = RestRequest.getRequestForQuery(getString(R.string.api_version), soql);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (serviceCall == CallType.FIRSTTIME && !Utilities.getIsProgressLoading()) {
                Utilities.showloadingDialog(this);
            }

            new ClientManager(CompanyDocumentsActivity.this, SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(CompanyDocumentsActivity.this, new ClientManager.RestClientCallback() {
                @Override
                public void authenticatedRestClient(final RestClient client) {
                    if (client == null) {
                        SalesforceSDKManager.getInstance().logout(CompanyDocumentsActivity.this);
                        return;
                    } else {
                        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                            @Override
                            public void onSuccess(RestRequest request, RestResponse response) {
                                company_documents.addAll(SFResponseManager.parseCompanyDocumentObject(response.toString(), true));
                                for (Company_Documents__c company_documents__c : company_documents) {
                                    if (company_documents__c.getAttachment_Id__c() != null && !company_documents__c.getAttachment_Id__c().equals("")) {
                                        _filteredCompanyDocuments.add(company_documents__c);
                                    }
                                }
                                if (_filteredCompanyDocuments.size() > 0) {
                                    new StoreData(getApplicationContext()).setCompanyDocumentFromAttachment(response.toString());
                                    if (serviceCall == CallType.FIRSTTIME) {
                                        Utilities.dismissLoadingDialog();
                                    } else {
                                        mSwipeRefreshLayout.setRefreshing(false);
                                    }
                                    adapter = new CompanyDocumentsActivityAdapter(getApplicationContext(), _filteredCompanyDocuments);
                                    lstCompanyDocuments.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    lstCompanyDocuments.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            gson = new Gson();
                                            company_document.setAttachment_Id__c(_filteredCompanyDocuments.get(position).getAttachment_Id__c());
                                            company_document.setHasAttachment(true);
                                            Intent returnIntent = new Intent();
                                            returnIntent.putExtra("result", gson.toJson(company_document));
                                            returnIntent.putExtra("position", Docposition);
                                            setResult(RESULT_OK, returnIntent);
                                            finish();
                                        }
                                    });
                                } else {
                                    final int newoffset = offset + 50;
                                    DoRequest(serviceCall, limit, newoffset);
                                }
                            }

                            @Override
                            public void onError(Exception exception) {
                                Utilities.showToast(CompanyDocumentsActivity.this, "Please Check Your internet connection");
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(View v) {
        if (v == imageBack || v == tvBack) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", "");
            returnIntent.putExtra("position", -1);
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
    }
}