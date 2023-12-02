<script>
    import { ShoppingList } from "$lib/shoppinglist.js";
    import { Product } from "$lib/product.js";
    import { set, get } from "idb-keyval";
    import { productStore } from "../../../stores.js";

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

    const saveShoppingList = () => {
        set(shoppinglist.getID(), shoppinglist.toJSON());
        // TODO: Save it to the cloud
    };
</script>

<h1 class="font-bold text-xl">{representation.name}</h1>

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
        {/each}
    </div>
{/if}

<div>
    <button
        on:click={saveShoppingList}
        type="submit"
        class="btn min-h-fit h-fit p-3 w-full btn-primary">Create</button
    >
</div>
