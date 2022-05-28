package by.bsu.biot;

import by.bsu.biot.util.BenchmarkUtil;

public class Runner {

    static {
        System.loadLibrary("LibraryNative");
    }

    public static void main(String[] args) {
        new BenchmarkUtil().runBenchmark();
    }
}
