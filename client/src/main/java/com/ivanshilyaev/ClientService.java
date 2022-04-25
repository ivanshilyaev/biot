package com.ivanshilyaev;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ClientService {

    public void turnOn() throws IOException, InterruptedException {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://192.168.100.93/led?state=1"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void turnOff() throws IOException, InterruptedException {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://192.168.100.93/led?state=0"))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
