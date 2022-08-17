package org.chudoba.michal.zapoctak.Util;

/** Wrapped board position for the purpose of Attack.
 * Comes with number, that in its usage in AI reflects confidance in the position.
 * Confidance is meant as a confidance that opponent ship is present in the specific place.
 *
 * Therefore, it resembles prior probability in the pick.
 *
 * It can be used as a tuple with extra added information in a for of a double.
 *
 */
public class AprioriProb {
    /** Coordinates of field as tuple
     *
     */
    public Tuple<Integer> coordinates;
    /** apriori prob of ship if only this field selectable
     *
     */
    public double prob;

    /** Field with apriori prob of the ship (Constructor)
     *
     * @param coordinates Field coordinates
     * @param prob apriori prob of ship
     */
    public AprioriProb(Tuple<Integer> coordinates, double prob){
        this.coordinates = coordinates;
        this.prob = prob;
    }
}
