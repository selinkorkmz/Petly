package com.example.petly;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserListActivity extends Activity {

    private static final String TAG = "UserListActivity";
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private Button homeButton, profileButton;
    private String currentUsername;
    private ExecutorService srv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        homeButton = findViewById(R.id.button_home);
        profileButton = findViewById(R.id.button_profile);
        srv = Executors.newCachedThreadPool();

        // Get the username from the intent
        currentUsername = getIntent().getStringExtra("username");

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to HomeActivity (replace with your actual home activity class)
                Intent intent = new Intent(UserListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ProfileActivity
                Intent intent = new Intent(UserListActivity.this, UserProfileActivity.class);
                intent.putExtra("username", currentUsername);
                startActivity(intent);
            }
        });

        fetchUsers(currentUsername);
    }

    private void fetchUsers(String username) {
        new FetchUsersTask().execute(username);
    }

    private class FetchUsersTask extends AsyncTask<String, Void, String> {
        private Handler handler;

        FetchUsersTask() {
            handler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(android.os.Message msg) {
                    String response = (String) msg.obj;
                    Log.d(TAG, "Server response: " + response);
                    List<User> users = parseUsersFromResponse(response);
                    if (users != null) {
                        fetchPetInfoForUsers(users);
                    } else {
                        Toast.makeText(UserListActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
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
                Log.e(TAG, "Error in FetchUsersTask: " + e.getMessage());
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String response) {
            // Nothing to do here as the result is handled by the handler
        }
    }

    private void fetchPetInfoForUsers(List<User> users) {
        for (User user : users) {
            Handler petHandler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(android.os.Message msg) {
                    String response = (String) msg.obj;
                    Log.d(TAG, "Pet info response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject petObject = jsonResponse.getJSONObject("data");
                        Pet pet = new Pet();
                        pet.setName(petObject.getString("name"));
                        pet.setType(petObject.getString("type"));
                        pet.setAge(petObject.getString("age"));
                        pet.setGender(petObject.getString("gender"));
                        pet.setDescription(petObject.getString("description"));
                        pet.setMeettype(petObject.getString("meettype"));
                        user.setPet(pet);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        e.printStackTrace();
                    }
                    updateRecyclerView(users);
                    return true;
                }
            });
            PetlyRepository repo = new PetlyRepository();
            repo.findByPet(user.getUsername(), petHandler);
        }
    }

    private void updateRecyclerView(List<User> users) {
        if (userAdapter == null) {
            userAdapter = new UserAdapter(UserListActivity.this, users, currentUsername);
            recyclerView.setAdapter(userAdapter);
        } else {
            userAdapter.notifyDataSetChanged();
        }
    }

    private List<User> parseUsersFromResponse(String response) {
        try {
            Log.d(TAG, "Parsing user response: " + response);
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray usersArray = jsonResponse.getJSONArray("data");
            List<User> users = new ArrayList<>();

            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject userObject = usersArray.getJSONObject(i);

                User user = new User();
                user.setUsername(userObject.getString("username"));
                user.setEmail(userObject.getString("email"));
                users.add(user);
            }
            return users;
        } catch (JSONException e) {
            Log.e(TAG, "JSON parsing error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
