package com.oficial.rtlib;

public class AmdahlCalculator {
    public static double speedup(double f, int N) {
        return 1.0 / ((1.0 - f) + (f / N));
    }
}
