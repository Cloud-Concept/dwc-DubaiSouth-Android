package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import RestAPI.SFResponseManager;
import RestAPI.SoqlStatements;
import adapter.SpinnerAdapter;
import adapter.ViewStatementAdapter;
import cloudconcept.dwc.R;
import dataStorage.StoreData;
import fragmentActivity.ViewStatementActivity;
import model.FreeZonePayment;
import model.User;
import utilities.CallType;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 9/6/2015.
 */
public class ViewStatementFragment extends Fragment {

    private ViewStatementAdapter mAdapter;
    ViewStatementActivity activity;
    int limit = 20;
    int offset = 0;
    private User user;
    RestRequest restRequest;
    Spinner spinnerViewStatementFilter;
    String[] filterItems = new String[]{"Current Quarter", "Last Quarter", "Current Year", "Last Year", "All Time"};
    String startDate = "", endDate = "";
    private String queryFilter = "";
    ListView lstStatements;
    private SwipyRefreshLayout mSwipeRefreshLayout;
    private String filterItem = "Current Quarter";
    ArrayList<FreeZonePayment> freeZonePayments;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.view_statement, container, false);
        spinnerViewStatementFilter = (Spinner) view.findViewById(R.id.spinnerViewStatementFilter);
        mSwipeRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        lstStatements = (ListView) view.findViewById(R.id.lstStatements);
        ArrayAdapter<String> dataAdapter = new SpinnerAdapter(getActivity().getApplicationContext(), R.layout.spinner_item,
                Arrays.asList(filterItems));
        spinnerViewStatementFilter.setAdapter(dataAdapter);
        spinnerViewStatementFilter.setSelection(0);
        freeZonePayments = new ArrayList<>();
        InitializeViews(view);
        return view;
    }

    private void InitializeViews(View view) {

        activity = (ViewStatementActivity) getActivity();

        spinnerViewStatementFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != -1) {
                    filterItem = filterItems[position];
                    freeZonePayments.clear();
                    mAdapter = new ViewStatementAdapter(getActivity(), getActivity().getApplicationContext(),
                            R.layout.view_statement_item_row, freeZonePayments);
                    lstStatements.setAdapter(mAdapter);
                    queryFilter = ConstructDateRangeFilter(filterItems[position]);
                    CallFreeZonePaymentRequest(offset, limit, queryFilter, CallType.SPINNETCHANGEDDATA);
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
                    Date[] dates = Utilities.formatStartAndEndDate(filterItem);
                    startDate = dates[0] + "T00:00:00Z";
                    endDate = dates[1] + "T00:00:00Z";
                    queryFilter = String.format("CreatedDate >= %s AND CreatedDate <= %s", startDate, endDate);
                    CallFreeZonePaymentRequest(offset, limit, queryFilter, CallType.REFRESH);
                } else {
                    offset += 20;
                    Date[] dates = Utilities.formatStartAndEndDate(filterItem);
                    startDate = dates[0] + "T00:00:00Z";
                    endDate = dates[1] + "T00:00:00Z";
                    queryFilter = String.format("CreatedDate >= %s AND CreatedDate <= %s", startDate, endDate);
                    CallFreeZonePaymentRequest(offset, limit, queryFilter, CallType.LOADMORE);
                }
            }
        });


        Date[] dates = Utilities.formatStartAndEndDate("Current Quarter");

        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        startDate = DATE_FORMAT.format(dates[0]) + "T00:00:00Z";
        endDate = DATE_FORMAT.format(dates[1]) + "T00:00:00Z";
        queryFilter = String.format("CreatedDate >= %s AND CreatedDate <= %s", startDate, endDate);
        CallFreeZonePaymentRequest(offset, limit, queryFilter, CallType.FIRSTTIME);
    }

    private String ConstructDateRangeFilter(String filterItem) {
        queryFilter = "";
        if (!filterItem.equals("All Time")) {
            Date[] dates = Utilities.formatStartAndEndDate(filterItem);
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
            startDate = DATE_FORMAT.format(dates[0]) + "T00:00:00Z";
            endDate = DATE_FORMAT.format(dates[1]) + "T00:00:00Z";
            queryFilter = String.format("CreatedDate >= %s AND CreatedDate <= %s", startDate, endDate);
        }

        return queryFilter;
    }

    private void CallFreeZonePaymentRequest(final int offset, final int limit, final String queryFilter, final CallType callType) {
        if (callType == CallType.SPINNETCHANGEDDATA) {
            if (!Utilities.getIsProgressLoading()) {
                Utilities.showloadingDialog(getActivity());
            }
        }

        Gson gson = new Gson();
        user = gson.fromJson(new StoreData(getActivity().getApplicationContext()).getUserDataAsString(), User.class);
        String soql = SoqlStatements.constructViewStatementQuery(user.get_contact().get_account().getID(), offset, limit, queryFilter);
        try {
            restRequest = RestRequest.getRequestForQuery(getString(R.string.api_version), soql);
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
                                if (callType == CallType.REFRESH || callType == CallType.LOADMORE) {
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }
                                if (callType == CallType.SPINNETCHANGEDDATA) {
                                    if (Utilities.getIsProgressLoading()) {
                                        Utilities.dismissLoadingDialog();
                                    }
                                }

                                ArrayList<FreeZonePayment> payments = (ArrayList<FreeZonePayment>) SFResponseManager.parseFreeZonePaymentResponse(response.toString());
                                freeZonePayments.addAll(payments);
                                mAdapter = new ViewStatementAdapter(getActivity(), getActivity().getApplicationContext(),
                                        R.layout.view_statement_item_row, freeZonePayments);
                                lstStatements.setAdapter(mAdapter);
                            }

                            @Override
                            public void onError(Exception exception) {
                                if (Utilities.getIsProgressLoading()) {
                                    Utilities.dismissLoadingDialog();
                                }
                                if (callType == CallType.REFRESH || callType == CallType.LOADMORE) {
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }
                                if (callType == CallType.SPINNETCHANGEDDATA) {
                                    Utilities.dismissLoadingDialog();
                                }
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