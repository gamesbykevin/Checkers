package com.gamesbykevin.checkers.player;

import com.gamesbykevin.framework.base.Sprite;

import com.gamesbykevin.checkers.board.Board;
import com.gamesbykevin.checkers.engine.Engine;
import com.gamesbykevin.checkers.message.Message;
import com.gamesbykevin.checkers.piece.Checker;
import com.gamesbykevin.checkers.resources.GameAudio;
import com.gamesbykevin.checkers.shared.IElement;

import java.awt.event.KeyEvent;
import java.awt.Graphics;
import java.awt.Image;

/**
 * This class will contain the players in the game
 * @author GOD
 */
public final class Players extends Sprite implements IElement
{
    //the players in the game
    private Player player1, player2;
    
    //player 1 will go first
    private boolean player1turn = true;
    
    //has the game ended
    private boolean gameover = false;
    
    /**
     * The button to hit for reset
     */
    private static final int KEY_RESET = KeyEvent.VK_R;
    
    /**
     * Player modes
     * 1) human vs. cpu
     * 2) cpu vs. cpu
     * 3) human vs. human
     */
    public static final int HUMAN_CPU = 0;
    public static final int CPU_CPU = 1;
    public static final int HUMAN_HUMAN = 2;
    
    public enum PieceKey
    {
        RegularPlayer1(2,1),
        RegularPlayer1Other(1,1),
        RegularPlayer2(0,1),
        KingPlayer1(2,0),
        KingPlayer1Other(1,0),
        KingPlayer2(0,0);
        
        private final int col, row;
        
        private PieceKey(final int col, final int row)
        {
            this.col = col;
            this.row = row;
        }
    }
    
    public Players(final Image image, final boolean random, final int playerModeIndex) throws Exception
    {
        //assign image
        super.setImage(image);
        
        //create spritesheet
        super.createSpriteSheet();
        
        //pick checker animations for player 1
        PieceKey regular = (random) ? PieceKey.RegularPlayer1 : PieceKey.RegularPlayer1Other;
        PieceKey king = (random) ? PieceKey.KingPlayer1 : PieceKey.KingPlayer1Other;
        
        switch (playerModeIndex)
        {
            case HUMAN_CPU:
            default:
                //player 1 will attack north
                this.player1 = new Human(true, regular, king);
                
                //player 2 will attack south
                this.player2 = new Cpu(false, PieceKey.RegularPlayer2, PieceKey.KingPlayer2);
                break;
                
            case CPU_CPU:
                //player 1 will attack north
                this.player1 = new Cpu(true, regular, king);

                //player 2 will attack south
                this.player2 = new Cpu(false, PieceKey.RegularPlayer2, PieceKey.KingPlayer2);
                break;
                
            case HUMAN_HUMAN:
                //player 1 will attack north
                this.player1 = new Human(true, regular, king);
                
                //player 2 will attack south
                this.player2 = new Human(false, PieceKey.RegularPlayer2, PieceKey.KingPlayer2);
                break;
        }
        
        //add the animation for each piece
        for (PieceKey key : PieceKey.values())
        {
            super.getSpriteSheet().add(key.col * Board.CELL_DIMENSIONS, key.row * Board.CELL_DIMENSIONS, Board.CELL_DIMENSIONS, Board.CELL_DIMENSIONS, 0, key);
        }
        
        //make sure players aren't attacking in the same direction
        if (getPlayer1().assignedNorth() && getPlayer2().assignedNorth() || !getPlayer1().assignedNorth() && !getPlayer2().assignedNorth())
            throw new Exception("Both players can't be attacking in the same direction.");
    }
    
    /**
     * Has the game ended one way or the other?<br>
     * @param gameover true=yes, false=no
     */
    public void setGameover(final boolean gameover)
    {
        this.gameover = gameover;
    }
    
    public boolean isGameover()
    {
        return this.gameover;
    }
    
    private Player getPlayer1()
    {
        return this.player1;
    }
    
    private Player getPlayer2()
    {
        return this.player2;
    }
    
    /**
     * Get the opponent the specified player is facing.
     * @param Player The current player (not the opponent)
     * @return The player we are playing against, either the human or cpu player
     */
    public Player getOpponent(final Player player)
    {
        if (player.id != getPlayer1().id)
        {
            return getPlayer1();
        }
        else
        {
            return getPlayer2();
        }
    }
    
