package example;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.ResourceBundle;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

/**
 *
 * @author <a href="https://github.com/naokikimura">naokikimura</a>
 *
 */
public abstract class App {

    private static final ResourceBundle resources = ResourceBundle.getBundle(App.class.getName());

    public abstract void execute(final Map context, final Object root) throws Exception;

    static App create(final InputStream in, final PrintStream out, final PrintStream err) {
        return new App() {
            @Override
            public void execute(final Map context, final Object root) throws Exception {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String expression;
                while ((expression = reader.readLine()) != null) {
                    try {
                        Object value = Ognl.getValue(expression, context, root);
                        out.printf(resources.getString("out.printf.format"), value);
                    } catch (OgnlException e) {
                        err.printf(resources.getString("err.printf.format"), e);
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
                    String expression = console.readLine(resources.getString("console.readLine.format"), ++line);
                    if (expression == null) {
                        console.printf("%n");
                        return;
                    } else if (expression.trim().equals("")) {
                        continue;
                    }

                    try {
                        Object value = Ognl.getValue(expression, context, root);
                        out.printf(resources.getString("console.printf.format"), value);
                    } catch (OgnlException e) {
                        err.printf(resources.getString("err.printf.format"), e);
                    }
                }
            }
        };
    }

    static App create() {
        Console console = System.console();
        if (console == null) {
            return App.create(System.in, System.out, System.err);
        } else {
            return App.create(console, console.writer(), System.err);
        }
    }

    static Object newRootInstance() throws OgnlException {
        String expression = System.getProperty("ognl.repl.root.expression");
        return expression == null || expression.equals("") ? null : Ognl.getValue(expression, null);
    }

    public static void main(String[] args) throws Exception {
        App app = create();
        app.execute(new OgnlContext(), newRootInstance());
    }
}
