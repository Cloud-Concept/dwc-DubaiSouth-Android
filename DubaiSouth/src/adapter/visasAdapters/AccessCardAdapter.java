package adapter.visasAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import adapter.ClickableListAdapter;
import adapter.HorizontalListViewAdapter;
import cloudconcept.dwc.R;
import custom.DWCRoundedImageView;
import custom.HorizontalListView;
import custom.expandableView.ExpandableLayoutItem;
import model.Card_Management__c;
import model.ServiceItem;
import utilities.Utilities;

/**
 * Created by Abanoub on 7/2/2015.
 */
public class AccessCardAdapter extends ClickableListAdapter {

    private final Activity act;
    Context context;

    public AccessCardAdapter(Activity act, Context context, int viewid, List objects) {
        super(context, viewid, objects);
        this.context = context;
        this.act = act;
    }

    @Override
    protected ViewHolder createHolder(View v) {

        TextView tvFullName, tvType, tvPassportNumber, tvStatus;
        TextView tvCardExpiry;
        DWCRoundedImageView _smartEmployeeImage;

        final ExpandableLayoutItem item = (ExpandableLayoutItem) v.findViewById(R.id.expandableLayoutListView);
        RelativeLayout relativeHeader = item.getHeaderLayout();
        tvFullName = (TextView) relativeHeader.findViewById(R.id.tvFullName);
        tvType = (TextView) relativeHeader.findViewById(R.id.tvType);
        tvPassportNumber = (TextView) relativeHeader.findViewById(R.id.tvpassportNumber);
        tvStatus = (TextView) relativeHeader.findViewById(R.id.tvStatus);
        tvCardExpiry = (TextView) relativeHeader.findViewById(R.id.tvCardExpiry);
//        tvCardNumber = (TextView) relativeHeader.findViewById(R.id.tvCardNumber);
        _smartEmployeeImage = (DWCRoundedImageView) relativeHeader.findViewById(R.id.view);

        RelativeLayout relativeContent = item.getContentLayout();
        HorizontalListView _horizontalServices = (HorizontalListView) relativeContent.findViewById(R.id.horizontalServices);

        AccessCardViewHolder holder = new AccessCardViewHolder(tvFullName, tvType, tvPassportNumber, tvStatus, tvCardExpiry, _smartEmployeeImage, item, _horizontalServices);

        return holder;
    }

    @Override
    protected void bindHolder(ViewHolder h) {
        AccessCardViewHolder holder = (AccessCardViewHolder) h;
        Card_Management__c _cardManagement = (Card_Management__c) holder.data;
//        holder.tvCardNumber.setText(Utilities.stringNotNull(_cardManagement.getCard_Number__c()));
        holder.tvFullName.setText(Utilities.stringNotNull(_cardManagement.getFull_Name__c()));
        holder.tvType.setText(Utilities.stringNotNull(_cardManagement.getCard_Type__c()));
        holder.tvPassportNumber.setText(Utilities.stringNotNull(_cardManagement.getPassport_Number__c()));
        holder.tvStatus.setText(Utilities.stringNotNull(_cardManagement.getStatus__c()));
        holder.tvCardExpiry.setText(_cardManagement.getCard_Expiry_Date__c() == null ? "" : Utilities.formatVisitVisaDate(_cardManagement.getCard_Expiry_Date__c()));
        if (_cardManagement.getPersonal_Photo__c() != null && !_cardManagement.getPersonal_Photo__c().equals(""))
            Utilities.setUserPhoto(act, _cardManagement.getPersonal_Photo__c(), holder._smartEmployeeImage);

        ArrayList<ServiceItem> _items = new ArrayList<ServiceItem>();

        Calendar _calendar = Calendar.getInstance();
        if(_cardManagement.getCard_Expiry_Date__c()!=null&&!_cardManagement.getCard_Expiry_Date__c().equals("")){
            int DaysToExpire = (int) Utilities.daysDifference(_cardManagement.getCard_Expiry_Date__c());

            if (_cardManagement.getStatus__c().equals("Expired") || (_cardManagement.getStatus__c().equals("Active") && DaysToExpire < 7)) {
                _items.add(new ServiceItem("Renew Card", R.mipmap.renew_card));
            }
        }

        if (_cardManagement.getStatus__c().equals("Active")) {
            _items.add(new ServiceItem("Cancel Card", R.mipmap.cancel_card));
            _items.add(new ServiceItem("Replace Card", R.mipmap.replace_card));
        }


        _items.add(new ServiceItem("Show Details", R.mipmap.service_show_details));

//        holder.item.setOnClickListener(new OnClickListener(holder) {
//
//            public void onClick(View v, ViewHolder viewHolder) {
//                if (viewHolder != null) {
//                    AccessCardViewHolder myViewHolder = (AccessCardViewHolder) viewHolder;
//                    if (!myViewHolder.item.isOpened()) {
//                        myViewHolder.item.show();
//                    } else {
//                        myViewHolder.item.hide();
//                    }
//                }
//            }
//        });

        holder._horizontalListView.setAdapter(new HorizontalListViewAdapter(_cardManagement, act, context, _items));
    }

    static class AccessCardViewHolder extends ViewHolder {

        TextView tvFullName, tvType, tvPassportNumber, tvStatus, tvCardExpiry;
        DWCRoundedImageView _smartEmployeeImage;
        ExpandableLayoutItem item;
        HorizontalListView _horizontalListView;

//        public AccessCardViewHolder(TextView tvFullName, TextView tvType, TextView tvPassportNumber, TextView tvStatus, DWCRoundedImageView _smartEmployeeImage, ExpandableLayoutItem item, HorizontalListView _horizontalListView) {
//            this.tvFullName = tvFullName;
//            this.tvType = tvType;
//            this.tvPassportNumber = tvPassportNumber;
//            this.tvStatus = tvStatus;
////            this.tvCardExpiry = tvCardExpiry;
////            this.tvCardNumber = tvCardNumber;
//            this._smartEmployeeImage = _smartEmployeeImage;
//            this.item = item;
//            this._horizontalListView = _horizontalListView;
//        }


        public AccessCardViewHolder(TextView tvFullName, TextView tvType, TextView tvPassportNumber, TextView tvStatus, TextView tvCardExpiry, DWCRoundedImageView _smartEmployeeImage, ExpandableLayoutItem item, HorizontalListView _horizontalListView) {
            this.tvFullName = tvFullName;
            this.tvType = tvType;
            this.tvPassportNumber = tvPassportNumber;
            this.tvStatus = tvStatus;
            this.tvCardExpiry = tvCardExpiry;
            this._smartEmployeeImage = _smartEmployeeImage;
            this.item = item;
            this._horizontalListView = _horizontalListView;
        }
    }
}
