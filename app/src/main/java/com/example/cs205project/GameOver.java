package com.example.cs205project;



import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.widget.Toast;

public class GameOver extends MainActivity {

    TextView tvPoints;
    Boolean submitted;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);
        int points = getIntent().getExtras().getInt("points");
        tvPoints = findViewById(R.id.tvPoints);
        tvPoints.setText("" + points);
        submitted = false;
    }

    public void restart(View view) {
        Intent intent = new Intent(GameOver.this, StartGame.class);
        finish();
        startActivity(intent);

    }
    public void submit(View view){
        if(!submitted) {
            EditText nameEditText = findViewById(R.id.nameEditText);
            String name = nameEditText.getText().toString();
            int points = getIntent().getExtras().getInt("points");
            //        Log.d("MainActivity",name);
            //        Log.d("MainActivity", String.valueOf(points));
            if(!isNameEmpty(name)){
                SendScoreTask task = new SendScoreTask();
                task.execute(name, String.valueOf(points));
                Intent intent = new Intent(GameOver.this, HighScoreActivity.class);
                startActivity(intent);
                submitted = true;
            }
        } else {
            Toast.makeText(GameOver.this, "Score already submitted!", Toast.LENGTH_SHORT).show();
        }
    }

    private class SendScoreTask extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... params) {
            try {
                // Create a URL object with the endpoint URL
                URL url = new URL("http://3.15.12.223:31000/scores");

                // Create a HttpURLConnection object and set the request method to POST
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");

                // Set the request headers
                connection.setRequestProperty("Content-Type", "application/json");

                // Create a JSON object with the data to send
                JSONObject postData = new JSONObject();
                postData.put("name", params[0]);
                postData.put("score", Integer.parseInt(params[1]));

                // Convert the JSON object to a byte array
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                // Set the content length of the request body
                connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

                // Enable output and write the request body to the connection
                connection.setDoOutput(true);
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(postDataBytes);
                outputStream.flush();
                outputStream.close();

                // Read the response from the server
                int statusCode = connection.getResponseCode();
                Log.d("MainActivity","Status Code is "+ statusCode);
                if (statusCode == HttpURLConnection.HTTP_CREATED) {
                    InputStream inputStream = connection.getInputStream();
                    // Process the response data
                    inputStream.close();
                    return true;
                } else {
                    // Handle the error
                    return false;
                }
            } catch (Exception e) {
                Log.d("MainActivity",e.toString());
                // Handle the exception
                return false;
            }
        }

        protected void onPostExecute(Boolean result) {
            // Update the UI based on the result of the background operation
            if (result) {
                Toast.makeText(GameOver.this, "Score sent successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(GameOver.this, "Failed to send score", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void exit(View view) {
        finish();
    }

    private boolean isNameEmpty(String name){
        if(name.length() == 0){
            Toast.makeText(GameOver.this,"Please fill in your name",Toast.LENGTH_LONG).show();
        }
        return name.length() == 0;
    }
    public void share(View view){
        EditText nameEditText = findViewById(R.id.nameEditText);
        String name = nameEditText.getText().toString();
        Log.d("MainActivity", String.valueOf(name.length()));
        int points = getIntent().getExtras().getInt("points");
        if(!isNameEmpty(name) &&  points == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Are you sure?");
            builder.setMessage("Your score is 0. Are you sure you want to share?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Action to perform when Yes button is clicked
                    sharePopUp(name +" scored " + String.valueOf(points) + " points.","");

                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Action to perform when No button is clicked
                    Toast.makeText(GameOver.this,"Try again :)",Toast.LENGTH_LONG).show();

                }
            });
            builder.show();
        }else if(!isNameEmpty(name) && points > 0){
            sharePopUp(name +" scored " + String.valueOf(points) + " points.","");
        }

    }

    private void sharePopUp(String body, String sub){
        Intent myIntent = new Intent(Intent.ACTION_SEND);
        myIntent.setType("text/plain");
        myIntent.putExtra(Intent.EXTRA_SUBJECT, sub);
        myIntent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(myIntent, "Share Using"));
    }
}

