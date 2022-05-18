package by.bsu.biot.service;

import by.bsu.biot.dto.EncryptionResult;
import by.bsu.biot.util.HexEncoder;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Service
public class ClientService {

    @Value("${biot.encryption.enabled}")
    private boolean encryptionEnabled;

    private static final String LUMP_STATIC_IP_ADDRESS = "192.168.100.93";

    private static final OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public void turnOn() throws IOException {
        if (encryptionEnabled) {
            sendEncryptedMessage("onn");
        }
        else {
            sendMessage("onn");
        }
    }

    public void turnOff() throws IOException {
        if (encryptionEnabled) {
            sendEncryptedMessage("off");
        }
        else {
            sendMessage("off");
        }
    }

    private void sendMessage(String state) throws IOException {
        FormBody body = new FormBody.Builder()
                .add("param1", state)
                .build();
        Request request = new Request.Builder()
                .url("http://" + LUMP_STATIC_IP_ADDRESS + "/led")
                .post(body)
                .build();
        client.newCall(request).execute();
    }

    private void sendEncryptedMessage(String state) throws IOException {
        int l = 256;
        int d = 1;
        byte[] A = new byte[16];
        byte[] K = HexEncoder.reverseAndDecode(System.getenv("ENCRYPTION_KEY"));
        byte[] X = state.getBytes(StandardCharsets.UTF_8);
        byte[] I = new byte[49];

        AuthenticatedEncryptionService service = new AuthenticatedEncryptionService(l, d);
        EncryptionResult encryptionResult = service.authEncrypt(A, K, I, X);

        String message = new String(Base64.getEncoder().encode(HexEncoder.encode(encryptionResult.getY()).getBytes()));
        String mac = new String(Base64.getEncoder().encode(HexEncoder.encode(encryptionResult.getT()).getBytes()));

        FormBody body = new FormBody.Builder()
                .add("param1", message)
                .add("param2", mac)
                .build();
        Request request = new Request.Builder()
                .url("http://" + LUMP_STATIC_IP_ADDRESS + "/led")
                .post(body)
                .build();
        client.newCall(request).execute();
    }
}
