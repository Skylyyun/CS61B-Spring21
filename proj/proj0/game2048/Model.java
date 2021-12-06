package game2048;

import java.beans.AppletInitializer;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Observable;


/**
 * The state of a game of 2048.
 *
 * @author TODO: YOUR NAME HERE
 */
public class Model extends Observable {
    /**
     * Current contents of the board.
     */
    private Board board;
    /**
     * Current score.
     */
    private int score;
    /**
     * Maximum score so far.  Updated when game ends.
     */
    private int maxScore;
    /**
     * True iff game is ended.
     */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /**
     * Largest piece value.
     */
    public static final int MAX_PIECE = 2048;

    /**
     * A new 2048 game on a board of size SIZE with no pieces
     * and score 0.
     */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /**
     * A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes.
     */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /**
     * Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     * 0 <= COL < size(). Returns null if there is no tile there.
     * Used for testing. Should be deprecated and removed.
     */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /**
     * Return the number of squares on one side of the board.
     * Used for testing. Should be deprecated and removed.
     */
    public int size() {
        return board.size();
    }

    /**
     * Return true iff the game is over (there are no moves, or
     * there is a tile with value 2048 on the board).
     */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /**
     * Return the current score.
     */
    public int score() {
        return score;
    }

    /**
     * Return the current maximum game score (updated at end of game).
     */
    public int maxScore() {
        return maxScore;
    }

    /**
     * Clear the board to empty and reset the score.
     */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /**
     * Add TILE to the board. There must be no Tile currently at the
     * same position.
     */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /**
     * Tilt the board toward SIDE. Return true iff this changes the board.
     * <p>
     * 1. If two Tile objects are adjacent in the direction of motion and have
     * the same value, they are merged into one Tile of twice the original
     * value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     * tilt. So each move, every tile will only ever be part of at most one
     * merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     * value, then the leading two tiles in the direction of motion merge,
     * and the trailing tile does not.
     */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;
        if (side != Side.NORTH) {
            if (side == Side.WEST) {
                board.setViewingPerspective(Side.WEST);
            } else if (side == Side.EAST) {
                board.setViewingPerspective(Side.EAST);
            } else if (side == Side.SOUTH) {
                board.setViewingPerspective(Side.SOUTH);
            }
            changed = tileOfNorth(false);
            board.setViewingPerspective(Side.NORTH);

        } else {
            changed = tileOfNorth(false);
        }


        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    public boolean tileOfNorth(boolean changed) {
        int numOfTile = 0;
        boolean tempChanged = false;
        for (int col = 0; col < board.size(); col++) {
            ArrayList<Tile> tiles = tilesInOneCol(col);
            numOfTile = tiles.size();
            //只要有一个changed, 那么就changed
            switch (numOfTile) {
                case 0:
                    break;
                case 1:
                    tempChanged = oneTile(false, tiles, col);
                    break;
                case 2:
                    tempChanged = twoTile(false, tiles, col);
                    break;
                case 3:
                    tempChanged = threeTile(false, tiles, col);
                    break;
                case 4:
                    tempChanged = fourTile(false, tiles, col);
                    break;
                default:
                    System.out.println("ERROR!");
                    break;
            }
            if (tempChanged) {
                changed = true;
            }
        }
        return changed;
    }

    public boolean oneTile(boolean changed, ArrayList<Tile> tiles, int col) {
        Tile onlyTile = tiles.get(0);
        if (board.tile(col, 3) == null) {
            board.move(col, 3, onlyTile);
            changed = true;
        }
        return changed;
    }

    public boolean twoTile(boolean changed, ArrayList<Tile> tiles, int col) {
        Tile upTile = tiles.get(0);
        Tile downTile = tiles.get(1);

        // if two tiles value equal
        if (upTile.value() == downTile.value()) {
            if (board.tile(col, 3) == null) {
                board.move(col, 3, upTile);
            }
            board.move(col, 3, downTile);
            changed = true;
            score += board.tile(col, 3).value();
        }

        //if two tiles value not equal
        else {
            if (board.tile(col, 3) == null) {
                board.move(col, 3, upTile);
                changed = true;
            }
            if (board.tile(col,2) == null) {
                board.move(col, 2, downTile);
                changed = true;
            }
        }
        return changed;
    }

