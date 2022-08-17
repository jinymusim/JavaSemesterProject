package org.chudoba.michal.zapoctak.AI;

import org.chudoba.michal.zapoctak.Util.*;

import java.io.Serializable;

/** AI that plays the game in a place of human player.
 * Can be used as a replacement to any other players.
 *
 * Its communication with game logic should always be done through interface.
 * To ensure, that the interface implementation of AI integration is simple,
 * communication structures from Util package are used.
 *
 * All other AI implementations derived from BaseAI will follow this protocol.
 * All implementations shouldn't have any other public function that those declared in BaseAI.
 *
 */
public abstract class BaseAI implements Serializable {

    /** Move where the AI will attack
     *
     * @return Attack of the AI
     */
    public abstract AIResponse aiMove();

    /** Find placements for ship, to be placed by the game logic
     *
     * @param ships ships to be placed
     * @return Array of placements of ships
     */
    public abstract ShipPlacement[] placeShips(boolean[][][] ships);

    /** Process if there was a hit by AI
     *
     * @param i y Axis of the attack
     * @param j x Axis of the attack
     * @param hit flag if ship was hit
     */
    public abstract void processAIHit(int i, int j, boolean hit);

    /** Process the Attack from opponent
     *
     * @param i y Axis of the attack
     * @param j x Axis of the attack
     */
    public abstract void processOpponentHit(int i, int j);

    /** Process opponent sunken ship to better Attack
     *
     * @param ship the type and placement of the ship
     */
    public abstract void processSunkenShip(ShipPlacement ship);

    /** Process the opponent placement after game
     *
     * @param trueOpponentField Opponent board with true ship locations
     */
    public abstract void processOpponentField(Field[][] trueOpponentField);

    /** Method to be called before every individual game to reset
     * all field necessary to the game play
     *
     */
    public abstract void prepareForNewGame();

}
