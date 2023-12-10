import { get, set } from "idb-keyval";
import { ShoppingList } from "$lib/shoppinglist.js";

export const ssr = false;

export async function load({ fetch, params }) {
    try {
        const res = await fetch(`http://localhost:9999/${params.id}`, {
            method: "GET",
        });
        let sl = await res.json();
        const localJSON = await get(params.id);

        if (res.ok && localJSON) {
            console.log("Remote and local");
            const remote = new ShoppingList();
            const local = new ShoppingList();

            local.fromJSON(localJSON);
            remote.fromJSON(sl);

            local.join(remote);
            set(params.id, local.toJSON());

            sl = local.toJSON();

            return { sl: sl, id: params.id };
        } else if (res.ok && !localJSON) {
            console.log("Remote only");
            set(params.id, sl);

            return { sl: sl, id: params.id };
        } else {
            throw new Error("No data");
        }
    } catch {
        console.log("Local only");
        const localJSON = await get(params.id);
        if (localJSON === undefined) {
            throw new Error("No data");
        }
        return { sl: localJSON, id: params.id };
    }
}
