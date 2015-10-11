package fragment.companyDocumentsScreen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import RestAPI.SFResponseManager;
import RestAPI.SoqlStatements;
import adapter.CertificatesAdapter;
import cloudconcept.dwc.R;
import custom.expandableView.ExpandableLayoutListView;
import dataStorage.StoreData;
import model.EServices_Document_Checklist__c;
import utilities.CallType;

/**
 * Created by Abanoub Wagdy on 9/21/2015.
 */
public class CertificatesAndAgreementsFragment2 extends Fragment {

    private static final String ARG_TEXT = "CertificatesAndAgreementsFragment2";
    int offset = 0;
    int limit = 10;
    ExpandableLayoutListView lstTrueCopies;
    SwipyRefreshLayout mSwipeRefreshLayout;
    private RestRequest restRequest;
    private ArrayList<EServices_Document_Checklist__c> eServiceDocumentChecklists;
    ArrayList<EServices_Document_Checklist__c> eServices_document_checklist__cs = null;
    CertificatesAdapter adapter;
    private int index;
    private int top;

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
        CallTrueCopiesService(CallType.FIRSTTIME, offset, limit);
        return view;
    }

    private void InitializeViews(View view) {
        lstTrueCopies = (ExpandableLayoutListView) view.findViewById(R.id.lstTrueCopies);
        mSwipeRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    offset = 0;
                    adapter = null;
                    index=0;
                    top=0;
                    getListPosition();
                    eServiceDocumentChecklists.clear();
                    CallTrueCopiesService(CallType.REFRESH, offset, limit);
                } else {
                    offset += limit;
                    getListPosition();
                    CallTrueCopiesService(CallType.LOADMORE, offset, limit);
                }
            }
        });
        eServiceDocumentChecklists = new ArrayList<>();
    }

    private void CallTrueCopiesService(final CallType method, int offset, final int limit) {
        if (method == CallType.FIRSTTIME && !new StoreData(getActivity().getApplicationContext()).getCertificatesAgreementsResponse().equals("")) {

//                eServices_document_checklist__cs = (ArrayList<EServices_Document_Checklist__c>) SFResponseManager.parseEServiceDocumentChecklist(new StoreData(getActivity().getApplicationContext()).getCertificatesAgreementsResponse());
            try {
                JSONArray jsonArray = new JSONArray(new StoreData(getActivity().getApplicationContext()).getCertificatesAgreementsResponse());
                eServices_document_checklist__cs = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    Gson gson = new Gson();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    EServices_Document_Checklist__c eServices_document_checklist__c = gson.fromJson(jsonObject.toString(), EServices_Document_Checklist__c.class);
                    eServices_document_checklist__cs.add(eServices_document_checklist__c);
                }
            } catch (JSONException e) {
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

        } else {
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
                                    if (eServices_document_checklist__cs.size() > 0) {
                                        Gson gson = new Gson();
                                        String str = gson.toJson(eServices_document_checklist__cs);
                                        new StoreData(getActivity().getApplicationContext()).saveCertificatesAgreementsResponse(str);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (method == CallType.LOADMORE || method == CallType.REFRESH) {
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }
                                if (adapter == null) {
                                    eServiceDocumentChecklists.addAll(eServices_document_checklist__cs);
                                    adapter = new CertificatesAdapter(getActivity(), getActivity().getApplicationContext(),
                                            R.layout.true_copies_item_row, eServiceDocumentChecklists);
                                    lstTrueCopies.setAdapter(adapter);
                                    restoreListPosition();
                                } else {
                                    adapter.addAll(eServices_document_checklist__cs);
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
    }

    public void getListPosition() {
        index = lstTrueCopies.getFirstVisiblePosition();
        View v = lstTrueCopies.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - lstTrueCopies.getPaddingTop());
    }

    public void restoreListPosition() {

        lstTrueCopies.setSelectionFromTop(index, top);
    }
}
