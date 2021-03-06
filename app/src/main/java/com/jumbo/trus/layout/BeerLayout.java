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
     * nutno tuto metodu zavolat p??ed jakoukoliv dal???? akc??.
     * Metoda vezme seznam hr?????? a po??et jeho piv a body zap????e do instance t????dy PlayerLines
     *
     * @param playerList seznam hr??????, kte???? budou m??t ulo??en?? piva
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
     * vyhled?? ulo??en??ho hr????e z metody loadPlayers a zad?? vykreslen?? jeho piv
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
     * p??id?? lajnu piva (body) do instance t????dy PlayerLines a vykresl?? v??sledek
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
     * odebere lajnu piva z instance t????dy PlayerLines a vykresl?? v??sledek
     */
    public void removeBeer() {
        playerLinesList.get(playerIndex).removeLastBeerPosition();
        playerList.get(playerIndex).removeBeer();
        drawBeers();
    }

    /**
     * p??id?? lajnu tvrd??ho (body) do instance t????dy PlayerLines a vykresl?? v??sledek
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
     * odebere lajnu tvrd??ho z instance t????dy PlayerLines a vykresl?? v??sledek
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
            returnText = "Zp??tky k pivku";
        }
        else {
            drawLiquorText();
            returnText = "P??itvrd??me";
        }
        liquerDraw = !liquerDraw;
        return returnText;
    }

    /**
     * p??id?? text, kter?? zna???? ????rkov??n?? ko??alek ("tvrdej")
     *
     */
    public void drawLiquorText() {
        playerLinesList.get(playerIndex).setLiquorImage(true);
        invalidate();
    }

    /**
     * odebere text, kter?? zna???? ????rkov??n?? ko??alek ("tvrdej")
     *
     */
    public void removeLiquorText() {
        if (playerList.get(playerIndex).getNumberOfLiquors() == 0) {
            playerLinesList.get(playerIndex).setLiquorImage(false);
            invalidate();
        }
    }

    /**
     * vypo????t?? pozice ????ry dal????ho p??idan??ho piva
     *
     * @param i kolik??t?? pivo hr????e to je
     */
    private void calculateBeerLinePosition(int i) {
        Log.d(TAG, "calculateBeerLinePosition: " + layoutHeight + " ??irka " + layoutWidth);
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
     * vypo????t?? pozice ????ry dal????ho p??idan??ho tvrd??ho
     *
     * @param i kolik??tej tvrdej hr????e to je
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
     * rozkouskuje ????ru pro nakreslen?? na d??l????ch 50 kus??, kter?? se pak budou poatupn?? kreslit
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
        //text tvrd??ho
        if (playerLines.isLiquorImage() || liquerDraw || playerLines.isLiquerLinesDrawed()) { //bu?? m?? ulo??eno liquerImage (= ji?? m?? z d????v??j??ka pan??ky || na p??edchoz??m hr????i se d??valy pan??ky, automaticky se po????t?? s t??m, ??e budou pokra??ovat || m?? na??m??ran?? pan??ky, kter?? je??t?? nejsou ulo??eny
            liquorImage.setBounds(5, (layoutHeight - 5) - (layoutWidth / 9) * 2, layoutWidth / 3, layoutHeight - 5);
            liquorImage.draw(canvas);
            //onLineFinishedListener.drawFinished(true);
        }
    }

    //ne?? budem kreslit ????ry mus??me zjistit v????ku a ??????ku
    //a?? to zjist??me m????eme kreslit ????rky
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
