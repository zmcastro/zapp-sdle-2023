package server.model.crdts;

public class PNCounter {
    private GCounter p;
    private GCounter n;

    public PNCounter(String id) {
        this.p = new GCounter(id);
        this.n = new GCounter(id);
    }

    public PNCounter(PNCounter counter) {
        this.p = new GCounter(counter.p);
        this.n = new GCounter(counter.n);
    }

    // Get positive counter value
    public GCounter getP() {
        return this.p;
    }

    // Get negative counter value
    public GCounter getN() {
        return this.n;
    }

    // Read local counter value
    public int local() {
        int val = p.local() - n.local();
        return val < 0 ? 0 : val;
    }

    // Read counter value
    public int read() {
        int val = p.read() - n.read();
        return val < 0 ? 0 : val;
    }

    // Increment value
    public void inc(int tosum) {
        p.inc(tosum);
    }

    // Decrement value
    public void dec(int tosum) {
        n.inc(tosum);
    }

    // Merge counters
    public void join(PNCounter pnCounter) {
        p.join(pnCounter.p);
        n.join(pnCounter.n);
    }
}