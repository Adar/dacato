package co.ecso.dacato.h2;

import co.ecso.dacato.database.querywrapper.DatabaseField;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * HTwoDatabaseEntityTest.
 *
 * @author Christian Scharmach (cs@e-cs.co)
 * @version $Id:$
 * @since 03.09.16
 */
public final class HTwoDatabaseEntityTest extends AbstractHTwoTest {

    private HTwoCustomer customer = null;

    @Before
    public void setUp() throws Exception {
        this.setUpHTwoDatabase();
        this.customer = new HTwoCustomers(new HTwoTestApplicationConfig()).create("firstName", 1234L)
                .get(10, TimeUnit.SECONDS);
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupHTwoDatabase();
    }

    @Test
    public void testId() throws Exception {
        Assert.assertEquals(Long.valueOf(1L), this.customer.primaryKey());
    }

    @Test
    public void testFirstName() throws Exception {
        Assert.assertEquals("firstName", this.customer.firstName().get(10, TimeUnit.SECONDS).resultValue());
    }

    @Test
    public void testNumber() throws Exception {
        Assert.assertEquals(Long.valueOf(1234), this.customer.number().get(10, TimeUnit.SECONDS).resultValue());
    }

    @Test(expected = ExecutionException.class)
    public void testSave() throws Exception {
        final Long id = this.customer.primaryKey();
        Map<DatabaseField<?>, Object> map = new HashMap<>();
        map.put(HTwoCustomer.Fields.FIRST_NAME, "foo1");
        this.customer.save(() -> map).get(5, TimeUnit.SECONDS);

        this.customer = new HTwoCustomers(new HTwoTestApplicationConfig()).findOne(id).get(10, TimeUnit.SECONDS);
        Assert.assertEquals("foo1", this.customer.firstName().get(10, TimeUnit.SECONDS).resultValue());

        map.clear();
        map.put(HTwoCustomer.Fields.FIRST_NAME, "foo2");
        this.customer.save(() -> map).get(5, TimeUnit.SECONDS);

        Assert.assertEquals("foo2", this.customer.firstName().get(10, TimeUnit.SECONDS).resultValue());
    }
}
