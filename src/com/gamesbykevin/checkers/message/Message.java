package com.gamesbykevin.checkers.message;

import com.gamesbykevin.framework.awt.CustomImage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

/**
 * Here we will create a custom message to be displayed on screen
 * @author GOD
 */
public final class Message extends CustomImage
{
    //the custom message to display to the user
    private String description1 = "", description2 = "";
    
    //default messages
    public static final String MESSAGE_BEGIN = "Begin Game";
    public static final String MESSAGE_NONE = "";
    public static final String MESSAGE_PIECE_EXISTS = "Piece exists here.";
    public static final String MESSAGE_COMPLETE_JUMP = "Must complete capture.";
    public static final String MESSAGE_JUMP_REQUIRED = "Player must capture opponent.";
    public static final String MESSAGE_INVALID_SELECTION = "Invalid Selection";
    public static final String MESSAGE_INVALID_MOVE = "Invalid Move";
    public static final String MESSAGE_PLAYER_1_TURN = "Player 1 Turn";
    public static final String MESSAGE_PLAYER_2_TURN = "Player 2 Turn";
    public static final String MESSAGE_PLAYER_1_WINS = "Player 1 Wins";
    public static final String MESSAGE_PLAYER_2_WINS = "Player 2 Wins";
    public static final String MESSAGE_RESET = "Press 'R' reset or 'Esc' menu";
    
    //where the custom message is located on the custom image
    private static final int DESCRIPTION_LOCATION_X = 15;
    private static final int DESCRIPTION_LOCATION_Y = 30;
    
    //the dimensions of the message
    private static final int WIDTH = 380;
    private static final int HEIGHT = 73;
    
    //the location of the message
    public static final int LOCATION_X_3D = 66;
    public static final int LOCATION_Y_3D = 430;
    
    //the location of the message
    public static final int LOCATION_X_2D = 66;
    public static final int LOCATION_Y_2D = 375;
    
    //the font height
    private int fontHeight = 10;
    
    public Message(final Image background)
    {
        //call parent constructor
        super(WIDTH, HEIGHT);
        
        //assign our background image for this message
        super.setImage(background);
    }
    
    @Override
    public void setFont(final Font font)
    {
        super.setFont(font);
        
        //store the font height
        this.fontHeight = getGraphics2D().getFontMetrics().getHeight();
    }
    
    /**
     * Set the description
     * @param description The text to display
     */
    public void setDescription1(final String description1)
    {
        if (description1 != null)
        {
            this.description1 = description1;

            //new description has been set, so render a new image
            render();
        }
    }
    
    public String getDescription1()
    {
        return this.description1;
    }
    
    /**
     * Set the description
     * @param description The text to display
     */
    public void setDescription2(final String description2)
    {
        if (description2 != null)
        {
            this.description2 = description2;

            //new description has been set, so render a new image
            render();
        }
    }
    
    public String getDescription2()
    {
        return this.description2;
    }
    
    @Override
    public void render()
    {
        //clear our existing image
        super.clear();
        
        //draw the background image first
        getGraphics2D().drawImage(getImage(), 0, 0, WIDTH, HEIGHT, null);
        
        //set the font color
        getGraphics2D().setColor(Color.BLACK);
        
        //now draw the message
        getGraphics2D().drawString(getDescription1(), DESCRIPTION_LOCATION_X, DESCRIPTION_LOCATION_Y);
        
        //now draw the second message
        getGraphics2D().drawString(getDescription2(), DESCRIPTION_LOCATION_X, DESCRIPTION_LOCATION_Y + fontHeight);
        
        //set the dimensions
        super.setWidth(WIDTH);
        super.setHeight(HEIGHT);
    }
    
    /**
     * Render our custom image
     * @param graphics Object used to draw graphics
     * @throws Exception 
     */
    public void render(final Graphics graphics) throws Exception
    {
        super.draw(graphics, super.getBufferedImage());
    }
}