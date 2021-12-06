package fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import com.mobileappcourse.db.DBManager;
import com.mobileappcourse.mobileapp.MovieActivity;
import com.mobileappcourse.mobileapp.R;
import com.mobileappcourse.utilities.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FavoriteFragment extends Fragment {

    private static Context mcontext;
    private ArrayList<Movie> movies;

    private ListView moviesList;
    private ImageView img;
//    private TextView text;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    public static FavoriteFragment newInstance(Context context) {
        FavoriteFragment fragment = new FavoriteFragment();
        mcontext = context;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        moviesList = view.findViewById(R.id.lstMovies);
        img = view.findViewById(R.id.empty_movie);
//        text = view.findViewById(R.id.empty_movies);

        movies = DBManager.getInstance(mcontext).getMovies();
        moviesList.setAdapter(new MovieAdapter(mcontext, movies));
        if(movies.size() == 0) {
            // Show MSG
//            img.setVisibility(View.VISIBLE);
//            text.setVisibility(View.VISIBLE);
            // Hide list
            moviesList.setVisibility(View.INVISIBLE);
        } else {
            // Hide MSG
//            img.setVisibility(View.INVISIBLE);
//            text.setVisibility(View.INVISIBLE);
            // Show list
            moviesList.setVisibility(View.VISIBLE);

            // Fill Movie info
            for (int i = 0; i < movies.size(); i++) {
                getMovieDataFromAPI(movies.get(i));
            }
        }
        return view;
    }

    private void getMovieDataFromAPI(Movie movie) {
        Response.Listener<JSONObject> jsonObjectListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject movieObject = response.getJSONObject("results");
                    movie.setDescription(movieObject.getString("plot"));
                    movie.setId(movieObject.getString("imdb_id"));
                    movie.setRating(movieObject.getString("rating"));
                    movie.setTitle(movieObject.getString("title"));
                    movie.setImage_url(movieObject.getString("image_url"));
                    movie.setRelease(movieObject.getString("release"));

                    // Notify change
                    ((BaseAdapter) MovieAdapter.getAdapter()).notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("errorListener", "onErrorResponse: " + error);
                Toast.makeText(mcontext, error.getMessage(), Toast.LENGTH_LONG);
            }
        };

        Map<String, String> headerParams = new HashMap<String, String>();
        headerParams.put(Constants.API_KEY_NAME, Constants.API_KEY_VALUE);

        String url = Constants.BASE_URL + "/id/" + movie.getId() + "/";

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

        private Context context; // Context
        private ArrayList<Movie> items; // Data source of the list adapter
        private static BaseAdapter adapter;

        // Public constructor
        public MovieAdapter(Context context, ArrayList<Movie> items) {
            this.context = context;
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

            // get current item to be displayed
            Movie currentItem = (Movie) getItem(position);
            Log.d("Fav", "getView: " + currentItem.toString());

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