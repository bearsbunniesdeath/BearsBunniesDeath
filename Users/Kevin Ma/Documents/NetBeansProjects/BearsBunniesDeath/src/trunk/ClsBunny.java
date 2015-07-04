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
 * @author Kevin Ma
 */
public class ClsBunny extends ClsCharacter {

    public String ItemName = "BUNNY";
    private boolean myIsStunned = false;
    private ClsFlare myFlare;
    private final ClsGrid myGrid;

    public ClsBunny(ClsCoordinate startPosition, Rectangle2D.Double rect, BufferedImage bmp, ClsGrid grid) {
        super(startPosition, rect, bmp, eCharacterImage.BUNNY, "BUNNY");
        myGrid = grid;
    }

    public ClsCoordinate NextMove() {
        ClsCoordinate nextMove;
        if (!this.IsDead() && !this.myIsStunned) {
            nextMove = this.GetCoord().Move(ClsGrid.eDirection.RANDOM);
        } else {
            if (this.myIsStunned) {
                this.myIsStunned = false;
            }
            nextMove = this.GetCoord();
        }
        return nextMove;
    }

    public boolean IsStunned() {
        return myIsStunned;
    }

    public void PutDown() {
        this.myIsStunned = true;
        if (myFlare != null) {
            if (myFlare.GetCurrentLightTime() == -1) {
                myFlare.Ignite();
            }
            myFlare.SetCoord(this.GetCoord());
            myGrid.SetFlares(ClsHelperUtils.AddSquareToArray(myGrid.GetFlares(), myFlare));
        }
    }

    public void CombineWithFlare(ClsFlare flare) {
        myFlare = flare;
        bmp = GetImage(ClsGrid.eItemImage.BUNNY_FLARE);
        flare.bmp = GetImage(ClsGrid.eItemImage.BLANK);
    }

    @Override
    public void Move(ClsCoordinate coord) {
        SetCoord(coord);
        if (myFlare != null) {
            if (myFlare.GetCurrentLightTime() == 1) {
                myFlare = null;
                //?Matt? Remove from Map Or all flare use?
                bmp = GetImage(ClsGrid.eCharacterImage.BUNNY);
            } else {
                myFlare.SetCoord(coord);
            }
        }
    }

    public ClsFlare getMyFlare() {
        return myFlare;
    }

}
