package com.gamesbykevin.checkers.background;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;

import java.awt.Graphics;
import java.awt.Image;

/**
 * This will manage a scrolling background
 * @author GOD
 */
public final class Background extends Sprite
{
    //the destination for the background
    private final int imageWidth;
    
    //the timer to scroll the background
    private Timer timer;
    
    //the delay for the background to repeat
    private static final long DELAY = Timers.toNanoSeconds(15000L);
    
    public Background(final Image image)
    {
        //assign background
        super.setImage(image);
        
        //store the width
        this.imageWidth = image.getWidth(null);
        
        //create a new timer
        this.timer = new Timer(DELAY);
        
        //assign the dimensions
        super.setDimensions(imageWidth);
    }
    
    public void update(final long time)
    {
        //update timer
        timer.update(time);
        
        if (timer.hasTimePassed())
        {
            timer.reset();
            super.setX(0);
        }
        else
        {
            //the x pixels to offset
            double offsetX = timer.getProgress() * imageWidth;
            
            super.setX(-offsetX);
        }
    }
    
    public void render(final Graphics graphics) throws Exception
    {
        //store original location
        final double x = getX();
        
        //draw background
        super.draw(graphics);
        
        //move location forward
        super.setX(x + imageWidth);
        
        //draw background
        super.draw(graphics);
        
        //restore location
        super.setX(x);
    }
}
