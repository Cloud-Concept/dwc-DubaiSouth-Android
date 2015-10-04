package utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

import java.util.ArrayList;

import activity.AccessCardShowDetailsActivity;
import activity.CompanyDocumentsActivity;
import activity.DirectorShowDetailsActivity;
import activity.EmployeeListActivity;
import activity.LegalRepresentativesShowDetailsActivity;
import activity.ReportsActivity;
import activity.ShowDetailsMyRequestsActivity;
import activity.ViewStatementShowDetails;
import cloudconcept.dwc.HomepageActivity;
import fragmentActivity.CardActivity;
import fragmentActivity.ChangeAndRemovalActivity;
import fragmentActivity.CompanyInfoActivity;
import fragmentActivity.CompanyNOC.CompanyNocActivity;
import fragmentActivity.DashboardActivity;
import fragmentActivity.HomeCompanyDocumentsActivity;
import fragmentActivity.LeasingShowDetailsActivity;
import fragmentActivity.LicenseActivity;
import fragmentActivity.LicenseCancellationActivity;
import fragmentActivity.MyRequestsActivity;
import fragmentActivity.NOCScreen.NocActivity;
import fragmentActivity.NameReservationActivity;
import fragmentActivity.NeedHelpActivity;
import fragmentActivity.NotificationsActivity;
import fragmentActivity.PreviewActivity;
import fragmentActivity.QuickAccessActivity;
import fragmentActivity.RequestTrueCopyActivity;
import fragmentActivity.ShareHolderActivity;
import fragmentActivity.ShowDetailsActivity;
import fragmentActivity.ThankYouActivity;
import fragmentActivity.ViewStatementActivity;
import fragmentActivity.VisaActivity;
import fragmentActivity.VisasAndCardsActivity;
import model.Card_Management__c;
import model.Company_Documents__c;
import model.Contract_DWC__c;
import model.Directorship;
import model.EServices_Document_Checklist__c;
import model.FreeZonePayment;
import model.LegalRepresentative;
import model.ManagementMember;
import model.MyRequest;
import model.ShareOwnership;
import model.User;
import model.Visa;

/**
 * Created by Abanoub on 6/14/2015.
 */
public class ActivitiesLauncher {

    private static Intent intent;
    private static Gson gson;

