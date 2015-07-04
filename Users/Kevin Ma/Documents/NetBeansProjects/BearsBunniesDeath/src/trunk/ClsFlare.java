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
public class ClsFlare extends ClsSquare {

    private ClsGrid myGrid;
    private int myCurrentLightTime = -1;
    private int myLightRadius = 1;
    private int myPotentialLightTime = 100;

    public ClsFlare(ClsCoordinate coord, Rectangle2D.Double rect, BufferedImage bmp, ClsGrid grid) {
        super(coord, rect, GetImage(ClsGrid.eItemImage.FLARE), "FLARE");
        myGrid = grid;
//        SetBmp(bmp);
    }

    public void Ignite() {
        myCurrentLightTime = myPotentialLightTime;
    }

    public void BurnOneTurn() {
        if (myCurrentLightTime > 0) {
            myCurrentLightTime -= 1;
        }
    }

    public int GetCurrentLightTime() {
        return myCurrentLightTime;
    }

    public int GetLightRadius() {
        return myLightRadius;
    }

}
