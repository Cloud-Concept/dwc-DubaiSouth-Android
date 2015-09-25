package fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;

import RestAPI.RestMessages;
import RestAPI.SFResponseManager;
import RestAPI.SoqlStatements;
import adapter.MyRequestsAdapter;
import cloudconcept.dwc.R;
import custom.PullAndLoadListView;
import custom.PullToRefreshListView;
import dataStorage.StoreData;
import model.MyRequest;
import model.User;
import utilities.ActivitiesLauncher;
import utilities.CallType;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 7/22/2015.
 */
public class MyRequestsFragment extends Fragment {

    private static Calendar calendar;
    String[] status_filter = new String[]{"All", "Completed", "In Process", "Ready For Collection", "Not Submitted"};
    String[] request_type_filter = new String[]{"All", "Visa Services", "NOC Services", "License Services", "Access Cards Services", "Registration Services", "Leasing Services"};
    int offset = 0;
    PullAndLoadListView pullandloadMyRequests;
    Spinner spinnerStatusFilter, spinnerRequestTypeFilter;
    String status = "";
    String request_type = "";
    private String soqlQuery;
    private RestRequest restRequest;
    private View view;
    private static String loadMoreResponse = "";
    static ArrayList<MyRequest> InflatedRequests;
    EditText etSearch;
    private ArrayList<MyRequest> _FilteredRequests;

    private MyRequestsAdapter adapter;

    public static Fragment newInstance(String s) {
        MyRequestsFragment fragment = new MyRequestsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("text", s);
        fragment.setArguments(bundle);
        calendar = Calendar.getInstance();
        InflatedRequests = new ArrayList<MyRequest>();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.my_requests, container, false);
        pullandloadMyRequests = (PullAndLoadListView) view.findViewById(R.id.pullandloadMyRequests);
        etSearch = (EditText) view.findViewById(R.id.etSearch);
        _FilteredRequests = new ArrayList<>();
        pullandloadMyRequests.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                pullandloadMyRequests.prepareForRefresh();
                InflatedRequests.clear();
                loadMoreResponse = "";
                if (status.equals("") && request_type.equals("")) {
                    status = "All";
                    request_type = "All";
                    new PullToRefreshMyRequestsTask(status, request_type).execute();
                } else {
                    new PullToRefreshMyRequestsTask(status_filter[spinnerStatusFilter.getSelectedItemPosition()], request_type_filter[spinnerRequestTypeFilter.getSelectedItemPosition()]).execute();
                }
                pullandloadMyRequests.setLastUpdated(calendar.getTime().toString());
                pullandloadMyRequests.setOnLoadMoreListener(new PullAndLoadListView.OnLoadMoreListener() {

                    @Override
                    public void onLoadMore() {
//                        if (status.equals("") && request_type.equals("")) {
//                            status = "All";
//                            request_type = "All";
//                            new LoadMoreMyRequestsTask(status, request_type).execute();
//                        } else {
//                            if (!status.equals("All")) {
//                                status = status_filter[spinnerStatusFilter.getSelectedItemPosition() - 1];
//                            }
//                            if (!request_type.equals("All")) {
//                                request_type = request_type_filter[spinnerRequestTypeFilter.getSelectedItemPosition() - 1];
//                            }
//                            new LoadMoreMyRequestsTask(status, request_type).execute();
//                        }
                        new LoadMoreMyRequestsTask(status, request_type).execute();
                    }
                });
            }
        });

