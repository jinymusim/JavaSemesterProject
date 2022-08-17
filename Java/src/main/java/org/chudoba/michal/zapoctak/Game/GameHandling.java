package org.chudoba.michal.zapoctak.Game;

import org.chudoba.michal.zapoctak.Util.*;

/** Class for game logistic.
 * Shouldn't be used by itself, there should always be interface sitting on top of it.
 * This interface should ensure, that players don't have direct communication with game logic.
 *
 * Provides all data it has if requested.
 * The proper usage and providing the data so the players may not cheat,
 * is left as a job for the interface.
 *
 */
public class GameHandling {

    /**Size of playing board
     *
     */
    public static final int BOARD_SIZE = 10;

    /** Type of ship present on the board
     *
     */
    static final boolean[][] longShip = new boolean[][] {{true, true, true, true, true}};
    /** Type of ship present on the board
     *
     */
    static final boolean[][] middleLongShip = new boolean[][]{{true,true,true,true}};
    /** Type of ship present on the board
     *
     */
    static final boolean[][] middleShip = new boolean[][]{{true, true, true}};
    /** Type of ship present on the board
     *
     */
    static final boolean[][] shortShip = new boolean[][]{{true,true}};

    /** Class representation of player board and associate data
     *
     */
    private static class PlayerData{
        /** Raw board with fields
         *
         */
        private Field[][] playerBoard;
        /** Number of field with ships
         *
         */
        private int shipPresentNum;
        /** Number of field with ships hit by opponent
         *
         */
        private int shipHitted;


        /** Construct base field with no ships using constant BOARD_SIZE for size
         *
         */
        PlayerData(){
            playerBoard = new Field[BOARD_SIZE][BOARD_SIZE];
            for(var line : playerBoard){
                for(int i =0; i<line.length; i++){
                    line[i] = new Field();
                }
            }
        }


        /** Add ship to the player board
         *
         * @param newShip ship placement
         */
        void addShip(ShipPlacement newShip){
            int baseXAxis = newShip.getJ();
            int baseYAxis = newShip.getI();
            int width = (newShip.isSide()) ? newShip.getShipType().length : newShip.getShipType()[0].length;
            int height = (newShip.isSide()) ? newShip.getShipType()[0].length : newShip.getShipType().length;
            if(baseXAxis < 0 || baseXAxis + width > BOARD_SIZE || baseYAxis < 0 || baseYAxis + height > BOARD_SIZE)
                throw new IllegalArgumentException("Ship placement outside of range of the board");

            if(newShip.isSide()){
                for(int j=0; j< newShip.getShipType().length; j++){
                    for(int i=0; i<newShip.getShipType()[j].length; i++){
                        if(newShip.getShipType()[j][i] && placeHasShip(baseYAxis+i,baseXAxis+j) )
                            throw new IllegalArgumentException("Multiple ships at one place");
                        if(newShip.getShipType()[j][i]) {
                            shipPresentNum++;
                            playerBoard[baseYAxis + i][baseXAxis + j] = new Field(true, false, newShip);
                        }
                    }
                }

            }
            else {
                for(int i=0; i<newShip.getShipType().length; i++){
                    for(int j=0; j<newShip.getShipType()[i].length; j++){
                        if(newShip.getShipType()[i][j] && placeHasShip(baseYAxis+i,baseXAxis+j) )
                            throw new IllegalArgumentException("Multiple ships at one place");
                        if(newShip.getShipType()[i][j]) {
                            shipPresentNum++;
                            playerBoard[baseYAxis + i][baseXAxis + j] = new Field(true, false, newShip);
                        }

                    }
                }
            }
        }


        /** Look if field specified by coordinates already hit
         *
         * @param i y axis position
         * @param j x axis position
         * @return boolean flag if field on coordinates hit
         */
        boolean placeAlreadyHit(int i, int j) {return playerBoard[i][j].isAlreadyHit(); }

        /** Look if Field location on board has ship present
         *
         * @param i y axis of specific Field location on board
         * @param j x axis of specific Field location on board
         * @return boolean flag if field has ship on it.
         */
        boolean placeHasShip(int i, int j) {return  playerBoard[i][j].isShipPresent();}

