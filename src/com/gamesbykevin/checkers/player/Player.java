package com.gamesbykevin.checkers.player;

import com.gamesbykevin.checkers.board.Board;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.checkers.engine.Engine;
import com.gamesbykevin.checkers.piece.Checker;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;


/**
 * This class represents a player in the game
 * @author GOD
 */
public abstract class Player implements Disposable
{
    //give each player a unique id
    public final long id = System.nanoTime();
    
    //list of checker pieces
    private List<Checker> pieces;
    
    //the direction the player is attacking (either north or south)
    private final boolean north;
    
    //this will represent no selection
    protected static final int NO_SELECTION = -1;
    
    //a normal move
    public static final int MOVE_NORMAL = 1;
    
    //a jumping move
    public static final int MOVE_CAPTURE = 2;
    
    //the selected piece
    private int selection = NO_SELECTION;
    
    protected Player(final boolean north)
    {
        //assign the direction we are attacking
        this.north = north;
        
        //create a new list to hold the checker pieces
        this.pieces = new ArrayList<>();
    }
    
    /**
     * Get the index of the piece located within the coordinates
     * @param location x,y coordinates
     * @return The index of the piece within the coordinate, if none found NO_SELECTION -1 will be returned
     */
    protected int getSelection(final Point location)
    {
        return this.getSelection(location.x, location.y);
    }
    
    /**
     * Get the index of the piece located within the coordinates
     * @param x x-coordinate
     * @param y y-coordinate
     * @return The index of the piece within the coordinate, if none found NO_SELECTION -1 will be returned
     */
    protected int getSelection(final int x, final int y)
    {
        //check each piece
        for (int i = 0; i < getPieces().size(); i++)
        {
            Checker piece = getPiece(i);
            
            //if the coordinates are located within the piece
            if (x >= piece.getX() && x <= piece.getX() + Board.CELL_DIMENSIONS)
            {
                if (y >= piece.getY() && y <= piece.getY() + Board.CELL_DIMENSIONS)
                    return i;
            }
        }
        
        //return no selection
        return NO_SELECTION;
    }
    
    /**
     * Get the player's selected piece
     * @return The index of the piece they are interacting with
     */
    protected int getSelection()
    {
        return this.selection;
    }
    
    /**
     * Set the piece selection
     * @param selection The index of the piece you want to interact with
     */
    protected void setSelection(final int selection)
    {
        this.selection = selection;
    }
    
    /**
     * Does the player have a piece selected
     * @return true=yes, false=no
     */
    protected boolean hasSelection()
    {
        return  (getSelection() != NO_SELECTION);
    }
    
    /**
     * Is this player attacking north?<br>
     * If the player is not attacking north, they are attacking south.
     * @return true = attacking north, false = attacking south
     */
    public boolean assignedNorth()
    {
        return this.north;
    }
    
    /**
     * Add a checker piece to the players list.<br>
     * The piece added will not be a king
     * @param col Column location
     * @param row Row location
     * @throws Exception If we are adding a piece that already exists in this location
     */
    public void add(final int col, final int row) throws Exception
    {
        this.add(col, row, false);
    }
    
    /**
     * Add a checker piece to the players list.<br>
     * @param col Column location
     * @param row Row location
     * @param king Is this piece a king?
     * @throws Exception If we are adding a piece that already exists in this location
     */
    public void add(final int col, final int row, final boolean king) throws Exception
    {
        if (hasPiece(col, row))
            throw new Exception("A checker piece already exists here. col=" + col + ", row=" + row);
        
        getPieces().add(new Checker(col, row, king));
    }
    
    /**
     * Remove the checker piece from this location.
     * @param col Column
     * @param row Row
     * @throws Exception If we attempt to remove a piece that does not exist
     */
    protected void remove(final int col, final int row) throws Exception
    {
        if (!hasPiece(col, row))
            throw new Exception("The checker piece does not exist. col=" + col + ", row=" + row);
        
        //check each piece to see if we have a match
        for (int i = 0; i < getPieces().size(); i++)
        {
            //if the location matches remove this piece
            if (getPiece(i).hasMatch(col, row))
            {
                //remove piece
                getPieces().remove(i);
                
                //exit loop
                break;
            }
        }
    }
    
