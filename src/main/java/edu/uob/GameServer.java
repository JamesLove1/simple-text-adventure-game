package edu.uob;

import edu.uob.actions.LoadActions;
import edu.uob.commands.CommandInterpreter;
import edu.uob.commands.Tokeniser;
import edu.uob.gameMap.GameMap;
import edu.uob.gameMap.LoadDOTFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Arrays;

public final class GameServer {

    private static final char END_OF_TRANSMISSION = 4;

    private CommandInterpreter commandInterp;

    public static void main(String[] args) throws IOException {
        //James Love - Added ("cw-stag"+ File.separator +) remove at a latter date
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get( "config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();

        //test map
//        File entitiesFile = Paths.get("cw-stag"+ File.separator +"config" + File.separator + "james-entities.dot").toAbsolutePath().toFile();
//        File actionsFile = Paths.get("cw-stag"+ File.separator +"config" + File.separator + "james-actions.xml").toAbsolutePath().toFile();

        GameServer server = new GameServer(entitiesFile, actionsFile);
        server.blockingListenOn(8888);
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Instanciates a new server instance, specifying a game with some configuration files
    *
    * @param entitiesFile The game configuration file containing all game entities to use in your game
    * @param actionsFile The game configuration file containing all game actions to use in your game
    */
    public GameServer(File entitiesFile, File actionsFile) {
        // TODO implement your server logic here
        GameMap gameMap = new GameMap(entitiesFile);
        LoadActions gameActions = new LoadActions(actionsFile);
        this.commandInterp = new CommandInterpreter(gameMap,gameActions);

    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * This method handles all incoming game commands and carries out the corresponding actions.</p>
    *
    * @param command The incoming command to be processed
    */
    public String handleCommand(String command) {
        // TODO implement your server logic here
        return this.commandInterp.intep(command);
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Starts a *blocking* socket server listening for new connections.
    *
    * @param portNumber The port to listen on.
    * @throws IOException If any IO related operation fails.
    */
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }
        }
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Handles an incoming connection from the socket server.
    *
    * @param serverSocket The client socket to read/write from.
    * @throws IOException If any IO related operation fails.
    */
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if(incomingCommand != null) {
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
