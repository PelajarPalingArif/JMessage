import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private DataInputStream in = null;

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("-- Server Started --");
            System.out.println("-- Waiting for a client --");
            socket = serverSocket.accept();
            System.out.println("-- Client accepted");

            in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream())
            );


            String line = "";
            while (!line.equals("End")){
                    line = in.readUTF();
                    System.out.println(line);
            }
            System.out.println("-- Closing connection --");
            socket.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Server server = new Server(5000);
    }
}
