package com.gamesbykevin.checkers.player;

import com.gamesbykevin.checkers.board.Board;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.checkers.piece.Checker;
import com.gamesbykevin.checkers.engine.Engine;
import com.gamesbykevin.checkers.resources.GameAudio;
import com.gamesbykevin.checkers.resources.Resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class will control a cpu opponent
 * @author GOD
 */
public final class Cpu extends Player implements Disposable
{
    //the object representing the current move
    private Move currentMove;
    
    //our object containing possible checker selections for various things, etc...
    private List<Move> options;
    
    /**
     * The time delay to complete a regular move
     */
    private static final long DELAY_MOVE_REGULAR = Timers.toNanoSeconds(1000L);
    
    /**
     * The time delay to complete a capture move
     */
    private static final long DELAY_MOVE_CAPTURE = Timers.toNanoSeconds(1250L);
    
    //our timer to determine how long a move takes to finish
    private Timer timer;
    
    public Cpu(final boolean attackNorth, final Players.PieceKey pieceRegular, final Players.PieceKey pieceKing)
    {
        super(attackNorth, pieceRegular, pieceKing);
        
        //create the object representing the move
        this.currentMove = new Move();
        
        //create a new timer
        this.timer = new Timer();
        
        //create a new list of options
        this.options = new ArrayList<>();
    }
    
    @Override
    public void dispose()
    {
        this.currentMove = null;
        
        this.options.clear();
        this.options = null;
        
        this.timer = null;
    }
    
    @Override
    public boolean update(final Engine engine) throws Exception
    {
        //has a valid move been completed, default false
        boolean valid = false;
        
        //get the opponent
        Player opponent = engine.getManager().getPlayers().getOpponent(this);
            
        //don't continue if trapped
        if (isTrapped(opponent))
            return false;
        
        /**
         * The logic to picking/moving a checker piece is prioritized as follows
         * 1. If we have a capture, we must take it
         * 2. If we have a piece that can be captured, lets see if we can avoid capture
         * 3. Lets see if we can move a piece to capture the opponent, without risking capture
         * 4. Is there a piece we can move to create a king
         * 5. Lets see if we can just move a piece to a safe location without risking capture
         * 5. Worst case scenario move any piece at random
         */
        if (!hasSelection())
        {
            /**
             * If we have a capture we must make that move
             */
            if (hasCapture(opponent))
            {
                //determine what piece performs the capture
                checkCapture(opponent);
            }
            /**
             * If we don't have a capture, check if one of our pieces is in danger.<br>
             * If so we will move out of the way.
             */
            else if (opponent.hasCapture(this))
            {
                //avoid capture "if possible"
                avoidCapture(opponent);
            }
             
            //if we still don't have any exiting options
            if (options.isEmpty())
            {
                //lets see if we can attack the opponent without risking our pieces using a king
                moveSafelyCapture(opponent, true);
            }
            
            //if we still don't have any exiting options
            if (options.isEmpty())
            {
                //lets see if we can attack the opponent without risking our pieces using a regular piece
                moveSafelyCapture(opponent, true);
            }
            
            //if we still don't have any exiting options
            if (options.isEmpty())
            {
                //now lets see if we can advance a checker to be king
                advanceToKing(opponent);
            }
            
            //if we still don't have any exiting options
            if (options.isEmpty())
            {
                //see if we can move a regular piece that won't put any checker in immediate danger
                moveSafely(opponent, false);
            }
     
            //if we still don't have any exiting options
            if (options.isEmpty())
            {
                //see if we can move a king piece that won't put any checker in immediate danger
                moveSafely(opponent, true);
            }
            
            //if we still don't have any exiting options
            if (options.isEmpty())
            {
                //move random
                moveRandom(opponent);
            }
            
            //pick a random move
            pickMove(engine.getRandom());
            
            //play sound effect
            engine.getResources().playGameAudio(GameAudio.Keys.Select2);
        }
        else
        {
            //move the piece and get the result if the move has completed
            valid = move(engine.getMain().getTime(), engine.getManager().getBoard(), opponent, engine.getRandom(), engine.getResources());
        }
        
        //return our result
        return valid;
    }
    
