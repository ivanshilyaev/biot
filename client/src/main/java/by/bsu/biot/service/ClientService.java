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

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final EncryptionAndHashService encryptionService;

    @Value("${biot.encryption.enabled}")
    private boolean encryptionEnabled;

    private String encryptionKey;

    private int messageCount;

    private static final String LUMP_STATIC_IP_ADDRESS = "192.168.100.93";

    private static final String LED_PATH = "/led";

    private static final String KEY_PATH = "/key";

    private static final int HTTP_SUCCESS_CODE = 200;

    private static final OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    @PostConstruct
    public void init() {
        int l = 256;
        int d = 1;
        byte[] I = new byte[0];
        encryptionService.init(l, d, I);
        messageCount = 0;

        encryptionKey = HexEncoder.generateRandomHexString(System.getenv("INITIAL_ENCRYPTION_KEY").length());
        log.info("encryption key: " + encryptionKey);
    }

    public void turnOn() throws IOException {
        log.info("onn command sent");
        if (encryptionEnabled) {
            sendEncryptedMessage("onn", encryptionKey, LED_PATH);
        } else {
            sendPostRequest("onn", "", LED_PATH);
        }
    }

    public void turnOff() throws IOException {
        log.info("off command sent");
        if (encryptionEnabled) {
            sendEncryptedMessage("off", encryptionKey, LED_PATH);
        } else {
            sendPostRequest("off", "", LED_PATH);
        }
    }

    public void sendEncryptionKey() throws IOException {
        sendEncryptedMessage(encryptionKey, System.getenv("INITIAL_ENCRYPTION_KEY"), KEY_PATH);
    }

    private void sendEncryptedMessage(String message, String key, String path) throws IOException {
        byte[] A = String.valueOf(++messageCount).getBytes();
        byte[] K = HexEncoder.decode(key);
        byte[] X = message.getBytes(StandardCharsets.UTF_8);

        EncryptionResult encryptionResult = encryptionService.authEncrypt(A, K, X);

        log.info("encrypted message: " + HexEncoder.encode(encryptionResult.getY()));
        log.info("mac: " + HexEncoder.encode(encryptionResult.getT()));
        String encryptedMessage = new String(
                Base64.getEncoder().encode(HexEncoder.encode(encryptionResult.getY()).getBytes()));
        String mac = new String(
                Base64.getEncoder().encode(HexEncoder.encode(encryptionResult.getT()).getBytes()));

        Response response = sendPostRequest(encryptedMessage, mac, path);
        if (response.code() == HTTP_SUCCESS_CODE) {
            log.info("message has been successfully processed");
            log.info("count: " + ++messageCount);
        }
    }

    private Response sendPostRequest(String param1, String param2, String path) throws IOException {
        FormBody body = new FormBody.Builder()
                .add("param1", param1)
                .add("param2", param2)
                .build();
        Request request = new Request.Builder()
                .url("http://" + LUMP_STATIC_IP_ADDRESS + path)
                .post(body)
                .build();

        return client.newCall(request).execute();
    }
}
