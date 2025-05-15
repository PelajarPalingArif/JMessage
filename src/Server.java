import javax.xml.crypto.Data;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;

public class Server {
    private Socket socketOne = null;
    private ServerSocket serverSocketOne = null;
    private DataInputStream inOne = null;
    private DataOutputStream outOne = null;

    private Socket socketTwo = null;
    private ServerSocket serverSocketTwo = null;
    private DataInputStream inTwo = null;
    private DataOutputStream outTwo = null;

    public Server(int portOne, int portTwo) throws InterruptedException {
        try {
            CountDownLatch countDown = new CountDownLatch(2);
            serverSocketOne = new ServerSocket(portOne);
            System.out.println("-- Server Started --");
            Thread st1 = new Thread(() -> {
                System.out.println("-- Waiting for client one --");
                try {
                    socketOne = serverSocketOne.accept();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("-- Client One accepted");
                System.out.println("Client One: ");
                System.out.println("IP One: " + socketOne.getInetAddress().toString());
                countDown.countDown();
                try {
                    inOne = new DataInputStream(
                            new BufferedInputStream(socketOne.getInputStream())
                    );
                    outOne = new DataOutputStream(socketOne.getOutputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            serverSocketTwo = new ServerSocket(portTwo);
            Thread st2 = new Thread(() -> {
                System.out.println("-- Waiting for client two --");
                try {
                    socketTwo = serverSocketTwo.accept();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("-- Client two accepted");
                System.out.println("Client two: ");
                System.out.println("IP Two: " + socketTwo.getInetAddress().toString());
                countDown.countDown();
                try {
                    inTwo = new DataInputStream(
                            new BufferedInputStream(socketTwo.getInputStream())
                    );
                    outTwo = new DataOutputStream(socketTwo.getOutputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            outOne.writeUTF("C1");
            outTwo.writeUTF("C2");
            st1.start();
            st2.start();
            countDown.await();
            System.out.println("All party connection established");


            Thread t1 = new Thread(() -> {
                String c1Text = "";
                while (!c1Text.equals("End")){
                    try {
                        c1Text = inOne.readUTF();
                    } catch (SocketException e) {
                        System.out.println("C1 Ending Connection");
                        try {
                            socketOne.close();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("C1 : " + c1Text);
                    try {
                        outTwo.writeUTF("C1 : " + c1Text);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            Thread t2 = new Thread(() -> {
                String c2Text = "";
                while (!c2Text.equals("End")){
                    try {
                        c2Text = inTwo.readUTF();
                    } catch (SocketException e) {
                        System.out.println("C2 Ending Connection");
                        try {
                            socketTwo.close();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("C2 : " + c2Text);
                    try {
                        outOne.writeUTF("C2 : " + c2Text);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            });

            t1.start();
            t2.start();
            t1.join();
            t2.join();
            System.out.println("-- Closing connection --");
            if (!socketOne.isClosed())socketOne.close();
            if (!socketTwo.isClosed())socketTwo.close();
            inOne.close();
            outOne.close();
            inTwo.close();
            outTwo.close();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Server server = new Server(5000, 5001);
    }
}
