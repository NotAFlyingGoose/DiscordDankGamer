package gametests;

import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class FishingTest {
    static Scanner scanner = new Scanner(System.in);
    static String fishingRod;
    static String location;
    static String[] catching;
    static int mode = -2;

    static int rodX = 4;
    static int rodY = -1;

    static Random r = new Random();

    static Timer catchTimer = new Timer();
    static String catchInput = "";

    public static void main(String[] args) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Enter power level (0 - 10): ");
        int inputPower = scanner.nextInt();
        if (inputPower < 0) inputPower = 0;
        if (inputPower > 10) inputPower = 10;

        float power = (inputPower * 0.1f);

        if (power == 0) {
            System.out.println("That throw was \uD83D\uDCA9.");
            rodY = -1;
            return;
        }

        System.out.println(power);

        double throwY = Math.ceil(power * 7.0f);

        System.out.println("setting rod position to 4, " + throwY);
        rodX = rodX + (r.nextInt(4) - 2);
        if (rodX < 1) rodX = 1;
        if (rodX > 7) rodX = 7;
        rodY = (int) throwY;

        printWorld();

        long sleepTime = 1000 * (r.nextInt(10 - 4) + 4);

        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("BITE!!!");
        System.out.println("Type catch to get the fish (must be within 3 seconds):");

        catchTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (catchInput.equals("catch")) {
                    System.out.println("You caught a fish!");
                } else {
                    System.out.println("You failed :(");
                }
                System.exit(1);
            }
        }, 3000);

        catchInput = scanner.next();
    }

    public static void printWorld() {
        StringBuilder send = new StringBuilder();
        String[] topWorld = new String[8];
        String[][] screen = new String[8][8];
        for (int i = 0; i < topWorld.length; i++) {
            topWorld[i] = "  ";
            if (i == rodX) topWorld[i] = "\uD83D\uDE42 ";

            send.append(topWorld[i]);
        }
        send.append('\n');

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                screen[y][x] = "~ ";
                if (x == rodX && y < rodY) screen[y][x] = "| ";
                if (x == rodX && y == rodY) screen[y][x] = "🎣 ";

                send.append(screen[y][x]);
            }
            send.append('\n');
        }
        System.out.print(send.toString());
    }

}