    /**
     * Does this player have a jump to capture the opponent piece?
     * @param opponent The opponent we are attacking
     * @return true if a jump move is available to capture an opponent piece, false otherwise
     */
    public boolean hasCapture(final Player opponent)
    {
        //check each piece to see if we have a jump
        for (int i = 0; i < getPieces().size(); i++)
        {
            //if the current piece has a capture, return true
            if (getPiece(i).hasCapture(opponent, this))
                return true;
        }
        
        //no capture found, return false
        return false;
    }
    
    /**
     * Assign the location for the current selected piece.<br>
     * Then assign the appropriate x,y coordinates.<br>
     * We will also check if the piece qualifies to become a king<br>
     * Finally reset the current piece selection<br>
     * 
     * @param board The game board, used to get the x,y coordinates
     * @param col The column we want to place the current assigned piece
     * @param row The row we want to place the current assigned piece
     * @throws Exception if the column or row is out of range of the board
     */
    protected void placeSelection(final Board board, final int col, final int row) throws Exception
    {
        //the current selected piece
        Checker piece = getPiece(getSelection());

        //assign the location
        piece.setCol(col);
        piece.setRow(row);
        
        //if the piece is not a king let's check if it qualifies
        if (!piece.isKing())
        {
            if (assignedNorth())
            {
                piece.setKing(piece.getRow() == Board.ROWS_MIN);
            }
            else
            {
                piece.setKing(piece.getRow() == Board.ROWS_MAX);
            }
        }
        
        //reset coordinates
        piece.setX(board.getCoordinateX(piece));
        piece.setY(board.getCoordinateY(piece));
                        
        //reset selection
        setSelection(NO_SELECTION);
    }
    
    /**
     * Is there a piece here at this location?<br>
     * @param col Column
     * @param row Row
     * @return true if there is a piece at this location, false otherwise
     */
    public boolean hasPiece(final int col, final int row)
    {
        //check each piece to see if we have one
        for (int i = 0; i < getPieces().size(); i++)
        {
            //if the location matches we have a piece here
            if (getPiece(i).hasMatch(col, row))
                return true;
        }
        
        //we do not have a piece at this location
        return false;
    }
    
    /**
     * Get the checker piece at the specified location.
     * @param col Column
     * @param row Row
     * @return The checker piece at the specified location, if not found null is returned
     */
    public Checker getPiece(final int col, final int row)
    {
        //if a piece does not exist in this location, return null
        if (!hasPiece(col, row))
            return null;
        
        //check each piece
        for (int i = 0; i < getPieces().size(); i++)
        {
            //if the locations match return the piece
            if (getPiece(i).hasMatch(col, row))
                return getPiece(i);
        }
        
        //the piece was not found return null
        return null;
    }
    
    /**
     * Get the checker piece
     * @param index The location of the piece in the list
     * @return The checker piece in the list
     */
    public Checker getPiece(final int index)
    {
        return getPieces().get(index);
    }
    
    /**
     * Get the checkers that belong to the player
     * @return The list of checkers
     */
    protected List<Checker> getPieces()
    {
        return this.pieces;
    }
    
    /**
     * Are all of the player's checkers trapped?<br>
     * @param opponent The opponent we re playing
     * @return true if the player has at least 1 piece that can move, false otherwise
     */
    public boolean isTrapped(final Player opponent)
    {
        //there are no more pieces, we are trapped
        if (getPieces().isEmpty())
            return true;
        
        //check each piece
        for (int i = 0; i < getPieces().size(); i++)
        {
            //get the current piece
            final Checker piece = getPiece(i);
            
            //if this piece is not trapped, then the player can make at least 1 move
            if (!piece.isTrapped(opponent, this))
                return false;
        }
        
        //we couldn't find 1 piece that is not trapped, so the player is trapped
        return true;
    }
    
    /**
     * Each player will need a way to update their pieces
     * @param engine Object containing game elements
     * @return true if the player has completed their turn
     * @throws Exception 
     */
    public abstract boolean update(final Engine engine) throws Exception;
    
    @Override
    public void dispose()
    {
        for (int i = 0; i < pieces.size(); i++)
        {
            pieces.set(i, null);
        }
        
        pieces.clear();
        pieces = null;
    }
}