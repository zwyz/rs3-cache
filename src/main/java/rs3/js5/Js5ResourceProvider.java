package rs3.js5;

public interface Js5ResourceProvider {
    byte[] get(int archive, int group, boolean urgent, int priority);
}
