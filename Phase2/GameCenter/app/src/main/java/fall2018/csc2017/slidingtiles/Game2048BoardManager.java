package fall2018.csc2017.slidingtiles;

import java.io.Serializable;
import java.util.List;

public class Game2048BoardManager extends BoardManager<Game2048Board, Game2048Tile> implements Serializable{
    final private Integer DIMENSION = 4;
//    private Double time;

    int getScore() {
        return getBoard().getScore();
    }

    void cheat() {
        getBoard().getTile(0,0).setValue(2048);
    }

    private boolean isFull() {
        return getBoard().findEmpty().size() == 0;
    }

    @Override
    boolean isLost() {
        return !isWon() && !canMove();
    }

    @Override
    boolean isWon(){
        for (Game2048Tile tile : getBoard()) {
            if (tile.getValue() == 2048) {
                return true;
            }
        }
        return false;
    }

    @Override
    void move(Object direction) {
        getBoard().merge((String) direction);
    }

    boolean canMove() {
        if (!isFull()) {
            return true;
        } else {
            for (int i = 0; i < DIMENSION; i++) {
                for (int j = 0; j < DIMENSION; j++) {
                    Game2048Tile tile = getBoard().getTile(i, j);
                    if ((i < DIMENSION - 1 && tile.equals(getBoard().getTile(i + 1, j)))
                            || (j < DIMENSION - 1 && tile.equals(getBoard().getTile(i, j+1)))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
