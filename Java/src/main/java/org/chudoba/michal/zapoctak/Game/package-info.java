/**
 * Package with logistic of the game.
 * Ensures that the game plays in correct way.
 *
 * No further expansions are expected from this package.
 * For usage the most crucial are methods placeShips,
 * tryHittingOpponent, gameEnd, isWhoesTurn and canStart.
 *
 * placeShips is expecting proper placements of ships
 * to be stored into internal data keep.
 *
 * tryHittingOpponent is the base attack method
 * that does specific attack internally.
 *
 * gameEnd signals if the game has ended.
 *
 * isWhoesTurn signals whose turn it is
 * true is used for player1, false for player2.
 *
 * canStart signals if the boards are balanced
 * and the game can start.
 */
package org.chudoba.michal.zapoctak.Game;