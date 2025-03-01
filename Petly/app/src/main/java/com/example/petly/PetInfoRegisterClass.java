package com.example.petly;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.view.View;

import com.example.petly.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PetInfoRegisterClass extends Activity {

    private static final int PICK_IMAGE = 100;
    private EditText petNameEditText, petTypeEditText, ageEditText, hobbiesEditText, descriptionEditText;
    private RadioGroup genderRadioGroup;
    private Uri imageUri;
    private ExecutorService srv;
    private Handler handler;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_info_register);

        petNameEditText = findViewById(R.id.pet_name_edit_text);
        petTypeEditText = findViewById(R.id.pet_type_edit_text);
        genderRadioGroup = findViewById(R.id.gender_radio_group);
        ageEditText = findViewById(R.id.age_edit_text);
        hobbiesEditText = findViewById(R.id.hobbies_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        Button uploadImageButton = findViewById(R.id.upload_image_button);
        Button submitButton = findViewById(R.id.submit_button);
        username = getIntent().getStringExtra("username");

        srv = Executors.newCachedThreadPool();
        handler = new Handler(msg -> {
            String response = (String) msg.obj;
            Toast.makeText(PetInfoRegisterClass.this, response, Toast.LENGTH_SHORT).show();
            Log.i("DEV", response);

            if (response.contains("Pet created")) {
                // Pet creation successful, now save pet to user
                savePetToUser();
            } else if (response.contains("Pet connected to its owner")) {
                // Save pet to user successful, navigate to next activity
                Intent intent = new Intent(PetInfoRegisterClass.this, SeekOptionsActivityClass.class);
                intent.putExtra("petName", petNameEditText.getText().toString());
                intent.putExtra("username", username);
                startActivity(intent);
            }

            return true;
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPet();
            }
        });
    }
    String petName;
    private void createPet() {
        // Collect pet information
        petName = petNameEditText.getText().toString();
        String petType = petTypeEditText.getText().toString();
        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
        String gender = selectedGenderId == R.id.male_radio_button ? "Male" : "Female";
        String age = ageEditText.getText().toString();
        String hobbies = hobbiesEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        List<String> imageUrls = new ArrayList<>();
        if (imageUri != null) {
            imageUrls.add(imageUri.toString());
        }

        // Create JSON object for the pet
        JSONObject petJson = new JSONObject();
        try {
            petJson.put("name", petName);
            petJson.put("type", petType);
            petJson.put("breed", "Unknown");
            petJson.put("age", age);
            petJson.put("gender", gender);
            petJson.put("description", description);
            petJson.put("meettype", "Unknown");
            petJson.put("imageUrls", new JSONArray(imageUrls));
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(PetInfoRegisterClass.this, "Error creating JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create pet
        PetlyRepository repo = new PetlyRepository();
        repo.createPet(srv, handler, petJson.toString());
    }

    private void savePetToUser() {
        JSONObject savePetToUserJson = new JSONObject();
        try {

            savePetToUserJson.put("username", username);
            savePetToUserJson.put("petname", petName);

            PetlyRepository repo = new PetlyRepository();
            repo.savePetToUser(srv, handler, savePetToUserJson.toString());

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(PetInfoRegisterClass.this, "Error creating save pet JSON", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            Toast.makeText(this, "Image selected: " + imageUri.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
