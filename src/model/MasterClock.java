package model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class MasterClock extends Clock implements Runnable {
    private final List<Socket> slaveSockets = new CopyOnWriteArrayList<>();

    @Override
    public void displayTime() {
        System.out.println("Master Clock Time: " + time);
    }

    public void addSlave(Socket slaveSocket) {
        slaveSockets.add(slaveSocket);
    }

    public void synchronizeSlaves() {
        if (slaveSockets.isEmpty()) {
            System.out.println("No slave clocks to synchronize.");
            return;
        }

        try {
            long totalSeconds = this.time.getEpochSecond();
            Iterator<Socket> iterator = slaveSockets.iterator();

            while (iterator.hasNext()) {
                Socket socket = iterator.next();
                try {
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                    DataInputStream input = new DataInputStream(socket.getInputStream());

                    output.writeUTF("REQUEST_TIME");
                    output.flush();

                    long slaveTime = input.readLong();
                    totalSeconds += slaveTime;
                } catch (EOFException e) {
                    System.out.println("Client disconnected: " + socket);
                    iterator.remove();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            long averageSeconds = totalSeconds / (slaveSockets.size() + 1);

            for (Socket socket : slaveSockets) {
                try {
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                    output.writeLong(averageSeconds);
                    output.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            long masterOffsetSeconds = averageSeconds - this.getTime().getEpochSecond();
            this.adjustTime(masterOffsetSeconds);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        Random random = new Random();

        while (true) {
            try {
                if (this.slaveSockets.isEmpty()) {
                    Thread.sleep(random.nextInt(5000));
                    continue;
                }
                if (random.nextInt(100) == 0) {
                    System.out.println();
                    System.out.println("Synchronizing clocks...");
                    System.out.println("Before Sync:");
                    displayTime();
                    synchronizeSlaves();
                    System.out.println("After synchronization:");
                    displayTime();
                    System.out.println();
                    Thread.sleep(random.nextInt(5000)+5000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}