# Development of Pityoulish Exercises

As of 2017, all exercises are coded in Java.
Each Pityoulish exercise comprises:
- Instructions in HTML, from files in
  [src/main/prose/](src/main/prose/)
- A JAR file, compiled and assembled from source code in 
  [src/main/java/](src/main/java/)
- JavaDocs, generated from the same source code

All student files for an exercise are packaged into a single zip archive.
Instructors can build notes and server-side JARs in a similar fashion.
This readme describes how to set up a development environment, how to build various components with Ant, and how the build process works.

Table of Contents:
- [Setup](#setup) - install your own build environment
- [Ant Targets](#ant-targets) - use Ant to build exercises and related stuff
- [Build Steps](#build-steps) - how the build process works


# Setup

To set up a command-line based development environment, you will need:
- a Java Software Development Kit, for example OpenJDK  
  Linux distributions typically provide that through their package manager.
- Command-line tools: `git`, `ant`  
  Linux distributions typically provide these through their package manager.
- Java libraries:
  [`ant-contrib`](http://ant-contrib.sourceforge.net/),
  [`junit4`](http://junit.org/junit4/),
  [`hamcrest`](http://hamcrest.org/JavaHamcrest/)  
  Linux distributions may provide some through their package manager.
  Download the JAR files for the others.  
  _Expect JMockit to appear in this list in the future._
- _optional:_ [grip](https://github.com/joeyespo/grip)
  for previewing markdown before committing

My workflow is based on an editor, command line, and browser.
When building exercises, the Java sources are pre-processed with Ant, to punch in the gaps that students have to fill. You might find it tricky to use an IDE for that step.
The actual coding and unit-testing during development is done without pre-processing though. It should work smoothly with an IDE.

To get started...
1. Clone the [Pityoulish](https://github.com/rolandweber/pityoulish/) GitHub project.
2. Make sure Ant has access to the ant-contrib tasks.
3. Copy [`ant.local.template`](ant.local.template) to `ant.local`.
   Edit the latter and specify the paths to the extra JAR files.
   For example on my Linux workstation:
   ```
   junit4.jar = /usr/share/java/junit4.jar
   hamcrest.jar = /usr/share/java/hamcrest-all.jar
   ```
4. In a terminal, change to the project directory and execute:
   ```
   ant _clean compile test jdoc
   ```
   This should pass without errors or warnings.
   To see the main [Ant targets](#ant-targets), execute:
   ```
   ant -projecthelp
   ```
5. Point your browser to `src/main/prose/local-index.html` in the project directory.  
   This page contains links to the test results and JavaDocs from the previous step, and more.

Happy hacking!


# Ant Targets

Run `ant -projecthelp` to list the main Ant targets.
Targets without a hyphen (-) apply to the whole project, across exercises.
This includes "compile", "test", and "jdoc" mentioned in [Setup](#setup) above.
Ant targets specific to an exercise have at least one hyphen in their name, and an acronym in the description. For example:

| Target | Acronym | Purpose |
| ------ | ------- | ------- |
| prep-java | tpj | Tutorial: Prepare for Java exercises |
| client-sockets-java | xjs | Exercise: Java Sockets |
| server-sockets-java | ijs | Instructions: Java Sockets |
| follow-sockets-java | fjs | Follow the board: Java Sockets |
| client-rmi-java | xjr | Exercise: Java RMI |
| server-rmi-java | ijr | Instructions: Java RMI |
| follow-rmi-java | fjr | Follow the board: Java RMI |

The target for an Exercise or the Instructions will build a zip archive for students or instructors, respectively.
This requires several sub-targets to be built. Ant prints these sub-targets while it executes.
If you are working on a specific part of an exercise, you can build the respective sub-target directly to save time.
For example, building the Tutorial with `prep-java` builds these sub-targets, among others:
- `tpj-jar`   - The JAR file with bugs to fix, and another without the bugs.
- `tpj-jdoc`  - JavaDocs just for the classes in the JAR file.
- `tpj-prose` - The tutorial description in HTML.
- `tpj-all`   - The zip archive comprising JAR, JavaDocs, and description.
  Same as `prep-java` itself.



# Build Steps

The Java source code for all clients, servers, and other programs is in one combined source tree at [src/main/java/](src/main/java/).
Unit tests are in a similar source tree at [src/test/java/](src/test/java/).
For development, the source tree is compiled into one class tree, and different programs started by running <code>Main</code> from different packages.
There is no packaging into subsets of classes for the different programs at this point.
The unit tests are also compiled into one class tree.
Some relevant Ant targets are:

- `ant compile` compiles the main sources into classes
- `ant compile-tests` compiles the unit tests into classes.
  Requires the main sources to be compiled.
- `ant compile-pitfill` preprocesses the sources and compiles the result.
  See the next section about preprocessing.

## PYL Preprocessing

The code in the source tree is fully functional. For the exercises, selected parts are removed to introduce the bugs that students have to fix.
This is achieved by processing the source code before compilation.
Special comments in the code indicate which parts to remove. For example:

```java
    // PYL:keep
    Missing.here("bind a socket to a hostname");
    Missing.pretend(IOException.class);
    // PYL:cut
    Socket so = new Socket();
    so.bind(isa);
    // PYL:end
```

For development, this code is compiled as-is. The calls to class <code>Missing</code> are no-ops there. The subsequent calls perform the expected operation.
To trigger a <code>MissingException</code> during development, call <code>Missing.here(null)</code>, without an argument string.

The source code is pre-processed twice, into a <i>pitted</i> version from which to build student JARs (faulty), and into a <i>filled</i> version from which to build instructor JARs (good).
The Ant targets "preprocess" and "compile-pitfill" take care of preprocessing and of compiling the preprocessed sources, respectively.

For the <i>pitted</i> version, the lines between PYL:keep and PYL:cut are retained, while the lines between PYL:cut and PYL:end get removed.
In the faulty JARs built from these sources, calls to <code>Missing.here</code> raise an exception. This leads students directly to the point in the code where the functional lines have been removed.

For the <i>filled</i> version, the lines between PYL:keep and PYL:cut get removed, while the lines between PYL:cut and PYL:end are retained.
In the good JARs built from these sources, there are no references to class <code>Missing</code> at all. That class itself is missing.
These JARs can be handed out for reference after an exercise. Without the special comments, the sources are less confusing for the students.

PYL:keep, PYL:cut, and PYL:end lines must always appear in triplets, even if one of the sections is empty.
These lines themselves are removed from both versions. Besides the keyword, they may contain only whitespace and comment markers.
In combination with multi-line comment start <code>/\*</code> and end <code>\*/</code>, this can be used for creative handling of special cases.
For example if the <i>pitted</i> version of a class is abstract, while the full source file would not compile with that keyword:

```java
/* PYL:keep
abstract
// PYL:cut */
// PYL:end
public class FooImpl implements Foo {
    // PYL:keep
    // PYL:cut
    public int bar() { return 8; }
    // PYL:end
}
```
