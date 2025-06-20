package axxcee.shulkerboxdrop.models;

public class PlayerCounter {
    private int count = 0;

    public int increment() {
        return ++count;
    }

    public void reset() {
        count = 0;
    }

    public int getCount() {
        return count;
    }
}