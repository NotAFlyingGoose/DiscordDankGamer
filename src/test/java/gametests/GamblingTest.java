package gametests;

import java.util.Random;
import java.util.Scanner;

public class GamblingTest {

    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Gambling Addiction is a serious problem, if you or anyone you know has a problem find help here: https://www.addictioncenter.com/drugs/gambling-addiction/. That being said, I hope you enjoy my game about gambling!");
        Deck deck = Deck.createStandardDeck().shuffle();
        Hand[] hands = new Hand[4];
        int[] coins = new int[4];
        boolean[] folded = new boolean[4];
        for (int i = 0; i < 4; i++) {
            hands[i] = new Hand(deck).collect();
            coins[i] = 100;
        }

        Random r = new Random();
        int turn = 0;
        int currentBet = 0;
        int maxRounds = 3;
        int round = 0;

        while (true) {
            int lastNotFolded = 0;
            int foldedTotal = 0;
            for (int i = 0; i < folded.length; i++)
                if (folded[i]) foldedTotal++;
                else lastNotFolded = i;

            if (foldedTotal == folded.length - 1) {
                System.out.println("\n\n\tPlayer #" + (lastNotFolded + 1) + " is the winner");
                System.exit(0);
            }

            int currentTurn = (int) modulus(turn, 4);

            if (currentTurn == 0) {
                round++;
            }

            if (folded[currentTurn]) {
                turn++;
                continue;
            }

            if (round == 4) {
                round = 0;
                System.out.println("\n\n\tThe next round of betting has started");
                currentBet = 0;
            }

            System.out.println("\n\nturn #" + (turn + 1));
            System.out.println("The current bet is " + currentBet);
            System.out.println("coins " + coins[currentTurn]);

            if (currentTurn == 2) {
                System.out.println("Your deck: " + hands[currentTurn].getCards() + " (" + hands[currentTurn].getScore() + ")");
                System.out.println("Do you want to check, call, raise, or fold. Type your answer");

                String action;
                boolean goodAnswer = false;
                do {
                    action = input.nextLine();

                    if (action.equals("check") || action.equals("call") || action.equals("raise") || action.equals("fold")) goodAnswer = true;
                    else System.out.println("You must type check, call, raise, or fold.");
                } while (!goodAnswer);

                switch (action) {
                    case "check" -> {
                        if (currentBet == 0) {
                            System.out.println("You checked");
                        } else {
                            System.out.println("You cannot check.");
                            continue;
                        }
                    }
                    case "call" -> {
                        if (currentBet > coins[currentTurn]) {
                            System.out.println("You don't have enough coins.");
                            continue;
                        } else {
                            coins[currentTurn] -= currentBet;
                        }
                        System.out.println("You called the bet.");
                    }
                    case "raise" -> {
                        System.out.println("How much do you want to bet?");

                        int bet;
                        bet = input.nextInt();
                        if (bet > coins[currentTurn]) {
                            System.out.println("You don't have enough coins.");
                            continue;
                        }

                        if (bet > currentBet) {
                            System.out.println("You raised the bet by " + (bet - currentBet));
                            coins[currentTurn] -= bet;
                            currentBet = bet;
                        } else {
                            System.out.println("You must pick a number that is higher than the current bet in order to raise.");
                            continue;
                        }
                    }
                    case "fold" -> {
                        System.out.println("You fold, leaving the game");
                        folded[currentTurn] = true;
                        hands[currentTurn].place();
                    }
                }
            } else {
                boolean fold = false;
                if (hands[currentTurn].getScore().score < 5) {
                    int foldChance = (hands[currentTurn].getScore().score + 1) * 10;
                    if (r.nextInt(foldChance) == 0) {
                        fold = true;
                    }
                }

                int raisedBet = currentBet + (r.nextInt(15 - hands[currentTurn].getScore().score) + hands[currentTurn].getScore().score);

                boolean canPayMinimum = coins[currentTurn] > currentBet;
                boolean canPayRaised = coins[currentTurn] > raisedBet;

                if (!canPayMinimum) fold = true;

                if (fold) {
                    folded[currentTurn] = true;
                    hands[currentTurn].place();
                    System.out.println("Player #" + (currentTurn + 1) + " has folded.");
                    turn++;
                    Thread.sleep(5000);
                    continue;
                }

                if (currentBet == 0) {
                    int callChance = (hands[currentTurn].getScore().score + 1) * 5;
                    if (r.nextInt(callChance) == 0) {
                        System.out.println("Player #" + (currentTurn + 1) + " has checked.");
                        turn++;
                        Thread.sleep(5000);
                        continue;
                    }
                }
                boolean raise = false;

                if (canPayRaised && r.nextInt(2) == 0 || currentBet == 0) {
                    raise = true;
                }

                if (raise) {
                    System.out.println("Player #" + (currentTurn + 1) + " has raised to " + raisedBet + ".");
                    coins[currentTurn] -= raisedBet;
                    currentBet = raisedBet;
                } else {
                    System.out.println("Player #" + (currentTurn + 1) + " has called the bet.");
                    coins[currentTurn] -= currentBet;
                }
                turn++;
                Thread.sleep(5000);
                continue;
            }

            turn++;

            Thread.sleep(5000);
        }
    }

    public static double modulus(double a, double b) {
        return getDecimal(a / b) * b;
    }

    public static double getDecimal(double value) {
        String doubleAsString = String.valueOf(value);
        int indexOfDecimal = doubleAsString.indexOf(".");
        return Double.parseDouble(doubleAsString.substring(indexOfDecimal));
    }

}
