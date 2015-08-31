/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trunk;

/**
 *
 * @author Kevin Ma
 */
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import trunk.ClsGrid.eDarkness;
import trunk.ClsGrid.eTerrain;
import trunk.ClsGrid.eTileSet;
import static trunk.ClsImageUtil.GetImage;


public class ClsSquare {

    private ClsCoordinate coord;
    private final String DisplayName; 
    Rectangle2D.Double rect;
    BufferedImage bmp;
    eTerrain type = eTerrain.WALKABLE;
    eTileSet tileType;
    eDarkness darkness = eDarkness.NONE;

    public ClsSquare(ClsCoordinate coord, Rectangle2D.Double rect, BufferedImage bmp, String displayName) {
        this.coord = coord;
        this.rect = rect;
        this.bmp = bmp;
        this.DisplayName = displayName;
    }

    public void Draw(Graphics2D g2) {
//        g2.setColor(Color.BLACK);
//        g2.draw(rect);
    }

    public void Fill(Graphics2D g2) {       
        if (bmp != null) {            
            g2.drawImage(bmp, this.GetPixelX(), this.GetPixelY(), null);
        }
    }
    
    public void Fill(Graphics2D g2, Object name) {
        BufferedImage tmpBmp = GetImage(name);
        if (tmpBmp != null) {
            g2.drawImage(tmpBmp, this.GetPixelX(), this.GetPixelY(), null);
        }
    }
    
    public void PaintDarkness(Graphics2D g2) {              
        if (darkness.equals(eDarkness.FULL)) {
            Rectangle2D.Double darkRect = new Rectangle2D.Double(this.GetPixelX(), this.GetPixelY(), ClsGrid.SQUARELEN, ClsGrid.SQUARELEN);           
            g2.setColor(Color.BLACK);
            g2.draw(darkRect);
            g2.fill(darkRect);
        } else if (darkness.equals(eDarkness.DIM)) {
            Rectangle2D.Double darkRect = new Rectangle2D.Double(this.GetPixelX(), this.GetPixelY(), ClsGrid.SQUARELEN, ClsGrid.SQUARELEN);
            Color veryGray = new Color(25,25,25);
            g2.setColor(veryGray);
            g2.draw(darkRect);
            g2.fill(darkRect);
        } else if (darkness.equals(eDarkness.NW)) {
            Path2D.Double triangle = new Path2D.Double();
            Color veryGray = new Color(25,25,25);
            triangle.moveTo(rect.x, rect.y + ClsGrid.SQUARELEN);                  
            triangle.lineTo(rect.x, rect.y);
            triangle.lineTo(rect.x + ClsGrid.SQUARELEN, rect.y);
            triangle.closePath();           
            g2.setColor(veryGray);
            g2.fill(triangle);                                                                               
        } else if (darkness.equals(eDarkness.NE)) {
            Path2D.Double triangle = new Path2D.Double();
            Color veryGray = new Color(25,25,25);
            triangle.moveTo(rect.x, rect.y);
            triangle.lineTo(rect.x + ClsGrid.SQUARELEN, rect.y);
            triangle.lineTo(rect.x + ClsGrid.SQUARELEN, rect.y + ClsGrid.SQUARELEN);
            triangle.closePath();
            g2.setColor(veryGray);
            g2.fill(triangle);
        } else if (darkness.equals(eDarkness.SW)) {
            Path2D.Double triangle = new Path2D.Double();
            Color veryGray = new Color(25,25,25);
            triangle.moveTo(rect.x, rect.y);
            triangle.lineTo(rect.x, rect.y + ClsGrid.SQUARELEN);
            triangle.lineTo(rect.x + ClsGrid.SQUARELEN, rect.y + ClsGrid.SQUARELEN);
            triangle.closePath();
            g2.setColor(veryGray);
            g2.fill(triangle);
        } else if (darkness.equals(eDarkness.SE)) {
            Path2D.Double triangle = new Path2D.Double();
            Color veryGray = new Color(25,25,25);
            triangle.moveTo(rect.x + ClsGrid.SQUARELEN, rect.y);
            triangle.lineTo(rect.x + ClsGrid.SQUARELEN, rect.y + ClsGrid.SQUARELEN);
            triangle.lineTo(rect.x, rect.y + ClsGrid.SQUARELEN);
            triangle.closePath();
            g2.setColor(veryGray);
            g2.fill(triangle);
        }
    }
    

    public void SetCoord(ClsCoordinate coord) {
        this.coord = coord;
        if (rect != null) {
            rect.x = this.GetPixelX();
            rect.y = this.GetPixelY();
        }
    }

    public void SetRect(Rectangle2D.Double rect) {
        this.rect = rect;
    }
    
    public void SetBmp(BufferedImage bmp) {
        this.bmp = bmp;
    }
       
    public void SetBmpFromType(boolean force) {
        //Only set bmp if it is null, otherwise do nothing if not forced
       // InputStream stream = this.getClass().getClassLoader().getResourceAsStream("/Bitmaps/bearIcon.png");
        if (force == false) {           
            if (this.bmp == null) {               
                if (this.tileType != null) {
                    //try {
                                            this.bmp = GetImage(this.tileType);
                        //this.bmp = ImageIO.read(stream);
                   // } catch (IOException ex) {
                   //     Logger.getLogger(ClsSquare.class.getName()).log(Level.SEVERE, null, ex);
                   // }
                } else {
                    this.bmp = GetImage(this.type);      
                }
            }                                      
        } else {                       
            if (this.tileType != null) {
                this.bmp = GetImage(this.tileType);
            } else {
                this.bmp = GetImage(this.type);
            }
        }       
    }

    public void SetType(eTerrain type) {
        this.type = type;
        this.tileType = null;
        this.SetBmpFromType(true);
    }

    public void SetType(eTerrain type, eTileSet tileType) {
        this.type = type;
        this.tileType = tileType;
        this.SetBmpFromType(true);
    }
    
    public void SetDarknessType(eDarkness darkType) {
        this.darkness = darkType;
    }
    
    public int GetX() {
        return this.coord.x;
    }
    
    public int GetPixelX() {
        return ClsGrid.PAD + this.GetX() * ClsGrid.SQUARELEN;
    }

    public int GetY() {
        return this.coord.y;
    }
    
    public int GetPixelY() {
        return ClsGrid.PAD + this.GetY() * ClsGrid.SQUARELEN;
    }

    public ClsCoordinate GetCoord() {
        return this.coord;
    }
    
    public BufferedImage GetBmp() {
        return this.bmp;
    }

    public eTerrain GetType() {
        return this.type;
    }
    
    public eDarkness GetDarknessType() {
        return this.darkness;
    }
    
    public eTileSet GetTileSet() {
        return this.tileType;
    }

    public String getDisplayName() {
        return DisplayName;
    }
}
