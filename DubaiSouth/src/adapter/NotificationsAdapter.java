package adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import cloudconcept.dwc.BaseActivity;
import cloudconcept.dwc.R;
import dataStorage.StoreData;
import model.NotificationManagement;
import utilities.Utilities;

/**
 * Created by Bibo on 7/28/2015.
 */
public class NotificationsAdapter extends ArrayAdapter<NotificationManagement> {


    String[] status_filter;
    String[] services;


    List<NotificationManagement> objects;
    Activity context;

    public NotificationsAdapter(Activity context, int resource, int textViewResourceId, List<NotificationManagement> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.objects = objects;
        services = new String[]{"Visa Services", "NOC Services", "License Services", "Access Card Services", "Registration Services", "Leasing Services"};
        status_filter = new String[]{"Completed", "In Process", "Ready for collection", "Not Submitted", "Application Submitted", "Draft"};
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.notifications_row_item, parent, false);
        }
        TextView tvNotificationMessage, tvDate;
        ImageView imageView;
        RatingBar ratingBar;
        RelativeLayout relative;
        tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        tvNotificationMessage = (TextView) convertView.findViewById(R.id.tvNotificationMessage);
        ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
        imageView = (ImageView) convertView.findViewById(R.id.imageNotificationRow);
        relative = (RelativeLayout) convertView.findViewById(R.id.relative);
        relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  Mark this Notification as read
                if (!objects.get(position).isMessageRead()) {
                    Utilities.showloadingDialog(context);
                    new ClientManager(context, SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(context, new ClientManager.RestClientCallback() {
                        @Override
                        public void authenticatedRestClient(RestClient client) {
                            if (client == null) {
                                SalesforceSDKManager.getInstance().logout(context);
                                return;
                            } else {
                                Map<String, Object> caseFields = new HashMap<String, Object>();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                caseFields.put("Read_Date_and_Time__c", sdf.format(new Date(System.currentTimeMillis())));
                                caseFields.put("isMessageRead__c", true);
                                try {
                                    RestRequest restRequest = RestRequest.getRequestForUpdate(context.getString(R.string.api_version), "Notification_Management__c", objects.get(position).getId(), caseFields);
                                    client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                                        @Override
                                        public void onSuccess(RestRequest request, RestResponse response) {
                                            Log.d("MyTag", "onSuccess " + response.toString());
                                            Utilities.dismissLoadingDialog();
                                            objects.get(position).setIsMessageRead(true);
                                            notifyDataSetChanged();
                                            new StoreData(context).setNotificationCount(new StoreData(context).getNotificationCount() - 1 + "");
                                            ((BaseActivity) context).ManageBadgeNotification();
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
        });

        tvNotificationMessage.setText(Utilities.stringNotNull(objects.get(position).getMobile_Compiled_Message()));
        String pattern = "yyyy-MM-dd'T'HH:mm:ss";
        SimpleDateFormat dtf = new SimpleDateFormat(pattern);

        try {
            Date dateTime = dtf.parse(Utilities.stringNotNull(objects.get(position).getCreatedDate()));

            pattern = "dd-MMM-yyyy hh:mm a";
            dtf = new SimpleDateFormat(pattern);
            tvDate.setText(dtf.format(GMTToCVT(dateTime)));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (objects.get(position).isMessageRead()) {
            relative.setBackgroundColor(context.getResources().getColor(R.color.white));
        } else {
            relative.setBackgroundColor(context.getResources().getColor(R.color.light_grey));
        }

        if (objects.get(position).getCase_Process_Name().equals(services[0])) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.notification_visa));
        } else if (objects.get(position).getCase_Process_Name().equals(services[1])) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.notification_noc));
        } else if (objects.get(position).getCase_Process_Name().equals(services[2])) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.renew_license));
        } else if (objects.get(position).getCase_Process_Name().equals(services[3])) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.notification_card_icon));
        } else if (objects.get(position).getCase_Process_Name().equals(services[4])) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.notification_registration));
        } else if (objects.get(position).getCase_Process_Name().equals(services[5])) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.notification_leasing));
        }

        if (!objects.get(position).isFeedbackAllowed()) {
            ratingBar.setVisibility(View.GONE);
        } else {
            float ratingValue = Float.parseFloat(objects.get(position).getCaseNotification().getCase_Rating_Score() == null ? "0" : objects.get(position).getCaseNotification().getCase_Rating_Score());
            ratingBar.setVisibility(View.VISIBLE);
            ratingBar.setRating(ratingValue);
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, final float v, boolean b) {
                    // Send the rating to back end
                    Map<String, Object> caseFields = new HashMap<String, Object>();
                    caseFields.put("Case_Rating_Score__c", v);
                    try {
                        Utilities.showloadingDialog(context);
                        final RestRequest restRequest = RestRequest.getRequestForUpdate(context.getString(R.string.api_version), "Case", objects.get(position).getCaseNotification().getId(), caseFields);
                        new ClientManager(context, SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(context, new ClientManager.RestClientCallback() {
                            @Override
                            public void authenticatedRestClient(final RestClient client) {
                                if (client == null) {
                                    SalesforceSDKManager.getInstance().logout(context);
                                    return;
                                } else {
                                    client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                                        @Override
                                        public void onSuccess(RestRequest request, RestResponse response) {
                                            Log.d("MyTag", "onSuccess " + response.toString());
                                            Utilities.dismissLoadingDialog();
                                            objects.get(position).getCaseNotification().setCase_Rating_Score(v + "");
                                        }

                                        @Override
                                        public void onError(Exception exception) {
                                            Log.d("MyTag", "onError ");
                                            Utilities.dismissLoadingDialog();
                                        }
                                    });
                                }
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
        }


        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    private Date GMTToCVT(Date date) {
        TimeZone tz = TimeZone.getDefault();
        Date ret = new Date(date.getTime() + tz.getRawOffset());

        // if we are now in DST, back off by the delta.  Note that we are checking the GMT date, this is the KEY.
        if (tz.inDaylightTime(ret)) {
            Date dstDate = new Date(ret.getTime() + tz.getDSTSavings());

            // check to make sure we have not crossed back into standard time
            // this happens when we are on the cusp of DST (7pm the day before the change for PDT)
            if (tz.inDaylightTime(dstDate)) {
                ret = dstDate;
            }
        }
        return ret;
    }
}
