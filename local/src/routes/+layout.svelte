<script>
    import "../app.postcss";
    import { onMount } from 'svelte';
	export let data;

    let isOnline = navigator.onLine;

    const updateOnlineStatus = () => {
        isOnline = navigator.onLine;
    };

    onMount(() => {
    // Listen for online/offline events
    window.addEventListener('online', updateOnlineStatus);
    window.addEventListener('offline', updateOnlineStatus);

    // Cleanup event listeners on component destroy
    return () => {
        window.removeEventListener('online', updateOnlineStatus);
        window.removeEventListener('offline', updateOnlineStatus);
    };
});
</script>

<main class="min-h-screen flex flex-col justify-center items-center gap-4 p-5">
    <div class="w-full navbar absolute top-0 justify-between">
        <a class="btn btn-ghost text-xl" href="/">ZAPP</a>
        <div class="flex flex-col text-sm items-end">
            <p>UUID: {data.uuid}</p>
            <div class="flex flex-row items-center gap-2">
                <p class="w-3 h-fit">
                    <svg fill="{isOnline ? '#00FF00' : '#FF0000'}" viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
                        <circle cx="50" cy="50" r="50" />
                    </svg>
                </p>
                {isOnline ? "Online" : "Offline"}
            </div>
        </div>
    </div>
    <slot />
</main>
