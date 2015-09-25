package adapter.companyInfoAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import adapter.ClickableListAdapter;
import adapter.HorizontalListViewAdapter;
import cloudconcept.dwc.R;
import custom.HorizontalListView;
import custom.RoundedImageView;
import custom.expandableView.ExpandableLayoutItem;
import model.ManagementMember;
import model.ServiceItem;
import utilities.Utilities;

/**
 * Created by Abanoub on 7/7/2015.
 */
public class GeneralManagersAdapter extends ClickableListAdapter {

    private final Activity activity;
    Context context;

    public GeneralManagersAdapter(Activity activity, Context context, int viewid, List objects) {
        super(context, viewid, objects);
        this.activity = activity;
        this.context = context;
    }

    @Override
    protected ViewHolder createHolder(View v) {

        TextView tvFullName, tvNationality, tvPassportNumber, tvRole, tvStartDate;
        RoundedImageView _smartEmployeeImage;
        HorizontalListView horizontalListView;

        final ExpandableLayoutItem item = (ExpandableLayoutItem) v.findViewById(R.id.expandableLayoutListView);
        RelativeLayout relativeHeader = item.getHeaderLayout();


        tvFullName = (TextView) relativeHeader.findViewById(R.id.tvFullName);
        tvNationality = (TextView) relativeHeader.findViewById(R.id.tvNationality);
        tvPassportNumber = (TextView) relativeHeader.findViewById(R.id.tvpassportNumber);
        tvRole = (TextView) relativeHeader.findViewById(R.id.tvRole);
        tvStartDate = (TextView) relativeHeader.findViewById(R.id.tvStartDate);
        _smartEmployeeImage = (RoundedImageView) relativeHeader.findViewById(R.id.view);
        RelativeLayout relativeContent = item.getContentLayout();
        HorizontalListView _horizontalServices = (HorizontalListView) relativeContent.findViewById(R.id.horizontalServices);
        GeneralManagerViewHolder holder = new GeneralManagerViewHolder(tvFullName, tvNationality, tvPassportNumber, tvRole, tvStartDate, _smartEmployeeImage, _horizontalServices);
        return holder;
    }

    @Override
    protected void bindHolder(ViewHolder h) {

        GeneralManagerViewHolder holder = (GeneralManagerViewHolder) h;
        ManagementMember managementMember = (ManagementMember) holder.data;
        holder.tvFullName.setText(managementMember.get_manager().getName() == null ? "" : managementMember.get_manager().getName());
        holder.tvNationality.setText(managementMember.get_manager().getNationality() == null ? "" : managementMember.get_manager().getNationality());
        holder.tvPassportNumber.setText(managementMember.get_manager().getCurrentPassport() == null ? "" : managementMember.get_manager().getCurrentPassport().getName());
        holder.tvRole.setText(managementMember.getRole() == null ? "" : managementMember.getRole());
        holder.tvStartDate.setText(managementMember.getManager_Start_Date() == null ? "" : managementMember.getManager_Start_Date());
        Utilities.setUserPhoto(activity, Utilities.stringNotNull(managementMember.get_manager().getPersonal_Photo()), holder._smartEmployeeImage);
        ArrayList<ServiceItem> _items = new ArrayList<ServiceItem>();
        _items.add(new ServiceItem("Show Details", R.drawable.service_show_details));
        holder.horizontalListView.setAdapter(new HorizontalListViewAdapter(managementMember, activity, context, _items));
    }

    static class GeneralManagerViewHolder extends ViewHolder {

        TextView tvFullName, tvNationality, tvPassportNumber, tvRole, tvStartDate;
        RoundedImageView _smartEmployeeImage;
        HorizontalListView horizontalListView;

        public GeneralManagerViewHolder(TextView tvFullName, TextView tvNationality, TextView tvPassportNumber, TextView tvRole, TextView tvStartDate, RoundedImageView _smartEmployeeImage, HorizontalListView horizontalListView) {
            this.tvFullName = tvFullName;
            this.tvNationality = tvNationality;
            this.tvPassportNumber = tvPassportNumber;
            this.tvRole = tvRole;
            this.tvStartDate = tvStartDate;
            this._smartEmployeeImage = _smartEmployeeImage;
            this.horizontalListView = horizontalListView;
        }
    }
}

