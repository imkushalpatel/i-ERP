package com.dexteronweb.i_erp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import helper.JSONParser;
import helper.SessionManager;
import helper.ValidationMethod;


public class Login extends ActionBarActivity {
    EditText etusername, etpassword;
    Button btnlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        etusername = (EditText) findViewById(R.id.etUsername);
        etpassword = (EditText) findViewById(R.id.etPassword);
        btnlogin = (Button) findViewById(R.id.btnLogin);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check()) {

                    new LoginCheck().execute();

                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public boolean check() {
        if ((ValidationMethod.checkEmpty(etusername))
                && (ValidationMethod.checkEmpty(etpassword)) != true) {

            Toast.makeText(getBaseContext(), "Please fill up all details",
                    Toast.LENGTH_LONG).show();
        } else {

            return true;
            /*if (ValidationMethod.checkEmail(etusername.getText().toString().trim()) != true) {
                Toast.makeText(getBaseContext(), "Invalid Email id",
                        Toast.LENGTH_LONG).show();
            } else {
                return true;
            }*/
        }


        return false;

    }

    private class LoginCheck extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress;
        JSONParser jsonParser = new JSONParser(getApplicationContext());
        JSONObject jobj;
        SessionManager session = new SessionManager(Login.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(Login.this);
            progress.setMessage("Validating User...");
            progress.setIndeterminate(false);
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progress.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> par = new ArrayList<NameValuePair>();
            par.add(new BasicNameValuePair("user", etusername.getText().toString()));
            par.add(new BasicNameValuePair("pass", ValidationMethod.md5(etpassword.getText().toString())));

            jobj = jsonParser.makeHttpRequest(getResources().getString(R.string.login_url), "POST", par);
            //Log.i("parser", jobj.toString());
            try {
                if (jobj.getBoolean("Login")) {
                    session.createLoginSession(jobj.getString("EmpId"), jobj.getString("EntityId"), jobj.getString("UserType"));
                    Intent interntDashboard = new Intent(getApplicationContext(),
                            Dashboard.class);
                    interntDashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    interntDashboard.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(interntDashboard);
                    finish();
                } else {
                    Login.this.runOnUiThread(new Runnable() {
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    Login.this);
                            builder.setTitle("Login Error.");
                            builder.setMessage("User not Found.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,
                                                                    int id) {
                                                }
                                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }
    }
}
