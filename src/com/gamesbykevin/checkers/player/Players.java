package com.gamesbykevin.checkers.player;

import com.gamesbykevin.framework.base.Sprite;

import com.gamesbykevin.checkers.board.Board;
import com.gamesbykevin.checkers.engine.Engine;
import com.gamesbykevin.checkers.piece.Checker;
import com.gamesbykevin.checkers.shared.IElement;

import java.awt.Graphics;
import java.awt.Image;

/**
 * This class will contain the players in the game
 * @author GOD
 */
public final class Players extends Sprite implements IElement
{
    //the players in the game
    private Player human, cpu;
    
    //player 1 will go first
    private boolean player1turn = true;
    
    public enum PieceKey
    {
        RegularPlayer1(2,1),
        RegularPlayer2(0,1),
        KingPlayer1(2,0),
        KingPlayer2(0,0);
        
        private final int col, row;
        
        private PieceKey(final int col, final int row)
        {
            this.col = col;
            this.row = row;
        }
    }
    
    public Players(final Image image) throws Exception
    {
        //assign image
        super.setImage(image);
        
        //create spritesheet
        super.createSpriteSheet();
        
        //player 1 will attack north
        this.human = new Human(true);
        
        //player 2 will attack south
        this.cpu = new Human(false);
        
        //add the animation for each piece
        for (PieceKey key : PieceKey.values())
        {
            super.getSpriteSheet().add(key.col * Board.CELL_DIMENSIONS, key.row * Board.CELL_DIMENSIONS, Board.CELL_DIMENSIONS, Board.CELL_DIMENSIONS, 0, key);
        }
        
        //make sure players aren't attacking in the same direction
        if (getHuman().assignedNorth() && getCpu().assignedNorth() || !getHuman().assignedNorth() && !getCpu().assignedNorth())
            throw new Exception("Both players can't be attacking in the same direction.");
    }
    
    private Player getHuman()
    {
        return this.human;
    }
    
    private Player getCpu()
    {
        return this.cpu;
    }
    
    /**
     * Get the opponent
     * @param Player The current player (not the opponent)
     * @return The player we are playing against, either the human or cpu player
     */
    public Player getOpponent(final Player player)
    {
        if (player.id != getHuman().id)
        {
            return getHuman();
        }
        else
        {
            return getCpu();
        }
    }
    
    /**
     * Make sure the player's pieces are in the correct location
     * @param board The board where the pieces are placed
     */
    public void assignCoordinates(final Board board) throws Exception
    {
        for (int index = 0; index < this.getHuman().getPieces().size(); index++)
        {
            //get the current checker
            Checker piece = this.getHuman().getPiece(index);
            
            //assign the coordinates
            piece.setX(board.getCoordinateX(piece));
            piece.setY(board.getCoordinateY(piece));
        }
        
        for (int index = 0; index < this.getCpu().getPieces().size(); index++)
        {
            //get the current checker
            Checker piece = this.getCpu().getPiece(index);
            
            //assign the coordinates
            piece.setX(board.getCoordinateX(piece));
            piece.setY(board.getCoordinateY(piece));
        }
    }
    
    /**
     * Clear the players pieces and assign the default
     */
    public final void reset() throws Exception
    {
        //remove the players pieces
        this.getHuman().getPieces().clear();
        this.getCpu().getPieces().clear();
        
        //setup the pieces for the player
        for (int row = 0; row < Board.ROWS; row++)
        {
            for (int col = 0; col < Board.COLUMNS; col++)
            {
                //player2 attacking south
                if (row <= 2)
                {
                    if (row % 2 == 0)
                    {
                        if (col % 2 != 0)
                            getCpu().add(col, row);
                    }
                    else
                    {
                        if (col % 2 == 0)
                            getCpu().add(col, row);
                    }
                }
                else if (row >= Board.COLUMNS - 3)
                {
                    if (row % 2 == 0)
                    {
                        if (col % 2 != 0)
                            getHuman().add(col, row);
                    }
                    else
                    {
                        if (col % 2 == 0)
                            getHuman().add(col, row);
                    }
                }
            }
        }
    }
    
    @Override
    public void dispose()
    {
        if (human != null)
        {
            human.dispose();
            human = null;
        }
        
        if (cpu != null)
        {
            cpu.dispose();
            cpu = null;
        }
    }
    
    private boolean isPlayer1Turn()
    {
        return this.player1turn;
    }
    
    private void setPlayer1Turn(final boolean player1turn)
    {
        this.player1turn = player1turn;
    }
            
    @Override
    public void update(final Engine engine) throws Exception
    {
        //has the current player completed their turn
        boolean result;
        
        if (isPlayer1Turn())
        {
            result = getHuman().update(engine);
            
            //if the player finished switch turns
            if (result)
            {
                //switch turns
                setPlayer1Turn(!isPlayer1Turn());
            }
        }
        else
        {
            result = getCpu().update(engine);
            
            //if the player finished switch turns
            if (result)
            {
                //switch turns
                setPlayer1Turn(!isPlayer1Turn());
            }
        }
        
        //reset mouse input
        if (engine.getMouse().isMouseReleased())
            engine.getMouse().reset();
    }
    
    @Override
    public void render(final Graphics graphics) throws Exception
    {
        //assign the dimensions
        super.setDimensions(Board.CELL_DIMENSIONS);
        
        //render the players depending on the current turn
        if (!player1turn)
        {
            renderPlayer(graphics, getHuman(), PieceKey.KingPlayer1, PieceKey.RegularPlayer1);
            renderPlayer(graphics, getCpu(), PieceKey.KingPlayer2, PieceKey.RegularPlayer2);
        }
        else
        {
            renderPlayer(graphics, getCpu(), PieceKey.KingPlayer2, PieceKey.RegularPlayer2);
            renderPlayer(graphics, getHuman(), PieceKey.KingPlayer1, PieceKey.RegularPlayer1);
        }
    }
    
    /**
     * Render the player's checkers
     * @param graphics Object used to draw graphics
     * @param player The player who's checkers we want to draw
     * @param king Animation key for a king checker
     * @param regular Animation key for a regular checker
     * @throws Exception 
     */
    private void renderPlayer(final Graphics graphics, final Player player, final PieceKey king, final PieceKey regular) throws Exception
    {
        for (int index = 0; index < player.getPieces().size(); index++)
        {
            //get the current checker
            Checker piece = player.getPiece(index);
            
            //assign the location
            super.setLocation(piece.getX(), piece.getY());
            
            //assign the appropriate animation
            super.getSpriteSheet().setCurrent((piece.isKing()) ? king : regular);
            
            //draw the piece
            super.draw(graphics);
        }
    }
}
