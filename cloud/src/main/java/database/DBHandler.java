package database;

import ConsistentHashing.ConsistentHashing;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DBHandler {

    private static final String JSON_DIRECTORY = "src/main/java/database/";
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final ReadWriteLock NodesWriteLock = new ReentrantReadWriteLock();

    private ConsistentHashing consistentHashing;

    public DBHandler(ConsistentHashing consistentHashing){
        this.consistentHashing = consistentHashing;
    }

    public String getFile(String node, String id) throws IOException {
        List<String> nodes = getAdjacentNodes(node);
        String filePath;

        Exception lastException = null;

        readWriteLock.readLock().lock();

        try{
            for (String n : nodes) {
                try {
                    filePath = JSON_DIRECTORY + n + "/" + id + ".json";
                    String jsonData = new String(Files.readAllBytes(Paths.get(filePath)));
                    return jsonData;
                } catch (Exception e) {
                    lastException = e;
                    // continue to the next attempt
                }
            }
        }
        finally {
            readWriteLock.readLock().unlock();
        }

        if (lastException != null) {
            throw new IOException("Failed to read data from all nodes", lastException);
        } else {
            throw new IOException("No exception was caught, but all attempts failed");
        }
    }

    public void deleteFile(String node, String id) throws IOException {
        List<String> nodes = getAdjacentNodes(node);
        String filePath;


        readWriteLock.writeLock().lock();

        try {
            for (String n : nodes) {
                filePath = JSON_DIRECTORY + n + "/" + id + ".json";
                Files.delete(Paths.get(filePath));
            }
        } catch (Exception e) {
            throw e;
        } finally {
            readWriteLock.writeLock().unlock();
        }

    }

    public void storeFile(String node, String id, String jsonData) throws IOException {
        List<String> nodes = getAdjacentNodes(node);
        String filePath;

        readWriteLock.writeLock().lock();

        try {
            for (String n : nodes) {
                filePath = JSON_DIRECTORY + n + "/" + id + ".json";
                Files.write(Paths.get(filePath), jsonData.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public List<String> getAdjacentNodes(String node){
        List<String> nodes = this.listNodes();
        if (!nodes.contains(node)){
            return new ArrayList<>();
        }
        if (node.equals(nodes.get(0))){
            return new ArrayList<>(Arrays.asList(node, nodes.get(nodes.size()-1), nodes.get(1)));
        }
        if (node.equals(nodes.get(nodes.size()-1))){
            return new ArrayList<>(Arrays.asList(node, nodes.get(nodes.indexOf(node) - 1), nodes.get(0)));
        }
        return new ArrayList<>(Arrays.asList(node, nodes.get(nodes.indexOf(node) - 1), nodes.get(nodes.indexOf(node) + 1)));
    }

    public List<String> listNodes() {
        NodesWriteLock.readLock().lock();
        try {
            List<String> folderNames = new ArrayList<>();

            try {
                File directory = new File(JSON_DIRECTORY);

                // Check if the directory exists
                if (!directory.exists() || !directory.isDirectory()) {
                    throw new RuntimeException("Directory does not exist or is not a valid directory.");
                }

                File[] files = directory.listFiles();

                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            folderNames.add(file.getName());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error listing folders: " + e.getMessage());
            }

            // Sort the folder names by hash using the md5 function
            folderNames.sort((folder1, folder2) -> {
                try {
                    Long hash1 = this.consistentHashing.md5(folder1);
                    Long hash2 = this.consistentHashing.md5(folder2);
                    return hash1.compareTo(hash2);
                } catch (Exception e) {
                    throw new RuntimeException("Error calculating MD5 hash: " + e.getMessage());
                }
            });

            return folderNames;
        }
        finally {
            NodesWriteLock.readLock().unlock();
        }
    }

    public void createNode(String node) throws IOException {
        NodesWriteLock.writeLock().lock();
        this.consistentHashing.addNode(node);
        try {
            String folderPath = JSON_DIRECTORY + node;

            try {
                Set<String> allLists = new TreeSet<String>();
                Files.walkFileTree(Paths.get(JSON_DIRECTORY), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        // Get the file name without extension
                        String fileNameWithoutExtension = file.getFileName().toString().replaceFirst("[.][^.]+$", "");
                        if (!fileNameWithoutExtension.equals("DBHandler")){
                            allLists.add(fileNameWithoutExtension);
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }
                });

                System.out.println(allLists);

                Files.createDirectories(Paths.get(folderPath));

                for (String l: allLists){

                    if (this.getAdjacentNodes(node).contains(consistentHashing.getNode(l))){ // need to reallocate
                        final String[] fileContent = {null};
                        // delete previously stored
                        Files.walkFileTree(Paths.get(JSON_DIRECTORY), new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                String fileNameWithoutExtension = file.getFileName().toString().replaceFirst("[.][^.]+$", "");
                                if (fileNameWithoutExtension.equals(l) && file.toString().endsWith(".json")) {
                                    fileContent[0] = new String(Files.readAllBytes(file));
                                    Files.delete(file);
                                }
                                return FileVisitResult.CONTINUE;
                            }
                        });

                        this.storeFile(consistentHashing.getNode(l), l, fileContent[0]); // store in correct nodes
                    }
                }



            } catch (Exception e) {
                throw e;
            }
        }
        finally {
            NodesWriteLock.writeLock().unlock();
        }
    }
    public void removeNode(String node) throws IOException {
        NodesWriteLock.writeLock().lock();
        if (listNodes().size() <= 3){
            NodesWriteLock.writeLock().unlock();
            throw new RuntimeException("Minimum number of database nodes");
        }
        try {
            String folderPath = JSON_DIRECTORY + node;

            try {
                Set<String> allLists = new TreeSet<String>();
                Files.walkFileTree(Paths.get(JSON_DIRECTORY), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        // Get the file name without extension
                        String fileNameWithoutExtension = file.getFileName().toString().replaceFirst("[.][^.]+$", "");
                        if (!fileNameWithoutExtension.equals("DBHandler")){
                            allLists.add(fileNameWithoutExtension);
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }
                });

                System.out.println(allLists);

                Map<String, String> reallocate = new TreeMap<String, String>();

                for (String l: allLists){
                    if (this.getAdjacentNodes(node).contains(consistentHashing.getNode(l))){ // need to reallocate
                        final String[] fileContent = {null};
                        // delete previously stored
                        Files.walkFileTree(Paths.get(JSON_DIRECTORY), new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                String fileNameWithoutExtension = file.getFileName().toString().replaceFirst("[.][^.]+$", "");
                                if (fileNameWithoutExtension.equals(l) && file.toString().endsWith(".json")) {
                                    fileContent[0] = new String(Files.readAllBytes(file));
                                    Files.delete(file);
                                }
                                return FileVisitResult.CONTINUE;
                            }
                        });
                        reallocate.put(l, fileContent[0]);
                    }
                }

                // delete the folder
                Files.walkFileTree(Paths.get(folderPath), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        // Handle the error accordingly
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });

                this.consistentHashing.removeNode(node);

                for (String key: reallocate.keySet()){
                    this.storeFile(this.consistentHashing.getNode(key), key, reallocate.get(key)); // store in correct nodes
                }



            } catch (Exception e) {
                throw e;
            }
        } finally {
            NodesWriteLock.writeLock().unlock();
        }
    }
}