    /**
     * Move a piece at random
     * @param opponent The opponent we are facing
     */
    private void moveRandom(final Player opponent)
    {
        for (int i = 0; i < getPieces().size(); i++)
        {
            //get the current piece
            final Checker piece = getPieces().get(i);

            //get the original location
            final int col = (int)piece.getCol();
            final int row = (int)piece.getRow();

            //the place we want to move to
            int newCol;
            int newRow;

            /**
             * Lets try to move the piece to see if the capture count can be lowered
             */
            if (assignedNorth() || piece.isKing())
            {
                //set the new location
                newCol = col - Player.MOVE_NORMAL;
                newRow = row - Player.MOVE_NORMAL;

                //make sure this is a valid location
                if (piece.isValidLocation(opponent, this, newCol, newRow))
                {
                    //set our start and destination, and select the piece
                    addOptionalMove(col, row, newCol, newRow, i);
                }

                //set the new location
                newCol = col + Player.MOVE_NORMAL;
                newRow = row - Player.MOVE_NORMAL;

                //make sure this is a valid location
                if (piece.isValidLocation(opponent, this, newCol, newRow))
                {
                    //set our start and destination, and select the piece
                    addOptionalMove(col, row, newCol, newRow, i);
                }
            }

            if (!assignedNorth() || piece.isKing())
            {
                //set the new location
                newCol = col - Player.MOVE_NORMAL;
                newRow = row + Player.MOVE_NORMAL;

                //make sure this is a valid location
                if (piece.isValidLocation(opponent, this, newCol, newRow))
                {
                    //set our start and destination, and select the piece
                    addOptionalMove(col, row, newCol, newRow, i);
                }

                //set the new location
                newCol = col + Player.MOVE_NORMAL;
                newRow = row + Player.MOVE_NORMAL;

                //make sure this is a valid location
                if (piece.isValidLocation(opponent, this, newCol, newRow))
                {
                    //set our start and destination, and select the piece
                    addOptionalMove(col, row, newCol, newRow, i);
                }
            }
        }
    }
    