        /** Process if the ship present at given coordinates has sunk whole
         * 
         * @param yAxis y axis of hit Field location
         * @param xAxis x axis of hit Field location
         */
        void processSinking(int yAxis, int xAxis){
            var shipType = playerBoard[yAxis][xAxis].getBelongingShip();
            boolean allSunk = true;
            if(shipType.isSide()){
                whole: for(int j=0; j< shipType.getShipType().length; j++){
                    for(int i=0; i<shipType.getShipType()[j].length; i++) {
                        if(shipType.getShipType()[j][i] && !placeAlreadyHit(shipType.getI() + i, shipType.getJ() + j)) {
                            allSunk = false;
                            break whole;
                        }

                    }
                }
            }
            else{
                whole: for(int i=0; i<shipType.getShipType().length; i++) {
                    for (int j = 0; j < shipType.getShipType()[i].length; j++) {
                        if(shipType.getShipType()[i][j] && !placeAlreadyHit(shipType.getI() + i, shipType.getJ() + j)) {
                            allSunk = false;
                            break whole;
                        }

                    }
                }
            }
            if(allSunk){
                if(shipType.isSide()){
                    for(int j=0; j< shipType.getShipType().length; j++){
                        for(int i=0; i<shipType.getShipType()[j].length; i++) {
                            if(shipType.getShipType()[j][i])
                                playerBoard[shipType.getI() +i][shipType.getJ() + j].setShipSunk(true);
                        }
                    }
                }
                else{
                    for(int i=0; i<shipType.getShipType().length; i++) {
                        for (int j = 0; j < shipType.getShipType()[i].length; j++) {
                            if(shipType.getShipType()[i][j])
                                playerBoard[shipType.getI() +i][shipType.getJ() + j].setShipSunk(true);
                        }
                    }
                }
            }
        }

        /** if ship within Field at given location on board has sunk whole
         *
         * @param i y axis of specific Field location on board
         * @param j x axis of specific Field location on board
         * @return flag if ship within this field sunk
         */
        boolean isShipSunk(int i, int j){
            return playerBoard[i][j].isShipSunk();
        }

        /** Get the ship type of sunken ship
         *
         * @param i y axis of specific Field location on board
         * @param j x axis of specific Field location on board
         * @return Ship within Field location that has sunk
         */
        ShipPlacement getShipSunkType(int i, int j){
            return playerBoard[i][j].getBelongingShip();
        }

        /** Hit Field location on board specified by opponents coordinates
         *
         * @param i y axis of Field location to hit
         * @param j x axis of Field location to hit
         * @return boolean flag if new ship was hit
         */
        boolean hitField(int i, int j){
            if(placeAlreadyHit(i,j))
                return false;

            playerBoard[i][j].hitPlace();

            if(playerBoard[i][j].isShipPresent()){
                shipHitted +=1;
                processSinking(i,j);

            }
            return playerBoard[i][j].isShipPresent();
        }

        /** Number of ships present on board
         *
         * @return return the proper number of ships on board
         */
        int getShipPresentNum(){
            return shipPresentNum;
        }

        /** Number of ships hit by opponent
         *
         * @return return the proper number of ships hit by opponent
         */
        int getShipHitted(){return  shipHitted;}

        /** Display outside view on board
         *
         * @return String representing the board
         */
        @Override
        public String toString() {
            StringBuilder base = new StringBuilder();
            base.append("X0123456789\n");
            Integer i= Integer.valueOf(0);
            for(var line: playerBoard){
                base.append(i.toString());
                i++;
                for(var place : line){
                    base.append(place.toString());
                }
                base.append('\n');
            }
            return base.toString();
        }

        /** Display inner view on board
         *
         * @return String representing the inner state of board
         */
        public String toStringInner(){
            StringBuilder base = new StringBuilder();
            base.append("X0123456789\n");
            Integer i= Integer.valueOf(0);
            for(var line: playerBoard){
                base.append(i.toString());
                i++;
                for(var place : line){
                    base.append(place.toStringInner());
                }
                base.append('\n');
            }
            return base.toString();
        }

    }

    /** Player 1 board data
     *
     */
    private PlayerData player1;
    /** Player 2 board data
     *
     */
    private PlayerData player2;
    /** which player is currently on the move.
     *  true for player 1, false for player 2
     *
     */
    private boolean whoesTurn;

    /** Create blank boards
     *
     */
    public GameHandling(){
        player1 = new PlayerData();
        player2 = new PlayerData();
    }

    /** Signaling end of game when no ships left on 1 board
     *
     * @return flag if the game ended
     */
    public boolean gameEnd(){
        return player1.getShipPresentNum() == player1.getShipHitted() ||
                player2.getShipPresentNum() == player2.getShipHitted();
    }

    /** Signaling that game is balanced and can start
     *
     * @return flag if game balanced and can start
     */
    public boolean canStart(){
        return (player1.getShipPresentNum() == player2.getShipPresentNum() && player1.getShipPresentNum() > 0) ;
    }

