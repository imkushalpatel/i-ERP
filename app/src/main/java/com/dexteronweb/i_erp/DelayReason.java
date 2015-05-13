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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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

import adapter.DelayListAdapter;
import data.DelayList;
import data.ProjectList;
import helper.JSONParser;
import helper.SessionManager;


public class DelayReason extends ActionBarActivity {
    Calendar calendar;
    ArrayList<DelayList> delayLists;
    DelayListAdapter delayListAdapter;
    Boolean proj;
    TextView txtmain;
    AutoCompleteTextView completeTextView;
    Button btnsubmit;
    JSONParser jsonParser;
    ListView listView;
    EditText etfrom, etto;
    SessionManager sessionManager;
    ArrayAdapter adaptermain;
    List<ProjectList> projectLists;
    List<String> listmain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delay_reason);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33b5e5")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtmain = (TextView) findViewById(R.id.txtmain);
        completeTextView = (AutoCompleteTextView) findViewById(R.id.actvheads);
        listView = (ListView) findViewById(R.id.listView);
        etfrom = (EditText) findViewById(R.id.etfrom);
        etto = (EditText) findViewById(R.id.etto);
        btnsubmit = (Button) findViewById(R.id.btnsubmit);
        sessionManager = new SessionManager(this);
        jsonParser = new JSONParser(getApplicationContext());
        calendar = Calendar.getInstance();
        sessionManager.checkLogin();
        Intent i = this.getIntent();
        proj = i.getExtras().getBoolean("Proj");
        etfrom.setText(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        etto.setText(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

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
                DatePickerDialog dpd = new DatePickerDialog(DelayReason.this,
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
                DatePickerDialog dpd = new DatePickerDialog(DelayReason.this,
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
       /* completeTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (new LoadList().execute().get()) {
                        listView.setVisibility(View.VISIBLE);
                        delayListAdapter = new DelayListAdapter(DelayReason.this, delayLists);
                        listView.setAdapter(delayListAdapter);
                    } else {
                        listView.setVisibility(View.INVISIBLE);
                        Toast.makeText(DelayReason.this, "No Data Available", Toast.LENGTH_LONG).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });*/
        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listmain.contains(completeTextView.getText().toString())) {
                    try {
                        if (new LoadList().execute().get()) {
                            listView.setVisibility(View.VISIBLE);
                            delayListAdapter = new DelayListAdapter(DelayReason.this, delayLists);
                            listView.setAdapter(delayListAdapter);
                        } else {
                            listView.setVisibility(View.INVISIBLE);
                            Toast.makeText(DelayReason.this, "No Data Available", Toast.LENGTH_LONG).show();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                } else {
                    if (proj) {
                        Toast.makeText(DelayReason.this, "Select Project", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(DelayReason.this, "Select Category", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_delay_reason, menu);
        return true;
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
            progress = new ProgressDialog(DelayReason.this);
            progress.setMessage("Loading Data...");
            progress.setIndeterminate(false);
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            completeTextView.setAdapter(adaptermain);
            progress.dismiss();


        }

        @Override
        protected Void doInBackground(Void... params) {
            listmain = new ArrayList<>();
            projectLists = new ArrayList<>();
            listmain.add("ALL");
            if (proj) {
                txtmain.setText("Project:");
                projectLists.add(new ProjectList("0", "ALL"));
                object = jsonParser.getJSONFromUrl(getResources().getString(R.string.projectlist_url));
                try {
                    JSONArray jsonArray = object.getJSONArray("ProjectList");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object1 = jsonArray.getJSONObject(i);
                        listmain.add(object1.getString("ProjectName"));
                        projectLists.add(new ProjectList(object1.getString("ProjectId"), object1.getString("ProjectName")));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                txtmain.setText("Category:");
                object = jsonParser.getJSONFromUrl(getResources().getString(R.string.issuelist_url));
                try {
                    JSONArray jsonArray = object.getJSONArray("IssuesList");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object1 = jsonArray.getJSONObject(i);
                        listmain.add(object1.getString("Issue"));


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            adaptermain = new ArrayAdapter<String>(DelayReason.this, android.R.layout.simple_list_item_1, listmain);

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
            delayLists = new ArrayList<DelayList>();
            List<NameValuePair> par = new ArrayList<NameValuePair>();
            par.add(new BasicNameValuePair("main", completeTextView.getText().toString()));
            par.add(new BasicNameValuePair("from", etfrom.getText().toString()));
            par.add(new BasicNameValuePair("to", etto.getText().toString()));

            if (proj) {
                try {
                    object = jsonParser.makeHttpRequest(getResources().getString(R.string.delaylist_proj_url), "POST", par);
                    Log.i("object", object.toString());
                    JSONArray jsonArray = object.getJSONArray(completeTextView.getText().toString().trim());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object1 = jsonArray.getJSONObject(i);
                        delayLists.add(new DelayList(object1.getString("cat"), object1.getString("values")));
                    }
                    delayLists.add(new DelayList("Null", object.getString("nocat")));
                    delayLists.add(new DelayList("Total", object.getString("total")));
                    return true;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    object = jsonParser.makeHttpRequest(getResources().getString(R.string.delaylist_issue_url), "POST", par);
                    Log.i("object", object.toString());
                    JSONArray jsonArray = object.getJSONArray(completeTextView.getText().toString().trim());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object1 = jsonArray.getJSONObject(i);
                        delayLists.add(new DelayList(object1.getString("proj"), object1.getString("values")));

                    }
                    delayLists.add(new DelayList("Total", object.getString("total")));
                    return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                return false;
            }
            return false;
        }
    }
}
