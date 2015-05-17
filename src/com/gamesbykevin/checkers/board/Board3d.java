package com.gamesbykevin.checkers.board;

import com.gamesbykevin.checkers.piece.Checker;
import com.gamesbykevin.checkers.resources.GameImages;
import com.gamesbykevin.checkers.shared.Shared;
import java.awt.Color;
import java.awt.Polygon;

/**
 * This class will create an isometric board
 * @author GOD
 */
public final class Board3d extends Board
{
    //our temporary polygon for rendering and testing collision
    private Polygon p = new Polygon();
    
    /**
     * For isometric, we need to offset the column for user collision/detection
     */
    private static final double OFFSET_COLUMN = -.2;
    
    /**
     * For isometric, we need to offset the row for user collision/detection
     */
    private static final double OFFSET_ROW = -.5;
    
    /**
     * Isometric will have different dimensions for the board
     */
    private static final int CELL_DIMENSIONS = 48;
    
    //the alternating colors for the board
    private Color light, dark;
    
    public Board3d(GameImages.Keys key)
    {
        super((BOARD_COLUMNS) * CELL_DIMENSIONS, (BOARD_ROWS) * CELL_DIMENSIONS);
        
        switch (key)
        {
            case BoardGlass:
                light = new Color(0f, 1f, 0f, .5f);
                break;
                
            case BoardMarble:
                light = new Color(.19f, .3f, .3f, 1f);
                break;
                
            case BoardPlastic:
                light = new Color(.47f, .53f, .6f, 1f);
                break;
                
            case BoardWood:
                light = new Color(0.8f, .52f, .24f, 1f);
                break;
                
            case BoardOriginal:
            default:
                light = new Color(1f, 0f, 0f, 1f);
                dark = new Color(0f, 0f, 0f, 1f);
                break;
        }
        
        //if dark has not been assigned yet we will make it a darker than the light color
        if (dark == null)
        {
            //the darker color will be 3 times darker than the lighter color
            dark = light.darker().darker().darker();
        }
    }
    
    /**
     * Get the x-coordinate
     * @return x-coordinate of the first cell (0,0)
     */
    private int getStartX()
    {
        return (Shared.ORIGINAL_WIDTH / 2);
    }
    
    /**
     * Get the y-coordinate
     * @return y-coordinate of the first cell (0,0)
     */
    private int getStartY()
    {
        return (int)(CELL_DIMENSIONS / 2);
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
        return (int)((col - row) * (CELL_DIMENSIONS / 2)) + getStartX();
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
        return (int)((col + row) * (CELL_DIMENSIONS / 2)) + getStartY();
    }
    
    /**
     * Place the piece on the board.<br>
     * Basically we will assign the appropriate x,y coordinates for the checker piece.
     * @param piece The piece we want to place
     */
    @Override
    public void placePiece(final Checker piece)
    {
        piece.setX(getCoordinateX(piece.getCol() + OFFSET_COLUMN, piece.getRow() + OFFSET_ROW) + (int)getX());
        piece.setY(getCoordinateY(piece.getCol() + OFFSET_COLUMN, piece.getRow() + OFFSET_ROW) + (int)getY());
    }
    
