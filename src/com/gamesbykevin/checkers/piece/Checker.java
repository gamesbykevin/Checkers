package com.gamesbykevin.checkers.piece;

import com.gamesbykevin.checkers.board.Board;
import com.gamesbykevin.checkers.player.Player;

/**
 * This class represents a checker
 * @author GOD
 */
public final class Checker
{
    //is this checker a king
    private boolean king = false;
    
    //the location of the checker
    private int col, row;
    
    //the location of the piece
    private int x, y;
    
    public Checker(final int col, final int row)
    {
        this(col, row, false);
    }
    
    public Checker(final int col, final int row, final boolean king)
    {
        //assign location
        setCol(col);
        setRow(row);
        
        //assign this a king
        setKing(king);
    }
    
    /**
     * Can this checker capture any of the opponent checkers?<br>
     * @param opponent The opponent we are facing
     * @param self The player we control
     * @return true if this Checker piece can capture an opponent piece, false otherwise
     */
    public boolean hasCapture(final Player opponent, final Player self)
    {
        //check if we can capture north
        if (self.assignedNorth() || isKing())
        {
            //make sure capture location is within the playable board
            if (Board.hasBounds(getCol() - Player.MOVE_CAPTURE, getRow() - Player.MOVE_CAPTURE))
            {
                //make sure the opponent has a piece in place to capture
                if (opponent.hasPiece(getCol() - Player.MOVE_NORMAL, getRow() - Player.MOVE_NORMAL))
                {
                    //make sure we and the opponent don't have a piece here
                    if (!opponent.hasPiece(getCol() - Player.MOVE_CAPTURE, getRow() - Player.MOVE_CAPTURE) && 
                        !self.hasPiece(getCol() - Player.MOVE_CAPTURE, getRow() - Player.MOVE_CAPTURE)
                        )
                        return true;
                }
            }

            //make sure capture location is within the playable board
            if (Board.hasBounds(getCol() + Player.MOVE_CAPTURE, getRow() - Player.MOVE_CAPTURE))
            {
                //make sure the opponent has a piece in place to capture
                if (opponent.hasPiece(getCol() + Player.MOVE_NORMAL, getRow() - Player.MOVE_NORMAL))
                {
                    //make sure we and the opponent don't have a piece here
                    if (!opponent.hasPiece(getCol() + Player.MOVE_CAPTURE, getRow() - Player.MOVE_CAPTURE) && 
                        !self.hasPiece(getCol() + Player.MOVE_CAPTURE, getRow() - Player.MOVE_CAPTURE))
                        return true;
                }
            }
        }
            
        //check if we can capture south
        if (!self.assignedNorth() || isKing())
        {
            //make sure capture location is within the playable board
            if (Board.hasBounds(getCol() - Player.MOVE_CAPTURE, getRow() + Player.MOVE_CAPTURE))
            {
                //make sure the opponent has a piece in place to capture
                if (opponent.hasPiece(getCol() - Player.MOVE_NORMAL, getRow() + Player.MOVE_NORMAL))
                {
                    //make sure we and the opponent don't have a piece here
                    if (!opponent.hasPiece(getCol() - Player.MOVE_CAPTURE, getRow() + Player.MOVE_CAPTURE) && 
                        !self.hasPiece(getCol() - Player.MOVE_CAPTURE, getRow() + Player.MOVE_CAPTURE)
                        )
                        return true;
                }
            }

            //make sure capture location is within the playable board
            if (Board.hasBounds(getCol() + Player.MOVE_CAPTURE, getRow() + Player.MOVE_CAPTURE))
            {
                //make sure the opponent has a piece in place to capture
                if (opponent.hasPiece(getCol() + Player.MOVE_NORMAL, getRow() + Player.MOVE_NORMAL))
                {
                    //make sure we and the opponent don't have a piece here
                    if (!opponent.hasPiece(getCol() + Player.MOVE_CAPTURE, getRow() + Player.MOVE_CAPTURE) && 
                        !self.hasPiece(getCol() + Player.MOVE_CAPTURE, getRow() + Player.MOVE_CAPTURE))
                        return true;
                }
            }
        }
        
        //this piece does not have a capture
        return false;
    }
    
    /**
     * Is this checker trapped?<br>
     * @param opponent The opponent we are facing
     * @param self The player we control
     * @return true if this Checker can't move anywhere, false otherwise
     */
    public boolean isTrapped(final Player opponent, final Player self)
    {
        //if this piece can capture an opponent piece, we are not trapped
        if (hasCapture(opponent, self))
            return false;

        //if we are assigned north, or if the piece is a king
        if (self.assignedNorth() || isKing())
        {
            //make sure this position is available on the board
            if (Board.hasBounds(getCol() - Player.MOVE_NORMAL, getRow() - Player.MOVE_NORMAL))
            {
                //make sure there are no pieces here, and then we will not be trapped
                if (!opponent.hasPiece(getCol() - Player.MOVE_NORMAL, getRow() - Player.MOVE_NORMAL) && 
                    !self.hasPiece(getCol() - Player.MOVE_NORMAL, getRow() - Player.MOVE_NORMAL))
                    return false;
            }

            //make sure this position is available on the board
            if (Board.hasBounds(getCol() + Player.MOVE_NORMAL, getRow() - Player.MOVE_NORMAL))
            {
                //make sure there are no pieces here, and then we will not be trapped
                if (!opponent.hasPiece(getCol() + Player.MOVE_NORMAL, getRow() - Player.MOVE_NORMAL) && 
                    !self.hasPiece(getCol() + Player.MOVE_NORMAL, getRow() - Player.MOVE_NORMAL))
                    return false;
            }
        }

        if (!self.assignedNorth() || isKing())
        {
            //make sure this position is available on the board
            if (Board.hasBounds(getCol() - Player.MOVE_NORMAL, getRow() + Player.MOVE_NORMAL))
            {
                //make sure there are no pieces here, and then we will not be trapped
                if (!opponent.hasPiece(getCol() - Player.MOVE_NORMAL, getRow() + Player.MOVE_NORMAL) && 
                    !self.hasPiece(getCol() - Player.MOVE_NORMAL, getRow() + Player.MOVE_NORMAL))
                    return false;
            }

            //make sure this position is available on the board
            if (Board.hasBounds(getCol() + Player.MOVE_NORMAL, getRow() + Player.MOVE_NORMAL))
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
     * Assign the column location
     * @param col Column
     */
    public void setCol(final int col)
    {
        this.col = col;
    }
    
    /**
     * Get the column
     * @return The column location of this checker piece
     */
    public int getCol()
    {
        return this.col;
    }
    
    /**
     * Assign the row location
     * @param row Row
     */
    public void setRow(final int row)
    {
        this.row = row;
    }
    
    /**
     * Get the row
     * @return The row location of this checker piece
     */
    public int getRow()
    {
        return this.row;
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