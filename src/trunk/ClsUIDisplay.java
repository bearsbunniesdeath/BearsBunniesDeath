/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trunk;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 *
 * @author Matt
 */
public class ClsUIDisplay {

    public static final int PANEL_WIDTH_WITHOUT_PADDING = 230;
    public static final int TEXT_X_POSITION = 20;
    public static final int TEXT_Y_POSITION = 20;
    public static final int ITEM_X_POSITION = 75;
    public static final int ITEM_Y_POSITION = 250;
    public static final int DROPPED_ITEM_X_POSITION = 150;
    public static final int DROPPED_ITEM_Y_POSITION = 350;
    public static final int ITEM_1_X_POSITION = 80;
    public static final int ITEM_1_Y_POSITION = 500;
    public static final int ITEM_2_X_POSITION = 118;
    public static final int ITEM_2_Y_POSITION = 500;
    public static final int ITEM_3_X_POSITION = 156;
    public static final int ITEM_3_Y_POSITION = 500;
    public static final int SPACEBAR_PROMPT_X_POSITION = 60;
    public static final int SPACEBAR_PROMPT_Y_POSITION = 290;

    private Graphics myGraphic;
    private Integer myLeftPosition;
    private Integer myPanelLeftPosition;
    private Integer myBottomPosition;
    private Integer myFullWidth;
    private Integer myWindowHeight;
    private Integer myWidowWidth;
//    private Integer highScore = 0;
//    private Integer currentScore = 0;

    ClsUIDisplay(Integer leftPosition, Integer bottomPosition, Integer windowWidth, Integer windowHeight) {
        myLeftPosition = leftPosition;
        myBottomPosition = bottomPosition;
        myFullWidth = windowWidth - leftPosition;
        myPanelLeftPosition = leftPosition + (myFullWidth - PANEL_WIDTH_WITHOUT_PADDING) / 2;
        myWindowHeight = windowHeight;
    }

    //Paint the score
    public void PaintLoadingString(Graphics g, String str) {
        g.drawString(str, (myPanelLeftPosition / 2) - 50, (myWindowHeight / 2) - 35);
    }

    public void PaintGameOverScore(Graphics g, Integer movesMade, Integer highScore) {
//        g.fillRect(myFullWidth / 2, myBottomPosition/2 - 125, 350, 250);
        g.drawImage(ClsImageUtil.GetImage(ClsGrid.eUIImage.TRANSPARENT_BOX), myFullWidth / 2, myBottomPosition / 2 - 125, null);

        g.setColor(java.awt.Color.WHITE);
        g.setFont(new Font("TimesRoman", Font.BOLD, 18));

        g.drawString("Number of moves: " + Integer.toString(movesMade), myFullWidth / 2 + 50, myBottomPosition / 2 - 25);
        g.drawString("High score: " + Integer.toString(highScore), myFullWidth / 2 + 50, myBottomPosition / 2);

        g.drawString("Press space to restart.", myFullWidth / 2 + 50, myBottomPosition / 2 + 50);
    }

