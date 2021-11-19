package com.jumbo.trus.web;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class RetreiveFeedTask extends AsyncTask<String, Void, String>
{
    @Override
    protected String doInBackground(String ... urls)
    {
        try
        {
            URL url = new URL(urls[0]);
            URLConnection uc = url.openConnection();
            //String j = (String) uc.getContent();
            uc.setDoInput(true);
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String inputLine;
            StringBuilder a = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                a.append(inputLine);
            in.close();

            return a.toString();
        }
        catch (Exception e)
        {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("TAG", "onPostExecute: " + result);
    }
}