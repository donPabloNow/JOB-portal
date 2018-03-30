package brdevelopers.com.jobvibe;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by End User on 27-03-2018.
 */

public class Profile_personal extends Fragment implements View.OnClickListener, View.OnFocusChangeListener,AdapterView.OnItemSelectedListener,TextWatcher{

    private EditText et_email, et_dob,et_mobile,et_name,et_address,et_pincode,et_city;
    RadioButton radio_male,radio_female;
    private ImageView iv_dob;
    private TextView tv_btnnext,tv_age;
    private Spinner degree,fos;

    private String getdegree,getfos,password;

    String degreeUrl="http://103.230.103.142/jobportalapp/job.asmx/GetCourse";
    String fosUrl="http://103.230.103.142/jobportalapp/job.asmx/GetBranch";
    String saveCanditate="http://103.230.103.142/jobportalapp/job.asmx/SaveCandidate";

    final Map<String,String> clist=new LinkedHashMap<>();
    final Map<String,String> fosmap=new LinkedHashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.profile_personal,container,false);

        //Binding view to references
        et_email= view.findViewById(R.id.ET_email);
        et_name= view.findViewById(R.id.ET_name);
        et_mobile=view.findViewById(R.id.ET_mobie);
        et_address=view.findViewById(R.id.ET_address);
        et_pincode= view.findViewById(R.id.ET_pincode);
        et_city=view.findViewById(R.id.ET_currentcity);
        et_dob=view.findViewById(R.id.ET_dob);
        radio_male=view.findViewById(R.id.Radio_male);
        radio_female=view.findViewById(R.id.Radio_female);
        degree=view.findViewById(R.id.Spinner_degree);
        fos=view.findViewById(R.id.Spinner_fos);
        iv_dob=view.findViewById(R.id.IV_dob);
        tv_btnnext=view.findViewById(R.id.TV_btnnext);
        tv_age=view.findViewById(R.id.TV_age);


        //Receiving email and password from Profile activity
        Bundle bundle=getArguments();
        final String email=bundle.getString("email");
        password=bundle.getString("password");
        et_email.setText(email); //Setting email id to email EditText


        tv_btnnext.setOnClickListener(this);    //button next
        et_dob.setOnClickListener(this);        //Edit Text dob click
        iv_dob.setOnClickListener(this);        //Image View dob click
        et_dob.setOnFocusChangeListener(this);  //Edit Text dob on focus
        et_mobile.addTextChangedListener(this);  //Edit Text mobile on textchange
       //Fetching data for degree from web service
        getDegree();

        //On degree Spinner click
        degree.setOnItemSelectedListener(this);

        //On fos spinner click
        fos.setOnItemSelectedListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.TV_btnnext)
        {
            tv_btnnext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String email=et_email.getText().toString();
                    String name=et_name.getText().toString();
                    String mobile=et_mobile.getText().toString();
                    String address=et_address.getText().toString();
                    String pincode=et_pincode.getText().toString();
                    String city=et_city.getText().toString();
                    String dob=et_dob.getText().toString();
                    String gender=null;
                    boolean bol=radio_male.isChecked();
                    if(bol)
                        gender="Male";
                    else
                        gender="Female";


                    personalDetailsEntry(email,name,mobile,address,pincode,city,dob,gender);

                }


            });
        }
        else if(v.getId()==R.id.ET_dob)
        {
            //Calling dateOfBirth on click on edittext
            dateOfBirth();
        }
        else if(v.getId()==R.id.IV_dob)
        {
            //Calling dateOfBirth on click on calendar ImageView
            dateOfBirth();
        }

    }

    private void personalDetailsEntry(final String email, final String name, final String mobile, final String address, final String pincode, final String city, final String dob, final String gender) {

        StringRequest stringRequest=new StringRequest(Request.Method.POST, saveCanditate, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("checklog",response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("checklog"," "+error);
                Toast.makeText(getActivity(), ""+error, Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String,String> hashMap=new HashMap<>();
                hashMap.put("email",email);
                hashMap.put("name",name);
                hashMap.put("pwd",password);
                hashMap.put("currentcity",city);
                hashMap.put("address",address);
                hashMap.put("pincode",pincode);
                hashMap.put("gender",gender);
                hashMap.put("dob",dob);
                hashMap.put("course",getdegree);
                hashMap.put("branch",getfos);
                hashMap.put("poy"," ");
                hashMap.put("percentage"," ");
                hashMap.put("il"," ");
                hashMap.put("iname"," ");
                hashMap.put("uname"," ");

                return hashMap;
            }
        };
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private void dateOfBirth() {

        Calendar c=Calendar.getInstance();
        final int dd,mm,yy;
        dd=c.get(Calendar.DAY_OF_MONTH);
        mm=c.get(Calendar.MONTH);
        yy=c.get(Calendar.YEAR);

        DatePickerDialog dpd=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                et_dob.setText(dayOfMonth+"/"+month+"/"+year);
                calculateAge(dd,mm,yy,dayOfMonth,month,year); //calling calculateage to show it on textview age
            }
        },dd,mm,yy);

        dpd.updateDate(1980,0,1);
        dpd.show();
    }

    //On focus on edittext
    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if(v.getId()==R.id.ET_dob && hasFocus)
        {
            dateOfBirth();
        }

    }

