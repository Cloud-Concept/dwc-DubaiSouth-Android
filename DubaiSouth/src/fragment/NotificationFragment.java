package fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import RestAPI.RestMessages;
import RestAPI.SFResponseManager;
import RestAPI.SoqlStatements;
import adapter.NotificationsAdapter;
import cloudconcept.dwc.BaseActivity;
import cloudconcept.dwc.R;
import dataStorage.StoreData;
import model.NotificationManagement;
import model.User;
import utilities.CallType;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 6/17/2015.
 */
public class NotificationFragment extends Fragment implements SwipyRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

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
private Handler handler;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notifications, container, false);
        handler=new Handler();
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
        //lstNotifications.setOnItemClickListener(this);
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
                                String attUrl = client.getClientInfo().resolveUrl("/services/apexrest/MobileNotificationsReadWebService?AccountId=" + user.get_contact().get_account().getID()).toString()+(InflatedNotificationManagements.size()>0?"":("&CreatedDate="+InflatedNotificationManagements.get(0).getCreatedDate()));

                                HttpClient httpclient = new DefaultHttpClient();
                                HttpGet httppost = new HttpGet(attUrl);
                                httppost.setHeader("Authorization", "Bearer " + client.getAuthToken());
                                StringEntity entity = null;
                                try {
                                    HttpResponse response = httpclient.execute(httppost);
                                    result = EntityUtils.toString(response.getEntity());
                                    Log.d("result", result);

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(result.toLowerCase().contains("success")){
                                                new StoreData(getActivity()).setNotificationCount("0");
                                                ((BaseActivity)getActivity()).ManageBadgeNotification();
                                                CallOnRefreshNotificationService(CallType.REFRESH, offset, limit, client);
                                            }
                                        }
                                    });
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                } catch (ClientProtocolException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

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
                        if(callType==CallType.REFRESH)
                            InflatedNotificationManagements=new ArrayList<NotificationManagement>();
                        InflatedNotificationManagements.addAll(notificationManagements);
                    }
                    loadMoreResponse = result.toString();
                    if (callType == CallType.LOADMORE || callType == CallType.REFRESH) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        ((NotificationsAdapter)lstNotifications.getAdapter()).notifyDataSetChanged();
                    } else if (callType == CallType.FIRSTTIME) {
                        Utilities.dismissLoadingDialog();
                    }

//                    lstNotifications.setAdapter(new NotificationBaseAdapter(getActivity(), getActivity().getApplicationContext(), R.layout.notifications_row_item, InflatedNotificationManagements));
                    lstNotifications.setAdapter(new NotificationsAdapter(getActivity(), R.layout.notifications_row_item, R.id.tvNotificationMessage, InflatedNotificationManagements));
                    ((NotificationsAdapter)lstNotifications.getAdapter()).notifyDataSetChanged();
                    restoreListPosition();
                }

                @Override
                public void onError(Exception exception) {
                    Utilities.showToast(getActivity(), RestMessages.getInstance().getErrorMessage());
                    if (callType == CallType.FIRSTTIME) {
                        Utilities.dismissLoadingDialog();
                    }
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
        if(!InflatedNotificationManagements.get(i).isMessageRead()) {
            Utilities.showloadingDialog(getActivity());
            new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
                @Override
                public void authenticatedRestClient(RestClient client) {
                    if (client == null) {
                        SalesforceSDKManager.getInstance().logout(getActivity());
                        return;
                    } else {
                        Map<String, Object> caseFields = new HashMap<String, Object>();
                        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                        caseFields.put("Read_Date_and_Time__c", sdf.format(new Date(System.currentTimeMillis())));
                        caseFields.put("isMessageRead__c", true);
                        try {
                            RestRequest restRequest = RestRequest.getRequestForUpdate(getActivity().getString(R.string.api_version), "Notification_Management__c", InflatedNotificationManagements.get(i).getId(), caseFields);
                            client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                                @Override
                                public void onSuccess(RestRequest request, RestResponse response) {
                                    Log.d("MyTag", "onSuccess " + response.toString());
                                    Utilities.dismissLoadingDialog();
                                    InflatedNotificationManagements.get(i).setIsMessageRead(true);
                                    ((NotificationsAdapter)lstNotifications.getAdapter()).notifyDataSetChanged();
                                    new StoreData(getActivity()).setNotificationCount(new StoreData(getActivity()).getNotificationCount()-1+"");
                                    ((BaseActivity)getActivity()).ManageBadgeNotification();
                                }

                                @Override
                                public void onError(Exception exception) {
                                    VolleyError volleyError = (VolleyError) exception;
                                    NetworkResponse response = volleyError.networkResponse;
                                    String json = new String(response.data);
                                    Log.d("", json);
                                    Utilities.dismissLoadingDialog();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }
}
