package com.example.petly;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SeekOptionsActivityClass extends Activity {

    private LinearLayout rehomeOption, datesOption, connectOption;
    private String petName;
    private ExecutorService srv;
    private Handler handler;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seek_options);

        rehomeOption = findViewById(R.id.rehome_option);
        datesOption = findViewById(R.id.dates_option);
        connectOption = findViewById(R.id.connect_option);

        // Retrieve the username and petName from the Intent
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        petName = intent.getStringExtra("petName");

        srv = Executors.newCachedThreadPool();
        handler = new Handler(msg -> {
            String response = (String) msg.obj;
            Toast.makeText(SeekOptionsActivityClass.this, response, Toast.LENGTH_SHORT).show();
            return true;
        });

        rehomeOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMeetTypeAndViewPossibleMatches("Rehome");
            }
        });

        datesOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMeetTypeAndViewPossibleMatches("Dates");
            }
        });

        connectOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMeetTypeAndViewPossibleMatches("Connect");
            }
        });
    }

    private void updateMeetTypeAndViewPossibleMatches(String meetType) {
        // Update meet type
        JSONObject updateMeetTypeRequest = new JSONObject();
        try {
            updateMeetTypeRequest.put("meettype", meetType);
            updateMeetTypeRequest.put("petname", petName);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        PetlyRepository repo = new PetlyRepository();
        repo.updateMeetType(srv, handler, updateMeetTypeRequest.toString());


        // Start UserListActivity
        Intent intent = new Intent(SeekOptionsActivityClass.this, UserListActivity.class);
        intent.putExtra("meetType", meetType);
        intent.putExtra("username", username);
        Log.i("vardediler", username);
        startActivity(intent);
    }
}
