public class DrawTriangle {
    public static void main(String[] args) {
        int count = 1;
        for (int i = 1; i < 6; i++) {
            for (int j = 1; j < count; j++) {
                System.out.print("*");
            }
            count += 1;
            System.out.println("*");
        }
    }
}