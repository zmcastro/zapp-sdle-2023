import { entries } from "idb-keyval";
export const ssr = false;

export async function load() {
    
    let res = [];
    let values = await entries();
    for (const [key, value] of values) {
        if (key !== "uuid") {
            res.push([key, value]);
        }
    }
    
    return {sl: res};
}