    /**
     * Lets see if we have any pieces to move to capture the opponent without putting in immediate danger.<br>
     * @param opponent The opponent we are facing
     * @param checkKing true if attempting to capture with a king safely, false if attempting to capture with a regular piece safely
     */
    private void moveSafelyCapture(final Player opponent, final boolean checkKing)
    {
        //lets see if we can move 1 piece forward without being captured
        for (int i = 0; i < getPieces().size(); i++)
        {
            //get the current piece
            final Checker piece = getPieces().get(i);

            //if we are checking for kings
            if (checkKing)
            {
                //if the piece is not king, don't check it
                if (!piece.isKing())
                    continue;
            }
            else
            {
                //if the piece is king, don't check it
                if (piece.isKing())
                    continue;
            }
            
            //get the original location
            final int col = (int)piece.getCol();
            final int row = (int)piece.getRow();

            //the place we want to move to
            int newCol;
            int newRow;

            /**
             * Lets try to move the piece to see if the capture count can be lowered
             */
            if (assignedNorth() || piece.isKing())
            {
                //set the new location
                newCol = col - Player.MOVE_NORMAL;
                newRow = row - Player.MOVE_NORMAL;

                //make sure this is a valid location
                if (piece.isValidLocation(opponent, this, newCol, newRow))
                {
                    //set new location
                    piece.setCol(newCol);
                    piece.setRow(newRow);

                    //make sure this is a safe place to move
                    if (opponent.getCaptureCount(this) <= Checker.NO_CAPTURES)
                    {
                        //we also want to check that we have a capture available
                        if (hasCapture(opponent))
                        {
                            //set our start and destination, and select the piece
                            addOptionalMove(col, row, newCol, newRow, i);
                        }
                    }
                }

                //set the new location
                newCol = col + Player.MOVE_NORMAL;
                newRow = row - Player.MOVE_NORMAL;

                //make sure this is a valid location
                if (piece.isValidLocation(opponent, this, newCol, newRow))
                {
                    //set new location
                    piece.setCol(newCol);
                    piece.setRow(newRow);

                    //make sure this is a safe place to move
                    if (opponent.getCaptureCount(this) <= Checker.NO_CAPTURES)
                    {
                        //we also want to check that we have a capture available
                        if (hasCapture(opponent))
                        {
                            //set our start and destination, and select the piece
                            addOptionalMove(col, row, newCol, newRow, i);
                        }
                    }
                }
            }

            if (!assignedNorth() || piece.isKing())
            {
                //set the new location
                newCol = col - Player.MOVE_NORMAL;
                newRow = row + Player.MOVE_NORMAL;

                //make sure this is a valid location
                if (piece.isValidLocation(opponent, this, newCol, newRow))
                {
                    //set new location
                    piece.setCol(newCol);
                    piece.setRow(newRow);

                    //make sure this is a safe place to move
                    if (opponent.getCaptureCount(this) <= Checker.NO_CAPTURES)
                    {
                        //we also want to check that we have a capture available
                        if (hasCapture(opponent))
                        {
                            //set our start and destination, and select the piece
                            addOptionalMove(col, row, newCol, newRow, i);
                        }
                    }
                }

                //set the new location
                newCol = col + Player.MOVE_NORMAL;
                newRow = row + Player.MOVE_NORMAL;

                //make sure this is a valid location
                if (piece.isValidLocation(opponent, this, newCol, newRow))
                {
                    //set new location
                    piece.setCol(newCol);
                    piece.setRow(newRow);

                    //make sure this is a safe place to move
                    if (opponent.getCaptureCount(this) <= Checker.NO_CAPTURES)
                    {
                        //we also want to check that we have a capture available
                        if (hasCapture(opponent))
                        {
                            //set our start and destination, and select the piece
                            addOptionalMove(col, row, newCol, newRow, i);
                        }
                    }
                }
            }

            //reset location
            piece.setCol(col);
            piece.setRow(row);
        }
    }
    
