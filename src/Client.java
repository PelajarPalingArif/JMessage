import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;

public class Client {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    public Client(String address, int port){
        try {
            socket = new Socket(address, port);
            System.out.println("Connected to " + address + ":" + port);
            in = new DataInputStream(System.in);
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String line = "";
        while (!line.equals("End")){
            try {
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(in));
                line = bufferedReader.readLine();
                out.writeUTF(line);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try{
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
