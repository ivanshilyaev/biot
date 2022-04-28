import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ClientService {

    private static final OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public void turnOn() throws IOException {
        Request request = new Request.Builder()
                .url("http://192.168.100.93/led?state=1")
                .get()
                .build();
        client.newCall(request).execute();
    }

    public void turnOff() throws IOException {
        Request request = new Request.Builder()
                .url("http://192.168.100.93/led?state=0")
                .get()
                .build();
        client.newCall(request).execute();
    }
}
