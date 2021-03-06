package adapter.visasAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adapter.ClickableListAdapter;
import adapter.HorizontalListViewAdapter;
import cloudconcept.dwc.R;
import custom.DWCRoundedImageView;
import custom.HorizontalListView;
import custom.expandableView.ExpandableLayoutItem;
import dataStorage.StoreData;
import model.ServiceItem;
import model.User;
import model.Visa;
import utilities.Utilities;

/**
 * Created by Abanoub on 7/2/2015.
 */
public class PermanentEmployeeListAdapter extends ClickableListAdapter {

    Context context;
    Activity act;

    public PermanentEmployeeListAdapter(Activity act, Context context, int viewid, List objects) {
        super(context, viewid, objects);
        this.context = context;
        this.act = act;
    }

    @Override
    protected ViewHolder createHolder(View v) {

        TextView tvFullName, tvPassportNumber, tvStatus, tvVisaExpiry, tvVisaExpiryLabel;
        DWCRoundedImageView _smartEmployeeImage;

        final ExpandableLayoutItem item = (ExpandableLayoutItem) v.findViewById(R.id.expandableLayoutListView);

        RelativeLayout relativeHeader = item.getHeaderLayout();
        tvFullName = (TextView) relativeHeader.findViewById(R.id.tvFullName);
        tvVisaExpiry = (TextView) relativeHeader.findViewById(R.id.tvVisaExpiry);
        tvPassportNumber = (TextView) relativeHeader.findViewById(R.id.tvpassportNumber);
        tvStatus = (TextView) relativeHeader.findViewById(R.id.tvStatus);
        tvVisaExpiryLabel = (TextView) relativeHeader.findViewById(R.id.tvVisaExpiryLabel);
        _smartEmployeeImage = (DWCRoundedImageView) relativeHeader.findViewById(R.id.view);

        RelativeLayout relativeContent = item.getContentLayout();

        HorizontalListView _horizontalServices = (HorizontalListView) relativeContent.findViewById(R.id.horizontalServices);
        VisaViewHolder mvh = new VisaViewHolder(tvFullName, tvVisaExpiryLabel, tvVisaExpiry, tvPassportNumber, tvStatus, _smartEmployeeImage, item, _horizontalServices);

        return mvh;
    }

