package activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cloudconcept.dwc.R;
import model.MyRequest;
import utilities.Utilities;

/**
 * Created by Abanoub Wagdy on 9/16/2015.
 */
public class ShowDetailsMyRequestsActivity extends Activity implements View.OnClickListener {

    MyRequest myRequest;
    Gson gson;
    ImageView imageBack;
    Button btnBack;
    TextView tvRefNumber, tvDate, tvStatus, tvRequestType, tvPersonName;
    ImageView imageRequestType;
    String[] services = new String[]{"Visa Services", "NOC Services", "License Services", "Access Cards Services", "Registration Services", "Leasing Services"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_details_myrequests);
        gson = new Gson();
        myRequest = gson.fromJson(getIntent().getExtras().getString("object"), MyRequest.class);
        InitializeViews(myRequest);
    }

    private void InitializeViews(MyRequest myRequest) {

        imageBack = (ImageView) findViewById(R.id.imageBack);
        btnBack = (Button) findViewById(R.id.btnBackTransparent);
        imageBack.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        tvRefNumber = (TextView) findViewById(R.id.tvRefNumber);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvStatus = (TextView) findViewById(R.id.tvCurrentStatus);
        tvRequestType = (TextView) findViewById(R.id.tvRequestType);
        tvPersonName = (TextView) findViewById(R.id.tvPersonName);
        imageRequestType = (ImageView) findViewById(R.id.imageRequestType);

        tvRefNumber.setText(Utilities.stringNotNull(myRequest.getCaseNumber()));
        String pattern = "yyyy-MM-dd'T'HH:mm:ss";
        SimpleDateFormat dtf =new SimpleDateFormat(pattern);
        try {
            Date dateTime = dtf.parse(Utilities.stringNotNull(myRequest.getCreatedDate()));
            pattern = "dd-MMM-yyyy hh:mm a";
            dtf=new SimpleDateFormat(pattern);
            tvDate.setText(dtf.format(dateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tvRequestType.setText(Utilities.stringNotNull(myRequest.getType()));
        tvRequestType.setText(Utilities.stringNotNull(myRequest.getSub_Type()).equals("") ? Utilities.stringNotNull(myRequest.getSub_Type_Formula()) : Utilities.stringNotNull(myRequest.getSub_Type()));

        tvStatus.setText(myRequest.getStatus());

        if (myRequest.getType() != null) {
            if (myRequest.getType().equals(services[0])) {
                imageRequestType.setBackgroundResource(R.drawable.notification_visa);
            } else if (myRequest.getType().equals(services[1])) {
                imageRequestType.setBackgroundResource(R.drawable.notification_noc);
            } else if (myRequest.getType().equals(services[2])) {
                imageRequestType.setBackgroundResource(R.drawable.notification_license);
            } else if (myRequest.getType().equals(services[3])) {
                imageRequestType.setBackgroundResource(R.drawable.notification_card_icon);
            } else if (myRequest.getType().equals(services[4])) {
                imageRequestType.setBackgroundResource(R.drawable.notification_registration);
            } else if (myRequest.getType().equals(services[5])) {
                imageRequestType.setBackgroundResource(R.drawable.notification_leasing);
            }
        }

    }

    @Override
    public void onClick(View v) {
        if (v == btnBack || v == imageBack) {
            finish();
        }
    }
}
