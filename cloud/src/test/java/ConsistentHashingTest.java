import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConsistentHashingTest {

    @Test
    void addExtraNode() {
        ConsistentHashing ch = new ConsistentHashing(5);

        ch.addNode("Node 1");
        ch.addNode("Node 2");
        ch.addNode("Node 3");

        assertEquals("Node 1", ch.getNode("Key 1"));
        assertEquals("Node 3", ch.getNode("Key 2"));
        assertEquals("Node 3", ch.getNode("Key 3"));
        assertEquals("Node 1", ch.getNode("Key 4"));
        assertEquals("Node 3", ch.getNode("Key 5"));
        assertEquals("Node 2", ch.getNode("Key 6"));
        assertEquals("Node 2", ch.getNode("Key 7"));
        assertEquals("Node 3", ch.getNode("Key 8"));
        assertEquals("Node 1", ch.getNode("Key 9"));
        assertEquals("Node 1", ch.getNode("Key 10"));
        assertEquals("Node 2", ch.getNode("Key 11"));
        assertEquals("Node 1", ch.getNode("Key 12"));

        // Adding a new node, only key 8 is remapped.
        ch.addNode("Node 4");

        assertEquals("Node 1", ch.getNode("Key 1"));
        assertEquals("Node 3", ch.getNode("Key 2"));
        assertEquals("Node 3", ch.getNode("Key 3"));
        assertEquals("Node 1", ch.getNode("Key 4"));
        assertEquals("Node 3", ch.getNode("Key 5"));
        assertEquals("Node 2", ch.getNode("Key 6"));
        assertEquals("Node 2", ch.getNode("Key 7"));
        assertEquals("Node 4", ch.getNode("Key 8"));
        assertEquals("Node 1", ch.getNode("Key 9"));
        assertEquals("Node 1", ch.getNode("Key 10"));
        assertEquals("Node 2", ch.getNode("Key 11"));
        assertEquals("Node 1", ch.getNode("Key 12"));
    }

    @Test
    void removeNode() {
        ConsistentHashing ch = new ConsistentHashing(5);
        ch.addNode("Node 1");
        ch.addNode("Node 2");
        ch.addNode("Node 3");

        assertEquals("Node 1", ch.getNode("Key 1"));
        assertEquals("Node 3", ch.getNode("Key 2"));
        assertEquals("Node 3", ch.getNode("Key 3"));
        assertEquals("Node 1", ch.getNode("Key 4"));
        assertEquals("Node 3", ch.getNode("Key 5"));
        assertEquals("Node 2", ch.getNode("Key 6"));
        assertEquals("Node 2", ch.getNode("Key 7"));
        assertEquals("Node 3", ch.getNode("Key 8"));
        assertEquals("Node 1", ch.getNode("Key 9"));
        assertEquals("Node 1", ch.getNode("Key 10"));
        assertEquals("Node 2", ch.getNode("Key 11"));
        assertEquals("Node 1", ch.getNode("Key 12"));

        // By removing Node 3, Key 2 gets remapped to node 2, while Key 3 gets remapped to Node 1.
        // Without virtual nodes working properly, this wouldn't have been possible.
        ch.removeNode("Node 3");

        assertEquals("Node 1", ch.getNode("Key 1"));
        assertEquals("Node 2", ch.getNode("Key 2"));
        assertEquals("Node 1", ch.getNode("Key 3"));
        assertEquals("Node 1", ch.getNode("Key 4"));
        assertEquals("Node 2", ch.getNode("Key 5"));
        assertEquals("Node 2", ch.getNode("Key 6"));
        assertEquals("Node 2", ch.getNode("Key 7"));
        assertEquals("Node 2", ch.getNode("Key 8"));
        assertEquals("Node 1", ch.getNode("Key 9"));
        assertEquals("Node 1", ch.getNode("Key 10"));
        assertEquals("Node 2", ch.getNode("Key 11"));
        assertEquals("Node 1", ch.getNode("Key 12"));

    }
}

