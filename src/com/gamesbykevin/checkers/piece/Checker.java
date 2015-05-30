package com.gamesbykevin.checkers.piece;

import com.gamesbykevin.framework.base.Cell;

import com.gamesbykevin.checkers.board.Board;
import com.gamesbykevin.checkers.player.Player;

/**
 * This class represents a checker
 * @author GOD
 */
public final class Checker extends Cell
{
    //is this checker a king
    private boolean king = false;
    
    //the location of the piece
    private int x, y;
    
    //0 captures
    public static final int NO_CAPTURES = 0;
    
    public Checker(final int col, final int row)
    {
        this(col, row, false);
    }
    
    public Checker(final int col, final int row, final boolean king)
    {
        super();
        
        //assign location
        setCol(col);
        setRow(row);
        
        //assign this a king
        setKing(king);
    }

    /**
     * Is this location valid?<br>
     * We will check if the location is in bounds.<br>
     * We also make sure there are no other pieces existing at the location.<br>
     * We will also make sure we are heading in the correct direction.<br>
     * @param opponent The opponent we are facing
     * @param self The player we control
     * @param column The column we want to check
     * @param row The row we want to check
     * @return true if the location is available, false otherwise
     */
    public boolean isValidLocation(final Player opponent, final Player self, final int column, final int row)
    {
        //if not in bounds the location is not valid
        if (!Board.hasBoundary(column, row))
            return false;
        
        //make sure there are no pieces at this location
        if (opponent.hasPiece(column, row) || self.hasPiece(column, row))
            return false;
        
        //are we moving north 
        final boolean north = (row < this.getRow());
        
        //if this checker is not a king, make sure we can move in the direction
        if (!isKing())
        {
            //if we are to attack north, but aren't moving north, the move is invalid
            if (self.assignedNorth() && !north)
                return false;
            
            //if we are to attack south, but aren't moving south, the move is invalid
            if (!self.assignedNorth() && north)
                return false;
        }
        
        //the location is valid
        return true;
    }
    
    /**
     * Does the checker have a capture at this location?<br>
     * Here we will also take into consideration if the move is in bounds and legal.<br>
     * @param opponent The opponent we are facing
     * @param self The player we control
     * @param column The destination Column
     * @param row The destination Row
     * @return true if the checker can capture an opponent piece when placing at the destination (column, row), false otherwise
     */
    public boolean hasCapture(final Player opponent, final Player self, final double column, final double row)
    {
        return hasCapture(opponent, self, (int)column, (int)row);
    }
    
    /**
     * Does the checker have a capture at this location?<br>
     * Here we will also take into consideration if the move is in bounds and legal.<br>
     * @param opponent The opponent we are facing
     * @param self The player we control
     * @param column The destination Column
     * @param row The destination Row
     * @return true if the checker can capture an opponent piece when placing at the destination (column, row), false otherwise
     */
    public boolean hasCapture(final Player opponent, final Player self, final int column, final int row)
    {
        //if the location is not valid return false
        if (!isValidLocation(opponent, self, column, row))
                return false;
        
        //determine the difference between the start and destination
        final int colDiff = (int)((column > getCol()) ? column - getCol() : getCol() - column);
        final int rowDiff = (int)((row > getRow()) ? row - getRow() : getRow() - row);
        
        //we also want to verify that the destination (column, row) is within range of the current (column, row)
        if (colDiff != Player.MOVE_CAPTURE || rowDiff != Player.MOVE_CAPTURE)
            return false;
        
        //make sure we are jumping an opponent piece, or else the move would be invalid
        if (!opponent.hasPiece(
            (column > getCol()) ? column - Player.MOVE_NORMAL : getCol() - Player.MOVE_NORMAL,
            (row > getRow()) ? row - Player.MOVE_NORMAL : getRow() - Player.MOVE_NORMAL
            ))
            return false;
        
        //yes there is a capture here
        return true;
    }
    
