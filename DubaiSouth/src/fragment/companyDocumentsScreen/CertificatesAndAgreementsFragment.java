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

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import RestAPI.SFResponseManager;
import RestAPI.SoqlStatements;
import adapter.TrueCopiesAdapter;
import cloudconcept.dwc.R;
import fragmentActivity.HomeCompanyDocumentsActivity;
import model.EServices_Document_Checklist__c;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 9/8/2015.
 */
public class CertificatesAndAgreementsFragment extends Fragment implements UltimateRecyclerView.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String ARG_TEXT = "TrueCopies";
    //    SuperRecyclerView recyclerView;
    private HomeCompanyDocumentsActivity activity;
    private TrueCopiesAdapter mAdapter;
    int offset = 0;
    int limit = 10;
    private RestRequest restRequest;
    //    private Handler mHandler;
    UltimateRecyclerView recyclerView;
    private ArrayList<EServices_Document_Checklist__c> eServiceDocumentChecklists;
    ArrayList<EServices_Document_Checklist__c> eServices_document_checklist__cs = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.true_copies, container, false);
        InitializeViews(view);
//        mHandler = new Handler(Looper.getMainLooper());
//        onRefresh();
//        Utilities.showloadingDialog(getActivity());
        CallTrueCopiesService(CallMethod.FIRSTTIME, 0, limit);
        return view;
    }

    private void InitializeViews(View view) {
        activity = (HomeCompanyDocumentsActivity) getActivity();
        recyclerView = (UltimateRecyclerView) view.findViewById(R.id.lstTrueCopies);
        recyclerView.enableLoadmore();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setDefaultOnRefreshListener(this);
        recyclerView.setOnLoadMoreListener(this);
//        recyclerView.setRefreshingColorResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, R.color.dwc_blue_color);
//        recyclerView.setupMoreListener(this, 20);
        eServiceDocumentChecklists = new ArrayList<>();
    }

    public static CertificatesAndAgreementsFragment newInstance(String text) {
        CertificatesAndAgreementsFragment fragment = new CertificatesAndAgreementsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TEXT, text);
        fragment.setArguments(bundle);
        return fragment;
    }

    private synchronized void CallTrueCopiesService(final CallMethod method, final int offset, final int limit) {

        Gson gson = new Gson();
        final String soql = SoqlStatements.constructTrueCopiesQuery(offset, limit);

        new Thread(new Runnable() {
            @Override
            public void run() {
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
                                    if (method == CallMethod.REFRESH) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                recyclerView.setRefreshing(false);
                                            }
                                        });

                                    } else if (method == CallMethod.FIRSTTIME) {
//                                        getActivity().runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                Utilities.dismissLoadingDialog();
//                                            }
//                                        });
                                    }

                                    if (mAdapter == null) {
                                        eServiceDocumentChecklists.addAll(eServices_document_checklist__cs);
                                        mAdapter = new TrueCopiesAdapter(getActivity(), getActivity().getApplicationContext(), eServiceDocumentChecklists);
                                        recyclerView.setAdapter(mAdapter);
                                        if (eServices_document_checklist__cs.size() < limit) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    recyclerView.disableLoadmore();
                                                }
                                            });
                                        }
                                    } else {
                                        if (eServices_document_checklist__cs.size() == 0) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    recyclerView.disableLoadmore();
                                                }
                                            });
                                        } else {
                                            if (eServices_document_checklist__cs.size() == 0) {
                                                recyclerView.disableLoadmore();
                                            } else {
                                                boolean temp = mAdapter.addAll(eServices_document_checklist__cs);
                                                if(!temp){
                                                    recyclerView.disableLoadmore();
                                                }
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
            }
        }).start();
    }

    @Override
    public void onRefresh() {
        offset = 0;
        CallTrueCopiesService(CallMethod.REFRESH, offset, limit);
    }

    @Override
    public void loadMore(int i, int i1) {
        offset += limit;
        CallTrueCopiesService(CallMethod.LOADMORE, offset, limit);
    }

    enum CallMethod {
        REFRESH,
        LOADMORE, FIRSTTIME
    }
}