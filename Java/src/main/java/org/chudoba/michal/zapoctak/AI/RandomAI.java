package org.chudoba.michal.zapoctak.AI;

import org.chudoba.michal.zapoctak.Util.AIResponse;
import org.chudoba.michal.zapoctak.Util.Field;
import org.chudoba.michal.zapoctak.Util.ShipPlacement;

import java.io.IOException;

/** AI using random numbers to play
 *
 *  {@inheritDoc}
 */
public class RandomAI extends BaseAI {

    /** Field of ai ships for placement
     *
     */
    transient private Field[][] opponentField;
    /** Size of the board
     *
     */
    private int boardSize;

    /** Initialize the attributes with base values
     *
     * @param boardSize Size of the playing board
     */
    public RandomAI(int boardSize){
        this.boardSize = boardSize;

        opponentField = new Field[boardSize][boardSize];
        for(var line: opponentField){
            for(int i=0; i<line.length; i++)
                line[i] = new Field();
        }
    }

    /** Use random number generator till place is found that was not hit
     *
     * @return Attack of place that was not hit
     */
    @Override
    public AIResponse aiMove() {
        while(true){
            int xAxis = (int) (Math.random() * boardSize);
            int yAxis = (int) (Math.random() * boardSize);
            if(!opponentField[yAxis][xAxis].isAlreadyHit())
                return new AIResponse(xAxis,yAxis);
        }

    }

    /** Use random number generator to found placements for ship
     *  That doesn't overlap
     *
     * @param ships ships to be placed
     * @return Successful placements of ship to be done by game logic
     */
    @Override
    public ShipPlacement[] placeShips(boolean[][][] ships) {
        Field[][] aiField = new Field[boardSize][boardSize];
        for(var line: aiField){
            for(int i=0; i<line.length; i++)
                line[i] = new Field();
        }
        ShipPlacement[] placements = new ShipPlacement[ships.length];
        for(int i=0; i<ships.length; i++){
            var ship = ships[i];
            int xAxis = 0;
            int yAxis = 0;
            placementSuc: while(true){
                boolean noOverlap = true;
                xAxis = (int) (Math.random() * (boardSize - ship[0].length));
                yAxis = (int) (Math.random() * (boardSize - ship.length));
                loopPlace: for(int j=0; j<ship.length; j++){
                    for(int k=0; k<ship[j].length; k++){
                        if(aiField[j + yAxis][k + xAxis].isShipPresent() && ship[j][k]){
                            noOverlap = false;
                            break loopPlace;
                        }

                    }
                }
                if(noOverlap)
                    break placementSuc;
            }
            ShipPlacement succPlace = new ShipPlacement(ship,xAxis,yAxis,false);
            placements[i] = succPlace;
            for(int j=0; j<ship.length; j++) {
                for (int k = 0; k < ship[j].length; k++) {
                    if (ship[j][k])
                        aiField[j+ yAxis][k + xAxis] = new Field(true,false,succPlace);
                }
            }
        }

        return placements;
    }

    @Override
    public void processOpponentHit(int i, int j) { }


    @Override
    public void processAIHit(int i, int j, boolean hit) {
        opponentField[i][j].hitPlace();
    }

    @Override
    public void processSunkenShip(ShipPlacement ship) { }

    @Override
    public void processOpponentField(Field[][] trueOpponentField) { }

    @Override
    public void prepareForNewGame(){
        opponentField = new Field[boardSize][boardSize];
        for(var line: opponentField){
            for(int i=0; i<line.length; i++)
                line[i] = new Field();
        }
    }

    @Override
    public void loadFromFilename(String filename) throws IOException {
        throw new IOException("Not supported");
    }

    @Override
    public void writeToFilename(String filename) throws IOException {}

}
