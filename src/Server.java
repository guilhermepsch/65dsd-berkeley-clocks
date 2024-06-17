import model.MasterClock;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        MasterClock masterClock = new MasterClock();

        Thread connectionHandler = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(12345)) {
                System.out.println("Master clock server started...");

                while (true) {
                    Socket socket = serverSocket.accept();
                    masterClock.addSlave(socket);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        connectionHandler.start();
        new Thread(masterClock).start();
    }
}
