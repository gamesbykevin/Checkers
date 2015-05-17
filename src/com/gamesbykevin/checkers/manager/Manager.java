package com.gamesbykevin.checkers.manager;

import com.gamesbykevin.checkers.background.Background;
import com.gamesbykevin.framework.util.Timers;
import com.gamesbykevin.checkers.board.*;
import com.gamesbykevin.checkers.engine.Engine;
import com.gamesbykevin.checkers.menu.CustomMenu;
import com.gamesbykevin.checkers.menu.CustomMenu.*;
import com.gamesbykevin.checkers.player.Players;
import com.gamesbykevin.checkers.resources.GameAudio;
import com.gamesbykevin.checkers.resources.GameImages;
import com.gamesbykevin.checkers.shared.Shared;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * The class that contains all of the game elements
 * @author GOD
 */
public final class Manager implements IManager
{
    //where gameplay occurs
    private Rectangle window;
    
    //the location where the board is to be drawn
    private static final int BOARD_START_X = -Board.CELL_DIMENSIONS;
    private static final int BOARD_START_Y = Board.CELL_DIMENSIONS;
    
    //the game board
    private Board board;
    
    //the players in the game
    private Players players;
    
    private Background background;
    
    /**
     * Constructor for Manager, this is the point where we load any menu option configurations
     * @param engine Engine for our game that contains all objects needed
     * @throws Exception 
     */
    public Manager(final Engine engine) throws Exception
    {
        //set the audio depending on menu setting
        engine.getResources().setAudioEnabled(engine.getMenu().getOptionSelectionIndex(LayerKey.Options, OptionKey.Sound) == CustomMenu.SOUND_ENABLED);
        
        //set the game window where game play will occur
        setWindow(engine.getMain().getScreen());
    }
    
    @Override
    public void reset(final Engine engine) throws Exception
    {
        //create list of optional boards
        List<GameImages.Keys> options = new ArrayList<>();

        //reset options list
        options.clear();

        //get the board selection
        final int boardSelection = engine.getMenu().getOptionSelectionIndex(LayerKey.Options, OptionKey.Board);

        //first selection is random
        if (boardSelection == 0)
        {
            options.add(GameImages.Keys.BoardGlass);
            options.add(GameImages.Keys.BoardMarble);
            options.add(GameImages.Keys.BoardPlastic);
            options.add(GameImages.Keys.BoardWood);
            options.add(GameImages.Keys.BoardOriginal);
        }
        else if (boardSelection == 1)
        {
            options.add(GameImages.Keys.BoardMarble);
        }
        else if (boardSelection == 2)
        {
            options.add(GameImages.Keys.BoardGlass);
        }
        else if (boardSelection == 3)
        {
            options.add(GameImages.Keys.BoardPlastic);
        }
        else if (boardSelection == 4)
        {
            options.add(GameImages.Keys.BoardWood);
        }
        else
        {
            options.add(GameImages.Keys.BoardOriginal);
        }

        //pick random choice
        int index = engine.getRandom().nextInt(options.size());

        //are we rendering isometric?
        final boolean isometric = (engine.getMenu().getOptionSelectionIndex(LayerKey.Options, OptionKey.Render) != 0);
        
        if (isometric)
        {
            //create the board
            board = new BoardIsometric(options.get(index));
        
            //render a new image for the board
            board.renderImage();
            
            //set the location of the board, now that the piece have been placed
            board.setX(BOARD_START_X);
            board.setY(BOARD_START_Y);
        }
        else
        {
            //create new board and assign board image
            board = new Board2d(engine.getResources().getGameImage(options.get(index)));
            
            //render a new image for the board
            board.renderImage();
            
            //set the location of the board, now that the piece have been placed
            board.setX((Shared.ORIGINAL_WIDTH / 2) - (board.getWidth() / 2));
            board.setY((Shared.ORIGINAL_HEIGHT / 2) - (board.getHeight() / 2));
        }
        
        if (players == null)
        {
            //reset options list
            options.clear();
            
            //get the piece selection
            final int pieceSelection = engine.getMenu().getOptionSelectionIndex(LayerKey.Options, OptionKey.Piece);
            
            //first selection is random
            if (pieceSelection == 0)
            {
                options.add(GameImages.Keys.PiecesRegular);
                options.add(GameImages.Keys.PiecesMarble);
                options.add(GameImages.Keys.PiecesStone);
                options.add(GameImages.Keys.PiecesOriginal);
            }
            else if (pieceSelection == 1)
            {
                options.add(GameImages.Keys.PiecesRegular);
            }
            else if (pieceSelection == 2)
            {
                options.add(GameImages.Keys.PiecesMarble);
            }
            else if (pieceSelection == 3)
            {
                options.add(GameImages.Keys.PiecesStone);
            }
            else
            {
                options.add(GameImages.Keys.PiecesOriginal);
            }
            
            //pick random choice
            index = engine.getRandom().nextInt(options.size());
            
            //create the players and assign the pieces image
            players = new Players(engine.getResources().getGameImage(options.get(index)), engine.getRandom().nextBoolean());
        }
        
        //reset the player's pieces
        players.reset();
        
        //assign the x,y coordinates of the pieces
        players.assignCoordinates(board);
        
        if (background == null)
            background = new Background(engine.getResources().getGameImage(GameImages.Keys.Background));
    }
    
    public Players getPlayers()
    {
        return this.players;
    }
    
    public Board getBoard()
    {
        return this.board;
    }
    
    @Override
    public Rectangle getWindow()
    {
        return this.window;
    }
    
    @Override
    public void setWindow(final Rectangle window)
    {
        this.window = new Rectangle(window);
    }
    
    /**
     * Free up resources
     */
    @Override
    public void dispose()
    {
        if (window != null)
            window = null;
        
        if (players != null)
        {
            players.dispose();
            players = null;
        }
        
        if (board != null)
        {
            board.dispose();
            board = null;
        }
        
        try
        {
            //recycle objects
            super.finalize();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Update all elements
     * @param engine Our game engine
     * @throws Exception 
     */
    @Override
    public void update(final Engine engine) throws Exception
    {
        //update the players
        players.update(engine);
        
        //update the scrolling background
        background.update(engine.getMain().getTime());
    }
    
    /**
     * Draw all of our application elements
     * @param graphics Graphics object used for drawing
     */
    @Override
    public void render(final Graphics graphics) throws Exception
    {
        //draw the scrolling background
        background.render(graphics);
        
        //draw the entire custom image representing the game board
        board.render(graphics);
        
        //draw the player's pieces
        players.render(graphics);
    }
}