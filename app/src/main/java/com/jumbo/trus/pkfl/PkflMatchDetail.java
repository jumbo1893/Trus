package com.jumbo.trus.pkfl;

import java.util.ArrayList;
import java.util.List;

public class PkflMatchDetail {

    private String refereeComment;
    private List<PkflMatchPlayer> pkflPlayers;

    public PkflMatchDetail(String refereeComment, List<PkflMatchPlayer> pkflPlayers) {
        this.refereeComment = refereeComment;
        this.pkflPlayers = pkflPlayers;
    }

    public String getRefereeComment() {
        return refereeComment;
    }

    public List<PkflMatchPlayer> getPkflPlayers() {
        return pkflPlayers;
    }

    public PkflMatchPlayer getBestPlayer() {
        for (PkflMatchPlayer pkflMatchPlayer : pkflPlayers) {
            if (pkflMatchPlayer.isBestPlayer()) {
                return pkflMatchPlayer;
            }
        }
        return null;
    }

    public List<PkflMatchPlayer> getGoalScorers() {
        List<PkflMatchPlayer> players = new ArrayList<>();
        for (PkflMatchPlayer pkflMatchPlayer : pkflPlayers) {
            if (pkflMatchPlayer.getGoals() > 0) {
                players.add(pkflMatchPlayer);
            }
        }
        return players;
    }

    public List<PkflMatchPlayer> getOwnGoalScorers() {
        List<PkflMatchPlayer> players = new ArrayList<>();
        for (PkflMatchPlayer pkflMatchPlayer : pkflPlayers) {
            if (pkflMatchPlayer.getOwnGoals() > 0) {
                players.add(pkflMatchPlayer);
            }
        }
        return players;
    }

    public List<PkflMatchPlayer> getYellowCardPlayers() {
        List<PkflMatchPlayer> players = new ArrayList<>();
        for (PkflMatchPlayer pkflMatchPlayer : pkflPlayers) {
            if (pkflMatchPlayer.getYellowCards() > 0) {
                players.add(pkflMatchPlayer);
            }
        }
        return players;
    }

    public List<PkflMatchPlayer> getRedCardPlayers() {
        List<PkflMatchPlayer> players = new ArrayList<>();
        for (PkflMatchPlayer pkflMatchPlayer : pkflPlayers) {
            if (pkflMatchPlayer.getYellowCards() > 0) {
                players.add(pkflMatchPlayer);
            }
        }
        return players;
    }
}
