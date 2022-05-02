import encryption.AuthenticatedEncryptionService;

public class Runner {

    public static void main(String[] args) {
        AuthenticatedEncryptionService service = new AuthenticatedEncryptionService();
        service.start();
    }
}
