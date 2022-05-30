package com.jumbo.trus.web;

import android.util.Log;

import com.jumbo.trus.pkfl.PkflMatch;
import com.jumbo.trus.pkfl.PkflSeason;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class RetrieveSeasonUrlTask implements Callable<List<PkflSeason>> {

    private final String url;
    private final boolean currentSeason;
    private static final String TAG = "RetrieveSeasonUrlTask";

    private static String BASE_URL = "https://pkfl.cz";

    public RetrieveSeasonUrlTask(String url, boolean currentSeason) {
        this.url = url;
        this.currentSeason = currentSeason;
    }

    @Override
    public List<PkflSeason> call() {
        List<PkflSeason> returnSeasons = new ArrayList<>();
        try {
            //Connect to the website
            Document document = Jsoup.connect(url).get();


            Element matchesSpinnerDiv = document.getElementsByClass("dropdown-content").get(0);
            Elements spinnerSeason = matchesSpinnerDiv.select("a[href]");

            Log.d(TAG, "call: " + spinnerSeason + spinnerSeason.size());
            if (currentSeason) {
                for (Element spinnerButton : spinnerSeason) {
                    Element seasonButton = document.getElementsByClass("dropbtn").get(0);
                    Log.d(TAG, "seasonButton: " + seasonButton);
                    if (spinnerButton.text().contains(getCurrentSeason(seasonButton))) {
                        returnSeasons.add(returnPkflSeason(spinnerButton));
                    }
                }
            }
            else {
                for (Element spinnerButton : spinnerSeason) {
                    returnSeasons.add(returnPkflSeason(spinnerButton));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnSeasons;
    }

    private String getCurrentSeason(Element button) {
        Log.d(TAG, "getCurrentSeason: " + button.text().split(" ")[0].trim().toLowerCase());
        return button.text().split(" ")[0].trim().toLowerCase();
    }

    private PkflSeason returnPkflSeason(Element spinnerButton) {
        PkflSeason pkflSeason = null;
        Log.d(TAG, "returnPkflSeason: " + spinnerButton);
        try {
            pkflSeason = new PkflSeason(BASE_URL + spinnerButton.select("a[href]").attr("href"), spinnerButton.text());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pkflSeason;
    }


}
