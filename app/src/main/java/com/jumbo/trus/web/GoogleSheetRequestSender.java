package com.jumbo.trus.web;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


public class GoogleSheetRequestSender {

    private static final String TAG = "GoogleSheetRequestSender";

    private static final int SOCKET_TIMEOUT = 50000;
    private static final String SCRIPT_URL = "https://script.google.com/macros/s/AKfycbxoFFIKPNDRo40vwZEL0ShiheV0Yo1JzKQzGzY12HFD6_RkXwHsKp-PImsfQhQ3J2g/exec";
    private IRequestListener iRequestListener;
    private Context context;

    public GoogleSheetRequestSender(IRequestListener iRequestListener, Context context) {
        this.iRequestListener = iRequestListener;
        this.context = context;
    }

    public void sendRequest(final JSONObject jsonObject) {
        Log.d(TAG, "sendRequest: " + jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SCRIPT_URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                iRequestListener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                parseVolleyError(error);
            }
        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(SOCKET_TIMEOUT, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    public void parseVolleyError(VolleyError error) {
        if (error instanceof TimeoutError) {
            //This indicates that the reuest has either time out or there is no connection
            iRequestListener.onErrorResponse("Připojení timeoutovalo");
        } else if (error instanceof NoConnectionError) {
            iRequestListener.onErrorResponse("Chybí připojení, buď spadly servery google nebo seš v metru vole");

        } else if (error instanceof AuthFailureError) {
            // Error indicating that there was an Authentication Failure while performing the request
            iRequestListener.onErrorResponse("Autentizační chyba");

        } else if (error instanceof ServerError) {
            //Indicates that the server responded with a error response
            iRequestListener.onErrorResponse("Nastala serverová chyba");

        } else if (error instanceof NetworkError) {
            //Indicates that there was network error while performing the request
            iRequestListener.onErrorResponse("Network error");

        } else if (error instanceof ParseError) {
            // Indicates that the server response could not be parsed
            iRequestListener.onErrorResponse("Neznámá chyba");

        }

    }
}
