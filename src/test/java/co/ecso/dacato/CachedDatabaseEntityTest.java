package co.ecso.dacato;

import co.ecso.dacato.database.query.DatabaseField;
import co.ecso.dacato.helpers.CachedCustomer;
import co.ecso.dacato.helpers.CachedCustomers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * CachedDatabaseEntityTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 06.09.16
 */
public final class CachedDatabaseEntityTest extends AbstractTest {

    private CachedCustomer customer;

    @Before
    public void setUp() throws Exception {
        this.setUpDatabase();
        this.customer = new CachedCustomers(APPLICATION_CONFIG).create("firstName", 1234L).get(10, TimeUnit.SECONDS);
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupDatabase();
    }

    @Test
    public void testId() throws Exception {
        Assert.assertEquals(Long.valueOf(0L), this.customer.primaryKey());
    }

    @Test
    public void testFirstName() throws Exception {
        Assert.assertEquals("firstName", this.customer.firstName().get().resultValue());
    }

    @Test
    public void testNumber() throws Exception {
        Assert.assertEquals(Long.valueOf(1234L), this.customer.number().get().resultValue());
    }

    @Test
    public void testSave() throws Exception {
        final Map<DatabaseField<?>, Object> map = new HashMap<>();
        map.put(CachedCustomer.Fields.FIRST_NAME, "foobar1");
        final CachedCustomer newCustomer = this.customer.save(() -> map)
                .get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);
        Assert.assertEquals(customer.primaryKey(), this.customer.primaryKey());
        Assert.assertEquals("foobar1", newCustomer.firstName().get(10, TimeUnit.SECONDS).resultValue());
        this.customer = newCustomer;
    }
}