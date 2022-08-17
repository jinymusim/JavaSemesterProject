package org.chudoba.michal.zapoctak.Util;

import java.io.Serializable;

/** Inner representation of location on playing Board.
 * Field is used as an information hub about specific
 * location, without knowing the location itself.
 *
 * It should be used within bigger structure, like Array.
 *
 * Information provided:
 *      boolean if within this Field part of ship is present.
 *      boolean if this Field was hit by opponent player in game.
 *      ShipPlacement, what type of ship is within this Field.
 * If no ship is present, value is null
 *      boolean if ship within this Field has sunk whole,
 * meaning if all other Fields with same ship were also hit.
 *
 */
public class Field implements Serializable {

    /**Boolean if part of ship is present on this Field
     *
     */
    private boolean shipPresent;
    /**Boolean if this Field was hit by opponent
     *
     */
    private boolean alreadyHit;

    /** Ship this Field belongs to
     * null if no ship present
     *
     */
    private ShipPlacement belongingShip;

    /** If the whole ship is sunk
     *
     */
    private boolean shipSunk;

    /** Base constructor, use of attribute default value
     *
     */
    public Field(){}

    /** Assign Field with ship or hit (will not be used)
     *
     * @param shipPresent Part of a ship is in this field
     * @param alreadyHit Field was already hit
     * @param belongsTo The ship this field is part of
     */
    public Field(boolean shipPresent , boolean alreadyHit, ShipPlacement belongsTo){
        this.shipPresent = shipPresent;
        this.alreadyHit = alreadyHit;
        this.belongingShip = belongsTo;
    }

    /** Set if the whole ship is sunk
     *
     * @param sunk flag if ship is sunk
     */
    public void setShipSunk(boolean sunk){
        shipSunk = sunk;
    }

    /** Flag if the whole ship this Field belongs to is sunk
     *
     * @return flag if the whole ship is sunk
     */
    public boolean isShipSunk(){
        return shipSunk;
    }

    /** Change flag that the Field was hit
     *
     */
    public void hitPlace(){
        if(alreadyHit){
            throw new IllegalStateException("Already hit");
        }
        alreadyHit = true;
    }

    /** Get function on boolean if this Field was hit
     *
     * @return boolean if thsi Field was hit
     */
    public boolean isAlreadyHit(){
        return alreadyHit;
    }

    /** Get function on boolean if part of ship is on this Field
     *
     * @return boolean if part of ship is on this Field
     */
    public boolean isShipPresent(){
        return shipPresent;
    }

    /** Get the ship this Field belongs to
     *
     * @return ship this Field belongs to
     */
    public ShipPlacement getBelongingShip(){
        return belongingShip;
    }

    /** Outside view of the Field
     *
     * @return String representation of outside view on field
     */
    @Override
    public String toString() {
        if(shipPresent && alreadyHit && shipSunk)
            return "B";
        else if(shipPresent && alreadyHit)
            return "H";
        else if(alreadyHit)
            return "X";
        else
            return " ";
    }

    /** Inner view of the Field
     *
     * @return String representation of inner field state
     */
    public String toStringInner(){
        if(shipPresent && alreadyHit && shipSunk)
            return "B";
        else if(shipPresent && alreadyHit)
            return "H";
        else if(alreadyHit)
            return "X";
        else if(shipPresent)
            return "S";
        else
            return " ";
    }
}
