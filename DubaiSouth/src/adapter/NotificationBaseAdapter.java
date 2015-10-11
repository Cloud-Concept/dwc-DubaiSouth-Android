package adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
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
import custom.DWCRoundedImageView;
import model.NotificationManagement;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 10/10/2015.
 */
public class NotificationBaseAdapter extends ClickableListAdapter {

    private String[] status_filter;
    private String[] services;
    private List objects;
    Context context;
    Activity act;

    public NotificationBaseAdapter(Activity act, Context context, int viewid, List objects) {
        super(context, viewid, objects);
        this.context = context;
        this.act = act;
        this.objects = objects;
        services = new String[]{"Visa Services", "NOC Services", "License Services", "Access Card Services", "Registration Services", "Leasing Services"};
        status_filter = new String[]{"Completed", "In Process", "Ready for collection", "Not Submitted", "Application Submitted", "Draft"};
    }

    @Override
    protected ViewHolder createHolder(View v) {
        TextView tvNotificationMessage, tvDate;
        DWCRoundedImageView imageView;
        RelativeLayout relativeLayout;
        RatingBar ratingBar;
        tvDate = (TextView) v.findViewById(R.id.tvDate);
        tvNotificationMessage = (TextView) v.findViewById(R.id.tvNotificationMessage);
        ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);
        imageView = (DWCRoundedImageView) v.findViewById(R.id.imageNotificationRow);
        relativeLayout = (RelativeLayout) v.findViewById(R.id.relative);
        ViewHolder holder = new ViewHolder(tvNotificationMessage, tvDate, imageView, ratingBar, relativeLayout);
        return holder;
    }

    @Override
    protected void bindHolder(ClickableListAdapter.ViewHolder h) {
        final ViewHolder mvh = (ViewHolder) h;
        final NotificationManagement mo = (NotificationManagement) mvh.data;

        mvh.tvNotificationMessage.setText(Utilities.stringNotNull(mo.getMobile_Compiled_Message()));
        String pattern = "yyyy-MM-dd'T'HH:mm:ss";
        SimpleDateFormat dtf = new SimpleDateFormat(pattern);
        try {
            Date dateTime = dtf.parse(Utilities.stringNotNull(mo.getCreatedDate()));
            pattern = "dd-MMM-yyyy hh:mm a";
            dtf = new SimpleDateFormat(pattern);
            mvh.tvDate.setText(dtf.format(dateTime).substring(0, 11));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (mo.isMessageRead()) {
            mvh.relative.setBackgroundColor(act.getResources().getColor(R.color.noc_grey));
        } else {
            mvh.relative.setBackgroundColor(act.getResources().getColor(R.color.white));
        }

        if (mo.getCase_Process_Name().equals(services[0])) {
            mvh.imageView.setImageResource(R.mipmap.notification_visa);
        } else if (mo.getCase_Process_Name().equals(services[1])) {
            mvh.imageView.setImageResource(R.mipmap.notification_noc);
        } else if (mo.getCase_Process_Name().equals(services[2])) {
            mvh.imageView.setImageResource(R.mipmap.notification_license);
        } else if (mo.getCase_Process_Name().equals(services[3])) {
            mvh.imageView.setImageResource(R.mipmap.notification_card_icon);
        } else if (mo.getCase_Process_Name().equals(services[4])) {
            mvh.imageView.setImageResource(R.mipmap.notification_registration);
        } else if (mo.getCase_Process_Name().equals(services[5])) {
            mvh.imageView.setImageResource(R.mipmap.notification_leasing);
        } else {
            mvh.imageView.setImageResource(R.mipmap.notification_leasing);
        }

        if (!mo.isFeedbackAllowed()) {
            mvh.ratingBar.setVisibility(View.GONE);
        } else {
            float ratingValue = Float.parseFloat(mo.getCaseNotification().getCase_Rating_Score() == null ? "0" : mo.getCaseNotification().getCase_Rating_Score());
            mvh.ratingBar.setVisibility(View.VISIBLE);
            mvh.ratingBar.setRating(ratingValue);
            mvh.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, final float v, boolean b) {
                    // Send the rating to back end
                    Map<String, Object> caseFields = new HashMap<String, Object>();
                    caseFields.put("Case_Rating_Score__c", v);
                    try {

                        final RestRequest restRequest = RestRequest.getRequestForUpdate(context.getString(R.string.api_version), "Case", mo.getCaseNotification().getId(), caseFields);
                        new ClientManager(context, SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(act, new ClientManager.RestClientCallback() {
                            @Override
                            public void authenticatedRestClient(final RestClient client) {
                                if (client == null) {
                                    SalesforceSDKManager.getInstance().logout(act);
                                    return;
                                } else {
                                    client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                                        @Override
                                        public void onSuccess(RestRequest request, RestResponse response) {
                                            Log.d("MyTag", "onSuccess " + response.toString());
                                            mo.getCaseNotification().setCase_Rating_Score(v + "");
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
    }

    public static class ViewHolder extends ClickableListAdapter.ViewHolder {
        private RelativeLayout relative;
        TextView tvNotificationMessage, tvDate;
        DWCRoundedImageView imageView;
        RatingBar ratingBar;

        public ViewHolder(TextView tvNotificationMessage, TextView tvDate, DWCRoundedImageView imageView, RatingBar ratingBar, RelativeLayout relativeLayout) {
            this.tvNotificationMessage = tvNotificationMessage;
            this.tvDate = tvDate;
            this.imageView = imageView;
            this.ratingBar = ratingBar;
            this.relative = relativeLayout;
        }
    }
}
