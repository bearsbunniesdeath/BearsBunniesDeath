/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trunk;

import java.util.Arrays;
import java.util.Random;
import static trunk.ClsGrid.NUMOFX;
import static trunk.ClsGrid.NUMOFY;
import static trunk.ClsHelperUtils.AddCoordFromArray;

/**
 *
 * @author Matt
 */
public class ClsObstacle {

    private int myNumOfX;
    private int myNumOfY;
    private boolean myIsPlaced = false;
    private ClsCoordinate[] myBlockedCoords;
    private ClsCoordinate[] myObstacleCoords;
//    final int 

    ClsObstacle(ClsCoordinate[] blockedCoords, int numX, int numY) {
        this.myBlockedCoords = blockedCoords;
        this.myNumOfX = numX;
        this.myNumOfY = numY;

        while (!this.myIsPlaced) {
            BuildObstacleCoordinates();
        }
        
    }

    private void BuildObstacleCoordinates() {
        Random randNumberGenerator = new Random();

        int sizeX = 1; //randNumberGenerator.nextInt(2) + 1; // 1 or 2 length and Height
        int sizeY = 1; //randNumberGenerator.nextInt(2) + 1;

        int attemptedX = randNumberGenerator.nextInt(myNumOfX - sizeX);
        int attemptedY = randNumberGenerator.nextInt(myNumOfY - sizeY);

        ClsCoordinate[] attemptedCoords = BuildObstacleCoords(attemptedX, attemptedY, sizeX, sizeY);

        if (IsObstacleClear(attemptedCoords)) {
            this.myObstacleCoords = attemptedCoords;
            this.myIsPlaced = true;
        }
    }

    private ClsCoordinate[] BuildObstacleCoords(int x, int y, int width, int height) {
        ClsCoordinate[] obstacleCoords = new ClsCoordinate[width * height];
        int index = 0;
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                obstacleCoords[index] = new ClsCoordinate(i, j);
                index++;
            }
        }
        return obstacleCoords;
    }

    private boolean IsObstacleClear(ClsCoordinate[] obstacleCoords) {
        for (ClsCoordinate coord : obstacleCoords) {
            for (ClsCoordinate blockCoord : myBlockedCoords){
                if (coord.Equals(blockCoord)){
                    return false;
                }
            }
        }
        return true;
    }

    public ClsCoordinate[] getMyObstacleCoords() {
        return myObstacleCoords;
    }
    
    public static ClsCoordinate[] BuildRiverCoordinates() {
        int currX = 0;
        int currY;
        Random randNumberGenerator = new Random();
        int riverVerticalSpace = NUMOFY / 2;
        int riverVerticalOffset = NUMOFY / 4;
        currY = randNumberGenerator.nextInt(riverVerticalSpace) + riverVerticalOffset;

        ClsCoordinate[] myRiverCoordinates = new ClsCoordinate[0];
        myRiverCoordinates = AddCoordFromArray(myRiverCoordinates, new ClsCoordinate(currX, currY));
        //this.myRiverCoordinates[0] = new ClsCoordinate(currX, currY);
        int segmentDecision;
        ClsGrid.eDirection lastSegmentDirection = ClsGrid.eDirection.EAST;
        while (currX < NUMOFX - 1) {
            segmentDecision = randNumberGenerator.nextInt(3);
            if (segmentDecision == 0) {
                currX++;
                myRiverCoordinates = AddCoordFromArray(myRiverCoordinates, new ClsCoordinate(currX, currY));

                lastSegmentDirection = ClsGrid.eDirection.EAST;
            } else if ((segmentDecision == 1) && (lastSegmentDirection != ClsGrid.eDirection.NORTH) && (currY < riverVerticalOffset + riverVerticalSpace - 1)) {
                currY++;
                myRiverCoordinates = AddCoordFromArray(myRiverCoordinates, new ClsCoordinate(currX, currY));
                lastSegmentDirection = ClsGrid.eDirection.SOUTH;
            } else if ((segmentDecision == 2) && (lastSegmentDirection != ClsGrid.eDirection.SOUTH) && (currY > riverVerticalOffset)) {
                currY--;
                myRiverCoordinates = AddCoordFromArray(myRiverCoordinates, new ClsCoordinate(currX, currY));
                lastSegmentDirection = ClsGrid.eDirection.NORTH;
            }
        }
//        OutputRiverCoordinates(myRiverCoordinates);
        return myRiverCoordinates;
    }
    
    private static void OutputRiverCoordinates(ClsCoordinate[] myRiverCoordinates) {
        System.out.print("xRiver = [");
        for (int i = 0; i < myRiverCoordinates.length; i++) {
            System.out.print(Integer.toString(myRiverCoordinates[i].x));
            if (i != myRiverCoordinates.length - 1) {
                System.out.print(",");
            }
        }
        System.out.println("]");

        System.out.print("yRiver = [");
        for (int i = 0; i < myRiverCoordinates.length; i++) {
            System.out.print(Integer.toString(myRiverCoordinates[i].y));
            if (i != myRiverCoordinates.length - 1) {
                System.out.print(",");
            }
        }
        System.out.println("]");

    }

}
