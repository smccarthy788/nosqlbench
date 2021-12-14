package io.nosqlbench.virtdata.library.basics.shared.from_long.to_long;

import io.nosqlbench.nb.api.errors.BasicError;
import io.nosqlbench.virtdata.api.annotations.ThreadSafeMapper;

import java.util.function.Function;
import java.util.function.LongUnaryOperator;

/**
 * Return a value within a range, pseudo-randomly. This is equivalent to
 * returning a value with in range between 0 and some maximum value, but
 * with a minimum value added.
 *
 * You can specify hash ranges as small as a single-element range, like
 * (5,5), or as wide as the relevant data type allows.
 */
@ThreadSafeMapper
public class HashRangeFunc implements LongUnaryOperator {

    private final Function<Long, Long> minFunc;
    private final Function<Long, Long> maxFunc;
    private final Hash hash = new Hash();

    public HashRangeFunc(Function<Long, Long> minFunc, Function<Long, Long> maxFunc)
    {
        this.minFunc = minFunc;
        this.maxFunc = maxFunc;
    }

    @Override
    public long applyAsLong(long operand) {
        long minValue = minFunc.apply(operand);
        long maxValue = maxFunc.apply(operand);

        long width = (minValue - maxValue)+1;

        return minValue + (hash.applyAsLong(operand) % width);
    }
}
