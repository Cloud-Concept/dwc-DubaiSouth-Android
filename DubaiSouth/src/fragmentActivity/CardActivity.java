package fragmentActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import RestAPI.RestMessages;
import cloudconcept.dwc.R;
import dataStorage.StoreData;
import exceptionHandling.ExceptionHandler;
import fragment.Cards.CancelCard.CancelCardMainFragment;
import fragment.Cards.NOCAttachmentPage;
import fragment.Cards.NewCard.MainNewCardFragment;
import model.Card_Management__c;
import model.Company_Documents__c;
import model.Country__c;
import model.User;
import model.WebForm;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 8/19/2015.
 */
public class CardActivity extends BaseFragmentActivity {



    public Card_Management__c getCard() {
        return card;
    }

    public void setCard(Card_Management__c card) {
        this.card = card;
    }

    private Card_Management__c card;

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    private String Duration;


    private String cardType;

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardRecordId() {
        return cardRecordId;
    }

    public void setCardRecordId(String cardRecordId) {
        this.cardRecordId = cardRecordId;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }



    public String getCaseRecordTypeId() {
        return caseRecordTypeId;
    }

    public void setCaseRecordTypeId(String caseRecordTypeId) {
        this.caseRecordTypeId = caseRecordTypeId;
    }


    private String caseRecordTypeId = null;

    private String cardRecordId = null;

    public ArrayList<Country__c> getCountries() {
        return countries;
    }

    public void setCountries(ArrayList<Country__c> countries) {
        this.countries = countries;
    }

    ArrayList<Country__c> countries;

    public String getCardRecordTypeId() {
        return cardRecordTypeId;
    }

    public void setCardRecordTypeId(String cardRecordTypeId) {
        this.cardRecordTypeId = cardRecordTypeId;
    }

    private String cardRecordTypeId = null;
    private String caseNumber = null;
    private Map<String, Object> serviceFields = new HashMap<String, Object>();
    private Map<String, String> parameters = new HashMap<String, String>();

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }




    public Map<String, Object> getServiceFields() {
        return serviceFields;
    }

    public void setServiceFields(Map<String, Object> serviceFields) {
        this.serviceFields = serviceFields;
    }






    private FragmentManager fragmentManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.new_card);
        gson = new Gson();
        user = gson.fromJson(new StoreData(getApplicationContext()).getUserDataAsString(), User.class);

        type = getIntent().getExtras().getString("type");
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("card")) {
                setCard((Card_Management__c) getIntent().getExtras().get("card"));
                card.setNationality__c(card.getNationality__r().getName());
                card.setAccount__c(card.getAccount__r().getName());
                card.setRequested_From__c("Portal");
            } else
                card = new Card_Management__c();
            card.setRequested_From__c("Portal");
        } else {
            card = new Card_Management__c();
            card.setRequested_From__c("Portal");
        }

        if (type.equals("1")) {
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, MainNewCardFragment.newInstance("NewCard"))
                    .commit();
        } else {
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, CancelCardMainFragment.newInstance("CancelCard"))
                    .commit();
        }
    }

}
