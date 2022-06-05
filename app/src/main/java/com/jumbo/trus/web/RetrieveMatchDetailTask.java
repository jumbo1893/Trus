package com.jumbo.trus.web;

import android.util.Log;

import com.jumbo.trus.pkfl.PkflMatch;
import com.jumbo.trus.pkfl.PkflMatchDetail;
import com.jumbo.trus.pkfl.PkflMatchPlayer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class RetrieveMatchDetailTask implements Callable<PkflMatchDetail> {

    private final String url;
    private static final String TAG = "RetrieveMatchDetail";

    public RetrieveMatchDetailTask(String url) {
        this.url = url;
        Log.d(TAG, "RetrieveMatchDetail: " + url);
    }

    @Override
    public PkflMatchDetail call() {
        PkflMatchDetail pkflMatchDetail = null;
        try {
            //Connect to the website
            Document document = Jsoup.connect(url).ignoreHttpErrors(true).validateTLSCertificates(false).get();
            Elements matches = document.getElementsByClass("matches");
            Elements ps = matches.select("p");
            Elements tables = document.getElementsByClass("dataTable table table-striped no-footer");
            Log.d(TAG, "call: " + tables.size());
            pkflMatchDetail = new PkflMatchDetail(getRefereeComment(ps), getPlayersFromMatch(getTrsFromTables(tables, isHomeMatch(ps))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pkflMatchDetail;
    }

    private String getRefereeComment(Elements ps) {
        if (ps.size() > 6 && ps.get(6).text().toLowerCase().contains("komentář")) {
            return ps.get(6).text();
        }
        else return "Bez komentáře rozhodčího";
    }

    private Boolean isHomeMatch(Elements ps) {
        String name = ps.get(0).text().split("/")[1].trim().toLowerCase();
        Log.d(TAG, "isHomeMatch: " + name);
        return !name.equals("liščí trus");
    }

    private List<PkflMatchPlayer> getPlayersFromMatch(Elements trs) {
        List<PkflMatchPlayer> players = new ArrayList<>();

        for (Element tr : trs) {
            Elements tds = tr.select("td");
            if (tds.size() > 6) {
                players.add(initPlayerFromTds(tds));
            }
        }
        return players;
    }

    private PkflMatchPlayer initPlayerFromTds(Elements tds) {

        PkflMatchPlayer pkflMatchPlayer = null;
        try {
            pkflMatchPlayer = new PkflMatchPlayer(tds.get(0).text().trim(), Integer.parseInt(tds.get(1).text()), Integer.parseInt(tds.get(2).text()), Integer.parseInt(tds.get(3).text()),
                    getNumbersFromString(tds.get(4).text()), Integer.parseInt(tds.get(5).text()), Integer.parseInt(tds.get(6).text()), isBestPlayer(tds.get(0)));
            if (pkflMatchPlayer.getYellowCards() > 0) {
                pkflMatchPlayer.setYellowCardComment(returnCardComment(tds.get(5)));
            }
            if (pkflMatchPlayer.getRedCards() > 0) {
                pkflMatchPlayer.setRedCardComment(returnCardComment(tds.get(6)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pkflMatchPlayer;
    }

    private String returnCardComment(Element td) {
        String comment = null;
        try {
            comment = td.select("a").first().attr("title");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return comment;
    }

    private int getNumbersFromString(String number) {
        return Integer.parseInt(number.replaceAll("[^0-9]", ""));
    }

    private Elements getTrsFromTables(Elements tables, boolean homeMatch) {
        if (homeMatch) {
            return tables.get(0).select("tr");
        }
        else {
            return tables.get(1).select("tr");
        }
    }

    private Boolean isBestPlayer(Element tdName) {
        Elements elements = tdName.getElementsByClass("best-player");
        return !elements.isEmpty();
    }
}
