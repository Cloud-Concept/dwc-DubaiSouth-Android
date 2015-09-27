package fragment.companyInfo;

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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import RestAPI.SFResponseManager;
import RestAPI.SoqlStatements;
import adapter.companyInfoAdapters.DirectorsAdapter;
import cloudconcept.dwc.R;
import custom.expandableView.ExpandableLayoutListView;
import dataStorage.StoreData;
import model.Directorship;
import model.User;
import utilities.CallType;

/**
 * Created by Abanoub on 7/2/2015.
 */
public class DirectorsFragment extends Fragment {

    private static final String ARG_TEXT = "DirectorsFragment";
    ArrayList<Directorship> directorships;
    String lastReponseString = "";
    private SwipyRefreshLayout swipyRefreshLayout;
    private ExpandableLayoutListView lvDirectors;
    private int offset = 0;
    private int limit = 10;
    private String soqlQuery;
    private RestRequest restRequest;

    public static DirectorsFragment newInstance(String text) {
        DirectorsFragment fragment = new DirectorsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TEXT, text);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shareholders, container, false);
        InitializeViews(view);
        CallDirectorsService(CallType.FIRSTTIME, offset, limit);
        return view;
    }

    private void InitializeViews(View view) {
        swipyRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        lvDirectors = (ExpandableLayoutListView) view.findViewById(R.id.expandableLayoutListView);
        swipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {
                if (swipyRefreshLayoutDirection == SwipyRefreshLayoutDirection.TOP) {
                    offset = 0;
                    CallDirectorsService(CallType.REFRESH, offset, limit);
                } else {
                    offset += limit;
                    CallDirectorsService(CallType.LOADMORE, offset, limit);
                }
            }
        });
    }

    private void CallDirectorsService(final CallType serviceCall, int offset, int limit) {
        if (serviceCall == CallType.FIRSTTIME && !new StoreData(getActivity().getApplicationContext()).getDirectorsResponse().equals("")) {
            directorships = new ArrayList<Directorship>();
            directorships = SFResponseManager.parseDirectionshipObject(new StoreData(getActivity().getApplicationContext()).getDirectorsResponse());
            if (directorships.size() > 0) {
                lvDirectors.setAdapter(new DirectorsAdapter(getActivity(), getActivity().getApplicationContext(), R.layout.directors_item, directorships));
            }
        }else{
            Gson gson = new Gson();
            User _user = gson.fromJson(new StoreData(getActivity().getApplicationContext()).getUserDataAsString(), User.class);
            soqlQuery = SoqlStatements.getInstance().constructDirectorsQuery(_user.get_contact().get_account().getID(), limit, offset);
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
                                public void onSuccess(RestRequest request, RestResponse response) {

                                    if (serviceCall == CallType.LOADMORE || serviceCall == CallType.REFRESH)
                                        swipyRefreshLayout.setRefreshing(false);
                                    if (serviceCall == CallType.LOADMORE) {
                                        ArrayList<Directorship> directorships1 = SFResponseManager.parseDirectionshipObject(response.toString());
                                        if (directorships1.size() > 0) {
                                            new StoreData(getActivity().getApplicationContext()).setDirectorsResponse(response.toString());
                                            for (int i = 0; i < directorships1.size(); i++) {
                                                boolean found = false;
                                                for (int j = 0; j < directorships.size(); j++) {
                                                    if (directorships1.get(i).getID().equals(directorships.get(j).getID())) {
                                                        found = true;
                                                        break;
                                                    }
                                                }
                                                if (!found) {
                                                    directorships.add(directorships1.get(i));
                                                }
                                            }
                                            lvDirectors.setAdapter(new DirectorsAdapter(getActivity(), getActivity().getApplicationContext(), R.layout.directors_item, directorships));
                                        }
                                    } else {
                                        directorships = new ArrayList<Directorship>();
                                        directorships = SFResponseManager.parseDirectionshipObject(response.toString());
                                        if (directorships.size() > 0) {
                                            new StoreData(getActivity().getApplicationContext()).setDirectorsResponse(response.toString());
                                            lvDirectors.setAdapter(new DirectorsAdapter(getActivity(), getActivity().getApplicationContext(), R.layout.directors_item, directorships));
                                        }
                                    }
                                }

                                @Override
                                public void onError(Exception exception) {

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