    @Override
    protected void bindHolder(ViewHolder h) {

        final VisaViewHolder mvh = (VisaViewHolder) h;
        Visa mo = (Visa) mvh.data;

        mvh.tvFullName.setText(mo.getApplicant_Full_Name__c());
        mvh.tvStatus.setText(mo.getVisa_Validity_Status__c());
        mvh.tvPassportNumber.setText(mo.getPassport_Number__c());
        if (mo.getPersonal_Photo__c() != null && !mo.getPersonal_Photo__c().equals(""))
            Utilities.setUserPhoto(act, mo.getPersonal_Photo__c(), mvh._smartEmployeeImage);

        ArrayList<ServiceItem> _items = new ArrayList<ServiceItem>();


//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//
//        Date VisaDate = null;
//        try {
//            if (mo.getVisa_Expiry_Date__c() != null) {
//                VisaDate = sdf.parse(mo.getVisa_Expiry_Date__c());
//            } else {
//                VisaDate = null;
//            }
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        if (mo.getVisa_Validity_Status__c().equals("Issued")) {
            mvh.tvVisaExpiry.setText(Utilities.formatVisitVisaDate(mo.getVisa_Expiry_Date__c()));
            _items.add(new ServiceItem("New NOC", R.mipmap.noc_service_image));
        } else {
            mvh.tvVisaExpiry.setVisibility(View.GONE);
            mvh.tvVisaExpiryLabel.setVisibility(View.GONE);
        }

        User user = new Gson().fromJson(new StoreData(context).getUserDataAsString(), User.class);
        boolean manager = mo.getVisa_Holder__c().equals(user.get_contact().get_account().getID());
        if ((mo.getVisa_Validity_Status__c().equals("Issued") || mo.getVisa_Validity_Status__c().equals("Expired"))
                &&
                (mo.getVisa_Type__c().equals("Employment") || mo.getVisa_Type__c().equals("Transfer - Internal") || mo.getVisa_Type__c().equals("Transfer - External"))) {
            _items.add(new ServiceItem("Renew Visa", R.mipmap.renew_visa));
        }

        if (mo.getVisa_Validity_Status__c().equals("Issued")) {
            _items.add(new ServiceItem("Renew Passport", R.mipmap.noc_service_image));
        }

        if ((mo.getVisa_Validity_Status__c().equals("Issued") || mo.getVisa_Validity_Status__c().equals("Under Process") || mo.getVisa_Validity_Status__c().equals("Under Renewal")) && (mo.getVisa_Type__c().equals("Employment") || mo.getVisa_Type__c().equals("Transfer - Internal") || mo.getVisa_Type__c().equals("Transfer - External")) && !manager) {
            _items.add(new ServiceItem("Cancel Visa", R.mipmap.cancel_visa));
        }


//Deprecated By Ahmed Ahdel New Requirement Date:28/09/2015

//
//        if (mo.getVisa_Validity_Status__c().equals("Issued")) {
//
//            _items.add(new ServiceItem("New NOC", R.mipmap.noc_service_image));
//            _items.add(new ServiceItem("Renew Passport", R.mipmap.renew_license));
//            if (mo.getVisa_Type__c().equals("Employment") || mo.getVisa_Type__c().equals("Transfer - Internal") || mo.getVisa_Type__c().equals("Transfer - External")) {
//                _items.add(new ServiceItem("Renew Visa", R.mipmap.renew_visa));
//                _items.add(new ServiceItem("Cancel Visa", R.mipmap.cancel_visa));
//            }
//
//        } else if (VisaDate != null) {
//            if (AutomaticUtilities.daysBetween(VisaDate, System.currentTimeMillis()) < 60) {
//                _items.add(new ServiceItem("Renew Visa", R.mipmap.renew_visa));
//            }
//        }

        _items.add(new ServiceItem("Show Details", R.mipmap.service_show_details));
//        mvh.item.setOnClickListener(new OnClickListener(mvh) {
//
//            public void onClick(View v, ViewHolder viewHolder) {
//                if (viewHolder != null) {
//                    VisaViewHolder myViewHolder = (VisaViewHolder) viewHolder;
//                    if (!myViewHolder.item.isOpened()) {
//                        myViewHolder.item.show();
//                    } else {
//                        myViewHolder.item.hide();
//                    }
//                }
//            }
//        });

        mvh._horizontalListView.setAdapter(new HorizontalListViewAdapter(mo, act, context, _items));
//        mvh._horizontalListView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_MOVE:
//                        view.getParent().requestDisallowInterceptTouchEvent(true);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_CANCEL:
//                        view.getParent().requestDisallowInterceptTouchEvent(false);
//                        break;
//                }
//                return false;
//            }
//        });
    }

    static class VisaViewHolder extends ViewHolder {

        TextView tvFullName, tvVisaExpiry, tvPassportNumber, tvStatus;
        DWCRoundedImageView _smartEmployeeImage;
        ExpandableLayoutItem item;
        HorizontalListView _horizontalListView;
        TextView tvVisaExpiryLabel;

        public VisaViewHolder(TextView tvFullName, TextView tvVisaExpiryLabel, TextView tvVisaExpiry, TextView tvPassportNumber, TextView tvStatus, DWCRoundedImageView i, ExpandableLayoutItem item, HorizontalListView _horizontalListView) {
            this.tvFullName = tvFullName;
            this._smartEmployeeImage = i;
            this.tvPassportNumber = tvPassportNumber;
            this.tvVisaExpiry = tvVisaExpiry;
            this.tvStatus = tvStatus;
            this.item = item;
            this._horizontalListView = _horizontalListView;
            this.tvVisaExpiryLabel = tvVisaExpiryLabel;
        }
    }
}