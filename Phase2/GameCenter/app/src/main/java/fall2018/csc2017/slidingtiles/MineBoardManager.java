package fall2018.csc2017.slidingtiles;

import java.io.Serializable;
import java.util.List;

public class MineBoardManager extends BoardManager<MineBoard, MineTile>{
    private boolean isFirst = true;
    private boolean lost = false;
    private List<MineTile> minePosition;

    MineBoardManager(){}

    MineBoardManager(int d, int numOfMine) {
        super(d);
        setBoard(new BuilderBoard().setMine(numOfMine).setMineLeft(numOfMine).setDimension(getDimension()).setMineTiles().buildMineBoard());
        setTiles(getBoard().getTiles());
        setDimension(getDimension());
        minePosition = getBoard().getMinePosition();
        setUpBoard();
    }

    MineBoardManager(int d, int mineNum, int mineLeft, double time, List<MineTile> tiles) {
        super(d);
        setBoard(new BuilderBoard().setMine(mineNum).setMineLeft(mineLeft).setDimension(getDimension()).setMineTiles(tiles).buildMineBoard());
        isFirst = false;
        setDimension(getDimension());
        setTime(time);
        minePosition = getBoard().getMinePosition();
    }

    List<MineTile> getMinePosition() {
        return minePosition;
    }

     void setUpBoard() {
        lost = false;
        isFirst = true;
    }

    void mark(int position) {
        getBoard().flag(position);
    }

    void setMinePosition(List<MineTile> minePosition) {
        this.minePosition = minePosition;
    }

    private void setNumbers() {
        for (int pos = 0; pos < getDimension() *getDimension() ; pos++) {
            //if the tile is a mine, set the number to -1.
            if (getTiles().get(pos).isMine()) {
                getTiles().get(pos).setNumber(-1);
            }

            //if the tile isn't a mine, set the number to be the number of mines surrounds it.
            else {
                int count = 0;
                int i = pos / getDimension() ;
                int j = pos % getDimension() ;

                if (i > 0 && j > 0 && getTiles().get(pos-getDimension() -1).isMine()) count++; //upper-left tile
                if (j > 0 && getTiles().get(pos-1).isMine()) count++; //left tile
                if (i < getDimension()  - 1 && j > 0 && getTiles().get(pos+getDimension() -1).isMine())
                    count++; //lower-left
                if (i > 0 && getTiles().get(pos-getDimension() ).isMine()) count++; // upper tile
                if (i < getDimension()  - 1 && getTiles().get(pos+getDimension() ).isMine()) count++; // lower tile
                if (i > 0 && j < getDimension()  - 1 && getTiles().get(pos-getDimension() +1).isMine())
                    count++; //upper-right
                if (j < getDimension()  - 1 && getTiles().get(pos+1).isMine()) count++; //right tile
                if (i < getDimension()  - 1 && j < getDimension()  - 1 && getTiles().get(pos+getDimension() +1).isMine())
                    count++; //lower-right tile
                getTiles().get(pos).setNumber(count);
            }
        }
    }

    @Override
    void move(Object position) {
        int po = (int) position;
        if (isFirst) {
            getBoard().setMines(po);
            setNumbers();
            isFirst = false;
        }
        MineTile currTile = getBoard().getTile(po);
        if (!currTile.isFlagged()) {
            getBoard().reveal(po);
            if (currTile.isMine()) {
                lost = true;
            }
        }
    }

    @Override
    boolean isLost() {
        if (lost){
            getBoard().showMines();
        }
        return lost;
    }

    @Override
    boolean isWon() {
        for (int i = 0; i < getDimension()  * getDimension() ; i++){
            if (getBoard().getTile(i).isObscured() && !getBoard().getTile(i).isMine()){
                return false;
            }
        }
        return true;
    }
}
