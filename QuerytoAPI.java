package com.example.tomer.agnihotratiming;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by tomer on 8/7/2017.
 */

public class QuerytoAPI extends AsyncTask <String, Void, String>{

    private Exception exception;
    private String jsonrecieved;   // For Json coming from API with sunset  , sunrise information
    private Context mcontext;
    private int locationnum;
    ProgressDialog progressDialog;


    QuerytoAPI(Context context, int locnum, ProgressDialog progressBar) {
    mcontext=context;
        locationnum=locnum;
        progressDialog=progressBar;
    }

    protected void onPreExecute() {
        // progressBar.setVisibility(View.VISIBLE);
        //  responseView.setText("");
      progressDialog.show();

        Log.d("RAJU", "ÏNTO THE PRE");
    }
    @Override
    protected String doInBackground(String... urls) {
        //  String email = emailText.getText().toString();
        // Do some validation here

        String API_URL="https://www.homatherapie.de/en/Agnihotra_Zeitenprogramm/results/api/v2";
        Log.d("RAJU", "ÏNTO THE DO");
        try {
            URL url = new URL(API_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            // Create the data
            String myData = urls[0];

// Enable writing
            urlConnection.setDoOutput(true);
            jsonrecieved="";

// Write the data
            urlConnection.getOutputStream().write(myData.getBytes());
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                jsonrecieved = stringBuilder.toString();
                Log.d("RAJU" , "JSON RECIEVED");
                if(jsonrecieved!="") {
                    CreateDB(jsonrecieved);
                }
                else
                {
                    Toast.makeText(mcontext,"Unable to create database ! ", Toast.LENGTH_SHORT).show();
                }

                return jsonrecieved;
            }
            finally{
                urlConnection.disconnect();
            }
        }
        catch(Exception e) {

            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    protected void onPostExecute(String response) {
        if(response == null) {
            response = "THERE WAS AN ERROR";
        }
        //  progressBar.setVisibility(View.GONE);
        Log.i("INFO", response);
        Log.d("RAJU", "ÏNTO THE POST");
        progressDialog.dismiss();
        // responseView.setText(response);
        Toast.makeText(mcontext,"Done!", Toast.LENGTH_LONG).show();

       }

    public void CreateDB(String JsonRecieved)
    {
        Date todaydate = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String todaysdate = simpleDateFormat.format(todaydate);
        DBhelper dBhelper = new DBhelper(mcontext,locationnum);
Log.d( "RAJU", "INTO CREATE DB");
        int i=JsonRecieved.indexOf(todaysdate);
        int j=0;
        do {

            j = JsonRecieved.indexOf("rise", i);
            String datestring = JsonRecieved.substring(j - 14, j - 4);
            String sunrise = JsonRecieved.substring(j + 7, j + 15);
            String sunset = JsonRecieved.substring(j + 24, j + 32);
            Entrydate entrydate = new Entrydate(datestring,sunrise,sunset);
            dBhelper.addDate(entrydate);
            Log.d("Make is", datestring + " " + sunrise + " " + sunset);

            j+=10;
            i=j;
        }while(j<jsonrecieved.length());


    }

}


