package fr.poulpogaz.nonogramssolver.reader;

import java.net.http.HttpClient;

public class HttpUtils {

    public static final HttpClient CLIENT = HttpClient.newBuilder().build();
}
