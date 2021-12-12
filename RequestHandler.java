import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;

public class RequestHandler extends Thread {
    private final Socket client;
    private String serverId;
    private HashSet<String> myState;
    private String mode;

    public RequestHandler(Socket client, String serverId, HashSet<String> myState, String mode) {
        this.client = client;
        this.serverId = serverId;
        this.myState = myState;
        this.mode = mode;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));) {
            String clientInput;
            String clientId;
            String requestNum;
            int i = 0;
            while ((clientInput = in.readLine()) != null) {
                if (mode.equals("passive") || mode.equals("active")) {
                    clientId = clientInput.substring(0, 3);
                    if (!clientId.equals("LFD")) {
                        requestNum = clientInput.split("Request_num")[1];
                        System.out.println("[" + System.currentTimeMillis() + "]" + " Received " + "<" + clientId + ", " + serverId + ", " + requestNum + ", " + clientInput + ">");
                    } else {
                        System.out.println("[" + System.currentTimeMillis() + "]" + " Received " + "<" + clientId + ", " + serverId + ", " + clientInput + ">");
                    }
                    if (!clientId.equals("LFD")) {
                        requestNum = clientInput.split("Request_num")[1];
                        System.out.println("[" + System.currentTimeMillis() + "]" + " " + " my_state_S = " + myState.size() + " before processing " + "<" + clientId + ", " + serverId + ", " + requestNum + ", " + clientInput + ">");
                        myState.add(clientInput);
                        System.out.println("[" + System.currentTimeMillis() + "]" + " " + " my_state_S = " + myState.size() + " after processing " + "<" + clientId + ", " + serverId + ", " + requestNum + ", " + clientInput + ">");
                    }
                    if (!clientId.equals("LFD")) {
                        writer.write("Hello: I have received your message" + i);
                        i += 1;
                    } else {
                        writer.write("Hello: I have received your message");
                    }

                    if (!clientId.equals("LFD")) {
                        requestNum = clientInput.split("Request_num")[1];
                        System.out.println("[" + System.currentTimeMillis() + "]" + " Sending " + "<" + clientId + ", " + serverId + ", " + requestNum + ", " + "Hello: I have received your message" + ">");
                    } else {
                        System.out.println("[" + System.currentTimeMillis() + "]" + " Sending " + "<" + clientId + ", " + serverId + ", " + "Hello: I have received your message" + ">");
                    }
                    writer.newLine();
                    writer.flush();
                } else {
                    clientId = clientInput.substring(0, 3);
                    if (clientId.equals("LFD")) {
                        System.out.println("[" + System.currentTimeMillis() + "]" + " Received " + "<" + clientId + ", " + serverId + ", " + clientInput + ">");
                        writer.write("Hello: I have received your message");
                        System.out.println("[" + System.currentTimeMillis() + "]" + " Sending " + "<" + clientId + ", " + serverId + ", " + "Hello: I have received your message" + ">");
                        writer.newLine();
                        writer.flush();
                    } else if (clientId.equals("pri")) {
                        String[] state = clientInput.split("\\*");
//                        System.out.println("received from primary: " + clientInput);
//                        myState.addAll(Arrays.asList(state));
                        int len = state.length - 1;
                        if (len == 0) {
                            len = 1;
                        }
                        System.out.println("Updated: " + serverId + " my_state " + len + " checkpoint_count: " + clientInput.split("#")[1]);
//                        System.out.println("Updated: " + serverId + " my_state " + myState.size() + " checkpoint_count: " + clientInput.split("#")[1]);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("I/O exception" + e);
        } catch (Exception ex) {
            System.out.println("Exception in Thread Run. Exception : " + ex);
        }
    }
}