package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 20;
    //private static final String MINE = "\uD83D\uDCA3";
    private static final String MINE = "B";
    //private static final String FLAG = "\uD83D\uDEA9";
    private static final String FLAG = "F";
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int score;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE+2);
        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                setCellValue(x, y, "");
            }
        }
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(5) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.SALMON);
            }
        }

        setCellValueEx(0, 21, Color.LIGHTSALMON, "SCO", Color.BLACK, 30);
        setCellValueEx(1, 21, Color.LIGHTSALMON, "RE:", Color.BLACK, 30);
        setCellNumber(2, 21, score);
        setCellValueEx(4, 21, Color.LIGHTSALMON, "BOM", Color.BLACK, 30);
        setCellValueEx(5, 21, Color.LIGHTSALMON, "BS:", Color.BLACK, 30);
        setCellNumber(6, 21, countMinesOnField);
        setCellValueEx(8, 21, Color.LIGHTSALMON, "FLA", Color.BLACK, 30);
        setCellValueEx(9, 21, Color.LIGHTSALMON, "GS:", Color.BLACK, 30);

        countMineNeighbors();
        countFlags = countMinesOnField;

        setCellNumber(10, 21, countFlags);
    }

    private void countMineNeighbors(){
        for(int i = 0; i < SIDE; i++){
            for(int j = 0; j < SIDE; j++){
                if(!gameField[i][j].isMine) {
                    List<GameObject> result = getNeighbors(gameField[i][j]);
                    for(int s = 0; s < result.size(); s++) {
                        if(result.get(s).isMine) {
                            gameField[i][j].countMineNeighbors++;
                        }
                    }
                }
            }
        }
    }

    private void openTile(int x, int y){
        if(gameField[y][x].isOpen || gameField[y][x].isFlag || isGameStopped){
            return;
        }
        else{
            gameField[y][x].isOpen = true;
            countClosedTiles--;
            setCellColor(x, y, Color.LIGHTSEAGREEN);
            if(gameField[y][x].isMine){
                setCellValueEx(x, y, Color.RED, MINE);
                gameOver();
            }
            else if(countClosedTiles == countMinesOnField){
                win();
            }
            else if(!gameField[y][x].isMine && gameField[y][x].countMineNeighbors == 0){
                score += 5;
                setScore(score);
                setCellNumber(2, 21, score);
                setCellValue(x, y, "");
                List<GameObject> result = getNeighbors(gameField[y][x]);
                for(int i = 0; i < result.size(); i++){
                    if(result.get(i).countMineNeighbors == 0 && !(result.get(i).isOpen)){
                        openTile(result.get(i).x, result.get(i).y);
                    }
                    else if(!(result.get(i).isOpen)){
                        openTile(result.get(i).x, result.get(i).y);
                    }
                }
            }
            else{
                score += 5;
                setScore(score);
                setCellNumber(2, 21, score);
                setCellNumber(x, y, gameField[y][x].countMineNeighbors);
            }
        }
    }

    private void markTile(int x, int y){
        if(!isGameStopped && !gameField[y][x].isOpen && countFlags > 0 && !gameField[y][x].isFlag){
            gameField[y][x].isFlag = true;
            countFlags--;
            setCellValue(x, y, FLAG);
            setCellColor(x, y, Color.YELLOW);
            setCellNumber(10, 21, countFlags);
        }
        else if(!isGameStopped && !gameField[y][x].isOpen && gameField[y][x].isFlag){
            gameField[y][x].isFlag = false;
            countFlags++;
            setCellValue(x, y, "");
            setCellColor(x, y, Color.SALMON);
            setCellNumber(10, 21, countFlags);
        }
    }

    public void onMouseLeftClick(int x, int y){
        if(isGameStopped){
            restart();
            return;
        }
        openTile(x, y);
    }
    public void onMouseRightClick(int x, int y){
        markTile(x, y);
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void restart(){
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        countMinesOnField = 0;
        setScore(score);
        createGame();
    }

    private void win(){
        isGameStopped = true;
        showMessageDialog(Color.GOLD, "LUCKY YOU! Score: " + score, Color.BLANCHEDALMOND, 36);
    }

    private void gameOver(){
        isGameStopped = true;
        showMessageDialog(Color.AQUA, "TRY AGAIN ;)", Color.CORAL, 36);
    }
}