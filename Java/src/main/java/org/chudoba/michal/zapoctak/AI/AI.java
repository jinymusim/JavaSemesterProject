package org.chudoba.michal.zapoctak.AI;

import org.chudoba.michal.zapoctak.Util.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/** Class representing the inbuilt AI in game.
 * This AI works based on frequency table from previous games.
 *
 *  {@inheritDoc}
 */
public class AI extends BaseAI{

    /** lower limit for Random assignment to start the AI
     *
     */
    private static final long MIN_ASS_NUM = 5;
    /** upper limit for Random assignment to start the AI
     *
     */
    private static final long MAX_ASS_NUM = 30;
    /** Base max number that can be present in frequency
     *
     */
    private static final long ASSUMED_LIMIT = 1_000_000_000_000_000L;
    /** Frequency of hits from previous games
     *
     */
    private long[][] hitFrequency;
    /** Frequency of opponent attacks from previous games.
     *
     */
    private long[][] opponentHitsFrequency;
    /** Opponent board, tracking hits and misses.
     * Opponent board is used to derive attacks that stand from ship location.
     *
     */
    transient private Field[][] opponentBoard;
    /** Size of the board
     *
     */
    private int boardSize;

    /** Constructor, builds base objects base on given size.
     *
     * @param boardSize Size of the playing board
     */
    public AI(int boardSize){
        this.boardSize = boardSize;
        hitFrequency = new long[boardSize][boardSize];
        for(var arr : hitFrequency){
            for(int i=0; i< arr.length; i++)
                arr[i] = (long) (Math.random() * (MAX_ASS_NUM - MIN_ASS_NUM) + MIN_ASS_NUM);
        }
        opponentHitsFrequency = new long[boardSize][boardSize];
        for(var arr : opponentHitsFrequency){
            for(int i=0; i< arr.length; i++)
                arr[i] = (long) (Math.random() * (MAX_ASS_NUM - MIN_ASS_NUM) + MIN_ASS_NUM);
        }

        opponentBoard = new Field[boardSize][boardSize];
        for(var arr : opponentBoard){
            for(int i =0; i<arr.length; i++){
                arr[i] = new Field();
            }
        }
    }

    /** Increment the frequency base on opponent attacks
     *
     * @param i y Axis of the attack
     * @param j x Axis of the attack
     */
    private void incrementOpponentFrequency(int i, int j){
        if(i< 0 || i >= boardSize || j< 0 || j>= boardSize)
            throw new IllegalArgumentException("Out of range");
        opponentHitsFrequency[i][j] +=MIN_ASS_NUM;
    }

    /** Increment the frequency base on hits by the AI
     *
     * @param i y Axis of the hit
     * @param j x Axis of the hit
     */
    private void incrementAIFrequency(int i, int j){
        if(i< 0 || i >= boardSize || j< 0 || j>= boardSize)
            throw new IllegalArgumentException("Out of range");
        hitFrequency[i][j] +=MIN_ASS_NUM;
    }

    /** Get list of all hits of ships that aren't sunk yet.
     *
     * @return List of hits where ships aren't sunk
     */
    private List<Tuple<Integer>> unsunkHits(){
        List<Tuple<Integer>> places = new ArrayList<>();
        for(int i=0; i<boardSize; i++){
            for(int j=0; j<boardSize; j++){
                if(opponentBoard[i][j].isShipPresent() && !opponentBoard[i][j].isShipSunk())
                    places.add(new Tuple<>(i,j));
            }
        }
        return places;
    }