// calculating age from datetimepicker and show it on textview age

    public void calculateAge(int cdd,int cmm,int cyy,int bdd,int bmm,int byy)
    {
        cmm++;
        bmm++;
        if(cdd<bdd)
            cmm=cmm-1;
        if(cmm<bmm)
            cyy=cyy-1;

        int age=cyy-byy;

        tv_age.setText("Age : "+age);
        tv_age.setVisibility(View.VISIBLE);

    }

    //Getting degree data from webservices
    private void getDegree() {

        StringRequest stringRequest=new StringRequest(Request.Method.GET, degreeUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("courseList");
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject course=jsonArray.getJSONObject(i);
                        String courseid=course.getString("cid");
                        String coursename=course.getString("cname");
                        clist.put(courseid,coursename);
                    }

                    //Converting hashmap to string array
                    String[] clistString = clist.values().toArray(new String[0]);
                    ArrayAdapter<String> arrayAdapterCourse=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,clistString);
                    degree.setAdapter(arrayAdapterCourse);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("LogCheck",error.toString());
                Toast.makeText(getActivity(), ""+error, Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    //On Degree Spinner Item selected
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(parent.getId()==R.id.Spinner_degree) {
            String hashvalue = parent.getItemAtPosition(position).toString(); //Getting value of the spinner item by position
            String hashkey = null;
            getdegree=hashvalue;
            for (Object o : clist.keySet()) {
                if (clist.get(o).equals(hashvalue)) {
                    hashkey = o.toString();   //Getting the haskey from the hashvalue
                    break;
                }
            }

            //Calling fieldOfStudey and passing hashkey of degreeId
            fieldOfStudy(hashkey);
        }
        else if(parent.getId()==R.id.Spinner_fos)
        {
            getfos=parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //Retrieving data from webservice according the degreeid
    private void fieldOfStudy(final String hashkey) {

        StringRequest stringRequest=new StringRequest(Request.Method.POST, fosUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    fosmap.clear();
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("branchList");
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject foslist=jsonArray.getJSONObject(i);
                        String fosid=foslist.getString("bid");
                        String fosname=foslist.getString("bname");

                        fosmap.put(fosid,fosname);

                    }

                    String[] fosmapString = fosmap.values().toArray(new String[0]);
                    ArrayAdapter<String> arrayAdapterCourse=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,fosmapString);
                    fos.setAdapter(arrayAdapterCourse);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("LogCheck",""+error);
                Toast.makeText(getActivity(), ""+error, Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String,String> foshash=new HashMap<>();
                foshash.put("courseid",hashkey);
                return foshash;
            }
        };
        Volley.newRequestQueue(getActivity()).add(stringRequest);


    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        String phoneno=et_mobile.getText().toString();

       if(phoneno.length()==1) {
            s.replace(0, phoneno.length(),"+91"+phoneno);
        }
        else if(phoneno.length()==3)
       {
           s.replace(0,phoneno.length(),"");
       }

    }


}