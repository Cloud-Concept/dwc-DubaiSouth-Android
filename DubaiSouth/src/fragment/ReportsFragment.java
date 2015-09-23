package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cloudconcept.dwc.R;
import utilities.ActivitiesLauncher;

/**
 * Created by Bibo on 7/22/2015.
 */
public class ReportsFragment extends Fragment implements View.OnClickListener {

    ImageView imageMyRequests, imageStatementOfAccount;
    TextView tvMyRequests, tvStatementOfAccount;


    public static Fragment newInstance(String reports) {
        ReportsFragment fragment = new ReportsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("text", reports);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reports, container, false);
        imageMyRequests = (ImageView) view.findViewById(R.id.imageMyRequests);
        imageStatementOfAccount = (ImageView) view.findViewById(R.id.imageStatementOfAccount);
        tvMyRequests = (TextView) view.findViewById(R.id.tvMyRequests);
        tvStatementOfAccount = (TextView) view.findViewById(R.id.tvStatementOfAccount);
        imageMyRequests.setOnClickListener(this);
        imageStatementOfAccount.setOnClickListener(this);
        tvMyRequests.setOnClickListener(this);
        tvStatementOfAccount.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == imageMyRequests || v == tvMyRequests) {
            ActivitiesLauncher.openMyRequestsActivity(getActivity().getApplicationContext());
        } else if (v == imageStatementOfAccount || v == tvStatementOfAccount) {
            ActivitiesLauncher.openViewStatementActivity(getActivity().getApplicationContext());
        }
    }
}