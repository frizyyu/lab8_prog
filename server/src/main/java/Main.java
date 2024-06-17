import helpers.ServerToClient;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedHashSet;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        Logger logger = new helpers.Logger().getLogger();
        LinkedHashSet<supportive.MusicBand> collection = new LinkedHashSet<>();
        ServerToClient listener = new ServerToClient(InetAddress.getByName("127.0.0.1"), collection); //start server
        logger.log(Level.INFO, "SERVER STARTED");
        while (true){
                listener.listen();
        }
    }
}
