import java.nio.charset.StandardCharsets;

public class Runner {

    static {
        System.loadLibrary("LibraryNative");
    }

    public static void main(String[] args) {
        AuthenticatedEncryptionService service = new AuthenticatedEncryptionService();
        service.start();
        service.commit(AutomateDataTypes.NULL.code);
        service.absorb("on".getBytes(StandardCharsets.UTF_8));
    }
}
