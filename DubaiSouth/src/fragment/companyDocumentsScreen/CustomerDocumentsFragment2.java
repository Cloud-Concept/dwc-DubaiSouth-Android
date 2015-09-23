package fragment.companyDocumentsScreen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
import fragmentActivity.HomeCompanyDocumentsActivity;
import model.Company_Documents__c;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 9/21/2015.
 */
public class CustomerDocumentsFragment2 extends Fragment {

    private static final String ARG_TEXT = "CustomerDocumentsFragment2";
    private ArrayList<Company_Documents__c> company_documents__cs;
    private static ArrayList<Company_Documents__c> companyDocuments;
    private static ListView lstCustomerDocuments;
    private SwipyRefreshLayout mSwipeRefreshLayout;
    private static HomeCompanyDocumentsActivity activity;
    private int offset = 0;
    private int limit = 10;
    private boolean iscalledFromRefresh = false;
    private RestRequest restRequest;
    private static ClickableCustomerDocumentsAdapter adapter;

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
        Utilities.showloadingDialog(getActivity());
        CallCustomerDocumentsService(CallMethod.FIRSTTIME, offset, limit);
        return view;
    }

    private void InitializeViews(View view) {
        activity = (HomeCompanyDocumentsActivity) getActivity();
        lstCustomerDocuments = (ListView) view.findViewById(R.id.lstTrueCopies);
        mSwipeRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.d("MainActivity", "Refresh triggered at "
                        + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    iscalledFromRefresh = true;
                    offset = 0;
                    CallCustomerDocumentsService(CallMethod.REFRESH, offset, limit);
                } else {
                    iscalledFromRefresh = false;
                    offset += limit;
                    CallCustomerDocumentsService(CallMethod.LOADMORE, offset, limit);
                }
            }
        });
        companyDocuments = new ArrayList<>();
    }

    private synchronized void CallCustomerDocumentsService(final CallMethod method, final int offset, final int limit) {

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
                            if (method == CallMethod.REFRESH || method == CallMethod.LOADMORE) {
                                mSwipeRefreshLayout.setRefreshing(false);
                            } else if (method == CallMethod.FIRSTTIME) {
                                Utilities.dismissLoadingDialog();
                            }
                            company_documents__cs = (ArrayList<Company_Documents__c>) SFResponseManager.parseCompanyDocumentObject(response.toString());

                            if (adapter == null) {
                                companyDocuments.addAll(company_documents__cs);
                                adapter = new ClickableCustomerDocumentsAdapter(getActivity(), getActivity().getApplicationContext(),
                                        R.layout.company_document_item_row_screen, companyDocuments);
                                lstCustomerDocuments.setAdapter(adapter);
                            } else {
                                adapter.addAll(company_documents__cs);
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
    }

    public static void setNewCompanyDocuments(Company_Documents__c company_documents__c, int position) {
        companyDocuments.set(position, company_documents__c);
        adapter = new ClickableCustomerDocumentsAdapter(activity, activity.getApplicationContext(),
                R.layout.company_document_item_row_screen, companyDocuments);
        lstCustomerDocuments.setAdapter(adapter);
    }


    enum CallMethod {
        REFRESH,
        LOADMORE,
        FIRSTTIME
    }
}
