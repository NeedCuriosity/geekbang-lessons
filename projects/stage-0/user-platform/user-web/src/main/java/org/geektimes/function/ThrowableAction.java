package org.geektimes.function;

/**
 * @author zhouzy
 * @since 2021-03-08
 */
@FunctionalInterface
public interface ThrowableAction {

    void execute() throws Throwable;

    static void execute(ThrowableAction action) throws RuntimeException {
        try {
            action.execute();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
