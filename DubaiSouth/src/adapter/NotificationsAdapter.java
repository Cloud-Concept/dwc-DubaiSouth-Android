package adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cloudconcept.dwc.R;
import model.NotificationManagement;
import utilities.Utilities;

/**
 * Created by Bibo on 7/28/2015.
 */
public class NotificationsAdapter extends ClickableListAdapter {

    Context context;
    android.app.Activity act;
    String[] status_filter;
    String[] services;

    public NotificationsAdapter(Activity act, Context context, int viewid, ArrayList<NotificationManagement> notificationManagements) {
        super(context, viewid, notificationManagements);
        this.context = context;
        this.act = act;
        services = new String[]{"Visa Services", "NOC Services", "License Services", "Access Cards Services", "Registration Services", "Leasing Services"};
        status_filter = new String[]{"Completed", "In Process", "Ready for collection", "Not Submitted", "Application Submitted", "Draft"};
    }

    @Override
    protected ViewHolder createHolder(View v) {

        TextView tvNotificationMessage, tvDate;
        ImageView imageView;
        RatingBar ratingBar;
        tvDate = (TextView) v.findViewById(R.id.tvDate);
        tvNotificationMessage = (TextView) v.findViewById(R.id.tvNotificationMessage);
        ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);
        imageView = (ImageView) v.findViewById(R.id.imageNotificationRow);
        NotificationsViewHolder holder = new NotificationsViewHolder(tvNotificationMessage, tvDate, imageView, ratingBar);
        return holder;
    }

    @Override
    protected void bindHolder(ViewHolder h) {
        final NotificationsViewHolder mvh = (NotificationsViewHolder) h;
        NotificationManagement notificationManagement = (NotificationManagement) mvh.data;

        mvh.tvNotificationMessage.setText(Utilities.stringNotNull(notificationManagement.getMobile_Compiled_Message()));
        String pattern = "yyyy-MM-dd'T'HH:mm:ss";
        SimpleDateFormat dtf =new SimpleDateFormat(pattern);
        try {
            Date dateTime = dtf.parse(Utilities.stringNotNull(notificationManagement.getCreatedDate()));
            pattern = "dd-MMM-yyyy hh:mm a";
            dtf=new SimpleDateFormat(pattern);
            mvh.tvDate.setText(dtf.format(dateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (notificationManagement.getCase_Process_Name().equals(services[0])) {
            mvh.imageView.setImageDrawable(act.getResources().getDrawable(R.mipmap.notification_visa));
        } else if (notificationManagement.getCase_Process_Name().equals(services[1])) {
            mvh.imageView.setImageDrawable(act.getResources().getDrawable(R.mipmap.notification_noc));
        } else if (notificationManagement.getCase_Process_Name().equals(services[2])) {
            mvh.imageView.setImageDrawable(act.getResources().getDrawable(R.mipmap.renew_license));
        } else if (notificationManagement.getCase_Process_Name().equals(services[3])) {
            mvh.imageView.setImageDrawable(act.getResources().getDrawable(R.mipmap.notification_license));
        } else if (notificationManagement.getCase_Process_Name().equals(services[4])) {
            mvh.imageView.setImageDrawable(act.getResources().getDrawable(R.mipmap.notification_registration));
        } else if (notificationManagement.getCase_Process_Name().equals(services[5])) {
            mvh.imageView.setImageDrawable(act.getResources().getDrawable(R.mipmap.notification_leasing));
        }

        if (notificationManagement.getCaseNotification().getCase_Rating_Score() == null) {
            mvh.ratingBar.setVisibility(View.GONE);
        } else {
            float ratingValue = Float.parseFloat(notificationManagement.getCaseNotification().getCase_Rating_Score());
            mvh.ratingBar.setRating(ratingValue);
        }


    }

    static class NotificationsViewHolder extends ViewHolder {
        TextView tvNotificationMessage, tvDate;
        ImageView imageView;
        RatingBar ratingBar;

        public NotificationsViewHolder(TextView tvNotificationMessage, TextView tvDate, ImageView imageView, RatingBar ratingBar) {
            this.tvNotificationMessage = tvNotificationMessage;
            this.tvDate = tvDate;
            this.imageView = imageView;
            this.ratingBar = ratingBar;
        }
    }
}
