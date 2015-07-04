/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trunk;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import trunk.ClsGrid.eCharacterImage;
import trunk.ClsGrid.eDarkness;
import trunk.ClsGrid.eItemImage;
import trunk.ClsGrid.eTerrain;
import trunk.ClsGrid.eTileSet;

/**
 *
 * @author Kevin Ma
 */
public class ClsImageUtil {
    private static final Map<Object, BufferedImage> cache = new HashMap<>(); 
       
    private ClsImageUtil() {
         //Nothing so that no instances can be created         
    }
    
    public static BufferedImage GetImage(Object imgName) {       
        BufferedImage bmp = cache.get(imgName);
        if (bmp == null) {
            bmp = GetImageFromFile(imgName);
            cache.put(imgName, bmp);           
        }       
        return bmp;             
    }    
    
    private static BufferedImage GetImageFromFile(Object imgName) {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            if (imgName.equals(eTileSet.BRIDGE)) {
                return ImageIO.read(cl.getResource("trunk/Bitmaps/testBridge.png"));
            } else if (imgName.equals(eTerrain.WALKABLE)) {
                return ImageIO.read(cl.getResource("trunk/Bitmaps/testGrass.png"));
            } else if (imgName.equals(eTerrain.RIVER)) {
                return ImageIO.read(cl.getResource("trunk/Bitmaps/testRiver.png"));
            } else if (imgName.equals(eTileSet.HOUSE)) {
                return ImageIO.read(cl.getResource("trunk/Bitmaps/testHouse.png"));
            } else if (imgName.equals(eTileSet.STAIRS)) {
                return ImageIO.read(cl.getResource("trunk/Bitmaps/testStairs.png"));
            } else if (imgName.equals(eTileSet.ROCK)) {
                return ImageIO.read(cl.getResource("trunk/Bitmaps/testRock.png"));
            } else if (imgName.equals(eTileSet.TREE)) {
                return ImageIO.read(cl.getResource("trunk/Bitmaps/testTree.png"));
            } else if (imgName.equals(eCharacterImage.USER_L)) {
                return ImageIO.read(cl.getResource("trunk/Bitmaps/userCharIconLeft.png"));
            } else if (imgName.equals(eCharacterImage.USER_R)) {
                return ImageIO.read(cl.getResource("trunk/Bitmaps/userCharIconRight.png"));
            } else if (imgName.equals(eCharacterImage.USER_U)) {
                return ImageIO.read(cl.getResource("trunk/Bitmaps/userCharIconUp.png"));
            } else if (imgName.equals(eCharacterImage.USER_D)) {
                return ImageIO.read(cl.getResource("trunk/Bitmaps/userCharIconDown.png"));
            } else if (imgName.equals(eCharacterImage.BUNNY)) {
                return ImageIO.read(cl.getResource("trunk/Bitmaps/bunnyIcon.png"));
            } else if (imgName.equals(eCharacterImage.BUNNY_DEAD)) {
                return ImageIO.read(cl.getResource("trunk/Bitmaps/bunnyDeadIcon.png"));
            } else if (imgName.equals(eCharacterImage.BEAR)) {
                return ImageIO.read(cl.getResource("trunk/Bitmaps/bearIcon.png"));
            } else if (imgName.equals(eItemImage.FLARE)) {
                return ImageIO.read(cl.getResource("trunk/Bitmaps/flareIcon.png"));
            } else if (imgName.equals(eItemImage.BEARTRAP)) {
                return ImageIO.read(cl.getResource("trunk/Bitmaps/bearTrapIcon.png"));
            } else if (imgName.equals(eItemImage.BEARTRAP_BUNNY)) {
                return ImageIO.read(cl.getResource("trunk/Bitmaps/bearTrapBunnyIcon.png"));
            } else if (imgName.equals(eItemImage.BEARTRAP_FLARE)) {
                return ImageIO.read(cl.getResource("trunk/Bitmaps/bearTrapTorchIcon.png"));
            } else if (imgName.equals(eItemImage.BUNNY_FLARE)) {
                return ImageIO.read(cl.getResource("trunk/Bitmaps/bunnyTorchIcon.png"));
            } else if (imgName.equals(eDarkness.ALERT)) {
                return ImageIO.read(cl.getResource("trunk/Bitmaps/darknessAlert.png"));
            } else if (imgName.equals(eItemImage.BLANK) || imgName.equals(eCharacterImage.BLANK)) {
                return ImageIO.read(cl.getResource("trunk/Bitmaps/blankImage.png"));
            } else {
                return ImageIO.read(cl.getResource("trunk/Bitmaps/missing.png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
    

