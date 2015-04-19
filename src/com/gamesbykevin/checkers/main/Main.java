package com.gamesbykevin.checkers.main;

import java.awt.*;
import javax.swing.*;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.checkers.engine.Engine;
import com.gamesbykevin.checkers.shared.Shared;

public final class Main extends Thread implements Disposable
{
    //image where all game/menu elements will be written to
    private Image bufferedImage;
    
    //Graphics object used to draw buffered image
    private Graphics bufferedImageGraphics;
    
    //our dimensions for the original screen window
    private Rectangle originalSizeWindow;
    
    //our dimensions for the full screen window
    private Rectangle fullSizeWindow;
    
    //our dimensions for keeping track of the size of the current window
    private Rectangle currentWindow;
    
    //our main game engine
    private Engine engine;
    
    //how many nanoseconds are there in one millisecond
    private static final double NANO_SECONDS_PER_MILLISECOND = 1000000.0;
    
    //how many nanoseconds are there in one second
    private static final double NANO_SECONDS_PER_SECOND = 1000000000.0;
    
    //need double for accuracy
    private double nanoSecondsPerUpdate;
    
    //reference to our applet
    private JApplet applet;
    
    //reference to our panel
    private JPanel panel;
    
    //cache this graphics object so we aren't constantly creating it
    private Graphics graphics;
    
    //is the thread active
    private boolean active = true;
    
    public Main(final int ups, final JApplet applet)
    {
        this(ups);
        
        this.applet = applet;
    }
    
    public Main(final int ups, final JPanel panel)
    {
        this(ups);
        
        this.panel = panel;
    }
    
    /**
     * Main class that manages the game engine
     * 
     * @param ups Desired updates per second
     */
    private Main(final int ups)
    {
        //the dimensions used for original/full screen
        originalSizeWindow = new Rectangle(0, 0, Shared.ORIGINAL_WIDTH, Shared.ORIGINAL_HEIGHT);
        fullSizeWindow     = new Rectangle(originalSizeWindow);

        //duration of each update in nanoseconds
        this.nanoSecondsPerUpdate = NANO_SECONDS_PER_SECOND / ups;
    }
    
    /**
     * Mark objects for garbage collection
     */
    @Override
    public void dispose()
    {
        if (bufferedImage != null)
        {
            bufferedImage.flush();
            bufferedImage = null;
        }
        
        if (bufferedImageGraphics != null)
        {
            bufferedImageGraphics.dispose();
            bufferedImageGraphics = null;
        }
        
        if (graphics != null)
        {
            graphics.dispose();
            graphics = null;
        }
        
        if (engine != null)
        {
            engine.dispose();
            engine = null;
        }
        
        if (applet != null)
        {
            applet.destroy();
            applet = null;
        }
        
        if (panel != null)
        {
            panel.removeAll();
            panel = null;
        }
        
        originalSizeWindow = null;
        fullSizeWindow = null;
        currentWindow = null;
    }
    
    /**
     * Create our main game engine and apply input listeners
     */
    public void create() throws Exception
    {
        engine = new Engine(this);
        
        //now that engine is created apply listeners so we can detect key/mouse input
        if (applet != null)
        {
            applet.addKeyListener(engine);
            applet.addMouseMotionListener(engine);
            applet.addMouseListener(engine);
        }
        else
        {
            panel.addKeyListener(engine);
            panel.addMouseMotionListener(engine);
            panel.addMouseListener(engine);
        }
    }
    
    @Override
    public void run()
    {
        //keep track of the number of updates
        int updates = 0;
        
        //store the time to track ups (updates per second)
        long previous = System.nanoTime();
        
        while (active)
        {
            try
            {
                //get the current time
                final long before = System.nanoTime();
                
                //update game
                engine.update(this);
                
                //render image
                renderImage();

                //draw image
                drawScreen();
                
                //keep track of the number of updates
                updates++;
                
                //get the time after processing complete
                final long after = System.nanoTime();
                
                //if we are debugging
                if (Shared.DEBUG)
                {
                    //if 1 second has passed display ups
                    if (after - previous >= NANO_SECONDS_PER_SECOND)
                    {
                        //display updates per second
                        System.out.println("UPS = " + updates);

                        //reset count
                        updates = 0;

                        //update the previous time with the current
                        previous = after;
                    }
                }
                
                //get the time passed for this update (in nanoseconds)
                final long passed = (after - before);
                
                //get the extra leftover time
                double remaining = nanoSecondsPerUpdate - passed;
                
                //the time remaining can't be negative
                if (remaining < 0)
                    remaining = 0;
                
                //get the milliseconds to sleep
                long millis = (long)(remaining / NANO_SECONDS_PER_MILLISECOND);
                
                //take the remainder to get the nanoseconds
                int nanos = (int)(((remaining / NANO_SECONDS_PER_MILLISECOND) - (double)millis) * NANO_SECONDS_PER_MILLISECOND);
                
                //sleep thread for the specified amount so each second maintains the same number of updates
                Thread.sleep(millis, nanos);
            }
            catch(Exception e)
            {
                //dislay error
                e.printStackTrace();

                //no longer active thread
                active = false;
            }
        }
    }
    
