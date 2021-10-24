package com.jumbo.trus.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.jumbo.trus.R;
import com.jumbo.trus.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BeerLayout extends View {

    private static final String TAG = "BeerLayout";
    public static final int BEER_LIMIT = 30;
    public static final int LIQUOR_LIMIT = 20;

    private Paint paint;
    private Paint backroundPaint;
    private int x1;
    private int x2;
    private int y1;
    private int y2;
    private Random random;
    private List<Player> playerList;
    private List<PlayerLines> playerLinesList;
    private int playerIndex;
    private List<PointF> listOfPoints;
    private int inte = 0;
    boolean animated;
    private OnLineFinishedListener onLineFinishedListener;
    private Drawable liquorImage;
    private boolean liquerDraw;

    public BeerLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.WHITE);
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.ROUND);
        listOfPoints = new ArrayList<>();
        random = new Random();
        initLiquorImage();
        liquerDraw = false;
        initBorders();
    }

    private void initBorders() {
        backroundPaint = new Paint();
        backroundPaint.setColor( Color.BLACK );
        backroundPaint.setStrokeWidth( 5.5f );
        backroundPaint.setStyle( Paint.Style.STROKE );
    }

    /** nutno tuto metodu zavolat před jakoukoliv další akcí.
     * Metoda vezme seznam hráčů a počet jeho piv a body zapíše do instance třídy PlayerLines
     * @param playerList seznam hráčů, kteří budou mít uložená piva
     */
    public void loadPlayers (List<Player> playerList) {
        this.playerList = playerList;
        playerLinesList = new ArrayList<>();
        for (int i = 0; i < playerList.size(); i++) {
            playerLinesList.add(new PlayerLines());
        }
        for (int i = 0; i < playerList.size(); i++) {
            for (int j = 0; j < playerList.get(i).getNumberOfBeers(); j++) {
                calculateBeerLinePosition(j);
                playerLinesList.get(i).addAllBeerPositions(x1, x2, y1, y2);
            }
            for (int j = 0; j < playerList.get(i).getNumberOfLiquors(); j++) {
                calculateLiquorLinePosition(j);
                playerLinesList.get(i).addAllLiquorPositions(x1, x2, y1, y2);
                playerLinesList.get(i).setLiquorImage(true);
            }
        }
    }

    public void attachListener(OnLineFinishedListener onLineFinishedListener) {
        this.onLineFinishedListener = onLineFinishedListener;
    }


    private void initLiquorImage() {
        liquorImage = getResources().getDrawable(R.drawable.tvrdej, null);
        liquorImage.setBounds(5,700, 270, 895);
    }

    /** vyhledá uloženého hráče z metody loadPlayers a zadá vykreslení jeho piv
     * @param player hráč, kterého se vykreslení všech jeho piv týká
     */
    public void drawBeers(Player player) {
        Log.d(TAG, "draw: ");
        onLineFinishedListener.drawFinished(false);
        playerIndex = playerList.indexOf(player);
        invalidate();
    }
    /** přidá lajnu piva (body) do instance třídy PlayerLines a vykreslí výsledek
     * @param player hráč, kterého se přidání piva týká
     */
    public void addBeer(Player player) {
        calculateBeerLinePosition(player.getNumberOfBeers()-1);
        onLineFinishedListener.drawFinished(false);
        playerIndex = playerList.indexOf(player);
        divideLineIntoEqualParts();
        animated = true;
        liquerDraw = false;
        invalidate();
        onLineFinishedListener.drawFinished(false);
    }

    /** odebere lajnu piva z instance třídy PlayerLines a vykreslí výsledek
     * @param player hráč, kterého se odebrání piva týká
     */
    public void removeBeer(Player player) {
        playerIndex = playerList.indexOf(player);
        playerLinesList.get(playerIndex).removeLastBeerPosition();
        drawBeers(player);
    }

    /** přidá lajnu tvrdýho (body) do instance třídy PlayerLines a vykreslí výsledek
     * @param player hráč, kterého se přidání piva týká
     */
    public void addLiquor(Player player) {
        calculateLiquorLinePosition(player.getNumberOfLiquors()-1);
        onLineFinishedListener.drawFinished(false);
        playerIndex = playerList.indexOf(player);
        divideLineIntoEqualParts();
        animated = true;
        liquerDraw = true;
        invalidate();
        onLineFinishedListener.drawFinished(false);
    }

    /** odebere lajnu tvrdýho z instance třídy PlayerLines a vykreslí výsledek
     * @param player hráč, kterého se odebrání piva týká
     */
    public void removeLiquor(Player player) {
        playerIndex = playerList.indexOf(player);
        playerLinesList.get(playerIndex).removeLastLiquorPosition();
        drawBeers(player);
    }

    /**přidá text, který značí čárkování kořalek ("tvrdej")
     * @param player player hráč, kterého se přidání textu týká
     */
    public void drawLiquorText(Player player) {
        playerIndex = playerList.indexOf(player);
        playerLinesList.get(playerIndex).setLiquorImage(true);
        invalidate();
    }

    /**odebere text, který značí čárkování kořalek ("tvrdej")
     * @param player player hráč, kterého se odebrání textu týká
     */
    public void removeLiquorText(Player player) {
        playerIndex = playerList.indexOf(player);
        playerLinesList.get(playerIndex).setLiquorImage(false);
        invalidate();
    }

    /**
     * vypočítá pozice čáry dalšího přidaného piva
     * @param i kolikáté pivo hráče to je
     */
    private void calculateBeerLinePosition(int i) {
        if (i == 4 || i == 9 || i == 14) {
            y1 = 175 + random.nextInt(100)-50;
            y2 = 175 + random.nextInt(100)-50;
        }
        else if (i == 19 || i == 24 || i == 29) {
            y1 = 475 + random.nextInt(100)-50;
            y2 = 475 + random.nextInt(100)-50;
        }
        else if (i < 15) {
            y1 = 50 + random.nextInt(80)-40;
            y2 = 300 + random.nextInt(80)-40;
        }
        else {
            y1 = 350 + random.nextInt(60)-30;
            y2 = 600 + random.nextInt(60)-30;
        }
        if (i == 4 || i == 19) {
            x1 = 50 + random.nextInt(60)-30;
            x2 = 240 + random.nextInt(60)-30;
        }
        else if (i == 9 || i == 24) {
            x1 = 250 + random.nextInt(60)-30;
            x2 = 440 + random.nextInt(60)-30;
        }
        else if (i == 14 || i == 29) {
            x1 = 450 + random.nextInt(60)-30;
            x2 = 640 + random.nextInt(60)-30;
        }
        else {
            if (i >= 15) {
                i=i-15;
            }
            x1 = 100 + i * 40 + random.nextInt(20) - 10;
            x2 = 100 + i * 40 + random.nextInt(20) - 10;
        }
    }

    /**
     * vypočítá pozice čáry dalšího přidaného piva
     * @param i kolikáté pivo hráče to je
     */
    private void calculateLiquorLinePosition(int i) {
        if (i == 4 || i == 9 || i == 14 || i == 19) {
            y1 = 800 + random.nextInt(80)-40;
            y2 = 800 + random.nextInt(80)-40;
        }
        else {
            y1 = 730 + random.nextInt(80)-40;
            y2 = 870 + random.nextInt(80)-40;
        }
        if (i == 4) {
            x1 = 300 + random.nextInt(40)-20;
            x2 = 410 + random.nextInt(40)-20;
        }
        else if (i == 9) {
            x1 = 450 + random.nextInt(40)-20;
            x2 = 560 + random.nextInt(40)-20;
        }
        else if (i == 14) {
            x1 = 600 + random.nextInt(40)-20;
            x2 = 710 + random.nextInt(40)-20;
        }
        else if (i == 19) {
            x1 = 750 + random.nextInt(40)-20;
            x2 = 860 + random.nextInt(40)-20;
        }
        else {
            x1 = 300 + i * 30 + random.nextInt(20) - 10;
            x2 = 300 + i * 30 + random.nextInt(20) - 10;
        }
    }


    /**
     * rozkouskuje čáru pro nakreslení na dílčích 50 kusů, které se pak budou poatupně kreslit
     */
    private void divideLineIntoEqualParts() {

        listOfPoints.clear();
        int pixelsNumber = 20;
        for (int k = 1; k <= pixelsNumber; k++) {
            listOfPoints.add(new PointF(x1 + ((k * (x2 - x1)) / pixelsNumber),y1 + (k * (y2 - y1)) / pixelsNumber));
        }

        Log.d("listOfPoints : size : ",listOfPoints.size()+", x1: " + x1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getWidth(), getHeight(), backroundPaint);
        PlayerLines playerLines = playerLinesList.get(playerIndex);
        for (int i = 0; i < playerLines.getX1().size(); i++) {
            canvas.drawLine(playerLines.getX1().get(i), playerLines.getY1().get(i), playerLines.getX2().get(i), playerLines.getY2().get(i), paint);
        }
        for (int i = 0; i < playerLines.getLiquor_x1().size(); i++) {
            canvas.drawLine(playerLines.getLiquor_x1().get(i), playerLines.getLiquor_y1().get(i), playerLines.getLiquor_x2().get(i), playerLines.getLiquor_y2().get(i), paint);
        }

        if(inte < listOfPoints.size() && animated){
            canvas.drawLine(listOfPoints.get(0).x, listOfPoints.get(0).y, listOfPoints.get(inte).x,listOfPoints.get(inte).y, paint);
            inte++;

            if(inte < listOfPoints.size()){
                invalidate();
            }
            else {
                animated = false;
                inte = 0;
                if (liquerDraw) {
                    playerLinesList.get(playerIndex).addAllLiquorPositions(x1, x2, y1, y2);
                }
                else {
                    playerLinesList.get(playerIndex).addAllBeerPositions(x1, x2, y1, y2);
                }
                onLineFinishedListener.drawFinished(true);
            }
        }
        else {
            onLineFinishedListener.drawFinished(true);
        }
        //text tvrdýho
         if (playerLines.isLiquorImage()) {
            liquorImage.draw(canvas);
            //onLineFinishedListener.drawFinished(true);
        }
    }
}
