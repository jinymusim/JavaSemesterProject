package org.chudoba.michal.zapoctak;

import org.chudoba.michal.zapoctak.Interface.*;

/** Main class storing the main method.
 *
 */
public class Main {

    /** Main method, just creates interface that will deal with the rest
     *
     * @param args Not used
     */
    public static void main(String[] args){
        BaseGameInterface game = new SimpleGameInterface();
        game.play();
    }
}
