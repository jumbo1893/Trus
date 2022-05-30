package com.jumbo.trus.pkfl.stats;

public enum SpinnerOption {
    GOALS("Góly"),
    BEST_PLAYERS("Hvězdy zápasu"),
    YELLOW_CARDS("Žluté karty"),
    RED_CARDS("Červené karty"),
    OWN_GOALS("Vlastňáky"),
    MATCHES("Zápasy"),
    GOALKEEPING_MINUTES("Odchytané minuty"),
    GOAL_RATIO("Počet gólů/zápasy"),
    RECEIVED_GOALS_RATIO("Obdržené góly/odchytané zápasy"),
    BEST_PLAYER_RATIO("Hvězda utkání/zápasy"),
    YELLOW_CARD_RATIO("Žluté karty/zápasy"),
    HATTRICK("Hattricky"),
    CLEAN_SHEET("Čistá konta"),
    CARD_DETAIL("Detail karet"),
    RECEIVED_GOALS("Obdržené góly");


    private String name;

    SpinnerOption(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
