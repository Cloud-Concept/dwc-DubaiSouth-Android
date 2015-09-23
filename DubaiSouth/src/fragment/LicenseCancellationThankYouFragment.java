package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import cloudconcept.dwc.R;

/**
 * Created by Abanoub Wagdy on 9/13/2015.
 */
public class LicenseCancellationThankYouFragment extends Fragment implements View.OnClickListener {

    Button btnClose;
    TextView tvThankYouMessage;
    static String Amount;


    public static Fragment newInstance(String thankYou, String s) {
        LicenseCancellationThankYouFragment fragment = new LicenseCancellationThankYouFragment();
        Bundle bundle = new Bundle();
        Amount = s;
        bundle.putString("FragmentName", s);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.thank_you, container, false);
        InitializeViews(view);
        return view;
    }

    private void InitializeViews(View view) {
        btnClose = (Button) view.findViewById(R.id.btnClose);
        tvThankYouMessage = (TextView) view.findViewById(R.id.tvThankyou);
        String Message = getString(R.string.ServiceThankYouMessage) + "\n" + String.format(getString(R.string.ServiceThankYouMessagePayment), Amount);
        tvThankYouMessage.setText(Message);
        btnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnClose) {
            getActivity().finish();
        }
    }
}
