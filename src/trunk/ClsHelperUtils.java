package trunk;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import javax.imageio.ImageIO;
import static trunk.ClsGrid.*;

/**
 *
 * @author Matt
 */
public class ClsHelperUtils {

    private static boolean configLoaded = false;
    private static final Properties config = new Properties();

    // private static String myProjPath;
    // private static String bmpPath;
    private ClsHelperUtils() {
//    Nothing so that no instances can be created
    }

    //Load the config vars
    public static void LoadConfig() {
        // String myProjPathVar = GetConfigVar("myProjPathVar", "\\trunk\\Bitmaps");
        //myProjPath = new File("").getAbsolutePath().concat(myProjPathVar);
        // new File("").getPath();
        //bmpPath = myProjPath + GetConfigVar("bmpPathVar", "Bitmaps\\");
        if (GetConfigVar("debug", "0").equals("1")) {
            NUMOFX = Integer.parseInt(GetConfigVar("x", Integer.toString(NUMOFX)));
            NUMOFY = Integer.parseInt(GetConfigVar("y", Integer.toString(NUMOFY)));
            if (GetConfigVar("no_terrain", "false").equals("true")) {
                MAX_NUM_OF_HOUSES = 0;
                MAX_NUM_OF_OBSTACLES = 0;
            } else {
                MAX_NUM_OF_HOUSES = Integer.parseInt(GetConfigVar("houses", Integer.toString(MAX_NUM_OF_HOUSES)));
                MAX_NUM_OF_OBSTACLES = Integer.parseInt(GetConfigVar("obstacles", Integer.toString(MAX_NUM_OF_OBSTACLES)));
            }
            NUM_OF_BUNNIES = Integer.parseInt(GetConfigVar("bunnies", Integer.toString(NUM_OF_BUNNIES)));
            NUM_OF_BEARS = Integer.parseInt(GetConfigVar("bears", Integer.toString(NUM_OF_BEARS)));
            BUILD_RIVER = Boolean.parseBoolean(GetConfigVar("river", Boolean.toString(BUILD_RIVER)));
        } else {  //Load from the difficulty
            String prefix = "";
            if (DIFFICULTY.equals(eDifficulty.EASY)) {
                prefix = "easy_";
            } else if (DIFFICULTY.equals(eDifficulty.NORMAL)) {
                prefix = "norm_";
            } else if (DIFFICULTY.equals(eDifficulty.HARD)) {
                prefix = "hard_";
            }
            NUMOFX = Integer.parseInt(GetConfigVar(prefix.concat("x"), Integer.toString(NUMOFX)));
            NUMOFY = Integer.parseInt(GetConfigVar(prefix.concat("y"), Integer.toString(NUMOFY)));
            NUM_OF_BUNNIES = Integer.parseInt(GetConfigVar(prefix.concat("bunnies"), Integer.toString(NUM_OF_BUNNIES)));
            NUM_OF_BEARS = Integer.parseInt(GetConfigVar(prefix.concat("bears"), Integer.toString(NUM_OF_BEARS)));            
        }
    }

//    	public static <T> String join(T[] arr, ClsCoordinate sep) {
//		StringBuilder sb = new StringBuilder();
//
//		for (int i = 0; i < arr.length; i++) {
//			sb.append(arr[i]);
//			if (i + 1 != arr.length) {
//				sb.append(sep);
//			}
//		}
//
//		return sb.toString();
//	}
    public static ClsCoordinate[] RemoveCoordFromArray(ClsCoordinate[] input, ClsCoordinate deleteMe) {
//        List result = new LinkedList();
        
//        TEMP for debugging
        if (input.length < 1) {
            System.err.println("");
        }
        
        ClsCoordinate[] result = new ClsCoordinate[input.length - 1];
        int index = 0;

        for (ClsCoordinate item : input) {
            if (!deleteMe.Equals(item)) {
                result[index] = item;
                index++;
            }
        }

        return result;
    }

    public static ClsSquare[] RemoveSquareFromArray(ClsSquare[] input, ClsSquare deleteMe) {
        ClsSquare[] result = new ClsSquare[input.length - 1];
        int index = 0;

        for (ClsSquare item : input) {
            if (!deleteMe.equals(item)) {
                result[index] = item;
                index++;
            }
        }

        return result;
    }

