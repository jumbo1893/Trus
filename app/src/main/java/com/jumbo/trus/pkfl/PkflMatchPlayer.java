package com.jumbo.trus.pkfl;

import java.util.Objects;

public class PkflMatchPlayer {

    private String name;
    private int goals;
    private int receivedGoals;
    private int ownGoals;
    private int goalkeepingMinutes;
    private int yellowCards;
    private int redCards;
    private boolean bestPlayer;

    public PkflMatchPlayer(String name, int goals, int receivedGoals, int ownGoals, int goalkeepingMinutes, int yellowCards, int redCards, boolean bestPlayer) {
        this.name = name;
        this.goals = goals;
        this.receivedGoals = receivedGoals;
        this.ownGoals = ownGoals;
        this.goalkeepingMinutes = goalkeepingMinutes;
        this.yellowCards = yellowCards;
        this.redCards = redCards;
        this.bestPlayer = bestPlayer;
    }

    public String getName() {
        return name;
    }

    public int getGoals() {
        return goals;
    }

    public int getReceivedGoals() {
        return receivedGoals;
    }

    public int getOwnGoals() {
        return ownGoals;
    }

    public int getGoalkeepingMinutes() {
        return goalkeepingMinutes;
    }

    public int getYellowCards() {
        return yellowCards;
    }

    public int getRedCards() {
        return redCards;
    }

    public boolean isBestPlayer() {
        return bestPlayer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PkflMatchPlayer)) return false;
        PkflMatchPlayer that = (PkflMatchPlayer) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "PkflMatchPlayer{" +
                "name='" + name + '\'' +
                ", goals=" + goals +
                ", receivedGoals=" + receivedGoals +
                ", ownGoals=" + ownGoals +
                ", goalkeepingMinutes=" + goalkeepingMinutes +
                ", yellowCards=" + yellowCards +
                ", redCards=" + redCards +
                ", bestPlayer=" + bestPlayer +
                '}';
    }
}
