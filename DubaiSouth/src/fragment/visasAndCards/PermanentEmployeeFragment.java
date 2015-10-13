package fragment.visasAndCards;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

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

import RestAPI.RestMessages;
import RestAPI.SFResponseManager;
import RestAPI.SoqlStatements;
import adapter.SpinnerAdapter;
import adapter.visasAdapters.PermanentEmployeeListAdapter;
import cloudconcept.dwc.R;
import custom.Images;
import custom.expandableView.ExpandableLayoutListView;
import dataStorage.StoreData;
import model.Visa;
import utilities.CallType;
import utilities.Utilities;

/**
 * Created by Abanoub on 6/24/2015.
 */
public class PermanentEmployeeFragment extends Fragment {

    private static final String ARG_TEXT = "PermanentEmployee";
    String strFilter;
    ExpandableLayoutListView expandableLayoutListView;
    SwipyRefreshLayout mSwipeRefreshLayout;
    Spinner spinnerFilterPermanentEmployee;
    EditText etSearch;
    String[] visa_validity_status = new String[]{"All", "Issued", "Expired", "Cancelled", "Under Process", "Under Renewal"};
    RestRequest restRequest;
    ArrayList<Visa> _visas;
    ArrayList<Visa> _Filteredvisas;
    int limit = 10;
    int offset = 0;
    PermanentEmployeeListAdapter adapter;
    private String soqlQuery;
    private int index;
    private int top;

