package game2048;

import java.util.Arrays;
import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author Jiasheng Xiao
 */
class Model extends Observable {

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to _board[c][r].  Be careful! This is not the usual 2D matrix
     * numbering, where rows are numbered from the top, and the row
     * number is the *first* index. Rather it works like (x, y) coordinates.
     */

    /** Largest piece value. */
    static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    Model(int size) {
        _board = new Tile[size][size];
        _score = _maxScore = 0;
        _gameOver = false;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there. */
    Tile tile(int col, int row) {
        return _board[col][row];
    }

    /** Return the number of squares on one side of the board. */
    int size() {
        return _board.length;
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    boolean gameOver() {
        return _gameOver;
    }

    /** Return the current score. */
    int score() {
        return _score;
    }

    /** Return the current maximum game score (updated at end of game). */
    int maxScore() {
        return _maxScore;
    }

    /** Clear the board to empty and reset the score. */
    void clear() {
        _score = 0;
        _gameOver = false;
        for (Tile[] column : _board) {
            Arrays.fill(column, null);
        }
        setChanged();
    }

    /** Add TILE to the board.  There must be no Tile currently at the
     *  same position. */
    void addTile(Tile tile) {
        assert _board[tile.col()][tile.row()] == null;
        _board[tile.col()][tile.row()] = tile;
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board. */
    boolean tilt(Side side) {
        boolean changed = edgemerge(side);
        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    /** This is used to merge the entire board towards the @param side
     * and @return tells if the board changed.**/
    boolean edgemerge(Side side) {
        boolean changed = false;
        int col = 0;
        while (col < size()) {
            int counter = size() - 1;
            int row = size() - 2;
            while (row >= 0) {
                if (vtile(col, row, side) != null) {
                    if (vtile(col, counter, side) == null) {
                        setVtile(col, counter, side, vtile(col, row, side));
                        changed = true;
                    } else {
                        if (vtile(col, counter, side).value()
                                == vtile(col, row, side).value()) {
                            setVtile(col, counter, side, vtile(col, row, side));
                            changed = true;
                            _score += vtile(col, counter, side).value();
                            counter--;
                        } else {
                            counter--;
                            if (counter != row) {
                                setVtile(col, counter, side,
                                        vtile(col, row, side));
                                changed = true;
                            }
                        }
                    }
                }
                row--;
            }
            col++;
        }
        return changed;
    }

    /** Return the current Tile at (COL, ROW), when sitting with the board
     *  oriented so that SIDE is at the top (farthest) from you. */
    private Tile vtile(int col, int row, Side side) {
        return _board[side.col(col, row, size())][side.row(col, row, size())];
    }

    /** Move TILE to (COL, ROW), merging with any tile already there,
     *  where (COL, ROW) is as seen when sitting with the board oriented
     *  so that SIDE is at the top (farthest) from you. */
    private void setVtile(int col, int row, Side side, Tile tile) {
        int pcol = side.col(col, row, size()),
            prow = side.row(col, row, size());
        if (tile.col() == pcol && tile.row() == prow) {
            return;
        }
        Tile tile1 = vtile(col, row, side);
        _board[tile.col()][tile.row()] = null;

        if (tile1 == null) {
            _board[pcol][prow] = tile.move(pcol, prow);
        } else {
            _board[pcol][prow] = tile.merge(pcol, prow, tile1);
        }
    }

    /** @return returns whether game is over and update _gameOver and _maxScore
     *  accordingly. */
    private boolean isFilled() {
        int col = 0;
        while (col < size()) {
            int row = 0;
            while (row < size()) {
                if (tile(col, row) == null) {
                    return false;
                }
                row++;
            }
            col++;
        }
        return true;
    }
    /** @return returns if there are any
     * piece worth the MAX_PIECE on the board.*/
    private boolean maxed() {
        int col = 0;
        while (col < size()) {
            int row = 0;
            while (row < size()) {
                if (tile(col, row) != null
                        &&
                        tile(col, row).value() == MAX_PIECE) {
                    return true;
                }
                row++;
            }
            col++;
        }
        return false;
    }
    /** @return returns if any block on the board could merge
     * with any of the blocks next to each of them.**/
    private boolean canMerge() {
        int col = 0;
        while (col < size()) {
            int row = 0;
            while (row < size()) {
                Tile current = tile(col, row);
                if (current != null) {
                    if (row + 1 < size()) {
                        Tile up = tile(col, row + 1);
                        if (up != null && current.value() == up.value()) {
                            return true;
                        }
                    }
                    if (row - 1 >= 0) {
                        Tile down = tile(col, row - 1);
                        if (down != null && current.value() == down.value()) {
                            return true;
                        }
                    }
                    if (col - 1 >= 0) {
                        Tile left = tile(col - 1, row);
                        if (left != null && current.value() == left.value()) {
                            return true;
                        }
                    }
                    if (col + 1 < size()) {
                        Tile right = tile(col + 1, row);
                        if (right != null && current.value() == right.value()) {
                            return true;
                        }
                    }
                }
                row++;
            }
            col++;
        }
        return false;
    }

    /** This checks if the game is over.**/
    private void checkGameOver() {
        if ((isFilled() && !canMerge()) || maxed()) {
            _gameOver = true;
        } else {
            _gameOver = false;
        }
        if (_gameOver && _score > _maxScore) {
            _maxScore = _score;
        }
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("[%n");
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
        out.format("] %d (max: %d)", score(), maxScore());
        return out.toString();
    }

    /** Current contents of the board. */
    private Tile[][] _board;
    /** Current score. */
    private int _score;
    /** Maximum score so far.  Updated when game ends. */
    private int _maxScore;
    /** True iff game is ended. */
    private boolean _gameOver;

}