    /**
     * Get the column at the associated location
     * @param x x-coordinate
     * @param y y-coordinate
     * @return The column, if not in the board range -1 will be returned
     */
    @Override
    public int getColumn(final int x, final int y) throws Exception
    {
        for (int col = COLUMNS_MIN; col <= COLUMNS_MAX; col++)
        {
            for (int row = ROWS_MIN; row <= ROWS_MAX; row++)
            {
                //if the coordinate is within, return result 
                if (getTopSide(col, row).contains(x, y))
                    return (int)col;
            }
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
    public int getRow(final int x, final int y) throws Exception
    {
        for (int col = COLUMNS_MIN; col <= COLUMNS_MAX; col++)
        {
            for (int row = ROWS_MIN; row <= ROWS_MAX; row++)
            {
                //if the coordinate is within, return result 
                if (getTopSide(col, row).contains(x, y))
                    return row;
            }
        }
        
        return -1;
    }
    
    private Polygon getRightSide(final int col, final int row)
    {
        //determine the starting coordinate
        final int x = this.getCoordinateX(col, row) + (int)getX();
        final int y = this.getCoordinateY(col, row) + (int)getY();
        
        //remove existing
        p.reset();
        
        //clear cache
        p.invalidate();
        
        //add points
        p.addPoint(x + (CELL_DIMENSIONS/2), y + (CELL_DIMENSIONS/2));
        p.addPoint(x + (CELL_DIMENSIONS/2), y + CELL_DIMENSIONS);
        p.addPoint(x + CELL_DIMENSIONS, y + (CELL_DIMENSIONS/2));
        p.addPoint(x + CELL_DIMENSIONS, y);
        
        //return polygon
        return p;
    }
    
    private Polygon getFrontSide(final int col, final int row)
    {
        //determine the starting coordinate
        final int x = this.getCoordinateX(col, row) + (int)getX();
        final int y = this.getCoordinateY(col, row) + (int)getY();
        
        //remove existing
        p.reset();
        
        //clear cache
        p.invalidate();
        
        //add points
        p.addPoint(x, y);
        p.addPoint(x + (CELL_DIMENSIONS/2), y + (CELL_DIMENSIONS/2));
        p.addPoint(x + (CELL_DIMENSIONS/2), y + CELL_DIMENSIONS);
        p.addPoint(x, y + (CELL_DIMENSIONS / 2));
        
        //return polygon
        return p;
    }
    
    private Polygon getTopSide(final int col, final int row)
    {
        //determine the starting coordinate
        final int x = this.getCoordinateX(col, row) + (int)getX();
        final int y = this.getCoordinateY(col, row) + (int)getY();

        //remove existing
        p.reset();
        
        //clear cache
        p.invalidate();
        
        //add points
        p.addPoint(x, y);
        p.addPoint(x + (CELL_DIMENSIONS/2), y + (CELL_DIMENSIONS/2));
        p.addPoint(x + CELL_DIMENSIONS, y);
        p.addPoint(x + (CELL_DIMENSIONS/2), y - (CELL_DIMENSIONS/2));
        
        //return polygon
        return p;
    }
    
    @Override
    public void render() throws Exception
    {
        //clear the existing image
        super.clear();
        
        //reset the location to the origin while rendering the image
        super.setX(0);
        super.setY(0);
        
        //we want to alternate colors between each cell
        boolean flag = true;
        
        for (int row = ROWS_MIN; row <= ROWS_MAX; row++)
        {
            for (int col = COLUMNS_MIN; col <= COLUMNS_MAX; col++)
            {
                //temporary object reference
                Polygon tmp;
                
                //we only need to the front if the last row
                if (row == ROWS_MAX)
                {
                    //front side
                    tmp = this.getFrontSide(col, row);

                    getGraphics2D().setColor((flag) ? light : dark);
                    getGraphics2D().fillPolygon(tmp);
                    getGraphics2D().setColor(dark);
                    getGraphics2D().drawPolygon(tmp);
                }
                
                //we only need to draw the depth for the cells in the last column, and the last row
                if (col == COLUMNS_MAX)
                {
                    //right side (east)
                    tmp = this.getRightSide(col, row);
                    
                    getGraphics2D().setColor((flag) ? light : dark);
                    getGraphics2D().fillPolygon(tmp);
                    getGraphics2D().setColor(dark);
                    getGraphics2D().drawPolygon(tmp);
                }

                //top side
                tmp = this.getTopSide(col, row);

                getGraphics2D().setColor((flag) ? light : dark);
                getGraphics2D().fillPolygon(tmp);
                
                //draw the outline if the cell if on the ends
                if (col == COLUMNS_MAX || row == ROWS_MAX || col == COLUMNS_MIN || row == ROWS_MIN)
                {
                    getGraphics2D().setColor(dark);
                    getGraphics2D().drawPolygon(tmp);
                }
                
                //alternate the color
                if (col != COLUMNS_MAX)
                    flag = !flag;
            }
        }
        
        //now we are done rendering the board, set the dimensions of the entire board
        super.setWidth(CELL_DIMENSIONS * BOARD_COLUMNS);
        super.setHeight(CELL_DIMENSIONS * BOARD_ROWS);
        
        //assign the entire image dimensions
        assignImageDimension((int)getWidth(), (int)getHeight());
    }
}