package fragmentActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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


import fragment.Visa.Cancel.CancelVisaMainFragment;
import fragment.Visa.NOCAttachmentPage;

import fragment.Visa.Renew.VisaMainFragment;

import fragment.Visa.RenewPassport.RenewPassportMainFragment;
import model.Company_Documents__c;
import model.Country__c;

import model.CurrentPassport;
import model.JobTitleAtImmigration;
import model.User;
import model.Visa;
import utilities.Utilities;

/**
 * Created by M_Ghareeb on 8/30/2015.
 */
public class VisaActivity extends BaseFragmentActivity {

    private String visaRecordTypeId = null;
    private String caseNumber = null;


    public CurrentPassport getNewPassport() {
        return newPassport;
    }

    public void setNewPassport(CurrentPassport newPassport) {
        this.newPassport = newPassport;
    }

    private CurrentPassport newPassport;
    public String getService_Requested__c() {
        return service_Requested__c;
    }

    public void setService_Requested__c(String service_Requested__c) {
        this.service_Requested__c = service_Requested__c;
    }

    private String service_Requested__c=null;

    public Visa getVisa() {
        return visa;
    }

    public void setVisa(Visa visa) {
        this.visa = visa;
    }

    public List<Country__c> getCountries() {
        return countries;
    }

    public void setCountries(List<Country__c> countries) {
        this.countries = countries;
    }

    private List<Country__c> countries;
    private Visa visa;
    private List<JobTitleAtImmigration> Occupations;

    public List<JobTitleAtImmigration> getQualifications() {
        return Qualifications;
    }

    public void setQualifications(List<JobTitleAtImmigration> qualifications) {
        Qualifications = qualifications;
    }

    public List<JobTitleAtImmigration> getOccupations() {
        return Occupations;
    }

    public void setOccupations(List<JobTitleAtImmigration> occupations) {
        Occupations = occupations;
    }

    private List<JobTitleAtImmigration> Qualifications;

    public String getCaseRecordTypeId() {
        return caseRecordTypeId;
    }

    public void setCaseRecordTypeId(String caseRecordTypeId) {
        this.caseRecordTypeId = caseRecordTypeId;
    }

    public String getVisaRecordTypeId() {
        return visaRecordTypeId;
    }

    public void setVisaRecordTypeId(String visaRecordTypeId) {
        this.visaRecordTypeId = visaRecordTypeId;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }




    public String getVisaRecordId() {
        return visaRecordId;
    }

    public void setVisaRecordId(String visaRecordId) {
        this.visaRecordId = visaRecordId;
    }


    public Map<String, Object> getServiceFields() {
        return serviceFields;
    }

    public void setServiceFields(Map<String, Object> serviceFields) {
        this.serviceFields = serviceFields;
    }




    private String caseRecordTypeId = null;
    private String visaRecordId = null;
    private Map<String, Object> caseFields = new HashMap<String, Object>();
    private Map<String, Object> serviceFields = new HashMap<String, Object>();
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
            if (getIntent().getExtras().containsKey("visa")) {
                setVisa((Visa) getIntent().getExtras().get("visa"));
setInsertedServiceId(getVisa().getID());

            } else
                visa = new Visa();
        } else {
            visa = new Visa();
        }

        if (type.equals("renew")) {
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, VisaMainFragment.newInstance("RenewVisa"))
                    .commit();
        } else if (type.equals("Cancel")){
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, CancelVisaMainFragment.newInstance("CancelVisa"))
                    .commit();
        }else if (type.equals("Passport")){
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, RenewPassportMainFragment.newInstance("CancelVisa"))
                    .commit();
        }
    }


}


