package fall2018.csc2017.slidingtiles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;

public class MineBoard extends Observable implements Serializable {
    private int w, h;
    private int mine;
    private int mineLeft;
    private MineTile[][] tiles;
    private List<MineTile> minePosition = new ArrayList<>();

    MineBoard(int x, int y, int m) {
        mine = m;
        mineLeft = m;
        this.h = x;
        this.w = y;
        tiles = new MineTile[h][w];
    }

    int getW() {return w;}

    int getH() {return h;}

    int getMine() {return mine;}

    int getMineLeft() {return mineLeft;}

    List<MineTile> getMinePosition() {return minePosition;}

    MineTile[][] getTiles() {return tiles;}

    MineTile getTile(int position) {
        int row = position / w;
        int col = position % w;
        return tiles[row][col];
    }

    void setMines(int position) {
        int mine = getMine();
        List<MineTile> startNine = getSurround(position);
        startNine.add(getTile(position));
        Random r = new Random();
        List<Integer> randomNum = new ArrayList<>();
        int i = 0;
        while (i < mine) {
            Integer num = r.nextInt(w * h);

            if (!randomNum.contains(num) && !startNine.contains(getTile(num))) {
                randomNum.add(num);
                getTile(num).setMine();
                minePosition.add(getTile(num));
                i++;
            }
        }
    }

    void setTiles(){
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                MineTile tile = new MineTile();
                tiles[i][j] = tile;
                tile.setPosition((i * w + j));
                tile.setBackground();
            }
        }
    }

    void reveal(int position) {
        MineTile currTile = getTile(position);
        if (currTile.getNumber() == 0) {
            currTile.reveal();
            for (MineTile tile : getSurround(position)) {
                 if(tile.isObscured() && !tile.isFlagged()) {
                     tile.reveal();
                     if (tile.getNumber() == 0) {
                         reveal(tile.getPosition());
                     }
                 }
            }
        }
        else {
            currTile.reveal();
        }

        setChanged();
        notifyObservers();
    }

    void flag(int position) {
        getTile(position).flag();
        if (getTile(position).isFlagged()){mineLeft--;}
        else {mineLeft++;}

        setChanged();
        notifyObservers();
    }

    List<MineTile> getSurround(int position) {
        int row = position / w;
        int col = position % w;
        List<MineTile> surround = new ArrayList<>();

        if (row != 0 && col != 0) {
            surround.add(getTiles()[row - 1][col - 1]);
        }
        if (row != 0 && col != w - 1) {
            surround.add(getTiles()[row - 1][col + 1]);
        }
        if (row != 0) {
            surround.add(getTiles()[row - 1][col]);
        }
        if (row != h - 1 && col != 0) {
            surround.add(getTiles()[row + 1][col - 1]);
        }
        if (row != h - 1 && col != w - 1) {
            surround.add(getTiles()[row + 1][col + 1]);
        }
        if (row != h - 1) {
            surround.add(getTiles()[row + 1][col]);
        }
        if (col != 0) {
            surround.add(getTiles()[row][col - 1]);
        }
        if (col != w - 1) {
            surround.add(getTiles()[row][col + 1]);
        }
        return surround;
    }

    void showMines(){
        for (MineTile tile : minePosition){
            tile.showMine();
        }

        setChanged();
        notifyObservers();
    }
}