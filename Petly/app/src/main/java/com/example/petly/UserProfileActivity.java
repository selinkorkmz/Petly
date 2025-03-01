package com.example.petly;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

public class UserProfileActivity extends Activity {

    private static final String TAG = "UserProfileActivity";

    private TextView userUsername;
    private TextView userlikecount;
    private TextView pet_name;
    private ImageView petImage;
    private TextView petAgeGender;
    private TextView petBreedType;
    private TextView petDescription;
    private TextView petHobbies;
    private TextView petMeettype;
    private LinearLayout petPhotosContainer;
    private Button backButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userUsername = findViewById(R.id.user_username);
        userlikecount = findViewById(R.id.user_like_count);
        pet_name = findViewById(R.id.pet_name);
        petImage = findViewById(R.id.pet_image);
        petAgeGender = findViewById(R.id.pet_age_gender);
        petBreedType = findViewById(R.id.pet_breed_type);
        petDescription = findViewById(R.id.pet_description);
        petMeettype = findViewById(R.id.pet_meettype);
        backButton = findViewById(R.id.button_back);

        backButton.setOnClickListener(v -> finish());
        // Get the username from the intent
        String username = getIntent().getStringExtra("username");
        fetchUserAndPetInfo(username);

    }

    private void fetchUserAndPetInfo(String username) {
        new FetchUserAndPetInfoTask().execute(username);
    }

    private class FetchUserAndPetInfoTask extends AsyncTask<String, Void, String> {
        private Handler handler;

        FetchUserAndPetInfoTask() {
            handler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(android.os.Message msg) {
                    String response = (String) msg.obj;
                    Log.d(TAG, "Server response: " + response);
                    if (response.contains("Error")) {
                        Toast.makeText(UserProfileActivity.this, response, Toast.LENGTH_SHORT).show();
                    } else {
                        displayUserInfo(response);
                    }
                    return true;
                }
            });
        }

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            try {
                PetlyRepository repo = new PetlyRepository();
                repo.viewPossibleMatches(username, handler);
                return null; // The result will be handled by the handler
            } catch (Exception e) {
                Log.e(TAG, "Error in FetchUserAndPetInfoTask: " + e.getMessage());
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String response) {
            // Nothing to do here as the result is handled by the handler
        }
    }

    private void displayUserInfo(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray usersArray = jsonResponse.getJSONArray("data");

            String currentUsername = getIntent().getStringExtra("username");

            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject userObject = usersArray.getJSONObject(i);
                if (userObject.getString("username").equals(currentUsername)) {
                    userUsername.setText(userObject.getString("username"));
                    userlikecount.setText("Likes: " + userObject.getInt("likeCount"));

                    // Fetch and display pet details
                    new FetchPetDetailsTask().execute(userObject.getString("username"));
                    break;
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON parsing error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(UserProfileActivity.this, "Failed to load user info", Toast.LENGTH_SHORT).show();
        }
    }

    private class FetchPetDetailsTask extends AsyncTask<String, Void, String> {
        private Handler handler;

        FetchPetDetailsTask() {
            handler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(android.os.Message msg) {
                    String response = (String) msg.obj;
                    Log.d(TAG, "Pet details response: " + response);
                    displayPetInfo(response);
                    return true;
                }
            });
        }

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            try {
                PetlyRepository repo = new PetlyRepository();
                repo.findByPet(username, handler);
                return null; // The result will be handled by the handler
            } catch (Exception e) {
                Log.e(TAG, "Error in FetchPetDetailsTask: " + e.getMessage());
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String response) {
            // Nothing to do here as the result is handled by the handler
        }
    }

    private void displayPetInfo(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject petObject = jsonResponse.getJSONObject("data");

            String name = petObject.getString("name");
            String type = petObject.getString("type");
            String breed = petObject.getString("breed");
            String age = petObject.getString("age");
            String gender = petObject.getString("gender");
            String description = petObject.getString("description");
            String meettype = petObject.getString("meettype");

            pet_name.setText("Pet name is: " + name);
            petAgeGender.setText(age + " years old, " + gender);
            petBreedType.setText( "I am a: " + type );
            petDescription.setText("Description: " + description);
            petMeettype.setText("Meet Type: " + meettype);



        } catch (JSONException e) {
            Log.e(TAG, "JSON parsing error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(UserProfileActivity.this, "Failed to load pet info", Toast.LENGTH_SHORT).show();
        }
    }
}
