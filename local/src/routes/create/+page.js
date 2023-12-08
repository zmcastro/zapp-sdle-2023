import { v4 as uuidv4 } from "uuid";
import { ShoppingList } from "$lib/shoppinglist.js";
export const ssr = false;

export async function load({ fetch, params }) {
    const id = uuidv4();
    const name = "New Shopping List";
    let sl = new ShoppingList(id, name, false);
    sl = sl.toJSON();

    return { sl: sl };
}
