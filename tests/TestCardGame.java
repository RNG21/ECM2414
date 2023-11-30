package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import src.CardGame;
import src.Pack;
import src.Card;
import src.exceptions.InvalidPack;

public class TestCardGame {
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    Scanner scanner;
    String[] filenames = new String[]{
        "fileNotExist_918230981293810923.txt",
        "packTooLong.txt",
        "packTooShort.txt",
        "invalidPackNotInteger.txt",
        "invalidPackNegativeInteger.txt",
        "4PlayersValidPack.txt"
    };

    @Before
    public void setUp(){
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDown() throws IOException {
        if(scanner != null) {
            scanner.close();
        }
        for (String filename : filenames) {
            if (Files.exists(Path.of(filename))) {
                Files.delete(Path.of(filename));
            }
        }
    }

    public static int substringCount(String str, String subStr) {
        return (str.length() - str.replace(subStr, "").length()) / subStr.length();
    }

    @Test
    public void testGetPlayerAmount(){
        String input = "1.2\n"
                     + "asd\n"
                     + "0\n"
                     + "-1\n"
                     + "2";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        scanner = new Scanner(in);
        try {
            CardGame.getPlayerAmount(scanner);
        } catch (NoSuchElementException e) {
            fail("Validator rejected valid input");
        }
        assertEquals(substringCount(outContent.toString(), "Enter player amount:"), 5);
    }

    public void createPackFiles() throws IOException{
        for (String filename : filenames) {
            if (filename == "fileNotExist_918230981293810923.txt") {
                continue;
            }
            ArrayList<String> pack = new ArrayList<>();
            switch (filename) {
                case "packTooLong.txt":
                    for (int i = 0; i < 100; i++) {
                        pack.add("1");
                    }
                    break;
                case "packTooShort.txt":
                    for (int i = 0; i < 4; i++) {
                        pack.add("1");
                    }
                    break;
                case "invalidPackNotInteger.txt":
                    for (int i = 0; i < 31; i++) {
                        pack.add("1");
                    }
                    pack.add("text");
                    break;
                case "invalidPackNegativeInteger.txt":
                    for (int i = 0; i < 31; i++) {
                        pack.add("1");
                    }
                    pack.add("-1");
                    break;
                case "4PlayersValidPack.txt":
                    for (int i = 0; i < 32; i++) {
                        pack.add("1");
                    }
                    break;
                default:
                    break;
            }

            try (FileWriter writer = new FileWriter(filename)) {
                StringBuilder builder = new StringBuilder();
                for (String line : pack) {
                    builder.append(line).append("\n");
                }
                writer.write(builder.toString());
            }
        }
    }

    @Test
    public void testGetPackPath() throws IOException{
        createPackFiles();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < filenames.length; i++) {
            builder.append(filenames[i]).append("\n");
        }
        ByteArrayInputStream in = new ByteArrayInputStream(builder.toString().getBytes());
        scanner = new Scanner(in);
        try {
            CardGame.getPackPath(4, scanner);
        } catch (NoSuchElementException e) {
            fail("Validator rejected valid input");
        }
        assertEquals(substringCount(outContent.toString(), "Enter pack path:"), filenames.length);
    }

    @Test
    public void testDealCards() throws InvalidPack{
        int playerAmount = 3;
        int[] nums = new int[8*playerAmount];
        for (int i = 0; i < 8*playerAmount; i++) {
            nums[i] = i;
        }
        Pack pack = new Pack(nums, playerAmount);
        Card[][][] dealtCards = CardGame.dealCards(pack);
        int[][][] expected = new int[][][] {
            {{0, 3, 6, 9}, {1, 4, 7, 10}, {2, 5, 8, 11}},
            {{12, 15, 18, 21}, {13, 16, 19, 22}, {14, 17, 20, 23}}
        };
        for (int i = 0; i < dealtCards.length; i++) {
            for (int j = 0; j < dealtCards[i].length; j++) {
                for (int k = 0; k < dealtCards[i][j].length; k++) {
                    assertEquals(dealtCards[i][j][k].value, expected[i][j][k]);
                }
            }
        }
    }
}
