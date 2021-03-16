package org.geektimes.immutable;

/**
 * @author zhouzy
 * @since 2021-03-17
 */
public class Pair<L, R> {

    private L left;
    private R right;

    private Pair() {
    }

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }
}