    /**
     * Lets see if we have any pieces that we can move without putting in immediate danger.<br>
     * @param opponent The opponent we are facing
     * @param checkKing true if attempting to move kings safely, false if attempting to move regular pieces safely
     */
    private void moveSafely(final Player opponent, final boolean checkKing)
    {
        //lets see if we can move 1 piece forward without being captured
        for (int i = 0; i < getPieces().size(); i++)
        {
            //get the current piece
            final Checker piece = getPieces().get(i);

            //if we are checking for kings
            if (checkKing)
            {
                //if the piece is not king, don't check it
                if (!piece.isKing())
                    continue;
            }
            else
            {
                //if the piece is king, don't check it
                if (piece.isKing())
                    continue;
            }
            
            //get the original location
            final int col = (int)piece.getCol();
            final int row = (int)piece.getRow();

            //the place we want to move to
            int newCol;
            int newRow;

            /**
             * Lets try to move the piece to see if the capture count can be lowered
             */
            if (assignedNorth() || piece.isKing())
            {
                //set the new location
                newCol = col - Player.MOVE_NORMAL;
                newRow = row - Player.MOVE_NORMAL;

                //make sure this is a valid location
                if (piece.isValidLocation(opponent, this, newCol, newRow))
                {
                    //set new location
                    piece.setCol(newCol);
                    piece.setRow(newRow);

                    //if there are no captures for the opponent this location is good
                    if (opponent.getCaptureCount(this) <= Checker.NO_CAPTURES)
                    {
                        //set our start and destination, and select the piece
                        addOptionalMove(col, row, newCol, newRow, i);
                    }
                }

                //set the new location
                newCol = col + Player.MOVE_NORMAL;
                newRow = row - Player.MOVE_NORMAL;

                //make sure this is a valid location
                if (piece.isValidLocation(opponent, this, newCol, newRow))
                {
                    //set new location
                    piece.setCol(newCol);
                    piece.setRow(newRow);

                    //if there are no captures for the opponent this location is good
                    if (opponent.getCaptureCount(this) <= Checker.NO_CAPTURES)
                    {
                        //set our start and destination, and select the piece
                        addOptionalMove(col, row, newCol, newRow, i);
                    }
                }
            }

            if (!assignedNorth() || piece.isKing())
            {
                //set the new location
                newCol = col - Player.MOVE_NORMAL;
                newRow = row + Player.MOVE_NORMAL;

                //make sure this is a valid location
                if (piece.isValidLocation(opponent, this, newCol, newRow))
                {
                    //set new location
                    piece.setCol(newCol);
                    piece.setRow(newRow);

                    //if there are no captures for the opponent this location is good
                    if (opponent.getCaptureCount(this) <= Checker.NO_CAPTURES)
                    {
                        //set our start and destination, and select the piece
                        addOptionalMove(col, row, newCol, newRow, i);
                    }
                }

                //set the new location
                newCol = col + Player.MOVE_NORMAL;
                newRow = row + Player.MOVE_NORMAL;

                //make sure this is a valid location
                if (piece.isValidLocation(opponent, this, newCol, newRow))
                {
                    //set new location
                    piece.setCol(newCol);
                    piece.setRow(newRow);

                    //if there are no captures for the opponent this location is good
                    if (opponent.getCaptureCount(this) <= Checker.NO_CAPTURES)
                    {
                        //set our start and destination, and select the piece
                        addOptionalMove(col, row, newCol, newRow, i);
                    }
                }
            }

            //reset location
            piece.setCol(col);
            piece.setRow(row);
        }
    }
    
    /**
     * Lets see if we have any pieces that can become a king
     * @param opponent The opponent we are facing
     */
    private void advanceToKing(final Player opponent)
    {
        //lets see if we can advance 1 checker to be a king
        for (int i = 0; i < getPieces().size(); i++)
        {
            //get the current piece
            final Checker piece = getPieces().get(i);

            //we are not concerned with advancing a piece if already a king
            if (piece.isKing())
                continue;

            //get the original location
            final int col = (int)piece.getCol();
            final int row = (int)piece.getRow();

            //check which way we are heading
            if (assignedNorth())
            {
                if (piece.getRow() == Board.ROWS_MIN + Player.MOVE_NORMAL)
                {
                    //if valid location move accordingly
                    if (piece.isValidLocation(opponent, this, col - Player.MOVE_NORMAL, Board.ROWS_MIN))
                    {
                        addOptionalMove(col, row, col - Player.MOVE_NORMAL, Board.ROWS_MIN, i);
                    }
                    else if (piece.isValidLocation(opponent, this, col + Player.MOVE_NORMAL, Board.ROWS_MIN))
                    {
                        addOptionalMove(col, row, col + Player.MOVE_NORMAL, Board.ROWS_MIN, i);
                    }
                }
            }
            else
            {
                if (piece.getRow() == Board.ROWS_MAX - Player.MOVE_NORMAL)
                {
                    //if valid location move accordingly
                    if (piece.isValidLocation(opponent, this, col - Player.MOVE_NORMAL, Board.ROWS_MAX))
                    {
                        addOptionalMove(col, row, col - Player.MOVE_NORMAL, Board.ROWS_MAX, i);
                    }
                    else if (piece.isValidLocation(opponent, this, col + Player.MOVE_NORMAL, Board.ROWS_MAX))
                    {
                        addOptionalMove(col, row, col + Player.MOVE_NORMAL, Board.ROWS_MAX, i);
                    }
                }
            }
        }
    }
    
