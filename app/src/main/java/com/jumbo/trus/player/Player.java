package com.jumbo.trus.player;

import android.util.Log;

import com.jumbo.trus.Date;
import com.jumbo.trus.Model;
import com.jumbo.trus.fine.Fine;
import com.jumbo.trus.fine.ReceivedFine;
import com.jumbo.trus.match.Match;
import com.jumbo.trus.repayment.Repayment;

import java.util.ArrayList;
import java.util.List;

public class Player extends Model {

    private static final String TAG = "Player";

    private int age;
    private boolean fan;
    private long dateOfBirth;
    private int numberOfBeers;
    private int numberOfLiquors;
    private int numberOfBeersInMatches;
    private int numberOfLiquorsInMatches;
    private int numberOfFinesInMatches;
    private int amountOfFinesInMatches;
    private List<ReceivedFine> receivedFines = new ArrayList<>();
    private boolean matchParticipant;
    private List<Repayment> repayments = new ArrayList<>();

    private Date date = new Date();

    public Player(String name, boolean fan, long dateOfBirth) {
        super(name);
        this.fan = fan;
        this.dateOfBirth = dateOfBirth;
        age = date.calculateAge(dateOfBirth);
    }

    public Player() {

    }

    public boolean isMatchParticipant() {
        return matchParticipant;
    }

    public void setMatchParticipant(boolean matchParticipant) {
        this.matchParticipant = matchParticipant;
    }

