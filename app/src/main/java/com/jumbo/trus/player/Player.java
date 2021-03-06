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

    public String returnBirthdayInStringFormat() {
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
        return receivedFines.add(new ReceivedFine(fine, count));
    }

    public void setNewFineCountsToAllReceivedFines(List<Integer> finesNumber) {
        Log.d(TAG, "setNewFineCountsToAllReceivedFines: " + finesNumber.size() + receivedFines.size());
        for (int i = 0; i < receivedFines.size(); i++) {
            receivedFines.get(i).setCount(finesNumber.get(i));
        }
    }

    public boolean compareNumberOfReceivedFines(List<Integer> finesNumber) {
        return finesNumber.equals(returnNumberOfFines());
    }

    public List<Integer> returnNumberOfFines() {
        List<Integer> finesNumbers = new ArrayList<>();
        for (ReceivedFine receivedFine : getReceivedFines()) {
            finesNumbers.add(receivedFine.getCount());
        }
        return finesNumbers;
    }

    /**
     * metoda vezme dostupn?? pokuty a p??id?? je ji?? k existuj??c??m pokut??m co m?? hr????
     * Pokud jsou pokuty stejn??, tak z??stane ta co m?? hr???? (v??etn?? v????e, atd.)
     *
     * @param fines v??echny dostupn?? pokuty
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
     * vypo????t?? do parametru numberOfBeersInMatches celkov?? po??et piv v z??pasech
     *
     * @param matchList seznam z??pas??, ze kter??ch se to po????t??
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
     * vypo????t?? do parametru numberOfLiquorsInMatches celkov?? po??et tvrd??ho v z??pasech
     *
     * @param matchList seznam z??pas??, ze kter??ch se to po????t??
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
     * vypo????t?? do parametru numberOfFinesInMatches a amountOfFinesInMatches celkov?? po??et pokut a ????stku v z??pasech
     *
     * @param matchList seznam z??pas??, ze kter??ch se to po????t??
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
     * pou??it?? pro jeden z??pas
     *
     * @return celkovou ????stku v??ech ud??len??ch pokut
     */
    public int returnAmountOfAllReceviedFines() {
        int amount = 0;
        for (ReceivedFine receivedFine : receivedFines) {
            amount += receivedFine.returnAmountOfAllFines();
        }
        return amount;
    }

    /**
     * pou??it?? pro jeden z??pas
     *
     * @return po??et v??ech ud??len??ch pokut hr????i
     */
    public int returnNumberOfAllReceviedFines() {
        int fineCount = 0;
        for (ReceivedFine receivedFine : receivedFines) {
            fineCount += receivedFine.getCount();
        }
        return fineCount;
    }

    public int calculateDaysToBirthday() {
        Date date = new Date();
        return date.calculateDaysToBirthday(dateOfBirth);
    }

    /**
     * pou??it?? pro jeden z??pas, nutn?? pou????t na hr????e co je v z??pase
     *
     * @return po??et pokut ud??len??ch hr????i
     */
    public int returnNumberOfReceviedFine(ReceivedFine fine) {
        for (ReceivedFine receivedFine : receivedFines) {
            if (fine.equals(receivedFine))
                return receivedFine.getCount();
        }
        return 0;
    }

    /**
     * pou??it?? pro jeden z??pas, nutn?? pou????t na hr????e co je v z??pase
     *
     * @return ????stku, kter?? tato pokuta st??la hr????e v z??pase
     */
    public int returnAmountOfReceviedFine(ReceivedFine fine) {
        for (ReceivedFine receivedFine : receivedFines) {
            if (fine.equals(receivedFine))
                return receivedFine.returnAmountOfAllFines();
        }
        return 0;
    }

    /**
     * pou??it?? pro jeden z??pas, nutn?? pou????t na hr????e co je v z??pase
     *
     * @return po??et pokut tohoto typu, kter?? padly v z??pase
     */
    public int returnNumberOfReceviedFine(Fine fine) {
        for (ReceivedFine receivedFine : receivedFines) {
            if (fine.equals(receivedFine))
                return receivedFine.getCount();
        }
        return 0;
    }

    /**
     * pou??it?? pro jeden z??pas, nutn?? pou????t na hr????e co je v z??pase
     *
     * @return ????stka za pokuty tohoto typu, kter?? padly v z??pase
     */
    public int returnAmountOfReceviedFine(Fine fine) {
        for (ReceivedFine receivedFine : receivedFines) {
            if (fine.equals(receivedFine))
                return receivedFine.returnAmountOfAllFines();
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
     * pou??it?? pro jeden z??pas, nutn?? pou????t na hr????e co je v z??pase
     *
     * @return Text pro ka??dou pokutu v??etn?? po??tu, n??zvu a ceny
     */
    public List<String> returnListOfFineInStringList() {
        List<String> returnText = new ArrayList<>();
        for (ReceivedFine receivedFine : returnReceivedFineWithCount()) {
            StringBuilder text = new StringBuilder(receivedFine.getCount() + " ");

            if (receivedFine.getCount() == 1) {
                text.append("pokuta ");
            }
            else if (receivedFine.getCount() < 5) {
                text.append("pokuty ");
            }
            else {
                text.append("pokut ");
            }
            text.append(receivedFine.getFine().getName()).append(" v celkov?? ????stce ").append(receivedFine.getFine().getAmount() * receivedFine.getCount() + " K??");
            returnText.add(text.toString());
        }
        return returnText;
    }

    /**
     * vr??t?? ????stku, kterou hr???? zaplatil na t??to konkr??tn?? pokut?? v konkr??tn??ch z??pasech
     *
     * @param matchList seznam z??pas??, ze kter??ch se to po????t??
     * @param fine      pokuta kterou zji????ujem
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
        return name;
    }
}