    /**
     * Check if we can avoid capture.<br>
     * We only want to check if we currently have a piece that will be captured if we do not act
     * @param opponent The opponent we are facing
     */
    private void avoidCapture(final Player opponent)
    {
        //get the capture count
        final int captureMax = opponent.getCaptureCount(this);

        //check each piece to see if we can lower the capture count
        for (int i = 0; i < getPieces().size(); i++)
        {
            //get the current piece
            final Checker piece = getPieces().get(i);

            //get the original location
            final int col = (int)piece.getCol();
            final int row = (int)piece.getRow();

            //the place we want to move to
            int newCol;
            int newRow;

            /**
             * Lets try to move the piece to see if the capture count can be lowered
             */
            if (assignedNorth() || piece.isKing())
            {
                //set the new location
                newCol = col - Player.MOVE_NORMAL;
                newRow = row - Player.MOVE_NORMAL;

                //make sure this is a valid location
                if (piece.isValidLocation(opponent, this, newCol, newRow))
                {
                    //set new location
                    piece.setCol(newCol);
                    piece.setRow(newRow);

                    //if the capture count is lower this is a good move
                    if (opponent.getCaptureCount(this) < captureMax)
                    {
                        //set our start and destination, and select the piece
                        addOptionalMove(col, row, newCol, newRow, i);
                    }
                }

                //set the new location
                newCol = col + Player.MOVE_NORMAL;
                newRow = row - Player.MOVE_NORMAL;

                //make sure this is a valid location
                if (piece.isValidLocation(opponent, this, newCol, newRow))
                {
                    //set new location
                    piece.setCol(newCol);
                    piece.setRow(newRow);

                    //if the capture count is lower this is a good move
                    if (opponent.getCaptureCount(this) < captureMax)
                    {
                        //set our start and destination, and select the piece
                        addOptionalMove(col, row, newCol, newRow, i);
                    }
                }
            }

            if (!assignedNorth() || piece.isKing())
            {
                //set the new location
                newCol = col - Player.MOVE_NORMAL;
                newRow = row + Player.MOVE_NORMAL;

                //make sure this is a valid location
                if (piece.isValidLocation(opponent, this, newCol, newRow))
                {
                    //set new location
                    piece.setCol(newCol);
                    piece.setRow(newRow);

                    //if the capture count is lower this is a good move
                    if (opponent.getCaptureCount(this) < captureMax)
                    {
                        //set our start and destination, and select the piece
                        addOptionalMove(col, row, newCol, newRow, i);
                    }
                }

                //set the new location
                newCol = col + Player.MOVE_NORMAL;
                newRow = row + Player.MOVE_NORMAL;

                //make sure this is a valid location
                if (piece.isValidLocation(opponent, this, newCol, newRow))
                {
                    //set new location
                    piece.setCol(newCol);
                    piece.setRow(newRow);

                    //if the capture count is lower this is a good move
                    if (opponent.getCaptureCount(this) < captureMax)
                    {
                        //set our start and destination, and select the piece
                        addOptionalMove(col, row, newCol, newRow, i);
                    }
                }
            }

            //reset location
            piece.setCol(col);
            piece.setRow(row);
        }
    }
    
    /**
     * Determine which piece will capture.
     * @param opponent The opponent we are facing
     */
    private void checkCapture(final Player opponent)
    {
        //check each piece to see where we can capture
        for (int i = 0; i < getPieces().size(); i++)
        {
            //get the current piece
            final Checker piece = getPieces().get(i);

            //check the captures for this piece and add optional selections (if available)
            checkCapturePiece(opponent, piece, i);
        }
    }
    
