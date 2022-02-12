package com.jumbo.trus.notification;

import com.jumbo.trus.Date;
import com.jumbo.trus.Model;
import com.jumbo.trus.fine.ReceivedFine;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.player.Player;
import com.jumbo.trus.user.User;

import java.util.List;

public class Notification extends Model {

    private String title;
    private User user;
    private String text;
    private long timestamp;

    public Notification(String title, String text, User user) {
        super(title);
        this.title = title;
        this.text = text;
        this.user = user;
        timestamp = System.currentTimeMillis();
    }

    public Notification(String title, String text) {
        super(title);
        this.title = title;
        this.text = text;
        timestamp = System.currentTimeMillis();
    }

    public Notification(String title, User user) {
        super(title);
        this.title = title;
        this.user = user;
        timestamp = System.currentTimeMillis();
    }

    public Notification() {
    }

    public Notification(Match match, List<Player> playerList, List<Integer> oldBeers, List<Integer> oldLiquors) {
        timestamp = System.currentTimeMillis();
        this.title = "Změna pitiva v zápase proti " + match.getName();
        text = "";
        for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).getNumberOfBeers() != oldBeers.get(i)) {
                text += playerList.get(i).getName() + ": " + oldBeers.get(i) + " piv ==> " + playerList.get(i).getNumberOfBeers() + " piv\n";
            }
            if (playerList.get(i).getNumberOfLiquors() != oldLiquors.get(i)) {
                text += playerList.get(i).getName() + ": " + oldLiquors.get(i) + " panáků ==> " + playerList.get(i).getNumberOfLiquors() + " panáků\n";
            }
        }
    }

    public Notification(Match match, Player player, List<ReceivedFine> newReceivedFines) {
        timestamp = System.currentTimeMillis();
        this.title = "Změna pokut v zápase proti " + match.getName() + " u hráče " + player.getName();
        text = "Nový stav pokut:\n";
        for (int i = 0; i < newReceivedFines.size(); i++) {
            text += newReceivedFines.get(i).getFine().getName() + ": ==> " + newReceivedFines.get(i).getCount() + "\n";
        }
    }

    public Notification prepareNotificationAboutChangedSeasons(List<Match> matchList) {
        String title = "U následujících zápasů byla změněna sezona: ";
        String text = "";
        for (Match match : matchList) {
            text += "zápas s " + match.getName() + ", " + match.returnDateOfMatchInStringFormat() + ": změna na " + match.getSeason().getName() + "\n";
        }
        return new Notification(title, text);
    }

    public Notification prepareNotificationAboutChangedFinesInPlayerList(Match match, List<ReceivedFine> fines, List<Integer> finesNumbers, List<Player> players) {
        StringBuilder notificationText = new StringBuilder();
        StringBuilder notificationTitle = new StringBuilder("V zápase proti " + match.getOpponent() + " byly změněny pokuty u hráčů: ");
        //pro notifikaci, ať to neprojíždí přes všechny hráče
        for (int i = 0; i < fines.size(); i++) {
            int count = finesNumbers.get(i);
            if (count > 0) {
                notificationText.append(fines.get(i).getName()).append(" navýšeno o ").append(count).append("\n");

            }
        }
        for (Player player : players) {
            notificationTitle.append(player.getName() + ", ");
        }
        return new Notification(notificationTitle.toString(), notificationText.toString());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestampInStringFormat() {
        Date date = new Date();
        return date.convertMillisToStringTimestamp(timestamp);
    }

    @Override
    public String toString() {
        return "Notification{" +
                "text='" + title + '\'' +
                ", user=" + user +
                ", timestamp=" + timestamp +
                '}';
    }
}
