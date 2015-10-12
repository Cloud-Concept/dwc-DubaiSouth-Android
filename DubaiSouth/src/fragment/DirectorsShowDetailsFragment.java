package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cloudconcept.dwc.R;
import custom.DWCRoundedImageView;
import model.DWCView;
import model.Directorship;
import model.ItemType;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 10/5/2015.
 */
public class DirectorsShowDetailsFragment extends Fragment {

    private static final String ARG_TEXT = "DirectorsShowDetailsFragment";
    LinearLayout linearAddForms;
    static Directorship directorship;
    private ArrayList<DWCView> _views;
    DWCRoundedImageView imageView;
    TextView tvCardOwnerName;

    public static DirectorsShowDetailsFragment newInstance(String text, Directorship _directorship) {
        DirectorsShowDetailsFragment fragment = new DirectorsShowDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TEXT, text);
        DirectorsShowDetailsFragment.directorship = _directorship;
        fragment.setArguments(bundle);
        return fragment;
    }

    public DirectorsShowDetailsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.show_details_director, container, false);
        InitializeViews(view);
        return view;
    }

    private void InitializeViews(View view) {
        _views = new ArrayList<>();
        imageView = (DWCRoundedImageView) view.findViewById(R.id.view);
        tvCardOwnerName = (TextView) view.findViewById(R.id.tvCardOwnerName);
        tvCardOwnerName.setText(directorship.get_director().getName() == null ? "" : directorship.get_director().getName());
        Utilities.setUserPhoto(getActivity(), Utilities.stringNotNull(directorship.get_director().getPersonal_Photo()), imageView);
        linearAddForms = (LinearLayout) view.findViewById(R.id.linearAddForms);
        _views.add(new DWCView("Personal Information", ItemType.HEADER));
        _views.add(new DWCView("Name", ItemType.LABEL));
        _views.add(new DWCView(Utilities.stringNotNull(directorship.get_director().getName()), ItemType.VALUE));
        _views.add(new DWCView("", ItemType.LINE));
        _views.add(new DWCView("Nationality", ItemType.LABEL));
        _views.add(new DWCView(Utilities.stringNotNull(directorship.get_director().getNationality()), ItemType.VALUE));
        _views.add(new DWCView("", ItemType.LINE));
        _views.add(new DWCView("Passport Number", ItemType.LABEL));
        _views.add(new DWCView(directorship.get_director().get_currentPassport() == null ? "" : directorship.get_director().get_currentPassport().getName(), ItemType.VALUE));
        _views.add(new DWCView("", ItemType.LINE));
        _views.add(new DWCView("Passport Expiry", ItemType.LABEL));
        _views.add(new DWCView(directorship.get_director().get_currentPassport() == null ? "" : Utilities.formatVisitVisaDate(Utilities.stringNotNull(directorship.get_director().get_currentPassport().getPassport_Expiry_Date__c())), ItemType.VALUE));
        _views.add(new DWCView("", ItemType.LINE));
        _views.add(new DWCView("Director Information", ItemType.HEADER));
        _views.add(new DWCView("Role", ItemType.LABEL));
        _views.add(new DWCView(Utilities.stringNotNull(directorship.getRoles()), ItemType.VALUE));
        _views.add(new DWCView("", ItemType.LINE));
        _views.add(new DWCView("Start Date", ItemType.LABEL));
        _views.add(new DWCView(Utilities.formatVisitVisaDate(Utilities.stringNotNull(directorship.getDirectorship_Start_Date())), ItemType.VALUE));
        _views.add(new DWCView("", ItemType.LINE));
        _views.add(new DWCView("End Date", ItemType.LABEL));
        _views.add(new DWCView(Utilities.formatVisitVisaDate(Utilities.stringNotNull(directorship.getDirectorship_End_Date())), ItemType.VALUE));

        View viewItems = Utilities.drawViewsOnLayout(getActivity(), directorship, getActivity().getApplicationContext(), _views);
        linearAddForms.removeAllViews();
        linearAddForms.addView(viewItems);
    }
}
