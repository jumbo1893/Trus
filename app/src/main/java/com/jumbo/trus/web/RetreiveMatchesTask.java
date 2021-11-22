package com.jumbo.trus.web;

import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.internal.$Gson$Preconditions;
import com.jumbo.trus.pkfl.PkflMatch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

public class RetreiveMatchesTask implements Callable<List<PkflMatch>> {

    private final String url;
    private static final String TAG = "RetreiveMatchesTask";

    public RetreiveMatchesTask(String url) {
        this.url = url;
    }

    @Override
    public List<PkflMatch> call() {
        List<PkflMatch> returnMatches = new ArrayList<>();
        try {
            //Connect to the website
            Document document = Jsoup.connect(url).get();
            Elements table = document.getElementsByClass("dataTable table table-bordered table-striped");
            Elements trs = table.select("tr");
            Log.d(TAG, "call: " + trs.size());
            for (Element tr : trs) {
                Elements tds = tr.select("td");
                if (tds.size() > 8) {
                    returnMatches.add(returnPkflMatch(tds));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnMatches;
    }

    private PkflMatch returnPkflMatch(Elements tds) {
        PkflMatch pkflMatch = null;
        try {
            pkflMatch = new PkflMatch(tds.get(0).text(), tds.get(1).text(), tds.get(4).text(), tds.get(5).text(), Integer.parseInt(tds.get(2).text()),
                    tds.get(3).text(), tds.get(6).text(), tds.get(7).text(), tds.get(8).text());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pkflMatch;
    }


}