    /**
     * Get the capture count?<br>
     * We count each capture, by each direction a capture is available.<br>
     * In checkers a checker can move a max of 4 directions (when king), so the capture count will not exceed 4
     * @param opponent The opponent we are facing
     * @param self The player we control
     * @return The total number of directions a capture can be performed (max 4)
     */
    public int getCaptureCount(final Player opponent, final Player self)
    {
        //how many different captures this checker can make
        int count = 0;
        
        //check north-west
        if (hasCapture(opponent, self, getCol() - Player.MOVE_CAPTURE, getRow() - Player.MOVE_CAPTURE))
            count++;
        
        //check north-east
        if (hasCapture(opponent, self, getCol() + Player.MOVE_CAPTURE, getRow() - Player.MOVE_CAPTURE))
            count++;
        
        //check south-west
        if (hasCapture(opponent, self, getCol() - Player.MOVE_CAPTURE, getRow() + Player.MOVE_CAPTURE))
            count++;
        
        //check south-east
        if (hasCapture(opponent, self, getCol() + Player.MOVE_CAPTURE, getRow() + Player.MOVE_CAPTURE))
            count++;
        
        //return the number of captures found
        return count;
    }
    
    /**
     * Is this checker trapped?<br>
     * Here we are checking if the checker has any valid moves to make.<br>
     * We will need to check the current player and opponents pieces.<br>
     * @param opponent The opponent we are facing
     * @param self The player we control
     * @return true if this Checker can't move, false otherwise
     */
    public boolean isTrapped(final Player opponent, final Player self)
    {
        //if this piece can capture an opponent piece, we are not trapped
        if (getCaptureCount(opponent, self) > NO_CAPTURES)
            return false;

        //if we are assigned north, or if the piece is a king
        if (self.assignedNorth() || isKing())
        {
            //make sure this position is available on the board
            if (Board.hasBoundary(getCol() - Player.MOVE_NORMAL, getRow() - Player.MOVE_NORMAL))
            {
                //make sure there are no pieces here, and then we will not be trapped
                if (!opponent.hasPiece(getCol() - Player.MOVE_NORMAL, getRow() - Player.MOVE_NORMAL) && 
                    !self.hasPiece(getCol() - Player.MOVE_NORMAL, getRow() - Player.MOVE_NORMAL))
                    return false;
            }

            //make sure this position is available on the board
            if (Board.hasBoundary(getCol() + Player.MOVE_NORMAL, getRow() - Player.MOVE_NORMAL))
            {
                //make sure there are no pieces here, and then we will not be trapped
                if (!opponent.hasPiece(getCol() + Player.MOVE_NORMAL, getRow() - Player.MOVE_NORMAL) && 
                    !self.hasPiece(getCol() + Player.MOVE_NORMAL, getRow() - Player.MOVE_NORMAL))
                    return false;
            }
        }

        //if we are assigned south, or if the piece is a king
        if (!self.assignedNorth() || isKing())
        {
            //make sure this position is available on the board
            if (Board.hasBoundary(getCol() - Player.MOVE_NORMAL, getRow() + Player.MOVE_NORMAL))
            {
                //make sure there are no pieces here, and then we will not be trapped
                if (!opponent.hasPiece(getCol() - Player.MOVE_NORMAL, getRow() + Player.MOVE_NORMAL) && 
                    !self.hasPiece(getCol() - Player.MOVE_NORMAL, getRow() + Player.MOVE_NORMAL))
                    return false;
            }

            //make sure this position is available on the board
            if (Board.hasBoundary(getCol() + Player.MOVE_NORMAL, getRow() + Player.MOVE_NORMAL))
            {
                //make sure there are no pieces here, and then we will not be trapped
                if (!opponent.hasPiece(getCol() + Player.MOVE_NORMAL, getRow() + Player.MOVE_NORMAL) && 
                    !self.hasPiece(getCol() + Player.MOVE_NORMAL, getRow() + Player.MOVE_NORMAL))
                    return false;
            }
        }
        
        //we have not found a valid move, so we are trapped
        return true;
    }
    
    public void setX(final int x)
    {
        this.x = x;
    }
    
    public void setY(final int y)
    {
        this.y = y;
    }
    
    public int getX()
    {
        return this.x;
    }
    
    public int getY()
    {
        return this.y;
    }
    
    /**
     * Does the specified location match this checker?
     * @param col Column
     * @param row Row
     * @return true if the column and row match the specified, false otherwise
     */
    public boolean hasMatch(final int col, final int row)
    {
        return (getCol() == col && getRow() == row);
    }
    
    /**
     * Is this checker piece a king?<br>
     * @param king true=yes, false=no
     */
    public void setKing(final boolean king)
    {
        this.king = king;
    }
    
    /**
     * Is this checker piece a king?<br>
     * @return true=yes, false=no
     */
    public boolean isKing()
    {
        return this.king;
    }
}