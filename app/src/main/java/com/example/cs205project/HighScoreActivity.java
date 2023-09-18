package com.example.cs205project;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class HighScoreActivity extends MainActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        RequestQueue volleyQueue = Volley.newRequestQueue(this);
        String url = "http://3.15.12.223:31000/scores";

        // since the response we get from the api is in JSON, we
        // need to use `JsonObjectRequest` for parsing the
        // request response
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                // we are using GET HTTP request method
                Request.Method.GET,
                // url we want to send the HTTP request to
                url,
                // this parameter is used to send a JSON object to the
                // server, since this is not required in our case,
                // we are keeping it `null`
                null,

                // lambda function for handling the case
                // when the HTTP request succeeds
                (Response.Listener<JSONObject>) response -> {
                    // get the image url from the JSON object
                    Log.d("MainActivity",response.toString());
                    try {
                        String responseObj = response.getString("scores");
                        Log.d("MainActivity",responseObj);
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<HighScore>>(){}.getType();
                        List<HighScore> highScores = gson.fromJson(responseObj, type);
                        for(int i = 0;i<highScores.size();i++){
                            highScores.get(i).setID(i +1);
                        }
                        Log.d("MainActivity",highScores.toString());
                        HighScoreAdapter adapter = new HighScoreAdapter(highScores);
                        RecyclerView recyclerView = findViewById(R.id.highScoreRecycler);
                        recyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                // lambda function for handling the case
                // when the HTTP request fails
                (Response.ErrorListener) error -> {
                    // make a Toast telling the user
                    // that something went wrong
                    Toast.makeText(this, "Some error occurred! Cannot fetch high scores", Toast.LENGTH_LONG).show();
                    // log the error message in the error stream
                    Log.e("MainActivity", error.toString());
                }
        );
        volleyQueue.add(jsonObjectRequest);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.high_score_activity);
    }
    public void exit(View view) {
        finish();
    }
}