    /** Attack by the AI, using the frequency list and base knowledge of Ships
     *
     * @return Attack that will be performed by the game logic
     */
    @Override
    public AIResponse aiMove(){
        //List of hits where ship can be around
        var places = unsunkHits();
        //no such hits, try to find the best possible blind Attack
        if(places.size() == 0){
            //Find field with highest frequency that is not hit already
            long bestNum = 0;
            //start with random hit within board
            AIResponse bestResponce = new AIResponse((int)(Math.random() * boardSize),(int)(Math.random() * boardSize));
            for(int i=0; i<boardSize; i++){
                for(int j=0; j<boardSize; j++){
                    if(!opponentBoard[i][j].isAlreadyHit() && hitFrequency[i][j] > bestNum){
                        bestNum = hitFrequency[i][j];
                        bestResponce = new AIResponse(j,i);
                    }
                }
            }
            //return the best response
            return bestResponce;
        }
        //There are places to go from
        else{
            //List of neighboring Field locations
            List<Tuple<Integer>> neighborDir = new ArrayList<>();
            neighborDir.add(new Tuple<>(-1,0));
            neighborDir.add(new Tuple<>(1,0));
            neighborDir.add(new Tuple<>(0,-1));
            neighborDir.add(new Tuple<>(0,1));
            //Possible attack position with probability
            List<AprioriProb> allPositions = new ArrayList<>();
            //Go through neighbours and try to get the them
            for(var place : places) {
                //positions from this one Hit
                List<AprioriProb> positions = new ArrayList<>();
                //Possible line and the way of the line
                boolean noHitNeigh = true;
                Tuple<Integer> bestWay = null;
                //sum of priors (To normalize)
                int sumBest = 0;
                for (var neigh : neighborDir) {
                    //Neighbour not in range
                    if(place.first + neigh.first < 0 || place.first + neigh.first >= boardSize ||
                            place.second + neigh.second < 0 || place.second + neigh.second >= boardSize)
                        continue;
                    else{
                        //possible line segment, best way is the other way around from this neighbour
                        if(opponentBoard[place.first +neigh.first][place.second + neigh.second].isShipPresent() &&
                                !opponentBoard[place.first +neigh.first][place.second + neigh.second].isShipSunk()) {
                            noHitNeigh = false;
                            int i = (neigh.first == 0) ? 0 : -neigh.first;
                            int j = (neigh.second == 0) ? 0 : -neigh.second;
                            bestWay = new Tuple<>(i,j);

                        } //Place hit, no information more
                        else if(opponentBoard[place.first +neigh.first][place.second + neigh.second].isAlreadyHit()){ }
                        else{ //Free position neighbour
                            positions.add(new AprioriProb(new Tuple<>(place.first + neigh.first, place.second + neigh.second), 1));
                            sumBest++;
                        }
                    }
                }
                if(!noHitNeigh){ //There was a neighbour that determines line, fix prob.
                    for(var pp : positions){
                        if(pp.coordinates.first - place.first == bestWay.first &&
                        pp.coordinates.second - place.second == bestWay.second){
                            pp.prob +=4;
                            sumBest +=4;
                        }
                        else
                            pp.prob -=0.2;
                    }
                }
                for(var pp : positions) //fix prob normal way
                    pp.prob = pp.prob/sumBest;
                allPositions.addAll(positions);
            }
            //Find the Attack with best probability
            double bestProb = Double.MIN_VALUE;
            List<Tuple<Integer>> bestResponseList = new ArrayList<>();
            for(var pos : allPositions){
                if(pos.prob == bestProb)
                    bestResponseList.add(pos.coordinates);
                else if(pos.prob > bestProb){
                    bestProb = pos.prob;
                    bestResponseList.clear();
                    bestResponseList.add(pos.coordinates);
                }
            }
            if(bestResponseList.size() == 1) //just one with the highest prob. Use it
                return new AIResponse(bestResponseList.get(0).second, bestResponseList.get(0).first);
            else{ //More with the highest prob. Get the one with the highest frequency
                long bestPost = 0;
                //random hit within board, fail save for none in bestResponseList
                Tuple<Integer> bestCoor = new Tuple<Integer>((int)(Math.random() * boardSize),(int)(Math.random() * boardSize));
                for(var pos : bestResponseList) {
                    if(hitFrequency[pos.first][pos.second] > bestPost){
                        bestPost = hitFrequency[pos.first][pos.second];
                        bestCoor = pos;
                    }
                }
                return new AIResponse(bestCoor.second, bestCoor.first);
            }
        }
    }

    /** Process if there was a hit by AI
     *
     * @param i y Axis of the attack
     * @param j x Axis of the attack
     * @param hit flag if ship was hit
     */
    @Override
    public void processAIHit(int i, int j, boolean hit){
        if(hit)
            incrementAIFrequency(i,j);
        opponentBoard[i][j] = new Field(hit, true, null);
    }

    /** Increment frequency based on the opponent board after game
     *
     * @param trueOpponentField Opponent board with true ship locations
     */
    @Override
    public void processOpponentField(Field[][] trueOpponentField){
        for(int i=0; i<boardSize; i++){
            for(int j=0; j<boardSize; j++){
                if(trueOpponentField[i][j].isShipPresent() && !opponentBoard[i][j].isAlreadyHit())
                    incrementAIFrequency(i,j);
            }
        }
    }

