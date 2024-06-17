package helpers;

import supportive.MusicBand;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedHashSet;

public class ServerConnector {
    public static ClientToServer clientToServer;

    public void createConnection() throws IOException {
        LinkedHashSet<MusicBand> collection = new LinkedHashSet<MusicBand>();
        clientToServer = new ClientToServer(InetAddress.getByName("127.0.0.1"), 63342, collection);
        clientToServer.connect();
    }
    public void setConnection(ClientToServer clientToServer){
        ServerConnector.clientToServer = clientToServer;
    }

    public ClientToServer getConnection(){
        return clientToServer;
    }
}
