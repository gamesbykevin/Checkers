package com.gamesbykevin.checkers.board;

import com.gamesbykevin.framework.awt.CustomImage;

import com.gamesbykevin.checkers.piece.Checker;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

/**
 * This class is the game board
 * @author GOD
 */
public final class Board extends CustomImage
{
    /**
     * The dimensions (pixel width/height) of a single cell on the board
     */
    public static final int CELL_DIMENSIONS = 32;
    
    /**
     * Total dimensions of the playable board
     */
    public static final int COLUMNS = 8;
    
    /**
     * Total dimensions of the playable board
     */
    public static final int ROWS = 8;
    
    /**
     * The minimum column of the playable board
     */
    public static final int COLUMNS_MIN = 0;
    
    /**
     * The maximum column of the playable board
     */
    public static final int COLUMNS_MAX = COLUMNS - 1;
    
    //the min/max row of the playable board
    /**
     * The minimum row of the playable board
     */
    public static final int ROWS_MIN = 0;
    
    /**
     * The maximum row of the playable board
     */
    public static final int ROWS_MAX = ROWS - 1;
    
    //how large is the entire board including the part not playable (border)
    private static final int BOARD_COLUMNS = COLUMNS + 2;
    private static final int BOARD_ROWS = ROWS + 2;
    
    //the area of our entire custom image
    private Rectangle imageDimension;
    
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
    
    public Board(final Image boardSpriteSheet)
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
     * Is the column, row within the playable board boundary?<br>
     * @param column Column
     * @param row Row
     * @return true if within the playable game board, false otherwise
     */
    public static boolean hasBounds(final int column, final int row)
    {
        //make sure within column
        if (column >= COLUMNS_MIN && column <= COLUMNS_MAX)
        {
            //make sure within row
            if (row >= ROWS_MIN && row <= ROWS_MAX)
                return true;
        }
        
        //location not in bounds
        return false;
    }
    
    /**
     * Get the x-coordinate for this piece.<br>
     * @param checker The piece containing the column
     * @return The x-coordinate for the column
     * @throws Exception If the specified location is out of bounds
     */
    public int getCoordinateX(final Checker checker) throws Exception
    {
        return this.getCoordinateX(checker.getCol());
    }
    
    /**
     * Get the row at the associated column
     * @param x x-coordinate
     * @return The column, if not in the board range -1 will be returned
     */
    public int getColumn(final int x) throws Exception
    {
        for (int col = 0; col < COLUMNS; col++)
        {
            //the start coordinate
            final int startX = this.getCoordinateX(col);
            
            if (x >= startX && x <= startX + CELL_DIMENSIONS)
                return col;
        }
        
        return -1;
    }
    
    /**
     * Get the row at the associated row
     * @param y y-coordinate
     * @return The row, if not in the board range -1 will be returned
     */
    public int getRow(final int y) throws Exception
    {
        for (int row = 0; row < ROWS; row++)
        {
            //the start coordinate
            final int startY = this.getCoordinateY(row);
            
            if (y >= startY && y <= startY + CELL_DIMENSIONS)
                return row;
        }
        
        return -1;
    }
    
    /**
     * Get the x-coordinate of the specified column.<br>
     * Only the playable cells on the board will be targeted here
     * @param col Column
     * @return The x-coordinate for the column
     * @throws Exception If the specified location is out of bounds
     */
    public int getCoordinateX(final int col) throws Exception
    {
        if (col < 0 || col >= COLUMNS)
            throw new Exception("Column is out of range");
        
        return (int)(getX() + CELL_DIMENSIONS) + (CELL_DIMENSIONS * col);
    }
    
    /**
     * Get the y-coordinate for this piece.<br>
     * @param checker The piece containing the row
     * @return The y-coordinate for the row
     * @throws Exception If the specified location is out of bounds
     */
    public int getCoordinateY(final Checker checker) throws Exception
    {
        return this.getCoordinateY(checker.getRow());
    }
    
    /**
     * Get the y-coordinate of the specified row on the board.<br>
     * Only the playable cells on the board will be targeted here
     * @param row Row
     * @return The y-coordinate for the row
     * @throws Exception If the specified location is out of bounds
     */
    public int getCoordinateY(final int row) throws Exception
    {
        if (row < 0 || row >= ROWS)
            throw new Exception("Row is out of range");
        
        return (int)(getY() + CELL_DIMENSIONS) + (CELL_DIMENSIONS * row);
    }
    
    @Override
    public void renderImage() throws Exception
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
        
        //also reset the coordinates
        super.setX(0);
        super.setY(0);
        
        //assign the entire image dimensions
        imageDimension = new Rectangle(0, 0, (int)getWidth(), (int)getHeight());
    }
    
    @Override
    public void draw(final Graphics graphics) throws Exception
    {
        super.draw(graphics, super.getBufferedImage(), imageDimension);
    }
}