    /**
     * Check the captures for the given piece.<br>
     * If the piece has captures, they will be added to the list of optional moves.
     * @param opponent Opponent we are facing
     * @param piece The current piece we want to check for captures
     * @param indexSelection The index of the checker we are targeting
     */
    private void checkCapturePiece(final Player opponent, final Checker piece, final int indexSelection)
    {
        //get the original location
        final int col = (int)piece.getCol();
        final int row = (int)piece.getRow();

        //the place we want to move to
        int newCol;
        int newRow;

        //if this piece has a capture
        if (piece.getCaptureCount(opponent, this) > Checker.NO_CAPTURES)
        {
            //north-west
            newCol = col - Player.MOVE_CAPTURE;
            newRow = row - Player.MOVE_CAPTURE;

            //mark at selection if we can capture
            if (piece.hasCapture(opponent, this, newCol, newRow))
                addOptionalMove(col, row, newCol, newRow, indexSelection);

            //north-east
            newCol = col + Player.MOVE_CAPTURE;
            newRow = row - Player.MOVE_CAPTURE;

            //mark at selection if we can capture
            if (piece.hasCapture(opponent, this, newCol, newRow))
                addOptionalMove(col, row, newCol, newRow, indexSelection);

            //south-west
            newCol = col - Player.MOVE_CAPTURE;
            newRow = row + Player.MOVE_CAPTURE;

            //mark at selection if we can capture
            if (piece.hasCapture(opponent, this, newCol, newRow))
                addOptionalMove(col, row, newCol, newRow, indexSelection);

            //south-east
            newCol = col + Player.MOVE_CAPTURE;
            newRow = row + Player.MOVE_CAPTURE;

            //mark at selection if we can capture
            if (piece.hasCapture(opponent, this, newCol, newRow))
                addOptionalMove(col, row, newCol, newRow, indexSelection);
        }
    }
    
    /**
     * Move the current selected piece towards the destination.
     * @param time Time to deduct from timer
     * @param board The game board
     * @param opponent The opponent we are facing
     * @param random Object used to make random decisions
     * @param resources Object used to play sound
     * @return true if the cpu move has been completed, false otherwise
     * @throws Exception 
     */
    private boolean move(final long time, final Board board, final Player opponent, final Random random, final Resources resources) throws Exception
    {
        boolean valid = false;
        
        //update timer
        timer.update(time);

        double col;
        double row;

        //move selection towards the destination accordingly
        if (currentMove.endRow > currentMove.startRow)
        {
            row = (timer.getProgress() * (currentMove.endRow - currentMove.startRow)) + currentMove.startRow;
        }
        else
        {
            row = currentMove.startRow - (timer.getProgress() * (currentMove.startRow - currentMove.endRow));
        }

        //move selection towards the destination accordingly
        if (currentMove.endCol > currentMove.startCol)
        {
            col = (timer.getProgress() * (currentMove.endCol - currentMove.startCol)) + currentMove.startCol;
        }
        else
        {
            col = currentMove.startCol - (timer.getProgress() * (currentMove.startCol - currentMove.endCol));
        }

        //set the new location
        getCurrentSelection().setCol(col);
        getCurrentSelection().setRow(row);

        //update the x,y coordinates of the piece here
        board.placePiece(getCurrentSelection());

        if (timer.hasTimePassed())
        {
            //get the row difference
            int rowDiff = (int)((currentMove.endRow > currentMove.startRow) ? currentMove.endRow - currentMove.startRow : currentMove.startRow - currentMove.endRow);

            //if the row difference is 2, we captured a piece
            final boolean performedCapture = (rowDiff == Player.MOVE_CAPTURE);

            //if we captured a piece, we need to remove it
            if (performedCapture)
            {
                //get the location were the opponent is
                int opponentRow = (int)((currentMove.endRow > currentMove.startRow) ? currentMove.endRow - Player.MOVE_NORMAL : currentMove.startRow - Player.MOVE_NORMAL);
                int opponentCol = (int)((currentMove.endCol > currentMove.startCol) ? currentMove.endCol - Player.MOVE_NORMAL : currentMove.startCol - Player.MOVE_NORMAL);

                //remove opponent piece
                opponent.remove(opponentCol, opponentRow);
                
                //play sound effect
                resources.playGameAudio(GameAudio.Keys.Capture2);
            }
            else
            {
                //play sound effect
                resources.playGameAudio(GameAudio.Keys.Place2);
            }

            //store the current piece selection
            final int selection = getSelection();

            //place the piece
            placeSelection(board, currentMove.endCol, currentMove.endRow);

            //assign the piece once more
            setSelection(selection);

            //if we did not perform a capture, the turn is over
            if (!performedCapture)
            {
                //do not have a piece selected
                setSelection(NO_SELECTION);

                //a valid move has been made
                valid = true;
            }
            else
            {
                //if we did perform a capture, but no longer have captures with this piece, the turn is complete
                if (getCurrentSelection().getCaptureCount(opponent, this) == Checker.NO_CAPTURES)
                {
                    //do not have a piece selected
                    setSelection(NO_SELECTION);

                    //a valid move has been made
                    valid = true;
                }
                else
                {
                    //locate the capture moves for this piece
                    checkCapturePiece(opponent, getCurrentSelection(), super.getSelection());
                    
                    //if there are no more captures in the optional list, the turn is over
                    if (options.isEmpty())
                    {
                        //do not have a piece selected
                        setSelection(NO_SELECTION);

                        //a valid move has been made
                        valid = true;
                    }
                    else
                    {
                        //else pick the next jump move
                        pickMove(random);
                    }
                }
            }

            //reset timer
            timer.reset();
        }

        //return the result if the move is complete
        return valid;
    }
    
