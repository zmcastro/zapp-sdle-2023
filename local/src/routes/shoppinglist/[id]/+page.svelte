<script>
    import { ShoppingList } from "$lib/shoppinglist.js";
    import { Product } from "$lib/product.js";
    import { set, get } from "idb-keyval";
    import { productStore } from "../../../stores.js";
    import { copy } from "svelte-copy";
    export let data;

    let representation;
    const shoppinglist = new ShoppingList();
    shoppinglist.fromJSON(data.sl);

    get("uuid").then((uuid) => {
        shoppinglist.setUUID(uuid);
    });

    const updateShoppingList = () => {
        set(shoppinglist.getID(), shoppinglist.toJSON());
        representation = shoppinglist.toFrontendJSON();
    };

    updateShoppingList();

    const addProduct = () => {
        get("uuid").then((uuid) => {
            const newProduct = new Product($productStore.name, uuid);
            shoppinglist.addProduct(newProduct);
            updateShoppingList();
        });
    };

    const decProduct = (product) => {
        shoppinglist.decProduct(product);
        updateShoppingList();
    };

    const incProduct = (product) => {
        shoppinglist.incProduct(product);
        updateShoppingList();
    };

    const removeProduct = (product) => {
        shoppinglist.removeProduct(product);
        updateShoppingList();
    };

    const saveShoppingList = async () => {
        // Save locally
        set(shoppinglist.getID(), shoppinglist.toJSON());

        // Save to cloud
        try {
            const res = await fetch(`http://localhost:9999/${data.id}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(shoppinglist.toJSON()),
            });

            window.location.href = "/";
        } catch {
            window.location.href = "/";
        }
    };

    const showAlert = () => {
        show = true;
        setTimeout(() => {
            show = false;
        }, 1000);
    };

    let show = false;
</script>

<div class="grid grid-cols-3 items-center content-center gap-4">
    <h1 class="font-bold text-xl col-start-2 self-center mx-auto">
        {representation.name}
    </h1>
    <button
        use:copy={shoppinglist.getID()}
        on:svelte-copy={() => showAlert()}
        class="btn btn-ghost w-fit"
    >
        <svg
            width="24px"
            height="24px"
            viewBox="0 0 24 24"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            xmlns:xlink="http://www.w3.org/1999/xlink"
        >
            <!-- Uploaded to: SVG Repo, www.svgrepo.com, Generator: SVG Repo Mixer Tools -->
            <g
                id="🔍-Product-Icons"
                stroke="none"
                stroke-width="1"
                fill="none"
                fill-rule="evenodd"
            >
                <g
                    id="ic_fluent_copy_24_regular"
                    fill="#212121"
                    fill-rule="nonzero"
                >
                    <path
                        d="M5.50280381,4.62704038 L5.5,6.75 L5.5,17.2542087 C5.5,19.0491342 6.95507456,20.5042087 8.75,20.5042087 L17.3662868,20.5044622 C17.057338,21.3782241 16.2239751,22.0042087 15.2444057,22.0042087 L8.75,22.0042087 C6.12664744,22.0042087 4,19.8775613 4,17.2542087 L4,6.75 C4,5.76928848 4.62744523,4.93512464 5.50280381,4.62704038 Z M17.75,2 C18.9926407,2 20,3.00735931 20,4.25 L20,17.25 C20,18.4926407 18.9926407,19.5 17.75,19.5 L8.75,19.5 C7.50735931,19.5 6.5,18.4926407 6.5,17.25 L6.5,4.25 C6.5,3.00735931 7.50735931,2 8.75,2 L17.75,2 Z M17.75,3.5 L8.75,3.5 C8.33578644,3.5 8,3.83578644 8,4.25 L8,17.25 C8,17.6642136 8.33578644,18 8.75,18 L17.75,18 C18.1642136,18 18.5,17.6642136 18.5,17.25 L18.5,4.25 C18.5,3.83578644 18.1642136,3.5 17.75,3.5 Z"
                        id="🎨-Color"
                    >
                    </path>
                </g>
            </g>
        </svg>
    </button>
</div>

<div
    role="alert"
    class="alert alert-success absolute bottom-2 right-2 w-fit {show ? "opacity-100" : "opacity-0"} transition-opacity bg-primary text-white"
>
    <svg
        xmlns="http://www.w3.org/2000/svg"
        class="stroke-current shrink-0 h-6 w-6"
        fill="none"
        viewBox="0 0 24 24"
        ><path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
        /></svg
    >
    <span>ID coppied to clipboard!</span>
</div>

<div class="flex flex-row items-center gap-4">
    <input
        bind:value={$productStore.name}
        type="text"
        placeholder="Product Name"
        class="input input-bordered w-full"
    />
    <button
        disabled={$productStore.name == ""}
        on:click={() => addProduct()}
        class="btn min-h-fit h-fit font-bold btn-primary p-2"
        >+ Add Product</button
    >
</div>

{#if representation.products.length > 0}
    <div class="flex flex-col gap-4">
        {#each representation.products as product}
            <div class="relative flex flex-row">
                <div class="card w-96 bg-base-100 shadow-xl">
                    <div class="card-body flex-row justify-between p-3">
                        <h2 class="card-title font-medium text-lg truncate">
                            {product.name}
                        </h2>
                        <div>
                            <button
                                disabled={product.count == 0}
                                on:click={() => decProduct(product.name)}
                                class="btn btn-ghost disabled:bg-inherit">-</button
                            >
                            <span class="font-bold">{product.count}</span>
                            <button
                                on:click={() => incProduct(product.name)}
                                class="btn btn-ghost">+</button
                            >
                            <button
                                on:click={() => removeProduct(product.name)}
                                class="btn btn-ghost">X</button
                            >
                        </div>
                    </div>
                </div>
                {#if product.count < 0}
                    <div class="tooltip tooltip-top absolute text-primary -right-9 top-[calc(50%-12px)]" data-tip="It seems there are too many products">
                        <svg xmlns="http://www.w3.org/2000/svg" class="stroke-current shrink-0 h-6 w-6" fill="none" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>
                    </div>
                {/if}
            </div>
        {/each}
    </div>
{/if}

<div>
    <button
        on:click={saveShoppingList}
        type="submit"
        class="btn min-h-fit h-fit p-3 w-full btn-primary">Push to cloud</button
    >
</div>
