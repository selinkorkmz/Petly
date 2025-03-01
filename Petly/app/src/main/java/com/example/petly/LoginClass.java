package com.example.petly;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginClass extends Activity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton, backButton;
    private CheckBox rememberMeCheckBox;
    private ExecutorService srv;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        backButton = findViewById(R.id.back_button);
        rememberMeCheckBox = findViewById(R.id.remember_me_checkbox);

        srv = Executors.newCachedThreadPool();
        handler = new Handler(msg -> {
            String response = (String) msg.obj;
            // Process the response and update the UI accordingly
            if (response.contains("OK")) {
                Intent intent = new Intent(LoginClass.this, UserListActivity.class);
                intent.putExtra("username", emailEditText.getText().toString());
                startActivity(intent);startActivity(intent);
                Toast.makeText(LoginClass.this, "Login successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginClass.this, "Login failed: " + response, Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle login logic here
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Perform the login API call
                PetlyRepository repo = new PetlyRepository();
                String requestBody = "{\"username\":\"" + email + "\",\"password\":\"" + password + "\"}";
                repo.loginUser(srv, handler, requestBody);

                // Display toast and start the next activity for demo purposes
                 //Intent intent = new Intent(LoginClass.this, SeekOptionsActivityClass.class);
                 //startActivity(intent);
                 //Toast.makeText(LoginClass.this, "Login button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button logic here
                Toast.makeText(LoginClass.this, "Back button clicked", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            }
        });
    }
}
