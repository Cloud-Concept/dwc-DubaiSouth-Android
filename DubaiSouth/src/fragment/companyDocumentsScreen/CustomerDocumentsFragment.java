package fragment.companyDocumentsScreen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import RestAPI.SFResponseManager;
import RestAPI.SoqlStatements;
import adapter.CustomerDocumentsAdapter;
import cloudconcept.dwc.R;
import fragmentActivity.HomeCompanyDocumentsActivity;
import model.Company_Documents__c;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 9/8/2015.
 */
public class CustomerDocumentsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, UltimateRecyclerView.OnLoadMoreListener {

    private static final String ARG_TEXT = "CompanyDocuments";
    private static ArrayList<Company_Documents__c> companyDocuments;
    //    private Handler mHandler;
    private static HomeCompanyDocumentsActivity activity;
    private static UltimateRecyclerView recyclerView;
    private int offset = 0;
    private int limit = 10;
    private RestRequest restRequest;
    static CustomerDocumentsAdapter mAdapter;
    private ArrayList<Company_Documents__c> company_documents__cs;

    public static CustomerDocumentsFragment newInstance(String text) {
        CustomerDocumentsFragment fragment = new CustomerDocumentsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TEXT, text);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.true_copies, container, false);
        InitializeViews(view);
        offset = 0;
        Utilities.showloadingDialog(getActivity());
        CallCustomerDocumentsService(CallMethod.FIRSTTIME, offset, limit);
        return view;
    }

    private void InitializeViews(View view) {

        activity = (HomeCompanyDocumentsActivity) getActivity();
        recyclerView = (UltimateRecyclerView) view.findViewById(R.id.lstTrueCopies);
        recyclerView.enableLoadmore();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setDefaultOnRefreshListener(this);
        recyclerView.setOnLoadMoreListener(this);
        companyDocuments = new ArrayList<>();
    }

    private synchronized void CallCustomerDocumentsService(final CallMethod method, final int offset, final int limit) {

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        Gson gson = new Gson();
        String soql = SoqlStatements.constructCustomerDocumentsQuery(activity.getUser().get_contact().get_account().getID(), offset, limit);
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
                            company_documents__cs = (ArrayList<Company_Documents__c>) SFResponseManager.parseCompanyDocumentObject(response.toString());
                            if (method == CallMethod.REFRESH) {
                                recyclerView.setRefreshing(false);
                            } else if (method == CallMethod.FIRSTTIME) {
                                Utilities.dismissLoadingDialog();
                            }
                            if (mAdapter == null) {
                                companyDocuments.addAll(company_documents__cs);
                                mAdapter = new CustomerDocumentsAdapter(getActivity(), getActivity().getApplicationContext(), companyDocuments);
                                recyclerView.setAdapter(mAdapter);
                                if (company_documents__cs.size() <= limit) {
                                    recyclerView.disableLoadmore();
                                }
                            } else {
                                if (company_documents__cs.size() == 0) {
                                    recyclerView.disableLoadmore();
                                } else {
                                    boolean temp = mAdapter.addAll(company_documents__cs);
                                    if (!temp) {
                                        recyclerView.disableLoadmore();
                                    }
                                }
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
//            }
//        }).start();
    }

    @Override
    public void onRefresh() {
        offset = 0;
        CallCustomerDocumentsService(CallMethod.REFRESH, offset, limit);
    }

    public static void setNewCompanyDocuments(Company_Documents__c company_documents__c, int position) {
        companyDocuments.set(position, company_documents__c);
        mAdapter = new CustomerDocumentsAdapter(activity, activity.getApplicationContext(), companyDocuments);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void loadMore(int i, int i1) {
        offset += limit;
        CallCustomerDocumentsService(CallMethod.LOADMORE, offset, limit);
    }

    enum CallMethod {
        REFRESH,
        LOADMORE,
        FIRSTTIME
    }
}
