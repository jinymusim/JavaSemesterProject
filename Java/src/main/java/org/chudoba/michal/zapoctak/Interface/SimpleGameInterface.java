package org.chudoba.michal.zapoctak.Interface;

import org.chudoba.michal.zapoctak.Game.*;
import org.chudoba.michal.zapoctak.AI.*;
import org.chudoba.michal.zapoctak.Util.AIResponse;
import org.chudoba.michal.zapoctak.Util.ShipPlacement;

import java.io.*;
import java.nio.file.Path;

/** Simple text based interface for game between human and AI.
 *
 * {@inheritDoc}
 */
public class SimpleGameInterface extends BaseGameInterface {

    /** Base location for loading and storing AI
     *
     */
    private final String aiStoreLoc = "base";
    /** Loaded AI to play against
     *
     */
    private BaseAI selectedAI;
    /** Game logic
     *
     */
    private GameHandling myGame;
    /** flag which player is human and which is AI
     *
     */
    private boolean playerBoard;

    /** Initial sequence of parameters before Games
     *
     */
    private void initialSequence() {
        try {
            if (!Path.of(aiStoreLoc).toFile().exists()) {
                Path.of(aiStoreLoc).toFile().createNewFile();
                saveAI();
            }
        }
        catch (IOException e){
            System.out.println(e);
        }
    }

    /** Constructor, creates base game and AI
     *
     */
    public SimpleGameInterface(){
        this.selectedAI = new AI(GameHandling.BOARD_SIZE);
        this.myGame = new GameHandling();
        initialSequence();
    }

