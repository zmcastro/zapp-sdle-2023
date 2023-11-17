<script>
    async function test() {
        const res = await fetch('/api/test', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
        });
        const data = await res.json();
        console.log(data);
    }
</script>

<button on:click={test} class="bg-slate-200 p-2 rounded-md border border-black">WORK?</button>