    /**
     * Set this thread active.<br>
     * If the thread is not active the game will end
     * @param active true=yes, false=no
     */
    public void setActive(final boolean active)
    {
        this.active = active;
    }
    
    public JApplet getApplet()
    {
        return applet;
    }
    
    public JPanel getPanel()
    {
        return panel;
    }
    
    public Class<?> getContainerClass()
    {
        if (applet != null)
            return applet.getClass();
        
        if (panel != null)
            return panel.getClass();
        
        return null;
    }
    
    /**
     * Create buffered Image
     */
    private void createBufferedImage()
    {
        if (applet != null)
        {
            bufferedImage = applet.createImage(originalSizeWindow.width, originalSizeWindow.height);
        }
        else
        {
            bufferedImage = panel.createImage(originalSizeWindow.width, originalSizeWindow.height);
        }
    }
    
    /**
     * Get the size of the original window
     * @return Rectangle
     */
    public Rectangle getScreen()
    {
        return this.originalSizeWindow;
    }
    
    /**
     * This method will be called whenever the user turns full-screen on/off
     */
    public void setFullScreen()
    {
        if (applet != null)
        {
            fullSizeWindow = new Rectangle(0, 0, applet.getWidth(), applet.getHeight());
        }
        else
        {
            fullSizeWindow = new Rectangle(0, 0, panel.getWidth(), panel.getHeight());
        }
        
        //set the current window size
        currentWindow = new Rectangle(fullSizeWindow);
        
        //since full screen switched on/off create a new graphics object
        createGraphicsObject();
    }
    
    /**
     * Get the number of nanoseconds per each update.
     * @return long The nanosecond duration per each update which is based on updates per second.
     */
    public long getTime()
    {
        return (long)nanoSecondsPerUpdate;
    }
    
    /**
     * Writes all game/menu elements in our 
     * engine to our single bufferedImage.
     * 
     * @throws Exception 
     */
    private void renderImage() throws Exception
    {
        if (bufferedImage != null)
        {
            if (bufferedImageGraphics == null)
                bufferedImageGraphics = bufferedImage.getGraphics();
            
            //background by itself will be a black rectangle
            bufferedImageGraphics.setColor(Color.BLACK);
            bufferedImageGraphics.fillRect(0, 0, Shared.ORIGINAL_WIDTH, Shared.ORIGINAL_HEIGHT);

            engine.render(bufferedImageGraphics);
        }
        else
        {
            //create the image that will be displayed to the user
            createBufferedImage();
        }
    }
    
    /**
     * Do we have focus? <br>
     * If this is a JPanel it will always return true
     * @return true if the focus is on this container, false otherwise
     */
    public boolean hasFocus()
    {
        if (applet != null)
        {
            return applet.hasFocus();
        }
        else
        {
            //jPanel will always have focus
            return true;
        }
    }
    
    /**
     * Set the graphic object for drawing the rendered image
     */
    private void createGraphicsObject()
    {
        if (applet != null)
            graphics = applet.getGraphics();
        
        if (panel != null)
            graphics = panel.getGraphics();
    }
    
    /**
     * Draw Image onto screen
     */
    private void drawScreen()
    {
        //if no image has been rendered yet return
        if (bufferedImage == null)
            return;
        
        //cache graphics object to save resources
        if (graphics == null)
            createGraphicsObject();
        
        //make sure current window dimensions are set
        if (currentWindow == null)
            setFullScreen();
        
        try
        {
            //the destination will be the size of the window
            int dx1 = currentWindow.x;
            int dy1 = currentWindow.y;
            int dx2 = currentWindow.x + currentWindow.width;
            int dy2 = currentWindow.y + currentWindow.height;

            //the source will be the entire image
            int sx1 = 0;
            int sy1 = 0;
            int sx2 = bufferedImage.getWidth(null);
            int sy2 = bufferedImage.getHeight(null);
            
            //draw our rendered image at the specified location
            graphics.drawImage(bufferedImage, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}