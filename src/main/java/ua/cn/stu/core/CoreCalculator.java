package ua.cn.stu.core;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CoreCalculator {

    private static final String DESCRIPTION_PROPERTY = "description";
    private static final String DESCRIPTOR_NAME_PART = "descriptor";
    private static final String OPERATOR_PROPERTY = "operator";
    private static final String TYPE_PROPERTY = "type";
    private static final String MAIN_CLASS_PROPERTY = "main.class";
    private static final String PROPERTIES_EXTENSION = ".properties";
    private static final String JAR_EXTENSION = ".jar";
    private static final String PLUGIN_DIR = "E:/НАВЧАННЯ/JavaTech/Babko_lab1/plugins";

    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException,
            URISyntaxException, NoSuchMethodException, SecurityException, IllegalArgumentException,
            InvocationTargetException {
        File[] jars = getAllJarsFromPluginDir();
        Map<String, PluginInfo> pluginClasses = loadPlugins(jars);
        System.out.println("Supported operations:");
        for (PluginInfo pluginInfo : pluginClasses.values()) {
            System.out.println(pluginInfo.getDescription());
        }
        String input = null;
        while (!"exit".equalsIgnoreCase(input)) {
            System.out.println("Please enter expression or type exit >>>");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            input = reader.readLine();
            if ("exit".equalsIgnoreCase(input)) {
                System.out.println("Closing calculator");
            } else {
                boolean isMatchToOperation = false;
                for (String operation : pluginClasses.keySet()) {
                    if (input.contains(operation)) {
                        String escapedOperation = Pattern.quote(operation);
                        Pattern pattern = Pattern.compile("(\\d+(\\.\\d+)?)\\s*(" + escapedOperation + ")\\s*(\\d+(\\.\\d+)?)");
                        Matcher matcher = pattern.matcher(input);

                        if (matcher.matches()) {
                            isMatchToOperation = true;
                            String firstParameter = matcher.group(1);
                            String operator = matcher.group(3);
                            String secondParameter = matcher.group(4);
                            Class<?>[] methodParameterTypes = new Class<?>[] {
                                    double.class, double.class
                            };
                            Object[] methodArgument = new Object[] {
                                    Double.valueOf(firstParameter), Double.valueOf(secondParameter)
                            };
                            Double result = (Double) executeMethod(
                                    pluginClasses.get(operator).getClassReference(),
                                    "calculateBinary", methodParameterTypes, methodArgument
                            );
                            System.out.println("The result of operation " + result);
                        }
                    }
                }
                if (!isMatchToOperation) {
                    System.out.println("Operation is not supported");
                }
            }
        }
    }

    private static File[] getAllJarsFromPluginDir() {
        File pluginDir = new File(PLUGIN_DIR);
        File[] jars = pluginDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(JAR_EXTENSION);
            }
        });
        return jars;
    }

    private static Object executeMethod(Class<?> pluginClass, String methodName,
                                        Class<?>[] methodParameterTypes, Object[] methodArguments)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Method method = pluginClass.getMethod(methodName, methodParameterTypes);
        return method.invoke(pluginClass.newInstance(), methodArguments);
    }

    private static boolean isPluginClass(Class<?> pluginClass) {
        boolean isPlugin = false;
        Class<?>[] implementedInterfaces = pluginClass.getInterfaces();
        for (Class<?> implementedInterface : implementedInterfaces) {
            if ("ua.cn.stu.plugin.api.Plugin".equalsIgnoreCase(implementedInterface.getName())) {
                isPlugin = true;
                continue;
            }
        }
        return isPlugin;
    }

    private static String getDescriptorPath(URL jarURL) throws IOException {
        String descriptorPath = null;
        ZipInputStream zip = new ZipInputStream(jarURL.openStream());
        ZipEntry zipEntry = null;
        while ((zipEntry = zip.getNextEntry()) != null) {
            String entryName = zipEntry.getName();
            if (entryName.contains(DESCRIPTOR_NAME_PART) &&
                entryName.endsWith(PROPERTIES_EXTENSION)) {
                descriptorPath = entryName;
                continue;
            }
        }
        return descriptorPath;
    }

    private static Map<String, PluginInfo> loadPlugins(File[] jars) throws
            URISyntaxException, NoSuchMethodException, InvocationTargetException {
        Map<String, PluginInfo> pluginClasses = new HashMap<String, PluginInfo>();
        for (File jar : jars) {
            PluginClassLoader pluginClassLoader = null;
            try {
                URL jarURL = jar.toURI().toURL();
                pluginClassLoader = new PluginClassLoader(jar.getPath());
                String descriptorPath = getDescriptorPath(jarURL);
                Properties properties = new Properties();
                InputStream inputStream = pluginClassLoader.getResourceAsStream(descriptorPath);
                properties.load(inputStream);
                String className = properties.getProperty(MAIN_CLASS_PROPERTY);
                String operationType = properties.getProperty(TYPE_PROPERTY);
                String operator = properties.getProperty(OPERATOR_PROPERTY);
                String description = properties.getProperty(DESCRIPTION_PROPERTY);
                Class<?> pluginClass = Class.forName(className, false, pluginClassLoader);
                boolean isPlugin = isPluginClass(pluginClass);
                if (isPlugin && operationType != null && operator != null
                    && description != null) {
                    executeMethod(pluginClass, "invoke", new Class<?>[] {}, new Object[] {});
                    PluginInfo pluginInfo = new PluginInfo();
                    pluginInfo.setClassReference(pluginClass);
                    if (OperatorType.UNARY.getOperatorType().equalsIgnoreCase(operationType)) {
                        pluginInfo.setOperatorType(OperatorType.UNARY);
                    } else if (OperatorType.BINARY.getOperatorType().equalsIgnoreCase(operationType)) {
                        pluginInfo.setOperatorType(OperatorType.BINARY);
                    }
                    pluginInfo.setOperatorType(OperatorType.BINARY);
                    pluginInfo.setOperator(operator);
                    pluginInfo.setDescription(description);
                    pluginClasses.put(pluginInfo.getOperator().toString(), pluginInfo);
                }
            } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
        return  pluginClasses;
    }

}
