package org.chudoba.michal.zapoctak.Util;

import java.io.Serializable;

/** Information about specific ship on playing board.
 * Used as a container for ship placement on playing board.
 *
 * Should be always linked with specific Fields on board,
 * on which part of this ship is present.
 *
 * Doesn't come with information on which board is this
 * ship located.
 *
 * Information provided:
 *      Type of ship this specific ship is.
 * The type is represented by a 2D array of booleans
 *      Location of the highest position of the ship on the board.
 * Here highest position is meant as displayed, actually the lowest position
 *      Location of leftmost position of the ship on the board.
 *      boolean flag if the ship is rotated to the vertical axis
 *
 *
 */
public class ShipPlacement implements Serializable {

    /** Type of ship represented by matrix with ship schema
     *
     */
    private boolean[][] shipType;
    /** y Axis position of the highest Field location
     *
     */
    private int i;
    /** x Axis position of the leftmost Field location
     *
     */
    private int j;
    /** if the ship schema is rotated to vertical axis
     *
     */
    private boolean side;

    /** Constructor
     *
     * @param shipType type of ship
     * @param xAxis leftmost position
     * @param yAxis highest position
     * @param orientationPlacement axis alignment
     */
    public ShipPlacement(boolean[][] shipType, int xAxis, int yAxis, boolean orientationPlacement){
        this.shipType = shipType;
        i = yAxis;
        j = xAxis;
        side = orientationPlacement;
    }

    /** Type of ship represented
     *
     * @return type of ship as matrix
     */
    public boolean[][] getShipType() {
        return shipType;
    }

    /** y Axis position of the highest Field location
     *
     * @return y Axis position
     */
    public int getI() {
        return i;
    }

    /** x Axis position of the most left Field location
     *
     * @return x Axis position
     */
    public int getJ() {
        return j;
    }

    /** Axis alignment of the ship schema
     *
     * @return axis alignment of the ship
     */
    public boolean isSide() {
        return side;
    }
}
