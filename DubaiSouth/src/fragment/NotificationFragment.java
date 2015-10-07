package fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
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
import adapter.NotificationsAdapter;
import cloudconcept.dwc.BaseActivity;
import cloudconcept.dwc.R;
import custom.PullAndLoadListView;
import custom.PullToRefreshListView;
import dataStorage.StoreData;
import model.NotificationManagement;
import model.User;
import utilities.CallType;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 6/17/2015.
 */
public class NotificationFragment extends Fragment {

    PullAndLoadListView pullAndLoadListViewNotifications;
    private int limit = 10;
    private int offset = 0;
    private String soqlQuery;
    private RestRequest restRequest;
    private String loadMoreResponse = "";
    private ArrayList<NotificationManagement> InflatedNotificationManagements;
    private int top;
    private int index;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notifications, container, false);
        pullAndLoadListViewNotifications = (PullAndLoadListView) view.findViewById(R.id.pullandloadNotifications);
        pullAndLoadListViewNotifications.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                limit = 10;
                offset = 0;
                getListPosition();
                new PullToRefreshNotificationsTask(offset, limit).execute();
            }
        });

        pullAndLoadListViewNotifications.setOnLoadMoreListener(new PullAndLoadListView.OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                offset += 10;
                getListPosition();
                new LoadMoreNotificationsTask(offset, limit, null).execute();
            }
        });

        InflatedNotificationManagements = new ArrayList<NotificationManagement>();
        pullAndLoadListViewNotifications.onRefresh();
        return view;
    }

    public class PullToRefreshNotificationsTask extends AsyncTask<Void, Void, Void> {

        private int offset;
        private int limit;

        public PullToRefreshNotificationsTask(int offset, int limit) {
            this.limit = limit;
            this.offset = offset;
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
            new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
                @Override
                public void authenticatedRestClient(RestClient client) {
                    if (client == null) {
                        SalesforceSDKManager.getInstance().logout(getActivity());
                        return;
                    } else {
                        new LoadMoreNotificationsTask(offset, limit, client).execute();

                    }
                }
            });
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onCancelled() {
            pullAndLoadListViewNotifications.onRefreshComplete();
        }
    }

    public class LoadMoreNotificationsTask extends AsyncTask<Void, Void, String> {

        private int offset;
        private int limit;
        private RestClient client;
        private String result;

        public LoadMoreNotificationsTask(int offset, int limit, RestClient client) {
            this.limit = limit;
            this.offset = offset;
            this.client = client;
        }

        @Override
        protected String doInBackground(Void... params) {

            if (isCancelled()) {
                return null;
            }



            if (client != null) {
                offset = 0;
                InflatedNotificationManagements = new ArrayList<NotificationManagement>();
                Gson g=new Gson();
                User user= g.fromJson(new StoreData(getActivity()).getUserDataAsString(),User.class);
                String attUrl = client.getClientInfo().resolveUrl("/services/apexrest/MobileNotificationsReadWebService?AccountId="+user.get_contact().get_account().getID()).toString();

                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httppost = new HttpGet(attUrl);
                httppost.setHeader("Authorization", "Bearer " + client.getAuthToken());
                StringEntity entity = null;
                try {



                    HttpResponse response = httpclient.execute(httppost);
                    result = EntityUtils.toString(response.getEntity());
                    Log.d("result", result);
                    return result.toLowerCase().contains("success") ? "success" : result;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{

            }

            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            Utilities.showloadingDialog(getActivity());
            if (client != null)
                if (aVoid.equals("success")) {
                    new StoreData(getActivity()).setNotificationCount("0");
                    ((BaseActivity)getActivity()).ManageBadgeNotification();
                    CallOnRefreshNotificationService(CallType.REFRESH, offset, limit, client);

                }
                else {

                    Utilities.showLongToast(getActivity(), aVoid == null ? "There is an error" : aVoid.replace("\"", ""));
                }else{
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
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onCancelled() {
            pullAndLoadListViewNotifications.onRefreshComplete();
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
                    if (result.toString().equals(loadMoreResponse)) {
                       // pullAndLoadListViewNotifications.setOnLoadMoreListener(null);
                    }
                    loadMoreResponse = result.toString();
                    if (callType == CallType.LOADMORE) {
                        pullAndLoadListViewNotifications.onLoadMoreComplete();
                        pullAndLoadListViewNotifications.setScrollingCacheEnabled(true);

                    } else if (callType == CallType.REFRESH) {
                        pullAndLoadListViewNotifications.onRefreshComplete();
                    }
                    Utilities.dismissLoadingDialog();

                    pullAndLoadListViewNotifications.setAdapter(new NotificationsAdapter(getActivity(), getActivity().getApplicationContext(), R.layout.notifications_row_item, InflatedNotificationManagements));
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
    public void getListPosition(){
         index = pullAndLoadListViewNotifications.getFirstVisiblePosition();
        View v = pullAndLoadListViewNotifications.getChildAt(0);
         top = (v == null) ? 0 : (v.getTop() - pullAndLoadListViewNotifications.getPaddingTop());
    }
    public void restoreListPosition(){
        pullAndLoadListViewNotifications.setSelectionFromTop(index, top);
    }
}
