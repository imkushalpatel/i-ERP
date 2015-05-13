package com.dexteronweb.i_erp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import adapter.DayWiseListAdapter;
import data.DayWiseGroup;
import data.DayWiseList;
import data.ProjectList;
import helper.JSONParser;
import helper.SessionManager;


public class DayWise extends ActionBarActivity {
    DayWiseListAdapter listAdapter;
    ExpandableListView daywiselistview;
    ArrayList<DayWiseGroup> listDataHeader;


    AutoCompleteTextView actvproject;
    TextView txtdeadline, txtrrr, txttotal, txt1, txt2, txt3;
    SessionManager sessionManager;
    JSONParser jsonParser;
    ArrayAdapter<String> adapterproject;
    ArrayList<ProjectList> projectLists;
    List<String> listproject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_wise);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33b5e5")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);
        jsonParser = new JSONParser(getApplicationContext());
        sessionManager.checkLogin();

        daywiselistview = (ExpandableListView) findViewById(R.id.daywiselistview);
        txtdeadline = (TextView) findViewById(R.id.txtDeadline);
        txtrrr = (TextView) findViewById(R.id.txtRRR);
        txttotal = (TextView) findViewById(R.id.txtTotal);
        txt1 = (TextView) findViewById(R.id.txtTotalDisplay);
        txt2 = (TextView) findViewById(R.id.txtRRRDisplay);
        txt3 = (TextView) findViewById(R.id.txtDeadlineDisplay);
        actvproject= (AutoCompleteTextView) findViewById(R.id.actvproject1);
        new ProjectListLoad().execute();
        actvproject.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                txttotal.setText("");
                txtdeadline.setText("");
                txtrrr.setText("");
                try {
                    String projectid = projectLists.get(listproject.indexOf(actvproject.getText().toString())).getProjectId();
                    if (new FillData().execute(projectid).get()) {
                        listAdapter = new DayWiseListAdapter(DayWise.this, listDataHeader);

                        daywiselistview.setAdapter(listAdapter);
                        daywiselistview.setVisibility(View.VISIBLE);
                        txt1.setVisibility(View.VISIBLE);
                        txt2.setVisibility(View.VISIBLE);
                        txt3.setVisibility(View.VISIBLE);

                    } else {
                        Toast.makeText(DayWise.this, "No Data Available", Toast.LENGTH_LONG).show();
                        daywiselistview.setVisibility(View.INVISIBLE);
                        txt1.setVisibility(View.INVISIBLE);
                        txt2.setVisibility(View.INVISIBLE);
                        txt3.setVisibility(View.INVISIBLE);


                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_day_wise, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id==android.R.id.home)
        {
            Intent interntDashboard = new Intent(getApplicationContext(),
                    Dashboard.class);
            interntDashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            interntDashboard.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(interntDashboard);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent interntDashboard = new Intent(getApplicationContext(),
                Dashboard.class);
        interntDashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        interntDashboard.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(interntDashboard);
        finish();
    }

    class ProjectListLoad extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress;
        JSONObject object;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(DayWise.this);
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
            adapterproject = new ArrayAdapter<String>(DayWise.this, android.R.layout.simple_list_item_1, listproject);
            return null;
        }
    }
    class FillData extends AsyncTask<String,Void,Boolean>{
JSONObject object;
        @Override
        protected Boolean doInBackground(String... params) {
            listDataHeader = new ArrayList<DayWiseGroup>();


            object = jsonParser.getJSONFromUrl(getResources().getString(R.string.daywiselist_url) + '/' + params[0]);
            try {
                if (object.getBoolean("status")) {
                    JSONArray jsonArray = object.getJSONArray("DayWise");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object1 = jsonArray.getJSONObject(i);
                        JSONArray jsonArray1 = object1.getJSONArray("list");
                        ArrayList<DayWiseList> listDataChild = new ArrayList<DayWiseList>();
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject object2 = jsonArray1.getJSONObject(j);
                            listDataChild.add(new DayWiseList(object2.getString("date"), object2.getString("work")));
                        }
                        listDataHeader.add(new DayWiseGroup(object1.getString("month"), object1.getString("sum"), listDataChild));
                    }
                    ArrayList<DayWiseList> listDataChild = new ArrayList<DayWiseList>();
                    listDataChild.add(new DayWiseList("Total", object.getString("totaldone")));
                    listDataHeader.add(new DayWiseGroup("Total", object.getString("totaldone"), listDataChild));
                    txtdeadline.setText(object.getString("deadline"));
                    txtrrr.setText(object.getString("rrr"));
                    txttotal.setText(object.getString("totalreq"));
                    return true;
                } else {

                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }



            return false;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

        }
    }

}
