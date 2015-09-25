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
import model.LegalRepresentative;
import model.ServiceItem;
import utilities.Utilities;

/**
 * Created by Abanoub on 7/7/2015.
 */
public class LegalRepresentativesAdapter extends ClickableListAdapter {

    private Activity activity;
    Context context;

    public LegalRepresentativesAdapter(Activity activity, Context context, int viewid, List objects) {
        super(context, viewid, objects);
        this.activity = activity;
        this.context = context;
    }

    @Override
    protected ViewHolder createHolder(View v) {

        TextView tvFullName, tvNationality, tvPassportNumber, tvRole, tvStartDate;
        RoundedImageView _smartEmployeeImage;

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
        LegalRepresentativeViewHolder holder = new LegalRepresentativeViewHolder(tvFullName, tvNationality, tvPassportNumber, tvRole, tvStartDate, _smartEmployeeImage,_horizontalServices);
        return holder;
    }

    @Override
    protected void bindHolder(ViewHolder h) {
        LegalRepresentativeViewHolder holder = (LegalRepresentativeViewHolder) h;
        LegalRepresentative legalRepresentative = (LegalRepresentative) holder.data;
        holder.tvFullName.setText(legalRepresentative.getLegalRepresentativeLookup().getName() == null ? "" : legalRepresentative.getLegalRepresentativeLookup().getName());
        holder.tvNationality.setText(legalRepresentative.getLegalRepresentativeLookup().getNationality() == null ? "" : legalRepresentative.getLegalRepresentativeLookup().getNationality());
        holder.tvPassportNumber.setText(legalRepresentative.getLegalRepresentativeLookup().getCurrentPassport() == null ? "" : legalRepresentative.getLegalRepresentativeLookup().getCurrentPassport().getName());
        holder.tvRole.setText(legalRepresentative.getRole() == null ? "" : legalRepresentative.getRole());
        holder.tvStartDate.setText(legalRepresentative.getLegal_Representative_Start_Date() == null ? "" : legalRepresentative.getLegal_Representative_Start_Date());
        Utilities.setUserPhoto(activity, Utilities.stringNotNull(legalRepresentative.getLegalRepresentativeLookup().getPersonal_Photo()), holder._smartEmployeeImage);
        ArrayList<ServiceItem> _items = new ArrayList<ServiceItem>();
        _items.add(new ServiceItem("Show Details", R.drawable.service_show_details));
        holder._horizontalServices.setAdapter(new HorizontalListViewAdapter(legalRepresentative, activity, context, _items));
    }

    static class LegalRepresentativeViewHolder extends ViewHolder {

        TextView tvFullName, tvNationality, tvPassportNumber, tvRole, tvStartDate;
        RoundedImageView _smartEmployeeImage;
        HorizontalListView _horizontalServices;

        public LegalRepresentativeViewHolder(TextView tvFullName, TextView tvNationality, TextView tvPassportNumber, TextView tvRole, TextView tvStartDate, RoundedImageView smartEmployeeImage, HorizontalListView _horizontalServices) {
            this.tvFullName = tvFullName;
            this.tvNationality = tvNationality;
            this.tvPassportNumber = tvPassportNumber;
            this.tvRole = tvRole;
            this.tvStartDate = tvStartDate;
            this._smartEmployeeImage = smartEmployeeImage;
            this._horizontalServices = _horizontalServices;
        }
    }
}
