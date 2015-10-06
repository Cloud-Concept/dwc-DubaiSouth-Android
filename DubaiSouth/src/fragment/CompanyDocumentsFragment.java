package fragment;

import com.blunderer.materialdesignlibrary.handlers.ViewPagerHandler;

import custom.DWCViewPagerFragment;
import fragment.companyDocumentsScreen.CertificatesAndAgreementsFragment2;
import fragment.companyDocumentsScreen.CustomerDocumentsFragment2;

/**
 * Created by Abanoub Wagdy on 9/8/2015.
 */
public class CompanyDocumentsFragment extends DWCViewPagerFragment {

    @Override
    public boolean showViewPagerIndicator() {
        return true;
    }

    @Override
    public ViewPagerHandler getViewPagerHandler() {
        return new ViewPagerHandler(getActivity())
                .addPage("True Copies", CertificatesAndAgreementsFragment2.newInstance("CertificatesAndAgreementsFragment"))
                .addPage("Customer Documents", CustomerDocumentsFragment2.newInstance("CustomerDocumentsFragment"));
    }

    @Override
    public int defaultViewPagerPageSelectedPosition() {
        return 0;
    }


}

