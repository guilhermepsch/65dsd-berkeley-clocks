import model.SlaveClock;

import java.util.Random;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Adicione o IP do Master Clock: ");
        String masterIp = scanner.nextLine();
        long initialOffsetSeconds = new Random().nextInt(119) - 59;
        SlaveClock slave = new SlaveClock(initialOffsetSeconds, masterIp);
        new Thread(slave).start();
    }
}