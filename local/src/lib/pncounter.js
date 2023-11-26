import { GCounter } from "$lib/gcounter";

export class PNCounter {
    #p = null;
    #n = null;
    #uid = null;

    constructor(id) {
        this.#uid = id;
        this.#p = new GCounter(id);
        this.#n = new GCounter(id);
    }

    setUID(id) {
        this.#uid = id;
        this.#p.setKey(id);
        this.#n.setKey(id);
    }

    // Get positive counter value
    getP() {
        return this.#p.get();
    }

    // Get negative counter value
    getN() {
        return this.#n.get();
    }

    // Read local counter value
    local() {
        const val = this.#p.local() - this.#n.local();
        return val < 0 ? 0 : val;
    }

    // Read counter value
    read() {
        const val = this.#p.read() - this.#n.read();
        return val < 0 ? 0 : val;
    }

    // Increment value
    inc(tosum = 1) {
        this.#p.inc(tosum);
    }

    // Decrement value
    dec(tosum = 1) {
        this.#n.inc(tosum);
    }

    // Merge counters
    join(pncounter) {
        this.#p.join(pncounter.#p);
        this.#n.join(pncounter.#n);
    }
    
    fromJSON(counter) {
        this.#p = new GCounter();
        this.#p.fromJSON(counter.p);
        this.#n = new GCounter();
        this.#n.fromJSON(counter.n);
    }

    toJSON() {
        const res = {
            p: this.#p.toJSON(),
            n: this.#n.toJSON(),
        };

        return res;
    }
}