    /**
     * Pick a move from our available options
     * @param random Object used to make random decisions
     */
    private void pickMove(final Random random)
    {
        //pick random optional move
        final Move move = options.get(random.nextInt(options.size()));
        
        //assign the current move
        currentMove.assign(move);
        
        setSelection(move.selection);
        
        //get the row difference
        int rowDiff = (int)((currentMove.endRow > currentMove.startRow) ? currentMove.endRow - currentMove.startRow : currentMove.startRow - currentMove.endRow);

        //if the row difference is 2, we have a capture and we will alter the timer accordingly
        timer.setReset((rowDiff == Player.MOVE_CAPTURE) ? DELAY_MOVE_CAPTURE : DELAY_MOVE_REGULAR);
        
        //reset timer
        timer.reset();
        
        //also remove existing options
        options.clear();
    }
    
    /**
     * Assign the start and finish as well as checker selection
     * @param startCol Starting Column
     * @param startRow Starting Row
     * @param endCol End Column
     * @param endRow End Row
     * @param selection Checker selected
     */
    private void addOptionalMove(final int startCol, final int startRow, final int endCol, final int endRow, final int selection)
    {
        //add option to list
        this.options.add(new Move(startCol, startRow, endCol, endRow, selection));
    }
    
    /**
     * This class represents a move for a checker piece
     */
    private final class Move 
    {
        //the start
        private int startCol;
        private int startRow;
        
        //the destination
        private int endCol;
        private int endRow;
        
        //the checker piece to move
        private int selection;
        
        private Move()
        {
            this(0,0,0,0,0);
        }
        
        private Move(final int startCol, final int startRow, final int endCol, final int endRow, final int selection)
        {
            this.startCol = startCol;
            this.startRow = startRow;
            this.endCol = endCol;
            this.endRow = endRow;
            this.selection = selection;
        }
        
        /**
         * Assign the move with the given
         * @param move Object representing a checker move
         */
        private void assign(final Move move)
        {
            this.startCol   = move.startCol;
            this.startRow   = move.startRow;
            this.endCol     = move.endCol;
            this.endRow     = move.endRow;
            this.selection  = move.selection;
        }
    }
}