package com.potatohive.client.utils;

import java.util.Random;

public class RandomDelay {
    private static final Random rng = new Random();

    public static long gaussian(long mean) {
        return (long)(mean + rng.nextGaussian() * (mean / 4.0));
    }
}
