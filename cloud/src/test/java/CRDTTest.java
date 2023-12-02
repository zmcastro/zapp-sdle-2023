
import org.junit.jupiter.api.Test;
import server.model.Product;
import server.model.ShoppingList;
import server.model.crdts.CCounter;
import server.model.crdts.GCounter;
import server.model.crdts.PNCounter;

import static org.junit.jupiter.api.Assertions.*;

class CRDTTest {

    @Test
    public void testGCounter() {
        GCounter x = new GCounter("x");
        GCounter y = new GCounter("y");
        GCounter z = new GCounter("z");

        x.inc(1);
        x.inc(1);
        y.inc(2);
        z.join(x);
        z.join(y);

        assertEquals(4, z.read());

        x.inc(2);
        z.inc(2);
        z.join(x);
        z.join(x);

        assertEquals(8, z.read());
    }

    @Test
    public void testPNCounter() {
        PNCounter x = new PNCounter("x");
        PNCounter y = new PNCounter("y");

        x.inc(4);
        x.dec(1);
        y.dec(1);

        System.out.println(x.read());
        System.out.println(y.read());
        assertNotEquals(x.read(), y.read()); // 3 != 1

        x.join(y);
        y.join(x);

        System.out.println(x.read());
        System.out.println(y.read());
        assertEquals(x.read(), y.read()); // 2 == 2
    }

    @Test
    public void testCCounter() {
        CCounter cc1 = new CCounter("1");
        CCounter cc2 = new CCounter("1");
        CCounter cc3 = new CCounter("3");

        cc1.inc(2);
        cc2.inc(2);

        System.out.println(cc1.read() + ", " + cc2.read());

        cc2.setID("2");
        cc1.inc(1);
        cc2.dec(2);

        System.out.println(cc1.read() + ", " + cc2.read());

        System.out.println("cc1 " + cc1.read() + ", " + cc1.getMap() + ", " + cc1.getCC().getCC());
        System.out.println("cc2 " + cc2.read() + ", " + cc2.getMap() + ", " + cc2.getCC().getCC());

        System.out.println("------------------");

        cc1.join(cc2);

        System.out.println("cc1 " + cc1.read() + ", " + cc1.getMap() + ", " + cc1.getCC().getCC());

        System.out.println("------------------");

        cc3.dec(2);
        cc3.join(cc1);

        System.out.println("cc3 " + cc3.read() + ", " + cc3.getMap() + ", " + cc3.getCC().getCC());
    }

    /* this tests basically everything, but specifically the AWOR map */
    @Test
    public void testShoppingList() {
        ShoppingList sl1 = new ShoppingList("1", "sl1");
        ShoppingList sl2 = new ShoppingList("1", "sl2");

        Product p1 = new Product("bananas", "1");
        Product p2 = new Product("oranges", "1");

        Product p1_ = new Product("bananas", "1");
        Product p2_ = new Product("hammer", "1");

        sl1.addProduct(p1);
        sl1.addProduct(p2);

        sl1.incProduct("bananas", 3);
        for (Product product : sl1.getProducts().values()) {
            System.out.println(product.getName() + ", " + product.value());
        }
        System.out.println("-> Bananas should have 3");

        sl1.decProduct("bananas", 1);
        for (Product product : sl1.getProducts().values()) {
            System.out.println(product.getName() + ", " + product.value());
        }
        System.out.println("-> Bananas should have 2");

        System.out.println("\n------------------\n");

        sl2.addProduct(p1_);
        sl2.addProduct(p2_);
        sl2.incProduct("bananas", 1);
        sl2.removeProduct(p2_.getName());
        sl2.addProduct(p2_);

        System.out.println(sl2.getProducts().getCC().getCC() + ", " + sl2.getProducts().getMap());

        for (Product product : sl2.getProducts().values()) {
            System.out.println(product.getName() + ", " + product.value());
        }

        sl2.join(sl1);
        System.out.println("\n------------------\n");
        System.out.println(sl2.getProducts().getCC().getCC() + ", " + sl2.getProducts().getMap());

        for (Product product : sl2.getProducts().values()) {
            System.out.println(product.getName() + ", " + product.value());
        }
    }

    @Test
    void testJSON() {
        ShoppingList list = new ShoppingList("1", "test");
        list.fromJSON("{\"id\":\"3\",\"name\":\"sl3\",\"products\":{\"map\":[{\"name\":\"oranges\",\"context\":\"1\",\"counter\":{\"p\":[[\"1\",\"1\"],[\"2\",\"0\"],],n:[[\"1\",\"1\"],[\"2\",\"0\"],],},},{\"name\":\"bananas\",\"context\":\"2\",\"counter\":{\"p\":[[\"1\",\"0\"],[\"2\",\"3\"],],\"n\":[[\"1\",\"0\"],[\"2\",\"1\"],],},},],\"context\":{\"cc\":[[\"oranges\",\"1\"],[\"bananas\",\"2\"],],\"dc\":[],},},};");

        System.out.println(list.getName());
        System.out.println(list.getProducts());
    }
}

