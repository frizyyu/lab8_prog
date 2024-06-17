package helpers;

import interfacesFx.MainController;
import jsonHelper.ReadFromJson;
import supportive.MusicBand;

import java.io.*;
import javafx.scene.paint.Color;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.*;

public class ClientToServer {

    protected final DatagramChannel channel;
    protected SocketAddress address;
    public static HashMap<String, List<Long>> creators;
    LinkedHashSet<MusicBand> collection;
    ByteBuffer buf = ByteBuffer.allocate(65536);
    Scanner inputsc = new Scanner(System.in);
    public static String correctData = "";
    public static String response = ""; //так плохо, но хочу получить ответ, нужно было при изначальном проектировании возвращать не коллекцию, а класс с полями
    public ClientToServer(InetAddress host, int port, LinkedHashSet<MusicBand> collection) throws IOException {
        this.collection = collection;
        DatagramChannel dc;
        dc = DatagramChannel.open();
        address = new InetSocketAddress(host, port);
        this.channel = dc;
    }

    public void connect() throws IOException {
        if (!channel.isConnected()) {
            channel.connect(address);
            channel.configureBlocking(false);
            try {
                send(new Request("test server status", "", collection, "", "password", null, null));
                //System.out.println("CONNECTED");
            } catch (ClassNotFoundException e) {
                //System.out.println("WWWWWWW");
                throw new RuntimeException(e);
            }
        }
    }
    public LinkedHashSet<MusicBand> send(Request data) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        if ((data.getArgs().length() + data.getUser().length() + data.getCommand().length()) * 2 > 64000){
            System.out.println("Can not send large data");
            return new LinkedHashSet<MusicBand>();
        }
        else {
            oos.writeObject(data);
            buf.clear();
            //System.out.println(data.getCommand());
            //System.out.println(data.getArgs());
            buf = ByteBuffer.wrap(bos.toByteArray());
            //System.out.println(Arrays.toString(buf.array()) + " " + address.toString());
            channel.send(buf, address);
            return listen();
        }
        //return listen();
    }
    public LinkedHashSet<MusicBand> listener () throws IOException {
        return new LinkedHashSet<>();
    }
    public LinkedHashSet<MusicBand> listen() throws IOException, ClassNotFoundException {
        while (true){
            channel.configureBlocking(false);
            buf.clear();
            buf = ByteBuffer.allocate(65536);
            channel.receive(buf);

            try {
                byte[] bts = buf.array();
                ByteArrayInputStream bais = new ByteArrayInputStream(bts);
                ObjectInputStream ois = new ObjectInputStream(bais);

                Request req = (Request) ois.readObject();
                //System.out.println(req.getUser());
                if (req.getUser().equals(CreateUser.userName) || CreateUser.userName == null || req.getUser().equals("")) {
                    if (req.getArgs().contains("LARGEDATA")) {
                        for (int i = 0; i < Integer.parseInt(req.getArgs().replace("LARGEDATA", "")); i++) {
                            send(new Request(String.format("%s", i), "SENDPLS", collection, req.getUser(), req.getPswd(), null, null));
                        }
                        send(new Request("", "STOPSENDING", collection, req.getUser(), req.getPswd(), null, null));
                        req.setArgs("|");
                    }
                    collection = req.getElement();
                    if (req.getArgs().equals("UserExist|")) {
                        System.out.println("Logged in successful");
                    } else if (req.getArgs().contains("UserDoesntExist|")) {
                        CreateUser.userName = "|";
                        req.setArgs("|");
                    }
                    if (Objects.equals(req.getCommand(), "exit")) {
                        if (req.getArgs().strip().equals("unsaved")) {
                            ContinueAction cont = new ContinueAction();
                            System.out.println("Collection unsaved. Save and exit? y/n");
                            System.out.printf("%s >>> ", CreateUser.userName);
                            int c = cont.continueAction("Saving", "Not saved. Exit cancelled", "Action skipped. Invalid answer");
                            if (c == 1) {
                                send(new Request("exit", String.format("%ssaveit|%s", CreateUser.userName, ReadFromJson.fileName), collection, CreateUser.userName, "password", null, null));
                            }
                        }
                        disconnect();
                        System.exit(0);
                    } else if (req.getArgs() != null && req.getArgs().contains("FILENAME ")) {
                        if (req.getArgs().contains("true|")) {
                            try {
                                ReadFromJson.fileName = req.getArgs().split(" ")[1];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                ReadFromJson.fileName = "";
                            }
                            collection = req.getElement();
                            ReadFromJson.fileName = ReadFromJson.fileName.replace("|Y", "");
                        } else {
                            collection = null;
                        }
                    } else if (req.getArgs().strip().equals("ERRORINFILE")) {

                    }
                    creators = req.getCreators();
                    if ((!req.getArgs().contains("|") || req.getArgs().contains("|END|")) && (!req.getArgs().contains("CONNECTED") && !req.getArgs().contains("UserExist"))) {
                        response += req.getArgs();
                    }
                    if (req.getCommand().equals("show")) {
                        correctData = response.replace("|END|", "");
                    }
                    return collection;
                }
            } catch (StreamCorruptedException ignored) {
            } catch (EOFException | ClassNotFoundException e){
                System.out.println("A large amount of data, it is impossible to transfer");
            }
        }
    }

    public void listenForThread() throws IOException, ClassNotFoundException {
            //System.out.println("QWEQWEQEWQWE");
            channel.configureBlocking(false);
            buf.clear();
            buf = ByteBuffer.allocate(65536);
            channel.receive(buf);

            try {
                byte[] bts = buf.array();
                ByteArrayInputStream bais = new ByteArrayInputStream(bts);
                ObjectInputStream ois = new ObjectInputStream(bais);

                Request req = (Request) ois.readObject();
                //System.out.println(req.getArgs());
                if (req.getUser().equals(CreateUser.userName) || CreateUser.userName == null) {
                    if (req.getArgs().contains("LARGEDATA")) {
                        for (int i = 0; i < Integer.parseInt(req.getArgs().replace("LARGEDATA", "")); i++) {
                            send(new Request(String.format("%s", i), "SENDPLS", collection, req.getUser(), req.getPswd(), null, null));
                        }
                        send(new Request("", "STOPSENDING", collection, req.getUser(), req.getPswd(), null, null));
                        req.setArgs("|");
                    }
                    collection = req.getElement();
                    if (req.getArgs().equals("UserExist|")) {
                        System.out.println("Logged in successful");
                    } else if (req.getArgs().contains("UserDoesntExist|")) {
                        CreateUser.userName = "|";
                        req.setArgs("|");
                    }
                    if (Objects.equals(req.getCommand(), "exit")) {
                        if (req.getArgs().strip().equals("unsaved")) {
                            ContinueAction cont = new ContinueAction();
                            System.out.println("Collection unsaved. Save and exit? y/n");
                            System.out.printf("%s >>> ", CreateUser.userName);
                            int c = cont.continueAction("Saving", "Not saved. Exit cancelled", "Action skipped. Invalid answer");
                            if (c == 1) {
                                send(new Request("exit", String.format("%ssaveit|%s", CreateUser.userName, ReadFromJson.fileName), collection, CreateUser.userName, "password", null, null));
                            }
                        }
                        disconnect();
                        System.exit(0);
                    } else if (req.getArgs() != null && req.getArgs().contains("FILENAME ")) {
                        if (req.getArgs().contains("true|")) {
                            try {
                                ReadFromJson.fileName = req.getArgs().split(" ")[1];
                            } catch (ArrayIndexOutOfBoundsException e) {
                                ReadFromJson.fileName = "";
                            }
                            collection = req.getElement();
                            ReadFromJson.fileName = ReadFromJson.fileName.replace("|Y", "");
                        } else {
                            collection = null;
                        }
                    } else if (req.getArgs().strip().equals("ERRORINFILE")) {

                    }
                    creators = req.getCreators();
                    if ((!req.getArgs().contains("|") || req.getArgs().contains("|END|")) && (!req.getArgs().contains("CONNECTED") && !req.getArgs().contains("UserExist"))) {
                        response += req.getArgs();
                    }
                    if (req.getCommand().equals("show")) {
                        correctData = response.replace("|END|", "");
                    }
                }
            } catch (StreamCorruptedException ignored) {
            } catch (EOFException | ClassNotFoundException e){
                System.out.println("A large amount of data, it is impossible to transfer");
            }
    }

    public void disconnect() throws IOException {
        channel.disconnect();
        channel.close();
        //System.exit(0);
    }

    public LinkedHashSet<MusicBand> getCollection(){
        return collection;
    }
}
