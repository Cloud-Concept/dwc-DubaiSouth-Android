package activity;

import android.support.v4.app.Fragment;
import android.view.View;

import cloudconcept.dwc.BaseActivity;
import fragment.LegalRepresentativesShowDetailsFragment;

/**
 * Created by Abanoub Wagdy on 9/24/2015.
 */
public class LegalRepresentativesShowDetailsActivity extends BaseActivity {

    String objectType;

    @Override
    public int getNotificationVisibillity() {
        return View.VISIBLE;
    }

    @Override
    public int getMenuVisibillity() {
        return 0;
    }

    @Override
    public int getBackVisibillity() {
        return View.VISIBLE;
    }

    @Override
    public String getHeaderTitle() {
        objectType = getIntent().getExtras().getString("objectType");
        if (objectType.equals("LegalRepresentative")) {
            return "Legal Representatives";
        } else if (objectType.equals("ManagementMember")) {
            return "General Managers";
        } else {
            return "Shareholders";
        }
    }

    @Override
    public Fragment GetFragment() {
        objectType = getIntent().getExtras().getString("objectType");
        Fragment fragment = LegalRepresentativesShowDetailsFragment.newInstance("LegalRepresentativesShowDetailsFragment", objectType, getIntent().getExtras().getString("object"));
        return fragment;
    }
}