    /** which player has move
     *
     * @return flag whoes turn it is
     */
    public boolean isWhoesTurn(){
        return whoesTurn;
    }

    /** Complete one turn of game by hitting Field on opponent board
     * specified by player.
     *
     * @param i y axis of Field location to hit
     * @param j x axis of Field location to hit
     * @param playerTurnToken token signaling from whom the command come
     * @return return flag if ship was hit
     * @throws IllegalArgumentException If arguments don't pass the game state throw exception
     */
    public boolean tryHittingOpponent(int i, int j, boolean playerTurnToken) throws IllegalArgumentException {
        if(gameEnd()){
            throw new IllegalArgumentException("Game already ended");
        }
        if(!canStart()){
            throw new IllegalArgumentException("Game didn't started");
        }
        if (playerTurnToken != whoesTurn) {
            throw new IllegalArgumentException("Wrong player turn");
        }
        if (i < 0 || i >= BOARD_SIZE || j < 0 || j >= BOARD_SIZE) {
            throw new IllegalArgumentException("Requested hit outside range");
        }
        PlayerData opposingPlayer = (whoesTurn) ? player2 : player1;
        if (opposingPlayer.placeAlreadyHit(i, j)) {
            throw new IllegalArgumentException("Place alredy hit");
        }
        boolean shipWasHit = opposingPlayer.hitField(i,j);
        whoesTurn = (shipWasHit) ? whoesTurn : !whoesTurn;
        return shipWasHit;
    }

    /** Flag if the ship within the Field location on board sunk
     *
     * @param i y axis of specific Field location on board
     * @param j x axis of specific Field location on board
     * @param playerTurnToken player which just did the hit
     * @return flag if the ship present at given Field has sunk
     */
    public boolean isShipSunk(int i, int j, boolean playerTurnToken){
        return (playerTurnToken) ? player2.isShipSunk(i,j) : player1.isShipSunk(i,j);
    }

    /** Get the ship if indeed sunk
     *
     * @param i y axis of specific Field location on board
     * @param j x axis of specific Field location on board
     * @param playerTurnToken player which just did the hit
     * @return the ship present at given Field if sunk
     */
    public ShipPlacement getSunkenShip(int i, int j, boolean playerTurnToken){
        if(isShipSunk(i,j,playerTurnToken))
            return  (playerTurnToken) ? player2.getShipSunkType(i,j) : player1.getShipSunkType(i,j);
        return null;
    }

    /** Which player won the game
     *
     * @return flag signaling which player won
     */
    public boolean whoWon(){
        return player1.getShipHitted() < player2.getShipHitted();
    }

    /** Get all the ships, that should be present on the board
     *
     * @return array with all the ships
     */
    public boolean[][][] getAllShips(){
        return new boolean[][][] {longShip, middleLongShip, middleShip, middleShip, shortShip};
    }

    /** Place ships on the board with respect to selected player
     *
     * @param player with respect to whom the ships are placed
     * @param ships Ship placement, type and orientation
     */
    public void placeShips(boolean player, ShipPlacement... ships){
        PlayerData placingPlayer = (player) ? player1 : player2;
        for(var shipPlace : ships) {
            placingPlayer.addShip(shipPlace);
        }
    }

    /** Given the game state during game
     *
     * @param intimateDataPlayer Which board is controlled by human player
     * @return String of the state of the game
     */
    public String toString(boolean intimateDataPlayer){
        StringBuilder base = new StringBuilder();
        base.append("player1\n");
        if(intimateDataPlayer)
            base.append(player1.toStringInner());
        else
            base.append(player1);

        base.append("\nplayer2\n");
        if(!intimateDataPlayer)
            base.append(player2.toStringInner());
        else
            base.append(player2);
        return base.toString();
    }

    /** Gives the end state of the game
     *
     * @return String of the end state of the game
     */
    @Override
    public String toString(){
        if(gameEnd()) {
            StringBuilder base = new StringBuilder();
            base.append("player1\n");
            base.append(player1.toStringInner());
            base.append("\nplayer2\n");
            base.append(player2.toStringInner());
            return base.toString();
        }
        else
            return "";
    }

    /** Resets the board of selected player
     *
     * @param playerToReset flag which player board to reset
     */
    public void resetBoard(boolean playerToReset){
        if(playerToReset)
            player1 = new PlayerData();
        else
            player2 = new PlayerData();
    }

    /** Give full data of player board to learn the AI
     *
     * @param playerToken which board is given
     * @return board with data about placement
     */
    public Field[][] giveFullData(boolean playerToken){
        return (playerToken) ? player1.playerBoard : player2.playerBoard;
    }

}
