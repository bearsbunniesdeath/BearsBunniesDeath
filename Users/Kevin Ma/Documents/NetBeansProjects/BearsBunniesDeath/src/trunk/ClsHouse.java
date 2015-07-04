/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trunk;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Random;
import static trunk.ClsHelperUtils.RemoveCoordFromArray;

/**
 *
 * @author Matt
 */
public class ClsHouse {

    private ClsCoordinate[] myBlockedCoords;
    private ClsCoordinate[] myRemovableWallCoords;
    private ClsCoordinate[] myWallCoords;
    private ClsCoordinate[] myInteriorCoords;
    private ClsCoordinate[] myMapBorderCoords;
    private ClsCoordinate myDoorCoord;
    private ClsCoordinate myStairsCoord;
    private ClsCoordinate myMinCoord;
    private ClsCoordinate myMaxCoord;
    private int myNumOfXMap;
    private int myNumOfYMap;
    private int myXSize;
    private int myYSize;
    private int myXCoord;
    private int myYCoord;
    private boolean myIsHousePlaced = false;
    private boolean myIsDoorPlaced = false;
    private boolean myIsWindowPlaced = false;
    private int myLightTime = -1;
    private int myStartLightTime = 10;

    public ClsHouse(ClsCoordinate blockedCoords[], int NumOfX, int NumOfY, ClsCoordinate min, ClsCoordinate max) {
        myBlockedCoords = blockedCoords;
        myNumOfXMap = NumOfX;
        myNumOfYMap = NumOfY;
        myMinCoord = min;
        myMaxCoord = max;
        myWallCoords = new ClsCoordinate[NumOfX * NumOfY];
        myRemovableWallCoords = new ClsCoordinate[NumOfX * NumOfY];
        myMapBorderCoords = BuildMapBorderCoords();

        BuildHouseCoordinates();
    }

    private void BuildHouseCoordinates() {
        SizeHouse();
        int housePlacementAttempts = 0;
        int doorPlacementAttempts = 0;

//        Need a better way to check every possible position. housePlacementAttempts < myNumOfX * myNumOfY doesn't guaruntee.
        while (housePlacementAttempts < myNumOfXMap * myNumOfYMap && !this.myIsHousePlaced) {
            AttemptToPlaceHouse(myXSize, myYSize);
            housePlacementAttempts++;

        }

        InitializeInterior();

        while (doorPlacementAttempts < this.myRemovableWallCoords.length && !this.myIsDoorPlaced) {
            PlaceDoorOnHouse();
        }

        PlaceStairsInHouse();
    }

    private void SizeHouse() {
//        int[] houseSize = {0, 0};
        Random randNumberGenerator = new Random();
        this.myXSize = randNumberGenerator.nextInt(2) + 3;
        this.myYSize = randNumberGenerator.nextInt(2) + 3;
        this.myInteriorCoords = new ClsCoordinate[(myXSize - 2) * (myYSize - 2)];
    }

    private void AttemptToPlaceHouse(int sizeX, int sizeY) {
        Random randNumberGenerator = new Random();
        int attemptedX = randNumberGenerator.nextInt((myMaxCoord.x - sizeX) - myMinCoord.x + 1) + myMinCoord.x;
        int attemptedY = randNumberGenerator.nextInt((myMaxCoord.y - sizeY) - myMinCoord.y + 1) + myMinCoord.y;

        for (int i = attemptedX; i < attemptedX + sizeX; i++) {
            for (int j = attemptedY; j < attemptedY + sizeY; j++) {
                if (!CheckForRiverCollision(new ClsCoordinate(i, j))) {
                    return;
                }
            }
        }
        PlaceHouseWalls(sizeX, sizeY, attemptedX, attemptedY);
//        PrintHouseCoordinates();
        myXCoord = attemptedX;
        myYCoord = attemptedY;
        this.myIsHousePlaced = true;
    }

    private boolean CheckForRiverCollision(ClsCoordinate coord) {
        for (ClsCoordinate riverCoord : myBlockedCoords) {
            if (riverCoord.Equals(coord)) {
                return false;
            }
        }
        return true;
    }

