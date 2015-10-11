package adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cloudconcept.dwc.R;
import custom.HorizontalListView;
import custom.expandableView.ExpandableLayoutItem;
import model.EServices_Document_Checklist__c;
import model.ServiceItem;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 9/21/2015.
 */
public class CertificatesAdapter extends ClickableListAdapter {

    Context context;
    Activity activity;
    ArrayList<EServices_Document_Checklist__c> eServices_document_checklist__cs;

    public CertificatesAdapter(Activity act, Context context, int viewid, List objects) {
        super(context, viewid, objects);
        this.context = context;
        this.activity = act;
        eServices_document_checklist__cs = (ArrayList<EServices_Document_Checklist__c>) objects;
    }

    @Override
    protected ViewHolder createHolder(View itemView) {
        ExpandableLayoutItem item;
        HorizontalListView horizontalServices;
        TextView tvTrueCopyName;
        item = (ExpandableLayoutItem) itemView.findViewById(R.id.expandableLayoutItemTrueCopy);
        RelativeLayout relativeHeader = item.getHeaderLayout();
        tvTrueCopyName = (TextView) relativeHeader.findViewById(R.id.tvTrueCopyName);
        RelativeLayout relativeContent = item.getContentLayout();
        horizontalServices = (HorizontalListView) relativeContent.findViewById(R.id.horizontalServices);
        CertificatesViewHolder holder = new CertificatesViewHolder(item, horizontalServices, tvTrueCopyName);
        return holder;
    }

    @Override
    protected void bindHolder(ViewHolder h) {
        final CertificatesViewHolder mvh = (CertificatesViewHolder) h;
        EServices_Document_Checklist__c eServices_document_checklist__c = (EServices_Document_Checklist__c) mvh.data;
        mvh.tvTrueCopyName.setText(Utilities.stringNotNull(eServices_document_checklist__c.getName()));

        ArrayList<ServiceItem> _items = new ArrayList<>();

        if (eServices_document_checklist__c.getOriginal_can_be_Requested__c()) {
            _items.add(new ServiceItem("Request True Copy", R.mipmap.copy));
        }

        if (eServices_document_checklist__c.getAvailable_for_Preview__c()) {
            if (!eServices_document_checklist__c.getEService_Administration__r().getService_VF_Page__c().contains("<ContractId>") && !eServices_document_checklist__c.getEService_Administration__r().getService_VF_Page__c().contains("<tenId>")) {
                _items.add(new ServiceItem("Preview", R.mipmap.preview));
            }
        }

        mvh.horizontalServices.setAdapter(new HorizontalListViewAdapter(eServices_document_checklist__c, activity, context, _items));

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

    public boolean addAll(ArrayList<EServices_Document_Checklist__c> strings) {

        boolean returnedProgress = false;
        ArrayList<EServices_Document_Checklist__c> eServices_document_checklist__cs1 = new ArrayList<>();
        for (int i = 0; i < strings.size(); i++) {
            boolean found = false;
            for (int j = 0; j < eServices_document_checklist__cs.size(); j++) {
                if (eServices_document_checklist__cs.get(j).getId().equals(strings.get(i).getId())) {
                    found = true;
                    break;
                }
                if (!found) {
                    eServices_document_checklist__cs1.add(strings.get(i));
                }
            }
        }
        if (eServices_document_checklist__cs1.size() > 0) {
            eServices_document_checklist__cs.addAll(eServices_document_checklist__cs1);
            notifyDataSetChanged();
        }
        return returnedProgress;
    }


    static class CertificatesViewHolder extends ViewHolder {

        ExpandableLayoutItem item;
        HorizontalListView horizontalServices;
        TextView tvTrueCopyName;

        public CertificatesViewHolder(ExpandableLayoutItem item, HorizontalListView horizontalServices, TextView tvTrueCopyName) {
            this.item = item;
            this.horizontalServices = horizontalServices;
            this.tvTrueCopyName = tvTrueCopyName;
        }
    }
}