    public static void openDashboardActivity(Context applicationContext) {
        intent = new Intent(applicationContext, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        applicationContext.startActivity(intent);

    }

    public static void openMyRequestsActivity(Context applicationContext) {
        intent = new Intent(applicationContext, MyRequestsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        applicationContext.startActivity(intent);

    }

    public static void openNotificationsActivity(Context applicationContext) {
        intent = new Intent(applicationContext, NotificationsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        applicationContext.startActivity(intent);
    }

    public static void openCompanyInfoActivity(Context applicationContext) {
        intent = new Intent(applicationContext, CompanyInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        applicationContext.startActivity(intent);

    }

    public static void openReportsActivity(Context applicationContext) {
        intent = new Intent(applicationContext, ReportsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        applicationContext.startActivity(intent);

    }

    public static void openNeedHelpActivity(Context applicationContext) {
        intent = new Intent(applicationContext, NeedHelpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        applicationContext.startActivity(intent);

    }

    public static void openCompanyDocumentsActivity(Context applicationContext) {
        intent = new Intent(applicationContext, CompanyDocumentsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        applicationContext.startActivity(intent);

    }

    public static void openHomeCompanyDocumentsActivity(Context applicationContext) {
        intent = new Intent(applicationContext, HomeCompanyDocumentsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        applicationContext.startActivity(intent);

    }

    public static void openQuickAccessActivity(Context applicationContext) {
        intent = new Intent(applicationContext, QuickAccessActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        applicationContext.startActivity(intent);

    }

    public static void openVisasAndCardsActivity(Context applicationContext) {
        intent = new Intent(applicationContext, VisasAndCardsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        applicationContext.startActivity(intent);

    }

    public static void openHomePageActivity(Context applicationContext) {
        intent = new Intent(applicationContext, HomepageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        applicationContext.startActivity(intent);
    }

    public static void openNOCActivity(Context applicationContext, String objectAsString, String ObjectType) {
        intent = new Intent(applicationContext, NocActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("objectAsString", objectAsString);
//        Visa v=new Gson().fromJson(objectAsString,Visa.class);
        intent.putExtra("ObjectType", ObjectType);
//        intent.putExtra("object",v);
        applicationContext.startActivity(intent);
    }

    public static void openShowDetailsActivity(Context applicationContext, String title, String objectAsString, String objectType) {
        intent = new Intent(applicationContext, ShowDetailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("objectAsString", objectAsString);
        intent.putExtra("title", "Show Details");
        intent.putExtra("objectType", objectType);
        applicationContext.startActivity(intent);
    }

    public static void openEmployeeListActivity(Context applicationContext) {
        intent = new Intent(applicationContext, EmployeeListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        applicationContext.startActivity(intent);
    }

    public static void openThankYouActivity(Activity activity, Context context, String Message) {
        intent = new Intent(context, ThankYouActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Message", Message);
        context.startActivity(intent);
        activity.finish();
    }

    public static void openCompanyNocActivity(Context applicationContext) {
        intent = new Intent(applicationContext, CompanyNocActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        applicationContext.startActivity(intent);
    }

    public static void openNewCardActivity(Context applicationContext, String type) {
        intent = new Intent(applicationContext, CardActivity.class);
        intent.putExtra("type", type);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        applicationContext.startActivity(intent);
    }

    public static void openCardActivity(Context context, Card_Management__c card, String type) {
        intent = new Intent(context, CardActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("card", card);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void openVisaActivity(Context context, Visa visa, String renew) {
        intent = new Intent(context, VisaActivity.class);
        intent.putExtra("type", renew);
        intent.putExtra("visa", visa);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void openShowContractDetailsActivity(Context context, Contract_DWC__c contract_dwc__c) {
        intent = new Intent(context, LeasingShowDetailsActivity.class);
        intent.putExtra("contract", contract_dwc__c);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void openNameReservationActivity(Context context) {
        intent = new Intent(context, NameReservationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);

    }

    public static void openGenericChangeAndRemovalActivity(Context context, String s, Object object) {
        intent = new Intent(context, ChangeAndRemovalActivity.class);
        if (object instanceof Directorship) {
            Directorship directorship = (Directorship) object;
            Gson gson = new Gson();
            intent.putExtra("object", gson.toJson(directorship));
        } else if (object instanceof User) {
            User user = (User) object;
            if (user.get_contact().get_account().getEstablishmentCard() != null) {
                intent.putExtra("Current_Establishment_Card__c", user.get_contact().get_account().getEstablishmentCard().getCurrent_Establishment_Card__c());
                intent.putExtra("CardNumber", user.get_contact().get_account().getEstablishmentCard().getEstablishment_Card_Number__c());
                intent.putExtra("LicenseNumber", user.get_contact().get_account().get_currentLicenseNumber().getLicense_Number_Value());
                intent.putExtra("IssueDate", user.get_contact().get_account().getEstablishmentCard().getIssue_Date__c());
                intent.putExtra("ExpiryDate", user.get_contact().get_account().getEstablishmentCard().getExpiry_Date__c());
                intent.putExtra("Status", user.get_contact().get_account().getEstablishmentCard().getStatus());
            }
        }
        intent.putExtra("MethodType", s);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void openViewStatementActivity(Context context) {
        intent = new Intent(context, ViewStatementActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void openViewStatementShowDetails(Context context, FreeZonePayment freeZonePayment) {
        Gson gson = new Gson();
        String str = gson.toJson(freeZonePayment);
        intent = new Intent(context, ViewStatementShowDetails.class);
        intent.putExtra("str", str);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void openShareHolderActivity(Context context, ShareOwnership shareOwnership, String type, ArrayList<Object> objects) {
        intent = new Intent(context, ShareHolderActivity.class);
        intent.putExtra("type", "1");
        intent.putExtra("share", shareOwnership);
        intent.putExtra("shareHolders", objects);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void openCompanyDocumentsRequestTrueCopyActivity(Context context, EServices_Document_Checklist__c eServices_document_checklist__c) {
        Gson gson = new Gson();
        String str = gson.toJson(eServices_document_checklist__c);
        intent = new Intent(context, RequestTrueCopyActivity.class);
        intent.putExtra("object", str);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

//    public static void openCustomerDocumentsEditActivity(Context context, Company_Documents__c company_documents__c) {
//        Gson gson = new Gson();
//        String str = gson.toJson(company_documents__c);
//        intent = new Intent(context, CustomerDocumentEditActivity.class);
//        intent.putExtra("object", str);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        context.startActivity(intent);
//    }

    public static void openCustomerDocumentsPreviewActivity(Context context, Object object) {
        Gson gson = new Gson();
        String str = gson.toJson(object);
        intent = new Intent(context, PreviewActivity.class);
        intent.putExtra("object", str);
        if (object instanceof Company_Documents__c) {
            intent.putExtra("type", "Company_Documents__c");
        } else {
            intent.putExtra("type", "EServices_Document_Checklist__c");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);

    }

    public static void openLicenseCancellationActivity(Context context) {
        intent = new Intent(context, LicenseCancellationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void openChangeAndRemovalLicenceActivity(Context context, User _user, String s) {
        intent = new Intent(context, LicenseActivity.class);
        intent.putExtra("user", _user);
        intent.putExtra("type", s);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void openMyRequestsShowDetailsActivity(Context context, MyRequest myRequest) {
        intent = new Intent(context, ShowDetailsMyRequestsActivity.class);
        gson = new Gson();
        String ObjectAsStr = gson.toJson(myRequest);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("object", ObjectAsStr);
        context.startActivity(intent);
    }


    public static void openDirectorShowDetailsActivity(Context context, Directorship directorship) {
        intent = new Intent(context, DirectorShowDetailsActivity.class);
        gson = new Gson();
        String ObjectAsStr = gson.toJson(directorship);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("object", ObjectAsStr);
        context.startActivity(intent);
    }

    public static void openAccessCardShowDetailsActivity(Context context, Card_Management__c card) {
        intent = new Intent(context, AccessCardShowDetailsActivity.class);
        gson = new Gson();
        String ObjectAsStr = gson.toJson(card);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("object", ObjectAsStr);
        context.startActivity(intent);
    }

    public static void openLegalRepresentativesShowDetailsActivity(Context context, LegalRepresentative legalRepresentative) {
        intent = new Intent(context, LegalRepresentativesShowDetailsActivity.class);
        gson = new Gson();
        String ObjectAsStr = gson.toJson(legalRepresentative);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("object", ObjectAsStr);
        intent.putExtra("objectType", "LegalRepresentative");
        context.startActivity(intent);
    }

    public static void openGeneralManagersShowDetailsActivity(Context context, ManagementMember managementMember) {
        intent = new Intent(context, LegalRepresentativesShowDetailsActivity.class);
        gson = new Gson();
        String ObjectAsStr = gson.toJson(managementMember);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("object", ObjectAsStr);
        intent.putExtra("objectType", "ManagementMember");
        context.startActivity(intent);
    }

    public static void openShareHolderShowDetailsActivity(Context context, ShareOwnership shareHolder) {
        intent = new Intent(context, LegalRepresentativesShowDetailsActivity.class);
        gson = new Gson();
        String ObjectAsStr = gson.toJson(shareHolder);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("object", ObjectAsStr);
        intent.putExtra("objectType", "ShareHolder");
        context.startActivity(intent);
    }
}
