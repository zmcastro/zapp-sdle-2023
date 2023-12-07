export const ssr = false;

export async function load({ fetch, params }) {
    // Simulate receiving a JSON ShoppingList
    const res = await fetch(`http://localhost:9999/${params.id}`, {
        method: "GET",
    });
    const sl = await res.json();
    console.log(sl);

    return { sl: sl, id: params.id };
}
