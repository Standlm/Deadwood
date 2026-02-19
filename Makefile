all: makeGame
makeGame: Deadwood.java
	javac Deadwood.java
clean:	
	rm BoardSpace.class Deadwood.class Role.class Scene.class GameBoard.class LoadXml.class Player.class Dice.class GameView.class ConsoleView.class

