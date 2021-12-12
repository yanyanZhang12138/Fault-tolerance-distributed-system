import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class RMRequestHandler implements Runnable {
    private final Socket gfd;
    private ArrayList<String> memberShip;

    RMRequestHandler(Socket gfd, ArrayList<String> memberShip) {
        this.gfd = gfd;
        this.memberShip = memberShip;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(gfd.getInputStream()));) {
            String ldfInput;
            while ((ldfInput = in.readLine()) != null) {
                ldfInput = ldfInput.replaceAll("[^A-Za-z0-9 ]", "");
                final String addServer1 = "LFD1 add replica S1";
                final String addServer2 = "LFD2 add replica S2";
                final String addServer3 = "LFD3 add replica S3";
                final String deleteServer1 = "LFD1 delete replica S1";
                final String deleteServer2 = "LFD2 delete replica S2";
                final String deleteServer3 = "LFD3 delete replica S3";
                String s1 = "S1";
                String s2 = "S2";
                String s3 = "S3";
                switch (ldfInput) {
                    case addServer1:
                        memberShip.add(s1);
                        // System.out.println(addServer1);
                        System.out.println("RM: " + memberShip.size() + " member: " + memberShip.toString());
                        break;
                    case addServer2:
                        memberShip.add(s2);
                        // System.out.println(addServer2);
                        System.out.println("RM: " + memberShip.size() + " member: " + memberShip.toString());
                        break;
                    case addServer3:
                        memberShip.add(s3);
                        // System.out.println(addServer3);
                        System.out.println("RM: " + memberShip.size() + " member: " + " " + memberShip.toString());
                        break;
                    case deleteServer1:
                        memberShip.remove(s1);
                        // System.out.println(deleteServer1);
                        System.out.println("RM: " + memberShip.size() + " member: " + " " + memberShip.toString());
                        break;
                    case deleteServer2:
                        memberShip.remove(s2);
                        // System.out.println(deleteServer2);
                        System.out.println("RM: " + memberShip.size() + " member: " + " " + memberShip.toString());
                        break;
                    case deleteServer3:
                        memberShip.remove(s3);
                        // System.out.println(deleteServer1);
                        System.out.println("RM: " + memberShip.size() + " member: " + " " + memberShip.toString());
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("I/O exception" + e);
        } catch (Exception ex) {
            System.out.println("Exception in Thread Run. Exception : " + ex);
        }
    }
}