package adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cloudconcept.dwc.R;
import custom.HorizontalListView;
import custom.expandableView.ExpandableLayoutItem;
import model.Company_Documents__c;
import model.ServiceItem;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 9/21/2015.
 */
public class ClickableCustomerDocumentsAdapter extends ClickableListAdapter {

    private final ArrayList<Company_Documents__c> companyDocuments;
    private final Context context;
    private final Activity activity;

    public ClickableCustomerDocumentsAdapter(Activity act, Context context, int viewid, List objects) {
        super(context, viewid, objects);
        this.context = context;
        this.activity = act;
        companyDocuments = (ArrayList<Company_Documents__c>) objects;
    }

    @Override
    protected ViewHolder createHolder(View itemView) {
        ExpandableLayoutItem item = (ExpandableLayoutItem) itemView.findViewById(R.id.expandableLayoutItemTrueCopy);
        RelativeLayout relativeHeader = item.getHeaderLayout();
        TextView tvCompanyDocumentName = (TextView) relativeHeader.findViewById(R.id.tvCompanyDocumentName);
        TextView tvDate = (TextView) relativeHeader.findViewById(R.id.tvDate);
        TextView tvVersion = (TextView) relativeHeader.findViewById(R.id.tvVersion);
        RelativeLayout relativeContent = item.getContentLayout();
        HorizontalListView horizontalServices = (HorizontalListView) relativeContent.findViewById(R.id.horizontalServices);
        CustomerDocumentsViewHolder holder = new CustomerDocumentsViewHolder(tvCompanyDocumentName, tvDate, tvVersion, item, horizontalServices);
        return holder;
    }

    @Override
    protected void bindHolder(ViewHolder h) {
        final CustomerDocumentsViewHolder mvh = (CustomerDocumentsViewHolder) h;
        Company_Documents__c company_documents__c = (Company_Documents__c) mvh.data;
        mvh.tvCompanyDocumentName.setText(company_documents__c.getName());
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        if (company_documents__c.getCreatedDate() != null && !Utilities.stringNotNull(company_documents__c.getCreatedDate().getTime().toString()).equals("")) {
            String strDate = sdfDate.format(company_documents__c.getCreatedDate().getTime());
            mvh.tvDate.setText("Date:" + strDate);
        } else {
            mvh.tvDate.setText("");
        }
        mvh.tvVersion.setText("Version: V." + String.valueOf(company_documents__c.getVersion__c()));

        ArrayList<ServiceItem> _items = new ArrayList<ServiceItem>();
        if (company_documents__c.getAttachment_Id__c() != null && !company_documents__c.getAttachment_Id__c().equals("")) {
            _items.add(new ServiceItem("Preview", R.mipmap.preview));
        }
        _items.add(new ServiceItem("Edit", R.mipmap.edit));

        mvh.horizontalServices.setAdapter(new HorizontalListViewAdapter(company_documents__c, activity, context, _items));

//        mvh.item.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mvh != null) {
//                    if (!mvh.item.isOpened()) {
//                        mvh.item.show();
//                    } else {
//                        mvh.item.hide();
//                    }
//                }
//            }
//        });
    }

    public boolean addAll(ArrayList<Company_Documents__c> strings) {

        boolean returnedProgress = false;
        int startIndex = companyDocuments.size();
        ArrayList<Company_Documents__c> company_documents__cs = new ArrayList<>();
        for (int i = 0; i < strings.size(); i++) {
            boolean found = false;
            for (int j = 0; j < companyDocuments.size(); j++) {
                if (companyDocuments.get(j).getId().equals(strings.get(i).getId())) {
                    found = true;
                    break;
                }
                if (!found) {
                    company_documents__cs.add(strings.get(i));
                }
            }
        }
        if (company_documents__cs.size() > 0) {
            companyDocuments.addAll(company_documents__cs);
            notifyDataSetChanged();
        }
        return returnedProgress;
    }


    static class CustomerDocumentsViewHolder extends ViewHolder {

        private TextView tvCompanyDocumentName;
        private TextView tvDate;
        private TextView tvVersion;
        ExpandableLayoutItem item;
        HorizontalListView horizontalServices;

        public CustomerDocumentsViewHolder(TextView tvCompanyDocumentName, TextView tvDate, TextView tvVersion, ExpandableLayoutItem item, HorizontalListView horizontalServices) {
            this.tvCompanyDocumentName = tvCompanyDocumentName;
            this.tvDate = tvDate;
            this.tvVersion = tvVersion;
            this.item = item;
            this.horizontalServices = horizontalServices;
        }
    }
}
