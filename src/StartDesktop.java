import com.gamesbykevin.checkers.main.Main;
import com.gamesbykevin.checkers.shared.Shared;

import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This file will run the game as a desktop application
 * @author GOD
 */
public final class StartDesktop extends JPanel
{
    //our object that will run everything in the application
    private Main main;
    
    public static void main(String[] args)
    {
        try
        {
            //create a new jframe that will contain our application
            JFrame window = new JFrame(Shared.GAME_NAME);

            //use hidden cursor from Shared class
            window.setCursor(Shared.CURSOR);
            
            //create panel that will contain the game
            final StartDesktop startDesktop = new StartDesktop();
            
            //add component to window
            window.add(startDesktop);
            
            //do not allow user to resize window
            window.setResizable(false);
            
            //resize window based on dimensions set in StartDesktop
            window.pack();

            //set this null to place the panel in the center of the screen
            window.setLocationRelativeTo(null);
            
            //set visible to true so we can see panel
            window.setVisible(true);
            
            //exit on close
            //window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            //override the windows closing functionality so we can explicitly recycle object
            WindowListener exitListener = new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    
                    if (startDesktop != null)
                    {
                        if (startDesktop.getMain() != null)
                        {
                            //thread will no longer be active
                            startDesktop.getMain().setActive(false);
                            
                            try
                            {
                                //wait for the thread to finish execution
                                startDesktop.getMain().join();
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                            }
                            
                            //now we can recycle resources
                            startDesktop.getMain().dispose();
                        }
                    }
                    
                    //exit
                    System.exit(0);
                }
            };
            
            //add listener
            window.addWindowListener(exitListener);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Default Constructor
     */
    public StartDesktop()
    {
        //assign our custom mouse cursor to hide the default mouse
        setCursor(Shared.CURSOR);
        
        //set the dimensions of the window
        setPreferredSize(new Dimension(Shared.CONTAINER_WIDTH, Shared.CONTAINER_HEIGHT));
        
        //allow this window to be focusable
        setFocusable(true);
        
        //bring focus to this window
        requestFocus();
        
        try
        {
            //create a new instance of main with the specified ups
            main = new Main(Shared.DEFAULT_UPS, this);
            
            //new instance of our main engine
            main.create();
            
            //start the thread
            main.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public Main getMain()
    {
        return this.main;
    }
}