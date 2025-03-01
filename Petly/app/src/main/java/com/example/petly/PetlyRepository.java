package com.example.petly;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;

public class PetlyRepository {

    private static final String BASE_URL = "http://10.0.2.2:8081";


    public void loginUser(ExecutorService srv, Handler uiHandler, String requestBody) {
        postRequest(srv, uiHandler, "/petly/user/auth/login", requestBody);
    }


    public void registerUser(ExecutorService srv, Handler uiHandler, String requestBody) {
        postRequest(srv, uiHandler, "/petly/user/auth/register", requestBody);
    }
    public void createPet(ExecutorService srv, Handler uiHandler, String requestBody) {
        postRequest(srv, uiHandler, "/petly/pets/createpet", requestBody);
    }
    public void savePetToUser(ExecutorService srv, Handler uiHandler, String requestBody) {
        postRequest(srv, uiHandler, "/petly/user/savepettouser", requestBody);
    }


    public void updateMeetType(ExecutorService srv, Handler uiHandler, String requestBody) {
        postRequest(srv, uiHandler, "/petly/pets/updateMeetType", requestBody);
    }



    public void checkMatches(ExecutorService srv, Handler uiHandler, String requestBody) {
        srv.execute(() -> {
            try {
                URL url = new URL(BASE_URL + "/petly/user/checkmatches");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json"); // Set Content-Type to application/json

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(requestBody);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                conn.disconnect();

                Message msg = new Message();
                msg.obj = buffer.toString();
                uiHandler.sendMessage(msg);

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }




    public String viewPossibleMatches(String username, Handler uiHandler) {
        String query = "?username=" + username;
        getRequest("/petly/user/viewpossiblematches" + query, uiHandler);
        return query;
    }
    public String findByPet(String username, Handler uiHandler) {
        String query = "?username=" + username;
        getRequest("/petly/pets/findByPetId" + query, uiHandler);
        return query;
    }

    private void getRequest(String endpoint, Handler uiHandler) {
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + endpoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                conn.disconnect();

                Message msg = new Message();
                msg.obj = buffer.toString();
                uiHandler.sendMessage(msg);

            } catch (Exception e) {
                Log.e("DEV", "Error in getRequest: " + e.getMessage());
                Message msg = new Message();
                msg.obj = "Error: " + e.getMessage();
                uiHandler.sendMessage(msg);
            }
        }).start();
    }

    private void postRequest(ExecutorService srv, Handler uiHandler, String endpoint, String requestBody) {
        srv.execute(() -> {
            try {
                URL url = new URL(BASE_URL + endpoint);
                Log.i("TAG", String.valueOf(url));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json"); // Set Content-Type to application/json


                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(requestBody);
                writer.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                conn.disconnect();

                Message msg = new Message();
                msg.obj = buffer.toString();
                uiHandler.sendMessage(msg);

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
