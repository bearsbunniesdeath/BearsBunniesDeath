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
public class ClsCharacter extends ClsSquare { 
    
    private eCharacterImage charType;
    private boolean myIsDead = false;
    
    public ClsCharacter(ClsCoordinate startPosition, Rectangle2D.Double rect, BufferedImage bmp, eCharacterImage charType, String charDispName) {
        super(startPosition, rect, bmp, charDispName);
        this.charType = charType;
    }
           
    public void Move(ClsGrid.eDirection dir) {
        //Note: Always do collision detection before calling this!!!
        if (dir.equals(ClsGrid.eDirection.NORTH)) {
            this.MoveNorth();
        } else if (dir.equals(ClsGrid.eDirection.SOUTH)) {
            this.MoveSouth();
        } else if (dir.equals(ClsGrid.eDirection.WEST)) {
            this.MoveWest();
        } else if (dir.equals(ClsGrid.eDirection.EAST)) {
            this.MoveEast();
        } else if (dir.equals(ClsGrid.eDirection.NONE)){
//            DONT MOVE
        }
    }
    
    public void Move(ClsCoordinate coord) {
        //Note: Always do collision detection before calling this!!!
        this.SetCoord(coord);
    }
    
    public void MoveNorth() {
        this.SetCoord(this.GetCoord().Move(ClsGrid.eDirection.NORTH));      
    }
    
    public void MoveSouth() {
        this.SetCoord(this.GetCoord().Move(ClsGrid.eDirection.SOUTH));
    }
    
    public void MoveWest() {
        this.SetCoord(this.GetCoord().Move(ClsGrid.eDirection.WEST));
    }
    
    public void MoveEast() {
        this.SetCoord(this.GetCoord().Move(ClsGrid.eDirection.EAST));
    }
      
    @Override
    public void SetBmpFromType(boolean force) {
        //Only set bmp if it is null, otherwise do nothing if not forced
        if (force == false) {           
            if (this.bmp == null) {               
                if (this.charType != null) {
                    this.bmp = GetImage(this.charType);
                }                                
            }          
        } else {          
            if (this.charType != null) {
                this.bmp = GetImage(this.charType);
            }
        }
    }
    
    public boolean IsDead() {
        return myIsDead;
    }

    public void IsDead(boolean myIsDead) {
        this.myIsDead = myIsDead;
    }

    public void SetCharType(eCharacterImage charType) {
        this.charType = charType;
    }
        
}
