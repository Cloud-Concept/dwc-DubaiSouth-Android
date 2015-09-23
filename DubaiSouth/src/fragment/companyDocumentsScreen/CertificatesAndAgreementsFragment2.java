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

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import RestAPI.SFResponseManager;
import RestAPI.SoqlStatements;
import adapter.CertificatesAdapter;
import cloudconcept.dwc.R;
import model.EServices_Document_Checklist__c;

/**
 * Created by Abanoub Wagdy on 9/21/2015.
 */
public class CertificatesAndAgreementsFragment2 extends Fragment {

    private static final String ARG_TEXT = "CertificatesAndAgreementsFragment2";
    int offset = 0;
    int limit = 10;
    ListView lstTrueCopies;
    SwipyRefreshLayout mSwipeRefreshLayout;
    private boolean iscalledFromRefresh = false;
    private RestRequest restRequest;
    private ArrayList<EServices_Document_Checklist__c> eServiceDocumentChecklists;
    ArrayList<EServices_Document_Checklist__c> eServices_document_checklist__cs = null;
    CertificatesAdapter adapter;

    public static CertificatesAndAgreementsFragment2 newInstance(String text) {
        CertificatesAndAgreementsFragment2 fragment = new CertificatesAndAgreementsFragment2();
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
        CallTrueCopiesService(CallMethod.FIRSTTIME, offset, limit);
        return view;
    }

    private void InitializeViews(View view) {
        lstTrueCopies = (ListView) view.findViewById(R.id.lstTrueCopies);
        mSwipeRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.d("MainActivity", "Refresh triggered at "
                        + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    iscalledFromRefresh = true;
                    offset = 0;
                    CallTrueCopiesService(CallMethod.REFRESH, offset, limit);
                } else {
                    iscalledFromRefresh = false;
                    offset += limit;
                    CallTrueCopiesService(CallMethod.LOADMORE, offset, limit);
                }
            }
        });
        eServiceDocumentChecklists = new ArrayList<>();
    }

    private void CallTrueCopiesService(final CallMethod method, int offset, final int limit) {
        final String soql = SoqlStatements.constructTrueCopiesQuery(offset, limit);
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
                            try {
                                eServices_document_checklist__cs = (ArrayList<EServices_Document_Checklist__c>) SFResponseManager.parseEServiceDocumentChecklist(response.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (adapter == null) {
                                eServiceDocumentChecklists.addAll(eServices_document_checklist__cs);
                                adapter = new CertificatesAdapter(getActivity(), getActivity().getApplicationContext(),
                                        R.layout.true_copies_item_row, eServiceDocumentChecklists);
                                lstTrueCopies.setAdapter(adapter);
                            } else {
                                adapter.addAll(eServices_document_checklist__cs);
                            }

                            if (method == CallMethod.REFRESH || method == CallMethod.LOADMORE) {
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        }

                        @Override
                        public void onError(Exception exception) {
                        }
                    });
                }
            }
        });
    }


    enum CallMethod {
        REFRESH,
        LOADMORE,
        FIRSTTIME
    }
}
