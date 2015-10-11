package fragment.companyDocumentsScreen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import RestAPI.SFResponseManager;
import RestAPI.SoqlStatements;
import adapter.ClickableCustomerDocumentsAdapter;
import cloudconcept.dwc.R;
import custom.expandableView.ExpandableLayoutListView;
import dataStorage.StoreData;
import fragmentActivity.HomeCompanyDocumentsActivity;
import model.Company_Documents__c;
import utilities.CallType;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 9/21/2015.
 */
public class CustomerDocumentsFragment2 extends Fragment {

    private static final String ARG_TEXT = "CustomerDocumentsFragment2";
    private ArrayList<Company_Documents__c> company_documents__cs;
    private static ArrayList<Company_Documents__c> companyDocuments;
    private static ExpandableLayoutListView lstCustomerDocuments;
    private SwipyRefreshLayout mSwipeRefreshLayout;
    private static HomeCompanyDocumentsActivity activity;
    private int offset = 0;
    private int limit = 50;
    private RestRequest restRequest;
    private static ClickableCustomerDocumentsAdapter adapter;
    private int index;
    private int top;

    public static CustomerDocumentsFragment2 newInstance(String text) {
        CustomerDocumentsFragment2 fragment = new CustomerDocumentsFragment2();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TEXT, text);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.certificates_agreements, container, false);
        InitializeViews(view);
        CallCustomerDocumentsService(CallType.FIRSTTIME, offset, limit);
        return view;
    }

    private void InitializeViews(View view) {
        companyDocuments = new ArrayList<>();
        activity = (HomeCompanyDocumentsActivity) getActivity();
        lstCustomerDocuments = (ExpandableLayoutListView) view.findViewById(R.id.lstTrueCopies);
        mSwipeRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    offset = 0;
                    index = 0;
                    top = 0;
                    getListPosition();
                    company_documents__cs.clear();
                    CallCustomerDocumentsService(CallType.REFRESH, offset, limit);
                } else {
                    offset += limit;
                    getListPosition();
                    CallCustomerDocumentsService(CallType.LOADMORE, offset, limit);
                }
            }
        });
    }

    private synchronized void CallCustomerDocumentsService(final CallType method, final int offset, final int limit) {
        if (method == CallType.FIRSTTIME && !new StoreData(getActivity().getApplicationContext()).getCustomerDocumentsResponse().equals("")) {
//            try {
//                JSONArray jsonArray = new JSONArray(new StoreData(getActivity().getApplicationContext()).getCustomerDocumentsResponse());
//                company_documents__cs = new ArrayList<>();
//                for (int i = 0; i < jsonArray.length(); i++) {
//
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                    Company_Documents__c company_documents__c = gson.fromJson(jsonObject.toString(), Company_Documents__c.class);
//                    company_documents__cs.add(company_documents__c);
//                }
            String str = new StoreData(getActivity().getApplicationContext()).getCustomerDocumentsResponse();
            company_documents__cs = (ArrayList<Company_Documents__c>) SFResponseManager.parseCompanyDocumentObject(str, true);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

            companyDocuments.addAll(company_documents__cs);
            adapter = new ClickableCustomerDocumentsAdapter(getActivity(), getActivity().getApplicationContext(),
                    R.layout.company_document_item_row_screen, companyDocuments);
            lstCustomerDocuments.setAdapter(adapter);
            restoreListPosition();
        } else {
            if (method == CallType.FIRSTTIME) {
                if (!Utilities.getIsProgressLoading()) {
                    Utilities.showloadingDialog(getActivity());
                }
            }
            String soql = SoqlStatements.constructCustomerDocumentsQuery(activity.getUser().get_contact().get_account().getID(), limit, offset);
            try {
                restRequest = RestRequest.getRequestForQuery(getString(R.string.api_version), soql);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
                @Override
                public void authenticatedRestClient(RestClient client) {
                    if (client == null) {
                        SalesforceSDKManager.getInstance().logout(getActivity());
                        return;
                    } else {
                        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                            @Override
                            public void onSuccess(RestRequest request, final RestResponse response) {
                                company_documents__cs = (ArrayList<Company_Documents__c>) SFResponseManager.parseCompanyDocumentObject(response.toString(), true);
                                new StoreData(getActivity().getApplicationContext()).saveCustomerDocumentsResponse(response.toString());
                                if (method == CallType.REFRESH || method == CallType.LOADMORE) {
                                    mSwipeRefreshLayout.setRefreshing(false);
                                } else if (method == CallType.FIRSTTIME) {
                                    Utilities.dismissLoadingDialog();
                                }
                                companyDocuments.addAll(company_documents__cs);
                                adapter = new ClickableCustomerDocumentsAdapter(getActivity(), getActivity().getApplicationContext(),
                                        R.layout.company_document_item_row_screen, companyDocuments);
                                lstCustomerDocuments.setAdapter(adapter);
                                restoreListPosition();
                            }

                            @Override
                            public void onError(Exception exception) {
                                Utilities.dismissLoadingDialog();
                            }
                        });
                    }
                }
            });
        }
    }

    public static void setNewCompanyDocuments(Company_Documents__c company_documents__c, int position) {
        companyDocuments.set(position, company_documents__c);
        adapter = new ClickableCustomerDocumentsAdapter(activity, activity.getApplicationContext(),
                R.layout.company_document_item_row_screen, companyDocuments);
        lstCustomerDocuments.setAdapter(adapter);
    }

    public void getListPosition() {
        index = lstCustomerDocuments.getFirstVisiblePosition();
        View v = lstCustomerDocuments.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - lstCustomerDocuments.getPaddingTop());
    }

    public void restoreListPosition() {

        lstCustomerDocuments.setSelectionFromTop(index, top);
    }
}
