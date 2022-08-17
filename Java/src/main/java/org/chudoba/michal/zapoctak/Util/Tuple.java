package org.chudoba.michal.zapoctak.Util;


/** Tuple with 1 generic parameter.
 * Similar to (key, value) tuple pair, but with matching argument types.
 *
 * @param <K> Type of both variables on Tuple
 */
public class Tuple<K> {
    /** first value of the tuple
     *
     */
    public K first;
    /** second value of the tuple
     *
     */
    public K second;

    /** Constructor for the tuple
     *
     * @param first first value of the tuple
     * @param second second value of the tuple
     */
    public Tuple(K first, K second){
        this.first = first;
        this.second = second;
    }

}
