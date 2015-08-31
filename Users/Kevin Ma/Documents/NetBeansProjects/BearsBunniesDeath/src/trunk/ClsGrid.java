/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trunk;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Arrays;
import java.util.Random;
import javax.swing.*;
import static trunk.ClsHelperUtils.*;

/**
 *
 * @author Kevin Ma
 */
public class ClsGrid extends JPanel implements KeyListener {

    ClsSquare[] squares;
    public static final int PAD = 10;         //Space between frame and grid
    public static int NUMOFX = 35;       //Number of columns
    public static int NUMOFY = 19;       //Number of rows
    public static final int SQUARELEN = 32;

    public static int MAX_NUM_OF_HOUSES = 4;
    public boolean quadrantHouseSpawn = true;   //Will override max num of house to 4

    public static int MAX_NUM_OF_OBSTACLES = 100;
    public static final int DARKNESS_RADIUS = 3;
    public static int NUM_OF_BUNNIES = 25;
    public static int NUM_OF_BEARS = 3;
    public static final int NUM_OF_FLARES = 15;
    public static final int NUM_OF_BEAR_TRAPS = 15;

    public double BEAR_USER_SPAWN_DISTANCE = 5.0;
    public boolean mylightsOn = false;

    public static boolean BUILD_RIVER = true;
    public static int NUM_OF_BRIDGES = 2;

//    private final int myNumOfX = 32;
//    private final int myNumOfY = 30;
//    private final int myNumOfSquares = myNumOfX * myNumOfY;
    private ClsHouse[] myHouses;
    private ClsObstacle[] myObstacles;
    private ClsCoordinate[] myRiverCoordinates;
//    private ClsCoordinate[] myBlockedCoordinates = new ClsCoordinate[myRiverMaxSquares];
    private ClsCoordinate[] myNavigatedCoords = new ClsCoordinate[0];
    public ClsCoordinate[] myCorrectNavigationCoords = new ClsCoordinate[0];

    private ClsUIDisplay mySideBar;
    private Boolean[] myPotentialPickups = {false, false, false, false, false, false}; // {BUNNIES, FLARES, BEARTRAPS, BUNNY_FLARES, BEARTRAP_FLARE, BEARTRAP_BUNNY}
    private int myNumberOfPotentialPickupItems;
    private boolean myIsItemActionAvailable;

    public static final int BUNNY_INDEX = 0;
    public static final int FLARE_INDEX = 1;
    public static final int BEARTRAP_INDEX = 2;

    public static final int BUNNY_FLARE_INDEX = 3;
    public static final int BEARTRAP_FLARE_INDEX = 4;
    public static final int BEARTRAP_BUNNY_INDEX = 5;

    private ClsUserCharacter myUserChar;
    private ClsBear[] myBears;
    private ClsBunny[] myBunnies;
    private ClsFlare[] myFlares;
    private ClsBearTrap[] myBearTraps;

    private int movesMade = 0;
    private int highScore = 0;

    private int myEasyHighScore = 0;
    private int myNormalHighScore = 0;
    private int myHardHighScore = 0;

    public static eDifficulty DIFFICULTY = eDifficulty.NORMAL;

    private boolean titleScreen = true;
    private boolean playing = false;
    private boolean gameOver = false;

    public static enum eDifficulty {

        EASY, NORMAL, HARD
    }

    public static enum eDirection {

        NORTH, EAST, SOUTH, WEST, RANDOM, NONE
    };

    public static enum eMapQuadrant {

        NW, NE, SW, SE
    }

    public eDirection GetOppositeDirection(eDirection direction) {
        if (direction == eDirection.EAST) {
            return eDirection.WEST;
        } else if (direction == eDirection.WEST) {
            return eDirection.EAST;
        } else if (direction == eDirection.NORTH) {
            return eDirection.SOUTH;
        } else if (direction == eDirection.SOUTH) {
            return eDirection.NORTH;
        }
        return null;
    }

    public static enum eTerrain {

        WALKABLE, RIVER, BLOCKED
    };

    public static enum eTileSet {

        HOUSE, STAIRS, TREE, ROCK, BRIDGE
    }

    public static enum eDarkness {

        NONE, NW, NE, SW, SE, DIM, FULL, ALERT
    }

    public static enum eItemImage {

        BEARTRAP, FLARE, BUNNY_FLARE, BEARTRAP_BUNNY, BEARTRAP_FLARE, BLANK;
    }

    public static enum eCharacterImage {

        USER_L, USER_R, USER_U, USER_D, BUNNY, BEAR, BUNNY_DEAD, BEAR_EATING_BUNNY, BLANK;
    }

    public static enum eUIImage {

        TRANSPARENT_BOX;
    }

    public ClsGrid() {
        int retryAttempts = 0;
        int maxRetries = 3;
        while (true) {
            try {
                LoadConfig();
                InitSquares();
                BuildTerrain();
                BuildCharacters(NUM_OF_BUNNIES, NUM_OF_BEARS);

                AddItems();

                UpdateDarkness(DARKNESS_RADIUS, null);

                if (mylightsOn) {
                    LightEntireMap();
                }

                setFocusable(true);
                addKeyListener(this);

                break;

            } catch (Exception e) {
                System.err.println("An error occured while initializing the game");
                System.err.println("Attempt " + Integer.toString(++retryAttempts) + " of " + Integer.toString(maxRetries));
                if (retryAttempts == maxRetries) {
                    throw e;
                }
            }
        }
    }

