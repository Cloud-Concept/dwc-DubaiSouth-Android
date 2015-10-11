package activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;

import cloudconcept.dwc.R;
import custom.DWCRoundedImageView;
import exceptionHandling.ExceptionHandler;
import model.Card_Management__c;
import model.DWCView;
import model.ItemType;
import model.ServiceItem;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 9/23/2015.
 */
public class AccessCardShowDetailsActivity extends FragmentActivity {

    ArrayList<DWCView> _views;
    TextView tvCardOwnerName;
    private LinearLayout linearLayout;
    DWCRoundedImageView imageUser;
    ImageView imageBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.show_details_access_card);
        tvCardOwnerName = (TextView) findViewById(R.id.tvCardOwnerName);
        linearLayout = (LinearLayout) findViewById(R.id.linearAddForms);
        imageUser = (DWCRoundedImageView) findViewById(R.id.view);
        imageBack = (ImageView) findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Gson gson = new Gson();
        Card_Management__c _cardManagement = gson.fromJson(getIntent().getExtras().getString("object"), Card_Management__c.class);
        if (_cardManagement.getPersonal_Photo__c() != null && !_cardManagement.getPersonal_Photo__c().equals(""))
            Utilities.setUserPhoto(this, _cardManagement.getPersonal_Photo__c(), imageUser);
        tvCardOwnerName.setText(Utilities.stringNotNull(_cardManagement.getFull_Name__c()));
        _views = new ArrayList<DWCView>();
        _views.add(new DWCView("Card Details", ItemType.HEADER));
        _views.add(new DWCView("Card Number", ItemType.LABEL));
        _views.add(new DWCView(Utilities.stringNotNull(_cardManagement.getCard_Number__c()), ItemType.VALUE));
        _views.add(new DWCView("", ItemType.LINE));
        _views.add(new DWCView("Type", ItemType.LABEL));
        _views.add(new DWCView(Utilities.stringNotNull(_cardManagement.getCard_Type__c()), ItemType.VALUE));
        _views.add(new DWCView("", ItemType.LINE));
        _views.add(new DWCView("Expiry Date", ItemType.LABEL));
        _views.add(new DWCView(Utilities.formatVisitVisaDate(Utilities.stringNotNull(_cardManagement.getCard_Expiry_Date__c())), ItemType.VALUE));
        _views.add(new DWCView("", ItemType.LINE));
        _views.add(new DWCView("Status", ItemType.LABEL));
        _views.add(new DWCView(Utilities.stringNotNull(_cardManagement.getStatus__c()), ItemType.VALUE));
        _views.add(new DWCView("Person Details", ItemType.HEADER));
        _views.add(new DWCView("Passport Number", ItemType.LABEL));
        _views.add(new DWCView(Utilities.stringNotNull(_cardManagement.getPassport_Number__c()), ItemType.VALUE));
        _views.add(new DWCView("", ItemType.LINE));
        _views.add(new DWCView("Nationality", ItemType.LABEL));
        _views.add(new DWCView(Utilities.stringNotNull(_cardManagement.getNationality__r().getName()), ItemType.VALUE));
        _views.add(new DWCView("", ItemType.LINE));
        _views.add(new DWCView("Designation", ItemType.LABEL));
        _views.add(new DWCView(Utilities.stringNotNull(_cardManagement.getDesignation__c()), ItemType.VALUE));
        _views.add(new DWCView("", ItemType.LINE));
        _views.add(new DWCView("Sponsor company", ItemType.LABEL));
        _views.add(new DWCView(Utilities.stringNotNull(_cardManagement.getSponsor__c()), ItemType.VALUE));
        ArrayList<ServiceItem> _items = new ArrayList<ServiceItem>();

        Calendar _calendar = Calendar.getInstance();
        int DaysToExpire = (int) Utilities.daysDifference(_cardManagement.getCard_Expiry_Date__c());
        if (_cardManagement.getStatus__c().equals("Expired") || (_cardManagement.getStatus__c().equals("Active") && DaysToExpire < 7)) {
            _items.add(new ServiceItem("Renew Card", R.mipmap.renew_card));
        }

        if (_cardManagement.getStatus__c().equals("Active")) {
            _items.add(new ServiceItem("Cancel Card", R.mipmap.cancel_card));
            _items.add(new ServiceItem("Replace Card", R.mipmap.replace_card));
        }

        String str = "";
        if (_items.size() > 0) {
            for (int i = 0; i < _items.size(); i++) {
                str += _items.get(i).getTvServiceName() + ",";
            }

            if (!str.equals("")) {
                str = str.substring(0, str.length() - 1);
            }
            _views.add(new DWCView(str, ItemType.HORIZONTAL_LIST_VIEW));
        }

        View viewItems = Utilities.drawViewsOnLayout(this, _cardManagement, getApplicationContext(), _views);
        linearLayout.removeAllViews();
        linearLayout.addView(viewItems);
    }
}
