/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gamesbykevin.checkers.player;

import com.gamesbykevin.checkers.board.Board;
import com.gamesbykevin.checkers.engine.Engine;
import com.gamesbykevin.checkers.message.Message;
import com.gamesbykevin.checkers.piece.Checker;

/**
 * This will handle the human interaction with the player
 * @author GOD
 */
public final class Human extends Player
{
    public Human(final boolean attackNorth, final Players.PieceKey pieceRegular, final Players.PieceKey pieceKing)
    {
        super(attackNorth, pieceRegular, pieceKing);
    }
    
    @Override
    public boolean update(final Engine engine) throws Exception
    {
        //has a valid move been made, default false
        boolean valid = false;
        
        //get the mouse location
        final int x = engine.getMouse().getLocation().x;
        final int y = engine.getMouse().getLocation().y;
        
        //does the player have a piece selected
        if (hasSelection())
        {
            //the current selected piece
            Checker piece = getPiece(getSelection());
            
            if (engine.getMouse().hasMouseMoved() && !engine.getMouse().isMouseReleased())
            {
                //place piece where the mouse moved
                piece.setX(x - (Board.CELL_DIMENSIONS / 2));
                piece.setY(y - (Board.CELL_DIMENSIONS / 2));
            }
            else if (engine.getMouse().isMouseReleased())
            {
                //get the game board
                final Board board = engine.getManager().getBoard();
                
                //get the column, row where we want to place
                final int newCol = board.getColumn(x, y);
                final int newRow = board.getRow(x, y);
                
                //make sure the location is on the playable board
                if (!Board.hasBounds(newCol, newRow))
                {
                    //prompt user of invalid move
                    engine.getManager().getMessage().setDescription2(Message.MESSAGE_INVALID_MOVE);
                    
                    //return false, move has not completed
                    return false;
                }
                
                //do we already have a piece at this location
                if (hasPiece(newCol, newRow))
                {
                    //is the location we selected, the same location as the current piece
                    if (piece.hasMatch(newCol, newRow))
                    {
                        //reset selection
                        placeSelection(board, newCol, newRow);
                    }
                    else
                    {
                        //display to user that there is already a piece here
                        engine.getManager().getMessage().setDescription2(Message.MESSAGE_PIECE_EXISTS);
                    }
                }
                else
                {
                    //check if the piece can be placed here, lets see which direction we are heading
                    final boolean north = (piece.getRow() > newRow);
                    
                    //are we heading in the correct direction
                    boolean correctDirection = true;
                    
                    //if the piece is not a king, let's see if we can move in this direction
                    if (!piece.isKing())
                    {
                        //if we are moving in a direction we are not assigned
                        if (assignedNorth() && !north || !assignedNorth() && north)
                        {
                            //not the correct direction
                            correctDirection = false;
                            
                            //prompt user, that they made an invalid move
                            engine.getManager().getMessage().setDescription2(Message.MESSAGE_INVALID_MOVE);
                        }
                    }
                    else
                    {
                        //a king can move in any direction
                        correctDirection = true;
                    }
                    
                    //if we are heading in the appropriate direction
                    if (correctDirection)
                    {
                        //get the opponent
                        final Player opponent = engine.getManager().getPlayers().getOpponent(this);
                        
                        final boolean hasCapture = this.hasCapture(opponent);
                        
                        //you can't place a piece on top of the cpu
                        if (!opponent.hasPiece(newCol, newRow))
                        {
                            //now check if the new location is in range
                            final int rowDiff = (piece.getRow() > newRow) ? piece.getRow() - newRow : newRow - piece.getRow();
                            final int colDiff = (piece.getCol() > newCol) ? piece.getCol() - newCol : newCol - piece.getCol();
                            
                            //if moving 1 spot
                            if (colDiff == Player.MOVE_NORMAL && rowDiff == Player.MOVE_NORMAL)
                            {
                                //make sure the player doesn't have to make a required capture
                                if (!hasCapture)
                                {
                                    //reset selection
                                    placeSelection(board, newCol, newRow);

                                    //a valid move has been made
                                    valid = true;
                                }
                                else
                                {
                                    //prompt user, they must capture the opponent
                                    engine.getManager().getMessage().setDescription2(Message.MESSAGE_JUMP_REQUIRED);
                                }
                            }
                            else if (colDiff == Player.MOVE_CAPTURE && rowDiff == Player.MOVE_CAPTURE)
                            {
                                //get the enemy position
                                final int enemyCol = (newCol > piece.getCol()) ? newCol - 1 : newCol + 1;
                                final int enemyRow = (newRow > piece.getRow()) ? newRow - 1 : newRow + 1;
                                
                                //make sure we are jumping the enemy
                                if (opponent.hasPiece(enemyCol, enemyRow))
                                {
                                    //store the current piece selection
                                    //final int selection = getSelection();
                                    
                                    //remove enemy piece
                                    opponent.remove(enemyCol, enemyRow);

                                    //reset selection
                                    placeSelection(board, newCol, newRow);

                                    //if this player no longer has a capture, we have completed our turn
                                    if (!piece.hasCapture(opponent, this))
                                    {
                                        //a valid move has been made
                                        valid = true;
                                    }
                                    else
                                    {
                                        //the piece still can capture, so we will keep the same selection
                                        //setSelection(selection);
                                        
                                        //display to user they have to complete the capture
                                        engine.getManager().getMessage().setDescription2(Message.MESSAGE_COMPLETE_JUMP);
                                    }
                                }
                                else
                                {
                                    //not a valid jump
                                    valid = false;
                                    
                                    //display to user invalid move
                                    engine.getManager().getMessage().setDescription2(Message.MESSAGE_INVALID_MOVE);
                                }
                            }
                            else
                            {
                                //display to user invalid move
                                engine.getManager().getMessage().setDescription2(Message.MESSAGE_INVALID_MOVE);
                            }
                        }
                        else
                        {
                            //can't place a piece where the enemy is
                            valid = false;
                            
                            //display to user that there is already a piece here
                            engine.getManager().getMessage().setDescription2(Message.MESSAGE_PIECE_EXISTS);
                        }
                    }
                }
            }
        }
        else
        {
            //if the mouse was released check if a selection was made
            if (engine.getMouse().isMouseReleased())
            {
                //set selection
                setSelection(getSelection(engine.getMouse().getLocation()));
                
                //if no selection was made
                if (!hasSelection())
                {
                    engine.getManager().getMessage().setDescription2(Message.MESSAGE_INVALID_SELECTION);
                }
                else
                {
                    engine.getManager().getMessage().setDescription2(Message.MESSAGE_NONE);
                }
                
                //reset mouse input
                engine.getMouse().reset();
            }
        }
        
        //if move was made reset mouse input
        if (valid)
            engine.getMouse().reset();
        
        //return if a valid move was made
        return valid;
    }
}