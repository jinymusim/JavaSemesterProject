USER DOCUMENTATION OF THE PROGRAM

The program is a game of ships
to strat the program use command line
with command: 

	java -cp target/classes org.chudoba.michal.zapoctak.Main

After the game start there will be a prompt '>'
This always signals, that the player is in a menu
and not in the game itself

Typing help or nonsensical string displays the
help of the program with supported commands.

Supported commands are:

	help - displays help

	end - Ends the game 

	start - starts the game
	
	start first - changes the order of the turns

	load - loads basic AI from base file if present, 
	othrewise creates new AI

	load filename - loads AI from specific filename
	This AI needs to be a derivation from baseAI

	save - save the current AI into base file
	overvwriting all AI at that file

	save filename - saves the AI into specific file

	ai - generates new instance of basic AI

	ai random - generates new instance of random AI
	that only does random moves

Individual game of ships:
After command start is typed, the game itself starts.
First player is prompted to place their ships.
That is done in this fashion:

	Image how the ship looks like

	"Input y Axis level (from 0-9)"
	player is to input the y level of the highest point of the ship
	(by viewn meaning 0 is higher that 1)
	"Input x Axis level (from 0-9)"
	player is to input the x level of the leftest point of the ship
	"Should the ship be rotated? (y for yes, n for no)"
	player types y or n if the ship is to be rotated to it's vertical axis
	in a way that doesn't change the leftest and highest field

After that if player has turn
he is instructed to specify where he wan'ts to attack
	
	"Input y Axis coordinate of your strike (0-9)"
	player is to input y level of his attack
	"Input x Axis coordinate of your strike (0-9)"
	player is to input x level of his attack

After every attack boards are shown
with the player board being displayed fully.
In the board:

	'S' is a ship in board (visible only on player board)
	'H' is a hit ship
	'X' is a miss
	'B' is a sunked ship
	' ' unattacked place (on player board also without ship)

After someone sunks all opponent ships
the game ends and both boards are fully displayed.

Winner is also declared, with this the individual game ends
and player is sent back to menu (shown by the prompt '>')
	