    /** Save current AI to location
     *
     */
    private void saveAI(){
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(aiStoreLoc))){
            out.writeObject(selectedAI);
        }
        catch (IOException e){
            System.out.println("AI wasn't stored properly");
            System.out.println(e);
        }
    }

    /** Save AI to specific file
     *
     * @param filename filename to store the AI to
     */
    private void saveAI(String filename){
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))){
            out.writeObject(selectedAI);
        }
        catch (IOException e){
            System.out.println("AI wasn't stored properly");
        }
    }

    /** Load AI from location, if load fails, create new AI
     *
     */
    private void readStoredAI(){
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(aiStoreLoc))){
            selectedAI = (BaseAI) in.readObject();
        }
        catch (IOException | ClassNotFoundException e){
            System.out.println("AI wasn't loaded, new AI is generated");
            selectedAI = new AI(GameHandling.BOARD_SIZE);
        }
    }

    /** Load AI from specific file, if it fails, create new AI
     *
     * @param filename filename with the AI
     */
    private void readStoredAI(String filename){
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))){
            selectedAI = (BaseAI) in.readObject();
        }
        catch (IOException | ClassNotFoundException e){
            System.out.println("AI wasn't loaded, new AI is generated");
            selectedAI = new AI(GameHandling.BOARD_SIZE);
        }
    }


    /** Print the help with supported commands
     *
     */
    private void printHelp(){
        System.out.println("Supported commands are 'start' to start the game, 'load' to load AI, 'save' to store AI");
        System.out.println("'end' to end the game, 'ai' to generate new AI");
        System.out.println();
        System.out.println("'start' with parameter first changes the order of AI and player turn");
        System.out.println();
        System.out.println("'load' with filename after loads AI from file, otherwise it's loaded");
        System.out.println("from arbitrary file base");
        System.out.println();
        System.out.println("'save' with filename stores the AI into that file, otherwise it's stored");
        System.out.println("into arbitrary file base");
        System.out.println();
        System.out.println("'ai' generates new normal AI, if stated by random, the generated ai will be random variant");
    }

    /** Play the game with menu and all
     *
     */
    @Override
    public void play() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Hi welcome to the game, type help to get more info");
            game:
            while (true) {
                System.out.print("> ");
                String line = in.readLine();

                String[] commandWithPar = line.strip().split("\\s+");
                String command = commandWithPar[0];

                switch (command) {
                    case "help": {
                        printHelp();
                        break;
                    }
                    case "end": {
                        break game;
                    }
                    case "start": {
                        if(commandWithPar.length > 1){
                            if(commandWithPar[1].equals("first"))
                                playerBoard = true;
                        }
                        selectedAI.prepareForNewGame();
                        individualGame(in);
                        playerBoard = false;
                        break;
                    }
                    case "load": {
                        if (commandWithPar.length > 1)
                            readStoredAI(commandWithPar[1]);
                        else
                            readStoredAI();
                        break;
                    }
                    case "save":{
                        if(commandWithPar.length > 1)
                            saveAI(commandWithPar[1]);
                        else
                            saveAI();
                        break;
                    }
                    case "ai":{
                        if(commandWithPar.length>1){
                            if(commandWithPar[1].equals("random"))
                                selectedAI = new RandomAI(GameHandling.BOARD_SIZE);
                        }
                        else
                            selectedAI = new AI(GameHandling.BOARD_SIZE);
                        break;
                    }
                    default: {
                        System.out.println("Command: " + command + " isn't recognized, initializing help");
                        printHelp();
                    }
                }
            }
        }
        catch (IOException e){
            System.out.println(e);
        }
    }

    /** Display ship, to be placed by player
     *
     * @param ship Type of ship to be placed
     */
    private void displayShip(boolean[][] ship){
        StringBuilder base = new StringBuilder();
        for(var line: ship){
            for(int i=0; i<line.length; i++){
                if(line[i])
                    base.append("X");
                else
                    base.append(" ");
            }
            base.append('\n');
        }
        System.out.println(base);
    }

    /** Human input for placing ships on board
     *
     * @param in reader for human input
     * @throws IOException possible input exception
     */
    private void setShipsOnBoard(BufferedReader in) throws IOException{
        var ships = myGame.getAllShips();
            for (var ship : ships) {
                boolean result = false;
                while (!result) {
                    try {
                        System.out.println(myGame.toString(playerBoard));
                        System.out.println("Ship to be placed");
                        displayShip(ship);
                        if (playerBoard)
                            System.out.println("You are player1");
                        else
                            System.out.println("You are player2");
                        System.out.println("Input y Axis level (from 0-9)");
                        String i = in.readLine();
                        int yAxis = Integer.parseInt(i);
                        System.out.println("Input x Axis level (from 0-9)");
                        i = in.readLine();
                        int xAxis = Integer.parseInt(i);
                        System.out.println("Should the ship be rotated? (y for yes, n for no)");
                        i = in.readLine();
                        boolean rotate = i.equals("y");
                        myGame.placeShips(playerBoard, new ShipPlacement(ship, xAxis, yAxis, rotate));
                        result = true;
                    } catch (NumberFormatException e){
                        System.out.println("Inputted position not in correct format");
                    } catch (IllegalArgumentException e) {
                        System.out.println("During placement, collision with different ship occurred, resetting all placements");
                        return;
                    }
                }
            }
        var aiShipPlace = selectedAI.placeShips(ships);
        try{
            myGame.placeShips(!playerBoard, aiShipPlace);
        }
        catch (IllegalArgumentException e){
            System.out.println("AI error, tried to place illegally");
        }


    }

    /** Get the desired move from human player
     *
     * @param in std in reader
     * @return Response in format AIResponse
     * @throws IOException possible input exception occurring
     */
    private AIResponse humanPlayerMove(BufferedReader in) throws IOException{
        int yAxis = -1;
        int xAxis = -1;
        boolean result = false;
        while(!result) {
            try {
                System.out.println("Input y Axis coordinate of your strike (0-9)");
                yAxis = Integer.parseInt(in.readLine());
                System.out.println("Input x Axis coordinate of your strike (0-9)");
                xAxis = Integer.parseInt(in.readLine());
                if (yAxis != -1 && xAxis != -1)
                    result = true;
            } catch (NumberFormatException e) {
                System.out.println("Inputted numbers where not correct");
            }
        }
        return new AIResponse(xAxis, yAxis);
    }

    /** Play one game of ships
     *
     * @param in reader for human player input
     * @throws IOException possible input exception
     */
    private void individualGame(BufferedReader in) throws IOException{
        do{
            myGame.resetBoard(playerBoard);
            myGame.resetBoard(!playerBoard);
            setShipsOnBoard(in);
        } while(!myGame.canStart());
        while(!myGame.gameEnd()){
            System.out.println(myGame.toString(playerBoard));
            boolean turn = myGame.isWhoesTurn();

            if(playerBoard != turn){
                AIResponse response = selectedAI.aiMove();
                try {
                    boolean resultingHit = myGame.tryHittingOpponent(response.getyAxis(), response.getxAxis(), !playerBoard);
                    selectedAI.processAIHit(response.getyAxis(), response.getxAxis(), resultingHit);
                    if (resultingHit && myGame.isShipSunk(response.getyAxis(), response.getxAxis(), !playerBoard))
                        selectedAI.processSunkenShip(myGame.getSunkenShip(response.getyAxis(), response.getxAxis(), !playerBoard));
                    if (resultingHit)
                        System.out.println("Hit!");
                }
                catch (IllegalArgumentException e){
                    System.out.println(e.getMessage());
                }
            }
            else{

               AIResponse response =  humanPlayerMove(in);
               try {
                   boolean resultingHit = myGame.tryHittingOpponent(response.getyAxis(), response.getxAxis(), playerBoard);
                   if (resultingHit)
                       System.out.println("Hit!");
                   selectedAI.processOpponentHit(response.getyAxis(), response.getxAxis());
               }
               catch (IllegalArgumentException e){
                   System.out.println(e.getMessage());
               }
            }

        }
        selectedAI.processOpponentField(myGame.giveFullData(playerBoard));
        System.out.println(myGame);
        String winningString = (myGame.whoWon()) ? "Player1 won" : "Player2 won";
        System.out.println(winningString);
    }
}
