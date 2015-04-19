package com.gamesbykevin.checkers.manager;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.checkers.engine.Engine;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Basic methods required for game elements
 * @author GOD
 */
public interface IManager extends Disposable
{
    /**
     * Update the game elements accordingly
     * @param engine Object containing all game resources
     * @throws Exception If there is an issue updating game elements
     */
    public void update(final Engine engine) throws Exception;
    
    /**
     * Draw our game element(s) accordingly
     * @param graphics Object used to draw our final image
     * @throws Exception If there is an issue rendering image
     */
    public void render(final Graphics graphics) throws Exception;
    
    /**
     * Provide a way to reset the game elements
     * @param engine The Engine containing resources etc... if needed
     * @throws Exception 
     */
    public void reset(final Engine engine) throws Exception;
    
    /**
     * Set the window
     * @param window The area where the game play will take place
     */
    public void setWindow(final Rectangle window);
    
    /**
     * Get the window
     * @return Area where game play will take place
     */
    public Rectangle getWindow();
}