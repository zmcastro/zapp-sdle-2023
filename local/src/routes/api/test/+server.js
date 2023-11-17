import { json } from '@sveltejs/kit';

export async function POST({ request }) {
	return json("hi");
}