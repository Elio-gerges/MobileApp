package com.mobileappcourse.api;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * "Simple Request Queue Factory" is the implementation of the Volley API
 * Request strategy.
 * This class is a singleton for us to instantiate it once and use its
 * instance as many times as needed in the whole project.*/
public class SimpleRequestQueueFactory {

    private static SimpleRequestQueueFactory self; // For singleton

    private RequestQueue queue; // To keep track of our API Requests
    private static Context context; // Is the application context to check the context

    private SimpleRequestQueueFactory(Context context) {
        // Private constructor of the singleton
        // Gets the context saved below in the getInstance function
        SimpleRequestQueueFactory.context = context;
        // Creates a new Volley Request to get data from API based of the context
        this.queue = Volley.newRequestQueue(context);
    }

    public static SimpleRequestQueueFactory getInstance(Context c) throws Exception {
        // Conceptual error made by developer using this class
        // Check if context passed in params is null.
        if(c == null) {
            // If null, throws error.
            throw new Exception("Conceptual Error: Context is null; Error - Context should not be null;");
        }

        // If not null:
        // Check if singleton is initiated and make sure that we have the same
        // Context as before.
        if(self == null || context != c) {
            // If not initiated or different context from before:
            // Create a new instance of the SimpleRequestQueueFactory.
            self = new SimpleRequestQueueFactory(c);
        }

        // Return the SimpleRequestQueueFactory instance.
        return self;
    }

    public RequestQueue getQueue() {
        // Will only be called after instantiation of the class
        // SimpleRequestQueueFactory and will only return the
        // queue in question to use it to get data from API.
        return this.queue;
    }
}
