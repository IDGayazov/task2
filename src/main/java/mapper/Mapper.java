package mapper;

/**
 * Generic Mapper interface for transforming an object of type T to an object of type U.
 *
 * @param <T> the type of the input object
 * @param <U> the type of the output object
 */
public interface Mapper<T, U> {

    /**
     * Transforms an object of type T to an object of type U.
     *
     * @param value the input object to be transformed
     * @return the transformed object of type U
     */
    U map(T value);
}

