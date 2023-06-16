package com.example.thesis_1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.koushikdutta.ion.Ion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get references of UI elements
        final Button loginButton = findViewById(R.id.loginButton);
        final Switch saveCredentialsButton = findViewById(R.id.saveCredentials);
        final EditText usernameField = findViewById(R.id.usernameField);
        final EditText passwordField = findViewById(R.id.passwordField);
        final EditText addressField= findViewById(R.id.addressField);

        loginButton.setOnClickListener(v -> {

            // Get element value
            final String username = usernameField.getText().toString();
            final String password = passwordField.getText().toString();
            final String address = addressField.getText().toString();


            if(saveCredentialsButton.isChecked()){
                try {
                    saveCredentials(address , username , password);
                } catch (JsonIOException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                loginRequest("http://" + address + ":3000/androidLogin", username, password, () -> {

                    //Pass the address value to the next activity
                    Intent intent = new Intent(MainActivity.this , VideoStream.class);
                    intent.putExtra("ADDRESS" , address);
                    startActivity(intent);
                });
            } catch (JsonIOException e) {
                throw new RuntimeException(e);
            }

        });



        //Check if credentials are saved
        if(isCredentialsSaved()){
            final JsonObject credentials = readCredentials();

            //auto-fill the credentials
            try {
                usernameField.setText(credentials.get("username").toString().replace("\"" , ""));
                passwordField.setText(credentials.get("password").toString().replace("\"" , ""));
                addressField.setText(credentials.get("address").toString().replace("\"" , ""));
            } catch (JsonIOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Method for making a request to the given server
     * The server responds with a boolean according to the given credentials
     *
     * @param url - address
     * @param username - username
     * @param password - password
     * @param onSuccess - callback for when the server responds with success
     */

    private void loginRequest(String url , String username , String password , final Runnable onSuccess) {
        //Buffer JSON object to be send
        JsonObject json = new JsonObject();
        json.addProperty("username" , username);
        json.addProperty("password" , password);

        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        Ion.with(this).load(url).setJsonObjectBody(json)
                .asJsonObject()
                .setCallback((e, result) -> {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    if(e !=null){
                        new AlertDialog.Builder(MainActivity.this).setTitle("Network error!")
                                .setMessage("Server is unreachable!").setPositiveButton(
                                        "OK",
                                        (dialog, id) -> dialog.cancel()).show();
                        return;
                    }
                    if(result.get("isSuccessful").getAsBoolean()){
                        onSuccess.run();
                    }else{
                        new AlertDialog.Builder(MainActivity.this).setTitle("Wrong credentials!")
                                .setMessage("Please check you username and password!").setPositiveButton(
                                        "OK",
                                        (dialog, id) -> dialog.cancel()).show();
                    }
                });
    }

    /**
     * Method for writing credentials to a file
     *
     * @param url - video stream address
     * @param username - username
     * @param password - password
     */
    private void saveCredentials(String url , String username , String password) throws JsonIOException {
        JsonObject json = new JsonObject();
        json.addProperty("address" , url);
        json.addProperty("username" , username);
        json.addProperty("password" , password);


        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("credentials.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(json.toString());
            outputStreamWriter.close();
        }
        catch (IOException e) {
            System.err.println("File write failed: " + e);
        }
    }

    /**
     * Method for reading the saved credentials from a file
     *
     * @return {JsonObject}
     */

    private JsonObject readCredentials(){
        JsonParser parser= new JsonParser();
        try {
            FileInputStream fis = openFileInput("credentials.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            //Return a JSON object
            return (JsonObject) parser.parse(String.valueOf(sb.toString().getClass()));

        } catch (IOException fileNotFound) {
            return null;
        }
    }

    /**
     * Method for checking if credentials are already saved
     *
     * @return {boolean}
     */

    private boolean isCredentialsSaved() {
        String path = getFilesDir().getAbsolutePath() + "/credentials.txt";
        File file = new File(path);
        return file.exists();
    }

}