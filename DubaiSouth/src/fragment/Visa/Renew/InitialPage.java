package fragment.Visa.Renew;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ClientManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import RestAPI.JSONConstants;
import RestAPI.RestMessages;
import RestAPI.SFResponseManager;
import RestAPI.SoqlStatements;
import adapter.NationalityAdapter;
import adapter.SimpleSpinnerStringAdapter;
import cloudconcept.dwc.R;
import fragmentActivity.CardActivity;
import fragmentActivity.VisaActivity;
import model.Card_Management__c;
import model.Country__c;
import model.CurrentNationality;
import model.FormField;
import model.Receipt_Template__c;
import model.Visa;
import utilities.Utilities;

/**
 * Created by M_Ghareeb on 8/30/2015.
 */
public class InitialPage extends Fragment {
    String visaQuery = "SELECT ID, Name, Display_Name__c, Service_Identifier__c, Amount__c, Total_Amount__c, Related_to_Object__c, New_Edit_VF_Generator__c, Renewal_VF_Generator__c, Replace_VF_Generator__c, Cancel_VF_Generator__c, Record_Type_Picklist__c, (SELECT ID, Name, Type__c, Language__c, Document_Type__c, Authority__c FROM eServices_Document_Checklists__r) FROM Receipt_Template__c WHERE Is_Active__c = true AND Duration__c = " + "\'" + "%s" + "\'" + " AND Record_Type_Picklist__c = " + "\'" + "%s" + "\'";
    private RestRequest restRequest;
    private ArrayList<Receipt_Template__c> eServiceAdministrations;
    private String objectType;
    private String DeveloperName;
    private VisaActivity activity;
    EditText Name, ArabicName, Gender, Dateofbirth, BirthCountry,
            PlaceCountry, Email, mobile,  mothername,
            currentnationality, Previousnationality,
            passportno, dateofexpiry,placeofissue;
    Spinner countryofissue,Maritalstatus, religion, languagesspoken;
    String []Maritalstatuses ={"Single", "Married", "Divorced", "Widowed"};
    String [] Relegion ={"Bahaei", "Budhist", "Christian", "Hindu", "Kadiani", "Muslim","Sikh"};
    String [] Language ={"Arabic", "English", "French"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_initial_renew_visa, container, false);
        activity = (VisaActivity) getActivity();
        InitializeViews(view);
        return view;
    }

    private void InitializeViews(View view) {
        Name = (EditText) view.findViewById(R.id.Name);
        ArabicName = (EditText) view.findViewById(R.id.ArabicName);
        Gender = (EditText) view.findViewById(R.id.Gender);
        Dateofbirth = (EditText) view.findViewById(R.id.Dateofbirth);
        BirthCountry = (EditText) view.findViewById(R.id.BirthCountry);
        PlaceCountry = (EditText) view.findViewById(R.id.PlaceCountry);
        Email = (EditText) view.findViewById(R.id.Email);
        mobile = (EditText) view.findViewById(R.id.mobile);
        Maritalstatus = (Spinner) view.findViewById(R.id.Maritalstatus);
        mothername = (EditText) view.findViewById(R.id.mothername);
        currentnationality = (EditText) view.findViewById(R.id.currentnationality);
        Previousnationality = (EditText) view.findViewById(R.id.Previousnationality);
        religion = (Spinner) view.findViewById(R.id.religion);
        languagesspoken = (Spinner) view.findViewById(R.id.languagesspoken);
        passportno = (EditText) view.findViewById(R.id.passportno);
        dateofexpiry = (EditText) view.findViewById(R.id.dateofexpiry);
        countryofissue = (Spinner) view.findViewById(R.id.countryofissue);
        placeofissue = (EditText) view.findViewById(R.id.placeofissue);


        new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
            @Override
            public void authenticatedRestClient(RestClient client) {
                if (client == null) {
                    SalesforceSDKManager.getInstance().logout(getActivity());
                    return;
                } else {
                    new AsyncToGetNewVisaRequest(client).execute(activity.getVisa().getID());
                }
            }
        });
    }


    public static Fragment newInstance(String RenewVisa) {
        InitialPage fragment = new InitialPage();
        Bundle bundle = new Bundle();
        bundle.putString("text", RenewVisa);
        fragment.setArguments(bundle);
        return fragment;
    }
