package com.example.tomer.agnihotratiming;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ManualEntry extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);
        Button button = (Button) findViewById(R.id.button3);
      final   TextView tv1 = (TextView) findViewById(R.id.editText);
       final TextView tv2 = (TextView) findViewById(R.id.editText2);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double lats,longs;
                Intent intent = new Intent();
                final String tvtext1= tv1.getText().toString();
                final String tvtext2= tv2.getText().toString();

                try {
                    lats = Double.parseDouble(tvtext1);
                    longs = Double.parseDouble(tvtext2);
                    intent.putExtra("Latitude",lats);
                    intent.putExtra("Longitude",longs);
                    Log.d( " Raju ", lats + " " + longs );
                }
                catch (NumberFormatException e)
                {
                    Toast.makeText(getApplicationContext(),"Invalid Latitude or Longitude ", Toast.LENGTH_SHORT);
                    Log.d( " Raju ", " FORMAT ERROR ");
                }

                setResult(420,intent);
                finish();
            }
        });

    }
}
