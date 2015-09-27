package fragment.companyInfo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import RestAPI.SFResponseManager;
import adapter.LeasingInfoAdapter;
import cloudconcept.dwc.R;
import custom.expandableView.ExpandableLayoutListView;
import dataStorage.StoreData;
import model.Contract_DWC__c;
import model.User;
import utilities.CallType;
import utilities.Utilities;

/**
 * Created by Abanoub on 7/2/2015.
 */
public class LeasingInfoFragment extends Fragment {

    private static final String ARG_TEXT = "LeasingInfoFragment";
    SwipyRefreshLayout mSwipeRefreshLayout;
    ExpandableLayoutListView lvLeasingInfoItems;
    RestRequest restRequest;
    private String result;
    private TextView tvNoEmployees;
    private User user;
    int offset = 0;
    int limit = 10;
    private Set<Contract_DWC__c> contract_dwc__cs;

    public LeasingInfoFragment() {

    }


    public static LeasingInfoFragment newInstance(String text) {
        LeasingInfoFragment fragment = new LeasingInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TEXT, text);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.leasing_info_fragment, container, false);
        InitializeViews(view);
        DoRequest(CallType.FIRSTTIME);
        return view;
    }

    private void DoRequest(CallType callType) {
        if (callType == CallType.FIRSTTIME && !new StoreData(getActivity().getApplicationContext()).getLeasingInfoResponse().equals("")) {
            contract_dwc__cs.addAll(SFResponseManager.parseLeasingContractResponse(new StoreData(getActivity().getApplicationContext()).getLeasingInfoResponse()));
            ArrayList<Contract_DWC__c> contracts = new ArrayList<>();
            for (Contract_DWC__c contract_dwc__c : contract_dwc__cs) {
                boolean found = false;
                for (Contract_DWC__c contract_dwc__c1 : contracts) {
                    if (contract_dwc__c1.getID().equals(contract_dwc__c.getID())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    contracts.add(contract_dwc__c);
                }
            }
            lvLeasingInfoItems.setAdapter(new LeasingInfoAdapter(getActivity(), getActivity().getApplicationContext(), R.layout.leasing_info_item, contracts));
        } else {
            new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
                @Override
                public void authenticatedRestClient(final RestClient client) {
                    if (client == null) {
                        SalesforceSDKManager.getInstance().logout(getActivity());
                        return;
                    } else {
                        new LeasingInfoTask(CallType.FIRSTTIME, client, offset, limit).execute();
                    }
                }
            });
        }
    }

    private void InitializeViews(View view) {

        Gson gson = new Gson();
        user = gson.fromJson(new StoreData(getActivity().getApplicationContext()).getUserDataAsString(), User.class);
        contract_dwc__cs = new HashSet<>();
        mSwipeRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    offset = 0;
                    contract_dwc__cs.clear();
                    new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
                        @Override
                        public void authenticatedRestClient(final RestClient client) {
                            if (client == null) {
                                SalesforceSDKManager.getInstance().logout(getActivity());
                                return;
                            } else {
                                new LeasingInfoTask(CallType.REFRESH, client, offset, limit).execute();
                            }
                        }
                    });
                } else {
                    offset += limit;
                    new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
                        @Override
                        public void authenticatedRestClient(final RestClient client) {
                            if (client == null) {
                                SalesforceSDKManager.getInstance().logout(getActivity());
                                return;
                            } else {
                                new LeasingInfoTask(CallType.LOADMORE, client, offset, limit).execute();
                            }
                        }
                    });
                }
            }
        });
        lvLeasingInfoItems = (ExpandableLayoutListView) view.findViewById(R.id.expandableLayoutListView);
        tvNoEmployees = (TextView) view.findViewById(R.id.tvNoEmployees);
    }

    public class LeasingInfoTask extends AsyncTask<Void, Void, Void> {

        private final RestClient client;
        private int offset;
        private int limit;
        CallType callType;

        public LeasingInfoTask(CallType callType, RestClient client, int offset, int limit) {
            this.client = client;
            this.limit = limit;
            this.offset = offset;
            this.callType = callType;
        }

        @Override
        protected void onPreExecute() {
            if (callType == CallType.FIRSTTIME) {
                if (Utilities.getIsProgressLoading() == false) {
                    Utilities.showloadingDialog(getActivity());
                }
            }
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String attUrl = client.getClientInfo().resolveUrl("/services/apexrest/MobileTenantContractsWebService").toString();
            attUrl += "?AccountId=" + user.get_contact().get_account().getID() + "&LIMIT=" + limit + "&OFFSET=" + offset;
            HttpClient tempClient = new DefaultHttpClient();
            URI theUrl = null;
            try {
                theUrl = new URI(attUrl);
                HttpGet getRequest = new HttpGet();
                getRequest.setURI(theUrl);
                getRequest.setHeader("Authorization", "Bearer " + client.getAuthToken());
                HttpResponse httpResponse = null;
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
            if (result == null || result.equals("[]")) {
                tvNoEmployees.setVisibility(View.VISIBLE);
            } else {
                tvNoEmployees.setVisibility(View.GONE);
                new StoreData(getActivity().getApplicationContext()).setLeasingInfoResponse(result.toString());
                contract_dwc__cs.addAll(SFResponseManager.parseLeasingContractResponse(result.toString()));
                ArrayList<Contract_DWC__c> contracts = new ArrayList<>();
                for (Contract_DWC__c contract_dwc__c : contract_dwc__cs) {
                    boolean found = false;
                    for (Contract_DWC__c contract_dwc__c1 : contracts) {
                        if (contract_dwc__c1.getID().equals(contract_dwc__c.getID())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        contracts.add(contract_dwc__c);
                    }
                }
                lvLeasingInfoItems.setAdapter(new LeasingInfoAdapter(getActivity(), getActivity().getApplicationContext(), R.layout.leasing_info_item, contracts));
            }

            if (Utilities.getIsProgressLoading()) {
                Utilities.dismissLoadingDialog();
            }

            if (callType == CallType.LOADMORE || callType == CallType.REFRESH) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}