    /** Process opponent sunken ship to better Attack
     *
     * @param ship the type and placement of the ship
     */
    @Override
    public void processSunkenShip(ShipPlacement ship){
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

    /** Process the Attack from opponent
     *
     * @param i y Axis of the attack
     * @param j x Axis of the attack
     */
    @Override
    public void processOpponentHit(int i, int j){
        incrementOpponentFrequency(i, j);
    }

    /** Find placements for ship, to be placed by the game logic
     *
     * @param ships ships to be placed
     * @return Array of placements of ships
     */
    @Override
    public ShipPlacement[] placeShips(boolean[][][] ships){
        Field[][] aiField = new Field[boardSize][boardSize];
        for(var arr : aiField){
            for(int i =0; i<arr.length; i++){
                arr[i] = new Field();
            }
        }

        ShipPlacement placements[] = new ShipPlacement[ships.length];
        for(int i=0; i<ships.length; i++){
            var ship = ships[i];
            long leastSum = Long.MAX_VALUE;
            ShipPlacement bestPlace = new ShipPlacement(ship, -1,-1,false);
            long sumArray[] = new long[ship[0].length];
            for(int j=0; j<boardSize; j++){
                Arrays.fill(sumArray, ASSUMED_LIMIT);
                for(int k=0; k<boardSize; k++){
                    if(aiField[j][k].isShipPresent())
                        sumArray[k % sumArray.length] = ASSUMED_LIMIT;
                    else
                        sumArray[k % sumArray.length] = opponentHitsFrequency[j][k];
                    if(Arrays.stream(sumArray).sum() < leastSum) {
                        bestPlace = new ShipPlacement(ship, k + 1 - sumArray.length, j, false);
                        leastSum = Arrays.stream(sumArray).sum();
                    }

                }
            }
            for(int k=0; k<boardSize; k++){
                Arrays.fill(sumArray,ASSUMED_LIMIT);
                for(int j=0; j<boardSize; j++){
                    if(aiField[j][k].isShipPresent())
                        sumArray[j % sumArray.length] = ASSUMED_LIMIT;
                    else
                        sumArray[j % sumArray.length] = opponentHitsFrequency[j][k];
                    if(Arrays.stream(sumArray).sum() < leastSum) {
                        bestPlace = new ShipPlacement(ship, k, j + 1 -sumArray.length, true);
                        leastSum = Arrays.stream(sumArray).sum();
                    }
                }
            }

            placements[i] = bestPlace;
            if(!bestPlace.isSide()) {
                for (int j = 0; j < ship.length; j++) {
                    for (int k = 0; k < ship[j].length; k++) {
                        aiField[j + bestPlace.getI()][k + bestPlace.getJ()] = new Field(ship[j][k], false, bestPlace);
                    }
                }
            }
            else {
                for (int k = 0; k < ship.length; k++) {
                    for (int j = 0; j < ship[k].length; j++) {
                        aiField[j + bestPlace.getI()][k + bestPlace.getJ()] = new Field(ship[k][j], false, bestPlace);
                    }
                }
            }
        }
        return placements;
    }

    @Override
    public void prepareForNewGame() {
        opponentBoard = new Field[boardSize][boardSize];
        for(var arr : opponentBoard){
            for(int i =0; i<arr.length; i++){
                arr[i] = new Field();
            }
        }
    }

    @Override
    public void loadFromFilename(String filename) throws IOException {
        try(BufferedReader inputs = new  BufferedReader( new FileReader(filename)) ){
            for(int i=0; i<this.hitFrequency.length; i++){
                for (int j=0; j<this.hitFrequency[i].length; j++){
                    this.hitFrequency[i][j] = Long.parseLong(inputs.readLine());
                }
            }
        }
        catch (IOException e){
            throw new IOException("Loading from file not successful");
        }
    }

    @Override
    public void writeToFilename(String filename) throws IOException {
        try(PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            for(int i=0; i<this.hitFrequency.length; i++) {
                for (int j = 0; j < this.hitFrequency[i].length; j++) {
                    out.println(this.hitFrequency[i][j]);
                }
            }
        } catch (IOException e){
            throw new IOException("Writing state file not successful");
        }
    }
}
