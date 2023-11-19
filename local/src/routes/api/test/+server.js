import { json } from "@sveltejs/kit";
import { GCounter } from "$lib/gcounter.js";
import { PNCounter } from "$lib/pncounter.js";
import { AWORMap } from "$lib/awormap.js";

export async function POST({ request }) {
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

    return json("hi");
}
