package by.bsu.biot;

import by.bsu.biot.util.BenchmarkUtil;

import java.io.IOException;

public class Runner {

    static {
        System.loadLibrary("LibraryNative");
    }

    public static void main(String[] args) throws IOException {
        BenchmarkUtil benchmarkUtil = new BenchmarkUtil();
        benchmarkUtil.runSingleOperationBenchmark();
        benchmarkUtil.runProtocolBenchmark();
    }
}
