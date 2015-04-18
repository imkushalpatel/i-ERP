package com.dexteronweb.i_erp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import adapter.HeadWiseListAdapter;
import data.HeadWiseList;
import data.HeadsList;
import helper.JSONParser;
import helper.SessionManager;


public class HeadWise extends ActionBarActivity {

    AutoCompleteTextView completeTextView;
    JSONParser jsonParser;
    ListView listView;
    EditText etfrom, etto;
    SessionManager sessionManager;
    ArrayAdapter adapterheads;
    HeadWiseListAdapter headWiseListAdapter;
    ArrayList<HeadWiseList> headWiseLists;
    List<String> list;
    List<HeadsList> headsLists;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_wise);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33b5e5")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        completeTextView = (AutoCompleteTextView) findViewById(R.id.actvheads);
        etfrom = (EditText) findViewById(R.id.etfrom);
        etto = (EditText) findViewById(R.id.etto);
        listView = (ListView) findViewById(R.id.listViewHead);

        sessionManager = new SessionManager(this);
        jsonParser = new JSONParser(getApplicationContext());
        calendar = Calendar.getInstance();
        sessionManager.checkLogin();
        new LoadViewData().execute();

        etfrom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
        });


        etto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
        });


        etfrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(HeadWise.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                etfrom.setText(year + "-"
                                        + (monthOfYear + 1) + "-" + dayOfMonth);

                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });


        etto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(HeadWise.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                etto.setText(year + "-"
                                        + (monthOfYear + 1) + "-" + dayOfMonth);

                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });
        completeTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (new LoadList().execute().get()) {
                        listView.setVisibility(View.VISIBLE);
                        headWiseListAdapter = new HeadWiseListAdapter(HeadWise.this, headWiseLists);
                        listView.setAdapter(headWiseListAdapter);
                    } else {
                        listView.setVisibility(View.INVISIBLE);
                        Toast.makeText(HeadWise.this, "No Data Available", Toast.LENGTH_LONG).show();
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
        getMenuInflater().inflate(R.menu.menu_head_wise, menu);
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
        if (id == android.R.id.home) {
            Intent interntDashboard = new Intent(getApplicationContext(),
                    Dashboard.class);
            interntDashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            interntDashboard.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(interntDashboard);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    class LoadViewData extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress;
        JSONObject object;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(HeadWise.this);
            progress.setMessage("Loading Data...");
            progress.setIndeterminate(false);
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            completeTextView.setAdapter(adapterheads);
            progress.dismiss();


        }

        @Override
        protected Void doInBackground(Void... params) {
            list = new ArrayList<>();
            headsLists = new ArrayList<>();

            object = jsonParser.getJSONFromUrl(getResources().getString(R.string.headwiselist_heads_url));
            try {
                JSONArray jsonArray = object.getJSONArray("HeadList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object1 = jsonArray.getJSONObject(i);
                    list.add(object1.getString("Name"));
                    headsLists.add(new HeadsList(object1.getString("AssociateId"), object1.getString("Name")));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapterheads = new ArrayAdapter<String>(HeadWise.this, android.R.layout.simple_list_item_1, list);

            return null;
        }
    }

    class LoadList extends AsyncTask<Void, Void, Boolean> {
        JSONObject object;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            headWiseLists = new ArrayList<>();
            List<NameValuePair> par = new ArrayList<NameValuePair>();
            String id = headsLists.get(list.indexOf(completeTextView.getText().toString())).getHeadId();
            par.add(new BasicNameValuePair("id", id));
            par.add(new BasicNameValuePair("from", etfrom.getText().toString()));
            par.add(new BasicNameValuePair("to", etto.getText().toString()));


            try {
                object = jsonParser.makeHttpRequest(getResources().getString(R.string.headwiselist_list_url), "POST", par);
                //Log.i("object", object.toString());
                headWiseLists.add(new HeadWiseList("Project Name", "Work Done", "Value"));

                if (object.getBoolean("status")) {
                    JSONArray jsonArray = object.getJSONArray("headwise");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object1 = jsonArray.getJSONObject(i);
                        headWiseLists.add(new HeadWiseList(object1.getString("proj"), object1.getString("workdone"), object1.getString("value")));
                    }
                    headWiseLists.add(new HeadWiseList("Total", object.getString("totalwork"), object.getString("totalvalue")));
                    return true;
                } else return false;

            } catch (JSONException e) {
                //return false;
                e.printStackTrace();
            }

            return false;
        }
    }

}
