ognl-repl
=========

Object-Graph Navigation Language Read–eval–print loop

Quick Start
-----------

    git clone https://github.com/naokikimura/ognl-repl.git
    cd ognl-repl

    mvn -q compile exec:java

in a more useful way

    rlwrap mvn -q compile exec:java

Requires
--------

- <abbr title="Java Development Kit">JDK</abbr> 6+
- [Apache Maven][2] 3+

Read OGNL expressions from Standard input (stdin)
-------------------------------------------------

    echo "3 * 4" > test.ognl
    mvn -q compile exec:java < test.ognl

Creating an Executable JAR File
-------------------------------

    mvn clean package

    # copy dependency
    mvn dependency:copy-dependencies
    # run
    java -jar target/ognl-repl-*[0-9T].jar

System properties
-----------------

<dl>
<dt><code>ognl.repl.classpath</code></dt>
<dd>
  <p>Sets the Classpath. For example:</p>
  <pre><code>mvn compile exec:java -Dognl.repl.classpath="${HOME}/.m2/repository/org/apache/commons/commons-lang3/3.3.2/commons-lang3-3.3.2.jar:${HOME}/.m2/repository/log4j/log4j/1.2.17/log4j-1.2.17.jar"</code></pre>
</dd>
<dt><code>ognl.repl.root.expression</code></dt>
<dd>
  <p>Sets the root object. For example:</p>
  <pre><code>mvn compile exec:java -Dognl.repl.root.expression='#{"foo":1,"bar":"hello"}'</code></pre>
</dd>
</dl>

See also
--------

- [OGNL - Language Guide][1]
- [Apache Maven][2]
- [rlwrap][3]

[1]: http://commons.apache.org/proper/commons-ognl/language-guide.html
[2]: http://maven.apache.org/
[3]: http://utopia.knoware.nl/~hlub/rlwrap/