//        pullandloadMyRequests.setOnLoadMoreListener(new PullAndLoadListView.OnLoadMoreListener() {
//
//            @Override
//            public void onLoadMore() {
//                if (status.equals("") && request_type.equals("")) {
//                    status = "All";
//                    request_type = "All";
//                    new LoadMoreMyRequestsTask(status, request_type).execute();
//                } else {
//                    new LoadMoreMyRequestsTask(status_filter[spinnerStatusFilter.getSelectedItemPosition()], request_type_filter[spinnerRequestTypeFilter.getSelectedItemPosition()]).execute();
//                }
//            }
//        });

        InitializeViews(view);
        pullandloadMyRequests.onRefresh();
        return view;
    }

    private void InitializeViews(View view) {

        spinnerStatusFilter = (Spinner) view.findViewById(R.id.spinnerStatus);
        spinnerRequestTypeFilter = (Spinner) view.findViewById(R.id.spinnerType);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, status_filter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatusFilter.setAdapter(adapter);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, request_type_filter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRequestTypeFilter.setAdapter(adapter);

        spinnerStatusFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                status = status_filter[position];
//                int requestTypePosition = spinnerRequestTypeFilter.getSelectedItemPosition();
//                request_type = request_type_filter[requestTypePosition];
                pullandloadMyRequests.onRefresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerRequestTypeFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                request_type = request_type_filter[position];
//                int statusPosition = spinnerStatusFilter.getSelectedItemPosition();
//                status = status_filter[statusPosition];
                pullandloadMyRequests.onRefresh();
//                new PullToRefreshMyRequestsTask(status, request_type).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void CallMyRequestsService(final CallType callType, String status2, String request_type2, int limit, int offset) {

        Gson gson = new Gson();
        User _user = gson.fromJson(new StoreData(getActivity().getApplicationContext()).getUserDataAsString(), User.class);
        soqlQuery = SoqlStatements.getInstance().constructMyRequestsServiceQuery(_user.get_contact().get_account().getID(), status2, request_type2, limit, offset);
        try {
            restRequest = RestRequest.getRequestForQuery(
                    getActivity().getString(R.string.api_version), soqlQuery);
            new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
                @Override
                public void authenticatedRestClient(RestClient client) {
                    if (client == null) {
                        SalesforceSDKManager.getInstance().logout(getActivity());
                        return;
                    } else {

                        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                            @Override
                            public void onSuccess(RestRequest request, RestResponse result) {
                                ArrayList<MyRequest> myRequests = SFResponseManager.parseMyRequestsResponse(result.toString());
                                if (loadMoreResponse.equals("")) {
                                    InflatedRequests.addAll(myRequests);
                                } else {
                                    for (int i = 0; i < myRequests.size(); i++) {
                                        boolean found = false;
                                        for (int j = 0; j < InflatedRequests.size(); j++) {
                                            if (InflatedRequests.get(j).getCaseNumber().equals(myRequests.get(i).getCaseNumber())) {
                                                found = true;
                                                break;
                                            }
                                        }
                                        if (!found) {
                                            InflatedRequests.add(myRequests.get(i));
                                        }
                                    }
                                }
//                                loadMoreResponse = result.toString();
                                if (result.toString().equals(loadMoreResponse)) {
                                    pullandloadMyRequests.setOnLoadMoreListener(null);
                                }
                                loadMoreResponse = result.toString();
                                if (callType == CallType.LOADMORE) {
                                    pullandloadMyRequests.onLoadMoreComplete();
                                } else if (callType == CallType.REFRESH) {
                                    pullandloadMyRequests.onRefreshComplete();
                                }

                                pullandloadMyRequests.setAdapter(new MyRequestsAdapter(getActivity(), getActivity().getApplicationContext(), R.layout.my_requests_row_item, InflatedRequests));
                                pullandloadMyRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        ActivitiesLauncher.openMyRequestsShowDetailsActivity(getActivity().getApplicationContext(), InflatedRequests.get(position));
                                    }
                                });
                                etSearch.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                        if (!s.toString().equals("")) {
                                            pullandloadMyRequests.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
                                                @Override
                                                public void onRefresh() {
                                                    pullandloadMyRequests.onRefreshComplete();
                                                }
                                            });
                                            pullandloadMyRequests.setOnLoadMoreListener(new PullAndLoadListView.OnLoadMoreListener() {
                                                @Override
                                                public void onLoadMore() {
                                                    pullandloadMyRequests.onLoadMoreComplete();
                                                }
                                            });
                                            _FilteredRequests.clear();
                                            for (int i = 0; i < InflatedRequests.size(); i++) {
                                                if (InflatedRequests.get(i).getCaseNumber().contains(s.toString())) {
                                                    _FilteredRequests.add(InflatedRequests.get(i));
                                                }
                                            }
                                        } else {
                                            setListener(pullandloadMyRequests);
                                            _FilteredRequests.clear();
                                            _FilteredRequests.addAll(InflatedRequests);
                                        }

                                        adapter = new MyRequestsAdapter(getActivity(), getActivity().getApplicationContext(),
                                                R.layout.my_requests_row_item, _FilteredRequests);
                                        pullandloadMyRequests.setAdapter(adapter);
                                    }
                                });
                            }

                            @Override
                            public void onError(Exception exception) {
                                Utilities.showToast(getActivity(), RestMessages.getInstance().getErrorMessage());
                            }
                        });
                    }
                }
            });

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void setListener(final PullAndLoadListView pullandloadMyRequests) {

        pullandloadMyRequests.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                pullandloadMyRequests.prepareForRefresh();
                InflatedRequests.clear();
                loadMoreResponse = "";
                if (status.equals("") && request_type.equals("")) {
                    status = "All";
                    request_type = "All";
                    new PullToRefreshMyRequestsTask(status, request_type).execute();
                } else {
                    new PullToRefreshMyRequestsTask(status_filter[spinnerStatusFilter.getSelectedItemPosition()], request_type_filter[spinnerRequestTypeFilter.getSelectedItemPosition()]).execute();
                }
                pullandloadMyRequests.setLastUpdated(calendar.getTime().toString());
                pullandloadMyRequests.setOnLoadMoreListener(new PullAndLoadListView.OnLoadMoreListener() {

                    @Override
                    public void onLoadMore() {
                        new LoadMoreMyRequestsTask(status, request_type).execute();
                    }
                });
            }
        });
    }

    public class PullToRefreshMyRequestsTask extends AsyncTask<Void, Void, Void> {

        String status;
        String request_type;

        public PullToRefreshMyRequestsTask(String status, String request_type) {
            this.status = status;
            this.request_type = request_type;
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (isCancelled()) {
                return null;
            }
            offset = 0;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            CallMyRequestsService(CallType.REFRESH, status, request_type, Integer.parseInt(SoqlStatements.LIMIT), offset);
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onCancelled() {
            pullandloadMyRequests.onRefreshComplete();
        }
    }

    public class LoadMoreMyRequestsTask extends AsyncTask<Void, Void, Void> {

        String status, request_type;

        public LoadMoreMyRequestsTask(String s, String s1) {
            status = s;
            request_type = s1;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (isCancelled()) {
                return null;
            }
            offset += Integer.parseInt(SoqlStatements.LIMIT);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            CallMyRequestsService(CallType.LOADMORE, status, request_type, Integer.parseInt(SoqlStatements.LIMIT), offset);
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onCancelled() {
            pullandloadMyRequests.onLoadMoreComplete();
        }
    }
}