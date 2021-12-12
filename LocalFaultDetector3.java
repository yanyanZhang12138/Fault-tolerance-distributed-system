import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.lang.Runnable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalFaultDetector3 {
    private volatile static boolean connected;
    private volatile static boolean hasInput;

    public static void main(String[] args) throws Exception {
        String serverHostName = args[0];
        int serverPortNumber = Integer.parseInt(args[1]);
        String gfdHostName = args[2];
        int gfdPortNumber = Integer.parseInt(args[3]);
        String rmHostName = args[4];
        int rmPortNumber = Integer.parseInt(args[5]);
        int heardBeat = Integer.parseInt(args[6]);

        Thread serverWoker = new serverRunnable(serverHostName, serverPortNumber, heardBeat);
        serverWoker.setName("LFD");
        Thread gfdWorker = new gfdRunnable(gfdHostName, gfdPortNumber, heardBeat);
        Thread rmWorker = new rmRunnable(rmHostName, rmPortNumber, heardBeat);
        rmWorker.setName("rm");
        gfdWorker.setName("lfd");
        rmWorker.start();
        serverWoker.start();
        gfdWorker.start();
    }
    
    public static class serverRunnable extends Thread {
        //private Socket s;
        private String serverHostName;
        private int serverPortNumber;
        private long heartBeatInterval;

        public serverRunnable(String hostName, int portNumber, long heartBeat) {
            this.heartBeatInterval = heartBeat;
            this.serverHostName = hostName;
            this.serverPortNumber = portNumber;
        }

        @Override
        public void run() {
            try (Socket echoSocket = new Socket(serverHostName, serverPortNumber);
                 PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));) {
                out.println("LFD: Are you alive?");
                String serverInput;
                while ((serverInput = in.readLine()) != null) {
                    System.out.println(serverInput);
                    hasInput = true;
                    connected = true;
                    Thread.sleep(heartBeatInterval);
                    out.println("LFD: Are you alive?");
                }
                System.out.println("failed-heartbeat: timeout expiration");
                hasInput = false;
            } catch (UnknownHostException e) {
                System.out.println("Don't know about host " + serverHostName);
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to " + serverHostName);
                System.exit(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class gfdRunnable extends Thread {
        private String gfdHostName;
        private int gfdPortNumber;
        private long heartBeatInterval;

        public gfdRunnable(String hostName, int portNumber, long heartBeat) {
            this.gfdHostName = hostName;
            this.gfdPortNumber = portNumber;
            this.heartBeatInterval = heartBeat;
        }

        @Override
        public void run() {
            try (Socket echoSocket = new Socket(gfdHostName, gfdPortNumber);
                 PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true)) {
                boolean flag = true;
                String serverName = "S3";
                String lfdName = "LFD3";
                while (true) {
                    if (connected) {
                        if (hasInput) {
                            if (flag) {
                                out.println(lfdName + " add replica " + serverName);
                                flag = false;
                            } else {
                                out.println(serverName + " is alive");
                            }
                        } else {
                            break;
                        }
                        Thread.sleep(heartBeatInterval);
                    }
                }
                out.println(lfdName + " delete replica " + serverName);
            } catch (UnknownHostException e) {
                System.out.println("Don't know about host " + gfdHostName);
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to " + gfdHostName);
                System.exit(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static class rmRunnable extends Thread {
        private String rmHostName;
        private int rmPortNumber;
        private long heartBeatInterval;

        public rmRunnable(String hostName, int portNumber, long heartBeat) {
            this.rmHostName = hostName;
            this.rmPortNumber = portNumber;
            this.heartBeatInterval = heartBeat;
        }

        @Override
        public void run() {
            try (Socket echoSocket = new Socket(rmHostName, rmPortNumber);
                 PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true)) {
                boolean flag = true;
                String serverName = "S3";
                String lfdName = "LFD3";
                while (true) {
                    if (connected) {
                        if (hasInput) {
                            if (flag) {
                                out.println(lfdName + " add replica " + serverName);
                                flag = false;
                            } else {
                                out.println(serverName + " is alive");
                            }
                        } else {
                            break;
                        }
                        Thread.sleep(heartBeatInterval);
                    }
                }
                out.println(lfdName + " delete replica " + serverName);
            } catch (UnknownHostException e) {
                System.out.println("Don't know about host " + rmHostName);
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to " + rmHostName);
                System.exit(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}