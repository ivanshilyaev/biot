import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Runner {

    static {
        System.loadLibrary("LibraryNative");
    }

    public static void main(String[] args) {
        AuthenticatedEncryptionService service = new AuthenticatedEncryptionService();
        service.start();
        service.commit(AutomateDataTypes.NULL.code);
        service.absorb("on".getBytes(StandardCharsets.UTF_8));
        service.squeeze(2);
        service.encrypt("on".getBytes(StandardCharsets.UTF_8));
    }
}
