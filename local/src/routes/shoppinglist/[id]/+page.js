export const ssr = false;

export async function load({ fetch, params }) {
    // Simulate receiving a JSON ShoppingList
    const res = await fetch("/api/test", {
        method: "GET",
    });
    const sl = await res.json();

    return { sl: sl, id: params.id };
}
