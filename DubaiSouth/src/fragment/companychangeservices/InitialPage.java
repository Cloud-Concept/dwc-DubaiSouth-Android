package fragment.companychangeservices;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.gc.materialdesign.views.Switch;

import cloudconcept.dwc.R;
import fragmentActivity.ChangeAndRemovalActivity;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 9/2/2015.
 */
public class InitialPage extends Fragment {

    private static final String ARG_TEXT = "Initial";
    ChangeAndRemovalActivity activity;
    EditText etCurrentMobile, etCurrentFax, etCurrentEmail, etCurrentPoBox, etNewMobile, etNewFax, etNewEmail, etNewPoBox;
    EditText etCompanyName, etCompanyNameArabic, etNewCompanyName, etNewCompanyNameArabic;
    EditText etShareCapital, etNewShareCapital;
    EditText etDirector;
    TextView tvEstablishmentCard_CardNumber, tvEstablishmentCard_LicenseNumber, tvEstablishmentCard_IssueDate, tvEstablishmentCard_ExpiryDate, tvEstablishmentCard_Status;
    private Switch switchView;
    private TextView tvLabel;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = null;
        activity = (ChangeAndRemovalActivity) getActivity();
        if (activity.getMethodName().equals("CreateRequestAddressChange")) {
            view = inflater.inflate(R.layout.address_change_initial_page, container, false);
            InitializeAddressChangeLayout(view);
        } else if (activity.getMethodName().equals("CreateRequestNameChange")) {
            view = inflater.inflate(R.layout.name_change_initial_page, container, false);
            InitializeNameChangeLayout(view);
        } else if (activity.getMethodName().equals("CreateRequestCapitalChange")) {
            view = inflater.inflate(R.layout.capital_change_initial_page, container, false);
            InitializeCapitalChangeLayout(view);
        } else if (activity.getMethodName().equals("CreateRequestDirectorRemoval")) {
            view = inflater.inflate(R.layout.director_removal_initial_page, container, false);
            InitializeDirectorRemovalLayout(view);
        } else if (activity.getMethodName().equals("CreateEstablishmentCardRequest")) {
            view = inflater.inflate(R.layout.establishment_card_initial_page, container, false);
            InitializeEstablishmentCardLayout(view);
            activity.setisLostCardChecked(false);
        }

