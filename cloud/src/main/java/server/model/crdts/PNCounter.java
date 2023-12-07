package server.model.crdts;

public class PNCounter {
    private String uid;
    private GCounter p;
    private GCounter n;

    public PNCounter(String id) {
        this.uid = id;
        this.p = new GCounter(id);
        this.n = new GCounter(id);
    }

    public PNCounter(PNCounter counter) {
        this.uid = counter.uid;
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

    public void setUUID(String u_id) {
        this.uid = u_id;
        this.p.setKey(u_id);
        this.n.setKey(u_id);
    }

    // Read local counter value
    public int local() {
        int val = p.local() - n.local();
        return Math.max(val, 0);
    }

    // Read counter value
    public int read() {
        int val = p.read() - n.read();
        return Math.max(val, 0);
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