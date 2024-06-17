package model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class SlaveClock extends Clock implements Runnable {
    private static int increment = 1;
    private final int id;
    private final String masterIp;

    public SlaveClock(long initialOffsetSeconds, String masterIp) {
        super();
        id = increment;
        increment++;
        this.adjustTime(initialOffsetSeconds);
        this.masterIp = masterIp;
    }

    @Override
    public void displayTime() {
        System.out.println("Slave Clock " + this.id + " Time: " + time);
    }

    public void run() {
        try (Socket socket = new Socket(this.masterIp, 12345)) {
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream());

            while (true) {
                String request = input.readUTF();
                if (request.equals("REQUEST_TIME")) {
                    output.writeLong(this.getTime().getEpochSecond());
                    output.flush();

                    long newTime = input.readLong();
                    long offsetSeconds = newTime - this.getTime().getEpochSecond();
                    System.out.println();
                    System.out.println("Before Sync:");
                    displayTime();
                    System.out.println("After synchronization:");
                    this.adjustTime(offsetSeconds);
                    displayTime();
                    System.out.println();
                }
                Thread.sleep(new Random().nextInt(3000));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}