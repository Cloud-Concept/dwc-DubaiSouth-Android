package fragment;

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
import android.widget.ListView;
import android.widget.Spinner;
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
import java.util.Arrays;
import java.util.Calendar;

import RestAPI.RestMessages;
import RestAPI.SFResponseManager;
import RestAPI.SoqlStatements;
import adapter.MyRequestsAdapter;
import adapter.SpinnerAdapter;
import cloudconcept.dwc.R;
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
    String[] request_type_filter = new String[]{"All", "Visa Services", "NOC Services", "License Services", "Access Card Services", "Registration Services", "Leasing Services"};
    int offset = 0;
    int limit = 20;
    SwipyRefreshLayout mSwipeRefreshLayout;
    ListView lstMyRequests;
    Spinner spinnerStatusFilter, spinnerRequestTypeFilter;
    String status = "";
    String request_type = "";
    private String soqlQuery;
    private RestRequest restRequest;
    private View view;
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.my_requests, container, false);
        InitializeViews(view);
        CallMyRequestsService(CallType.FIRSTTIME, status, request_type, limit, offset);
        return view;
    }

    private void InitializeViews(View view) {
        etSearch = (EditText) view.findViewById(R.id.etSearch);
        mSwipeRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        lstMyRequests = (ListView) view.findViewById(R.id.lstMyRequests);
        spinnerStatusFilter = (Spinner) view.findViewById(R.id.spinnerStatus);
        spinnerRequestTypeFilter = (Spinner) view.findViewById(R.id.spinnerType);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, status_filter);
//        adapter.setDropDownViewResource(R.layout.spinner_item);
        ArrayAdapter<String> dataAdapter2 = new SpinnerAdapter(getActivity().getApplicationContext(), R.layout.spinner_item,
                Arrays.asList(status_filter));
        spinnerStatusFilter.setAdapter(dataAdapter2);