    private void PlaceHouseWalls(int sizeX, int sizeY, int attemptedX, int attemptedY) {
        int segmentIndex = 0;
        int removableSegmentIndex = 0;
        for (int i = attemptedX; i < attemptedX + sizeX; i++) {
//            Top wall left to right
            this.myWallCoords[segmentIndex] = new ClsCoordinate(i, attemptedY);
            if (i != attemptedX && i != attemptedX + sizeX - 1) {
                this.myRemovableWallCoords[removableSegmentIndex] = new ClsCoordinate(i, attemptedY);
                removableSegmentIndex++;
            }
            segmentIndex++;
        }
//        Right wall top to bottom
        for (int i = attemptedY + 1; i < attemptedY + sizeY; i++) {

            this.getMyWallCoords()[segmentIndex] = new ClsCoordinate(attemptedX + sizeX - 1, i);
            if (i != attemptedY + sizeY - 1) {
                this.myRemovableWallCoords[removableSegmentIndex] = new ClsCoordinate(attemptedX + sizeX - 1, i);
                removableSegmentIndex++;
            }
            segmentIndex++;
        }
// Bottom Wall Left to Right
        for (int i = attemptedX; i < attemptedX + sizeX - 1; i++) {
            this.getMyWallCoords()[segmentIndex] = new ClsCoordinate(i, attemptedY + sizeY - 1);
            if (i != attemptedX) {
                this.myRemovableWallCoords[removableSegmentIndex] = new ClsCoordinate(i, attemptedY + sizeY - 1);
                removableSegmentIndex++;
            }
            segmentIndex++;
        }
//Left Wall Top to Bottom
        for (int i = attemptedY + 1; i < attemptedY + sizeY - 1; i++) {
            this.getMyWallCoords()[segmentIndex] = new ClsCoordinate(attemptedX, i);
            this.myRemovableWallCoords[removableSegmentIndex] = new ClsCoordinate(attemptedX, i);
            segmentIndex++;
            removableSegmentIndex++;
        }
        this.myWallCoords = Arrays.copyOf(this.myWallCoords, segmentIndex);
        this.myRemovableWallCoords = Arrays.copyOf(this.myRemovableWallCoords, removableSegmentIndex);

    }

    private void PrintHouseCoordinates() {
        System.out.print("xHouse = [");
        for (int i = 0; i < this.getMyWallCoords().length; i++) {
            System.out.print(Integer.toString(getMyWallCoords()[i].x));
            if (i != this.getMyWallCoords().length - 1) {
                System.out.print(",");
            }
        }
        System.out.println("]");

        System.out.print("yHouse = [");
        for (int i = 0; i < this.getMyWallCoords().length; i++) {
            System.out.print(Integer.toString(getMyWallCoords()[i].y));
            if (i != this.getMyWallCoords().length - 1) {
                System.out.print(",");
            }
        }
        System.out.println("]");
    }

    public ClsCoordinate[] getMyWallCoords() {
        return myWallCoords;
    }

    private boolean PlaceDoorOnHouse() {
        Random randNumberGenerator = new Random();
        int attemptedDoorPositionIndex = randNumberGenerator.nextInt(this.myRemovableWallCoords.length);
        for (int i = attemptedDoorPositionIndex; i < this.myRemovableWallCoords.length; i++) {
            ClsCoordinate attemptedDoorPosition = this.myRemovableWallCoords[attemptedDoorPositionIndex % this.myRemovableWallCoords.length];

            //        Check for Door Blocking Issues
            if (!IsDoorBlocked(attemptedDoorPosition)) {

                this.myWallCoords = RemoveCoordFromArray(this.myWallCoords, attemptedDoorPosition);
                this.myDoorCoord = attemptedDoorPosition;
                this.myIsDoorPlaced = true;
                return true;
            }
        }
        return false;
    }

    private void PlaceStairsInHouse() {
        ClsCoordinate farthestCoord = this.myDoorCoord;
        double farthest = 0.0;
        for (ClsCoordinate interior : this.myInteriorCoords) {
            if (Point2D.distance(interior.x, interior.y, this.myDoorCoord.x, this.myDoorCoord.y) > farthest) {
                farthest = Point2D.distance(interior.x, interior.y, this.myDoorCoord.x, this.myDoorCoord.y);
                farthestCoord = interior;
            }
        }
        this.myStairsCoord = farthestCoord;
    }