    public int getAge() {
        age = date.calculateAge(dateOfBirth);
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isFan() {
        return fan;
    }

    public void setFan(boolean fan) {
        this.fan = fan;
    }

    public long getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(long dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        age = date.calculateAge(dateOfBirth);
    }

    public String getBirthdayInStringFormat() {
        Date date = new Date();
        return date.convertMillisToTextDate(dateOfBirth);
    }

    public int getNumberOfBeers() {
        return numberOfBeers;
    }

    public void setNumberOfBeers(int numberOfBeers) {
        this.numberOfBeers = numberOfBeers;
    }

    public int getNumberOfLiquors() {
        return numberOfLiquors;
    }

    public void setNumberOfLiquors(int numberOfLiquors) {
        this.numberOfLiquors = numberOfLiquors;
    }

    public int getNumberOfBeersInMatches() {
        return numberOfBeersInMatches;
    }

    public void setNumberOfBeersInMatches(int numberOfBeersInMatches) {
        this.numberOfBeersInMatches = numberOfBeersInMatches;
    }

    public int getNumberOfLiquorsInMatches() {
        return numberOfLiquorsInMatches;
    }

    public void setNumberOfLiquorsInMatches(int numberOfLiquorsInMatches) {
        this.numberOfLiquorsInMatches = numberOfLiquorsInMatches;
    }

    public int getNumberOfFinesInMatches() {
        return numberOfFinesInMatches;
    }

    public void setNumberOfFinesInMatches(int numberOfFinesInMatches) {
        this.numberOfFinesInMatches = numberOfFinesInMatches;
    }

    public int getAmountOfFinesInMatches() {
        return amountOfFinesInMatches;
    }

    public void setAmountOfFinesInMatches(int amountOfFinesInMatches) {
        this.amountOfFinesInMatches = amountOfFinesInMatches;
    }

    public void addBeer() {
        numberOfBeers++;
    }

    public void removeBeer() {
        numberOfBeers--;
        if (numberOfBeers < 0) {
            numberOfBeers = 0;
        }
    }

    public void addLiquor() {
        numberOfLiquors++;
    }

    public void removeLiquor() {
        numberOfLiquors--;
        if (numberOfLiquors < 0) {
            numberOfLiquors = 0;
        }
    }

    public List<ReceivedFine> getReceivedFines() {
        return receivedFines;
    }

    public void setReceivedFines(List<ReceivedFine> receivedFines) {
        this.receivedFines = receivedFines;
    }

    public List<Repayment> getRepayments() {
        return repayments;
    }

    public void setRepayments(List<Repayment> repayments) {
        this.repayments = repayments;
    }

    public void addRepayment(Repayment repayment) {
        repayments.add(repayment);
    }

    public boolean removeRepayment(Repayment repayment) {
        return repayments.remove(repayment);
    }

    public int returnAmountOwed() {
        int repaymentsAmount = 0;
        for (Repayment repayment : repayments) {
            repaymentsAmount += repayment.getAmount();
        }
        return (getAmountOfFinesInMatches() - repaymentsAmount);
    }

    public void addNewFine(Fine fine) {
        receivedFines.add(new ReceivedFine(fine, 0));
    }

    public boolean addNewFineCount(Fine fine, int count) {
        for (ReceivedFine receivedFine : receivedFines) {
            if (receivedFine.getFine().equals(fine)) {
                receivedFine.addFineCount(count);
                return true;
            }
        }
        return false;
    }

    /**
     * metoda vezme dostupné pokuty a přidá je již k existujícím pokutám co má hráč
     * Pokud jsou pokuty stejné, tak zůstane ta co má hráč (včetně výše, atd.)
     * @param fines všechny dostupné pokuty
     */
    public void mergeFineLists(List<Fine> fines) {
        for (int i = 0; i < fines.size(); i++) {
            Log.d(TAG, "mergeFineLists: fine: " + fines.get(i));
            if (!receivedFines.contains(fines.get(i))) {
                addNewFine(fines.get(i));
            }
        }
    }

    /**
     * vypočítá do parametru numberOfBeersInMatches celkový počet piv v zápasech
     * @param matchList seznam zápasů, ze kterých se to počítá
     */
    public void calculateAllBeersNumber(List<Match> matchList) {
        numberOfBeersInMatches = 0;
        for (Match match : matchList) {
            for (Player player : match.returnPlayerListOnlyWithParticipants()) {
                if (player.equals(this)) {
                    numberOfBeersInMatches += player.numberOfBeers;
                    break;
                }
            }
        }
    }


    /**
     * vypočítá do parametru numberOfLiquorsInMatches celkový počet tvrdýho v zápasech
     * @param matchList seznam zápasů, ze kterých se to počítá
     */
    public void calculateAllLiquorsNumber(List<Match> matchList) {
        numberOfLiquorsInMatches = 0;
        for (Match match : matchList) {
            for (Player player : match.returnPlayerListOnlyWithParticipants()) {
                if (player.equals(this)) {
                    numberOfLiquorsInMatches += player.numberOfLiquors;
                    break;
                }
            }
        }
    }

    /**
     * vypočítá do parametru numberOfFinesInMatches a amountOfFinesInMatches celkový počet pokut a částku v zápasech
     * @param matchList seznam zápasů, ze kterých se to počítá
     */
    public void calculateAllFinesNumber(List<Match> matchList) {
        numberOfFinesInMatches = 0;
        amountOfFinesInMatches = 0;
        for (Match match : matchList) {
            for (Player player : match.returnPlayerListWithoutFans()) {
                if (player.equals(this)) {
                    numberOfFinesInMatches += player.returnNumberOfAllReceviedFines();
                    amountOfFinesInMatches += player.returnAmountOfAllReceviedFines();
                    break;
                }
            }
        }
    }

    /**
     * použití pro jeden zápas
     * @return celkovou částku všech udělených pokut
     */
    public int returnAmountOfAllReceviedFines() {
        int amount = 0;
        for (ReceivedFine receivedFine : receivedFines) {
            amount += receivedFine.getAmountOfAllFines();
        }
        return amount;
    }

    /**
     * použití pro jeden zápas
     * @return počet všech udělených pokut hráči
     */
    public int returnNumberOfAllReceviedFines() {
        int fineCount = 0;
        for (ReceivedFine receivedFine : receivedFines) {
            fineCount += receivedFine.getCount();
        }
        return fineCount;
    }

    public int calculateDaysToBirthday () {
        Date date = new Date();
        return date.calculateDaysToBirthday(dateOfBirth);
    }

    /**
     * použití pro jeden zápas, nutné použít na hráče co je v zápase
     * @return počet pokut udělených hráči
     */
    public int returnNumberOfReceviedFine(ReceivedFine fine) {
        for (ReceivedFine receivedFine : receivedFines) {
            if (fine.equals(receivedFine))
            return receivedFine.getCount();
        }
        return 0;
    }

    /**
     * použití pro jeden zápas, nutné použít na hráče co je v zápase
     * @return částku, která tato pokuta stála hráče v zápase
     */
    public int returnAmountOfReceviedFine(ReceivedFine fine) {
        for (ReceivedFine receivedFine : receivedFines) {
            if (fine.equals(receivedFine))
                return receivedFine.getAmountOfAllFines();
        }
        return 0;
    }

    /**
     * použití pro jeden zápas, nutné použít na hráče co je v zápase
     * @return počet pokut tohoto typu, které padly v zápase
     */
    public int returnNumberOfReceviedFine(Fine fine) {
        for (ReceivedFine receivedFine : receivedFines) {
            if (fine.equals(receivedFine))
                return receivedFine.getCount();
        }
        return 0;
    }

    /**
     * použití pro jeden zápas, nutné použít na hráče co je v zápase
     * @return částka za pokuty tohoto typu, které padly v zápase
     */
    public int returnAmountOfReceviedFine(Fine fine) {
        for (ReceivedFine receivedFine : receivedFines) {
            if (fine.equals(receivedFine))
                return receivedFine.getAmountOfAllFines();
        }
        return 0;
    }

    public List<ReceivedFine> returnReceivedFineWithCount() {
        List<ReceivedFine> returnFines = new ArrayList<>();
        for (ReceivedFine receivedFine : receivedFines) {
            if (receivedFine.getCount() > 0) {
                returnFines.add(receivedFine);
            }
        }
        return returnFines;
    }

    /**
     * vrátí částku, kterou hráč zaplatil na této konkrétní pokutě v konkrétních zápasech
     * @param matchList seznam zápasů, ze kterých se to počítá
     * @param fine pokuta kterou zjišťujem
     */
    public int returnFineNumber(List<Match> matchList, Fine fine) {
        int fineNumber = 0;
        for (Match match : matchList) {
            for (Player player : match.returnPlayerListWithoutFans()) {
                if (player.equals(this)) {
                    fineNumber += player.returnAmountOfReceviedFine(fine);
                }
            }
        }
        return fineNumber;
    }

    @Override
    public String toString() {
        return "Hrac{" +
                "jmeno='" + name + '\'' +
                ", vek=" + age +
                ", fanousek=" + fan +
                ", datumNarozeni=" + getBirthdayInStringFormat() +
                ", pocet piv=" + numberOfBeers +
                '}';
    }
}
