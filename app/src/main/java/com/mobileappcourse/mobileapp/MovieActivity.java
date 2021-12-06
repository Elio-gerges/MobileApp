package com.mobileappcourse.mobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobileappcourse.api.APIRequest;
import com.mobileappcourse.beans.Movie;
import com.mobileappcourse.db.DBException;
import com.mobileappcourse.db.DBManager;
import com.mobileappcourse.utilities.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import fragments.HomeFragment;

public class MovieActivity extends AppCompatActivity {

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Intent intent = getIntent();
        String id = intent.getStringExtra("movie_id");
        getMovieDataFromAPI(id);
    }

    private void populateMovieData() {
        if(movie != null) {
            ImageView image1 = findViewById(R.id.imgMovieIntent);
            ImageView image2 = findViewById(R.id.imgMovieTrailerIntent);
            TextView lblMovieNameIntent = findViewById(R.id.lblMovieNameIntent);
            TextView lblMovieRelease = findViewById(R.id.lblMovieRelease);
            TextView lblMovieReview = findViewById(R.id.lblMovieReview);
            TextView lblMovieDescription = findViewById(R.id.lblMovieDescription);

            Picasso.get().load(movie.getImage_url()).into(image1);
            Picasso.get().load(movie.getImage_url()).into(image2);
            lblMovieNameIntent.setText(movie.getTitle());
            lblMovieRelease.setText(movie.getRelease());
            lblMovieReview.setText(movie.getRating());
            lblMovieDescription.setText(movie.getDescription());

            image2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(movie.getTrailer());
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }
            });

            FloatingActionButton btnFavorite = findViewById(R.id.fab);
            btnFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        DBManager.getInstance(getApplicationContext())
                                .manageMovie(movie);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void getMovieDataFromAPI(String id) {
        Response.Listener<JSONObject> jsonObjectListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject movieObject = response.getJSONObject("results");
                    movie = new Movie();
                    movie.setDescription(movieObject.getString("plot"));
                    movie.setId(movieObject.getString("imdb_id"));
                    movie.setRating(movieObject.getString("rating"));
                    movie.setTitle(movieObject.getString("title"));
                    movie.setImage_url(movieObject.getString("image_url"));
                    movie.setRelease(movieObject.getString("release"));
                    movie.setTrailer(movieObject.getString("trailer"));
                    populateMovieData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG);
            }
        };

        Map<String, String> headerParams = new HashMap<String, String>();
        headerParams.put(Constants.API_KEY_NAME, Constants.API_KEY_VALUE);

        String url = Constants.BASE_URL + "/id/" + id + "/";

        try {
            APIRequest.getInstance(getApplicationContext()).sendRequest(
                    Request.Method.GET,
                    url,
                    null,
                    jsonObjectListener,
                    errorListener,
                    headerParams
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}