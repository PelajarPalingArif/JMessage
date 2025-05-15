import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

public class Client {

    private Socket socket;
    private DataInputStream inFromServer;
    private DataInputStream inFromUser;
    private DataOutputStream out;

    public Client(String address, int port){
        try {
            socket = new Socket(address, port);
            System.out.println("Connected to " + address + ":" + port);

            // Read from server
            inFromServer = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            // Read from keyboard (user input)
            inFromUser = new DataInputStream(System.in);
            // Write to server
            out = new DataOutputStream(socket.getOutputStream());

            // Thread to read messages from the server
            AtomicReference<String> userName = new AtomicReference<>();
            Thread readThread = new Thread(() -> {
                try {
                    String serverMsg;
                    while ((serverMsg = inFromServer.readUTF()) != null) {
                        if (userName.get() == null) {
                            userName.set(serverMsg);
                            continue;
                        }
                        System.out.println(serverMsg);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });

            readThread.start();

            // Main thread handles sending messages to server
            String line = "";
            while (!line.equals("End")) {
                line = inFromUser.readLine();
                System.out.println(userName.get() + " : " + line);
                out.writeUTF(line);
                out.flush();
            }

            socket.close();
            inFromUser.close();
            inFromServer.close();
            out.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
