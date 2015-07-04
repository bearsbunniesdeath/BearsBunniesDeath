/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trunk;

import java.awt.geom.Point2D;
import static trunk.ClsGrid.NUMOFX;
import static trunk.ClsGrid.NUMOFY;
import static trunk.ClsHelperUtils.AddCoordFromArray;
import static trunk.ClsHelperUtils.IsCoordinateInArray;
import static trunk.ClsHelperUtils.RandomNumber;
import static trunk.ClsHelperUtils.RemoveCoordFromArray;

/**
 *
 * @author Matt
 */
public class ClsNavigator {

    ClsGrid myGrid;
    private ClsCoordinate[] myCorrectNavigationCoords = new ClsCoordinate[0];
    private ClsCoordinate[] myNavigatedCoords = new ClsCoordinate[0];

    public ClsNavigator(ClsGrid mapGrid) {
        myGrid = mapGrid;
    }

    // <editor-fold desc="Original Navigation">
    public void ClearNavigator() {
        myCorrectNavigationCoords = new ClsCoordinate[0];
        myNavigatedCoords = new ClsCoordinate[0];
    }

    public boolean IsRouteAvailable(ClsCoordinate currentCoord, ClsCoordinate coordDestination) {

        if (currentCoord.x > NUMOFX - 1 || currentCoord.x < 0 || currentCoord.y > NUMOFY - 1 || currentCoord.y < 0) {
            return false;
        }

        ClsGrid.eTerrain currentTerrian = myGrid.GetSquare(currentCoord).GetType();

        if (currentCoord.Equals(coordDestination)) {
            myCorrectNavigationCoords = (AddCoordFromArray(myCorrectNavigationCoords, currentCoord));
            return true;
        } else if (currentTerrian != ClsGrid.eTerrain.WALKABLE) {
            return false;
        } else if (IsCoordinateInArray(currentCoord, myNavigatedCoords) != -1) {
            return false;
        }

        myCorrectNavigationCoords = (AddCoordFromArray(myCorrectNavigationCoords, currentCoord));
        myNavigatedCoords = AddCoordFromArray(myNavigatedCoords, currentCoord);

        ClsGrid.eDirection[] priority;

        priority = DeterminePriorityDirectionOrder(currentCoord, coordDestination);

        if (IsRouteAvailable(currentCoord.Move(priority[0]), coordDestination)) {
            return true;
        }
        if (IsRouteAvailable(currentCoord.Move(priority[1]), coordDestination)) {
            return true;
        }
        if (IsRouteAvailable(currentCoord.Move(priority[2]), coordDestination)) {
            return true;
        }
        if (IsRouteAvailable(currentCoord.Move(priority[3]), coordDestination)) {
            return true;
        }

        SetMyCorrectNavigationCoords(RemoveCoordFromArray(GetMyCorrectNavigationCoords(), currentCoord));

        return false;
    }

    private ClsGrid.eDirection[] DeterminePriorityDirectionOrder(ClsCoordinate startCoord, ClsCoordinate endCoord) {
        int verticalDifference = startCoord.y - endCoord.y;
        int horizontalDifference = startCoord.x - endCoord.x;

        ClsGrid.eDirection[] topTwo = new ClsGrid.eDirection[2];
        ClsGrid.eDirection[] priorityDirOrder = new ClsGrid.eDirection[4];

        if (verticalDifference < 0) {
            topTwo[0] = ClsGrid.eDirection.SOUTH;
        } else {
            topTwo[0] = ClsGrid.eDirection.NORTH;
        }

        if (horizontalDifference < 0) {
            topTwo[1] = ClsGrid.eDirection.EAST;
        } else {
            topTwo[1] = ClsGrid.eDirection.WEST;
        }

        if (Math.abs(horizontalDifference) > Math.abs(verticalDifference)) {
            priorityDirOrder[0] = topTwo[1];
            priorityDirOrder[1] = topTwo[0];
        } else if (Math.abs(horizontalDifference) < Math.abs(verticalDifference)) {
            priorityDirOrder[0] = topTwo[0];
            priorityDirOrder[1] = topTwo[1];
        } else if (Math.abs(horizontalDifference) == Math.abs(verticalDifference)) {
            if (RandomNumber(2) == 0) {
                priorityDirOrder[0] = topTwo[0];
                priorityDirOrder[1] = topTwo[1];
            } else {
                priorityDirOrder[0] = topTwo[1];
                priorityDirOrder[1] = topTwo[0];
            }
        }
        priorityDirOrder[2] = myGrid.GetOppositeDirection(priorityDirOrder[1]);
        priorityDirOrder[3] = myGrid.GetOppositeDirection(priorityDirOrder[0]);

        return priorityDirOrder;
    }

    public ClsCoordinate[] GetMyCorrectNavigationCoords() {
        return myCorrectNavigationCoords;
    }

    public void SetMyCorrectNavigationCoords(ClsCoordinate[] myCorrectNavigationCoords) {
        this.myCorrectNavigationCoords = myCorrectNavigationCoords;
    }
        // </editor-fold >

