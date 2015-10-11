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
import dataStorage.StoreData;
import exceptionHandling.ExceptionHandler;
import model.DWCView;
import model.ItemType;
import model.User;
import model.Visa;
import utilities.Utilities;

/**
 * Created by Abanoub on 7/8/2015.
 */
public class ShowDetailsActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
    }

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
        String title = getIntent().getExtras().getString("title");
        return title;
    }

    @Override
    public Fragment GetFragment() {
        Fragment fragment = ShowDetailsFragment.newInstance("ShowDetails", getIntent().getExtras().getString("objectType"), getIntent().getExtras().getString("objectAsString"));
        return fragment;
    }

    public static class ShowDetailsFragment extends Fragment {

        private static ArrayList<DWCView> _views;
        private static String ARG_TEXT = "ShowDetails";
        static String ObjectType;
        static String ObjectAsString;
        Visa _visa;
        Gson gson = new Gson();
        TextView tvPersonName;
        DWCRoundedImageView imageView;

        public static ShowDetailsFragment newInstance(String details, String objectType, String objectAsString) {
            ShowDetailsFragment fragment = new ShowDetailsFragment();
            Bundle bundle = new Bundle();
            _views = new ArrayList<DWCView>();
            bundle.putString(ARG_TEXT, details);
            ObjectType = objectType;
            ObjectAsString = objectAsString;
            fragment.setArguments(bundle);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.show_details_visit_visa, container, false);
            _views.clear();
            if (ObjectType.equals("Visa")) {
                gson = new Gson();
                _visa = gson.fromJson(ObjectAsString, Visa.class);
                InitializeVisaViews(view);
            }
            return view;
        }

        private void InitializeVisaViews(View view) {
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linearAddForms);
            tvPersonName = (TextView) view.findViewById(R.id.tvPersonName);
            imageView = (DWCRoundedImageView) view.findViewById(R.id.view);
            tvPersonName.setText(_visa.getApplicant_Full_Name__c());
            if (_visa.getPersonal_Photo__c() != null && !_visa.getPersonal_Photo__c().equals(""))
                Utilities.setUserPhoto(getActivity(), _visa.getPersonal_Photo__c(), imageView);
            _views.add(new DWCView("Employee Information", ItemType.HEADER));
            _views.add(new DWCView("Gender", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(_visa.getApplicant_Gender__c()), ItemType.VALUE));
            _views.add(new DWCView("Birth Date", ItemType.LABEL));
            _views.add(new DWCView(Utilities.formatVisitVisaDate(Utilities.stringNotNull(_visa.getDate_of_Birth__c())), ItemType.VALUE));
            _views.add(new DWCView("Mobile", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(_visa.getApplicant_Mobile_Number__c()), ItemType.VALUE));
            _views.add(new DWCView("Email", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(_visa.getApplicant_Email__c()), ItemType.VALUE));
            _views.add(new DWCView("Visa Information", ItemType.HEADER));
            _views.add(new DWCView("Visa Number", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(_visa.getName()), ItemType.VALUE));
            _views.add(new DWCView("Status", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(_visa.getVisa_Validity_Status__c()), ItemType.VALUE));
            _views.add(new DWCView("Expiry", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(_visa.getVisa_Expiry_Date__c()), ItemType.VALUE));
            _views.add(new DWCView("Passport Information", ItemType.HEADER));
            _views.add(new DWCView("Passport", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(_visa.getPassport_Number__c()), ItemType.VALUE));
            _views.add(new DWCView("Expriry Date", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(_visa.getVisa_Expiry_Date__c()), ItemType.VALUE));
            _views.add(new DWCView("Issue Country", ItemType.LABEL));
            _views.add(new DWCView(Utilities.stringNotNull(_visa.getPassport_Country__c()), ItemType.VALUE));
            String Services = "";

            User user = new Gson().fromJson(new StoreData(getActivity().getApplicationContext()).getUserDataAsString(), User.class);
            boolean manager = _visa.getVisa_Holder__c().equals(user.get_contact().get_account().getID());
            if (_visa.getVisa_Type__c().equals("Visit")) {
                if ((_visa.getVisa_Validity_Status__c().equals("Issued") || _visa.getVisa_Validity_Status__c().equals("Under Process") || _visa.getVisa_Validity_Status__c().equals("Under Renewal")) && (_visa.getVisa_Type__c().equals("Employment") || _visa.getVisa_Type__c().equals("Visit") || _visa.getVisa_Type__c().equals("Transfer - Internal") || _visa.getVisa_Type__c().equals("Transfer - External")) && !manager) {
                    Services += "Cancel Visa,";
                }
            } else {
                if (_visa.getVisa_Validity_Status__c().equals("Issued")) {
                    Services += "New NOC,";
                }
                if ((_visa.getVisa_Validity_Status__c().equals("Issued") || _visa.getVisa_Validity_Status__c().equals("Expired"))
                        &&
                        (_visa.getVisa_Type__c().equals("Employment") || _visa.getVisa_Type__c().equals("Transfer - Internal") || _visa.getVisa_Type__c().equals("Transfer - External"))) {
                    Services += "Renew Visa,";
                }


                if ((_visa.getVisa_Validity_Status__c().equals("Issued") || _visa.getVisa_Validity_Status__c().equals("Under Process") || _visa.getVisa_Validity_Status__c().equals("Under Renewal")) && (_visa.getVisa_Type__c().equals("Employment") || _visa.getVisa_Type__c().equals("Transfer - Internal") || _visa.getVisa_Type__c().equals("Transfer - External")) && !manager) {
                    Services += "Cancel Visa,";
                }


                if (_visa.getVisa_Validity_Status__c().equals("Issued")) {
                    Services += "Renew Passport,";
                }
            }

            if (!Services.equals("")) {
                Services = Services.substring(0, Services.length() - 1);
                _views.add(new DWCView(Services, ItemType.HORIZONTAL_LIST_VIEW));
            }

            View RenderedViewItems = Utilities.drawViewsOnLayout(getActivity(), _visa, getActivity().getApplicationContext(), _views);
            linearLayout.addView(RenderedViewItems);
        }
    }
}
