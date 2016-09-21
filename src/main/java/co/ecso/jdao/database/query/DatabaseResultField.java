package co.ecso.jdao.database.query;

import java.util.Objects;

/**
 * DatabaseResultField.
 *
 * @param <T> Field type, p.e. Long or String.
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 09.09.16
 */
public final class DatabaseResultField<T> {

    private final DatabaseField<T> selectedField;
    private final T resultValue;

    /**
     * Construct.
     *
     * @param selectedField Selected field.
     * @param resultValue Result resultValue.
     */
    public DatabaseResultField(final DatabaseField<T> selectedField, final T resultValue) {
        this.selectedField = selectedField;
        this.resultValue = resultValue;
    }

    /**
     * Selected field.
     *
     * @return Selected field.
     */
    public DatabaseField<T> selectedField() {
        return selectedField;
    }

    /**
     * Result value.
     *
     * @return Result value.
     */
    public T resultValue() {
        return this.resultValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DatabaseResultField<?> that = (DatabaseResultField<?>) o;
        return Objects.equals(selectedField, that.selectedField) &&
                Objects.equals(resultValue, that.resultValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selectedField, resultValue);
    }
}