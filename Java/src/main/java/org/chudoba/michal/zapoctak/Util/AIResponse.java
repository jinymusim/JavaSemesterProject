package org.chudoba.michal.zapoctak.Util;

/** Wrapper around AIs desired attack on opponent board.
 * Can be also used to wrap human players attack.
 *
 * Is used for the purpose of standard indirect communication between AI and logic.
 *
 * Comes only with information about the desired attack coordinates.
 *
 */
public class AIResponse {
    /** x Axis of Field location on enemy board to attack
     *
     */
    private int xAxis;
    /** y Axis of Field location on enemy board to attack
     *
     */
    private int yAxis;

    /** Constructor of the Attack
     *
     * @param xAxis x coordinate
     * @param yAxis y coordinate
     */
    public AIResponse(int xAxis, int yAxis){
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    /** get the x coordinate value
     *
     * @return x coordinate value
     */
    public int getxAxis() {
        return xAxis;
    }

    /** get the y coordinate value
     *
     * @return y coordinate value
     */
    public int getyAxis() {
        return yAxis;
    }

}
