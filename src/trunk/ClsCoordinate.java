/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trunk;

//COMMITTEDST

/**
 *
 * @author Kevin Ma
 */
public class ClsCoordinate {
    public final int x;        //x-coordinate of the square
    public final int y;        //y-coordinate of the square

    public ClsCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean Equals(ClsCoordinate coord) {
        //Compares the coordinates and returns true if equal
        return ((this.x == coord.x) && (this.y == coord.y));
    }

    public ClsCoordinate Move(ClsGrid.eDirection dir) {
        if (dir == ClsGrid.eDirection.NORTH) {
            return new ClsCoordinate(x, y - 1);
        } else if (dir == ClsGrid.eDirection.WEST) {
            return new ClsCoordinate(x - 1, y);
        } else if (dir == ClsGrid.eDirection.EAST) {
            return new ClsCoordinate(x + 1, y);
        } else if (dir == ClsGrid.eDirection.SOUTH) {
            return new ClsCoordinate(x, y + 1);
        } else if (dir == ClsGrid.eDirection.RANDOM) {
            int randInt = ClsHelperUtils.RandomNumber(4);
            if (randInt == 0) {
                return this.Move(ClsGrid.eDirection.NORTH);
            } else if (randInt == 1) {
                return this.Move(ClsGrid.eDirection.WEST);
            } else if (randInt == 2) {
                return this.Move(ClsGrid.eDirection.EAST);
            } else if (randInt == 3) {
                return this.Move(ClsGrid.eDirection.SOUTH);
            }
        }
        return this;
    }
    }
