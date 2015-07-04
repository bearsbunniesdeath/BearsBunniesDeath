/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trunk;

import java.io.IOException;
import javax.swing.*;

/**
 *
 * @author Kevin Ma
 */
public class BearsBunniesDeathKevin {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException{
        // TODO code application logic here
                       
        JFrame f = new JFrame("Kevin's version");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);       
      //  f.setSize((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(), 
      //          (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
       // f.setLocation(0,0);       
        
        ClsGrid test = new ClsGrid();
        f.add(test);       
        f.setVisible(true);
       
        
        //Build terrain
        //test.BuildTerrain();
        //test.BuildCharacters(10,2);

        //test.SetSquareType(test.GetCoordinates(ClsGrid.eTerrain.RIVER), ClsGrid.eTerrain.RIVER);
        
        //Set houses
        //test.SetSquareType(test.GetCoordinates(ClsGrid.eTerrain.BLOCKED), ClsGrid.eTerrain.BLOCKED);
               
    }       
}


