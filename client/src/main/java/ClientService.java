import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ClientService {

    private static final OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

//    public void turnOn() throws IOException {
//        switchLamp("1");
//    }
//
//    public void turnOff() throws IOException {
//        switchLamp("0");
//    }

    public void switchLamp(String message, String mac) throws IOException {
        FormBody body = new FormBody.Builder()
                .add("message", message)
                .add("mac", mac)
                .build();
        Request request = new Request.Builder()
                .url("http://192.168.100.93/led")
                .post(body)
                .build();
        client.newCall(request).execute();
    }
}
