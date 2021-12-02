package com.mobileappcourse.api;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class APIRequest {

    private static APIRequest self; // For singleton

    private static Context mcontext; // Will contain the context of the application
    private SimpleRequestQueueFactory factory; // Will contain the SimpleRequestQueueFactory
                                               //  instance.

    private APIRequest(Context context) {
        // Private constructor of the singleton
        try {
            // Save the context for later use.
            mcontext = context;

            // Create new factory instance from SimpleRequestQueueFactory singleton
            //  while passing the context in question.
            this.factory = SimpleRequestQueueFactory.getInstance(mcontext);
        } catch (Exception e) {
            // Print error if happens
            e.printStackTrace();
        }
    }

    public static APIRequest getInstance(Context context) {
        // Check if singleton is initiated
        if(self == null) {
            // If not initiated:
            // Create a new instance of the APIRequest passing the context.
            self = new APIRequest(context);
        }

        // Return the APIRequest instance.
        return self;
    }

    public void sendRequest(int method,
                            String url,
                            JSONObject JSONRequest,
                            Response.Listener<org.json.JSONObject> listener,
                            Response.ErrorListener errorListener,
                            Map<String, String> requestParams) throws Exception {
        // sendRequest is only accessible after instantiating the singleton
        // Will take the following params:
        // - Method: in integer format from Request.method.Get or Request.method.Post
        // - JSONRequest: in JSONObject in case we need to send a requests with the API
        // - listener: is a callback function that will be executed after we have successfully
        //             retrieved the API data.
        // - errorListener: is a callback function that will be executed after we haven't successfully
        //             retrieved the API data.
        // - requestParams: in a Map format to send key=value params with the API

        // Creating the Request to get the API data AS JSON OBJECT
        final JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(method, url, JSONRequest, listener, errorListener) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() {
                        try {
                            return JSONRequest == null ? null : JSONRequest.toString().getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            // In case there is an error of encoding from the API provider side
                            // Will throw this.
                            return null;
                        }
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        // Will send the request params passed.
                        // To be used by Volley to create the request.
                        return requestParams;
                    }
                };

        // JSON Object Policy implemented in case there is a timeout (API Request taking too long to respond).
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // The SimpleRequestQueueFactory will be used to get the queue
        //  and add this request to the queue.
        this.factory.getQueue().add(jsonObjectRequest);
    }
}