    // <editor-fold desc="A* Navigation">
//////    function A*(start,goal)
//////    closedset := the empty set    // The set of nodes already evaluated.
//////    openset := {start}    // The set of tentative nodes to be evaluated, initially containing the start node
//////    cameFrom := the empty map    // The map of navigated nodes.
////// 
//////    g_score[start] := 0    // Cost from start along best known path.
//////    // Estimated total cost from start to goal through y.
//////    f_score[start] := g_score[start] + heuristic_cost_estimate(start, goal)
////// 
//////    while openset is not empty
//////        current := the node in openset having the lowest f_score[] value
//////        if current = goal
//////            return reconstruct_path(cameFrom, goal)
////// 
//////        remove current from openset
//////        add current to closedset
//////        for each neighbor in neighbor_nodes(current)
//////            if neighbor in closedset
//////                continue
//////            tentative_g_score := g_score[current] + dist_between(current,neighbor)
////// 
//////            if neighbor not in openset or tentative_g_score < g_score[neighbor] 
//////                cameFrom[neighbor] := current
//////                g_score[neighbor] := tentative_g_score
//////                f_score[neighbor] := g_score[neighbor] + heuristic_cost_estimate(neighbor, goal)
//////                if neighbor not in openset
//////                    add neighbor to openset
////// 
//////    return failure
////// 
//////function reconstruct_path(cameFrom,current)
//////    total_path := [current]
//////    while current in cameFrom:
//////        current := cameFrom[current]
//////        total_path.append(current)
//////    return total_path
    public ClsCoordinate[] GetShortestPath(ClsCoordinate start, ClsCoordinate finish) {

        ClsCoordinate[] closedSet = {};
        ClsCoordinate[] openSet = {start};
        ClsCoordinate[][] cameFrom = new ClsCoordinate[NUMOFX][NUMOFY];

        Double[][] gScore = new Double[NUMOFX][NUMOFY];
        Double[][] fScore = new Double[NUMOFX][NUMOFY];

        gScore[start.x][start.y] = 0.0;
        fScore[start.x][start.y] = 0.0 + Point2D.distance(start.x, start.y, finish.x, finish.y);

        ClsCoordinate current = new ClsCoordinate(-1, -1);
        Double lowestGScore = 50000.0;
        Integer lowestGScoreX = -1;
        Integer lowestGScoreY = -1;
        while (openSet.length != 0) {

            //FIND THE LOWEST GSCORE COORD TO CHECK NEIGHBORS
            lowestGScore = 50000.0;
            for (ClsCoordinate coord : openSet) {
                if (gScore[coord.x][coord.y] != null) {
                    if (fScore[coord.x][coord.y] < lowestGScore) {
                        lowestGScore = fScore[coord.x][coord.y];
                        current = coord;
                    }
                }
            }

            if (current.Equals(finish)) {
                return ReconstructPath(cameFrom, finish);
            }

            //              remove current from openset
            if (IsCoordinateInArray(current, openSet) != -1) {
                openSet = RemoveCoordFromArray(openSet, current);
            }
            //        add current to closedset
            closedSet = AddCoordFromArray(closedSet, current);

            //Create Array of Neighbouring coord to current.
            ClsCoordinate[] neighbours = FetchNeighbourCoordinates(current);

            Boolean breakFor = false;
            for (ClsCoordinate currNeighbour : neighbours) {
                //////            if neighbor in closedset
                breakFor = IsCoordinateInArray(currNeighbour, closedSet) == -1;
                //////                continue
                if (breakFor) {
                    //////            tentative_g_score := g_score[current] + dist_between(current,neighbor)
                    Double tentativeGScore = gScore[current.x][current.y] + Point2D.distance(current.x, current.y, currNeighbour.x, currNeighbour.y);
                    if (IsCoordinateInArray(currNeighbour, openSet) == -1 || tentativeGScore < gScore[currNeighbour.x][currNeighbour.y]) {
                        cameFrom[currNeighbour.x][currNeighbour.y] = current;
                        gScore[currNeighbour.x][currNeighbour.y] = tentativeGScore;
                        fScore[currNeighbour.x][currNeighbour.y] = gScore[currNeighbour.x][currNeighbour.y] + Point2D.distance(currNeighbour.x, currNeighbour.y, finish.x, finish.y);

                        if (IsCoordinateInArray(currNeighbour, openSet) == -1) {
                            openSet = AddCoordFromArray(openSet, currNeighbour);
                        }

                    }

                }

            }
            {
//                System.err.println("Current Lowest GScore:" + current.x);
            }

        }
        return null;
    }

    private ClsCoordinate[] FetchNeighbourCoordinates(ClsCoordinate current) {
        ClsCoordinate[] neighbours = {};
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (Math.abs(i) != Math.abs(j)) {
                    ClsCoordinate potentialNeighbour = new ClsCoordinate(current.x + i, current.y + j);
                    if (myGrid.GetSquare(potentialNeighbour) != null) {
                        if (myGrid.GetSquare(potentialNeighbour).type == ClsGrid.eTerrain.WALKABLE) {
                            neighbours = AddCoordFromArray(neighbours, potentialNeighbour);
                        }
                    }
                }
            }
        }
        return neighbours;
    }

    private ClsCoordinate[] ReconstructPath(ClsCoordinate[][] cameFrom, ClsCoordinate current) {
        Boolean doWhile = false;
        ClsCoordinate[] path = {};
//        for (int i = 0; i < NUMOFX; i++) {
//            for (int j = 0; j < NUMOFY; j++) {
//                if (cameFrom[i][j] != null) {
//                    if (cameFrom[i][j].Equals(current)) {
//                        doWhile = true;
//                    }
//                }
//            }
//        }
        path = AddCoordFromArray(path, current);
        do {
            doWhile = false;
            current = cameFrom[current.x][current.y];
            if (current != null){
                        path = AddCoordFromArray(path, current);
                        doWhile = true;
            }


        } while (doWhile);
        return path;

    }
//total_path := [current]
//    while current in came_from:
//        current := came_from[current]
//        total_path.append(current)
//    return total_path
    // </editor-fold>
}