    public boolean threeTile(boolean changed, ArrayList<Tile> tiles, int col) {
        Tile upTile = tiles.get(0);
        Tile midTile = tiles.get(1);
        Tile downTile = tiles.get(2);

        // 1. no merge will happen
        if (midTile.value() != upTile.value() &&
                midTile.value() != downTile.value()) {
            if (board.tile(col,3) == null) {
                board.move(col, 3, upTile);
                changed = true;
            }
            if (board.tile(col,2) == null) {
                board.move(col, 2, midTile);
                changed = true;
            }
            if (board.tile(col,1) == null) {
                board.move(col, 1, downTile);
                changed = true;
            }
        }

        //2. up two tiles merge
        else if (upTile.value() == midTile.value()) {
            if (board.tile(col,3) == null) {
                board.move(col, 3, upTile);
            }
            board.move(col, 3, midTile);
            board.move(col, 2, downTile);
            changed = true;
            score += board.tile(col, 3).value();
        }

        //3. down two tiles merge
        else if (midTile.value() == downTile.value()) {
            if (board.tile(col,3) == null) {
                board.move(col, 3, upTile);
            }
            if (board.tile(col,2) == null) {
                board.move(col, 2, midTile);
            }
            board.move(col, 2, downTile);
            changed = true;
            score += board.tile(col, 2).value();
        }

        return changed;
    }

    public boolean fourTile(boolean changed, ArrayList<Tile> tiles, int col) {
        Tile upTile = tiles.get(0);
        Tile secondTile = tiles.get(1);
        Tile thirdTile = tiles.get(2);
        Tile downTile = tiles.get(3);

        //1. no tile will merge
        if (upTile.value() != secondTile.value() &&
                secondTile.value() != thirdTile.value() &&
                thirdTile.value() != downTile.value()) {
            changed = false;
        }

        //2. up two tiles merge
        else if (upTile.value() == secondTile.value() &&
                thirdTile.value() != downTile.value()) {
            board.move(col, 3, secondTile);
            board.move(col, 2, thirdTile);
            board.move(col, 1, downTile);
            changed = true;
            score += board.tile(col, 3).value();
        }

        //3.two merge
        else if (upTile.value() == secondTile.value() &&
                thirdTile.value() == downTile.value()) {
            board.move(col, 3, secondTile);
            board.move(col, 2, thirdTile);
            board.move(col, 2, downTile);
            changed = true;
            score = score + board.tile(col, 3).value() +
                    board.tile(col, 2).value();
        }

        //4. mid two tiles merge
        else if (secondTile.value() == thirdTile.value()) {
            board.move(col, 2, thirdTile);
            board.move(col, 1, downTile);
            changed = true;
            score += board.tile(col, 2).value();
        }

        //5. down two tiles merge
        else if (thirdTile.value() == downTile.value()) {
            board.move(col, 1, downTile);
            changed = true;
            score += board.tile(col, 1).value();
        }

        return changed;
    }

    // return the ArrayList of tile in a col, from top to down
    public ArrayList<Tile> tilesInOneCol(int col) {
        ArrayList<Tile> tiles = new ArrayList<>();
        for (int row = board.size() - 1; row >= 0; row--) {
            Tile t = board.tile(col, row);
            if (t != null) {
                tiles.add(t);
            }
        }
        return tiles;
    }

    /**
     * Checks if the game is over and sets the gameOver variable
     * appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /**
     * Determine whether game is over.
     */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /**
     * Returns true if at least one space on the Board is empty.
     * Empty spaces are stored as null.
     */
    public static boolean emptySpaceExists(Board b) {
        for (int col = 0; col < b.size(); col++) {
            for (int row = 0; row < b.size(); row++) {
                if (b.tile(col, row) == null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        for (int col = 0; col < b.size(); col++) {
            for (int row = 0; row < b.size(); row++) {
                if (b.tile(col, row) == null) {
                    continue;
                }
                if (b.tile(col, row).value() == MAX_PIECE) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        final int INVALUABLE = 0;
        // if board has empty space, return true.
        if (emptySpaceExists(b)) {
            return true;
        }
        // if board has no empty space.
        for (int col = 0; col < b.size(); col++) {
            for (int row = 0; row < b.size(); row++) {
                int currentValue = b.tile(col, row).value();
                int rightValue = col == b.size() - 1 ? INVALUABLE :
                        b.tile(col + 1, row).value();
                int upValue = row == b.size() - 1 ? INVALUABLE : b.tile(col,
                        row + 1).value();
                if (currentValue == rightValue || currentValue == upValue) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    /** Returns the model as a string, used for debugging. */ public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */ public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */ public int hashCode() {
        return toString().hashCode();
    }
}
