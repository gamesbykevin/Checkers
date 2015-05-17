package com.gamesbykevin.checkers.shared;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.checkers.engine.Engine;

import java.awt.Graphics;

/**
 * Basic methods required for game elementsMethods needed for game elements
 * @author GOD
 */
public interface IElement extends Disposable
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
}