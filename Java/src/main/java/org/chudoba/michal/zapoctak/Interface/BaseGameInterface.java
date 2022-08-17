package org.chudoba.michal.zapoctak.Interface;

/** Abstract game interface all Interfaces need to follow.
 *  Sits on top of Game and delegates the logic with players.
 *
 *  Should be used to provide the indirect communication between players and game logic.
 *  For this purpose it should use provided standard communication classes in the Util package.
 *  This in return will provide ease of implementation as AI and game logic follow this protocol.
 */
public abstract class BaseGameInterface {
    /** Interface just needs to play.
     *  Playing meaning, being responsible for all aspects by delegating the tasks.
     *
     */
    public abstract void play();
}
