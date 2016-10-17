package co.ecso.dacato.database;

import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.config.ConfigGetter;
import co.ecso.dacato.database.query.EntityFinder;
import co.ecso.dacato.database.query.Updater;
import co.ecso.dacato.database.querywrapper.DatabaseResultField;
import co.ecso.dacato.database.querywrapper.SingleColumnQuery;
import co.ecso.dacato.database.querywrapper.SingleColumnUpdateQuery;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * DatabaseEntity.
 *
 * @param <T> Type of the auto_inc (primary) field of this entity, usually Long.
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 08.08.16
 */
public interface DatabaseEntity<T> extends ConfigGetter {
    /**
     * Get primary key.
     *
     * @return Primary key.
     */
    T primaryKey();

    /**
     * Save.
     *
     * @param columnValuesToSet Column and values to save.
     * @return DatabaseEntity of type T.
     */
    CompletableFuture<? extends DatabaseEntity<T>> save(final ColumnList columnValuesToSet);

    /**
     * Default statement options.
     *
     * @return Options for all statements.
     */
    default int statementOptions() {
        return -1;
    }

    /**
     * Wrapper for updater().update, usually called within save().
     *
     * @param query         Query to execute.
     * @param validityCheck Validity check callback.
     * @return Number of affected rows.
     */
    default CompletableFuture<Integer> update(final SingleColumnUpdateQuery<T> query,
                                              final Callable<AtomicBoolean> validityCheck) {
        return updater().update(query, validityCheck);
    }

    /**
     * Get updater.
     *
     * @return Updater.
     */
    default Updater<T> updater() {
        return new Updater<T>() {
            @Override
            public int statementOptions() {
                return DatabaseEntity.this.statementOptions();
            }

            @Override
            public ApplicationConfig config() {
                return DatabaseEntity.this.config();
            }
        };
    }

    /**
     * Get entity finder.
     *
     * @return EntityFinder.
     */
    default EntityFinder entityFinder() {
        return new EntityFinder() {
            @Override
            public int statementOptions() {
                return DatabaseEntity.this.statementOptions();
            }

            @Override
            public ApplicationConfig config() {
                return DatabaseEntity.this.config();
            }
        };
    }

    /**
     * Find one entry.
     *
     * @param query         Query to execute.
     * @param validityCheck Validity check callback.
     * @param <S>           Type to select, p.e. Long.
     * @param <W>           Type of where, p.e. String.
     * @return DatabaseResultField of type s.
     */
    default <S, W> CompletableFuture<DatabaseResultField<S>> findOne(final SingleColumnQuery<S, W> query,
                                                                     final Callable<AtomicBoolean> validityCheck) {
        return this.entityFinder().findOne(query, validityCheck);
    }
}
