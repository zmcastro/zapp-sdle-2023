export async function load({ fetch, params }) {
    const res = await fetch("/api/test", {
        method: "GET",
    });
    const sl = await res.json();

    return { sl: sl };
}
