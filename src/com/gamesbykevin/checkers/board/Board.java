package com.gamesbykevin.checkers.board;

import com.gamesbykevin.framework.awt.CustomImage;

import com.gamesbykevin.checkers.piece.Checker;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * This class is the game board
 * @author GOD
 */
public abstract class Board extends CustomImage
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
    protected static final int BOARD_COLUMNS = COLUMNS + 2;
    protected static final int BOARD_ROWS = ROWS + 2;
    
    //the area of our entire custom image
    private Rectangle imageDimension;
    
    public Board(final int width, final int height)
    {
        super(width, height);
    }
    
    /**
     * Is the column, row within the playable board boundary?<br>
     * @param column Column
     * @param row Row
     * @return true if the location is within the playable game board, false otherwise
     */
    public static boolean hasBoundary(final double column, final double row)
    {
        return hasBoundary((int)column, (int)row);
    }
    
    /**
     * Is the column, row within the playable board boundary?<br>
     * @param column Column
     * @param row Row
     * @return true if the location is within the playable game board, false otherwise
     */
    public static boolean hasBoundary(final int column, final int row)
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
     * Each board will need to place the piece accordingly
     * @param piece The piece we want to place on the board
     */
    public abstract void placePiece(final Checker piece);
    
    /**
     * Each board will need to implement a way to determine what column is at the specified location
     * @param x x-coordinate
     * @param y y-coordinate
     * @return The column on the playable game board for this location
     * @throws Exception
     */
    public abstract int getColumn(final int x, final int y) throws Exception;
    
    /**
     * Each board will need to implement a way to determine what row is at the specified location
     * @param x x-coordinate
     * @param y y-coordinate
     * @return The row on the playable game board for this location
     * @throws Exception
     */
    public abstract int getRow(final int x, final int y) throws Exception;
    
    /**
     * Each board will need to determine where the x-coordinate is at
     * @param col Column
     * @param row Row
     * @return The starting x-coordinate at the specified (column, row)
     */
    public abstract int getCoordinateX(final double col, final double row);
    
    /**
     * Each board will need to determine where the y-coordinate is at
     * @param col Column
     * @param row Row
     * @return The starting y-coordinate at the specified (column, row)
     */
    public abstract int getCoordinateY(final double col, final double row);
    
    /**
     * Get the image dimension.
     * @return The image dimension of the fully rendered board
     */
    protected Rectangle getImageDimension()
    {
        return this.imageDimension;
    }
    
    /**
     * Assign the image dimension.<br>
     * This will be the dimension of the entire image.
     * @param width Width
     * @param height Height
     */
    protected void assignImageDimension(final int width, final int height)
    {
        //if this has not been created yet, create rectangle
        if (imageDimension == null)
            imageDimension = new Rectangle();
        
        //set the size of the image dimension
        imageDimension.setSize(width, height);
    }
    
    /**
     * Draw the entire game board.
     * @param graphics Object used to write graphics
     * @throws Exception 
     */
    public void render(final Graphics graphics) throws Exception
    {
        super.draw(graphics, super.getBufferedImage(), getImageDimension());
    }
}