        return view;
    }

    private void InitializeEstablishmentCardLayout(View view) {

        tvEstablishmentCard_CardNumber = (TextView) view.findViewById(R.id.tvCardNumber);
        tvEstablishmentCard_ExpiryDate = (TextView) view.findViewById(R.id.tvExpiryDate);
        tvEstablishmentCard_IssueDate = (TextView) view.findViewById(R.id.tvIssueDate);
        tvEstablishmentCard_LicenseNumber = (TextView) view.findViewById(R.id.tvLicenseNumber);
        tvEstablishmentCard_Status = (TextView) view.findViewById(R.id.tvStatus);
        switchView = (Switch) view.findViewById(R.id.switchView);
        tvLabel = (TextView) view.findViewById(R.id.tvLabel);

        tvEstablishmentCard_CardNumber.setText(Utilities.stringNotNull(activity.getCardNumber()));
        tvEstablishmentCard_ExpiryDate.setText(Utilities.stringNotNull(activity.getExpiryDate()));
        tvEstablishmentCard_IssueDate.setText(Utilities.stringNotNull(activity.getIssueDate()));
        tvEstablishmentCard_LicenseNumber.setText(Utilities.stringNotNull(activity.getLicenseNumber()));
        tvEstablishmentCard_Status.setText(Utilities.stringNotNull(activity.getStatus()));

        if (activity.getServiceIdentifier().equals("Establishment Card Lost Fee")) {
            switchView.setVisibility(View.GONE);
            tvLabel.setVisibility(View.GONE);
        } else {
            switchView.setVisibility(View.VISIBLE);
            tvLabel.setVisibility(View.VISIBLE);
            switchView.setOncheckListener(new Switch.OnCheckListener() {
                @Override
                public void onCheck(Switch aSwitch, boolean b) {
                    if (b) {
                        activity.setisLostCardChecked(true);
                    } else {
                        activity.setisLostCardChecked(false);
                    }
                }
            });
        }
    }

    private void InitializeDirectorRemovalLayout(View view) {
        etDirector = (EditText) view.findViewById(R.id.etDirector);
        etDirector.setKeyListener(null);
        etDirector.setText(activity.getDirectorship().get_director().getName());
    }

    private void InitializeCapitalChangeLayout(View view) {

        etShareCapital = (EditText) view.findViewById(R.id.etShareCapital);
        etNewShareCapital = (EditText) view.findViewById(R.id.etNewShareCapital);
        etShareCapital.setKeyListener(null);

        etNewShareCapital.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                activity.setNewShareCapital(s.toString());
            }
        });
    }

    private void InitializeNameChangeLayout(View view) {
        etCompanyName = (EditText) view.findViewById(R.id.etCompanyName);
        etCompanyNameArabic = (EditText) view.findViewById(R.id.etCompanyNameArabic);
        etCompanyName.setKeyListener(null);
        etCompanyNameArabic.setKeyListener(null);

        etCompanyName.setText(activity.getUser().get_contact().get_account().getName());
        etCompanyNameArabic.setText(activity.getUser().get_contact().get_account().getArabicAccountName());

        etNewCompanyName = (EditText) view.findViewById(R.id.etNewCompanyName);
        etNewCompanyNameArabic = (EditText) view.findViewById(R.id.etNewCompanyNameArabic);

//        etNewCompanyName.setText(activity.getUser().get_contact().get_account().getName());
//        etNewCompanyNameArabic.setText(activity.getUser().get_contact().get_account().getArabicAccountName());

        activity.setCompanyName(etCompanyName.getText().toString());
        activity.setCompanyNameArabic(etCompanyNameArabic.getText().toString());
        activity.setNewCompanyName("");
        activity.setNewCompanyNameArabic("");

        etNewCompanyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                activity.setNewCompanyName(s.toString());
            }
        });

        etNewCompanyNameArabic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                activity.setNewCompanyNameArabic(s.toString());
            }
        });
    }

    private void InitializeAddressChangeLayout(View view) {
        etCurrentEmail = (EditText) view.findViewById(R.id.etCurrentEmail);
        etCurrentFax = (EditText) view.findViewById(R.id.etCurrentFax);
        etCurrentMobile = (EditText) view.findViewById(R.id.etCurrentMobile);
        etCurrentPoBox = (EditText) view.findViewById(R.id.etCurrentPOBox);

        etCurrentEmail.setKeyListener(null);
        etCurrentFax.setKeyListener(null);
        etCurrentMobile.setKeyListener(null);
        etCurrentPoBox.setKeyListener(null);

        etCurrentEmail.setText(Utilities.stringNotNull(activity.getUser().get_contact().get_account().getEmail()));
        etCurrentFax.setText(Utilities.stringNotNull(activity.getUser().get_contact().get_account().getFax()));
        etCurrentMobile.setText(Utilities.stringNotNull(activity.getUser().get_contact().get_account().getMobile()));
        etCurrentPoBox.setText(Utilities.stringNotNull(activity.getUser().get_contact().get_account().getBillingPostalCode()));

        activity.setCurrentEmail(etCurrentEmail.getText().toString());
        activity.setCurrentFax(etCurrentFax.getText().toString());
        activity.setCurrentMobile(etCurrentMobile.getText().toString());
        activity.setCurrentPoBox(etCurrentPoBox.getText().toString());

        etNewMobile = (EditText) view.findViewById(R.id.etNewMobile);
        etNewFax = (EditText) view.findViewById(R.id.etNewFax);
        etNewEmail = (EditText) view.findViewById(R.id.etNewEmail);
        etNewPoBox = (EditText) view.findViewById(R.id.etNewPOBox);

        etNewEmail.setText(Utilities.stringNotNull(activity.getUser().get_contact().get_account().getEmail()));
        etNewFax.setText(Utilities.stringNotNull(activity.getUser().get_contact().get_account().getFax()));
        etNewMobile.setText(Utilities.stringNotNull(activity.getUser().get_contact().get_account().getMobile()));
        etNewPoBox.setText(Utilities.stringNotNull(activity.getUser().get_contact().get_account().getBillingPostalCode()));

        activity.setNewEmail(etNewEmail.getText().toString());
        activity.setNewFax(etNewFax.getText().toString());
        activity.setNewMobile(etNewMobile.getText().toString());
        activity.setNewPoBox(etNewPoBox.getText().toString());

        etNewMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                activity.setNewMobile(s.toString());
            }
        });

        etNewFax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                activity.setNewFax(s.toString());
            }
        });

        etNewEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                activity.setNewEmail(s.toString());
            }
        });

        etNewPoBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                activity.setNewPoBox(s.toString());
            }
        });
    }

    public static InitialPage newInstance(String text) {

        InitialPage fragment = new InitialPage();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TEXT, text);
        fragment.setArguments(bundle);
        return fragment;
    }
}
