package dataStorage;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class StoreData {
    private Context context;
    String DATABASE_NAME = "DWC";
    private SharedPreferences sharedPreferences;
    private Editor editor;
    String TAG = "StoreData";

    public StoreData(Context ctx) {
        super();
        this.context = ctx;

        sharedPreferences = context.getSharedPreferences(DATABASE_NAME,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveUserID(String ID) {
        editor.putString("UserID", ID);
        editor.commit();
    }

    public String getUserID() {
        String UserID = sharedPreferences.getString("UserID",
                "");
        return UserID;
    }

    public synchronized void setCompanyRestResponse(String s) {
        editor.putString("CompanyRestResponse", s);
        editor.commit();
    }

    public synchronized String getCompanyRestResponse() {
        String UserID = sharedPreferences.getString("CompanyRestResponse",
                "");
        return UserID;
    }

    public void setUsername(String name) {
        editor.putString("Username", name);
        editor.commit();
    }

    public synchronized String getUsername() {
        String UserID = sharedPreferences.getString("Username",
                "");
        return UserID;
    }

    public synchronized void setUserDataAsString(String json) {
        editor.putString("jsonUser", json);
        editor.commit();
    }

    public synchronized String getUserDataAsString() {
        String UserData = sharedPreferences.getString("jsonUser",
                "");
        return UserData;
    }

    public void setNotificationCount(String s) {

        editor.putString("notificationCount", s);
        editor.commit();
    }

    public synchronized int getNotificationCount() {

        int NotificationCount = Integer.valueOf(sharedPreferences.getString("notificationCount",
                ""));
        return NotificationCount;
    }

    public synchronized void saveNocType(String selectedItem) {
        editor.putString("noc_type", selectedItem);
        editor.commit();
    }

    public synchronized String getNocType() {
        String noc_type = sharedPreferences.getString("noc_type",
                "");
        return noc_type;
    }

    public synchronized void saveNOCAuthorityType(String selectedItem) {
        editor.putString("noc__authority_type", selectedItem);
        editor.commit();
    }

    public synchronized String getNOCAuthorityType() {
        String noc__authority_type = sharedPreferences.getString("noc__authority_type",
                "");
        return noc__authority_type;
    }

    public void saveNOCLanguage(String selectedItem) {
        editor.putString("noc__language", selectedItem);
        editor.commit();
    }

    public synchronized String getNOCLanguage() {
        String noc__language = sharedPreferences.getString("noc__language",
                "");
        return noc__language;
    }

    public void setIsLoadedInitialEmployeeNOCPage(boolean b) {

        editor.putBoolean("InitialEmployeeNOCPage", b);
        editor.commit();
    }

    public synchronized boolean getIsLoadedInitialEmployeeNOCPage() {
        boolean b = sharedPreferences.getBoolean("InitialEmployeeNOCPage",
                false);
        return b;
    }

    public void setPickListSelected(String key, String entry) {
        editor.putString(key, entry);
        editor.commit();
    }

    public synchronized String getPickListSelected(String key) {
        String b = sharedPreferences.getString(key,
                "");
        return b;
    }

    public void setCheckBoxSelected(String s, boolean b) {
        editor.putBoolean(s, b);
        editor.commit();
    }

    public synchronized boolean getIsLoadedInitialEmployeeNOCPage(String s) {
        boolean b = sharedPreferences.getBoolean(s,
                false);
        return b;
    }

    public void setCompanyDocumentObject(String s) {
        editor.putString("CompanyDocumentObject", s);
        editor.commit();
    }

    public String getCompanyDocumentObject() {
        String b = sharedPreferences.getString("CompanyDocumentObject",
                "");
        return b;
    }

    public void setCompanyDocumentPosition(int position) {
        editor.putString("CompanyDocumentPosition", String.valueOf(position));
        editor.commit();
    }

    public String getCompanyDocumentPosition() {
        String b = sharedPreferences.getString("CompanyDocumentPosition",
                "");
        return b;
    }

    public void reset() {
        sharedPreferences = context.getSharedPreferences(DATABASE_NAME,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public void setFirstTimeLogin(boolean temp) {
        editor.putBoolean("FirstTimeLogin", temp);
        editor.commit();
    }

    public boolean getFirstTimeLogin() {
        boolean b = sharedPreferences.getBoolean("FirstTimeLogin",
                true);
        return b;
    }

    public void setHomepageScreenInfo(String s) {
        editor.putString("CompanyScreenInfo", s);
        editor.commit();
    }

    public String getHomepageScreenInfo() {
        String b = sharedPreferences.getString("CompanyScreenInfo",
                "");
        return b;
    }

    public void setIsHomepageDataLoaded(boolean temp) {
        editor.putBoolean("HomepageDataLoaded", temp);
        editor.commit();
    }

    public boolean getIsHomepageDataLoaded() {
        boolean b = sharedPreferences.getBoolean("HomepageDataLoaded",
                false);
        return b;

    }

    public void savePermanentEmployeeResponse(String s) {
        editor.putString("permanentEmployeeResponse", s);
        editor.commit();
    }

    public String getPermanentEmployeeResponse() {
        String b = sharedPreferences.getString("permanentEmployeeResponse",
                "");
        return b;
    }

    public void setPermanentEmployeeSpinnerFilterValue(String strFilter) {

        editor.putString("PermanentEmployeeSpinnerFilterValue", strFilter);
        editor.commit();
    }

    public String getPermanentEmployeeSpinnerFilterValue() {
        String b = sharedPreferences.getString("PermanentEmployeeSpinnerFilterValue",
                "");
        return b;
    }

    public void saveAccessCardResponse(String s) {
        editor.putString("AccessCardResponse", s);
        editor.commit();
    }


    public String getAccessCardResponse() {
        String b = sharedPreferences.getString("AccessCardResponse",
                "");
        return b;
    }

    public void setAccessCardSpinnerFilterValue(String strFilter) {

        editor.putString("AccessCardSpinnerFilterValue", strFilter);
        editor.commit();
    }

    public String getAccessCardSpinnerFilterValue() {
        String b = sharedPreferences.getString("AccessCardSpinnerFilterValue",
                "");
        return b;
    }

    public void saveVisitVisaResponse(String s) {
        editor.putString("VisitVisaResponse", s);
        editor.commit();
    }

    public String getVisitVisaResponse() {
        String b = sharedPreferences.getString("VisitVisaResponse",
                "");
        return b;
    }

    public void setVisitVisaSpinnerFilterValue(String strFilter) {

        editor.putString("VisitVisaSpinnerFilterValue", strFilter);
        editor.commit();
    }

    public String getVisitVisaSpinnerFilterValue() {
        String b = sharedPreferences.getString("VisitVisaSpinnerFilterValue",
                "");
        return b;
    }


    public void saveCertificatesAgreementsResponse(String s) {
        editor.putString("CertificatesAgreements", s);
        editor.commit();
    }

    public String getCertificatesAgreementsResponse() {
        String b = sharedPreferences.getString("CertificatesAgreements",
                "");
        return b;
    }

    public void saveCustomerDocumentsResponse(String s) {
        editor.putString("CustomerDocumentsResponse", s);
        editor.commit();
    }

    public String getCustomerDocumentsResponse() {
        String b = sharedPreferences.getString("CustomerDocumentsResponse",
                "");
        return b;
    }

    public void setMyRequestsStatus(String s) {
        editor.putString("MyRequestsStatus", s);
        editor.commit();
    }


    public String getMyRequestsStatus() {

        String b = sharedPreferences.getString("MyRequestsStatus",
                "");
        return b;
    }

    public void setMyRequestsRequestType(String s) {
        editor.putString("MyRequestsRequestType", s);
        editor.commit();
    }

    public String getMyRequestsRequestType() {
        String b = sharedPreferences.getString("MyRequestsRequestType",
                "");
        return b;
    }

    public void setLastMyRequestsResponse(String s) {
        editor.putString("MyRequestsResponse", s);
        editor.commit();
    }

    public String getLastMyRequestsResponse() {
        String b = sharedPreferences.getString("MyRequestsResponse",
                "");
        return b;
    }

    public void setCompanyDocumentFromAttachment(String s) {
        editor.putString("CompanyDocumentFromAttachment", s);
        editor.commit();
    }

    public String getCompanyDocumentFromAttachment() {
        String b = sharedPreferences.getString("CompanyDocumentFromAttachment",
                "");
        return b;
    }

    public void setLeasingInfoResponse(String s) {
        editor.putString("LeasingInfoResponse", s);
        editor.commit();
    }

    public String getLeasingInfoResponse() {
        String b = sharedPreferences.getString("LeasingInfoResponse",
                "");
        return b;
    }

    public void setShareholdersResponse(String s) {
        editor.putString("ShareholdersResponse", s);
        editor.commit();
    }

    public String getShareholdersResponse() {
        String b = sharedPreferences.getString("ShareholdersResponse",
                "");
        return b;
    }

    public void setDirectorsResponse(String s) {
        editor.putString("DirectorsResponse", s);
        editor.commit();
    }

    public String getDirectorsResponse() {
        String b = sharedPreferences.getString("DirectorsResponse",
                "");
        return b;
    }

    public void setGeneralManagersResponse(String s) {
        editor.putString("GeneralManagersResponse", s);
        editor.commit();
    }

    public String getGeneralManagersResponse() {
        String b = sharedPreferences.getString("GeneralManagersResponse",
                "");
        return b;
    }

    public void setLegalRepresentativesResponse(String s) {
        editor.putString("LegalRepresentativesResponse", s);
        editor.commit();
    }

    public String getLegalRepresentativesResponse() {
        String b = sharedPreferences.getString("LegalRepresentativesResponse",
                "");
        return b;
    }

    public void setLicenseActivityResponse(String s) {
        editor.putString("LicenseActivityResponse", s);
        editor.commit();
    }

    public String getLicenseActivityResponse() {
        String b = sharedPreferences.getString("LicenseActivityResponse",
                "");
        return b;
    }

    public void setInvoicesResponse(String s) {
        editor.putString("InvoicesResponse", s);
        editor.commit();
    }


    public String getInvoicesResponse() {
        String b = sharedPreferences.getString("InvoicesResponse",
                "");
        return b;
    }

    public void setEstablishmentCardPageExist(boolean b) {
        editor.putBoolean("EstablishmentCardPageExist", b);
        editor.commit();
    }

    public boolean getEstablishmentCardPageExist() {
        boolean b = sharedPreferences.getBoolean("EstablishmentCardPageExist",
                false);
        return b;
    }
}
