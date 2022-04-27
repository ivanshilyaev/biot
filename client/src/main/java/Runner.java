public class Runner {

    static {
        System.loadLibrary("LibraryNative");
    }

    public static void main(String[] args) {
        LibraryNative.greetings();

//        try {
//            new ClientService().turnOn();
//        } catch (IOException | InterruptedException e) {
//            System.out.println(e.getMessage());
//        }
    }
}