    public static ClsBunny[] RemoveSquareFromArray(ClsBunny[] input, ClsBunny deleteMe) {
        ClsBunny[] result = new ClsBunny[input.length - 1];
        int index = 0;

        for (ClsBunny item : input) {
            if (!deleteMe.equals(item)) {
                result[index] = item;
                index++;
            }
        }

        return result;
    }

    public static ClsFlare[] RemoveSquareFromArray(ClsFlare[] input, ClsFlare deleteMe) {
        ClsFlare[] result = new ClsFlare[input.length - 1];
        int index = 0;

        for (ClsFlare item : input) {
            if (!deleteMe.equals(item)) {
                result[index] = item;
                index++;
            }
        }

        return result;
    }

    public static ClsBearTrap[] RemoveSquareFromArray(ClsBearTrap[] input, ClsBearTrap deleteMe) {
        ClsBearTrap[] result = new ClsBearTrap[input.length - 1];
        int index = 0;

        for (ClsBearTrap item : input) {
            if (!deleteMe.equals(item)) {
                result[index] = item;
                index++;
            }
        }

        return result;
    }

    public static ClsBunny[] AddSquareToArray(ClsBunny[] input, ClsBunny addMe) {
        ClsBunny[] result = new ClsBunny[input.length + 1];
        int index = 0;
        for (ClsBunny item : input) {
            result[index] = item;
            index++;
        }
        result[index] = addMe;
        return result;
    }

    public static ClsFlare[] AddSquareToArray(ClsFlare[] input, ClsFlare addMe) {
        ClsFlare[] result = new ClsFlare[input.length + 1];
        int index = 0;
        for (ClsFlare item : input) {
            result[index] = item;
            index++;
        }
        result[index] = addMe;
        return result;
    }

    public static ClsBearTrap[] AddSquareToArray(ClsBearTrap[] input, ClsBearTrap addMe) {
        ClsBearTrap[] result = new ClsBearTrap[input.length + 1];
        int index = 0;
        for (ClsBearTrap item : input) {
            result[index] = item;
            index++;
        }
        result[index] = addMe;
        return result;
    }
    
    public static ClsBunny FetchBunnyAtCoordinate(ClsBunny[] listOfBunnies, ClsCoordinate coord){
        for (ClsBunny currBunny : listOfBunnies){
            if (currBunny.GetCoord().Equals(coord)){
                return currBunny;
            }
        }
        return null;
    }

    public static ClsCoordinate[] AppendCoordArrays(ClsCoordinate[] input1, ClsCoordinate[] input2) {

        ClsCoordinate[] result = new ClsCoordinate[input1.length + input2.length];
        int index = 0;

        for (ClsCoordinate item : input1) {
            result[index] = item;
            index++;
        }
        for (ClsCoordinate item : input2) {
            result[index] = item;
            index++;
        }
        return result;
    }

    public static ClsCoordinate[] AddCoordFromArray(ClsCoordinate[] input, ClsCoordinate addMe) {
        ClsCoordinate[] result = new ClsCoordinate[input.length + 1];
        int index = 0;

        for (ClsCoordinate item : input) {
            result[index] = item;
            index++;
        }
        result[index] = addMe;

        return result;
    }

    public static int RandomNumber(int max) {
        Random randNumberGenerator = new Random();
        return randNumberGenerator.nextInt(max);
    }

    public static String GetConfigVar(String varName, String defaultVal) {
        try {
            if (!configLoaded) {
                config.load(new FileReader("config.txt"));
                configLoaded = true;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return config.getProperty(varName, defaultVal);
    }

    public static int IsCoordinateInArray(ClsCoordinate coord, ClsCoordinate[] array) {
        ClsCoordinate searchCoord;
        for (int i = 0; i < array.length; i++) {
            searchCoord = array[i];
            if (coord.Equals(searchCoord)) {
                return i;
            }
        }
        return -1;

    }

//    public static double DistanceBetweenCoordinates(ClsCoordinate coord1, ClsCoordinate coord2) {
//        int xDifference = coord1.x - coord2.x;
//        int yDifference = coord1.y - coord2.y;
//
//        return Math.sqrt(Math.pow(xDifference, 2) + Math.pow(yDifference, 2));
//        
//    }
}
