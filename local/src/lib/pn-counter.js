import { GCounter } from "./g-counter";

export class PNCounter {

    #p = new GCounter();
    #n = new GCounter();

    constructor(id) {
        this.#p.setKey(id);
        this.#n.setKey(id);
    }

    // Get positive counter value
    getP() {
        return this.#p.get();
    }

    // Get negative counter value
    getN() {
        return this.#p.get();
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
    inc(tosum=1) {
        this.#p.inc(tosum);
    }

    // Decrement value
    dec(tosum=1) {
        this.#n.inc(tosum);
    }

    // Merge counters
    join(pncounter) {
        this.#p.join(pncounter.#p);
        this.#n.join(pncounter.#n);
    }
}