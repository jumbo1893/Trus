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
    private OnChangedPlayerListener onChangedPlayerListener;
    private Drawable liquorImage;
    private boolean liquerDraw;
    private int layoutHeight;
    private int layoutWidth;
    private boolean measured = false;

    public BeerLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.WHITE);
        paint = new Paint();
        listOfPoints = new ArrayList<>();
        random = new Random();
        initLiquorImage();
        liquerDraw = false;
        initBorders();
    }

    private void initBorders() {
        backroundPaint = new Paint();
        backroundPaint.setColor(Color.BLACK);
        backroundPaint.setStrokeWidth(5.5f);
        backroundPaint.setStyle(Paint.Style.STROKE);
    }

    private void initPaintParameters() {
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        Log.d(TAG, "initPaintParameters: " + layoutWidth / 100);
        paint.setStrokeWidth(layoutWidth / 100);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    /**
     * nutno tuto metodu zavolat před jakoukoliv další akcí.
     * Metoda vezme seznam hráčů a počet jeho piv a body zapíše do instance třídy PlayerLines
     *
     * @param playerList seznam hráčů, kteří budou mít uložená piva
     */
    public void loadPlayers(List<Player> playerList) {
        this.playerList = new ArrayList<>(playerList);
        Log.d(TAG, "loadPlayers: " + playerList.get(0).getNumberOfBeers());
        playerIndex = 0;
        liquerDraw = false;
        initPlayer();
    }

    private void initPlayer() {
        if (measured) {
            initPlayerLines();
            drawBeers();
        }
    }

    public List<Player> getPlayerList() {
        Log.d(TAG, "getPlayerList: " + playerList.get(playerIndex).getNumberOfBeers());
        return playerList;
    }

    public void setNextPlayer() {
        if (!(playerIndex == playerList.size() - 1)) {
            playerIndex++;
        } else {
            playerIndex = 0;
        }
        onChangedPlayerListener.playerChanged(playerList.get(playerIndex));
        drawBeers();
    }

    public void setPreviousPlayer() {
        if (playerIndex > 0) {
            playerIndex--;
        } else {
            playerIndex = playerList.size() - 1;
        }
        onChangedPlayerListener.playerChanged(playerList.get(playerIndex));
        drawBeers();
    }

    private void initPlayerLines() {
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

    public void attachOnLineFinishedListener(OnLineFinishedListener onLineFinishedListener) {
        this.onLineFinishedListener = onLineFinishedListener;
    }

    public void attachOnChangedPlayerListener(OnChangedPlayerListener onChangedPlayerListener) {
        this.onChangedPlayerListener = onChangedPlayerListener;
    }


    private void initLiquorImage() {
        liquorImage = getResources().getDrawable(R.drawable.tvrdej, null);

    }

    /**
     * vyhledá uloženého hráče z metody loadPlayers a zadá vykreslení jeho piv
     */
    private void drawBeers() {
        Log.d(TAG, "draw: ");
        onLineFinishedListener.drawFinished(false);
        invalidate();
    }

    public boolean addLine() {
        if (liquerDraw) {
            return addLiquor();
        }
        else {
            Log.d(TAG, "addLine: " + playerList.get(playerIndex).getNumberOfBeers());
            return addBeer();
        }
    }

    public void removeLine() {
        if (liquerDraw) {
            removeLiquor();
        }
        else {
            removeBeer();
        }
        Log.d(TAG, "removeLine: " + playerList.get(playerIndex).getNumberOfBeers());
    }

    /**
     * přidá lajnu piva (body) do instance třídy PlayerLines a vykreslí výsledek
     */
    public boolean addBeer() {
        if (playerList.get(playerIndex).getNumberOfBeers() < BeerLayout.BEER_LIMIT) {
            playerList.get(playerIndex).addBeer();
            calculateBeerLinePosition(playerList.get(playerIndex).getNumberOfBeers() - 1);
            onLineFinishedListener.drawFinished(false);
            divideLineIntoEqualParts();
            animated = true;
            liquerDraw = false;
            invalidate();
            onLineFinishedListener.drawFinished(false);
            return true;
        }
        return false;
    }

    /**
     * odebere lajnu piva z instance třídy PlayerLines a vykreslí výsledek
     */
    public void removeBeer() {
        playerLinesList.get(playerIndex).removeLastBeerPosition();
        playerList.get(playerIndex).removeBeer();
        drawBeers();
    }

    /**
     * přidá lajnu tvrdýho (body) do instance třídy PlayerLines a vykreslí výsledek
     */
    public boolean addLiquor() {
        if (playerList.get(playerIndex).getNumberOfLiquors() < BeerLayout.LIQUOR_LIMIT) {
            playerList.get(playerIndex).addLiquor();
            calculateLiquorLinePosition(playerList.get(playerIndex).getNumberOfLiquors() - 1);
            onLineFinishedListener.drawFinished(false);
            divideLineIntoEqualParts();
            animated = true;
            liquerDraw = true;
            invalidate();
            onLineFinishedListener.drawFinished(false);
            return true;
        }
        return false;
    }

    /**
     * odebere lajnu tvrdýho z instance třídy PlayerLines a vykreslí výsledek
     */
    public void removeLiquor() {
        playerLinesList.get(playerIndex).removeLastLiquorPosition();
        playerList.get(playerIndex).removeLiquor();
        drawBeers();
    }

    public String changeBooze() {
        String returnText;
        if (liquerDraw) {
            removeLiquorText();
            returnText = "Zpátky k pivku";
        }
        else {
            drawLiquorText();
            returnText = "Přitvrdíme";
        }
        liquerDraw = !liquerDraw;
        return returnText;
    }

    /**
     * přidá text, který značí čárkování kořalek ("tvrdej")
     *
     */
    public void drawLiquorText() {
        playerLinesList.get(playerIndex).setLiquorImage(true);
        invalidate();
    }

    /**
     * odebere text, který značí čárkování kořalek ("tvrdej")
     *
     */
    public void removeLiquorText() {
        if (playerList.get(playerIndex).getNumberOfLiquors() == 0) {
            playerLinesList.get(playerIndex).setLiquorImage(false);
            invalidate();
        }
    }

    /**
     * vypočítá pozice čáry dalšího přidaného piva
     *
     * @param i kolikáté pivo hráče to je
     */
    private void calculateBeerLinePosition(int i) {
        Log.d(TAG, "calculateBeerLinePosition: " + layoutHeight + " širka " + layoutWidth);
        if (i == 4 || i == 9 || i == 14) {
            y1 = (layoutHeight / 6) + random.nextInt(layoutHeight / 8) - layoutHeight / 16;
            y2 = (layoutHeight / 6) + random.nextInt(layoutHeight / 8) - layoutHeight / 16;
        } else if (i == 19 || i == 24 || i == 29) {
            y1 = (layoutHeight / 2) + random.nextInt(layoutHeight / 8) - layoutHeight / 16;
            y2 = (layoutHeight / 2) + 20 + random.nextInt(layoutHeight / 8) - layoutHeight / 16;
        } else if (i < 15) {
            y1 = random.nextInt(layoutHeight / 12);
            y2 = (layoutHeight / 3) + random.nextInt(layoutHeight / 12) - layoutHeight / 24;
        } else {
            y1 = (layoutHeight / 3) + random.nextInt(layoutHeight / 12);
            y2 = (layoutHeight / 3) * 2 + random.nextInt(layoutHeight / 12) - layoutHeight / 24;
        }
        if (i == 4 || i == 19) {
            x1 = random.nextInt(layoutWidth / 15);
            x2 = (layoutWidth / 15) * 5 + random.nextInt(layoutWidth / 15) - layoutWidth / 30;
        } else if (i == 9 || i == 24) {
            x1 = (layoutWidth / 15) * 5 + random.nextInt(layoutWidth / 15) - layoutWidth / 30;
            x2 = (layoutWidth / 15) * 10 + random.nextInt(layoutWidth / 15) - layoutWidth / 30;
        } else if (i == 14 || i == 29) {
            x1 = (layoutWidth / 15) * 10 + random.nextInt(layoutWidth / 15) - layoutWidth / 30;
            x2 = layoutWidth + random.nextInt(layoutWidth / 20) - layoutWidth / 20;
        } else {
            if (i >= 15) {
                i = i - 15;
            }
            x1 = layoutWidth / 15 + i * layoutWidth / 15 + random.nextInt(layoutWidth / 30);
            x2 = layoutWidth / 15 + i * layoutWidth / 15 + random.nextInt(layoutWidth / 30);
        }
    }

    /**
     * vypočítá pozice čáry dalšího přidaného tvrdýho
     *
     * @param i kolikátej tvrdej hráče to je
     */
    private void calculateLiquorLinePosition(int i) throws NullPointerException {
        if (i == 4 || i == 9 || i == 14 || i == 19) {
            y1 = (layoutHeight / 6) * 5 + random.nextInt(layoutHeight / 8) - layoutHeight / 16;
            y2 = (layoutHeight / 6) * 5 + random.nextInt(layoutHeight / 8) - layoutHeight / 16;
        } else {
            y1 = (layoutHeight / 3) * 2 + random.nextInt(layoutHeight / 12);
            y2 = layoutHeight + random.nextInt(layoutHeight / 12) - layoutHeight / 24;
        }
        if (i == 4) {
            x1 = layoutWidth / 3 + random.nextInt(layoutWidth / 30);
            x2 = (layoutWidth / 6) * 3 + random.nextInt(layoutWidth / 30);
        } else if (i == 9) {
            x1 = (layoutWidth / 6) * 3 + random.nextInt(layoutWidth / 30);
            x2 = (layoutWidth / 6) * 4 + random.nextInt(layoutWidth / 30);
        } else if (i == 14) {
            x1 = (layoutWidth / 6) * 4 + random.nextInt(layoutWidth / 30);
            x2 = (layoutWidth / 6) * 5 + random.nextInt(layoutWidth / 30);
        } else if (i == 19) {
            x1 = (layoutWidth / 6) * 5 + random.nextInt(layoutWidth / 30);
            x2 = layoutWidth + random.nextInt(layoutWidth / 20) - layoutWidth / 40;
        } else {
            x1 = (layoutWidth / 3) + layoutWidth / 20 + i * layoutWidth / 30 + random.nextInt(layoutWidth / 40);
            x2 = (layoutWidth / 3) + layoutWidth / 20 + i * layoutWidth / 30 + random.nextInt(layoutWidth / 40);
        }
    }


    /**
     * rozkouskuje čáru pro nakreslení na dílčích 50 kusů, které se pak budou poatupně kreslit
     */
    private void divideLineIntoEqualParts() {

        listOfPoints.clear();
        int pixelsNumber = 20;
        for (int k = 1; k <= pixelsNumber; k++) {
            listOfPoints.add(new PointF(x1 + ((k * (x2 - x1)) / pixelsNumber), y1 + (k * (y2 - y1)) / pixelsNumber));
        }

        Log.d("listOfPoints : size : ", listOfPoints.size() + ", x1: " + x1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //okraj
        canvas.drawRect(0, 0, getWidth(), getHeight(), backroundPaint);
        PlayerLines playerLines = playerLinesList.get(playerIndex);
        for (int i = 0; i < playerLines.getX1().size(); i++) {
            canvas.drawLine(playerLines.getX1().get(i), playerLines.getY1().get(i), playerLines.getX2().get(i), playerLines.getY2().get(i), paint);
        }
        for (int i = 0; i < playerLines.getLiquor_x1().size(); i++) {
            canvas.drawLine(playerLines.getLiquor_x1().get(i), playerLines.getLiquor_y1().get(i), playerLines.getLiquor_x2().get(i), playerLines.getLiquor_y2().get(i), paint);
        }

        if (inte < listOfPoints.size() && animated) {
            canvas.drawLine(listOfPoints.get(0).x, listOfPoints.get(0).y, listOfPoints.get(inte).x, listOfPoints.get(inte).y, paint);
            inte++;

            if (inte < listOfPoints.size()) {
                invalidate();
            } else {
                animated = false;
                inte = 0;
                if (liquerDraw) {
                    playerLinesList.get(playerIndex).addAllLiquorPositions(x1, x2, y1, y2);
                } else {
                    playerLinesList.get(playerIndex).addAllBeerPositions(x1, x2, y1, y2);
                }
                onLineFinishedListener.drawFinished(true);
            }
        } else {
            onLineFinishedListener.drawFinished(true);
        }
        //text tvrdýho
        if (playerLines.isLiquorImage()) {
            liquorImage.setBounds(5, (layoutHeight - 5) - (layoutWidth / 9) * 2, layoutWidth / 3, layoutHeight - 5);
            liquorImage.draw(canvas);
            //onLineFinishedListener.drawFinished(true);
        }
    }

    //než budem kreslit čáry musíme zjistit výšku a šířku
    //až to zjistíme můžeme kreslit čárky
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        layoutWidth = getMeasuredWidth();
        layoutHeight = getMeasuredHeight();
        Log.d(TAG, "onMeasure: w: " + getMeasuredWidth() + " h: " + getMeasuredHeight());
        initPaintParameters();
        measured = true;
        if (playerList != null) {
            initPlayer();
        }
    }
}
