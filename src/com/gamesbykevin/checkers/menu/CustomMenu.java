package com.gamesbykevin.checkers.menu;

import com.gamesbykevin.framework.display.FullScreen;
import com.gamesbykevin.framework.input.Mouse;
import com.gamesbykevin.framework.menu.*;
import com.gamesbykevin.framework.resources.FontManager;
import com.gamesbykevin.framework.resources.ImageManager;

import com.gamesbykevin.checkers.main.Main;
import com.gamesbykevin.checkers.engine.Engine;
import com.gamesbykevin.checkers.resources.Resources;
import com.gamesbykevin.checkers.shared.IElement;
import com.gamesbykevin.checkers.shared.Shared;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

/**
 * Custom menu setup
 * @author GOD
 */
public final class CustomMenu extends Menu implements IElement
{
    //object used to switch container to full screen
    private FullScreen screen;
    
    //previous Layer key used so when container loses focus we remember where we were at
    private Object previousLayerKey;
    
    /**
     * Identify each option we want to access
     * Spelling should match the options id=? in the .xml file
     */
    public enum OptionKey 
    {
        Sound, FullScreen, 
        Difficulty, Hint, Mode
    }
    
    /**
     * The layer keys we want to access.
     * Spelling of LayerKey has to match layer id = "" in menu.xml file
     */
    public enum LayerKey 
    {
        Initial, Credits, MainTitle, StaticCredits, Options,
        Controls, Instructions, GameStart, OptionsInGame, NewGameConfirm,
        CreateNewGame, ExitGameConfirm, ExitGameConfirmed, NoFocus
    }
    
    //is full screen enabled
    private boolean fullscreen = false;
    
    //is the sound enabled
    private boolean sound = true;
    
    //does the java container have focus
    private boolean focus = true;
    
    //values for sound
    public static final int SOUND_ENABLED = 0;
    public static final int SOUND_DISABLED = 1;
    
    //values for fullscreen
    public static final int FULLSCREEN_ENABLED = 1;
    public static final int FULLSCREEN_DISABLED = 0;
    
    //the images for the mouse cursor will be contained here
    private ImageManager images;
    
    /**
     * Unique id's used to access the resources and must match the id in the xml file
     */
    private enum MouseKey
    {
        Mouse, MouseDrag
    }
    
    //object that contains all the fonts for the menu
    private FontManager fonts;
    
    //the default font size
    private static final float DEFAULT_FONT_SIZE = 26f;
    
    /**
     * Unique id's used to access the resources and must match the id in the xml file
     */
    private enum FontKey
    {
        Menu
    }
    
    //the mouse images are provided in this node name
    private static final String MOUSE_NODE_NAME = "mouseImage";
    
    public CustomMenu(final Engine engine) throws Exception
    {
        //set the container the menu will reside within
        super(engine.getMain().getScreen(), Resources.XML_CONFIG_MENU, engine.getMain().getContainerClass());
        
        //set the first layer
        super.setLayer(LayerKey.Initial);
        
        //if debugging go straight to game
        if (Shared.DEBUG)
            super.setLayer(LayerKey.GameStart);
        
        //set the last layer so we know when the menu has completed
        super.setFinish(LayerKey.GameStart);
        
        //create container for mouse cursor images, use special node name for the mouse images
        this.images = new ImageManager(Resources.XML_CONFIG_MENU, MOUSE_NODE_NAME);
        
        //load the mouse images
        while(!images.isComplete())
        {
            images.update(engine.getMain().getContainerClass());
        }
        
        //verify if anything is incorrect
        images.verifyLocations(MouseKey.values());
        
        //create container for any menu fonts
        this.fonts = new FontManager(Resources.XML_CONFIG_MENU);
        
        //load the font(s)
        while(!fonts.isComplete())
        {
            fonts.update(engine.getMain().getContainerClass());
        }
        
        //verify if anything is incorrect
        fonts.verifyLocations(FontKey.values());
        
        //get the font just added and change the Font Size
        fonts.set(FontKey.Menu, fonts.get(FontKey.Menu).deriveFont(DEFAULT_FONT_SIZE));
    }
    
