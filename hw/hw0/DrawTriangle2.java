public class DrawTriangle2 {
    public static void main(String[] args) {
        printTriangle(10);
    }

    public static void printTriangle(int N) {
        int count = 1;
        for (int i = 1; i < N + 1; i++) {
            for (int j = 1; j < count; j++) {
                System.out.print("*");
            }
            count += 1;
            System.out.println("*");
        }
    }
}
