import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter IP Address : ");
        String ipAddress = input.nextLine();
        System.out.print("Enter Port : ");
        int port = input.nextInt();

        Client cl = new Client(ipAddress, port);
    }
}