    private ClsCoordinate[] BuildMapBorderCoords() {
        ClsCoordinate[] mapBorderCoords = new ClsCoordinate[(2 * myNumOfXMap) + 2 * (myNumOfYMap - 2)];
        int segment = 0;

//        Top Row of Map
        for (int i = 0; i < myNumOfXMap; i++) {
            mapBorderCoords[segment] = new ClsCoordinate(i, 0);
            segment++;
        }
//        Right Side (Minus Top)
        for (int i = 1; i < myNumOfYMap; i++) {
            mapBorderCoords[segment] = new ClsCoordinate(myNumOfXMap - 1, i);
            segment++;
        }
//        Bottom Row (minus Right)
        for (int i = 0; i < myNumOfXMap - 1; i++) {
            mapBorderCoords[segment] = new ClsCoordinate(i, myNumOfYMap - 1);
            segment++;
        }
//        Left Row (minus top and bottom)]
        for (int i = 1; i < myNumOfYMap - 1; i++) {
            mapBorderCoords[segment] = new ClsCoordinate(0, i);
            segment++;
        }

        return mapBorderCoords;
    }

    private boolean IsDoorBlocked(ClsCoordinate coord) {
        boolean blocked = false;
        for (ClsCoordinate borderCoord : this.myMapBorderCoords) {
            if (borderCoord.Equals(coord)) {
                blocked = true;
            }
        }
//       River: Check all adjacent coordinates for water
        ClsCoordinate topAdjacentCoord = new ClsCoordinate(coord.x, coord.y - 1);
        ClsCoordinate botAdjacentCoord = new ClsCoordinate(coord.x, coord.y + 1);
        ClsCoordinate rightAdjacentCoord = new ClsCoordinate(coord.x - 1, coord.y);
        ClsCoordinate leftAdjacentCoord = new ClsCoordinate(coord.x + 1, coord.y);
        for (ClsCoordinate riverCoord : this.myBlockedCoords) {

            if (topAdjacentCoord.Equals(riverCoord) || botAdjacentCoord.Equals(riverCoord) || leftAdjacentCoord.Equals(riverCoord) || rightAdjacentCoord.Equals(riverCoord)) {
                blocked = true;
            }
        }
        return blocked;
    }

    public ClsCoordinate getMyDoorCoord() {
        return myDoorCoord;
    }

    private void InitializeInterior() {
        int index = 0;
        for (int i = this.myXCoord + 1; i < this.myXCoord + this.myXSize - 1; i++) {
            for (int j = this.myYCoord + 1; j < this.myYCoord + this.myYSize - 1; j++) {
                this.myInteriorCoords[index] = new ClsCoordinate(i, j);
                index++;
            }
        }
    }

    public ClsCoordinate[] getMyInteriorCoords() {
        return myInteriorCoords;
    }

    public ClsCoordinate getMyStairsCoord() {
        return myStairsCoord;
    }

    public ClsCoordinate[] getDoorBlockingCoords() {
        ClsCoordinate[] blockedCoords = new ClsCoordinate[5];
        blockedCoords[0] = this.myDoorCoord.Move(ClsGrid.eDirection.EAST);
        blockedCoords[1] = this.myDoorCoord.Move(ClsGrid.eDirection.NORTH);
        blockedCoords[2] = this.myDoorCoord.Move(ClsGrid.eDirection.WEST);
        blockedCoords[3] = this.myDoorCoord.Move(ClsGrid.eDirection.SOUTH);
        blockedCoords[4] = this.myDoorCoord;

        return blockedCoords;
    }
    
    public ClsCoordinate[] GetAllHouseCoords(){
        ClsCoordinate[] retArray = ClsHelperUtils.AppendCoordArrays(myWallCoords, myInteriorCoords);
        retArray = ClsHelperUtils.AddCoordFromArray(retArray, myDoorCoord);
        return retArray;
    }

    public int GetLightTime() {
        return myLightTime;
    }

    public void ResetLightTime() {
        this.myLightTime = myStartLightTime;
    }

    public void UseOneLightingTurn() {
        if (myLightTime > -1) {
            this.myLightTime -= 1;
        }
    }
}
