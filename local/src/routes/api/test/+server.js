import { json } from '@sveltejs/kit';
import { GCounter } from "$lib/g-counter.js";
import { PNCounter } from "$lib/pn-counter.js";

export async function POST({ request }) {
	// testing gcounter
	let x = new GCounter("x");
	let y = new GCounter("y");
	let z = new GCounter("z");

	x.inc(); x.inc();
	y.inc(2);
	z.join(x); z.join(y);
	
	console.log("should be 4: ", z.read()); // 4
	
	x.inc(2);
	z.inc(2);
	z.join(x);
	z.join(x);
  
	console.log("should be 8: ", z.read()); // 8

	// testing pncounter
	x = new PNCounter("x");
	y = new PNCounter("y");

	x.inc(4); x.dec();
	y.dec();

	console.log(x.read() != y.read()); // different values 3 != -1

	x.join(y); y.join(x);
	
	console.log(x.read() == y.read()); // same values 2 == 2

	return json("hi");
}