all: makeGame
makeGame: Deadwood.java
	javac Deadwood.java
clean: Deadwood.class
	rm BoardSpace.class Deadwood.class Role.class

