package com.jumbo.trus.pkfl.stats;

import com.jumbo.trus.pkfl.PkflMatchPlayer;

import java.util.Objects;

public class PkflPlayerStats {

    private String name;
    private int goals;
    private int matches;
    private int receivedGoals;
    private int ownGoals;
    private int goalkeepingMinutes;
    private int yellowCards;
    private int redCards;
    private int bestPlayer;

    public PkflPlayerStats(String name) {
        this.name = name;
        goals = 0;
        matches = 0;
    }

    public void enhanceWithPlayerDetail(PkflMatchPlayer player) {
        matches++;
        goals += player.getGoals();
        receivedGoals += player.getReceivedGoals();
        ownGoals += player.getOwnGoals();
        goalkeepingMinutes += player.getGoalkeepingMinutes();
        yellowCards += player.getYellowCards();
        redCards += player.getRedCards();
        if (player.isBestPlayer()) {
            bestPlayer++;
        }
    }

    public float getGoalMatchesRatio() {
        return (float) goals/(float)matches;
    }

    public float getReceivedGoalsGoalkeepingMinutesRatio() {
        return (float) receivedGoals/((float)goalkeepingMinutes/60);
    }

    public float getBestPlayerMatchesRatio() {
        return (float) bestPlayer/(float)matches;
    }

    public float getYellowCardMatchesRatio() {
        return (float) yellowCards/(float)matches;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getMatches() {
        return matches;
    }

    public void setMatches(int matches) {
        this.matches = matches;
    }

    public int getReceivedGoals() {
        return receivedGoals;
    }

    public void setReceivedGoals(int receivedGoals) {
        this.receivedGoals = receivedGoals;
    }

    public int getOwnGoals() {
        return ownGoals;
    }

    public void setOwnGoals(int ownGoals) {
        this.ownGoals = ownGoals;
    }

    public int getGoalkeepingMinutes() {
        return goalkeepingMinutes;
    }

    public void setGoalkeepingMinutes(int goalkeepingMinutes) {
        this.goalkeepingMinutes = goalkeepingMinutes;
    }

    public int getYellowCards() {
        return yellowCards;
    }

    public void setYellowCards(int yellowCards) {
        this.yellowCards = yellowCards;
    }

    public int getRedCards() {
        return redCards;
    }

    public void setRedCards(int redCards) {
        this.redCards = redCards;
    }

    public int getBestPlayer() {
        return bestPlayer;
    }

    public void setBestPlayer(int bestPlayer) {
        this.bestPlayer = bestPlayer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof PkflMatchPlayer && (((PkflMatchPlayer) o).getName().equals(name))) {
            return true;
        }
        if (!(o instanceof PkflPlayerStats)) return false;
        PkflPlayerStats that = (PkflPlayerStats) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
