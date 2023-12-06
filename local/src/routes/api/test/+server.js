import { GCounter } from "$lib/crdt/gcounter.js";
import { PNCounter } from "$lib/crdt/pncounter.js";
import { AWORMap } from "$lib/crdt/awormap.js";
import { ShoppingList } from "$lib/shoppinglist.js";
import { Product } from "$lib/product.js";
import { CCounter } from "../../../lib/crdt/ccounter.js";

export async function GET({ request }) {
    // testing gcounter
    let x = new GCounter("x");
    let y = new GCounter("y");
    let z = new GCounter("z");

    x.inc();
    x.inc();
    y.inc(2);
    z.join(x);
    z.join(y);

    console.log("should be 4: ", z.read()); // 4

    x.inc(2);
    z.inc(2);
    z.join(x);
    z.join(x);

    console.log("should be 8: ", z.read()); // 8

    // testing pncounter
    x = new PNCounter("x");
    y = new PNCounter("y");

    x.inc(4);
    x.dec();
    y.dec();

    console.log(x.read() != y.read()); // different values 3 != -1

    x.join(y);
    y.join(x);

    console.log(x.read() == y.read()); // same values 2 == 2

    // testing awormap
    x = new AWORMap("x");
    y = new AWORMap("y");

    x.add("x", "bananas");
    x.add("x", "apples");

    console.log(x.getMap(), x.getCC().getCC());

    y.add("y", "oranges");

    x.join(y);
    console.log("------------------");

    console.log(x.getMap(), x.getCC().getCC());

    x.add("y", "pears");

    console.log("------------------");

    console.log(x.getMap(), x.getCC().getCC());

    x.rm("y");

    console.log("------------------");

    console.log(x.getMap(), x.getCC().getCC());

    console.log("------------------");

    // shopping list

    let sl1 = new ShoppingList("1", "sl1");
    let sl2 = new ShoppingList("1", "sl2");

    let p1 = new Product("bananas", "1");
    let p2 = new Product("oranges", "1");

    let p1_ = new Product("bananas", "1");
    let p2_ = new Product("hammer", "1");

    sl1.addProduct(p1);
    sl1.addProduct(p2);

    sl1.incProduct("bananas", 3);

    for (const product of sl1.getProducts().values()) {
        console.log(product.getName(), product.value());
    }

    sl1.decProduct("bananas");

    for (const product of sl1.getProducts().values()) {
        console.log(product.getName(), product.value());
    }

    console.log("------------------");

    sl2.addProduct(p1_);
    sl2.addProduct(p2_);
    sl2.incProduct("bananas", 1);
    sl2.removeProduct(p2_.getName());
    sl2.addProduct(p2_);

    console.log(sl2.getProducts().getCC().getCC(), sl2.getProducts().getMap());

    for (const product of sl2.getProducts().values()) {
        console.log(product.getName(), product.value());
    }

    sl2.join(sl1);
    console.log("------------------");
    console.log(sl2.getProducts().getCC().getCC(), sl2.getProducts().getMap());

    for (const product of sl2.getProducts().values()) {
        console.log(product.getName(), product.value());
        // This is a debug print for PNCounters
        // console.log(product.getCounter().getP(), product.getCounter().getN());
    }

    const sl3_json = {
        id: "3",
        name: "sl3",
        remove: "true",
        products: {
            map: [
                {
                    name: "oranges",
                    context: "1",
                    counter: {
                        map: [
                            {
                                name: "1",
                                context: "1",
                                value: 1,
                            },
                            {
                                name: "2",
                                context: "2",
                                value: 1,
                            },
                        ],
                        context: {
                            cc: [
                                ["1", "1"],
                                ["2", "2"],
                            ],
                            dc: [],
                        },
                    },
                },
                {
                    name: "bananas",
                    context: "2",
                    counter: {
                        map: [
                            {
                                name: "1",
                                context: "1",
                                value: 2,
                            },
                            {
                                name: "2",
                                context: "2",
                                value: 1,
                            },
                        ],
                        context: {
                            cc: [
                                ["1", "1"],
                                ["2", "2"],
                            ],
                            dc: [],
                        },
                    },
                },
            ],
            context: {
                cc: [
                    ["oranges", "1"],
                    ["bananas", "2"],
                ],
                dc: [],
            },
        },
    };

    console.log("------------------");

    let sl3 = new ShoppingList();
    sl3.fromJSON(sl3_json);

    for (const product of sl3.getProducts().values()) {
        console.log(
            product.getName(),
            product.value(),
            // This is a debug print for PNCounters
            // product.getCounter().getP(),
            // product.getCounter().getN(),
        );
    }

    console.log("------------------");

    const res = sl3.toJSON();

    console.log(JSON.stringify(res));

    console.log("------------------");

    const cc1 = new CCounter("1");
    const cc2 = new CCounter("1");
    const cc3 = new CCounter("3");

    cc1.inc(2);
    cc2.inc(2);

    console.log(cc1.read(), cc2.read());

    cc2.setID("2");
    cc1.inc(1);
    cc2.dec(2);

    console.log(cc1.read(), cc2.read());

    console.log("cc1", cc1.read(), cc1.getMap(), cc1.getCC().getCC());
    console.log("cc2", cc2.read(), cc2.getMap(), cc2.getCC().getCC());
    cc1.join(cc2);
    console.log("cc1", cc1.read(), cc1.getMap(), cc1.getCC().getCC());

    console.log("------------------");

    cc3.dec(2);
    cc3.join(cc1);

    console.log("cc3", cc3.read(), cc3.getMap(), cc3.getCC().getCC());

    return new Response(JSON.stringify(sl3.toJSON()));
}
