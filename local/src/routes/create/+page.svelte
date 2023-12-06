<script>
    import { Product } from "$lib/product.js";
    import { ShoppingList } from "$lib/shoppinglist.js";
    import { set, get } from "idb-keyval";

    export let data;

    const shoppinglist = new ShoppingList();
    shoppinglist.fromJSON(data.sl);
    let products = shoppinglist.toFrontendJSON().products;
    let productName = "";
    let shoppingListName = shoppinglist.getName();
    let remove = shoppinglist.getRemove();

    const addProduct = () => {
        get("uuid").then((uuid) => {
            const newProduct = new Product(productName, uuid);
            shoppinglist.addProduct(newProduct);
            products = shoppinglist.toFrontendJSON().products;
        });
    };

    const decProduct = (product) => {
        shoppinglist.decProduct(product);
        products = shoppinglist.toFrontendJSON().products;
    };

    const incProduct = (product) => {
        shoppinglist.incProduct(product);
        products = shoppinglist.toFrontendJSON().products;
    };

    const removeProduct = (product) => {
        shoppinglist.removeProduct(product);
        products = shoppinglist.toFrontendJSON().products;
    };

    const saveShoppingList = () => {
        shoppinglist.setName(shoppingListName);
        set(shoppinglist.getID(), shoppinglist.toJSON());
        // TODO: Save it to the cloud
    };

    const toggleRemove = () => {
        remove = !remove;
        shoppinglist.setRemove(remove);
    };
</script>

<h1 class="font-bold w-fit">Create a new shopping list</h1>

<div class="flex flex-col items-center gap-4">
    <input
        bind:value={shoppingListName}
        type="text"
        placeholder="Shopping List Name"
        class="input input-bordered"
        name="name"
    />
    <div class="form-control gap-4">
        <label class="label cursor-pointer">
            <span class="label-text pr-3">Remove item when count is 0</span>
            <input
                type="checkbox"
                class="toggle"
                checked={remove}
                on:change={() => toggleRemove()}
            />
        </label>
    </div>

    <h2 class="font-bold w-fit">Products</h2>
    <div class="flex flex-row items-center gap-2">
        <input
            bind:value={productName}
            type="text"
            placeholder="Product Name"
            class="input input-bordered w-full"
        />
        <button
            disabled={productName == ""}
            on:click={() => addProduct()}
            class="btn min-h-fit h-fit font-bold btn-primary p-2"
            >+ Add Product</button
        >
    </div>

    {#if products.length > 0}
        <div
            class="flex flex-col gap-4 p-4 max-h-96 {products.length > 5
                ? 'overflow-y-scroll'
                : 'overflow-none'}"
        >
            {#each products as product}
                <div class="card w-96 bg-base-100 shadow-xl">
                    <div class="card-body flex-row justify-between p-3">
                        <h2 class="card-title font-medium text-lg truncate">
                            {product.name}
                        </h2>
                        <div>
                            <button
                                disabled={product.count == 0}
                                on:click={() => decProduct(product.name)}
                                class="btn btn-ghost disabled:bg-inherit"
                                >-</button
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
</div>

<div>
    <button
        disabled={shoppingListName == "" || products.length == 0}
        on:click={saveShoppingList}
        type="submit"
        class="btn min-h-fit h-fit p-3 w-full btn-primary">Create</button
    >
</div>
