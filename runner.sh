javac -classpath out ./src/com/perguntas/interfaces/*.java -encoding UTF8 -d ./out
javac -classpath out ./src/com/perguntas/models/*.java -encoding UTF8 -d ./out
javac -classpath out ./src/com/perguntas/pcv/*.java -encoding UTF8 -d ./out
javac -classpath out ./src/com/perguntas/structures/*.java -encoding UTF8 -d ./out
javac -classpath out ./src/com/perguntas/crud/*.java -encoding UTF8 -d ./out
javac -classpath out ./src/com/perguntas/telas/*.java -encoding UTF8 -d ./out
javac -classpath out ./src/com/perguntas/Main.java -encoding UTF8 -d ./out

java -classpath out com.perguntas.Main