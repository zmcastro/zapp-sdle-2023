<script>
    import "../app.postcss";
</script>

<main class="min-h-screen flex flex-col justify-center items-center gap-4 p-5">
    <div class="w-full navbar absolute top-0">
        <a class="btn btn-ghost text-xl" href="/">ZAPP</a>
    </div>
    <slot />
</main>
