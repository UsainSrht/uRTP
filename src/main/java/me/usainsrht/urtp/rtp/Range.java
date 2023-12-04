package me.usainsrht.urtp.rtp;

import java.util.Random;

public class Range {

    private int min;
    private int max;

    public Range(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int next() {
        return new Random().nextInt(min, max+1);
    }

    public boolean isBetween(int value) {
        return value >= min && max >= value;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}