/*
Calling #MobileRenewVisaWebService
HTTP POST
@param oldVisaId
Return the new linked Visa Renewed for the given old visa
 */
    public class AsyncToGetNewVisaRequest extends AsyncTask<String, Void, String> {
        private RestClient client;
        private String result;

        public AsyncToGetNewVisaRequest(RestClient client) {
            this.client = client;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utilities.showloadingDialog(activity);
        }

        @Override
        protected String doInBackground(String... strings) {
            String attUrl = client.getClientInfo().resolveUrl("/services/apexrest/MobileRenewVisaWebService").toString();

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(attUrl);
            httppost.setHeader("Authorization", "Bearer " + client.getAuthToken());
            StringEntity entity = null;
            try {
                Map<String, String> map = new HashMap<String, String>();
                map.put("oldVisaId", strings[0]);
                entity = new StringEntity(new JSONObject(map).toString(), "UTF-8");
                entity.setContentType("application/json");
                httppost.setEntity(entity);
                HttpResponse response = httpclient.execute(httppost);
                result = EntityUtils.toString(response.getEntity());
                Log.d("result", result);

                return result;
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (result != null) {

                //Getting all information for new Created visa then setting UI
                String SoqlVisaQuery = String.format(SoqlStatements.renewVisaSQL, result.replace("\"", ""));
                try {
                    restRequest = RestRequest.getRequestForQuery(getActivity().getString(R.string.api_version), SoqlVisaQuery);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                    @Override
                    public void onSuccess(RestRequest request, RestResponse response) {

                        List<Visa> visa = SFResponseManager.parseVisaData(response.toString());
                        activity.setVisa(visa.get(0));
                        activity.getVisa().setOldPass(activity.getVisa().getPassport_Number__c());
                        Name.setText(activity.getVisa().getApplicant_Full_Name__c());
                        ArabicName.setText(activity.getVisa().getApplicant_Full_Name_Arabic__c());
                        Gender.setText(activity.getVisa().getApplicant_Gender__c());
                        Dateofbirth.setText(activity.getVisa().getDate_of_Birth__c());
                        BirthCountry.setText(activity.getVisa().getCountry_of_Birth__r().getName());
                        PlaceCountry.setText(activity.getVisa().getPlace_of_Birth__c());
                        Email.setText(activity.getVisa().getApplicant_Email__c());
                        Email.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                activity.getVisa().setApplicant_Email__c(editable.toString());
                            }
                        });
                        mobile.setText(activity.getVisa().getApplicant_Mobile_Number__c());
                        mobile.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                activity.getVisa().setApplicant_Mobile_Number__c(editable.toString());
                            }
                        });


                        Maritalstatus.setAdapter(new SimpleSpinnerStringAdapter(activity, R.layout.spinner_item_wizard, 0, Maritalstatuses));
                        Maritalstatus.setSelection(getSelectedPosition(Maritalstatuses, activity.getVisa().getMarital_Status__c()));
                        Maritalstatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                activity.getVisa().setMarital_Status__c(Maritalstatuses[i]);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });





                        mothername.setText(activity.getVisa().getMother_Name__c());
                        mothername.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                activity.getVisa().setMother_Name__c(editable.toString());
                            }
                        });
                        currentnationality.setText(activity.getVisa().getCurrent_Nationality__r().getName());
                        Previousnationality.setText(activity.getVisa().getPrevious_Nationality__r().getName());
                        placeofissue.setText(activity.getVisa().getPassport_Place_of_Issue__c());
                        placeofissue.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                activity.getVisa().setPassport_Place_of_Issue__c(editable.toString());
                            }
                        });
                        religion.setAdapter(new SimpleSpinnerStringAdapter(activity, android.R.layout.simple_list_item_1, 0, Relegion));
                        religion.setSelection(getSelectedPosition(Relegion, activity.getVisa().getReligion__c()));
                        religion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                activity.getVisa().setReligion__c(Relegion[i]);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });


                        languagesspoken.setAdapter(new SimpleSpinnerStringAdapter(activity, android.R.layout.simple_list_item_1, 0, Language));
                        languagesspoken.setSelection(getSelectedPosition(Language, activity.getVisa().getLanguages__c()));
                        languagesspoken.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                activity.getVisa().setLanguages__c(Language[i]);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                        passportno.setText(activity.getVisa().getPassport_Number__c());
                        passportno.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                activity.getVisa().setPassport_Number__c(editable.toString());
                            }
                        });
                        dateofexpiry.setText(activity.getVisa().getPassport_Expiry__c());
                        dateofexpiry.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatePickerDialog dialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        String date = year + "-" + (monthOfYear + 1) + "-" +dayOfMonth ;                                        dateofexpiry.setText(date);
                                        activity.getVisa().setPassport_Expiry__c(date);
                                    }
                                }, 2016, Calendar.MONTH, Calendar.DAY_OF_MONTH);
                                dialog.show();
                            }
                        });
                        dateofexpiry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (hasFocus) {
                                    DatePickerDialog dialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                            String date = year + "-" + (monthOfYear + 1) + "-" +dayOfMonth ;
                                            dateofexpiry.setText(date);
                                            activity.getVisa().setPassport_Expiry__c(date);
                                        }
                                    },2016, Calendar.MONTH, Calendar.DAY_OF_MONTH);
                                    dialog.show();
                                }
                            }
                        });


                        String referenceSoql = "select Id,Nationality_Name__c from Country__c";
                        try {
                            restRequest = RestRequest.getRequestForQuery(getString(R.string.api_version), referenceSoql);
                            new ClientManager(getActivity(), SalesforceSDKManager.getInstance().getAccountType(), SalesforceSDKManager.getInstance().getLoginOptions(), SalesforceSDKManager.getInstance().shouldLogoutWhenTokenRevoked()).getRestClient(getActivity(), new ClientManager.RestClientCallback() {
                                @Override
                                public void authenticatedRestClient(RestClient client) {
                                    if (client == null) {
                                        System.exit(0);
                                    } else {
                                        client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {
                                            @Override
                                            public void onSuccess(RestRequest request, RestResponse response) {
                                                try {

                                                    ArrayList<Country__c> countries = SFResponseManager.parseCountryObject(response.toString());
                                                    activity.setCountries(countries);
                                                    NationalityAdapter adapter = new NationalityAdapter(activity, android.R.layout.simple_list_item_1, 0,activity.getCountries());
                                                    adapter.setDropDownViewResource(R.layout.customtext);
                                                    countryofissue.setAdapter(adapter);
                                                    countryofissue.setSelection(getCountrySelection());
                                                    countryofissue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                                        @Override
                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                            CurrentNationality nationality = new CurrentNationality();
                                                            nationality.setID(activity.getCountries().get(position).getId());
                                                            nationality.setName(activity.getCountries().get(position).getNationality_Name__c());
                                                            activity.getVisa().setPassport_Issue_Country__r(nationality);
                                                            activity.getVisa().setPassport_Issue_Country__c(activity.getCountries().get(position).getId());

                                                        }

                                                        @Override
                                                        public void onNothingSelected(AdapterView<?> parent) {

                                                        }
                                                    });



                                                    Utilities.dismissLoadingDialog();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onError(Exception exception) {

                                            }
                                        });
                                    }
                                }
                            });


                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception exception) {
                        VolleyError volleyError = (VolleyError) exception;
                        NetworkResponse response = volleyError.networkResponse;
                        String json = new String(response.data);
                        Log.d("", json);
                        Utilities.dismissLoadingDialog();
                        getActivity().finish();
                    }
                });
            }

        }
    }

    private int getCountrySelection() {
        int selected=0;
        for (int i=0;i<activity.getCountries().size();i++){
            if(activity.getCountries().get(i).getId().equals(activity.getVisa().getPassport_Issue_Country__c()))
                selected=i;
        }
        return selected;
    }

    private int getSelectedPosition(String[] relegion, String religion__c) {
        int selected=0;
        for (int i=0;i<relegion.length;i++){
            if(relegion[i].equals(religion__c))
                selected=i;
        }
        return selected;
    }
}