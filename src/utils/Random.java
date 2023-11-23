package src.utils;

public class Random {

    /**
     * Generates a random integer in provided range
     * @param min inclusive minimum number
     * @param max exclusive maximum number
     * @return Random integer
     */
    public static int randInt(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
