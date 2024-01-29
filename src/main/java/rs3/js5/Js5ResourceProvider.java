package rs3.js5;

public interface Js5ResourceProvider extends AutoCloseable {
    byte[] get(int archive, int group);
}
