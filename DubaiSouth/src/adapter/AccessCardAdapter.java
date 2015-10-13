package adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cloudconcept.dwc.R;
import custom.DWCRoundedImageView;
import custom.HorizontalListView;
import custom.expandableView.ExpandableLayoutItem;
import model.Card_Management__c;
import model.ServiceItem;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 10/13/2015.
 */
public class AccessCardAdapter extends ArrayAdapter<Card_Management__c> {

    private Activity context;
    private List<Card_Management__c> card_management__cs;
    private boolean isFound;

    public AccessCardAdapter(Activity context, int resource, int textViewResourceId, List<Card_Management__c> card_management__cs) {
        super(context, resource, textViewResourceId, card_management__cs);
        this.card_management__cs = card_management__cs;
        this.context = context;
    }


    public void add(Card_Management__c card_management__c) {
        card_management__cs.add(card_management__c);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Card_Management__c> card_management__c) {
        card_management__cs.addAll(card_management__c);
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AccessCardViewHolder holder;
        Card_Management__c _cardManagement;
        isFound = false;
        _cardManagement = (Card_Management__c) card_management__cs.get(position);
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_row_access_card, parent, false);
            holder = new AccessCardViewHolder();
            holder.item = (ExpandableLayoutItem) convertView.findViewById(R.id.expandableLayoutListView);
            RelativeLayout relativeHeader = holder.item.getHeaderLayout();
            holder.tvFullName = (TextView) relativeHeader.findViewById(R.id.tvFullName);
            holder.tvType = (TextView) relativeHeader.findViewById(R.id.tvType);
            holder.tvPassportNumber = (TextView) relativeHeader.findViewById(R.id.tvpassportNumber);
            holder.tvStatus = (TextView) relativeHeader.findViewById(R.id.tvStatus);
            holder.tvCardExpiry = (TextView) relativeHeader.findViewById(R.id.tvCardExpiry);
//        tvCardNumber = (TextView) relativeHeader.findViewById(R.id.tvCardNumber);
            holder._smartEmployeeImage = (DWCRoundedImageView) relativeHeader.findViewById(R.id.view);
            RelativeLayout relativeContent = holder.item.getContentLayout();
            holder._horizontalListView = (HorizontalListView) relativeContent.findViewById(R.id.horizontalServices);
            convertView.setTag(holder);

        } else {
            holder = (AccessCardViewHolder) convertView.getTag();
        }
//        tvCardNumber.setText(Utilities.stringNotNull(_cardManagement.getCard_Number__c()));
        holder.tvFullName.setText(Utilities.stringNotNull(_cardManagement.getFull_Name__c()));
        holder.tvType.setText(Utilities.stringNotNull(_cardManagement.getCard_Type__c()));
        holder.tvPassportNumber.setText(Utilities.stringNotNull(_cardManagement.getPassport_Number__c()));
        holder.tvStatus.setText(Utilities.stringNotNull(_cardManagement.getStatus__c()));
        holder.tvCardExpiry.setText(_cardManagement.getCard_Expiry_Date__c() == null ? "" : Utilities.formatVisitVisaDate(_cardManagement.getCard_Expiry_Date__c()));

        ArrayList<ServiceItem> _items = new ArrayList<ServiceItem>();

//        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
//        File folder = new File(extStorageDirectory, "attachment-export");
//        if (folder.exists()) {
//            ArrayList<String> filesname = Utilities.getListOfAttachments();
//            for (int i = 0; i < filesname.size(); i++) {
//                if (filesname.get(i).equals(_cardManagement.getPersonal_Photo__c())) {
//                    isFound = true;
//                    break;
//                }
//            }
//        }
//        if (isFound) {
//            String path = Environment.getExternalStorageDirectory() + "/attachment-export/" + _cardManagement.getPersonal_Photo__c();
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
//            holder._smartEmployeeImage.setImageBitmap(bitmap);
//        } else {

//        }

        if (_cardManagement.getPersonal_Photo__c() != null && !_cardManagement.getPersonal_Photo__c().equals(""))
            Utilities.setUserPhoto(context, _cardManagement.getPersonal_Photo__c(), holder._smartEmployeeImage);

        Calendar _calendar = Calendar.getInstance();
        if (_cardManagement.getCard_Expiry_Date__c() != null && !_cardManagement.getCard_Expiry_Date__c().equals("")) {
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

        holder._horizontalListView.setAdapter(new HorizontalListViewAdapter(_cardManagement, context, context, _items));

        return convertView;
    }

    static class AccessCardViewHolder {

        TextView tvFullName, tvType, tvPassportNumber, tvStatus, tvCardExpiry;
        DWCRoundedImageView _smartEmployeeImage;
        ExpandableLayoutItem item;
        HorizontalListView _horizontalListView;

    }
}