//        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, request_type_filter);
//        adapter.setDropDownViewResource(R.layout.spinner_item);
        ArrayAdapter<String> dataAdapter = new SpinnerAdapter(getActivity().getApplicationContext(), R.layout.spinner_item,
                Arrays.asList(request_type_filter));
        spinnerRequestTypeFilter.setAdapter(dataAdapter);

        spinnerStatusFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!status.equals(status_filter[position])) {
                    offset = 0;
                    InflatedRequests.clear();
                    status = status_filter[position];
                    new StoreData(getActivity().getApplicationContext()).setMyRequestsStatus(status);
                    CallMyRequestsService(CallType.SPINNETCHANGEDDATA, status, request_type, limit, offset);
                }else {
                    ((TextView) parent.getChildAt(0)).setTextSize(10);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerRequestTypeFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!request_type.equals(request_type_filter[position])) {
                    offset = 0;
                    InflatedRequests.clear();
                    request_type = request_type_filter[position];
                    new StoreData(getActivity().getApplicationContext()).setMyRequestsRequestType(request_type);
                    CallMyRequestsService(CallType.SPINNETCHANGEDDATA, status, request_type, limit, offset);
                }else{
                    ((TextView) parent.getChildAt(0)).setTextSize(10);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    offset = 0;
                    InflatedRequests.clear();
                    CallMyRequestsService(CallType.REFRESH, status, request_type, limit, offset);
                } else {
                    offset += limit;
                    CallMyRequestsService(CallType.LOADMORE, status, request_type, limit, offset);
                }
            }
        });
        _FilteredRequests = new ArrayList<>();

        status = new StoreData(getActivity().getApplicationContext()).getMyRequestsStatus();
        request_type = new StoreData(getActivity().getApplicationContext()).getMyRequestsRequestType();
        if (status.equals("")) {
            status = "All";
        }
        if (request_type.equals("")) {
            request_type = "All";
        }
        ManageSpinnerSelection();
    }

    private void ManageSpinnerSelection() {

        if (status.equals("All")) {
            spinnerStatusFilter.setSelection(0, true);
        } else if (status.equals("Completed")) {
            spinnerStatusFilter.setSelection(1, true);
        } else if (status.equals("In Process")) {
            spinnerStatusFilter.setSelection(2, true);
        } else if (status.equals("Ready For Collection")) {
            spinnerStatusFilter.setSelection(3, true);
        } else if (status.equals("Not Submitted")) {
            spinnerStatusFilter.setSelection(4, true);
        }

        if (request_type.equals("All")) {
            spinnerRequestTypeFilter.setSelection(0, true);
        } else if (request_type.equals("Visa Services")) {
            spinnerRequestTypeFilter.setSelection(1, true);
        } else if (request_type.equals("NOC Services")) {
            spinnerRequestTypeFilter.setSelection(2, true);
        } else if (request_type.equals("License Services")) {
            spinnerRequestTypeFilter.setSelection(3, true);
        } else if (request_type.equals("Access Cards Services")) {
            spinnerRequestTypeFilter.setSelection(4, true);
        } else if (request_type.equals("Registration Services")) {
            spinnerRequestTypeFilter.setSelection(4, true);
        } else if (request_type.equals("Leasing Services")) {
            spinnerRequestTypeFilter.setSelection(4, true);
        }
    }

    private void CallMyRequestsService(final CallType callType, String status2, String request_type2, int limit, int offset) {
        if (callType == CallType.FIRSTTIME && !new StoreData(getActivity().getApplicationContext()).getLastMyRequestsResponse().equals("")) {
            ArrayList<MyRequest> myRequests = SFResponseManager.parseMyRequestsResponse(new StoreData(getActivity().getApplicationContext()).getLastMyRequestsResponse());
            if (InflatedRequests.size() == 0) {
                InflatedRequests.addAll(myRequests);
            }
            adapter = new MyRequestsAdapter(getActivity(), getActivity().getApplicationContext(),
                    R.layout.my_requests_row_item, InflatedRequests);
            lstMyRequests.setAdapter(adapter);
            lstMyRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                        _FilteredRequests.clear();
                        for (int i = 0; i < InflatedRequests.size(); i++) {
                            if (InflatedRequests.get(i).getCaseNumber().contains(s.toString())) {
                                _FilteredRequests.add(InflatedRequests.get(i));
                            }
                        }
                    } else {
                        _FilteredRequests.addAll(InflatedRequests);
                    }

                    adapter = new MyRequestsAdapter(getActivity(), getActivity().getApplicationContext(),
                            R.layout.my_requests_row_item, _FilteredRequests);
                    lstMyRequests.setAdapter(adapter);
                    lstMyRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ActivitiesLauncher.openMyRequestsShowDetailsActivity(getActivity().getApplicationContext(), _FilteredRequests.get(position));
                        }
                    });
                }
            });

        } else {
            Gson gson = new Gson();
            User _user = gson.fromJson(new StoreData(getActivity().getApplicationContext()).getUserDataAsString(), User.class);
            soqlQuery = SoqlStatements.getInstance().constructMyRequestsServiceQuery(_user.get_contact().get_account().getID(), status2, request_type2, limit, offset);
            try {
                restRequest = RestRequest.getRequestForQuery(
                        getActivity().getString(R.string.api_version), soqlQuery);
                if (callType == CallType.SPINNETCHANGEDDATA || callType == CallType.FIRSTTIME) {
                    Utilities.showloadingDialog(getActivity());
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
                                public void onSuccess(RestRequest request, RestResponse result) {
                                    if (callType == CallType.SPINNETCHANGEDDATA || callType == CallType.FIRSTTIME) {
                                        Utilities.dismissLoadingDialog();
                                    } else {
                                        mSwipeRefreshLayout.setRefreshing(false);
                                    }
                                    new StoreData(getActivity().getApplicationContext()).setLastMyRequestsResponse(result.toString());
                                    ArrayList<MyRequest> myRequests = SFResponseManager.parseMyRequestsResponse(result.toString());
                                    if (InflatedRequests.size() == 0) {
                                        InflatedRequests.addAll(myRequests);
                                    } else {
                                        if (myRequests.size() > 0) {
                                            for (int i = 0; i < myRequests.size(); i++) {
                                                boolean found = false;
                                                for (int j = 0; j < InflatedRequests.size(); j++) {
                                                    if (InflatedRequests.get(j).getID().equals(myRequests.get(i).getID())) {
                                                        found = true;
                                                        break;
                                                    }
                                                }
                                                if (!found) {
                                                    InflatedRequests.add(myRequests.get(i));
                                                }
                                            }
                                        }
                                    }

                                    adapter = new MyRequestsAdapter(getActivity(), getActivity().getApplicationContext(),
                                            R.layout.my_requests_row_item, InflatedRequests);
                                    lstMyRequests.setAdapter(adapter);
                                    lstMyRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                                                _FilteredRequests.clear();
                                                for (int i = 0; i < InflatedRequests.size(); i++) {
                                                    if (InflatedRequests.get(i).getCaseNumber().contains(s.toString())) {
                                                        _FilteredRequests.add(InflatedRequests.get(i));
                                                    }
                                                }
                                            } else {
                                                _FilteredRequests.addAll(InflatedRequests);
                                            }

                                            adapter = new MyRequestsAdapter(getActivity(), getActivity().getApplicationContext(),
                                                    R.layout.my_requests_row_item, _FilteredRequests);
                                            lstMyRequests.setAdapter(adapter);
                                            lstMyRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    ActivitiesLauncher.openMyRequestsShowDetailsActivity(getActivity().getApplicationContext(), _FilteredRequests.get(position));
                                                }
                                            });
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
    }
}