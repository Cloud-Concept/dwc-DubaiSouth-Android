package activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;

import cloudconcept.dwc.R;
import model.Directorship;

/**
 * Created by Abanoub Wagdy on 9/17/2015.
 */
public class DirectorShowDetailsActivity extends Activity {
    TextView tvFullName, tvNationality, tvPassportNumber, tvRole, tvStartDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_details_director);
        tvFullName = (TextView) findViewById(R.id.tvName);
        tvNationality = (TextView) findViewById(R.id.tvNationality);
        tvPassportNumber = (TextView) findViewById(R.id.tvPassportNumber);
        tvRole = (TextView) findViewById(R.id.tvRole);
        tvStartDate = (TextView) findViewById(R.id.tvStartDate);
        Gson gson = new Gson();
        Directorship _directorship = gson.fromJson(getIntent().getExtras().getString("object"), Directorship.class);
        tvFullName.setText(_directorship.get_director().getName() == null ? "" : _directorship.get_director().getName());
        tvNationality.setText(_directorship.get_director().getNationality() == null ? "" : _directorship.get_director().getNationality());
        tvPassportNumber.setText(_directorship.get_director().get_currentPassport() == null ? "" : _directorship.get_director().get_currentPassport().getName());
        tvRole.setText(_directorship.getRoles() == null ? "" : _directorship.getRoles());
        tvStartDate.setText(_directorship.getDirectorship_Start_Date() == null ? "" : _directorship.getDirectorship_Start_Date());
    }
}
