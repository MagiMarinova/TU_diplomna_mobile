package com.example.thesis_1;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
            //

            if(saveCredentialsButton.isChecked()){
                try {
                    saveCredentials(address , username , password);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

        });



        //Check if credentials are saved
        if(isCredentialsSaved()){
            final JSONObject credentials = readCredentials();

            //auto-fill the credentials
            try {
                usernameField.setText(credentials.get("username").toString().replace("\"" , ""));
                passwordField.setText(credentials.get("password").toString().replace("\"" , ""));
                addressField.setText(credentials.get("address").toString().replace("\"" , ""));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Method for writing credentials to a file
     *
     *
     * @param url
     * @param username
     * @param password
     */
    private void saveCredentials(String url , String username , String password) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("address" , url);
        json.put("username" , username);
        json.put("password" , password);


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
     *
     * @return {JsonObject}
     */

    private JSONObject readCredentials(){
        JSONParser parser= new JSONParser();
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
            return (JSONObject) parser.parse(String.valueOf(sb.toString().getClass()));

        } catch (IOException fileNotFound) {
            return null;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method for checking if credentials are already saved
     *
     *
     * @return {boolean}
     */

    private boolean isCredentialsSaved() {
        String path = getFilesDir().getAbsolutePath() + "/credentials.txt";
        File file = new File(path);
        return file.exists();
    }

}