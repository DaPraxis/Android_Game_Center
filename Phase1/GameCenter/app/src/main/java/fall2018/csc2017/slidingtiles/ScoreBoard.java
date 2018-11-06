package fall2018.csc2017.slidingtiles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The scoreBoard to display rankings.
 */
class ScoreBoard implements Serializable {
    private int scoreBoardSize;
    private List<Object[]> scoreList = new ArrayList<>();
    private SlidingTilesScoreStrategy scoreStrategy;
    private ScoreSorter scoreSorter;

    ScoreBoard(){
        setScoreBoardSize(10);
        setScoreSorter(new ScoreSorter());
        setScoreStrategy(new SlidingTilesScoreStrategy());
    }

    /**
     * Set the scoreBoardSize to size.
     * @param size the new size to set
     */
    void setScoreBoardSize(int size) {
        scoreBoardSize = size;
    }

    /**
     * Return a score calculated by using scoreStrategy.
     * @param boardManager the boardManager that is finished and to be calculated for score
     * @return a score calculated by using scoreStrategy
     */
    int calculateScore(BoardManager boardManager) {
        return scoreStrategy.calculateScore(boardManager);
    }

    /**
     * Set the scoreStrategy to strategy. Open to modification.
     * @param strategy the ScoreStrategy to set
     */
    void setScoreStrategy(SlidingTilesScoreStrategy strategy) {
        scoreStrategy = strategy;
    }

    /**
     * Set the scoreSorter to sorter. Open to modification.
     * @param sorter the Sorter to set
     */
    void setScoreSorter(ScoreSorter sorter) {
        scoreSorter = sorter;
    }

    /**
     * Add a new result to the scoreList and sort the list to get the new ranking.
     * @param scoreArray the new result to add
     */
    void addAndSort(Object[] scoreArray) {
        scoreList.add(scoreArray);
        scoreSorter.sort(scoreList);
        if (scoreList.size() == scoreBoardSize + 1) {
            scoreList.remove(scoreBoardSize);
        }
    }

    /**
     * Return the scoreList.
     * @return the scoreList
     */
    List<Object[]> getScoreList() {
        return scoreList;
    }

}


