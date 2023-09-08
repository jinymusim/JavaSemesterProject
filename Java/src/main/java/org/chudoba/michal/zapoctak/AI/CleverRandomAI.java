package org.chudoba.michal.zapoctak.AI;

import org.chudoba.michal.zapoctak.Util.AIResponse;
import org.chudoba.michal.zapoctak.Util.Field;
import org.chudoba.michal.zapoctak.Util.ShipPlacement;
import org.chudoba.michal.zapoctak.Util.Tuple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/** AI using random numbers to play but can sense if hit happened
 *
 *  {@inheritDoc}
 */
public class CleverRandomAI extends BaseAI {


    /** Size of the board
     *
     */
    private int boardSize;

    /** Field of ai ships for placement
     *
     */
    private transient Field[][] opponentBoard;

    /** Initialize the attributes with base values
     *
     * @param boardSize Size of the playing board
     */
    public CleverRandomAI(int boardSize) {
        this.boardSize = boardSize;
        this.opponentBoard = new Field[boardSize][boardSize];
        for(var line: opponentBoard){
            for(int i=0; i<line.length; i++)
                line[i] = new Field();
        }
    }

    /** Look if position is proper and unhit
     *
     * @param yAxis
     * @param xAxis
     * @return if position in range and unhit
     */
    private boolean properPositionNoHit(int yAxis, int xAxis){
        return yAxis>=0 && yAxis<boardSize && xAxis>=0 && xAxis<boardSize && !opponentBoard[yAxis][xAxis].isAlreadyHit();
    }

    @Override
    public AIResponse aiMove() {
        List<Tuple<Integer>> neighborVariation = Arrays.asList(new Tuple<>(-1,0), new Tuple<>(1,0), new Tuple<>(0,1), new Tuple<>(0,-1));
        List<Tuple<Integer>> unsunkHits = new ArrayList<>();
        for(int i=0; i< opponentBoard.length; i++){
            for(int j=0; j<opponentBoard[i].length; j++){
                if(opponentBoard[i][j].isAlreadyHit() && opponentBoard[i][j].isShipPresent() && !opponentBoard[i][j].isShipSunk()){
                    unsunkHits.add(new Tuple<>(i,j));
                }
            }
        }
        for(var hit: unsunkHits){
            for(var neigh: neighborVariation){
                if (properPositionNoHit(hit.first + neigh.first, hit.second + neigh.second)){
                    return new AIResponse(hit.second + neigh.second, hit.first + neigh.first );
                }
            }
        }
        while(true){
            int xAxis = (int) (Math.random() * boardSize);
            int yAxis = (int) (Math.random() * boardSize);
            if(!opponentBoard[yAxis][xAxis].isAlreadyHit())
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
        for (var line : aiField) {
            for (int i = 0; i < line.length; i++)
                line[i] = new Field();
        }
        ShipPlacement[] placements = new ShipPlacement[ships.length];
        for (int i = 0; i < ships.length; i++) {
            var ship = ships[i];
            int xAxis = 0;
            int yAxis = 0;
            placementSuc:
            while (true) {
                boolean noOverlap = true;
                xAxis = (int) (Math.random() * (boardSize - ship[0].length));
                yAxis = (int) (Math.random() * (boardSize - ship.length));
                loopPlace:
                for (int j = 0; j < ship.length; j++) {
                    for (int k = 0; k < ship[j].length; k++) {
                        if (aiField[j + yAxis][k + xAxis].isShipPresent() && ship[j][k]) {
                            noOverlap = false;
                            break loopPlace;
                        }

                    }
                }
                if (noOverlap)
                    break placementSuc;
            }
            ShipPlacement succPlace = new ShipPlacement(ship, xAxis, yAxis, false);
            placements[i] = succPlace;
            for (int j = 0; j < ship.length; j++) {
                for (int k = 0; k < ship[j].length; k++) {
                    if (ship[j][k])
                        aiField[j + yAxis][k + xAxis] = new Field(true, false, succPlace);
                }
            }
        }

        return placements;
    }

    @Override
    public void processAIHit(int i, int j, boolean hit) {
        opponentBoard[i][j] = new Field(hit, true, null);
    }

    @Override
    public void processOpponentHit(int i, int j) { }

    @Override
    public void processSunkenShip(ShipPlacement ship) {
        if(ship.isSide()){
            for(int j=0; j< ship.getShipType().length; j++){
                for(int i=0; i<ship.getShipType()[j].length; i++) {
                    if(ship.getShipType()[j][i])
                        opponentBoard[ship.getI() +i][ship.getJ() + j].setShipSunk(true);
                }
            }
        }
        else {
            for (int i = 0; i < ship.getShipType().length; i++) {
                for (int j = 0; j < ship.getShipType()[i].length; j++) {
                    if (ship.getShipType()[i][j])
                        opponentBoard[ship.getI() + i][ship.getJ() + j].setShipSunk(true);
                }
            }
        }

    }

    @Override
    public void processOpponentField(Field[][] trueOpponentField) { }

    @Override
    public void prepareForNewGame() {
        opponentBoard = new Field[boardSize][boardSize];
        for(var line: opponentBoard){
            for(int i=0; i<line.length; i++){
                line[i] = new Field();
            }
        }
    }

    @Override
    public void loadFromFilename(String filename) throws IOException {
        throw new IOException("Not supported for this AI");
    }

    @Override
    public void writeToFilename(String filename) throws IOException { }
}