    /**
     * Clear all existing pieces and assign the default.
     * @param board Board object needed to assign the correct x,y coordinates
     * @throws Exception 
     */
    public final void reset(final Board board) throws Exception
    {
        //remove the players pieces
        this.getPlayer1().getPieces().clear();
        this.getPlayer2().getPieces().clear();
        
        //player 1 goes first
        setPlayer1Turn(true);
        
        //the game is not over
        setGameover(false);
        
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
                            getPlayer2().add(col, row);
                    }
                    else
                    {
                        if (col % 2 == 0)
                            getPlayer2().add(col, row);
                    }
                }
                else if (row >= Board.COLUMNS - 3)
                {
                    if (row % 2 == 0)
                    {
                        if (col % 2 != 0)
                            getPlayer1().add(col, row);
                    }
                    else
                    {
                        if (col % 2 == 0)
                            getPlayer1().add(col, row);
                    }
                }
            }
        }
        
        //now assign the appropriate coordinates for player
        for (int index = 0; index < this.getPlayer1().getPieces().size(); index++)
        {
            //assign the appropriate (x,y) coordinates for the checker
            board.placePiece(getPlayer1().getPiece(index));
        }
        
        //now assign the appropriate coordinates for player
        for (int index = 0; index < this.getPlayer2().getPieces().size(); index++)
        {
            //assign the appropriate (x,y) coordinates for the checker
            board.placePiece(getPlayer2().getPiece(index));
        }
    }
    
    @Override
    public void dispose()
    {
        if (player1 != null)
        {
            player1.dispose();
            player1 = null;
        }
        
        if (player2 != null)
        {
            player2.dispose();
            player2 = null;
        }
    }
    
    /**
     * Is it player 1's turn?
     * @return true=yes, false=no
     */
    private boolean isPlayer1Turn()
    {
        return this.player1turn;
    }
    
    /**
     * Set the appropriate players turn.<br>
     * @param player1turn true = player 1's turn, false = player 2's turn
     */
    private void setPlayer1Turn(final boolean player1turn)
    {
        this.player1turn = player1turn;
    }
            
    @Override
    public void update(final Engine engine) throws Exception
    {
        //if game has ended
        if (isGameover())
        {
            //check if user hit reset button
            if (engine.getKeyboard().isKeyReleased())
            {
                //if the reset key was released, reset game
                if (engine.getKeyboard().hasKeyReleased(KEY_RESET))
                {
                    //reset player's pieces
                    reset(engine.getManager().getBoard());
                    
                    //also reset status message
                    engine.getManager().getMessage().setDescription1(Message.MESSAGE_PLAYER_1_TURN);
                    engine.getManager().getMessage().setDescription2(Message.MESSAGE_BEGIN);
                    
                    //stop all existing sound
                    engine.getResources().stopAllSound();
                    
                    //re-start playing music
                    engine.getResources().playRandomMusic(engine.getRandom());
                }
            }
            
            //no need to continue
            return;
        }
        
        //has the current player completed their turn
        boolean result;
        
        if (isPlayer1Turn())
        {
            result = getPlayer1().update(engine);
            
            //if the player finished switch turns
            if (result)
            {
                //switch turns
                setPlayer1Turn(!isPlayer1Turn());
                
                //set message
                engine.getManager().getMessage().setDescription1(Message.MESSAGE_PLAYER_2_TURN);
                engine.getManager().getMessage().setDescription2(Message.MESSAGE_NONE);
            }
        }
        else
        {
            result = getPlayer2().update(engine);
            
            //if the player finished switch turns
            if (result)
            {
                //switch turns
                setPlayer1Turn(!isPlayer1Turn());
                
                //set message
                engine.getManager().getMessage().setDescription1(Message.MESSAGE_PLAYER_1_TURN);
                engine.getManager().getMessage().setDescription2(Message.MESSAGE_NONE);
            }
        }
        
        if (!isGameover())
        {
            if (getPlayer1().isTrapped(getPlayer2()))
            {
                //flag game over
                setGameover(true);
                
                //set message
                engine.getManager().getMessage().setDescription1(Message.MESSAGE_PLAYER_2_WINS);
                engine.getManager().getMessage().setDescription2(Message.MESSAGE_RESET);
                
                //stop any existing sound
                engine.getResources().stopAllSound();
                
                //play lose sound effect
                engine.getResources().playGameAudio(GameAudio.Keys.Lose);
            }
            else if (getPlayer2().isTrapped(getPlayer1()))
            {
                //flag game over
                setGameover(true);
                
                //set message
                engine.getManager().getMessage().setDescription1(Message.MESSAGE_PLAYER_1_WINS);
                engine.getManager().getMessage().setDescription2(Message.MESSAGE_RESET);
                
                //stop any existing sound
                engine.getResources().stopAllSound();
                
                //play win sound effect
                engine.getResources().playGameAudio(GameAudio.Keys.Win);
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
            renderPlayer(graphics, getPlayer1());
            renderPlayer(graphics, getPlayer2());
        }
        else
        {
            renderPlayer(graphics, getPlayer2());
            renderPlayer(graphics, getPlayer1());
        }
    }
    
    /**
     * Render the player's checkers
     * @param graphics Object used to draw graphics
     * @param player The player who's checkers we want to draw
     * @throws Exception 
     */
    private void renderPlayer(final Graphics graphics, final Player player) throws Exception
    {
        for (int index = 0; index < player.getPieces().size(); index++)
        {
            //get the current checker
            Checker piece = player.getPiece(index);
            
            //assign the location
            super.setLocation(piece.getX(), piece.getY());
            
            //assign the appropriate animation
            super.getSpriteSheet().setCurrent((piece.isKing()) ? player.getKeyKing() : player.getKeyRegular());
            
            //draw the piece
            super.draw(graphics);
        }
    }
}