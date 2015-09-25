package activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.google.gson.Gson;

import cloudconcept.dwc.R;
import model.Card_Management__c;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 9/23/2015.
 */
public class AccessCardShowDetailsActivity extends FragmentActivity {
    TextView tvName, tvStatus, tvType, tvPassportNumber, tvCardExpiry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_details_access_card);
        tvName = (TextView) findViewById(R.id.tvName);
        tvType = (TextView) findViewById(R.id.tvType);
        tvPassportNumber = (TextView) findViewById(R.id.tvPassportNumber);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvCardExpiry = (TextView) findViewById(R.id.tvCardExpiry);
        Gson gson = new Gson();
        Card_Management__c _cardManagement = gson.fromJson(getIntent().getExtras().getString("object"), Card_Management__c.class);
        tvName.setText(Utilities.stringNotNull(_cardManagement.getFull_Name__c()));
        tvType.setText(Utilities.stringNotNull(_cardManagement.getCard_Type__c()));
        tvPassportNumber.setText(Utilities.stringNotNull(_cardManagement.getPassport_Number__c()));
        tvStatus.setText(Utilities.stringNotNull(_cardManagement.getStatus__c()));
        tvCardExpiry.setText(_cardManagement.getCard_Expiry_Date__c() == null ? "" : _cardManagement.getCard_Expiry_Date__c());
    }
}
