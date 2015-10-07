package fragmentActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import cloudconcept.dwc.BaseActivity;
import cloudconcept.dwc.R;
import custom.DWCRoundedImageView;
import model.Contract_DWC__c;
import model.DWCView;
import model.ItemType;
import model.ServiceItem;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 8/31/2015.
 */
public class LeasingShowDetailsActivity extends BaseActivity {

    Contract_DWC__c contract_dwc__c;
    Gson gson;

    @Override
    public int getNotificationVisibillity() {
        return View.VISIBLE;
    }

    @Override
    public int getMenuVisibillity() {
        return View.GONE;
    }

    @Override
    public int getBackVisibillity() {
        return View.VISIBLE;
    }

    @Override
    public String getHeaderTitle() {
        return "Leasing Info";
    }

    @Override
    public Fragment GetFragment() {
        gson = new Gson();
        contract_dwc__c = gson.fromJson(getIntent().getExtras().getString("object"), Contract_DWC__c.class);
        Fragment fragment = leasingShowDetailsFragment.newInstance("leasingShowDetailsFragment", contract_dwc__c);
        return fragment;
    }

    public static class leasingShowDetailsFragment extends Fragment {

        private static ArrayList<DWCView> _views;
        private static String ARG_TEXT = "ShowDetails";
        TextView tvContractName;
        DWCRoundedImageView dwcRoundedImageView;
        Gson gson = new Gson();
        static Contract_DWC__c contract_dwc__c;
        LinearLayout linearAddForms;
        private static ArrayList<ServiceItem> _items;

        public static leasingShowDetailsFragment newInstance(String details, Contract_DWC__c contract_dwc__c) {
            leasingShowDetailsFragment fragment = new leasingShowDetailsFragment();
            Bundle bundle = new Bundle();
            _views = new ArrayList<DWCView>();
            _items = new ArrayList<>();
            bundle.putString(ARG_TEXT, details);
            leasingShowDetailsFragment.contract_dwc__c = contract_dwc__c;
            fragment.setArguments(bundle);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.show_details_leasing_contract, container, false);
            _views.clear();
            InitializeViews(view);
            return view;
        }

        private void InitializeViews(View view) {
            tvContractName = (TextView) view.findViewById(R.id.tvContractName);
            linearAddForms = (LinearLayout) view.findViewById(R.id.linearAddForms);
            dwcRoundedImageView = (DWCRoundedImageView)view.findViewById(R.id.view);
            tvContractName.setText(contract_dwc__c.getName());
            _views.add(new DWCView("Contract Details", ItemType.HEADER));
            _views.add(new DWCView("Contract Type", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(contract_dwc__c.getContract_Type__c()), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Contract Duration (Years)", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(contract_dwc__c.getContract_Duration__c()), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Contract Duration (Months)", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(contract_dwc__c.getContract_Duration_Year_Month__c()), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Contract Number", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(contract_dwc__c.getContract_Number__c()), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Contract Start Date", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(contract_dwc__c.getContract_Start_Date__c()), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Contract End Date", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(contract_dwc__c.getContract_Expiry_Date__c()), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Rent Start Date", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(contract_dwc__c.getRent_Start_Date__c()), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Activated Date", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(contract_dwc__c.getActivated_Date__c()), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Total Price", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(contract_dwc__c.getTotal_Rent_Price__c()), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Leasing Unit Details", ItemType.HEADER));
            _views.add(new DWCView("Unit Name", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(contract_dwc__c.getContract_line_item__cs().get(0).getName()), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            String services = "";
            if (contract_dwc__c.getContract_Expiry_Date__c() != null && !contract_dwc__c.getContract_Expiry_Date__c().equals("")) {
                if (Utilities.daysDifference(contract_dwc__c.getContract_Expiry_Date__c()) < 60) {
                    if (contract_dwc__c.IS_BC_Contract__c()) {
                        services += "Renew BC Contract,";
                        dwcRoundedImageView.setImageResource(R.mipmap.lease_bc_contract);
                    } else {
                        services += "Renew Contract,";
                        dwcRoundedImageView.setImageResource(R.mipmap.lease_ac_contract);
                    }
                }
            }
//            services += "Cancel Contract,";
            if (!services.equals("")) {
                services = services.substring(0, services.length() - 1);
                _views.add(new DWCView(services, ItemType.HORIZONTAL_LIST_VIEW));
            }

            View RenderedViewItems = Utilities.drawViewsOnLayout(getActivity(), contract_dwc__c, getActivity().getApplicationContext(), _views);
            linearAddForms.addView(RenderedViewItems);
        }
    }
}