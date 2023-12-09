import { get } from "idb-keyval";
export const ssr = false;

export async function load() {
    const uuid = await get("uuid");
    console.log("uuid", uuid);
    return {uuid: uuid};
}
