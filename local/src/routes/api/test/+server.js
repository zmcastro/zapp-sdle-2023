import { GCounter } from "$lib/gcounter.js";
import { PNCounter } from "$lib/pncounter.js";
import { AWORMap } from "$lib/awormap.js";
import { ShoppingList } from "$lib/shoppinglist.js";
import { Product } from "$lib/product.js";

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

    for (const product of sl2.getProducts().values()) {
        console.log(product.getName(), product.value());
    }

    sl2.join(sl1);
    console.log("------------------");

    for (const product of sl2.getProducts().values()) {
        console.log(product.getName(), product.value());
        console.log(product.getCounter().getP(), product.getCounter().getN());
    }

    const sl3_json = {
        id: "3",
        name: "sl3",
        products: {
            map: [
                {
                    name: "bananas",
                    context: "1",
                    counter: {
                        p: [
                            ["1", "0"],
                            ["2", "2"],
                        ],
                        n: [
                            ["1", "0"],
                            ["2", "1"],
                        ],
                    },
                },
                {
                    name: "oranges",
                    context: "1",
                    counter: {
                        p: [
                            ["1", "1"],
                            ["2", "0"],
                        ],
                        n: [
                            ["1", "1"],
                            ["2", "0"],
                        ],
                    },
                },
                {
                    name: "bananas",
                    context: "2",
                    counter: {
                        p: [
                            ["1", "0"],
                            ["2", "3"],
                        ],
                        n: [
                            ["1", "0"],
                            ["2", "1"],
                        ],
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
            product.getCounter().getP(),
            product.getCounter().getN(),
        );
    }

    console.log("------------------");

    const res = sl3.toJSON();

    console.log(JSON.stringify(res));

    return new Response(JSON.stringify(sl3.toFrontendJSON()));
}
