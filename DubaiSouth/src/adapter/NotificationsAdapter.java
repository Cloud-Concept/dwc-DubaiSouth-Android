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
import android.widget.TextView;

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

import cloudconcept.dwc.R;
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
        services = new String[]{"Visa Services", "NOC Services", "License Services", "Access Cards Services", "Registration Services", "Leasing Services"};
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
        tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        tvNotificationMessage = (TextView) convertView.findViewById(R.id.tvNotificationMessage);
        ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
        imageView = (ImageView) convertView.findViewById(R.id.imageNotificationRow);


        tvNotificationMessage.setText(Utilities.stringNotNull(objects.get(position).getMobile_Compiled_Message()));
        String pattern = "yyyy-MM-dd'T'HH:mm:ss";
        SimpleDateFormat dtf = new SimpleDateFormat(pattern);
        try {
            Date dateTime = dtf.parse(Utilities.stringNotNull(objects.get(position).getCreatedDate()));
            pattern = "dd-MMM-yyyy hh:mm a";
            dtf = new SimpleDateFormat(pattern);
            tvDate.setText(dtf.format(dateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (objects.get(position).getCase_Process_Name().equals(services[0])) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.notification_visa));
        } else if (objects.get(position).getCase_Process_Name().equals(services[1])) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.notification_noc));
        } else if (objects.get(position).getCase_Process_Name().equals(services[2])) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.renew_license));
        } else if (objects.get(position).getCase_Process_Name().equals(services[3])) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.notification_license));
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
                                            objects.get(position).getCaseNotification().setCase_Rating_Score(v + "");
                                        }

                                        @Override
                                        public void onError(Exception exception) {
                                            Log.d("MyTag", "onError ");
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

}
