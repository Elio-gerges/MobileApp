package fragments;

import android.content.Context;
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
import com.mobileappcourse.mobileapp.R;
import com.mobileappcourse.utilities.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private static Context mcontext;

    private final String TAG = "HomeFragment";

    private ArrayList<Movie> movies = new ArrayList<Movie>();

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance(Context context) {
        HomeFragment fragment = new HomeFragment();
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
        getAPIData();
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private int movieTotal;

    private void getAPIData() {
        Response.Listener<JSONObject> jsonObjectListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray moviesData = response.getJSONArray("results");
                    movieTotal = response.getInt("count");
                    for (int i = 0; i < moviesData.length(); i++) {
                        getMovieDataFromAPI(moviesData.getJSONObject(i).getString("imdb_id"));
                    }
                    ListView moviesList = (ListView) getView().findViewById(R.id.lstMovies);
                    moviesList.setAdapter(new MovieAdapter(mcontext, movies));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error);
                Toast.makeText(mcontext, error.getMessage(), Toast.LENGTH_LONG);
            }
        };

        Map<String, String> headerParams = new HashMap<String, String>();
        headerParams.put(Constants.API_KEY_NAME, Constants.API_KEY_VALUE);

        String url = Constants.BASE_URL + "/order/upcoming/";

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
                    movies.add(movie);
                    ((BaseAdapter) MovieAdapter.getAdapter()).notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error);
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

        private Context context; // Context
        private ArrayList<Movie> items; // Data source of the list adapter
        private static BaseAdapter adapter;
        private static int done;

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
            // Inflate the layout for each list row
            if (convertView == null) {
                if ( position == 0 && done <= 0) {
                    // Display big first movie
                    convertView = LayoutInflater.from(context).
                            inflate(R.layout.movie_banner, parent, false);

                    // get current item to be displayed
                    Movie currentItem = (Movie) getItem(position);

                    // get the TextView for item name and item description
                    ImageView imageViewMovie = (ImageView)
                            convertView.findViewById(R.id.imgFirstMovie);
                    TextView textViewMovieName = (TextView)
                            convertView.findViewById(R.id.lblFirstMovie);

                    textViewMovieName.setText(currentItem.getTitle());
                    Picasso.get().load(currentItem.getImage_url()).into(imageViewMovie);
                    done++;
                } else {
                    // Display regular movies
                    convertView = LayoutInflater.from(context).
                            inflate(R.layout.movie, parent, false);

                    // get current item to be displayed
                    Movie currentItem = (Movie) getItem(position);

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
            }

            // returns the view for the current row
            return convertView;
        }
    }
}