    public void ResetGame() {
        int retryAttempts = 0;
        int maxRetries = 3;

        int tempRetries = 0;

        while (true) {
            try {
                titleScreen = false;
                playing = true;
                movesMade = 0;
                mySideBar.PaintLoadingString(this.getGraphics(), "Rebuilding map...");
                LoadConfig();
                InitSquares();
                BuildTerrain();
                BuildCharacters(NUM_OF_BUNNIES, NUM_OF_BEARS);
                AddItems();
                UpdateDarkness(DARKNESS_RADIUS, null);

                if (mylightsOn) {
                    LightEntireMap();
                }

//                boolean temp = false;
//                if (temp) {
                break;
//                }
//
//                tempRetries++;
//                System.out.print(tempRetries);

            } catch (Exception e) {
                System.err.println("An error occured while resetting the game");
                System.err.println("Attempt " + Integer.toString(++retryAttempts) + " of " + Integer.toString(maxRetries));
                if (retryAttempts == maxRetries) {
                    throw e;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int width = this.getWidth();
        int height = this.getHeight();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

//        TODO: Move all Score / Item display keeping logic to SideBar
        if (mySideBar == null && width > (PAD + SQUARELEN * NUMOFX) && height > (PAD + SQUARELEN * NUMOFY)) {
            mySideBar = new ClsUIDisplay(PAD + SQUARELEN * NUMOFX, PAD + SQUARELEN * NUMOFY, width, height);
//            mySideBar = new ClsUIDisplay(2260, 618, width, height);
        }

        Integer currDiffHighScore = 0;
        if (DIFFICULTY == eDifficulty.EASY) {
            currDiffHighScore = myEasyHighScore;
        } else if (DIFFICULTY == eDifficulty.NORMAL) {
            currDiffHighScore = myNormalHighScore;
        } else if (DIFFICULTY == eDifficulty.HARD) {
            currDiffHighScore = myHardHighScore;
        }
        if (mySideBar != null) {
            mySideBar.DrawSideBar(myPotentialPickups, myNumberOfPotentialPickupItems, myUserChar.GetItem(), myUserChar.GetItemToBeUsed(), g, movesMade, currDiffHighScore, myIsItemActionAvailable, DIFFICULTY);
        }

        if (titleScreen == true) {
            //Need to Move to UIDisplay
            g.drawString("Press a number to select a difficulty level", 350, 250);
            g.drawString("1 = Easy", 350, 300);
            g.drawString("2 = Normal", 350, 350);
            g.drawString("3 = Hard", 350, 400);

        } else if (playing == true) {
            //Paint terrain
            for (ClsSquare square : this.squares) {
                this.SetSquareImage(square, g2);
            }

            for (ClsBearTrap bearTrap : this.myBearTraps) {
                this.SetSquareImage(bearTrap, g2);
            }

            //Paint the bunnies
            for (ClsBunny bunny : this.myBunnies) {
                this.SetCharacterImage(bunny, g2);
            }

            for (ClsFlare flare : this.myFlares) {
                this.SetSquareImage(flare, g2);
                if (flare.GetCurrentLightTime() > 0) {
                    g.setFont(new Font("TimesRoman", Font.BOLD, 18));
                    g.drawString(Integer.toString(flare.GetCurrentLightTime()), flare.GetPixelX() + SQUARELEN / 2 - 5, flare.GetPixelY() + SQUARELEN / 2 + 5);
                    g.setFont(new Font("TimesRoman", Font.PLAIN, 12));
                }
            }

            //Paint the user character
            this.SetCharacterImage(this.myUserChar, g2);

            //Paint the bears
            for (ClsBear bears : this.myBears) {
                this.SetCharacterImage(bears, g2);
            }

            for (ClsBearTrap bearTrap : this.myBearTraps) {
                if (bearTrap.GetHoldBearFor() > 0) {
                    g.setColor(Color.red);
                    g.setFont(new Font("TimesRoman", Font.BOLD, 18));
                    g.drawString(Integer.toString(bearTrap.GetHoldBearFor()), bearTrap.GetPixelX() + SQUARELEN / 2 - 5, bearTrap.GetPixelY() + SQUARELEN / 2 + 5);
                    g.setFont(new Font("TimesRoman", Font.PLAIN, 12));
                }
            }

            //Paint the darkness
            for (ClsSquare square : this.squares) {
                if (!square.GetDarknessType().equals(eDarkness.NONE)) {
                    square.PaintDarkness(g2);
                }
            }

            //Paint the darkness alerts
            ClsCoordinate[] darknessCoord = new ClsCoordinate[0];
            for (ClsSquare square : this.squares) {
                if (square.GetDarknessType().equals(eDarkness.DIM)) {
                    darknessCoord = AddCoordFromArray(darknessCoord, square.GetCoord());
                }
            }

            if (this.mylightsOn) {
                mySideBar.DrawRulers(g2, NUMOFX, NUMOFY, PAD, SQUARELEN);
            }

            if (darknessCoord.length > 0) {
                for (ClsBear bears : this.myBears) {
                    if (IsCoordinateInArray(bears.GetCoord(), darknessCoord) != -1) {
                        this.GetSquare(bears.GetCoord()).Fill(g2, eDarkness.ALERT);
                    }
                }

                for (ClsBunny bunnies : this.myBunnies) {
                    if (bunnies.IsDead() == false && IsCoordinateInArray(bunnies.GetCoord(), darknessCoord) != -1) {
                        this.GetSquare(bunnies.GetCoord()).Fill(g2, eDarkness.ALERT);
                    }
                }
            }

        } else if (gameOver == true) {
            //Paint terrain
            for (ClsSquare square : this.squares) {
                this.SetSquareImage(square, g2);
            }
            //Paint the bunnies
            for (ClsBunny bunny : this.myBunnies) {
                this.SetCharacterImage(bunny, g2);
            }
            //Paint the bears
            for (ClsBear bears : this.myBears) {
                this.SetCharacterImage(bears, g2);
            }
            for (ClsFlare flare : this.myFlares) {
                this.SetSquareImage(flare, g2);
                if (flare.GetCurrentLightTime() > 0) {
                    g.setFont(new Font("TimesRoman", Font.BOLD, 18));
                    g.drawString(Integer.toString(flare.GetCurrentLightTime()), flare.GetPixelX() + SQUARELEN / 2 - 5, flare.GetPixelY() + SQUARELEN / 2 + 5);
                    g.setFont(new Font("TimesRoman", Font.PLAIN, 12));
                }
            }

            for (ClsBearTrap bearTrap : this.myBearTraps) {
                this.SetSquareImage(bearTrap, g2);
            }

            mySideBar.PaintGameOverScore(g, movesMade, currDiffHighScore);
        }
    }

    private void InitSquares() {
        this.squares = new ClsSquare[NUMOFX * NUMOFY];
        int square = 0;
        for (int i = 0; i < NUMOFX; i++) {
            double x = PAD + i * SQUARELEN;
            for (int j = 0; j < NUMOFY; j++) {
                double y = PAD + j * SQUARELEN;
                Rectangle2D.Double rect = new Rectangle2D.Double(x, y, SQUARELEN, SQUARELEN);
                this.squares[square] = new ClsSquare(new ClsCoordinate(i, j), rect, null, "Terrain");
                square++;
            }
        }
    }

    private void BuildCharacters(int numberOfBunnies, int numberOfBears) {
        //TESTING USING SHAPES FOR NOW 
        ClsCoordinate coord;
        ClsCoordinate[] walkCoord = this.GetCoordinates(eTerrain.WALKABLE);

        //Build user character
        double x = PAD + 7 * SQUARELEN;
        double y = PAD + 15 * SQUARELEN;
        Rectangle2D.Double rect; //= new Rectangle.Double(x, y, SQUARELEN, SQUARELEN);

        boolean validUserCharCoord = false;
        do {
            coord = walkCoord[RandomNumber(walkCoord.length)];
            walkCoord = RemoveCoordFromArray(walkCoord, coord);
            rect = new Rectangle2D.Double(PAD + coord.x * SQUARELEN, PAD + coord.y * SQUARELEN, SQUARELEN, SQUARELEN);

            ClsNavigator navigator = new ClsNavigator(this);

            //Any door will do. Since the houses and corner are all connected at this point
            //This check should reduce the map build freezing a lot.
            validUserCharCoord = navigator.IsRouteAvailable(coord, this.myHouses[0].getMyDoorCoord());

        } while (!validUserCharCoord);
        this.myUserChar = new ClsUserCharacter(coord, rect, null, this); //?Matt? USER SPAWNING

        //Build bunnies and bears              
        ClsBunny[] bunnies = new ClsBunny[numberOfBunnies];
        boolean validBunnyLocation;
        int placementAttempts = 0;
        for (int i = 0; i < numberOfBunnies; i++) {
            do {

                coord = walkCoord[RandomNumber(walkCoord.length)];

                ClsNavigator navigator = new ClsNavigator(this);
                validBunnyLocation = navigator.IsRouteAvailable(this.myUserChar.GetCoord(), coord);

                for (ClsHouse currHouse : myHouses) {
                    if (currHouse.getMyStairsCoord().Equals(coord)) {
                        validBunnyLocation = false;
                    }
                }

                placementAttempts++;
                if (placementAttempts > 1000) {
                    System.out.println("FAILED TO BUNNY PLACE");
                }

            } while (!validBunnyLocation);

            walkCoord = RemoveCoordFromArray(walkCoord, coord);
            rect = new Rectangle2D.Double(PAD + coord.x * SQUARELEN, PAD + coord.y * SQUARELEN, SQUARELEN, SQUARELEN);
            bunnies[i] = new ClsBunny(new ClsCoordinate(coord.x, coord.y), rect, null, this);
        }
        this.myBunnies = bunnies;
        System.out.println("Built Bunnies");

        ClsBear[] bears = new ClsBear[numberOfBears];
        walkCoord = this.GetCoordinates(eTerrain.WALKABLE);
        coord = null;
        boolean canBearReachUser = false;

        placementAttempts = 0;

        for (int i = 0; i < numberOfBears; i++) {
            do {
                coord = walkCoord[RandomNumber(walkCoord.length)];

                rect = new Rectangle2D.Double(PAD + coord.x * SQUARELEN, PAD + coord.y * SQUARELEN, SQUARELEN, SQUARELEN);
                bears[i] = new ClsBear(new ClsCoordinate(coord.x, coord.y), rect, null, this);
                ClsNavigator navigator = new ClsNavigator(this);
                canBearReachUser = navigator.IsRouteAvailable(this.myUserChar.GetCoord(), coord);

                placementAttempts++;
                if (placementAttempts > 1000) {
                    System.out.println("FAILED TO BEAR PLACE");
                }

            } while (!canBearReachUser || Point2D.distance(myUserChar.GetX(), myUserChar.GetY(), coord.x, coord.y) < BEAR_USER_SPAWN_DISTANCE);
            walkCoord = RemoveCoordFromArray(walkCoord, coord);
        }
        this.myBears = bears;
        System.out.println("Built Bears");
    }

    private void AddItems() {
        AddFlares(NUM_OF_FLARES);
        AddTraps(NUM_OF_BEAR_TRAPS);
    }

    private void AddFlares(int numberOfFlares) {
        double x = PAD + 7 * SQUARELEN;
        double y = PAD + 15 * SQUARELEN;
        Rectangle2D.Double rect = new Rectangle.Double(x, y, SQUARELEN, SQUARELEN);
        ClsFlare[] flares = new ClsFlare[numberOfFlares];
        ClsCoordinate[] walkCoord = this.GetCoordinates(eTerrain.WALKABLE);
        ClsCoordinate coord;
        for (int i = 0; i < numberOfFlares; i++) {
            boolean validFlareLocation;
            do {
                validFlareLocation = true;
                coord = walkCoord[RandomNumber(walkCoord.length)];

                if (myUserChar.GetCoord().Equals(coord)) {
                    validFlareLocation = false;
                }

                for (ClsHouse currHouse : myHouses) {
                    if (currHouse.getMyStairsCoord().Equals(coord)) {
                        validFlareLocation = false;
                    }
                }

            } while (!validFlareLocation);
            walkCoord = RemoveCoordFromArray(walkCoord, coord);
            rect = new Rectangle2D.Double(PAD + coord.x * SQUARELEN, PAD + coord.y * SQUARELEN, SQUARELEN, SQUARELEN);
            flares[i] = new ClsFlare(new ClsCoordinate(coord.x, coord.y), rect, null, this);

        }
        this.myFlares = flares;
    }

    private void AddTraps(int numberOfBearTraps) {
        double x = PAD + 7 * SQUARELEN;
        double y = PAD + 15 * SQUARELEN;
        Rectangle2D.Double rect; // = new Rectangle.Double(x, y, SQUARELEN, SQUARELEN);
        ClsBearTrap[] bearTraps = new ClsBearTrap[numberOfBearTraps];
        ClsCoordinate[] walkCoord = this.GetCoordinates(eTerrain.WALKABLE);
        ClsCoordinate coord;
        for (int i = 0; i < numberOfBearTraps; i++) {
            boolean validTrapLocation;
            do {
                validTrapLocation = true;
                coord = walkCoord[RandomNumber(walkCoord.length)];

                if (myUserChar.GetCoord().Equals(coord)) {
                    validTrapLocation = false;
                }

                for (ClsHouse currHouse : myHouses) {
                    if (currHouse.getMyStairsCoord().Equals(coord)) {
                        validTrapLocation = false;
                    }
                }
            } while (!validTrapLocation);
            walkCoord = RemoveCoordFromArray(walkCoord, coord);
            rect = new Rectangle2D.Double(PAD + coord.x * SQUARELEN, PAD + coord.y * SQUARELEN, SQUARELEN, SQUARELEN);
            bearTraps[i] = new ClsBearTrap(new ClsCoordinate(coord.x, coord.y), rect, null, this);

        }
        this.myBearTraps = bearTraps;
    }

    public void SetSquareType(ClsCoordinate coord, eTerrain type) {
        ClsSquare square = this.GetSquare(coord);
        square.SetType(type);
    }

    public void SetSquareType(ClsCoordinate coord, eTerrain type, eTileSet tileSet) {
        ClsSquare square = this.GetSquare(coord);
        square.SetType(type, tileSet);
    }

    public void SetSquareType(ClsCoordinate[] coords, eTerrain type) {
        for (ClsCoordinate coord : coords) {
            for (ClsSquare square : this.squares) {
                if (coord.Equals(square.GetCoord())) {
                    square.SetType(type);
                }
            }
        }
    }

    public void SetSquareType(ClsCoordinate[] coords, eTerrain type, eTileSet tileSet) {
        for (ClsCoordinate coord : coords) {
            for (ClsSquare square : this.squares) {
                if (coord.Equals(square.GetCoord())) {
                    square.SetType(type, tileSet);
                }
            }
        }
    }

    public void SetSquareImage(ClsSquare square, Graphics2D g2) {
        square.SetBmpFromType(false);
        square.Fill(g2);
        square.Draw(g2);
    }

    public void SetCharacterImage(ClsCharacter ch, Graphics2D g2) {
        ch.SetBmpFromType(false);
        ch.Fill(g2);
        ch.Draw(g2);
    }

    public ClsCoordinate[] GetCoordinates(eTerrain type) {
        ClsCoordinate[] coordsOfType = new ClsCoordinate[0];
        for (ClsSquare sqr : this.squares) {
            if (sqr.GetType().equals(type)) {
                coordsOfType = AddCoordFromArray(coordsOfType, new ClsCoordinate(sqr.GetX(), sqr.GetY()));
            }
        }
        return coordsOfType;
    }

    public ClsCoordinate[] GetCoordinates(eMapQuadrant quad) {
        ClsCoordinate[] coordsOfType = new ClsCoordinate[0];
        ClsCoordinate sqrCoord;
        int minX = 0;
        int maxX = NUMOFX - 1;
        int minY = 0;
        int maxY = NUMOFY - 1;

        if (quad.equals(eMapQuadrant.NW)) {
            maxX = maxX / 2;
            maxY = maxY / 2;
        } else if (quad.equals(eMapQuadrant.NE)) {
            minX = (maxX / 2) + 1;
            maxY = maxY / 2;
        } else if (quad.equals(eMapQuadrant.SW)) {
            maxX = maxX / 2;
            minY = (maxY / 2) + 1;
        } else if (quad.equals(eMapQuadrant.SE)) {
            minX = (maxX / 2) + 1;
            minY = (maxY / 2) + 1;
        }

        for (ClsSquare sqr : this.squares) {
            sqrCoord = sqr.GetCoord();
            if (sqrCoord.x >= minX && sqrCoord.x <= maxX && sqrCoord.y >= minY && sqrCoord.y <= maxY) {
                coordsOfType = AddCoordFromArray(coordsOfType, new ClsCoordinate(sqrCoord.x, sqrCoord.y));
            }
        }
        return coordsOfType;
    }

    public ClsSquare GetSquare(ClsCoordinate coord) {
        try {
            if (coord.x >= NUMOFX || coord.y >= NUMOFY || coord.x < 0 || coord.y < 0) {
                return null;
            }
            return this.squares[coord.x * NUMOFY + coord.y];
        } catch (Exception e) {
            return null;
        }
    }

    // <editor-fold desc="Building Terrain">
    public void BuildTerrain() {
        do {
            squares = new ClsSquare[0];
            InitSquares();
            myHouses = new ClsHouse[MAX_NUM_OF_HOUSES];
            myObstacles = new ClsObstacle[MAX_NUM_OF_OBSTACLES];
            myNavigatedCoords = new ClsCoordinate[0];
            myCorrectNavigationCoords = new ClsCoordinate[0];

            if (BUILD_RIVER) {
                this.myRiverCoordinates = ClsObstacle.BuildRiverCoordinates();
                SetSquareType(this.myRiverCoordinates, ClsGrid.eTerrain.RIVER);
            }
            System.out.println("Built River");

            SetHouseCoordinates();
            System.out.println("Built Houses");

            for (int i = 0; i < ClsGrid.MAX_NUM_OF_OBSTACLES; i++) {
                ClsCoordinate[] obstacleBlocked = AppendCoordArrays(GetCoordinates(eTerrain.RIVER), GetCoordinates(eTerrain.BLOCKED));

                for (ClsHouse house : this.myHouses) { //IF NOT NULL?
                    obstacleBlocked = AppendCoordArrays(obstacleBlocked, house.getMyInteriorCoords());
                    obstacleBlocked = AppendCoordArrays(obstacleBlocked, house.getDoorBlockingCoords());
                }

                this.myObstacles[i] = new ClsObstacle(obstacleBlocked, NUMOFX, NUMOFY);
                Random randNumberGenerator = new Random();
                int tileSetDecision = randNumberGenerator.nextInt(2);
                if (tileSetDecision == 0) {
                    SetSquareType(myObstacles[i].getMyObstacleCoords(), ClsGrid.eTerrain.BLOCKED, eTileSet.TREE);
                } else {
                    SetSquareType(myObstacles[i].getMyObstacleCoords(), ClsGrid.eTerrain.BLOCKED, eTileSet.ROCK);
                }
            }

            if (BUILD_RIVER) {
                BuildBridges(NUM_OF_BRIDGES);
            }

            //TODO: Check for no dead end bridges
        } while (!IsMapValid());
        System.out.println("Built Terrain");
    }

    private void BuildBridges(int numOfBridges) {
        //TODO: Need to make multiple Bridges

        //Devide River Coord in numOfBridges Segments
        for (int i = 0; i < numOfBridges; i++) {
            int leftSegmentBound = i * (NUMOFX / numOfBridges);
            int rightSegmentBound = (i + 1) * (NUMOFX / numOfBridges);
            ClsCoordinate[] currentSegment;
            if (i == numOfBridges - 1) {
                rightSegmentBound = NUMOFX;
            }
            currentSegment = GetRiverSegment(leftSegmentBound, rightSegmentBound);
            boolean bridgeBuilt = false;
            while (!bridgeBuilt) {
                int attemptedRiverCoordIndex = RandomNumber(currentSegment.length);
                ClsCoordinate attemptedRiverCoord = currentSegment[attemptedRiverCoordIndex];
                if (IsRiverCoordValidForBridge(attemptedRiverCoord)) {
                    for (ClsCoordinate riverCoord : currentSegment) {
                        if (riverCoord.x == attemptedRiverCoord.x) {
                            GetSquare(riverCoord).SetType(eTerrain.WALKABLE, eTileSet.BRIDGE);
                        }
                    }
                    bridgeBuilt = true;
                }
            }
        }
    }

    private ClsCoordinate[] GetRiverSegment(int leftSegmentBound, int rightSegmentBound) {
        ClsCoordinate[] returnSegment = new ClsCoordinate[0];
        for (ClsCoordinate currCoord : myRiverCoordinates) {
            if (currCoord.x >= leftSegmentBound && currCoord.x < rightSegmentBound) {
                returnSegment = AddCoordFromArray(returnSegment, currCoord);
            }
        }
        return returnSegment;
    }

    private boolean IsRiverCoordValidForBridge(ClsCoordinate attemptedRiverCoord) {
        //ABOVE RIVER
        ClsCoordinate aboveCoord = new ClsCoordinate(attemptedRiverCoord.x, attemptedRiverCoord.y + 1);
        ClsSquare aboveSquare = GetSquare(aboveCoord);
        while (aboveSquare.type == eTerrain.RIVER) { //keep going up until not in river
            aboveCoord = new ClsCoordinate(aboveCoord.x, aboveCoord.y + 1);
            aboveSquare = GetSquare(aboveCoord);
        }
        if (aboveSquare.type != eTerrain.WALKABLE) {
            return false;
        }
        // BELOW RIVER
        ClsCoordinate belowCoord = new ClsCoordinate(attemptedRiverCoord.x, attemptedRiverCoord.y - 1);
        ClsSquare belowSquare = GetSquare(belowCoord);
        while (belowSquare.type == eTerrain.RIVER) { //keep going up until not in river
            belowCoord = new ClsCoordinate(belowCoord.x, belowCoord.y - 1);
            belowSquare = GetSquare(belowCoord);
        }
        return belowSquare.type == eTerrain.WALKABLE;
    }

    public boolean CheckForCollision(ClsCoordinate coord, ClsCoordinate[] blockedCoords) {
        //blockedCoords is an optional argument and simply provides an extra array check
        boolean ret;
        //Check if coordinate is within grid bounds
        if ((coord.x > NUMOFX - 1) || (coord.y > NUMOFY - 1) || (coord.x < 0) || (coord.y < 0)) {
            return false;
        }

        //Check if coordinate is on unwalkable terrain
        for (ClsSquare square : this.squares) {
            if (coord.Equals(square.GetCoord())) {
                if ((square.type == eTerrain.RIVER) || (square.type == eTerrain.BLOCKED)) {
                    return false;
                }
            }
        }

        //Check the optional array
        if (blockedCoords != null && blockedCoords.length != 0) {
            {
                for (ClsCoordinate blCoord : blockedCoords) {
                    if (coord.Equals(blCoord)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    static int[] AddElement(int[] a, int e) {
        a = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }

    private void SetHouseCoordinates() {
        //This causes overlapping house
        // Need to add door blocking coords and walls to blocked coord
        // new ClsHouse(this.myRiverCoordinates + <HERE>, this.myNumOfX, this.myNumOfY)

        if (this.quadrantHouseSpawn == false) {
            for (int i = 0; i < this.myHouses.length; i++) {
                this.myHouses[i] = new ClsHouse(AppendCoordArrays(GetCoordinates(eTerrain.RIVER),
                        GetCoordinates(eTerrain.BLOCKED)), NUMOFX, NUMOFY, new ClsCoordinate(0, 0), new ClsCoordinate(NUMOFX, NUMOFY));
                SetSquareType(myHouses[i].getMyWallCoords(), ClsGrid.eTerrain.BLOCKED, eTileSet.HOUSE);
                SetSquareType(myHouses[i].getMyStairsCoord(), ClsGrid.eTerrain.WALKABLE, eTileSet.STAIRS);
            }
        } else {
            this.myHouses = new ClsHouse[4];
            ClsCoordinate[] riverCoords = AppendCoordArrays(GetCoordinates(eTerrain.RIVER),
                    GetCoordinates(eTerrain.BLOCKED));
            ClsCoordinate[] quadCoords;

            //NW house
            quadCoords = GetCoordinates(eMapQuadrant.NW);
            this.myHouses[0] = new ClsHouse(riverCoords, NUMOFX, NUMOFY, quadCoords[0], quadCoords[quadCoords.length - 1]);
            SetSquareType(myHouses[0].getMyWallCoords(), ClsGrid.eTerrain.BLOCKED, eTileSet.HOUSE);
            SetSquareType(myHouses[0].getMyStairsCoord(), ClsGrid.eTerrain.WALKABLE, eTileSet.STAIRS);

            //NE house
            quadCoords = GetCoordinates(eMapQuadrant.NE);
            this.myHouses[1] = new ClsHouse(riverCoords, NUMOFX, NUMOFY, quadCoords[0], quadCoords[quadCoords.length - 1]);
            SetSquareType(myHouses[1].getMyWallCoords(), ClsGrid.eTerrain.BLOCKED, eTileSet.HOUSE);
            SetSquareType(myHouses[1].getMyStairsCoord(), ClsGrid.eTerrain.WALKABLE, eTileSet.STAIRS);

            //SW house
            quadCoords = GetCoordinates(eMapQuadrant.SW);
            this.myHouses[2] = new ClsHouse(riverCoords, NUMOFX, NUMOFY, quadCoords[0], quadCoords[quadCoords.length - 1]);
            SetSquareType(myHouses[2].getMyWallCoords(), ClsGrid.eTerrain.BLOCKED, eTileSet.HOUSE);
            SetSquareType(myHouses[2].getMyStairsCoord(), ClsGrid.eTerrain.WALKABLE, eTileSet.STAIRS);

            //SE house
            quadCoords = GetCoordinates(eMapQuadrant.SE);
            this.myHouses[3] = new ClsHouse(riverCoords, NUMOFX, NUMOFY, quadCoords[0], quadCoords[quadCoords.length - 1]);
            SetSquareType(myHouses[3].getMyWallCoords(), ClsGrid.eTerrain.BLOCKED, eTileSet.HOUSE);
            SetSquareType(myHouses[3].getMyStairsCoord(), ClsGrid.eTerrain.WALKABLE, eTileSet.STAIRS);
        }
    }

    public boolean IsMapValid() {
        ClsNavigator navigator;

        ClsCoordinate[] checkedCoords = new ClsCoordinate[4]; //First add four corners

        checkedCoords[0] = new ClsCoordinate(0, 0);
        checkedCoords[1] = new ClsCoordinate(0, NUMOFY - 1); //SOUTHWEST
        checkedCoords[2] = new ClsCoordinate(NUMOFX - 1, 0); //NORTHEAST
        checkedCoords[3] = new ClsCoordinate(NUMOFX - 1, NUMOFY - 1);

        for (ClsHouse currHouse : this.myHouses) {
            checkedCoords = AppendCoordArrays(checkedCoords, currHouse.getMyInteriorCoords());
        }

        for (int i = 0; i < checkedCoords.length; i++) {
            for (int j = 0; j < checkedCoords.length; j++) {
                if (i != j) {
//                    myCorrectNavigationCoords = new ClsCoordinate[0];
//                    myNavigatedCoords = new ClsCoordinate[0];
                    navigator = new ClsNavigator(this);
                    if (!navigator.IsRouteAvailable(checkedCoords[i], checkedCoords[j])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    // </editor-fold>

    // <editor-fold desc="Movement Tasks">
    private void PrepareItemDropForNextMove() {
        if (!myIsItemActionAvailable) {
            return;
        }
        myIsItemActionAvailable = false;
        myUserChar.ShiftItemToBeUsed();
        if (CheckForItemsToPickUp()) {
            //Check if more than one option for item pickup
            if (myNumberOfPotentialPickupItems <= 1) {
                myUserChar.PickUpItem(-1, myPotentialPickups);
            }
        }

    }

    private void HandleMoveEvent(eDirection dir) {
        if (!HandleUserCharMoveAndItemDrop(dir)) {
            return;
        }
        CheckForStairs();
        MoveBunnies();
        MoveBears();
        CheckForTrappedBears();
        HandleItemsOnNewSquare();
        HandleLighting(dir);
        CheckForGameOver();
        CheckBunnyDeaths();
    }

    private void CheckForStairs() {
        if (MAX_NUM_OF_HOUSES > 1) {
            ClsCoordinate[] stairsCoords = new ClsCoordinate[0];
            for (ClsHouse house : this.myHouses) {
                stairsCoords = AddCoordFromArray(stairsCoords, house.getMyStairsCoord());
            }

            if (IsCoordinateInArray(this.myUserChar.GetCoord(), stairsCoords) > -1) {
                this.AlertBearsOfStairs(this.myUserChar.GetCoord());
                stairsCoords = RemoveCoordFromArray(stairsCoords, this.myUserChar.GetCoord());
                this.myUserChar.Move(stairsCoords[RandomNumber(stairsCoords.length)]);
            }

            for (ClsBear bear : this.myBears) {
                if (IsCoordinateInArray(bear.GetCoord(), stairsCoords) > -1) {
                    stairsCoords = RemoveCoordFromArray(stairsCoords, bear.GetCoord());
                    bear.Move(stairsCoords[RandomNumber(stairsCoords.length)]);
                    bear.ClearCoordLock();
                }
            }
        }
    }

    private boolean HandleUserCharMoveAndItemDrop(eDirection dir) {
        //Move the main character
        if (this.CheckForCollision(this.myUserChar.GetCoord().Move(dir), null)) {
            if (myUserChar.GetItemToBeUsed() != null) {
                myUserChar.UseItem();
            }
            this.myUserChar.Move(dir);
            return true;
        } else {
            return false;
        }
    }

    private void MoveBunnies() {
        ClsCoordinate NextMoveCoor;
        ClsCoordinate[] arrBunnyCoord = new ClsCoordinate[this.myBunnies.length];
        ClsCoordinate[] arrBearCoord = new ClsCoordinate[this.myBears.length];

        int i = 0;
        for (ClsBunny bunny : this.myBunnies) {      //Grab the current coordinates of bunnies
            arrBunnyCoord[i] = bunny.GetCoord();     //They are considered "blocked"
            i++;
        }

        i = 0;
        for (ClsBear bear : this.myBears) {      //Grab the current coordinates of bears
            arrBearCoord[i] = bear.GetCoord();     //They are considered "blocked"
            i++;
        }

        i = 0;
        for (ClsBunny bunny : this.myBunnies) {
            NextMoveCoor = bunny.NextMove();
            if (this.CheckForCollision(NextMoveCoor, arrBunnyCoord) && this.CheckForCollision(NextMoveCoor, arrBearCoord)) {
                bunny.Move(NextMoveCoor);
                arrBunnyCoord[i] = bunny.GetCoord();
            }
            i++;
        }
    }

    private void MoveBears() {
//        TEMP SAME AS BUNNIES FOR NOW

//        for (ClsBear currBear : this.myBears) {
        ClsCoordinate NextMoveCoord;
        ClsCoordinate[] blockedByBears = new ClsCoordinate[this.myBears.length];
        int i = 0;
        for (ClsBear bear : this.myBears) {      //Grab the current coordinates of bears
            blockedByBears[i] = bear.GetCoord();     //They are considered "blocked"
            i++;
        }
        i = 0;
        for (ClsBear bear : this.myBears) {
            if (!bear.GetCoord().Equals(myUserChar.GetCoord())) {
                NextMoveCoord = bear.NextMove();
                if (this.CheckForCollision(NextMoveCoord, blockedByBears)) {
                    bear.Move(NextMoveCoord);
                    blockedByBears[i] = bear.GetCoord();
                }
                i++;
            }
        }
//        }
    }

    private void AlertBearsOfStairs(ClsCoordinate stairs) {
        double distanceFromStairs;
        for (ClsBear bear : this.myBears) {
            distanceFromStairs = Point2D.distance(bear.GetX(), bear.GetY(), stairs.x, stairs.y);
            if (distanceFromStairs < bear.BEAR_STAIRS_LOCK_RADIUS) {
                bear.SetCoordLock(stairs);
            }
        }
    }

    private void HandleItemsOnNewSquare() {
        if (CheckForItemsToPickUp()) {
            //Check if more than one option for item pickup

            if (this.myUserChar.GetItem() == null && myNumberOfPotentialPickupItems <= 1) {
                myUserChar.PickUpItem(-1, myPotentialPickups);
//                ResetPotentialItems(); this neccessary?
            }
        }
        myIsItemActionAvailable = true;
    }

    private void CheckForTrappedBears() {
        for (ClsBearTrap currBearTrap : myBearTraps) {
            if (currBearTrap.GetHoldBearFor() == 1) {
                myBearTraps = RemoveSquareFromArray(myBearTraps, currBearTrap);
            }
            for (ClsBear currBear : myBears) {
                if (currBear.GetCoord().Equals(currBearTrap.GetCoord())) {
                    if (currBearTrap.GetHoldBearFor() > 1) {
                        currBear.SetIsStopped(true);
                        currBearTrap.UseOneTurn();
                    } else {
                        currBear.SetIsStopped(false);
                    }
                }
            }
        }
    }

    private boolean CheckForItemsToPickUp() {

        boolean retValue = false;

        ResetPotentialItems();

        myNumberOfPotentialPickupItems = 0;

        for (ClsBunny currBunny : myBunnies) {
            if (currBunny.GetCoord().Equals(myUserChar.GetCoord()) && !currBunny.IsDead() && currBunny.getMyFlare() == null) {
                myUserChar.SetPotentialBunny(currBunny);
                myPotentialPickups[BUNNY_INDEX] = true;
                myNumberOfPotentialPickupItems++;
                retValue = true;
            }
        }
        for (ClsFlare currFlare : myFlares) {
            if (currFlare.GetCoord().Equals(myUserChar.GetCoord()) && currFlare.GetCurrentLightTime() == -1) {
                myUserChar.SetPotentialFlare(currFlare);
                myPotentialPickups[FLARE_INDEX] = true;
                myNumberOfPotentialPickupItems++;
                retValue = true;
            }
        }
        for (ClsBearTrap currBearTrap : myBearTraps) {
            if (currBearTrap.GetCoord().Equals(myUserChar.GetCoord()) && currBearTrap.GetHoldBearFor() == -1) {
                myUserChar.SetPotentialBearTrap(currBearTrap);
                myPotentialPickups[BEARTRAP_INDEX] = true;
                myNumberOfPotentialPickupItems++;
                retValue = true;
            }
        }

        ExpandPotentialPickUpsWithHybrids();
//        System.out.println(Arrays.toString(myPotentialPickups));

        return retValue;
    }

    private void ResetPotentialItems() {
        myNumberOfPotentialPickupItems = 0;
        myPotentialPickups[BUNNY_INDEX] = false;
        myPotentialPickups[FLARE_INDEX] = false;
        myPotentialPickups[BEARTRAP_INDEX] = false;
        myPotentialPickups[BEARTRAP_BUNNY_INDEX] = false;
        myPotentialPickups[BUNNY_FLARE_INDEX] = false;
        myPotentialPickups[BEARTRAP_FLARE_INDEX] = false;
        myUserChar.SetPotentialBunny(null);
        myUserChar.SetPotentialBearTrap(null);
        myUserChar.SetPotentialFlare(null);
    }

    private void ExpandPotentialPickUpsWithHybrids() {
        boolean originalItemBool = false;
        int originalItemIndex = -1;

        if (myUserChar.GetItem() != null) {
            if (myUserChar.GetItem() instanceof ClsFlare) {
                originalItemBool = myPotentialPickups[FLARE_INDEX];
                originalItemIndex = FLARE_INDEX;
                myPotentialPickups[FLARE_INDEX] = true;
            } else if (myUserChar.GetItem() instanceof ClsBunny) {
                originalItemBool = myPotentialPickups[BUNNY_INDEX];
                originalItemIndex = BUNNY_INDEX;
                myPotentialPickups[BUNNY_INDEX] = true;
            } else if (myUserChar.GetItem() instanceof ClsBearTrap) {
                originalItemBool = myPotentialPickups[BEARTRAP_INDEX];
                originalItemIndex = BEARTRAP_INDEX;
                myPotentialPickups[BEARTRAP_INDEX] = true;
            }
        }

        if (myPotentialPickups[BUNNY_INDEX] && myPotentialPickups[FLARE_INDEX]) {
            myPotentialPickups[BUNNY_FLARE_INDEX] = true;
        }
        if (myPotentialPickups[FLARE_INDEX] && myPotentialPickups[BEARTRAP_INDEX]) {
            myPotentialPickups[BEARTRAP_FLARE_INDEX] = true;
        }
        if (myPotentialPickups[BUNNY_INDEX] && myPotentialPickups[BEARTRAP_INDEX]) {
            myPotentialPickups[BEARTRAP_BUNNY_INDEX] = true;
        }

        if (originalItemIndex != -1) {
            myPotentialPickups[originalItemIndex] = originalItemBool;
        }

    }

    private void HandleLighting(eDirection dir) {
        if (!dir.equals(eDirection.NONE)) {
            UpdateDarkness(DARKNESS_RADIUS, dir);
        }
        if (mylightsOn) {
            LightEntireMap();
        }
    }

    private void CheckForGameOver() {
        if (!IsUserDead()) {
            movesMade++;
//            System.out.println(movesMade);
        } else {
            if (DIFFICULTY == eDifficulty.EASY && movesMade > myEasyHighScore) {
                myEasyHighScore = movesMade;
            } else if (DIFFICULTY == eDifficulty.NORMAL && movesMade > myNormalHighScore) {
                myNormalHighScore = movesMade;
            } else if (DIFFICULTY == eDifficulty.HARD && movesMade > myHardHighScore) {
                myHardHighScore = movesMade;
            }
            System.out.println("EASY HIGH SCORE: " + myEasyHighScore);
            System.out.println("NORMAL HIGH SCORE: " + myNormalHighScore);
            System.out.println("HARD HIGH SCORE: " + myHardHighScore);
        }
    }

    private void CheckBunnyDeaths() {
        for (ClsBunny currBunny : this.myBunnies) {
            for (ClsBear currBear : this.myBears) {
                if (currBunny.GetCoord().Equals(currBear.GetCoord())) {
                    if (!currBunny.IsDead()) {
                        currBear.SetIsStopped(true);
                    }
                    KillBunny(currBunny);

                }
            }
        }
    }

    private void KillBunny(ClsBunny deadBunny) {

        deadBunny.IsDead(true);
        deadBunny.SetCharType(eCharacterImage.BUNNY_DEAD);
        deadBunny.SetBmpFromType(true);
//        ClsBunny[] newMyBunnies = new ClsBunny[myBunnies.length - 1];
//
//        int index = 0;
//
//        for (ClsBunny item : myBunnies) {
//            if (!deadBunny.equals(item)) {
//                newMyBunnies[index] = item;
//                index++;
//            }
//        }
//
//        myBunnies = newMyBunnies;

    }

    private boolean IsUserDead() {
        for (ClsBear currBear : this.myBears) {
            if (currBear.GetCoord().Equals(myUserChar.GetCoord())) {
                System.out.print("DEAD!");
                for (ClsSquare currSquare : this.squares) {
                    currSquare.SetDarknessType(eDarkness.FULL);
                }
                myUserChar.IsDead(true);
                playing = false;
                gameOver = true;
                return true;
            }
        }
        return false;
    }

    private void UpdateDarkness(int radius, eDirection dir) {
        int userX = myUserChar.GetX();
        int userY = myUserChar.GetY();
        int minX = userX - radius;
        int minY = userY - radius;
        int maxX = userX + radius;
        int maxY = userY + radius;
        ClsSquare cornerSqr1, cornerSqr2;

        if (dir == null) {
            dir = eDirection.NORTH;
        }

        int currX, currY;
        for (ClsSquare currSquare : this.squares) {
            currX = currSquare.GetX();
            currY = currSquare.GetY();
            if ((currX < minX) || (currY < minY) || (currX > maxX) || (currY > maxY)) {
                currSquare.SetDarknessType(eDarkness.FULL);
            } else if ((currX == minX && dir.equals(eDirection.WEST))
                    || (currY == minY && dir.equals(eDirection.NORTH))
                    || (currX == maxX && dir.equals(eDirection.EAST))
                    || (currY == maxY && dir.equals(eDirection.SOUTH))) {
                currSquare.SetDarknessType(eDarkness.NONE);
            } else if (currX == minX || currY == minY || currX == maxX || currY == maxY) {
                currSquare.SetDarknessType(eDarkness.DIM);
            } else {
                currSquare.SetDarknessType(eDarkness.NONE);
            }
        }

        if (dir == null || dir.equals(eDirection.NORTH)) {
            cornerSqr1 = this.GetSquare(new ClsCoordinate(minX, minY));
            cornerSqr2 = this.GetSquare(new ClsCoordinate(maxX, minY));
            if (cornerSqr1 != null) {
                cornerSqr1.SetDarknessType(eDarkness.SW);
            }
            if (cornerSqr2 != null) {
                cornerSqr2.SetDarknessType(eDarkness.SE);
            }
        } else if (dir.equals(eDirection.WEST)) {
            cornerSqr1 = this.GetSquare(new ClsCoordinate(minX, minY));
            cornerSqr2 = this.GetSquare(new ClsCoordinate(minX, maxY));
            if (cornerSqr1 != null) {
                cornerSqr1.SetDarknessType(eDarkness.NE);
            }
            if (cornerSqr2 != null) {
                cornerSqr2.SetDarknessType(eDarkness.SE);
            }
        } else if (dir.equals(eDirection.EAST)) {
            cornerSqr1 = this.GetSquare(new ClsCoordinate(maxX, minY));
            cornerSqr2 = this.GetSquare(new ClsCoordinate(maxX, maxY));
            if (cornerSqr1 != null) {
                cornerSqr1.SetDarknessType(eDarkness.NW);
            }
            if (cornerSqr2 != null) {
                cornerSqr2.SetDarknessType(eDarkness.SW);
            }
        } else if (dir.equals(eDirection.SOUTH)) {
            cornerSqr1 = this.GetSquare(new ClsCoordinate(minX, maxY));
            cornerSqr2 = this.GetSquare(new ClsCoordinate(maxX, maxY));
            if (cornerSqr1 != null) {
                cornerSqr1.SetDarknessType(eDarkness.NW);
            }
            if (cornerSqr2 != null) {
                cornerSqr2.SetDarknessType(eDarkness.NE);
            }
        }

        ShowFlareLights();

        CheckAndShowHouseLights();
    }

    private void ShowFlareLights() {
        ClsSquare squareToBeLit;
//        ClsFlare[] flaresNow = myFlares;
        for (ClsFlare flare : myFlares) {
            if (flare.GetCurrentLightTime() > 1) { //1 because this won't affect lighting until next turn
                for (int i = (flare.GetCoord().x - flare.GetLightRadius()); i <= (flare.GetCoord().x + flare.GetLightRadius()); i++) {
                    for (int j = (flare.GetCoord().y - flare.GetLightRadius()); j <= (flare.GetCoord().y + flare.GetLightRadius()); j++) {
                        squareToBeLit = GetSquare(new ClsCoordinate(i, j));
                        if (squareToBeLit != null) {
                            squareToBeLit.SetDarknessType(eDarkness.NONE);
                        }
                    }
                }
                flare.BurnOneTurn();
            } else if (flare.GetCurrentLightTime() == 1) {
                myFlares = RemoveSquareFromArray(myFlares, flare);
            }
        }
    }

    private void CheckAndShowHouseLights() {
        //Check if user in any House and Decrease All House Light Timers
        for (ClsHouse currHouse : myHouses) {
            currHouse.UseOneLightingTurn();
            ClsCoordinate[] currInteriorCoords = ClsHelperUtils.AddCoordFromArray(currHouse.getMyInteriorCoords(), currHouse.getMyDoorCoord());
            if (ClsHelperUtils.IsCoordinateInArray(myUserChar.GetCoord(), currInteriorCoords) != -1) {
                currHouse.ResetLightTime();
            }
            if (currHouse.GetLightTime() > -1) {
                for (ClsCoordinate currInteriorCoord : currHouse.GetAllHouseCoords()) {
                    ClsSquare currInteriorSquare = GetSquare(currInteriorCoord);
                    currInteriorSquare.SetDarknessType(eDarkness.NONE);
                }
            }
        }
        //Set Visibilities
    }

    private void LightEntireMap() {
        for (ClsSquare currSquare : this.squares) {
            currSquare.SetDarknessType(eDarkness.NONE);
        }
    }
    // </editor-fold>

    // <editor-fold desc="Gets and Sets">
    public ClsFlare[] GetFlares() {
        return myFlares;
    }

    public void SetFlares(ClsFlare[] myFlares) {
        this.myFlares = myFlares;
    }

    public void RemoveFlareFromGrid(ClsFlare flareToBeRemoved) {
        myFlares = RemoveSquareFromArray(myFlares, flareToBeRemoved);
    }

    public void AddFlareToGrid(ClsFlare flareToBeRemoved) {
        myFlares = AddSquareToArray(myFlares, flareToBeRemoved);
    }

    public ClsUserCharacter GetMyUserChar() {
        return myUserChar;
    }

    public ClsBear[] GetBears() {
        return myBears;
    }

    public ClsBunny[] GetBunnies() {
        return myBunnies;
    }

    public void SetBunnies(ClsBunny[] myBunnies) {
        this.myBunnies = myBunnies;
    }

    public void RemoveBunnyFromGrid(ClsBunny bunnyToBeRemoved) {
        myBunnies = RemoveSquareFromArray(myBunnies, bunnyToBeRemoved);
    }

    public void SetIsItemActionAvailable(boolean myItemActionAvailable) {
        this.myIsItemActionAvailable = myItemActionAvailable;
    }

    public ClsBearTrap[] GetBearTraps() {
        return myBearTraps;
    }

    public void SetBearTraps(ClsBearTrap[] myBearTraps) {
        this.myBearTraps = myBearTraps;
    }

    public ClsCoordinate[] GetAllStairsCoord() {
        ClsCoordinate[] stairsCoords = new ClsCoordinate[0];
        for (ClsHouse house : this.myHouses) {
            stairsCoords = AddCoordFromArray(stairsCoords, house.getMyStairsCoord());
        }
        return stairsCoords;
    }
    // </editor-fold>

    @Override
    public void keyPressed(KeyEvent e) {

        if (titleScreen == true) {
            if (e.getKeyCode() == KeyEvent.VK_NUMPAD1 || e.getKeyCode() == KeyEvent.VK_1) {
                titleScreen = false;
                playing = true;
                DIFFICULTY = eDifficulty.EASY;
                ResetGame();
            } else if (e.getKeyCode() == KeyEvent.VK_NUMPAD2 || e.getKeyCode() == KeyEvent.VK_2) {
                titleScreen = false;
                playing = true;
                DIFFICULTY = eDifficulty.NORMAL;
                ResetGame();
            } else if (e.getKeyCode() == KeyEvent.VK_NUMPAD3 || e.getKeyCode() == KeyEvent.VK_3) {
                titleScreen = false;
                playing = true;
                DIFFICULTY = eDifficulty.HARD;
                ResetGame();
            }
        } else if (playing == true) {
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                this.HandleMoveEvent(eDirection.EAST);
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                this.HandleMoveEvent(eDirection.WEST);
            } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                this.HandleMoveEvent(eDirection.NORTH);
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                this.HandleMoveEvent(eDirection.SOUTH);
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                this.PrepareItemDropForNextMove();
            } else if (e.getKeyCode() == KeyEvent.VK_NUMPAD1 || e.getKeyCode() == KeyEvent.VK_1) {
                if (myNumberOfPotentialPickupItems > 1) {
                    myUserChar.PickUpItem(BUNNY_INDEX, myPotentialPickups);
                }
            } else if (e.getKeyCode() == KeyEvent.VK_NUMPAD2 || e.getKeyCode() == KeyEvent.VK_2) {
                if (myNumberOfPotentialPickupItems > 1) {
                    myUserChar.PickUpItem(FLARE_INDEX, myPotentialPickups);
                }
            } else if (e.getKeyCode() == KeyEvent.VK_NUMPAD3 || e.getKeyCode() == KeyEvent.VK_3) {
                if (myNumberOfPotentialPickupItems > 1) {
                    myUserChar.PickUpItem(BEARTRAP_INDEX, myPotentialPickups);
                }
            } else if (e.getKeyCode() == KeyEvent.VK_NUMPAD4 || e.getKeyCode() == KeyEvent.VK_4) {
                if (myPotentialPickups[ClsGrid.BUNNY_FLARE_INDEX]) {
                    myUserChar.PickUpItem(BUNNY_FLARE_INDEX, myPotentialPickups);
                }
            } else if (e.getKeyCode() == KeyEvent.VK_NUMPAD5 || e.getKeyCode() == KeyEvent.VK_5) {
//                if (myPotentialPickups[ClsGrid.BEARTRAP_FLARE_INDEX]) {
//                    myUserChar.PickUpItem(BEARTRAP_FLARE_INDEX, myPotentialPickups);
//                }
            } else if (e.getKeyCode() == KeyEvent.VK_NUMPAD6 || e.getKeyCode() == KeyEvent.VK_6) {
//                if (myPotentialPickups[ClsGrid.BEARTRAP_BUNNY_INDEX]) {
//                    myUserChar.PickUpItem(BEARTRAP_BUNNY_INDEX, myPotentialPickups);
//                }
            } else if (e.getKeyCode() == KeyEvent.VK_L) {
                mylightsOn = !mylightsOn;
                UpdateDarkness(DARKNESS_RADIUS, null);
                if (mylightsOn) {
                    LightEntireMap();
                }

            } else if (e.getKeyCode() == KeyEvent.VK_R) {
                //Same code in IsUserDead() - Used for auto reset game.a
                for (ClsSquare currSquare : this.squares) {
                    currSquare.SetDarknessType(eDarkness.FULL);
                }
                myUserChar.IsDead(true);
                playing = false;
                gameOver = true;
            }

        } else if (gameOver == true) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                gameOver = false;
                titleScreen = true;
            }
        }

        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e
    ) {
    }

    @Override
    public void keyTyped(KeyEvent e
    ) {
    }
}
