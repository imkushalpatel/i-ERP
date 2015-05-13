package com.dexteronweb.i_erp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import data.CategoryList;
import data.HeadsList;
import data.LocationList;
import data.ProjectList;
import helper.JSONParser;
import helper.SessionManager;
import helper.ValidationMethod;


public class Dashboard extends ActionBarActivity {
    Spinner spnhead, spncategory, spnlocation;
    AutoCompleteTextView actvproject;
    DatePicker datePicker;
    Button btnsubmit, btndayview, btndelayview, btnclear, btnheadwiew;
    EditText etwork, etcomment;
    List<String> listproject, listcategory, listlocation, listhead;
    ArrayList<ProjectList> projectLists;
    ArrayList<HeadsList> headsLists;
    ArrayList<LocationList> locationLists;
    ArrayList<CategoryList> categoryLists;
    ArrayAdapter<String> adapterproject, adaptercategory, adapterlocation, adapterhead;

    SessionManager sessionManager;
    JSONParser jsonParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33b5e5")));

        sessionManager = new SessionManager(this);
        jsonParser = new JSONParser(getApplicationContext());
        actvproject = (AutoCompleteTextView) findViewById(R.id.actvproject);
        spnhead = (Spinner) findViewById(R.id.spnhead);
        spncategory = (Spinner) findViewById(R.id.spncategory);
        spnlocation = (Spinner) findViewById(R.id.spnlocation);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        etwork = (EditText) findViewById(R.id.etWork);
        etcomment = (EditText) findViewById(R.id.etComment);
        btnsubmit = (Button) findViewById(R.id.btnsubmit);
        btndayview = (Button) findViewById(R.id.btndayview);
        btndelayview = (Button) findViewById(R.id.btndelayview);
        btnclear=(Button)findViewById(R.id.btnclear);
        btnheadwiew = (Button) findViewById(R.id.btnheadview);

        sessionManager.checkLogin();
        HashMap<String, String> user = sessionManager.getUserDetails();
        if (user.get(SessionManager.KEY_USER_TYPE).equals("0")) {
            btndelayview.setVisibility(View.GONE);
            btndayview.setVisibility(View.GONE);
            btnheadwiew.setVisibility(View.GONE);
        }
        new DateLoad().execute();
        new ProjectListLoad().execute();

        actvproject.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String projectid = projectLists.get(listproject.indexOf(actvproject.getText().toString())).getProjectId();