    public void DrawSideBar(Boolean[] potentialItems, Integer numberOfItemsAvailible, ClsSquare userItem, ClsSquare userItemToUse, Graphics g, Integer currentScore, Integer highScore, boolean itemOptionAvailable) {

        this.DrawTemplate(g);

        //Draw Scores
        g.setColor(Color.black);
        g.drawString("Number of Moves: " + Integer.toString(currentScore), myPanelLeftPosition + TEXT_X_POSITION, TEXT_Y_POSITION);
        g.drawString("High Score: " + Integer.toString(highScore), myPanelLeftPosition + TEXT_X_POSITION, TEXT_Y_POSITION + 25);

        //Paint Item info
        if (userItem != null) {
            g.drawImage(userItem.bmp, ITEM_X_POSITION + myPanelLeftPosition, ITEM_Y_POSITION, null);
        }

        if (itemOptionAvailable) {
            //Space Prompt
            if (userItem != null) {
                if (numberOfItemsAvailible < 1) {
                    g.drawString("Space to Drop " + userItem.getDisplayName(), myPanelLeftPosition + SPACEBAR_PROMPT_X_POSITION, SPACEBAR_PROMPT_Y_POSITION);
                } else {
                    g.drawString("Space to Swap " + userItem.getDisplayName(), myPanelLeftPosition + SPACEBAR_PROMPT_X_POSITION, SPACEBAR_PROMPT_Y_POSITION);
                }
            }
            //Draw Potential Items Images In "Air"
            if (potentialItems[ClsGrid.BUNNY_INDEX]) {
                g.drawImage(ClsImageUtil.GetImage(ClsGrid.eCharacterImage.BUNNY), ITEM_1_X_POSITION + myPanelLeftPosition, ITEM_1_Y_POSITION, null);
            }
            if (potentialItems[ClsGrid.FLARE_INDEX]) {
                g.drawImage(ClsImageUtil.GetImage(ClsGrid.eItemImage.FLARE), ITEM_2_X_POSITION + myPanelLeftPosition, ITEM_2_Y_POSITION, null);
            }
            if (potentialItems[ClsGrid.BEARTRAP_INDEX]) {
                g.drawImage(ClsImageUtil.GetImage(ClsGrid.eItemImage.BEARTRAP), ITEM_3_X_POSITION + myPanelLeftPosition, ITEM_3_Y_POSITION, null);
            }
            if (potentialItems[ClsGrid.BUNNY_FLARE_INDEX]) {
                g.drawImage(ClsImageUtil.GetImage(ClsGrid.eItemImage.BUNNY_FLARE), ITEM_1_X_POSITION + myPanelLeftPosition, ITEM_1_Y_POSITION + 40, null);
            }
            if (potentialItems[ClsGrid.BEARTRAP_FLARE_INDEX]) {
                g.drawImage(ClsImageUtil.GetImage(ClsGrid.eItemImage.BEARTRAP_FLARE), ITEM_2_X_POSITION + myPanelLeftPosition, ITEM_2_Y_POSITION + 40, null);
            }
            if (potentialItems[ClsGrid.BEARTRAP_BUNNY_INDEX]) {
                g.drawImage(ClsImageUtil.GetImage(ClsGrid.eItemImage.BEARTRAP_BUNNY), ITEM_3_X_POSITION + myPanelLeftPosition, ITEM_3_Y_POSITION + 40, null);
            }

            if (numberOfItemsAvailible > 1) {
                //Number Prompts
                g.drawString("1 for Bunny, 2 for Flare, 3 for Trap", myPanelLeftPosition + SPACEBAR_PROMPT_X_POSITION, SPACEBAR_PROMPT_Y_POSITION + 25);
            }

        } else {
            if (userItemToUse != null) {
                g.drawString("Going to Use on next move:" + userItemToUse.getDisplayName(), myPanelLeftPosition, 325);
                g.drawImage(userItemToUse.bmp, myPanelLeftPosition + DROPPED_ITEM_X_POSITION, DROPPED_ITEM_Y_POSITION, null);
            }
        }
    }

    private void DrawTemplate(Graphics g) {
        //Draw Full Sized Rectangle with Smaller Panel inside
        g.setColor(Color.DARK_GRAY);
        g.fillRect(myLeftPosition, 10, myFullWidth, myBottomPosition);
        g.setColor(Color.GRAY);
        g.fillRect(myPanelLeftPosition, 10, PANEL_WIDTH_WITHOUT_PADDING, myBottomPosition);
        g.setColor(Color.black);
        //TEMP: Will eventually use image as template
        g.drawRect(ITEM_X_POSITION + myPanelLeftPosition, ITEM_Y_POSITION, ClsGrid.SQUARELEN, ClsGrid.SQUARELEN);
        g.drawRect(ITEM_1_X_POSITION + myPanelLeftPosition, ITEM_1_Y_POSITION, ClsGrid.SQUARELEN, ClsGrid.SQUARELEN);
        g.drawRect(ITEM_2_X_POSITION + myPanelLeftPosition, ITEM_2_Y_POSITION, ClsGrid.SQUARELEN, ClsGrid.SQUARELEN);
        g.drawRect(ITEM_3_X_POSITION + myPanelLeftPosition, ITEM_3_Y_POSITION, ClsGrid.SQUARELEN, ClsGrid.SQUARELEN);
    }

    public void DrawRulers(Graphics g, int numOfX, int numOfY, int padding, int squareSize) {
        //Draw Indexes on Grid for easier debuging
        for (int i = 0; i < numOfX; i += 5) {
            g.drawString(Integer.toString(i), padding + (i * squareSize), 10);
        }
        for (int i = 0; i < numOfY; i += 5) {
            g.drawString(Integer.toString(i), 0, padding + (i * squareSize));
        }
    }

}
