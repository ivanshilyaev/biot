package by.bsu.biot.service;

import by.bsu.biot.dto.EncryptionResult;
import by.bsu.biot.util.HexEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final AuthenticatedEncryptionService encryptionService;

    @Value("${biot.encryption.enabled}")
    private boolean encryptionEnabled;

    private String encryptionKey;

    private static final String LUMP_STATIC_IP_ADDRESS = "192.168.100.93";

    private static final int HTTP_SUCCESS_CODE = 200;

    private static final OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public void init() {
        int l = 256;
        int d = 1;
        byte[] A = new byte[0];
        byte[] I = new byte[0];
        encryptionService.init(l, d, A, I);

        encryptionKey = HexEncoder.generateRandomHexString(System.getenv("INITIAL_ENCRYPTION_KEY").length());
        log.info("encryption key: " + encryptionKey);
    }

    public void turnOn() throws IOException {
        log.info("onn command sent");
        if (encryptionEnabled) {
            sendEncryptedMessage("onn");
        } else {
            sendMessage("onn");
        }
    }

    public void turnOff() throws IOException {
        log.info("off command sent");
        if (encryptionEnabled) {
            sendEncryptedMessage("off");
        } else {
            sendMessage("off");
        }
    }

    public void sendEncryptionKey() throws IOException {
        byte[] K = HexEncoder.decode(System.getenv("INITIAL_ENCRYPTION_KEY"));
        byte[] encryptionKeyBytes = HexEncoder.decode(encryptionKey);
        EncryptionResult encryptionResult = encryptionService.authEncrypt(K, encryptionKeyBytes);
        String message = new String(Base64.getEncoder().encode(HexEncoder.encode(encryptionResult.getY()).getBytes()));
        String mac = new String(Base64.getEncoder().encode(HexEncoder.encode(encryptionResult.getT()).getBytes()));

        FormBody body = new FormBody.Builder()
                .add("param1", message)
                .add("param2", mac)
                .build();
        Request request = new Request.Builder()
                .url("http://" + LUMP_STATIC_IP_ADDRESS + "/key")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() == HTTP_SUCCESS_CODE) {
            log.info("encryption key has been successfully delivered");
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
        byte[] K = HexEncoder.reverseAndDecode(System.getenv("INITIAL_ENCRYPTION_KEY"));
        byte[] X = state.getBytes(StandardCharsets.UTF_8);

        EncryptionResult encryptionResult = encryptionService.authEncrypt(K, X);

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
        Response response = client.newCall(request).execute();
        if (response.code() == HTTP_SUCCESS_CODE) {
            log.info("command has been successfully processed");
        }
    }
}