    public static PermanentEmployeeFragment newInstance(String text) {

        PermanentEmployeeFragment fragment = new PermanentEmployeeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TEXT, text);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.visas_cards_permanent__employee, container, false);
        offset = 0;
        _visas = new ArrayList<Visa>();
        _Filteredvisas = new ArrayList<Visa>();
        InitializeViews(view);
        CallPermanentEmployeeService(strFilter, CallType.FIRSTTIME);
        return view;
    }

    private void InitializeViews(View view) {

        expandableLayoutListView = (ExpandableLayoutListView) view.findViewById(R.id.expandableLayoutListView);
        spinnerFilterPermanentEmployee = (Spinner) view.findViewById(R.id.spinner);
        mSwipeRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        etSearch = (EditText) view.findViewById(R.id.etSearch);
        etSearch.clearFocus();
        etSearch.setHint(Html.fromHtml("<p><img src='search'>Search</p>", new Images(getActivity()), null));
        ArrayAdapter<String> dataAdapter = new SpinnerAdapter(getActivity().getApplicationContext(), R.layout.spinner_item,
                Arrays.asList(getActivity().getApplicationContext().getResources().getStringArray(R.array.permanent_employee_filter)));
        spinnerFilterPermanentEmployee.setAdapter(dataAdapter);
        strFilter = new StoreData(getActivity().getApplicationContext()).getPermanentEmployeeSpinnerFilterValue();
        if (strFilter.equals("")) {
            spinnerFilterPermanentEmployee.setSelection(1, true);
            strFilter = visa_validity_status[1];
            new StoreData(getActivity().getApplicationContext()).setPermanentEmployeeSpinnerFilterValue(strFilter);
        } else {
            if (strFilter.equals("All")) {
                spinnerFilterPermanentEmployee.setSelection(0);
            } else if (strFilter.equals("Issued")) {
                spinnerFilterPermanentEmployee.setSelection(1);
            } else if (strFilter.equals("Expired")) {
                spinnerFilterPermanentEmployee.setSelection(2);
            } else if (strFilter.equals("Cancelled")) {
                spinnerFilterPermanentEmployee.setSelection(3);
            } else if (strFilter.equals("Under Process")) {
                spinnerFilterPermanentEmployee.setSelection(4);
            } else {
                spinnerFilterPermanentEmployee.setSelection(5);
            }
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    offset = 0;
                    getListPosition();
                    CallPermanentEmployeeService(strFilter, CallType.REFRESH);
                } else {
                    getListPosition();
                    CallPermanentEmployeeService(strFilter, CallType.LOADMORE);
                }
            }
        });

        spinnerFilterPermanentEmployee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!strFilter.equals(visa_validity_status[position])) {
                    strFilter = visa_validity_status[position];
                    new StoreData(getActivity().getApplicationContext()).setPermanentEmployeeSpinnerFilterValue(strFilter);
                    _visas.clear();
                    if (_Filteredvisas != null) {
                        _Filteredvisas.clear();
                    }
                    CallPermanentEmployeeService(strFilter, CallType.SPINNETCHANGEDDATA);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void CallPermanentEmployeeService(String visa_validity_status, final CallType callType) {
        if (callType == CallType.FIRSTTIME && !new StoreData(getActivity().getApplicationContext()).getPermanentEmployeeResponse().equals("")) {
            ArrayList<Visa> _Returnedvisas = SFResponseManager.parseVisaData(new StoreData(getActivity().getApplicationContext()).getPermanentEmployeeResponse());
            if (_visas.size() == 0) {
                _visas.addAll(_Returnedvisas);
            } else {
                for (int i = 0; i < _Returnedvisas.size(); i++) {
                    if (_visas.size() > 0) {
                        boolean isFound = false;
                        for (int j = 0; j < _visas.size(); j++) {
                            if (_Returnedvisas.get(i).getID().equals(_visas.get(j).getID())) {
                                isFound = true;
                                break;
                            }
                        }
                        if (!isFound) {
                            _visas.add(_Returnedvisas.get(i));
                        }
                    }
                }
            }

            adapter = new PermanentEmployeeListAdapter(getActivity(), getActivity().getApplicationContext(),
                    R.layout.item_row_permanent_employee, _visas);
            expandableLayoutListView.setAdapter(adapter);
            _Filteredvisas.clear();

            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().toLowerCase().equals("")) {
                        _Filteredvisas.clear();
                        for (int i = 0; i < _visas.size(); i++) {
                            if (_visas.get(i).getApplicant_Full_Name__c().toLowerCase().contains(s.toString().toLowerCase())) {
                                _Filteredvisas.add(_visas.get(i));
                            }
                        }
                    } else {
                        _Filteredvisas.clear();
                        _Filteredvisas.addAll(_visas);
                    }

                    adapter = new PermanentEmployeeListAdapter(getActivity(), getActivity().getApplicationContext(),
                            R.layout.item_row_permanent_employee, _Filteredvisas);
                    expandableLayoutListView.setAdapter(adapter);
                }
            });
        } else {
            soqlQuery = SoqlStatements.getInstance().constructPermanentEmployeeSoqlStatement(new StoreData(getActivity().getApplicationContext()).getUserDataAsString(), visa_validity_status, limit, offset);
            try {
                restRequest = RestRequest.getRequestForQuery(
                        getActivity().getString(R.string.api_version), soqlQuery);

                if (callType == CallType.FIRSTTIME || callType == CallType.REFRESH || callType == CallType.SPINNETCHANGEDDATA) {
                    offset = 0;
                } else {
                    offset += limit;
                }

                if (callType == CallType.SPINNETCHANGEDDATA || callType == CallType.FIRSTTIME) {
                    Utilities.showloadingDialog(getActivity());
                }
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
                            public void onSuccess(RestRequest request, RestResponse result) {
                                try {
                                    if (callType == CallType.REFRESH) {
                                        _visas.clear();
                                        if (_Filteredvisas != null) {
                                            _Filteredvisas.clear();
                                        }
                                    }
                                    if (callType == CallType.SPINNETCHANGEDDATA || callType == CallType.FIRSTTIME) {
                                        Utilities.dismissLoadingDialog();
                                    } else {
                                        mSwipeRefreshLayout.setRefreshing(false);
                                    }
                                    new StoreData(getActivity().getApplicationContext()).savePermanentEmployeeResponse(result.toString());
                                    ArrayList<Visa> _Returnedvisas = SFResponseManager.parseVisaData(result.toString());
                                    if (_visas.size() == 0) {
                                        _visas.addAll(_Returnedvisas);
                                    } else {
                                        for (int i = 0; i < _Returnedvisas.size(); i++) {
                                            if (_visas.size() > 0) {
                                                boolean isFound = false;
                                                for (int j = 0; j < _visas.size(); j++) {
                                                    if (_Returnedvisas.get(i).getID().equals(_visas.get(j).getID())) {
                                                        isFound = true;
                                                        break;
                                                    }
                                                }
                                                if (!isFound) {
                                                    _visas.add(_Returnedvisas.get(i));
                                                }
                                            }
                                        }
                                    }

                                    adapter = new PermanentEmployeeListAdapter(getActivity(), getActivity().getApplicationContext(),
                                            R.layout.item_row_permanent_employee, _visas);
                                    expandableLayoutListView.setAdapter(adapter);
                                    restoreListPosition();
                                    _Filteredvisas.clear();

                                    etSearch.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {
                                            if (!s.equals("")) {
                                                _Filteredvisas.clear();
                                                for (int i = 0; i < _visas.size(); i++) {
                                                    if (_visas.get(i).getApplicant_Full_Name__c().toLowerCase().contains(s.toString().toLowerCase())) {
                                                        _Filteredvisas.add(_visas.get(i));
                                                    }
                                                }
                                            } else {
                                                _Filteredvisas.clear();
                                                _Filteredvisas.addAll(_visas);
                                            }

                                            adapter = new PermanentEmployeeListAdapter(getActivity(), getActivity().getApplicationContext(),
                                                    R.layout.item_row_permanent_employee, _Filteredvisas);
                                            expandableLayoutListView.setAdapter(adapter);
                                        }
                                    });
                                } catch (Exception e) {
                                    onError(e);
                                }
                            }

                            @Override
                            public void onError(Exception exception) {
                                Utilities.showToast(getActivity(), RestMessages.getInstance().getErrorMessage());
                            }
                        });
                    }
                }
            });
        }
    }

    public void getListPosition() {
        index = expandableLayoutListView.getFirstVisiblePosition();
        View v = expandableLayoutListView.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - expandableLayoutListView.getPaddingTop());
    }

    public void restoreListPosition() {
        expandableLayoutListView.setSelectionFromTop(index, top);
    }
}