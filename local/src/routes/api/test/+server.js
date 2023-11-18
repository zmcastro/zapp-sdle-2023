import { json } from '@sveltejs/kit';
import { GCounter } from "$lib/g-counter.js";

export async function POST({ request }) {
	const x = new GCounter("x");
	const y = new GCounter("y");
	const z = new GCounter("z");

	x.inc(); x.inc();
	y.inc(2);
	z.join(x); z.join(y);
	
	console.log("should be 4: ", z.read()); // 4
	
	x.inc(2);
	z.inc(2);
	z.join(x);
	z.join(x);
  
	console.log("should be 8: ", z.read()); // 8
	console.log(z.get());
	return json("hi");
}