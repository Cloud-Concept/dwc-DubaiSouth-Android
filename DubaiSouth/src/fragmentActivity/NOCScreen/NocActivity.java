package fragmentActivity.NOCScreen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.apache.james.mime4j.codec.EncoderUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import RestAPI.RestMessages;
import RestAPI.SFResponseManager;
import adapter.DWCExpandableListAdapter;
import cloudconcept.dwc.R;
import dataStorage.StoreData;
import exceptionHandling.ExceptionHandler;
import fragmentActivity.BaseFragmentActivity;
import model.Company_Documents__c;
import model.NOC__c;
import model.User;
import model.Visa;
import utilities.Utilities;

public class NocActivity extends BaseFragmentActivity {

    private Gson gson;
    private Context ctx;
    private Activity act;
    private String objectType;
    public static Visa _visa;
    private FragmentManager fragmentManager;
    private static int RESULT_LOAD_IMG_FROM_GALLERY = 2;
    private static int RESULT_LOAD_IMG_FROM_CAMERA = 3;
    private Company_Documents__c company_documents__c;
    RestRequest restRequest;
    static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.noc);

        gson = new Gson();
        ctx = getApplicationContext();
        act = this;
        objectType = getIntent().getExtras().getString("ObjectType");
        if (objectType.equals("Visa")) {
            _visa = gson.fromJson(getIntent().getExtras().getString("objectAsString"), Visa.class);
        } else {
            user = gson.fromJson(new StoreData(getApplicationContext()).getUserDataAsString(), User.class);
        }

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, NocMainFragment.newInstance("BaseEmployee",this))
                .commit();

    }


    public static Visa get_visa() {
        return _visa;
    }



    @Override
    public void onBackPressed() {

    }
}
