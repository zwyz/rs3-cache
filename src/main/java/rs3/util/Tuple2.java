package rs3.util;

public record Tuple2<A, B>(A a, B b) {
    public String toString() {
        return "(" + a + ", " + b + ")";
    }
}
