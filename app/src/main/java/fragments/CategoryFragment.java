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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mobileappcourse.api.APIRequest;
import com.mobileappcourse.beans.Movie;
import com.mobileappcourse.mobileapp.MovieActivity;
import com.mobileappcourse.mobileapp.MoviesActivity;
import com.mobileappcourse.mobileapp.R;
import com.mobileappcourse.utilities.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CategoryFragment extends Fragment {

    private static Context mcontext;

    public CategoryFragment() {
        // Required empty public constructor
    }

    public static CategoryFragment newInstance(Context context) {
        CategoryFragment fragment = new CategoryFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        getAPIData();
        return view;
    }

    private void getAPIData() {
        Response.Listener<JSONObject> jsonObjectListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray genresData = response.getJSONArray("results");
                    ArrayList<String> strings = new ArrayList<>();
                    for (int i = 0; i < genresData.length(); i++) {
                        strings.add(genresData.getJSONObject(i).getString("genre"));
                    }
                    GridView genresList = getView().findViewById(R.id.lstCategories);
                    genresList.setAdapter(new GenreAdapter(mcontext, strings));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mcontext, error.getMessage(), Toast.LENGTH_LONG);
            }
        };

        Map<String, String> headerParams = new HashMap<String, String>();
        headerParams.put(Constants.API_KEY_NAME, Constants.API_KEY_VALUE);

        String url = "https://data-imdb1.p.rapidapi.com/genres/";

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

    private static class GenreAdapter extends BaseAdapter {

        private Context context; // Context
        private ArrayList<String> items; // Data source of the list adapter
        private static BaseAdapter adapter;
        private static int done;

        // Public constructor
        public GenreAdapter(Context context, ArrayList<String> items) {
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
            String currentItem = (String) getItem(position);

            // Inflate the layout for each list row
            if (convertView == null) {
                    // Display regular movies
                    convertView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.category, parent, false);

                    // get the TextView for item name and item description
                    TextView text = (TextView)
                            convertView.findViewById(R.id.text);

                    // Sets the text for genre from the current movie object
                text.setText(currentItem);
//                convertView.findViewById(R.id.card).setB
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent movieIntent = new Intent(mcontext, MoviesActivity.class);
                    movieIntent.putExtra("genre", currentItem);
                    movieIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mcontext.getApplicationContext().startActivity(movieIntent);
                }
            });

            // returns the view for the current row
            return convertView;
        }

        private String getColor() {
            // create object of Random class
            Random obj = new Random();
            int rand_num = obj.nextInt(0xffffff + 1);
            // format it as hexadecimal string and print
            return String.format("#%06x", rand_num);
        }
    }
}