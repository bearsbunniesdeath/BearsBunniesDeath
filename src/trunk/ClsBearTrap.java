/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trunk;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import trunk.ClsGrid.eCharacterImage;
import static trunk.ClsImageUtil.GetImage;

/**
 *
 * @author Matt
 */
public class ClsBearTrap extends ClsSquare {

    private ClsGrid myGrid;
    private int myNumTurnsToHoldBear = -1; //Number of turns left for bear to be stuck
    private int myPotentialHoldBearTime = 5;

    public ClsBearTrap(ClsCoordinate coord, Rectangle2D.Double rect, BufferedImage bmp, ClsGrid grid) {
        super(coord, rect, GetImage(ClsGrid.eItemImage.BEARTRAP), "BEARTRAP");
        myGrid = grid;
//        SetBmp(bmp);
    }

    public void Set() {
        myNumTurnsToHoldBear = myPotentialHoldBearTime;
    }

    public void UseOneTurn() {
        if (myNumTurnsToHoldBear > 0) {
            myNumTurnsToHoldBear -= 1;
        }
    }

    public int GetHoldBearFor() {
        return myNumTurnsToHoldBear;
    }

}
