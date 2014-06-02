package example;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import ognl.ClassResolver;
import ognl.DefaultClassResolver;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

/**
 *
 * @author <a href="https://github.com/naokikimura">naokikimura</a>
 *
 */
public abstract class App {

    private static final Logger LOG = Logger.getLogger(App.class.getName());

    private static final Properties PROPERTIES;

    private static final ResourceBundle RESOURCES = ResourceBundle.getBundle(App.class.getName());

    static {
        Properties defaults = null;
        try {
            defaults = loadProperties("example/ognl-repl.properties");
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        PROPERTIES = new Properties(defaults);
    }

    static Properties loadProperties(String resourceName) throws IOException {
        return loadProperties(resourceName, null);
    }

    static Properties loadProperties(String resourceName, Properties defaults) throws IOException {
        return loadProperties(resourceName, Charset.defaultCharset(), defaults);
    }

    static Properties loadProperties(String resourceName, Charset encoding, Properties defaults) throws IOException {
        return loadProperties(resourceName, encoding, App.class.getClassLoader(), defaults);
    }

    static Properties loadProperties(String resourceName, Charset encoding, ClassLoader classLoader, Properties defaults) throws IOException {
        return loadProperties(classLoader.getResources(resourceName), encoding, defaults);
    }

    private static Properties loadProperties(Enumeration<URL> resources, Charset encoding, Properties defaults) throws IOException {
        if (resources == null || !resources.hasMoreElements()) return new Properties(defaults);

        URL resource = resources.nextElement();
        LOG.config(String.format("load properties = %s", resource));
        Properties properties = new Properties(loadProperties(resources, encoding, defaults));
        Reader reader = new InputStreamReader(resource.openStream(), encoding);
        try {
            properties.load(reader);
        } finally {
            reader.close();
        }
        return properties;
    }

    static String findProperty(String key) {
        return findProperty(key, null);
    }

    static String findProperty(String key, String def) {
        String systemProperty = System.getProperty(key);
        String value = systemProperty == null ? PROPERTIES.getProperty(key, def) : systemProperty;
        LOG.config(String.format("%s = %s", key, value));
        return value;
    }

    public abstract void execute(final Map context, final Object root) throws Exception;

    public void execute(final Map context) throws Exception {
        execute(context, Ognl.getRoot(context));
    }

    static App create(final InputStream in, final PrintStream out, final PrintStream err) {
        return new App() {
            @Override
            public void execute(final Map context, final Object root) throws Exception {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String expression;
                while ((expression = reader.readLine()) != null) {
                    try {
                        Object value = Ognl.getValue(expression, context, root);
                        out.printf(RESOURCES.getString("out.printf.format"), value);
                    } catch (OgnlException e) {
                        LOG.log(Level.FINE, e.getLocalizedMessage(), e);
                        err.printf(RESOURCES.getString("err.printf.format"), e);
                    }
                }
            }
        };
    }

    static App create(final Console console, final PrintWriter out, final PrintStream err) {
        return new App() {
            @Override
            public void execute(final Map context, final Object root) throws Exception {
                int line = 0;
                while (true) {
                    String expression = console.readLine(RESOURCES.getString("console.readLine.format"), ++line);
                    if (expression == null) {
                        console.printf("%n");
                        return;
                    } else if (expression.trim().equals("")) {
                        continue;
                    }

                    try {
                        Object value = Ognl.getValue(expression, context, root);
                        out.printf(RESOURCES.getString("console.printf.format"), value);
                    } catch (OgnlException e) {
                        LOG.log(Level.FINE, e.getLocalizedMessage(), e);
                        err.printf(RESOURCES.getString("err.printf.format"), e);
                    }
                }
            }
        };
    }

    static App create() {
        Console console = System.console();
        return console == null ? create(System.in, System.out, System.err) : create(console, console.writer(), System.err);
    }

    static ClassResolver createClassResolver() throws MalformedURLException {
        String classpath = findProperty("ognl.repl.classpath", "");
        List<URL> urls = new ArrayList<URL>();
        for (String path : (classpath.trim() + ":").split(":")) {
            URL url = new File(path).toURI().toURL();
            LOG.config(String.format("classpath = %s", url));
            urls.add(url);
        }
        final ClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]));
        return new DefaultClassResolver() {

            @Override
            public Class classForName(String className, Map context) throws ClassNotFoundException {
                try {
                    return Class.forName(className, true, classLoader);
                } catch (ClassNotFoundException e) {
                    LOG.log(Level.FINE, e.getLocalizedMessage(), e);
                    return super.classForName(className, context);
                }
            }
        };
    }

    static Object newRootInstance() throws OgnlException {
        return newRootInstance(new OgnlContext());
    }

    static Object newRootInstance(Map context) throws OgnlException {
        return newRootInstance(context, Ognl.getRoot(context));
    }

    static Object newRootInstance(Map context, Object root) throws OgnlException {
        String expression = findProperty("ognl.repl.root.expression", "");
        return "".equals(expression) ? null : Ognl.getValue(expression, context, root);
    }

    public static void main(String[] args) throws Exception {
        App app = create();
        ClassResolver classResolver = createClassResolver();
        Object root = newRootInstance(Ognl.createDefaultContext(null, classResolver));
        app.execute(Ognl.createDefaultContext(root, classResolver));
    }
}
