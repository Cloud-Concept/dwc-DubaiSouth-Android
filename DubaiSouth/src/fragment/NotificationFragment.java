package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import RestAPI.RestMessages;
import RestAPI.SFResponseManager;
import RestAPI.SoqlStatements;
import adapter.NotificationBaseAdapter;
import cloudconcept.dwc.R;
import dataStorage.StoreData;
import model.NotificationManagement;
import model.User;
import utilities.CallType;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 6/17/2015.
 */
public class NotificationFragment extends Fragment implements SwipyRefreshLayout.OnRefreshListener {

    ListView lstNotifications;
    SwipyRefreshLayout mSwipeRefreshLayout;
    private int limit = 10;
    private int offset = 0;
    private String soqlQuery;
    private RestRequest restRequest;
    private String loadMoreResponse = "";
    private ArrayList<NotificationManagement> InflatedNotificationManagements;
    private int top;
    private int index;
    private String result = "";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notifications, container, false);
        lstNotifications = (ListView) view.findViewById(R.id.lstNotifications);
        mSwipeRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        InflatedNotificationManagements = new ArrayList<NotificationManagement>();
        Utilities.showloadingDialog(getActivity());
        new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
            @Override
            public void authenticatedRestClient(RestClient client) {
                if (client == null) {
                    SalesforceSDKManager.getInstance().logout(getActivity());
                    return;
                } else {
                    CallOnRefreshNotificationService(CallType.FIRSTTIME, offset, limit, client);
                }
            }
        });
        return view;
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection) {
        if (swipyRefreshLayoutDirection == SwipyRefreshLayoutDirection.TOP) {
            offset = 0;
            getListPosition();
            new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
                @Override
                public void authenticatedRestClient(final RestClient client) {
                    if (client == null) {
                        SalesforceSDKManager.getInstance().logout(getActivity());
                        return;
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Gson g = new Gson();
                                User user = g.fromJson(new StoreData(getActivity()).getUserDataAsString(), User.class);
                                String attUrl = client.getClientInfo().resolveUrl("/services/apexrest/MobileNotificationsReadWebService?AccountId=" + user.get_contact().get_account().getID()).toString();

                                HttpClient httpclient = new DefaultHttpClient();
                                HttpGet httppost = new HttpGet(attUrl);
                                httppost.setHeader("Authorization", "Bearer " + client.getAuthToken());
                                StringEntity entity = null;
                                try {
                                    HttpResponse response = httpclient.execute(httppost);
                                    result = EntityUtils.toString(response.getEntity());
                                    Log.d("result", result);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                } catch (ClientProtocolException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        CallOnRefreshNotificationService(CallType.REFRESH, offset, limit, client);
                    }
                }
            });
        } else {
            offset += limit;
            getListPosition();
            new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
                @Override
                public void authenticatedRestClient(RestClient client) {
                    if (client == null) {
                        SalesforceSDKManager.getInstance().logout(getActivity());
                        return;
                    } else {
                        CallOnRefreshNotificationService(CallType.LOADMORE, offset, limit, client);
                    }
                }
            });
        }
    }

    private void CallOnRefreshNotificationService(final CallType callType, final int offset, int limit, RestClient client) {

        Gson gson = new Gson();
        User _user = gson.fromJson(new StoreData(getActivity().getApplicationContext()).getUserDataAsString(), User.class);
        soqlQuery = SoqlStatements.getInstance().constructNotificationsServiceQuery(_user.get_contact().get_account().getID(), limit, offset);

        try {
            restRequest = RestRequest.getRequestForQuery(
                    getActivity().getString(R.string.api_version), soqlQuery);

            client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                @Override
                public void onSuccess(RestRequest request, RestResponse result) {
                    ArrayList<NotificationManagement> notificationManagements = SFResponseManager.parseNotificationsResponse(result.toString());
                    if (loadMoreResponse.equals("")) {
                        InflatedNotificationManagements.addAll(notificationManagements);
                    } else {
                        for (int i = 0; i < notificationManagements.size(); i++) {
                            if (!InflatedNotificationManagements.contains(notificationManagements.get(i))) {
                                InflatedNotificationManagements.add(notificationManagements.get(i));
                            }
                        }
                    }
                    loadMoreResponse = result.toString();
                    if (callType == CallType.LOADMORE || callType == CallType.REFRESH) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    } else if (callType == CallType.FIRSTTIME) {
                        Utilities.dismissLoadingDialog();
                    }

                    lstNotifications.setAdapter(new NotificationBaseAdapter(getActivity(), getActivity().getApplicationContext(), R.layout.notifications_row_item, InflatedNotificationManagements));
                    restoreListPosition();
                }

                @Override
                public void onError(Exception exception) {
                    Utilities.showToast(getActivity(), RestMessages.getInstance().getErrorMessage());
                }
            });


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void getListPosition() {
        index = lstNotifications.getFirstVisiblePosition();
        View v = lstNotifications.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - lstNotifications.getPaddingTop());
    }

    public void restoreListPosition() {

        lstNotifications.setSelectionFromTop(index, top);
    }
}
