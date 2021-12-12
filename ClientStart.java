import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;

public class ClientStart {
    private static volatile HashSet<String> lookup = new HashSet<>();

    public static void main(String[] args) {
        String[] hostName = new String[]{args[0], args[1], args[2]};
        int portNumber1 = Integer.parseInt(args[3]);
        int portNumber2 = Integer.parseInt(args[4]);
        int portNumber3 = Integer.parseInt(args[5]);
        String clientId = args[6];
        String requestNum = args[7];
        String host1 = "S1";
        String host2 = "S2";
        String host3 = "S3";
        ArrayList<String> activeServer = new ArrayList<>();
        activeServer.add(host1);
        activeServer.add(host2);
        activeServer.add(host3);
        int i = 0;
        while (true) {
            try {
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
                String clientInput;
                while ((clientInput = stdIn.readLine()) != null) {
                    for (String host : activeServer) {
                        switch (host) {
                            case "S1":
                                if (connect(hostName[0], portNumber1)) {
                                    Socket echoSocket1 = new Socket(hostName[0], portNumber1);
                                    Client client1 = new Client(echoSocket1, clientId, host1, clientInput, lookup, requestNum);
                                    client1.start();
                                }
                                break;
                            case "S2":
                                if (connect(hostName[1], portNumber2)) {
                                    Socket echoSocket2 = new Socket(hostName[1], portNumber2);
                                    Client client2 = new Client(echoSocket2, clientId, host2, clientInput, lookup, requestNum);
                                    client2.start();
                                }
                                break;
                            case "S3":
                                if (connect(hostName[2], portNumber3)) {
                                    Socket echoSocket3 = new Socket(hostName[2], portNumber3);
                                    Client client3 = new Client(echoSocket3, clientId, host3, clientInput, lookup, requestNum);
                                    client3.start();
                                }
                                break;
                        }
                    }
                    lookup.clear();
                    requestNum = String.valueOf(Integer.parseInt(requestNum) + 1);
                }
            } catch (UnknownHostException e) {
                System.out.println("Don't know about host ");
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection");
                e.printStackTrace();
            }
        }
    }

    private static boolean connect(String hostNameNum, int portNumber) throws IOException {
        Socket socket;
        try {
            socket = new Socket(hostNameNum, portNumber);
        } catch (IOException e) {
            return false;
        }
        socket.close();
        return true;
    }
}