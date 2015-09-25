package activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;

import cloudconcept.dwc.R;
import model.LegalRepresentative;
import model.ManagementMember;

/**
 * Created by Abanoub Wagdy on 9/24/2015.
 */
public class LegalRepresentativesShowDetailsActivity extends Activity {

    Object object;
    String objectAsStr;
    String objectType;
    Gson gson;
    ManagementMember managementMember;
    LegalRepresentative legalRepresentative;
    TextView tvFullName, tvNationality, tvPassportNumber, tvRole, tvStartDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        objectType = getIntent().getExtras().getString("objectType");
        gson = new Gson();
        if (objectType.equals("LegalRepresentative")) {
            legalRepresentative = gson.fromJson(getIntent().getExtras().getString("object"), LegalRepresentative.class);
        } else {
            managementMember = gson.fromJson(getIntent().getExtras().getString("object"), ManagementMember.class);
        }
        InitializeViews();
    }

    private void InitializeViews() {
        setContentView(R.layout.show_details_management_member);
        tvFullName = (TextView) findViewById(R.id.tvName);
        tvNationality = (TextView) findViewById(R.id.tvNationality);
        tvPassportNumber = (TextView) findViewById(R.id.tvPassportNumber);
        tvRole = (TextView) findViewById(R.id.tvRole);
        tvStartDate = (TextView) findViewById(R.id.tvStartDate);
        if (managementMember != null) {
            tvFullName.setText(managementMember.get_manager().getName() == null ? "" : managementMember.get_manager().getName());
            tvNationality.setText(managementMember.get_manager().getNationality() == null ? "" : managementMember.get_manager().getNationality());
            tvPassportNumber.setText(managementMember.get_manager().getCurrentPassport() == null ? "" : managementMember.get_manager().getCurrentPassport().getName());
            tvRole.setText(managementMember.getRole() == null ? "" : managementMember.getRole());
            tvStartDate.setText(managementMember.getManager_Start_Date() == null ? "" : managementMember.getManager_Start_Date());
        } else {
            tvFullName.setText(legalRepresentative.getLegalRepresentativeLookup().getName() == null ? "" : legalRepresentative.getLegalRepresentativeLookup().getName());
            tvNationality.setText(legalRepresentative.getLegalRepresentativeLookup().getNationality() == null ? "" : legalRepresentative.getLegalRepresentativeLookup().getNationality());
            tvPassportNumber.setText(legalRepresentative.getLegalRepresentativeLookup().getCurrentPassport() == null ? "" : legalRepresentative.getLegalRepresentativeLookup().getCurrentPassport().getName());
            tvRole.setText(legalRepresentative.getRole() == null ? "" : legalRepresentative.getRole());
            tvStartDate.setText(legalRepresentative.getLegal_Representative_Start_Date() == null ? "" : legalRepresentative.getLegal_Representative_Start_Date());
        }
    }
}
