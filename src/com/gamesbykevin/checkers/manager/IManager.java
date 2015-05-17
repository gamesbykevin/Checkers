package com.gamesbykevin.checkers.manager;

import com.gamesbykevin.checkers.engine.Engine;
import com.gamesbykevin.checkers.shared.IElement;


/**
 * IManager interface will also include a way to reset the game
 * @author GOD
 */
public interface IManager extends IElement
{
    /**
     * Provide a way to reset the game elements
     * @param engine The Engine containing resources etc... if needed
     * @throws Exception 
     */
    public void reset(final Engine engine) throws Exception;
}