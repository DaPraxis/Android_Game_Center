package fall2018.csc2017.slidingtiles;

import java.util.ArrayList;
import java.util.List;

public class MineMemory extends Memory {
    private int dimension;
    private int mine;
    private double timeMine;

    public int getDimension() {
        return dimension;
    }

    public int getMine() {
        return mine;
    }

    public double getTimeMine() {
        return timeMine;
    }

    public int getMineLeft() {
        return mineLeft;
    }

    public List<Boolean> getIsObscuredOfTiles() {
        return isObscuredOfTiles;
    }

    public List<Integer> getNumberOfTiles() {
        return numberOfTiles;
    }

    public List<Boolean> getIsMineOfTiles() {
        return isMineOfTiles;
    }

    public List<Boolean> getIsFlaggedOfTiles() {
        return isFlaggedOfTiles;
    }

    private int mineLeft;
    private List<Boolean> isObscuredOfTiles= new ArrayList<>();
    private List<Integer> numberOfTiles = new ArrayList<>();
    private List<Boolean> isMineOfTiles = new ArrayList<>();
    private List<Boolean> isFlaggedOfTiles = new ArrayList<>();

    public void makeCopy(MineBoardManager manager) {
        dimension = manager.getBoard().getDimension();
        mine = manager.getBoard().getMineNum();
        mineLeft = manager.getBoard().getMineLeft();
        timeMine = manager.getTime();
        for (Tile tile : manager.getBoard().getTiles()) {
            MineTile t = (MineTile) tile;
            isObscuredOfTiles.add(t.isObscured());
            numberOfTiles.add(t.getNumber());
            isMineOfTiles.add(t.isMine());
            isFlaggedOfTiles.add(t.isFlagged());
        }
    }

    public MineBoardManager copy() {
        List<MineTile> tiles = new ArrayList<>();
        for (int i = 0; i < dimension * dimension; i++) {
            tiles.add(new MineTile(isObscuredOfTiles.get(i), numberOfTiles.get(i), isMineOfTiles.get(i), isFlaggedOfTiles.get(i)));
        }
        Factory f = new Factory();
        return (MineBoardManager) f.loadMineManager(dimension, mine, mineLeft, timeMine, tiles);
    }
}
