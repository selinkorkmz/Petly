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

public class RegisterClass extends Activity {

    private EditText usernameEditText, emailEditText, passwordEditText, repeatPasswordEditText;
    private Button signUpButton, backButton;
    private CheckBox rememberMeCheckBox;
    private ExecutorService srv;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.username_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        repeatPasswordEditText = findViewById(R.id.repeat_password_edit_text);
        signUpButton = findViewById(R.id.sign_up_button);
        backButton = findViewById(R.id.back_button);
        rememberMeCheckBox = findViewById(R.id.remember_me_checkbox);

        srv = Executors.newCachedThreadPool();
        handler = new Handler(msg -> {
            String response = (String) msg.obj;
            // Process the response and update the UI accordingly
            if (response.contains("OK")) {
                Intent intent = new Intent(RegisterClass.this, PetInfoRegisterClass.class);
                intent.putExtra("username", usernameEditText.getText().toString());
                startActivity(intent);
                Toast.makeText(RegisterClass.this, "Registration successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RegisterClass.this, "Registration failed: " + response, Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle sign-up logic here
                String username = usernameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String repeatPassword = repeatPasswordEditText.getText().toString();

                if (!password.equals(repeatPassword)) {
                    Toast.makeText(RegisterClass.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                PetlyRepository repo = new PetlyRepository();
                String requestBody = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\",\"email\":\"" + email + "\",\"phoneNumber\":\"123456789\"}";
                repo.registerUser(srv, handler, requestBody);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button logic here
                Toast.makeText(RegisterClass.this, "Back button clicked", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            }
        });
    }
}
