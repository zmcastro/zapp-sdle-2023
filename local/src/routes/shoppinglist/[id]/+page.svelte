<script>
    export let data;
    const shoppinglist = data.sl;
</script>

<h1 class="font-bold text-xl">{shoppinglist.name}</h1>

<button class="btn min-h-fit h-fit font-bold btn-primary p-2"
    >+ Add Product</button
>
<div class="flex flex-col gap-4">
    {#each shoppinglist.products as product}
        <div class="card w-96 bg-base-100 shadow-xl">
            <div class="card-body flex-row justify-between p-3">
                <h2 class="card-title font-medium text-lg truncate">
                    {product.name}
                </h2>
                <div>
                    <button class="btn btn-ghost">-</button>
                    <span class="font-bold">{product.count}</span>
                    <button class="btn btn-ghost">+</button>
                    <button class="btn btn-ghost">X</button>
                </div>
            </div>
        </div>
    {/each}
</div>
