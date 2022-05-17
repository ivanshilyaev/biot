package by.bsu.biot.service;

import by.bsu.biot.dto.EncryptionResult;
import by.bsu.biot.util.HexEncoder;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Service
public class ClientService {

    private static final String LUMP_STATIC_IP_ADDRESS = "192.168.100.93";

    private static final OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public void turnOn() throws IOException {
        sendMessage("onn");
    }

    public void turnOff() throws IOException {
        sendMessage("off");
    }

    private void sendMessage(String message) throws IOException {
        int l = 256;
        int d = 1;
        byte[] A = new byte[16];
        byte[] K = HexEncoder.reverseAndDecode("5BE3D61217B96181FE6786AD716B890B5CB0C0FF33C356B835C405AED8E07F99");
        byte[] X = message.getBytes(StandardCharsets.UTF_8);
        byte[] I = new byte[49];

        AuthenticatedEncryptionService service = new AuthenticatedEncryptionService(l, d);
        EncryptionResult encryptionResult = service.authEncrypt(A, K, I, X);

        new ClientService().switchLamp(
                new String(Base64.getEncoder().encode(HexEncoder.encode(encryptionResult.getY()).getBytes())),
                new String(Base64.getEncoder().encode(HexEncoder.encode(encryptionResult.getT()).getBytes()))
        );
    }

    public void switchLamp(String message, String mac) throws IOException {
        FormBody body = new FormBody.Builder()
                .add("message", message)
                .add("mac", mac)
                .build();
        Request request = new Request.Builder()
                .url("http://" + LUMP_STATIC_IP_ADDRESS + "/led")
                .post(body)
                .build();
        client.newCall(request).execute();
    }
}
