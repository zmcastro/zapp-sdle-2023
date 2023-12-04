package ConsistentHashing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

public class ConsistentHashing {

    private final SortedMap<Long, String> ring = new TreeMap<>();
    private final int nReplicas;
    private final Function<String, Long> hashFunc;

    public ConsistentHashing(int nReplicas) {
        this.nReplicas = nReplicas;
        this.hashFunc = ConsistentHashing::md5;
    }

    public ConsistentHashing(int nReplicas, Function<String, Long> hash) {
        this.nReplicas = nReplicas;
        this.hashFunc = hash;
    }

    public void addNode(String node) {
        for (int i = 0; i < nReplicas; i++) {
            long hash = hashFunc.apply(node + i);
            ring.put(hash, node);
        }
    }

    public void removeNode(String node) {
        for (int i = 0; i < nReplicas; i++) {
            long hash = hashFunc.apply(node + i);
            ring.remove(hash);
        }
    }

    public String getNode(String key) {
        long hash = hashFunc.apply(key);

        // Get all virtual nodes with hash greater than or equal to provided hash
        SortedMap<Long, String> tailMap = ring.tailMap(hash);

        // Find next closest virtual node. Empty tail map means closest server is the first on the ring
        hash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
        return ring.get(hash);
    }

    public static long md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            // not using a loop here because of casting to long
            return ((long) (digest[3] & 0xFF) << 24) | ((long) (digest[2] & 0xFF) << 16) |
                    ((long) (digest[1] & 0xFF) << 8) | ((long) (digest[0] & 0xFF));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return -1;
    }

}