                Log.i("id", projectid);
                new HeadListLoad().execute(projectid);
                new CategoryListLoad().execute(projectid);
                new LocationListLoad().execute(projectid);
            }
        });
        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check())
                new InsertData().execute();
            }
        });
        btnclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvproject.setText("");
                etwork.setText("");
                etcomment.setText("");
            }
        });
        btndayview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dayview=new Intent(getApplicationContext(),DayWise.class);
                startActivity(dayview);
            }
        });
        btnheadwiew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent headview = new Intent(getApplicationContext(), HeadWise.class);
                startActivity(headview);
            }
        });


        btndelayview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);

                builder.setPositiveButton("Project Wise", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(Dashboard.this, DelayReason.class);
                        i.putExtra("Proj", true);
                        startActivity(i);
                    }
                });
                builder.setNegativeButton("Category Wise", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(Dashboard.this, DelayReason.class);
                        i.putExtra("Proj", false);
                        startActivity(i);
                    }
                });


                builder.setTitle("Select View Type");

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            SessionManager sessionManager = new SessionManager(this);
            sessionManager.logoutUser();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Are You Sure?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean check(){
        if ((ValidationMethod.checkEmpty(etwork)) != true) {
            Toast.makeText(getBaseContext(), "Please fill up Work Details",
                    Toast.LENGTH_LONG).show();


        } else if (ValidationMethod.checkEmpty(etcomment) != true) {
            Toast.makeText(getBaseContext(), "Please fill up Comment Details",
                    Toast.LENGTH_LONG).show();
        } else if (!listproject.contains(actvproject.getText().toString())) {
            Toast.makeText(getBaseContext(), "Please fill up Correct Project Name",
                    Toast.LENGTH_LONG).show();
        } else {

            return true;
        }


        return false;

    }

    class ProjectListLoad extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress;
        JSONObject object;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(Dashboard.this);
            progress.setMessage("Loading Data...");
            progress.setIndeterminate(false);
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            actvproject.setAdapter(adapterproject);
            progress.dismiss();


        }

        @Override
        protected Void doInBackground(Void... params) {
            listproject = new ArrayList<>();
            projectLists = new ArrayList<>();
            object = jsonParser.getJSONFromUrl(getResources().getString(R.string.projectlist_url));
            try {
                JSONArray jsonArray = object.getJSONArray("ProjectList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object1 = jsonArray.getJSONObject(i);
                    listproject.add(object1.getString("ProjectName"));
                    projectLists.add(new ProjectList(object1.getString("ProjectId"), object1.getString("ProjectName")));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapterproject = new ArrayAdapter<String>(Dashboard.this, android.R.layout.simple_list_item_1, listproject);
            return null;
        }
    }

    class HeadListLoad extends AsyncTask<String, Void, Void> {
        ProgressDialog progress;
        JSONObject object;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(Dashboard.this);
            progress.setMessage("Loading Data...");
            progress.setIndeterminate(false);
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            spnhead.setAdapter(adapterhead);
            progress.dismiss();

        }

        @Override
        protected Void doInBackground(String... params) {
            listhead = new ArrayList<>();
            headsLists = new ArrayList<>();
            //headsLists.add(new HeadsList("0","Select ProjectHead"));
            listhead.add("Select ProjectHead");
            object = jsonParser.getJSONFromUrl(getResources().getString(R.string.projectheadlist_url) + '/' + params[0]);

            try {
                JSONArray jsonArray = object.getJSONArray("ProjectHeadsList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object1 = jsonArray.getJSONObject(i);
                    listhead.add(object1.getString("Name"));
                    headsLists.add(new HeadsList(object1.getString("AssociateId"), object1.getString("Name")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapterhead = new ArrayAdapter<String>(Dashboard.this, android.R.layout.simple_list_item_1, listhead);
            return null;
        }
    }

    class CategoryListLoad extends AsyncTask<String, Void, Void> {
        ProgressDialog progress;
        JSONObject object;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(Dashboard.this);
            progress.setMessage("Loading Data...");
            progress.setIndeterminate(false);
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            spncategory.setAdapter(adaptercategory);
            progress.dismiss();
        }

        @Override
        protected Void doInBackground(String... params) {
            listcategory = new ArrayList<>();
            categoryLists = new ArrayList<>();
            listcategory.add("Select Category");
            object = jsonParser.getJSONFromUrl(getResources().getString(R.string.categorylist_url) + '/' + params[0]);

            try {
                JSONArray jsonArray = object.getJSONArray("CategoryList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object1 = jsonArray.getJSONObject(i);
                    listcategory.add(object1.getString("CategoryName"));
                    categoryLists.add(new CategoryList(object1.getString("CategoryId"), object1.getString("CategoryName")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adaptercategory = new ArrayAdapter<String>(Dashboard.this, android.R.layout.simple_list_item_1, listcategory);

            return null;
        }
    }

    class LocationListLoad extends AsyncTask<String, Void, Void> {
        ProgressDialog progress;
        JSONObject object;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(Dashboard.this);
            progress.setMessage("Loading Data...");
            progress.setIndeterminate(false);
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            spnlocation.setAdapter(adapterlocation);
            progress.dismiss();
        }

        @Override
        protected Void doInBackground(String... params) {
            listlocation = new ArrayList<>();
            locationLists = new ArrayList<>();
            listlocation.add("Select Location");
            object = jsonParser.getJSONFromUrl(getResources().getString(R.string.locationlist_url) + '/' + params[0]);

            try {
                JSONArray jsonArray = object.getJSONArray("LocationList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object1 = jsonArray.getJSONObject(i);
                    listlocation.add(object1.getString("LocationName"));
                    locationLists.add(new LocationList(object1.getString("LocationId"), object1.getString("LocationName")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapterlocation = new ArrayAdapter<String>(Dashboard.this, android.R.layout.simple_list_item_1, listlocation);

            return null;
        }
    }

    class DateLoad extends AsyncTask<Void, Void, Void> {

        JSONObject object;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar date = Calendar.getInstance();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            datePicker.setMaxDate(date.getTime().getTime());
            date.add(Calendar.DATE, -15);
            datePicker.setMinDate(date.getTime().getTime());

        }

        @Override
        protected Void doInBackground(Void... params) {
            object = jsonParser.getJSONFromUrl(getResources().getString(R.string.date_url));

            try {
                Log.i("Date", object.getString("Date"));
                date.setTime(df.parse(object.getString("Date")));

            } catch (ParseException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            datePicker.setMaxDate(date.getTime().getTime());
//            date.add(Calendar.DATE, -15);
//            datePicker.setMinDate(date.getTime().getTime());
            return null;
        }
    }

    class InsertData extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress;
        JSONObject object;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(Dashboard.this);
            progress.setMessage("Inserting Data...");
            progress.setIndeterminate(false);
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progress.dismiss();
            try{
                if(object.getBoolean("status")){
                    Toast.makeText(getApplicationContext(), "Data Saved Successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Data Insert Failed\nCheck Inserted Data ", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            String date = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();
            List<NameValuePair> par=new ArrayList<NameValuePair>();
            par.add(new BasicNameValuePair("proj",actvproject.getText().toString()));
            par.add(new BasicNameValuePair("user",spnhead.getSelectedItem().toString()));
            par.add(new BasicNameValuePair("cat",spncategory.getSelectedItem().toString()));
            par.add(new BasicNameValuePair("loc",spnlocation.getSelectedItem().toString()));
            par.add(new BasicNameValuePair("date",date));
            par.add(new BasicNameValuePair("work",etwork.getText().toString()));
            par.add(new BasicNameValuePair("comm",etcomment.getText().toString()));
            object=jsonParser.makeHttpRequest(getResources().getString(R.string.insertdata_url), "POST", par);

            return null;
        }
    }
}
