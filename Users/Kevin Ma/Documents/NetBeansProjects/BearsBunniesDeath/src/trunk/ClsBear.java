/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trunk;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import trunk.ClsGrid.eCharacterImage;

/**
 *
 * @author Kevin Ma
 */
public class ClsBear extends ClsCharacter {

//    private ClsGrid myGrid;
    public final double BEAR_RANDOM_RADIUS = 25;
    public final double BEAR_FOLLOW_ROUTE_RADIUS = 5;
    public final double BUNNY_CHASE_RADIUS = 2.1;
    private final ClsGrid myGrid;

    private ClsNavigator myNavigator;
    private ClsCoordinate[] currentPath;
    private int myCurrentRouteIndex = 0;
    private ClsCharacter myTarget;
    private boolean myIsStopped = false;
//    private BearBehaviour myBehaviour = BearBehaviour.eRandomMovement;

//    private enum BearBehaviour{
//       eChaseChar, eChaseBunny, eRandomMovement
//    }
    public ClsBear(ClsCoordinate startPosition, Rectangle2D.Double rect, BufferedImage bmp, ClsGrid grid) {
        super(startPosition, rect, bmp, eCharacterImage.BEAR, "BEAR");
        myGrid = grid;
        myNavigator = new ClsNavigator(myGrid);
    }

    public ClsCoordinate NextMove() {

        if (myIsStopped) {
            myIsStopped = false;
            return this.GetCoord();
        }

        //TODO: Fix this hack!!
        if (Point2D.distance(this.GetX(), this.GetY(), myGrid.GetMyUserChar().GetX(), myGrid.GetMyUserChar().GetY()) < 1.1){
            return myGrid.GetMyUserChar().GetCoord();
        }
        
//        if ( myTarget.IsDead()) {
        myTarget = DetermineTarget();
//        }

        if (myTarget == null) {
//            System.out.println(" Random Move");
            return this.GetCoord().Move(ClsGrid.eDirection.RANDOM);
        }

        ClsCoordinate[] chasePath = myNavigator.GetShortestPath(this.GetCoord(), myTarget.GetCoord());
//        myNavigator.IsRouteAvailable(this.GetCoord(), myTarget.GetCoord());
        
        //TODO:MOVEMENT ERROR nullpointer exception
        return chasePath[Math.max(chasePath.length - 2, 0)];

        // <editor-fold desc="OLD CHASE LOGIC">
//        boolean continueTrail;
        //        = (myNavigator != null && myNavigator.GetMyCorrectNavigationCoords().length - 3 > currentPath.length);
        //        continueTrail = (continueTrail && currentPath.length > 15 && myTarget.getClass() == myGrid.GetMyUserChar().getClass());
        //        continueTrail = continueTrail && !(Point2D.distance(this.GetX(), this.GetY(), myGrid.GetMyUserChar().GetX(), myGrid.GetMyUserChar().GetY()) < 2);
//        if (currentPath != null) {
//            myNavigator.IsRouteAvailable(this.GetCoord(), myTarget.GetCoord());
//            continueTrail = myNavigator.GetMyCorrectNavigationCoords().length > (currentPath.length - myCurrentRouteIndex);
//            continueTrail = continueTrail && myTarget.getClass() == myGrid.GetMyUserChar().getClass();
//            continueTrail = continueTrail && currentPath.length > 15;
//            continueTrail = continueTrail && currentPath.length < myCurrentRouteIndex;
//            continueTrail = continueTrail && (Point2D.distance(this.GetX(), this.GetY(), myGrid.GetMyUserChar().GetX(), myGrid.GetMyUserChar().GetY()) > 2);
//        } else {
//            continueTrail = false;
//        }
//
//        if (continueTrail) {
//            myCurrentRouteIndex++;
//            System.out.println(" Long Chase");
//            return currentPath[myCurrentRouteIndex];
//        } else {
//            myNavigator = new ClsNavigator(myGrid);
//            myNavigator.IsRouteAvailable(this.GetCoord(), myTarget.GetCoord());
//
//            myCurrentRouteIndex = 1;
//            System.out.println(" New Chase");
//            currentPath = myNavigator.GetMyCorrectNavigationCoords();
//            if (currentPath.length == 0) {
//                return this.GetCoord().Move(ClsGrid.eDirection.RANDOM);
//            } else if (currentPath.length == 1) {
//                return currentPath[0];
//            }
//            return currentPath[myCurrentRouteIndex];
//        }
            // </editor-fold>

    }

    private ClsBunny FetchCloseBunny() {
        for (ClsBunny currBunny : myGrid.GetBunnies()) {
            double distanceFromBear = Point2D.distance(this.GetX(), this.GetY(), currBunny.GetX(), currBunny.GetY());
            if (distanceFromBear < BUNNY_CHASE_RADIUS) {
                if (!currBunny.IsDead()) {
                    return currBunny;
                }
            }
        }

        return null;
    }

    private ClsCharacter DetermineTarget() {

        ClsCharacter returnSquare;

        returnSquare = FetchCloseBunny();

        if (!(returnSquare == null)) {
//            if (this.myNavigator.IsRouteAvailable(this.GetCoord(), returnSquare.GetCoord())) {
//            System.out.print("Bear: bunny");
            return returnSquare;
//            }
        }

        double distanceFromUser = Point2D.distance(this.GetX(), this.GetY(), myGrid.GetMyUserChar().GetX(), myGrid.GetMyUserChar().GetY());

        if (distanceFromUser < BEAR_RANDOM_RADIUS) {
//            System.out.print("Bear: User");
            return myGrid.GetMyUserChar();
        }

        return null;
    }

    public boolean GetIsStopped() {
        return myIsStopped;
    }

    public void SetIsStopped(boolean myIsEating) {
        this.myIsStopped = myIsEating;
    }

}
