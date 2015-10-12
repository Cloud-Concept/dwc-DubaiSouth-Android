package fragment;

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

import cloudconcept.dwc.R;
import custom.DWCRoundedImageView;
import model.DWCView;
import model.ItemType;
import model.LegalRepresentative;
import model.ManagementMember;
import model.ShareOwnership;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 10/5/2015.
 */
public class LegalRepresentativesShowDetailsFragment extends Fragment {

    private static final String ARG_TEXT = "LegalRepresentativesShowDetailsFragment";
    static String objectType;
    static String object;
    private Gson gson;
    private LegalRepresentative legalRepresentative;
    private ManagementMember managementMember;
    private ShareOwnership _ShareHolder;
    private TextView tvCardOwnerName;
    private DWCRoundedImageView image;
    private LinearLayout linearAddForms;
    private ArrayList<DWCView> _views;
    private View viewItems;

    public static LegalRepresentativesShowDetailsFragment newInstance(String text, String objectType,String object) {
        LegalRepresentativesShowDetailsFragment fragment = new LegalRepresentativesShowDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TEXT, text);
        LegalRepresentativesShowDetailsFragment.objectType = objectType;
        LegalRepresentativesShowDetailsFragment.object = object;
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.show_details_director, container, false);
        gson = new Gson();
        if (objectType.equals("LegalRepresentative")) {
            legalRepresentative = gson.fromJson(object, LegalRepresentative.class);
        } else if (objectType.equals("ManagementMember")) {
            managementMember = gson.fromJson(object, ManagementMember.class);
        } else {
            _ShareHolder = gson.fromJson(object, ShareOwnership.class);
        }
        InitializeViews(view);
        return view;
    }

    private void InitializeViews(View view) {
        tvCardOwnerName = (TextView) view.findViewById(R.id.tvCardOwnerName);
        image = (DWCRoundedImageView) view.findViewById(R.id.view);
        linearAddForms = (LinearLayout) view.findViewById(R.id.linearAddForms);
        if (managementMember != null) {
            //General Managers
            tvCardOwnerName.setText(managementMember.get_manager().getName() == null ? "" : managementMember.get_manager().getName());
            Utilities.setUserPhoto(getActivity(), Utilities.stringNotNull(managementMember.get_manager().getPersonal_Photo()), image);
            _views = new ArrayList<>();
            _views.add(new DWCView("Personal Information", ItemType.HEADER));
            _views.add(new DWCView("Name", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(managementMember.get_manager().getName()), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Nationality", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(managementMember.get_manager().getNationality()), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Passport Number", ItemType.LABEL));
            _views.add(new DWCView(managementMember.get_manager() == null ? "" : managementMember.get_manager().getCurrentPassport().getName(), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Passport Expiry", ItemType.LABEL));
            _views.add(new DWCView(managementMember.get_manager() == null ? "" : Utilities.formatVisitVisaDate(Utilities.stringNotNull(managementMember.get_manager().getCurrentPassport().getPassport_Expiry_Date__c())), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Manager Information", ItemType.HEADER));
            _views.add(new DWCView("Role", ItemType.LABEL));
            _views.add(new DWCView(managementMember.get_manager() == null ? "" : Utilities.stringNotNull(managementMember.getRole()), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Start Date", ItemType.LABEL));
            _views.add(new DWCView(managementMember.get_manager() == null ? "" : Utilities.formatVisitVisaDate(Utilities.stringNotNull(managementMember.getManager_Start_Date())), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("End Date", ItemType.LABEL));
            _views.add(new DWCView(managementMember.get_manager() == null ? "" : Utilities.formatVisitVisaDate(Utilities.stringNotNull(managementMember.getManager_End_Date())), ItemType.VALUE));
            viewItems = Utilities.drawViewsOnLayout(getActivity(), managementMember, getActivity().getApplicationContext(), _views);
        } else if (legalRepresentative != null) {
            //Legal Representatives
            tvCardOwnerName.setText(legalRepresentative.getLegalRepresentativeLookup().getName() == null ? "" : legalRepresentative.getLegalRepresentativeLookup().getName());
            Utilities.setUserPhoto(getActivity(), Utilities.stringNotNull(legalRepresentative.getLegalRepresentativeLookup().getPersonal_Photo()), image);
            _views = new ArrayList<>();
            _views.add(new DWCView("Personal Information", ItemType.HEADER));
            _views.add(new DWCView("Name", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(legalRepresentative.getLegalRepresentativeLookup().getName()), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Nationality", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(legalRepresentative.getLegalRepresentativeLookup().getNationality()), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Passport Number", ItemType.LABEL));
            _views.add(new DWCView(legalRepresentative.getLegalRepresentativeLookup() == null ? "" : legalRepresentative.getLegalRepresentativeLookup().getCurrentPassport().getName(), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Passport Expiry", ItemType.LABEL));
            _views.add(new DWCView(legalRepresentative.getLegalRepresentativeLookup() == null ? "" : Utilities.formatVisitVisaDate(Utilities.stringNotNull(legalRepresentative.getLegalRepresentativeLookup().getCurrentPassport().getPassport_Expiry_Date__c())), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Legal Representatives Information", ItemType.HEADER));
            _views.add(new DWCView("Role", ItemType.LABEL));
            _views.add(new DWCView(legalRepresentative.getLegalRepresentativeLookup() == null ? "" : Utilities.stringNotNull(legalRepresentative.getRole()), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Start Date", ItemType.LABEL));
            _views.add(new DWCView(legalRepresentative.getLegalRepresentativeLookup() == null ? "" : Utilities.formatVisitVisaDate(Utilities.stringNotNull(legalRepresentative.getLegal_Representative_Start_Date())), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("End Date", ItemType.LABEL));
            _views.add(new DWCView(legalRepresentative.getLegalRepresentativeLookup() == null ? "" : Utilities.formatVisitVisaDate(Utilities.stringNotNull(legalRepresentative.getLegal_Representative_End_Date())), ItemType.VALUE));
            viewItems = Utilities.drawViewsOnLayout(getActivity(), legalRepresentative, getActivity().getApplicationContext(), _views);
        } else {
            //Shareholders
            tvCardOwnerName.setText(_ShareHolder.get_shareholder().getName() == null ? "" : _ShareHolder.get_shareholder().getName());
            Utilities.setUserPhoto(getActivity(), Utilities.stringNotNull(_ShareHolder.get_shareholder().getPersonalPhoto()), image);

            _views = new ArrayList<>();
            _views.add(new DWCView("Personal Information", ItemType.HEADER));
            _views.add(new DWCView("Name", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(_ShareHolder.get_shareholder().getName()), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Nationality", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(_ShareHolder.get_shareholder().getNationality()), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Passport Number", ItemType.LABEL));
            _views.add(new DWCView(_ShareHolder.get_shareholder() == null ? "" : _ShareHolder.get_shareholder().get_currentPassport().getName(), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Passport Expiry", ItemType.LABEL));
            _views.add(new DWCView(_ShareHolder.get_shareholder() == null ? "" : Utilities.formatVisitVisaDate(Utilities.stringNotNull(_ShareHolder.get_shareholder().get_currentPassport().getPassport_Expiry_Date__c())), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Shareholder Information", ItemType.HEADER));
            _views.add(new DWCView("Ownership (%)", ItemType.LABEL));
            _views.add(new DWCView(_ShareHolder.get_shareholder() == null ? "" : Utilities.stringNotNull(_ShareHolder.getOwnership_of_Share__c()), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("No.of Share", ItemType.LABEL));
            _views.add(new DWCView(_ShareHolder.get_shareholder() == null ? "" : Utilities.stringNotNull(Utilities.processAmount(_ShareHolder.getNo_of_Shares__c())), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
            _views.add(new DWCView("Start Date", ItemType.LABEL));
            _views.add(new DWCView(_ShareHolder.get_shareholder() == null ? "" : Utilities.formatVisitVisaDate(Utilities.stringNotNull(_ShareHolder.getOwnership_Start_Date__c())), ItemType.VALUE));
            _views.add(new DWCView("", ItemType.LINE));
//            _views.add(new DWCView("End Date", ItemType.LABEL));
//            _views.add(new DWCView(_ShareHolder.get_shareholder() == null ? "" : Utilities.stringNotNull(_ShareHolder.getOwnership_End_Date__c()), ItemType.VALUE));
            viewItems = Utilities.drawViewsOnLayout(getActivity(), _ShareHolder, getActivity().getApplicationContext(), _views);
        }
        linearAddForms.removeAllViews();
        linearAddForms.addView(viewItems);
    }
}
