<script>
    const shoppinglists = {
        lists: {
            sl1: "1",
            sl2: "2",
            sl3: "3",
            sl4: "4",
            sl5: "5",
            sl6: "6",
            sl7: "7",
            sl8: "8",
            sl9: "9",
            sl10: "10",
        },
    };
</script>

<div class="flex flex-col gap-5">
    <div class="flex flex-row gap-4 w-full">
        <input
            type="text"
            placeholder="Find Shopping List"
            class="input input-bordered w-full"
        />
        <button type="submit" class="btn btn-ghost">
            <svg
                fill="#000000"
                height="15px"
                width="15px"
                version="1.1"
                id="Capa_1"
                xmlns="http://www.w3.org/2000/svg"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                viewBox="0 0 490.4 490.4"
                xml:space="preserve"
            >
                <g>
                    <path
                        d="M484.1,454.796l-110.5-110.6c29.8-36.3,47.6-82.8,47.6-133.4c0-116.3-94.3-210.6-210.6-210.6S0,94.496,0,210.796   s94.3,210.6,210.6,210.6c50.8,0,97.4-18,133.8-48l110.5,110.5c12.9,11.8,25,4.2,29.2,0C492.5,475.596,492.5,463.096,484.1,454.796z    M41.1,210.796c0-93.6,75.9-169.5,169.5-169.5s169.6,75.9,169.6,169.5s-75.9,169.5-169.5,169.5S41.1,304.396,41.1,210.796z"
                    />
                </g>
            </svg>
        </button>
    </div>
</div>
<h2 class="font-bold">Local shoppinglists</h2>
<div
    class="flex flex-col gap-4 p-4 max-h-96 {Object.keys(shoppinglists.lists)
        .length > 5
        ? 'overflow-y-scroll'
        : 'overflow-none'}"
>
    {#each Object.entries(shoppinglists.lists) as [name, id]}
        <div class="card w-96 bg-base-100 shadow-xl">
            <div class="card-body flex-row justify-between p-3">
                <h2 class="card-title font-md truncate">{name}</h2>
                <div class="card-actions justify-end">
                    <a
                        class="btn min-h-fit h-fit font-bold btn-primary p-2"
                        href="/shoppinglist/{id}">Open</a
                    >
                </div>
            </div>
        </div>
    {/each}
</div>
