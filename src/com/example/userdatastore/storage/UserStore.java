package com.example.userdatastore.storage;

import com.example.userdatastore.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory store backed by plain file persistence.
 */
public class UserStore {
    private final ConcurrentHashMap<String, User> map = new ConcurrentHashMap<>();

    public boolean add(User u) {
        if (u == null || u.getId() == null) return false;
        map.put(u.getId(), u);
        return true;
    }

    public boolean update(User u) {
        if (u == null || u.getId() == null) return false;
        if (!map.containsKey(u.getId())) return false;
        map.put(u.getId(), u);
        return true;
    }

    public boolean delete(String id) {
        return map.remove(id) != null;
    }

    public User get(String id) {
        return map.get(id);
    }

    public List<User> getAll() {
        return new ArrayList<>(map.values());
    }

    public void clear() {
        map.clear();
    }

    // Save as serialized plain object
    public void saveToFile(File file) throws Exception {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(getAll());
        }
    }

    // Load back
    @SuppressWarnings("unchecked")
    public void loadFromFile(File file) throws Exception {
        if (file == null || !file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Collection) {
                map.clear();
                for (User u : (Collection<User>) obj) {
                    map.put(u.getId(), u);
                }
            }
        }
    }
}
