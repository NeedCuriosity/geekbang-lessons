package org.geektimes.immutable;

/**
 * @author zhouzy
 * @since 2021-03-17
 */
public class Triple<L, M, R> {

    private L left;
    private M middle;
    private R right;

    private Triple(L left, M middle, R right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    public static <L, M, R> Triple<L, M, R> of(L left, M middle, R right) {
        return new Triple<>(left, middle, right);
    }

    public static <L, M, R> Triple<L, M, R> of() {
        return new Triple<>(null,null,null);
    }

    public L getLeft() {
        return left;
    }

    public M getMiddle() {
        return middle;
    }

    public R getRight() {
        return right;
    }
}
