import src.Pack;
import src.Card;

public class Main {
    public static void main(String[] args) {
        try {
            Pack pack = Pack.generatePack(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  }

