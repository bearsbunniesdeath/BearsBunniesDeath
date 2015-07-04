/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trunk;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

public class BearsBunniesDeathMatt {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        ClsGrid test = new ClsGrid();
        JFrame f = new JFrame("Bears, Bunnies, Death");

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.add(test);

//        test.SetSquareType(test.GetCoordinates(ClsGrid.eTerrain.RIVER), ClsGrid.eTerrain.RIVER);
//        test.SetSquareType(test.GetCoordinates(ClsGrid.eTerrain.BLOCKED), ClsGrid.eTerrain.BLOCKED);

        //Set houses
        //f.setSize(1600, 1400);
        //f.setLocation(0, 0);
        f.setVisible(true);

//        if (test.IsMapValid()) {
//            System.out.print("VALID MAP!");
//        } else {
//            System.out.print("INVALID MAP!");
//        }
    }

}
