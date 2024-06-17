package helpers;

import commandHelpers.CommandInitializator;
import commands.Save;
import jsonHelper.ReadFromJson;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServerToClient implements Runnable {
    private final int DEFAULT_PORT = 63342;
    protected final DatagramChannel channel;
    protected SocketAddress address;
    public static Map<String, List<Double>> userColorMap = new HashMap<String, List<Double>>();;
    Request r;
    LinkedHashSet<supportive.MusicBand> collection = new LinkedHashSet<>();
    ByteBuffer buf = ByteBuffer.allocate(65536);
    HashMap<String, List<String>> ar = new HashMap<>();
    public static HashMap<String, List<Long>> creators = new HashMap<>();
    final Reader rdr = new InputStreamReader(System.in);
    org.apache.logging.log4j.Logger logger = new helpers.Logger().getLogger();
    Scanner input = new Scanner(rdr);
    Thread thread;
    UserDB udb;
    private final Lock R_LOCK = new ReentrantLock();
    public static DBManipulator dbm;
    CreateUsersMap creator = null;
    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
    Selector selector = Selector.open();
    SelectionKey key;
    Iterator<SelectionKey> iter;
    public ServerToClient(InetAddress host, LinkedHashSet<supportive.MusicBand> collection) throws IOException { //Start server
        DatagramChannel dc;
        this.collection = collection;
        dc = DatagramChannel.open();
        address = new InetSocketAddress(63342);
        this.channel = dc;
        channel.bind(address);
        dbm = new DBManipulator("jdbc:postgresql://127.0.0.1:5432/studs", "s******", "password from .pgpass file"); //delete later
    
        udb = new UserDB(dbm);
        if (dbm.getIsConnected()) {
            logger.log(Level.INFO, "Connected to database");
        }
        else{
            logger.log(Level.INFO, "Connection to database failed");
        }
    }


    public void listen(){ //listen client
        try{
            if (rdr.ready()) {
                String st = input.nextLine();
                if (Objects.equals(st, "exit")) {
                    //System.out.println("Saving and exit");
                    if(ReadFromJson.fileName == null)
                        ReadFromJson.fileName = "backup";
                    if (!ReadFromJson.fileName.contains(".json"))
                        ReadFromJson.fileName += ".json";
                    Save saver = new Save(collection, ReadFromJson.fileName);
                    saver.execute(null);
                    System.exit(0);
                } else if (Objects.equals(st, "save")) {
                    Save saver = new Save(collection, null);
                    saver.execute(null);
                }
            }
            channel.configureBlocking(false);
            /*channel.register(selector, SelectionKey.OP_READ);
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            iter = selectedKeys.iterator();
            while (iter.hasNext()) {
                SelectionKey sk = iter.next();
                System.out.println(sk);
            }*/
            //buf.clear();
            ByteBuffer buf = ByteBuffer.allocate(65536);
            fixedThreadPool.submit(() -> {
                try {
                    address = channel.receive(buf);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                byte[] bts = buf.array();

                //System.out.println(Arrays.toString(bts));
                ByteArrayInputStream bais = new ByteArrayInputStream(bts);
                ObjectInputStream ois = null;
                try {
                    ois = new ObjectInputStream(bais);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    r = (Request) ois.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                logger.log(Level.INFO, String.format("Get request from client %s", r.getUser()));
            }).get();
            if (Objects.equals(r.getCommand(), "CONNECTCLIENT")){
                creator = new CreateUsersMap(dbm, r.getUser(), r.getArgs()); //"res"
            }
            else {
                if (CreateUsersMap.users == null || CreateUsersMap.users.get(r.getUser()) == null) {
                    creator = new CreateUsersMap(dbm, r.getUser(), udb.getFileName(r.getUser())); //"res"
                }
            }
            dbm.setCurrUser(r.getUser());
            //выполнение команды
            String data = commandExecution(r.getCommand(), r);
            //System.out.println(data);
            r = new Request(r.getCommand(), String.format("%s|END|", data), new LinkedHashSet<>(), r.getUser(), r.getPswd(), null, null);
            //System.out.println(r.getColor());
            if (r.getCommand().equals("GET CREATORS"))
                r.creators = creators;
            //logger.log(Level.INFO, String.format("Send response to client %s", r.getUser()));
            //System.out.println(r.getCommand());
            /*if (r.getCommand().equals("show")){
                for (String x: creators.keySet()){
                    System.out.println(x);
                    r.setUser(x);
                    sendToAll(r);
                }
            }
            else*/
                send(r);
            }
             catch (IOException | ExecutionException ignored) {
            } catch (SQLException | InterruptedException e) {
            logger.log(Level.WARN, "catch ClassNotFound error");
                throw new RuntimeException(e);
        }
    }
    public void send(Request r) throws IOException, InterruptedException {
        thread = new Thread(() -> {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                if (r.getArgs().length() * 2 > 8000) { //обработка не тут
                    ar.put(r.getUser(), new ArrayList<>());
                    //ar = new ArrayList<>();
                    int n = 5000;
                    //System.out.println(r.getArgs().length());
                    //System.out.println((Math.ceil((float) r.getArgs().length() / n))); //round
                    for (int i = 0; i < (Math.ceil(r.getArgs().length() / (float) n)); i++){
                        String res;
                        try {
                            res = String.format("%s", r.getArgs().substring(n * i, n * i + n));
                        } catch (StringIndexOutOfBoundsException er) {
                            res = String.format("%s", r.getArgs().substring(n * i));
                        }
                        //ar.put(r.getUser(), ar.get(r.getUser()).add(res));
                        ar.get(r.getUser()).add(res);
                    }
                    //System.out.println(ar);
                    r.setArgs(String.format("%sLARGEDATA", ar.get(r.getUser()).size()));
                }
                if (r.getArgs().contains("SENDPLS")){
                    //System.out.println(ar.get("artem"));
                    r.setArgs(ar.get(r.getUser()).get(Integer.parseInt(r.getCommand())));
                }
                if (r.getArgs().contains("STOPSENDING")){
                    //System.out.println("ASD");
                    r.setArgs("|");
                    ar.remove(r.getUser());
                }
                oos.writeObject(r);
                buf = ByteBuffer.wrap(bos.toByteArray());
                //System.out.println(r.getArgs());
                logger.log(Level.INFO, String.format("Send response to client %s. Argument lenght: %s bytes", r.getUser(), r.getArgs().length()));
                channel.send(buf, address);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //System.out.println(CreateUsersMap.users);
        });
        thread.start();
        thread.join();
        thread.stop();
        //System.out.println(r.getArgs());
        //Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
    }

    /*public void sendToAll(Request r) throws UnknownHostException, InterruptedException {
        thread = new Thread(() -> {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                if (r.getArgs().length() * 2 > 8000) { //обработка не тут
                    ar.put(r.getUser(), new ArrayList<>());
                    //ar = new ArrayList<>();
                    int n = 5000;
                    //System.out.println(r.getArgs().length());
                    //System.out.println((Math.ceil((float) r.getArgs().length() / n))); //round
                    for (int i = 0; i < (Math.ceil(r.getArgs().length() / (float) n)); i++) {
                        String res;
                        try {
                            res = String.format("%s", r.getArgs().substring(n * i, n * i + n));
                        } catch (StringIndexOutOfBoundsException er) {
                            res = String.format("%s", r.getArgs().substring(n * i));
                        }
                        //ar.put(r.getUser(), ar.get(r.getUser()).add(res));
                        ar.get(r.getUser()).add(res);
                    }
                    //System.out.println(ar);
                    r.setArgs(String.format("%sLARGEDATA", ar.get(r.getUser()).size()));
                }
                if (r.getArgs().contains("SENDPLS")) {
                    //System.out.println(ar.get("artem"));
                    r.setArgs(ar.get(r.getUser()).get(Integer.parseInt(r.getCommand())));
                }
                if (r.getArgs().contains("STOPSENDING")) {
                    //System.out.println("ASD");
                    r.setArgs("|");
                    ar.remove(r.getUser());
                }
                oos.writeObject(r);
                buf = ByteBuffer.wrap(bos.toByteArray());
                //System.out.println(r.getArgs());
                logger.log(Level.INFO, String.format("Send response to client %s. Argument lenght: %s bytes", r.getUser(), r.getArgs().length()));
                for (int port = 1024; port <= 65535; port++) {
                    System.out.println("QWEQE");
                    try {
                        channel.send(buf, new InetSocketAddress(port));
                    } catch (BindException ignored){}
                    System.out.println("QWEQWEQWE");
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            //System.out.println(CreateUsersMap.users);
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        thread.stop();
    }*/
    CommandInitializator commandsInitializator;
    public String commandExecution(String command, Request data) throws IOException, SQLException, InterruptedException {
        //creator.create();
        //System.out.println(CreateUsersMap.users);
        if (collection == null || CreateUsersMap.users.isEmpty() || CreateUsersMap.users.get(r.getUser()) == null){
            //System.out.println(CreateUsersMap.users);
            creator.create();
            collection = CreateUsersMap.users.get(data.getUser());
            //r = commandsInitializator.validateAndExecute(new Request(data.getCommand(), data.getArgs(), null, "NEEDCOLLECTION", null), false);
        }
        if (CreateUsersMap.users.containsKey(data.getUser()))
            collection = CreateUsersMap.users.get(data.getUser());
        AtomicReference<String> rar = new AtomicReference<>();
        rar.set("");
        commandsInitializator = new CommandInitializator(collection, ReadFromJson.fileName, dbm, udb);
        thread = new Thread(() -> {
            try {
                R_LOCK.lock();
                try {
                    logger.log(Level.INFO, "Executing command");
                    rar.set(commandsInitializator.validateAndExecute(data, false, udb));
                }
                finally {
                    R_LOCK.unlock();
                }
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        thread.join();
        thread.stop();
        //commandsInitializator = new CommandInitializator(collection, ReadFromJson.fileName, dbm, udb);
        //String r = commandsInitializator.validateAndExecute(data, false, udb);
        //System.out.println(data.getCommand());
        //System.out.println(CreateUsersMap.users.size());
        String r = rar.get();
        if (Objects.equals(data.getCommand(), "test server status") || Objects.equals(data.getCommand(), "NEWUSER") || Objects.equals(data.getCommand(), "CONNECTCLIENT")) {
            collection = commandsInitializator.collection; //обработка где-то тут
            //System.out.println(collection);
        }
        else {
            collection = CreateUsersMap.users.get(data.getUser());
        }
        //System.out.println(r);
        return r;
    }

    public Request getRequest(){
        return r;
    }

    @Override
    public void run() {

    }
}
