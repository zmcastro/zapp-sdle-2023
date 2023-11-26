export class GCounter {
    #map = new Map();
    #id;

    constructor(id) {
        this.#id = id;
        this.#map[id] = 0;
    }

    // Set key
    setKey(id) {
        this.#id = id;
        if (this.#map[this.#id] == undefined) this.#map[this.#id] = 0;
    }

    // Get map
    get() {
        const res = this.#map;
        return res;
    }

    // Read local counter value
    local() {
        return this.#map.get(this.#id);
    }

    // Read counter value
    read() {
        let res = 0;
        for (const [id, value] of Object.entries(this.#map)) {
            res += value;
        }
        return res;
    }

    // Increment value
    inc(tosum = 1) {
        this.#map[this.#id] += tosum;
    }

    // Merge counters
    join(gcounter) {
        for (const [id, value] of Object.entries(gcounter.get())) {
            if (!this.#map.has(id)) this.#map[id] = 0;
            this.#map[id] = Math.max(this.#map[id], value);
        }
    }

    fromJSON(counter) {
        this.#map = new Map();
        for (const [key, value] of counter) {
            this.#map[key] = value;
        }
    }

    toJSON() {
        const res = []; 
        for (const [key, value] of Object.entries(this.#map)) {
            console.log(key, value);
            res.push([key, value]);
        }
        
        return res;
    }
}
