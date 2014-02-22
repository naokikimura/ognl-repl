ognl-repl
=========

Object-Graph Navigation Language Read–eval–print loop

Quick Start
-----------

    git clone https://github.com/naokikimura/ognl-repl.git
    cd ognl-repl

    mvn compile exec:java

in a more useful way

    rlwrap mvn compile exec:java

Read OGNL expressions from Standard input (stdin)
-------------------------------------------------

    echo "3 * 4" > test.ognl
    mvn compile exec:java < test.ognl

Creating an Executable JAR File
-------------------------------

    mvn clean package

    # copy dependency
    mvn dependency:copy-dependencies
    # run
    java -jar target/ognl-repl-*.jar

See also
--------

- [OGNL - Language Guide](http://commons.apache.org/proper/commons-ognl/language-guide.html)
- [Apache Maven](http://maven.apache.org/)
- [rlwrap](http://utopia.knoware.nl/~hlub/rlwrap/)
