package whosthere.whosthere.db;

public interface Doer<T> {
    void doFromResult(T result);
}
