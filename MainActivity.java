package com.example.tomer.agnihotratiming;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    final PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
    String MyPreferences = "Myprefs";
    private int currentlocation = 0;
    private String timezoneparameters="";
    private String timezone="Asia/Kolkata";
    String tz = null;
    ProgressBar pb;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  // REMOVE BELOW FOR 4.1 above...check duality use

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        pd = new ProgressDialog(this);
        pd.setMessage(" Please wait , timings are being downloaded..");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        setContentView(R.layout.activity_main);
        Log.d("RAJA", " In Oncreate ! ");
        final SharedPreferences sharedPreferences = getSharedPreferences(MyPreferences, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        final int i1 = sharedPreferences.getInt("Location", 0);
        if (i1 == 0) {
            TextView tv1 = (TextView) findViewById(R.id.textViewLocation);
            TextView tv2 = (TextView) findViewById(R.id.textViewSunrise);
            TextView tv3 = (TextView) findViewById(R.id.textViewSunset);
            tv1.setText("Location Not set, Click on Icon !");
            tv2.setVisibility(View.INVISIBLE);
            tv3.setVisibility(View.INVISIBLE);
        }


        ImageView imageView = (ImageView) findViewById(R.id.locationimg);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            final int i1 = sharedPreferences.getInt("Location", 0);

            @Override
            public boolean onLongClick(View v) {
                if (i1 != 0) {
                    renamelocation();
                } else {
                    return true;
                }
                return false;
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  // Location Button Listener

                int i = sharedPreferences.getInt("Location", 0);
                if (i == 0) {
                   // TextView tv1 = (TextView) findViewById(R.id.textViewLocation);
                   // TextView tv2 = (TextView) findViewById(R.id.textViewSunrise);
                  //  TextView tv3 = (TextView) findViewById(R.id.textViewSunset);
                  //  tv1.setText("Location Not set, Click on Icon !");
                 //   tv2.setVisibility(View.INVISIBLE);
                  //  tv3.setVisibility(View.INVISIBLE);
                    runforlocation(0);
                } else {
                    Showlocations();
                    //  locationfill();
                }
            }
        });

        if (sharedPreferences.getInt("Run", 0) == 0) {  // First Run Preparation
            editor.putInt("Run", 1);
          //  if (editor.commit() == false) {
          //      Toast.makeText(getApplicationContext(), "Oops ! Issue making changes. ", Toast.LENGTH_SHORT).show();
          //  }
            runforlocation(1);
            editor.commit();
            // Toast.makeText(getApplicationContext(), " Initializing app for first run ... " , Toast.LENGTH_LONG).show();
        } else if (sharedPreferences.getInt("Location", 0) != 0){
            loadlocation(1);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menusettings, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.
        // SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
      /*  Boolean syncConnPref = sharedPref.getBoolean("fullscreen", true);
        Boolean timepref = sharedPref.getBoolean("hours", false);
        if (timepref == true) {
            TextClock textClock = (TextClock) findViewById(R.id.textClock2);
            textClock.setFormat12Hour("hh:mm:ss a");
        } else {
            TextClock textClock = (TextClock) findViewById(R.id.textClock2);
            textClock.setFormat24Hour("hh:mm:ss");
        }
        if (syncConnPref == true) {
//            requestWindowFeature(Window.FEATURE_NO_TITLE); // for hiding title
            //          getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            //                WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //    Toast.makeText(this, " TRUE IS THIs", Toast.LENGTH_SHORT).show();
        } else {
            //     getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //  Toast.makeText(this, " FALSE IS THIs", Toast.LENGTH_SHORT).show();
        } */

        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
        //      ActionBar actionBar = getActionBar();
//        actionBar.hide(); }
    }

    CharSequence name;
    CharSequence address;
    String attributions;
    LatLng latLng;
    String adrs;  // Making them global due to hanlding of 900 request code activity...need to run preparedb twice...

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 420) {

            try {

                final double lats = data.getDoubleExtra("Latitude", 23);
                final double longs = data.getDoubleExtra("Longitude", 78);
                Log.d(" Raju ", " The lats are " + lats + " " + longs);
                LatLngBounds latLngBounds = new LatLngBounds(new LatLng(lats, longs), new LatLng(lats + 0.1, longs + 0.1));
                builder.setLatLngBounds(latLngBounds);
                startActivityForResult(builder.build(MainActivity.this), 840);
            } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException exception) {
                Toast.makeText(getApplicationContext(), "Oops ! Issue with Google Play Services ! ", Toast.LENGTH_SHORT);
            }

        } else if (requestCode == 840) {

            if (isNetworkAvailable()) {
                Place place = PlacePicker.getPlace(this, data);
                name = place.getName();
                address = place.getAddress();
                attributions = (String) place.getAttributions();
                if (attributions == null) {
                    attributions = "";
                }
                Log.d(" Raju ", name + "  " + address + " attributions ");
                latLng = place.getLatLng();
                adrs = name + " " + address;
                if (name != "" && address != "")
                    PrepareDB(latLng.latitude, latLng.longitude, adrs, 0);
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 900) {  // from Dialog Time Zone
            tz = data.getStringExtra("tz");
            Log.d("Raju", tz);
            if (latLng != null) {
                PrepareDB(latLng.latitude, latLng.longitude, adrs, 1); //1 for 900 activity run
            } else
                Log.d("Raju", " LALONG NULL ");


        } else {
            Log.d("raju", "REquest code issue");
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void PrepareDB(Double lats, Double longs, String Address, int runcode) {
//runcode 0 for call coming from 840 activity, 1 for DialogTimezone activity run
        String MyPreferences = "Myprefs";   // to store some personalization variables - run, location, location no.

        SharedPreferences sharedPreferences = getSharedPreferences(MyPreferences, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final double lat = lats;
        final double longi = longs;
        final String Address1 = Address;

        Date todaydate = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        final String date1 = simpleDateFormat.format(todaydate);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(simpleDateFormat.parse(date1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.MONTH, 3);
        final int loc = sharedPreferences.getInt("Location", 0);
        final String date2 = simpleDateFormat.format(c.getTime());
        timezoneparameters=lats + "," + longs + "&timestamp=1331161200+key=AIzaSyD5YOvGZ8cGbP0q1OqyyBsFBm1GAyiYUPA";
     //   new QueryTimezone().execute();
        if (runcode == 0) {

            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setTitle("Please confirm timezone for this location");
            ad.setMessage(TimeZone.getDefault().getDisplayName() + "(" + TimeZone.getDefault().getID() + ")");
            ad.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tz = TimeZone.getDefault().getID();
                    String querystring = "lat_deg=" + lat + "&lon_deg=" + longi + "&timeZoneId=" + tz + "&date=" + date1 + "&end_date=" + date2;
                    Log.d("RAJU", querystring);
                    new QuerytoAPI(MainActivity.this, loc + 1, pd).execute(querystring);
                    if (tz != null) {
                        if (loc == 0) {
                            editor.putInt("Location", 1);
                            editor.putString("Location1", Address1);
                            editor.putString("Timezone1", tz);
                         //   loadlocation(1);  UNable to load as the Backgound download process not completes before coming here..
                        } else if (loc == 1) {
                            editor.putInt("Location", 2);
                            editor.putString("Location2", Address1);
                            editor.putString("Timezone2", tz);
                           // loadlocation(2);
                        } else if (loc == 2) {
                            editor.putInt("Location", 3);
                            editor.putString("Location3", Address1);
                            editor.putString("Timezone3", tz);
                           // loadlocation(3);
                        }

                        editor.commit();
                    }

                }
            });
            ad.setNegativeButton("Change timezone", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MainActivity.this, DialogTimeZone.class);
                    startActivityForResult(intent, 900);

                }
            });
            ad.create();
            ad.show();
        } else {
            String querystring = "lat_deg=" + lat + "&lon_deg=" + longi + "&timeZoneId=" + tz + "&date=" + date1 + "&end_date=" + date2;
            Log.d("RAJU", querystring);
            new QuerytoAPI(MainActivity.this, loc + 1, pd).execute(querystring);


            if (tz != null) {
                if (loc == 0) {
                    editor.putInt("Location", 1);
                    editor.putString("Location1", Address1);
                    editor.putString("Timezone1", tz);
                    // loadlocation(1);
                } else if (loc == 1) {
                    editor.putInt("Location", 2);
                    editor.putString("Location2", Address1);
                    editor.putString("Timezone2", tz);
                    //  loadlocation(2);
                } else if (loc == 2) {
                    editor.putInt("Location", 3);
                    editor.putString("Location3", Address1);
                    editor.putString("Timezone3", tz);
                    //  loadlocation(3);
                }

                editor.commit();
            }

        }

    }


    public void runforlocation(int runcode) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        final CharSequence[] items = {
                "Use Google Places", "Enter Manually", "Do it later"
        };
        alertDialog.setTitle(R.string.title_dialog);
        alertDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        try {
                            if (isNetworkAvailable())
                                startActivityForResult(builder.build(MainActivity.this), 840);
                            else
                                Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
                        } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException exception) {
                            Toast.makeText(getApplicationContext(), " Oops ! Problem with Google Play Services", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1:
                        Intent intent = new Intent(getApplicationContext(), ManualEntry.class);
                        startActivityForResult(intent, 420);
                        break;
                    default:
                        break;
                }
            }
        });
        alertDialog.create();
        alertDialog.show();
        if (runcode == 1)
            loadlocation(1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.raju1:
            case R.id.raju2:
            case R.id.raju3:
                Toast.makeText(this, " Oops ! Yet to code this ! ", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadlocation(int loc) {
        String MyPreferences = "Myprefs";   // to store some personalization variables - run, location, location no.

        SharedPreferences sharedPreferences = getSharedPreferences(MyPreferences, MODE_PRIVATE);
        currentlocation = loc;

        TextView tv1 = (TextView) findViewById(R.id.textViewLocation);
        TextView tv2 = (TextView) findViewById(R.id.textViewSunrise);
        tv2.setVisibility(View.VISIBLE);
        TextView tv3 = (TextView) findViewById(R.id.textViewSunset);
        tv3.setVisibility(View.VISIBLE);
        switch (loc) {
            case 1:
                tv1.setText(sharedPreferences.getString("Location1", " Location Not Set ") + "\n" + sharedPreferences.getString("Timezone1", " TimeZone Not Set "));
                break;
            case 2:
                tv1.setText(sharedPreferences.getString("Location2", " Location Not Set ") + "\n" + sharedPreferences.getString("Timezone2", " TimeZone Not Set "));
                break;
            case 3:
                tv1.setText(sharedPreferences.getString("Location3", " Location Not Set " + "\n" + sharedPreferences.getString("Timezone3", " TimeZone Not Set ")));
                break;
            default:
                tv1.setText(sharedPreferences.getString("Location1", " Location Not Set, Click on Icon "));
                break;
        }

        DBhelper dBhelper = new DBhelper(this, loc);
        Date todaydate = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String date1 = simpleDateFormat.format(todaydate);
        Entrydate entrydate = dBhelper.getDate(date1);
        if (entrydate != null) {
            tv2.setVisibility(View.VISIBLE);
            tv3.setVisibility(View.VISIBLE);
            tv2.setText(entrydate.getSunrise());
            tv3.setText(entrydate.getSunset());
        } else {
            //  tv2.setTextColor(Color.RED);
            // tv3.setTextColor(Color.RED);
            tv2.setText("");
            tv3.setText("");
        }

    }

    public void Showlocations() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        List<String> locationsname = new ArrayList<String>();
        String MyPreferences = "Myprefs";   // to store some personalization variables - run, location, location no.

        SharedPreferences sharedPreferences = getSharedPreferences(MyPreferences, MODE_PRIVATE);
        int i = sharedPreferences.getInt("Location", 0);
        int locationbutton = 0,deletebutton = 1 ;
        if (i == 0) {
            locationsname.add("Add Location");
            locationbutton = 0;
            deletebutton=1;
        } else if (i == 1) {
            locationsname.add((sharedPreferences.getString("Location1", " Location Not Set ")));
            locationsname.add("Add Location");
            locationsname.add("Delete all Locations");
            locationbutton = 1;
            deletebutton=2;
        } else if (i == 2) {
            locationsname.add((sharedPreferences.getString("Location1", " Location Not Set ")));
            locationsname.add((sharedPreferences.getString("Location2", " Location Not Set ")));
            locationsname.add("Add Location");
            locationsname.add("Delete all Locations");
            locationbutton = 2;
            deletebutton=3;
        } else if (i == 3) {
            locationsname.add((sharedPreferences.getString("Location1", " Location Not Set ")));
            locationsname.add((sharedPreferences.getString("Location2", " Location Not Set ")));
            locationsname.add((sharedPreferences.getString("Location3", " Location Not Set ")));
            locationsname.add("Delete all Locations");
            locationbutton = -1; // add location absent
            deletebutton=3;
        }

        final CharSequence[] items = locationsname.toArray(new CharSequence[locationsname.size()]);
        final int locationbutton1 = locationbutton, deletebutton1=deletebutton;

//        alertDialog.setTitle(R.string.title_dialog);
        alertDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("HUMA", which +  " deletebutton = " + deletebutton1 + " loca " + locationbutton1 );
                if (which != locationbutton1 && which!=deletebutton1 ) {  // if add location button is not pressed
                    switch (which) {
                        case 0:
                            loadlocation(1);
                            break;
                        case 1:
                            loadlocation(2);
                            break;
                        case 2:
                            loadlocation(3);
                            break;
                        default:
                            break;
                    }
                } else if ( which==locationbutton1){    // if add location button is pressed
                    runforlocation(0);
                }
                else if (which==deletebutton1)
                {
                    AlertDialog.Builder alertd = new AlertDialog.Builder(MainActivity.this);
                    alertd.setMessage("Are you sure you wish to delete all locations ? ");
                    alertd.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deletealllocation();
                        }
                    });
                    alertd.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertd.create().show();

                }

            }
        });
        alertDialog.create();
        alertDialog.show();
    }


    public void deletealllocation()
    {
        String MyPreferences = "Myprefs";   // to store some personalization variables - run, location, location no.

        SharedPreferences sharedPreferences = getSharedPreferences(MyPreferences, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Location", 0);
        editor.putString("Location1", "Location Not Set ! ");
        editor.putString("Location2", "Location Not Set ! ");
        editor.putString("Location3", "Location Not Set ! ");
        editor.putString("Timezone1", "TimeZone Not Set ! ");
        editor.putString("Timezone2", "TimeZone Not Set ! ");
        editor.putString("Timezone3", "TimeZone Not Set ! ");
        editor.commit();

        DBhelper dBhelper = new DBhelper(this,-1);
        dBhelper.deleteall();
        TextView tv1 = (TextView) findViewById(R.id.textViewLocation);
        TextView tv2 = (TextView) findViewById(R.id.textViewSunrise);
        TextView tv3 = (TextView) findViewById(R.id.textViewSunset);
        tv1.setText("Location Not set, Click on Icon !");
        tv2.setVisibility(View.INVISIBLE);
        tv3.setVisibility(View.INVISIBLE);
        Toast.makeText(this,"All locations deleted ! ",Toast.LENGTH_SHORT).show();
    }

    public void renamedeleteloc(int i) {  // Not using this in this version...only Rename Locaiton allowed or Delete all !

        SharedPreferences sharedPreferences = getSharedPreferences(MyPreferences, MODE_PRIVATE);
        String location = "Location" + currentlocation;
        location = sharedPreferences.getString(location, "");

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(location);
        alertDialog.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                renamelocation();
            }
        });
        alertDialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        alertDialog.create();
        alertDialog.show();

    }

    public void renamelocation() {
        SharedPreferences sharedPreferences = getSharedPreferences(MyPreferences, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        if (currentlocation != 0) {
            final String location = "Location" + currentlocation;


            final EditText inputname = new EditText(MainActivity.this);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Enter new name for location : ");
            alertDialog.setView(inputname);
            inputname.setLayoutParams(lp);
            alertDialog.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (inputname.getText().toString().length() > 1) {
                        editor.putString(location, inputname.getText().toString());
                        editor.commit();
                        TextView tv = (TextView) findViewById(R.id.textViewLocation);
                        tv.setText(inputname.getText().toString());
                        Toast.makeText(getApplicationContext(), "Done ! ", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(MainActivity.this, "Please enter at least 2 characters.", Toast.LENGTH_SHORT).show();
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  //  new QueryTimezone().execute();

                }
            });
            alertDialog.create();
            alertDialog.show();
        }
    }


    public class QueryTimezone extends AsyncTask<String, Void, String> {
// NOt using for time being...

        QueryTimezone() {

        }

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... urls) {
            //  String email = emailText.getText().toString();
            // Do some validation here

            String API_URL = "https://maps.googleapis.com/maps/api/timezone/json?location=";
            Log.d("RAJU", "ÏNTO THE DO2");
            try {
                URL url = new URL(API_URL+timezoneparameters);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                // Create the data
               String myData = urls[0];

// Enable writing
                urlConnection.setDoOutput(true);
                String jsonrecieved = "";


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
                    if (jsonrecieved == "")
                        Log.d("RAJA", "Issue with api for tz");

                    return jsonrecieved;
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {

                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if (response == null) {
                response = "THERE WAS AN ERROR";
            } else {
                String tz = response.substring(response.indexOf("Id")+7,response.indexOf(",")-1);
                timezone=tz;
                Log.d("RAJA", response + " " + tz);
            }
        }


    }
}
// Using Google Places in place of Google maps...so following code is temporarily discarded
/*        LatLng latLng = new LatLng(lats, longs);  // Storing Location detains in shared preference using geocoder
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocation(lats,longs,1);
            String msg="";
            if(addressList!=null) {
                Log.d("Raju","inside movemap3 adress" );
                String street = addressList.get(0).getAdminArea();
                String city = addressList.get(0).getLocality();
                String count = addressList.get(0).getCountryName();
                String colony = addressList.get(0).getSubLocality();
                msg = msg  + " " + city +  " " + count + " " + street + colony;
                Log.d("RAju", "ADress not null" + msg);
            }
            else{
                Log.d("Raju","adress is null");
            }


        }
        catch (Exception exception)
        {
            Log.d("raju","caught");
        } */



        ///editor.putString("City", city);
        //editor.putString("Locality", city);
        //editor.putString("Country", city);


