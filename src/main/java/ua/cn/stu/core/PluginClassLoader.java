package ua.cn.stu.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginClassLoader extends ClassLoader {

    private String jarName;
    private JarFile jar;
    private Map<String, Class<?>> loaded = new HashMap<String, Class<?>>();

    public PluginClassLoader(String jarName) {
        super(PluginClassLoader.class.getClassLoader());
        this.jarName = jarName;
        try {
            this.jar = new JarFile(jarName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> clazz = loaded.get(name);
        if (clazz != null)
            return clazz;
        try {
            return findSystemClass(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] data;
        try {
            data = loadClassData(name);
            clazz = defineClass(name, data, 0, data.length);
            loaded.put(name, clazz);
        } catch (Throwable e) {
            throw new ClassNotFoundException(e.getMessage());
        }
        return clazz;
    }

    private byte[] loadClassData(String name) throws ClassNotFoundException {
        String entryName = name.replace('.', '/') + ".class";
        byte buf[] = new byte[0];
        try {
            JarEntry entry = jar.getJarEntry(entryName);
            if (entry == null) {
                throw new ClassNotFoundException(name);
            }
            InputStream input = jar.getInputStream(entry);
            int size = (int) entry.getSize();
            buf = new byte[size];
            int count = input.read(buf);
            if (count < size) {
                throw new ClassNotFoundException("Error reading class " + name + " from: " + jarName);
            }
        } catch (IOException e) {
            throw new ClassNotFoundException(e.getMessage());
        }
        return buf;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        try {
            JarEntry entry = jar.getJarEntry(name);
            if (entry != null) {
                return jar.getInputStream(entry);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
