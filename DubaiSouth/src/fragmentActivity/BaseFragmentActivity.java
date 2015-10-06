package fragmentActivity;

import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import model.Company_Documents__c;
import model.Receipt_Template__c;
import utilities.AutomaticUtilities;

/**
 * Created by M_Ghareeb on 8/25/2015.
 */
public class BaseFragmentActivity extends FragmentActivity {
    public static final int RESULT_LOAD_IMG_FROM_GALLERY = 2;
    public static final int RESULT_LOAD_IMG_FROM_CAMERA = 3;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    String total;
    public Company_Documents__c company_documents__c;
    public Receipt_Template__c geteServiceAdministration() {
        return eServiceAdministration;
    }

    public void seteServiceAdministration(Receipt_Template__c eServiceAdministration) {
        this.eServiceAdministration = eServiceAdministration;
    }

    private Receipt_Template__c eServiceAdministration;
    @Override
    public void onBackPressed() {

    }


}
