all: makeGame

makeGame: Deadwood.java SceneCardCatalog.java
	javac Deadwood.java SceneCardCatalog.java

run:
	java Deadwood --gui

clean:
	rm -f *.class


