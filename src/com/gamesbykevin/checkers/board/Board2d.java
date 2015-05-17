package com.gamesbykevin.checkers.board;

import com.gamesbykevin.checkers.piece.Checker;
import java.awt.Image;

/**
 * This will draw a 2d board
 * @author GOD
 */
public final class Board2d extends Board
{
    public enum SpriteKey
    {
        BORDER_NW(1, 0), 
        BORDER_N(2, 0), 
        BORDER_NE(3, 0),
        BORDER_W(1, 1), 
        BORDER_E(3, 1),
        BORDER_SW(1, 2), 
        BORDER_S(2, 2), 
        BORDER_SE(3, 2),
        LIGHT_CELL(0,0),
        DARK_CELL(0,1);
        
        final int col;
        final int row;
        
        private SpriteKey(final int col, final int row)
        {
            this.col = col;
            this.row = row;
        }
    }
    
    public Board2d(final Image boardSpriteSheet)
    {
        //create an image for the entire board
        super((BOARD_COLUMNS) * CELL_DIMENSIONS, (BOARD_ROWS) * CELL_DIMENSIONS);
        
        //assign the spritesheet
        super.setImage(boardSpriteSheet);
        
        //create the sprite sheet
        super.createSpriteSheet();
        
        //map out dimensions and add to spritesheet
        for (SpriteKey key : SpriteKey.values())
        {
            //add single animations to the spritesheet
            super.getSpriteSheet().add(key.col * CELL_DIMENSIONS, key.row * CELL_DIMENSIONS, CELL_DIMENSIONS, CELL_DIMENSIONS, 0, key);
        }
    }
    
    /**
     * Get the x-coordinate of the specified column.<br>
     * @param col Column
     * @param row Row
     * @return The x-coordinate for the column
     */
    @Override
    public int getCoordinateX(final double col, final double row)
    {
        return (int)((getX() + CELL_DIMENSIONS) + (CELL_DIMENSIONS * col));
    }
    
    /**
     * Get the y-coordinate of the specified location on the board.<br>
     * @param col Column
     * @param row Row
     * @return The y-coordinate for the location
     */
    @Override
    public int getCoordinateY(final double col, final double row)
    {
        return (int)((getY() + CELL_DIMENSIONS) + (CELL_DIMENSIONS * row));
    }
    
    /**
     * Place the piece on the board.<br>
     * Basically we will assign the appropriate x,y coordinates for the checker piece.
     * @param piece The piece we want to place
     */
    @Override
    public void placePiece(final Checker piece)
    {
        piece.setX(getCoordinateX(piece.getCol(), 0));
        piece.setY(getCoordinateY(0, piece.getRow()));
    }
    
    /**
     * Get the column at the associated location
     * @param x x-coordinate
     * @param y y-coordinate
     * @return The column, if not in the board range -1 will be returned
     */
    @Override
    public int getColumn(final int x, final int y)
    {
        for (int col = 0; col < COLUMNS; col++)
        {
            //the start coordinate
            final int startX = getCoordinateX(col, 0);
            
            if (x >= startX && x <= startX + CELL_DIMENSIONS)
                return col;
        }
        
        return -1;
    }
    
    /**
     * Get the row at the associated location
     * @param x x-coordinate
     * @param y y-coordinate
     * @return The row, if not in the board range -1 will be returned
     */
    @Override
    public int getRow(final int x, final int y)
    {
        for (int row = 0; row < ROWS; row++)
        {
            //the start coordinate
            final int startY = getCoordinateY(0, row);
            
            if (y >= startY && y <= startY + CELL_DIMENSIONS)
                return row;
        }
        
        return -1;
    }
    
    @Override
    public void render() throws Exception
    {
        //clear the existing image
        super.clear();
        
        //we will alternate between light and dark cells
        boolean light = true;
        
        //the size of each cell will remain the same
        super.setWidth(CELL_DIMENSIONS);
        super.setHeight(CELL_DIMENSIONS);
        
        for (int row = 0; row < BOARD_ROWS; row++)
        {
            for (int col = 0; col < BOARD_COLUMNS; col++)
            {
                //the animation to use for this specific cell
                final SpriteKey key;
                
                if (col == 0)
                {
                    if (row == 0)
                    {
                        key = SpriteKey.BORDER_NW;
                    }
                    else if (row == BOARD_ROWS - 1)
                    {
                        key = SpriteKey.BORDER_SW;
                    }
                    else
                    {
                        key = SpriteKey.BORDER_W;
                    }
                }
                else if (col == BOARD_COLUMNS - 1)
                {
                    if (row == 0)
                    {
                        key = SpriteKey.BORDER_NE;
                    }
                    else if (row == BOARD_ROWS - 1)
                    {
                        key = SpriteKey.BORDER_SE;
                    }
                    else
                    {
                        key = SpriteKey.BORDER_E;
                    }
                }
                else if (row == 0)
                {
                    if (col == 0)
                    {
                        key = SpriteKey.BORDER_NW;
                    }
                    else if (col == BOARD_COLUMNS - 1)
                    {
                        key = SpriteKey.BORDER_NE;
                    }
                    else
                    {
                        key = SpriteKey.BORDER_N;
                    }
                }
                else if (row == BOARD_ROWS - 1)
                {
                    if (col == 0)
                    {
                        key = SpriteKey.BORDER_SW;
                    }
                    else if (col == BOARD_COLUMNS - 1)
                    {
                        key = SpriteKey.BORDER_SE;
                    }
                    else
                    {
                        key = SpriteKey.BORDER_S;
                    }
                }
                else
                {
                    //if not the first column, alter the color
                    if (col != 1)
                        light = !light;

                    key = (light) ? SpriteKey.LIGHT_CELL : SpriteKey.DARK_CELL;
                }
                
                //assign the animation to render
                super.getSpriteSheet().setCurrent(key);
     
                //set the appropriate coordinates
                super.setX(col * CELL_DIMENSIONS);
                super.setY(row * CELL_DIMENSIONS);

                //now draw animation to our image
                super.draw(getGraphics2D());
            }
        }
        
        //now we are done rendering the board, set the dimensions of the entire board
        super.setWidth(CELL_DIMENSIONS * BOARD_COLUMNS);
        super.setHeight(CELL_DIMENSIONS * BOARD_ROWS);
        
        //assign the entire image dimensions
        assignImageDimension((int)getWidth(), (int)getHeight());
    }
}