/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trunk;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import trunk.ClsGrid.eCharacterImage;

/**
 *
 * @author Kevin Ma
 */
public class ClsUserCharacter extends ClsCharacter {

    //private ClsItem[] items;
    //private String name;
    private ClsSquare myItem;
    private ClsSquare myItemToBeUsed;
    private ClsSquare[] myPotentialItems;
    private ClsGrid myGrid;

    private ClsBunny myPotentialBunny;
    private ClsBearTrap myPotentialBearTrap;
    private ClsFlare myPotentialFlare;

//    private boolean myWillUseItem = false;
    public ClsUserCharacter(ClsCoordinate startPosition, Rectangle2D.Double rect, BufferedImage bmp, ClsGrid grid) {
        super(startPosition, rect, bmp, eCharacterImage.USER_L, "USER");
        myGrid = grid;
    }

    @Override
    public void MoveNorth() {
        this.SetCoord(this.GetCoord().Move(ClsGrid.eDirection.NORTH));
        this.SetCharType(eCharacterImage.USER_U);
        this.SetBmpFromType(true);
    }

    @Override
    public void MoveSouth() {
        this.SetCoord(this.GetCoord().Move(ClsGrid.eDirection.SOUTH));
        this.SetCharType(eCharacterImage.USER_D);
        this.SetBmpFromType(true);
    }

    @Override
    public void MoveWest() {
        this.SetCoord(this.GetCoord().Move(ClsGrid.eDirection.WEST));
        this.SetCharType(eCharacterImage.USER_L);
        this.SetBmpFromType(true);
    }

    @Override
    public void MoveEast() {
        this.SetCoord(this.GetCoord().Move(ClsGrid.eDirection.EAST));
        this.SetCharType(eCharacterImage.USER_R);
        this.SetBmpFromType(true);
    }

    public ClsSquare GetItem() {
        return myItem;
    }

    public void ResetPotentialItems() {
        myPotentialItems = new ClsSquare[1];
        myPotentialItems[0] = new ClsSquare(new ClsCoordinate(0, 0), rect, null, "DUMMYSQUARE");
    }

    public void SetItem(ClsSquare myItem) {
        this.myItem = myItem;
    }

    public void PickUpItem(int choice, Boolean[] potentialItems) {
        // Need to move to userCharacter
        // If choice is -1, there is only one option to pickup so search for that one option and set as user item

        if (choice == -1) {
            if (myPotentialBunny != null) {
                myItem = myPotentialBunny;
                if (myPotentialBunny.getMyFlare() != null) {
                    myGrid.RemoveFlareFromGrid(myPotentialBunny.getMyFlare());
                }
                myGrid.SetBunnies(ClsHelperUtils.RemoveSquareFromArray(myGrid.GetBunnies(), myPotentialBunny));
            } else if (myPotentialFlare != null) {
                myItem = myPotentialFlare;
                myGrid.SetFlares(ClsHelperUtils.RemoveSquareFromArray(myGrid.GetFlares(), myPotentialFlare));
            } else if (myPotentialBearTrap != null) {
                myItem = myPotentialBearTrap;
                myGrid.SetBearTraps(ClsHelperUtils.RemoveSquareFromArray(myGrid.GetBearTraps(), myPotentialBearTrap));
            }
        } else if (choice == ClsGrid.BUNNY_INDEX) {
            ShiftItemToBeUsed();
            if (myPotentialBunny != null) {
                myItem = myPotentialBunny;
                if (myPotentialBunny.getMyFlare() != null) {
                    myGrid.RemoveFlareFromGrid(myPotentialBunny.getMyFlare());
                }
                myGrid.SetBunnies(ClsHelperUtils.RemoveSquareFromArray(myGrid.GetBunnies(), myPotentialBunny));
            }
        } else if (choice == ClsGrid.FLARE_INDEX) {
            ShiftItemToBeUsed();
            if (myPotentialFlare != null) {
                myItem = myPotentialFlare;
                myGrid.SetFlares(ClsHelperUtils.RemoveSquareFromArray(myGrid.GetFlares(), myPotentialFlare));
            }
        } else if (choice == ClsGrid.BEARTRAP_INDEX) {
            ShiftItemToBeUsed();
            if (myPotentialBearTrap != null) {
                myItem = myPotentialBearTrap;
                myGrid.SetBearTraps(ClsHelperUtils.RemoveSquareFromArray(myGrid.GetBearTraps(), myPotentialBearTrap));
            }
        } else if (choice == ClsGrid.BUNNY_FLARE_INDEX) {
            ClsBunny torchBunny = ConstructAndReturnBunnyFlare(potentialItems);
            myItem = torchBunny;
            myGrid.RemoveBunnyFromGrid(myPotentialBunny);
        } else if (choice == ClsGrid.BEARTRAP_FLARE_INDEX) {
            //TODO: Decide which items to destroy / leave / set & drop
        } else if (choice == ClsGrid.BEARTRAP_BUNNY_INDEX) {
            //TODO: Decide which items to destroy / leave / set & drop
        }
        UseItem(); //Needs to Move
    }