    /**
     * Update game menu
     * @param engine Our game engine containing all resources etc... needed to update menu
     * 
     * @throws Exception 
     */
    @Override
    public void update(final Engine engine) throws Exception
    {
        //if the menu is not on the last layer we need to check for changes made in the menu
        if (!super.hasFinished())
        {
            //are we currently in one of these layers
            boolean optionsInGame = super.hasCurrent(LayerKey.OptionsInGame);
            boolean optionsMain = super.hasCurrent(LayerKey.Options);
            
            //flag to check if anything changed
            boolean changeSound = false;
            boolean changeFullscreen = false;
            
            if (optionsInGame)
            {
                //if setting is not what we have stored, there was a change
                if (this.sound != (getOptionSelectionIndex(LayerKey.OptionsInGame, OptionKey.Sound) == SOUND_ENABLED))
                    changeSound = true;
                if (this.fullscreen != (getOptionSelectionIndex(LayerKey.OptionsInGame, OptionKey.FullScreen) == FULLSCREEN_ENABLED))
                    changeFullscreen = true;
            }
            else if (optionsMain)
            {
                //if setting is not what we have stored, there was a change
                if (this.sound != (getOptionSelectionIndex(LayerKey.Options, OptionKey.Sound) == SOUND_ENABLED))
                    changeSound = true;
                if (this.fullscreen != (getOptionSelectionIndex(LayerKey.Options, OptionKey.FullScreen) == FULLSCREEN_ENABLED))
                    changeFullscreen = true;
            }
            
            //if a sound change was made
            if (changeSound)
            {
                //update audio settings
                switchAudioEnabled(engine.getResources());
            }
            
            //if the full screen option changed
            if (changeFullscreen)
            {
                //switch full screen setting
                switchFullscreenEnabled(engine.getMain());
            }
            
            //if starting a new game change layer, stop all sound
            if (super.hasCurrent(LayerKey.CreateNewGame))
            {
                //go to specified layer
                super.setLayer(LayerKey.GameStart);
            }
            
            //if the values are not equal a change was made
            if (focus != engine.getMain().hasFocus())
            {
                //if the previous Layer is stored
                if (previousLayerKey != null)
                {
                    //set the menu to the previous Layer
                    super.setLayer(previousLayerKey);
                    
                    //there no longer is a previous Layer
                    previousLayerKey = null;
                }
                else
                {
                    //the previous Layer has not been set 
                    previousLayerKey = getKey();
                    
                    //set the current Layer to NoFocus
                    super.setLayer(LayerKey.NoFocus);
                }
                
                this.focus = !focus;
            }
            
            super.update(engine.getMouse(), engine.getKeyboard(), engine.getMain().getTime());
            
            //if confirming exit from the game, stop sound
            if (super.hasCurrent(LayerKey.ExitGameConfirmed))
            {
                //reset game upon exit
                engine.reset();
                
                //go to specified layer
                super.setLayer(LayerKey.MainTitle);
            }
            
            //if we now have finished the menu flag the engine to reset
            if (super.hasFinished())
            {
                //if we previously weren't in this layer reset the game
                if (!optionsInGame)
                    engine.reset();
            }
        }
        else
        {
            //the menu has finished and the user has pressed 'escape' so we will bring up the in game options
            if (engine.getKeyboard().hasKeyPressed(KeyEvent.VK_ESCAPE))
            {
                super.setLayer(LayerKey.OptionsInGame);
                engine.getKeyboard().reset();
            }
        }
    }
    
    /**
     * Switch full screen setting, enabled/disabled
     * @param main Container containing panel/applet where game play is rendered
     */
    public void switchFullscreenEnabled(final Main main) throws Exception
    {
        //flip setting
        fullscreen = !fullscreen;

        //create screen if null
        if (screen == null)
            screen = new FullScreen();

        //switch screen
        if (main.getApplet() != null)
        {
            screen.switchFullScreen(main.getApplet());
        }
        else
        {
            screen.switchFullScreen(main.getPanel());
        }

        //grab the rectangle coordinates of the full screen
        main.setFullScreen();

        //set option in other layer to match
        getOption(LayerKey.Options, OptionKey.FullScreen).setIndex((fullscreen) ? FULLSCREEN_ENABLED : FULLSCREEN_DISABLED);

        //flag a new image to be drawn for that layer
        getLayer(LayerKey.Options).resetOptionsImage();
        
        //set option in other layer to match
        getOption(LayerKey.OptionsInGame, OptionKey.FullScreen).setIndex((fullscreen) ? FULLSCREEN_ENABLED : FULLSCREEN_DISABLED);

        //flag a new image to be drawn for that layer
        getLayer(LayerKey.OptionsInGame).resetOptionsImage();
    }
    
    /**
     * Switch the current audio setting in the game.
     * @param resources Object containing audio resources
     */
    public void switchAudioEnabled(final Resources resources) throws Exception
    {
        //flip audio setting
        this.sound = !this.sound;
        
        //set the audio setting for this menu also
        setEnabled(sound);

        if (resources != null)
        {
            //change was made so set opposite to our stored setting
            resources.setAudioEnabled(sound);

            //if sound is not enabled, stop all sound
            if (!sound)
                resources.stopAllSound();
        }

        //set option in other layer to correct value
        getOption(LayerKey.Options, OptionKey.Sound).setIndex((sound) ? SOUND_ENABLED : SOUND_DISABLED);

        //flag a new image to be drawn for that layer
        getLayer(LayerKey.Options).resetOptionsImage();
        
        //set option in other layer to match
        getOption(LayerKey.OptionsInGame, OptionKey.Sound).setIndex((sound) ? SOUND_ENABLED : SOUND_DISABLED);

        //flag a new image to be drawn for that layer
        getLayer(LayerKey.OptionsInGame).resetOptionsImage();
    }
    
    public boolean hasFocus()
    {
        return focus;
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        //set menu font
        graphics.setFont(fonts.get(FontKey.Menu));
        
        //draw menu
        super.render(graphics);
    }
    
    /**
     * Draw the mouse
     * @param graphics Graphics object used to write mouse
     * @param mouse Object representing the state of the mouse
     */
    public void renderMouse(final Graphics graphics, final Mouse mouse)
    {
        if (mouse.getLocation() != null && images != null)
        {
            if (mouse.isMouseDragged())
            {
                graphics.drawImage(images.get(MouseKey.MouseDrag), mouse.getLocation().x, mouse.getLocation().y, null);
            }
            else
            {
                graphics.drawImage(images.get(MouseKey.Mouse), mouse.getLocation().x, mouse.getLocation().y, null);
            }
        }
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        if (screen != null)
        {
            screen.dispose();
            screen = null;
        }
        
        if (images != null)
        {
            images.dispose();
            images = null;
        }

        if (fonts != null)
        {
            fonts.dispose();
            fonts = null;
        }
        
        previousLayerKey = null;
    }
}