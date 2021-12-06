package com.mobileappcourse.mobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mobileappcourse.api.APIRequest;
import com.mobileappcourse.beans.Movie;
import com.mobileappcourse.utilities.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MoviesActivity extends AppCompatActivity {

    private Context mcontext;
    private ArrayList<Movie> movies = new ArrayList<Movie>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        mcontext = getApplicationContext();

        Intent intent = getIntent();
        getAPIData(intent.getStringExtra("genre"));
    }

    private void getAPIData(String genre) {
        Response.Listener<JSONObject> jsonObjectListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray moviesData = response.getJSONArray("results");
                    for (int i = 0; i < 50; i++) {
                        getMovieDataFromAPI(moviesData.getJSONObject(i).getString("imdb_id"));
                    }
                    ListView moviesList = findViewById(R.id.lstMoviesFromCat);
                    moviesList.setAdapter(new MovieAdapter(mcontext, movies));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "onErrorResponse: " + error);
                Toast.makeText(mcontext, error.getMessage(), Toast.LENGTH_LONG);
            }
        };

        Map<String, String> headerParams = new HashMap<String, String>();
        headerParams.put(Constants.API_KEY_NAME, Constants.API_KEY_VALUE);

        String url = Constants.BASE_URL + "/byGen/" + genre + "/";

        try {
            APIRequest.getInstance(mcontext).sendRequest(
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

    private void getMovieDataFromAPI(String id) {
        Response.Listener<JSONObject> jsonObjectListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject movieObject = response.getJSONObject("results");
                    Movie movie = new Movie();
                    movie.setDescription(movieObject.getString("plot"));
                    movie.setId(movieObject.getString("imdb_id"));
                    movie.setRating(movieObject.getString("rating"));
                    movie.setTitle(movieObject.getString("title"));
                    movie.setImage_url(movieObject.getString("image_url"));
                    movie.setRelease(movieObject.getString("release"));
                    ((BaseAdapter) MovieAdapter.getAdapter()).notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "onErrorResponse: " + error);
                Toast.makeText(mcontext, error.getMessage(), Toast.LENGTH_LONG);
            }
        };

        Map<String, String> headerParams = new HashMap<String, String>();
        headerParams.put(Constants.API_KEY_NAME, Constants.API_KEY_VALUE);

        String url = Constants.BASE_URL + "/id/" + id + "/";

        try {
            APIRequest.getInstance(mcontext).sendRequest(
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

    private static class MovieAdapter extends BaseAdapter {

        private Context mcontext; // Context
        private ArrayList<Movie> items; // Data source of the list adapter
        private static BaseAdapter adapter;

        // Public constructor
        public MovieAdapter(Context context, ArrayList<Movie> items) {
            this.mcontext = context;
            this.items = items;
            this.adapter = this;
        }

        public static BaseAdapter getAdapter() {
            return adapter;
        }

        @Override
        public int getCount() {
            return this.items.size();
        }

        @Override
        public Object getItem(int position) {
            return this.items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Movie currentItem = (Movie) getItem(position);

            // Inflate the layout for each list row
            if (convertView == null) {
                // Display regular movies
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.movie, parent, false);

                // get the TextView for item name and item description
                ImageView imageViewMovie = (ImageView)
                        convertView.findViewById(R.id.imgMovie);
                TextView textViewMovieName = (TextView)
                        convertView.findViewById(R.id.lblMovieName);
                TextView textViewMovieDescription = (TextView)
                        convertView.findViewById(R.id.lblMovieDescription);
                TextView textViewMovieRelease = (TextView)
                        convertView.findViewById(R.id.lblMovieRelease);
                TextView textViewMovieRating = (TextView)
                        convertView.findViewById(R.id.lblMovieReview);

                // Sets the text and image for movie from the current movie object
                textViewMovieName.setText(currentItem.getTitle());
                textViewMovieDescription.setText(currentItem.getDescription());
                textViewMovieRelease.setText(currentItem.getRelease());
                textViewMovieRating.setText(currentItem.getRating());
                Picasso.get().load(currentItem.getImage_url()).into(imageViewMovie);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent movieIntent = new Intent(mcontext, MovieActivity.class);
                    movieIntent.putExtra("movie_id", currentItem.getId());
                    movieIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mcontext.getApplicationContext().startActivity(movieIntent);
                }
            });

            // returns the view for the current row
            return convertView;
        }
    }
}