package util;

public class Timer {

    private float start;

    public Timer() {
        start = (float) (System.nanoTime() / 1e9);
    }

    public float getElapsedTime() {
        float end = (float) (System.nanoTime() / 1e9);
        return end - start;
    }

    public void restart() {
        start = (float) (System.nanoTime() / 1e9);
    }

}
