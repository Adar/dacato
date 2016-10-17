package co.ecso.dacato.hsql;

import co.ecso.dacato.database.querywrapper.DatabaseField;
import co.ecso.dacato.database.querywrapper.DatabaseResultField;
import co.ecso.dacato.helpers.Products;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * HSQLDatabaseTableTest.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 03.09.16
 */
public final class HSQLDatabaseTableTest extends AbstractHSQLTest {

    private HSQLCustomers customers = null;

    @Before
    public void setUp() throws Exception {
        this.setUpHSQLDatabase();
        this.customers = new HSQLCustomers(new HSQLTestApplicationConfig());
    }

    @After
    public void tearDown() throws Exception {
        this.cleanupHSQLDatabase();
    }

    @Test
    public void add() throws Exception {
        new Products(new HSQLTestApplicationConfig()).add(100, 10, 0, 0, 0, 0, 0, "image1", 1.0F, 0.0F, new Date(),
                new Date(), new Date(), 10.0F, 1, 1, 1, 1.0F).get(10, TimeUnit.SECONDS);
    }

    @Test
    public void testAdd() throws Exception {
        final HSQLCustomer newCustomer = this.customers.create("foo1", 12345L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);
        Assert.assertEquals("foo1", newCustomer.firstName().get().resultValue());
        final HSQLCustomer newCustomer2 = this.customers.create("foo2", 12345L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer2);
        Assert.assertEquals("foo2", newCustomer2.firstName().get().resultValue());
        Assert.assertNotEquals(newCustomer.primaryKey(), newCustomer2.primaryKey());
    }

    @Test
    public void testFindOne() throws Exception {
        final HSQLCustomer newCustomer = this.customers.create("foo1", 12345L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);

        final HSQLCustomer foundCustomer = this.customers.findOne(newCustomer.primaryKey()).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(foundCustomer);
        Assert.assertEquals("foo1", foundCustomer.firstName().get(10, TimeUnit.SECONDS).resultValue());
        Assert.assertEquals(Long.valueOf(12345L), foundCustomer.number().get(10, TimeUnit.SECONDS).resultValue());
    }

    @Test
    public void testFindOneByFirstName() throws Exception {
        final HSQLCustomer newCustomer = this.customers.create("foo1", 12345L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);

        final HSQLCustomer foundCustomer = this.customers.findOneByFirstName(newCustomer.firstName()
                .get(10, TimeUnit.SECONDS)
                .resultValue()).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(foundCustomer);
        Assert.assertEquals("foo1", foundCustomer.firstName().get(10, TimeUnit.SECONDS).resultValue());
        Assert.assertEquals(Long.valueOf(12345L), foundCustomer.number().get(10, TimeUnit.SECONDS).resultValue());
    }

    @Test
    public void testFindFirstNameAndLastNameById() throws Exception {
        final HSQLCustomer newCustomer = this.customers.create("foo1", 12345L).get(10, TimeUnit.SECONDS);
        final HSQLCustomer newCustomer2 = this.customers.create("foo1", 1235L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);
        Assert.assertNotNull(newCustomer2);

        final Map<DatabaseField, DatabaseResultField> firstName = this.customers.
                findFirstNameById(newCustomer.primaryKey()).get(10, TimeUnit.SECONDS);
        Assert.assertEquals(1, firstName.size());
        Assert.assertEquals("foo1", firstName.get(HSQLCustomer.Fields.FIRST_NAME).resultValue());
    }

    @Test
    public void testFindManyFirstNameAndLastNameById() throws Exception {
        final HSQLCustomer newCustomer = this.customers.create("foo1", 12345L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);
        final HSQLCustomer newCustomer2 = this.customers.create("foo2", 12346L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer2);

        final List<HSQLCustomer> all = this.customers.findAll().get(10, TimeUnit.SECONDS);
        Assert.assertEquals(2, all.size());

        final List<Map<DatabaseField, DatabaseResultField>> firstNameAndLastName = this.customers.
                findManyFirstName().get(10, TimeUnit.SECONDS);

        Assert.assertEquals(2, firstNameAndLastName.size());

        Assert.assertEquals("foo1", firstNameAndLastName.get(0).get(HSQLCustomer.Fields.FIRST_NAME).resultValue());
        Assert.assertEquals("foo2", firstNameAndLastName.get(1).get(HSQLCustomer.Fields.FIRST_NAME).resultValue());
    }

    @Test
    public void testRemoveOne() throws Exception {
        final HSQLCustomer newCustomer = this.customers.create("foo1", 12345L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer);
        final HSQLCustomer newCustomer2 = this.customers.create("foo2", 12346L).get(10, TimeUnit.SECONDS);
        Assert.assertNotNull(newCustomer2);

        final List<HSQLCustomer> all = this.customers.findAll().get(10, TimeUnit.SECONDS);
        Assert.assertEquals(2, all.size());

        final Integer count = this.customers.removeOne(newCustomer.primaryKey()).get(10, TimeUnit.SECONDS);
        Assert.assertEquals(Integer.valueOf(1), count);

        final List<HSQLCustomer> all2 = this.customers.findAll().get(10, TimeUnit.SECONDS);
        Assert.assertEquals(1, all2.size());
    }

    @Test
    public void testFindAllByFirstName() throws Exception {
        CompletableFuture.allOf(
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L),
                this.customers.create("foo2", 12345L),
                this.customers.create("foo2", 12345L)
        ).get(10, TimeUnit.SECONDS);

        final List<HSQLCustomer> foundCustomer = this.customers.findAllByFirstName("foo1").get(5, TimeUnit.SECONDS);

        Assert.assertNotNull(foundCustomer);
        Assert.assertEquals(3, foundCustomer.size());
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void testFindAll() throws Exception {
        CompletableFuture.allOf(
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L)
        ).get(10, TimeUnit.SECONDS);

        Assert.assertEquals(5, this.customers.findAll().get(5, TimeUnit.SECONDS).size());
    }

    @Test
    public void testFindOneByFirstNameAndLastName() throws Exception {
        CompletableFuture.allOf(
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L),
                this.customers.create("foo1", 12345L),
                this.customers.create("foo2", 12345L),
                this.customers.create("foo2", 12345L)
        ).get(10, TimeUnit.SECONDS);

        final HSQLCustomer found = this.customers.findOneByFirstName("foo1").get(5, TimeUnit.SECONDS);
        Assert.assertNotNull(found);
        Assert.assertEquals("foo1", found.firstName().get(10, TimeUnit.SECONDS).resultValue());
    }

    @Test
    public void testRemoveAll() throws Exception {
        this.customers.removeAll().get(10, TimeUnit.SECONDS);
        this.testFindAll();
        this.customers.removeAll().get(10, TimeUnit.SECONDS);
        Assert.assertEquals(0, this.customers.findAll().get(10, TimeUnit.SECONDS).size());
    }

}