    private ClsBunny ConstructAndReturnBunnyFlare(Boolean[] potentialItems) {
        //TODO: Return the bunny so it can be removed in PickUpItem()
        //Try to build item with only potential items so can drop and set currItem
        if (potentialItems[ClsGrid.BUNNY_INDEX] && potentialItems[ClsGrid.FLARE_INDEX]) {
            this.ShiftItemToBeUsed();
            myPotentialBunny.CombineWithFlare(myPotentialFlare);
            return myPotentialBunny;
        } else if (myItem instanceof ClsBunny) {
            ClsBunny asBunny = (ClsBunny) myItem;
            asBunny.CombineWithFlare(myPotentialFlare);
            myGrid.RemoveFlareFromGrid(myPotentialFlare);
            return asBunny;
        } else if (myItem instanceof ClsFlare) {
            //TODO: Holding flare with Square with Bunny
            ClsFlare asFlare = (ClsFlare) myItem;
            myPotentialBunny.CombineWithFlare(asFlare);
            return myPotentialBunny;
        }
        return null;
    }

    void UseItem() {
        if (myItemToBeUsed != null) {
            if (myItemToBeUsed instanceof ClsBunny) {
                ClsBunny tempBunnyItem;
                tempBunnyItem = (ClsBunny) myItemToBeUsed;
                tempBunnyItem.SetCoord(this.GetCoord());
                tempBunnyItem.PutDown();
                myGrid.SetBunnies(ClsHelperUtils.AddSquareToArray(myGrid.GetBunnies(), tempBunnyItem));
//                if (tempBunnyItem.)
                myItemToBeUsed = null;
            } else if (myItemToBeUsed instanceof ClsFlare) {
                ClsFlare tempFlareItem;
                tempFlareItem = (ClsFlare) myItemToBeUsed;
                tempFlareItem.SetCoord(this.GetCoord());
                tempFlareItem.Ignite();
                myGrid.SetFlares(ClsHelperUtils.AddSquareToArray(myGrid.GetFlares(), tempFlareItem));
                myItemToBeUsed = null;
            } else if (myItemToBeUsed instanceof ClsBearTrap) {
                ClsBearTrap tempBearTrap;
                tempBearTrap = (ClsBearTrap) myItemToBeUsed;
                tempBearTrap.SetCoord(this.GetCoord());
                tempBearTrap.Set();
                myGrid.SetBearTraps(ClsHelperUtils.AddSquareToArray(myGrid.GetBearTraps(), tempBearTrap));
                myItemToBeUsed = null;
            }
        }
//        myWillUseItem = false;
    }

//    public boolean WillUseItem() {
//        return myWillUseItem;
//    }
//
//    public void SetWillUseItem(boolean myWillUseItem) {
//        this.myWillUseItem = myWillUseItem;
//        this.myItemToBeUsed = this.myItem;
//        this.myItem = null;
//    }
    public ClsSquare GetItemToBeUsed() {
        return myItemToBeUsed;
    }

    void ShiftItemToBeUsed() {
//        this.myWillUseItem = myWillUseItem;
        this.myItemToBeUsed = this.myItem;
        this.myItem = null;
    }

    public ClsBunny GetPotentialBunny() {
        return myPotentialBunny;
    }

    public void SetPotentialBunny(ClsBunny myPotentialBunny) {
        this.myPotentialBunny = myPotentialBunny;
    }

    public ClsBearTrap GetPotentialBearTrap() {
        return myPotentialBearTrap;
    }

    public void SetPotentialBearTrap(ClsBearTrap myPotentialBearTrap) {
        this.myPotentialBearTrap = myPotentialBearTrap;
    }

    public ClsFlare GetPotentialFlare() {
        return myPotentialFlare;
    }

    public void SetPotentialFlare(ClsFlare myPotentialFlare) {
        this.myPotentialFlare = myPotentialFlare;
    }

}
