package co.ecso.dacato.cassandra;

import co.ecso.dacato.AbstractTest;
import co.ecso.dacato.config.ApplicationConfig;
import co.ecso.dacato.database.CachedDatabaseTable;
import co.ecso.dacato.database.cache.Cache;
import co.ecso.dacato.database.querywrapper.InsertQuery;
import co.ecso.dacato.database.querywrapper.SingleColumnQuery;
import co.ecso.dacato.database.querywrapper.TruncateQuery;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * HSQLCachedCustomers.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @since 17.09.16
 */
final class CassandraCachedCustomers implements CachedDatabaseTable<Long, CassandraCachedCustomer> {

    private final ApplicationConfig config;

    CassandraCachedCustomers(final ApplicationConfig config) {
        this.config = config;
    }

    public CompletableFuture<CassandraCachedCustomer> create(final String firstName, final long number) {
        final InsertQuery<Long> query = new InsertQuery<>(CassandraCachedCustomer.TABLE_NAME,
                "INSERT INTO customer (%s, %s) VALUES (?, ?)", CassandraCachedCustomer.Fields.ID);
        query.add(CassandraCachedCustomer.Fields.FIRST_NAME, firstName);
        query.add(CassandraCachedCustomer.Fields.NUMBER, number);
        return this.add(query).thenApply(newId -> new CassandraCachedCustomer(config, newId.resultValue()));
    }

    public CompletableFuture<Boolean> removeAll() {
        return truncate(new TruncateQuery<>(CassandraCachedCustomer.TABLE_NAME, "TRUNCATE TABLE customer"));
    }

    @Override
    public CompletableFuture<CassandraCachedCustomer> findOne(final Long primaryKey) {
        return this.findOne(new SingleColumnQuery<>(CassandraCachedCustomer.TABLE_NAME, "SELECT %s FROM customer WHERE %s = ?",
                CassandraCachedCustomer.Fields.ID, CassandraCachedCustomer.Fields.ID, primaryKey)).thenApply(foundId ->
                new CassandraCachedCustomer(config, foundId.resultValue()));
    }

    @Override
    public CompletableFuture<List<CassandraCachedCustomer>> findAll() {
        return this.findAll(CassandraCachedCustomer.TABLE_NAME, new SingleColumnQuery<>(CassandraCachedCustomer.TABLE_NAME,
                "SELECT %s FROM customer", CassandraCachedCustomer.Fields.ID))
                .thenApply(list -> list.stream().map(foundId ->
                        new CassandraCachedCustomer(config, foundId.resultValue())).collect(Collectors.toList()));
    }

    @Override
    public ApplicationConfig config() {
        return config;
    }

    @Override
    public Cache cache() {
        return AbstractTest.CACHE;
    }
}
