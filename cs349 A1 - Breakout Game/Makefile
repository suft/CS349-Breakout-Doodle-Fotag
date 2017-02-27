JFLAGS = -g
JC = javac
JVM= java

.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java
CLASSES = \
		Default.java \
		GameObject.java \
		Canvas.java \
		Breakout.java 

MAIN = Breakout

default: classes
classes: $(CLASSES:.java=.class)
run: $(MAIN).class
		$(JVM) $(MAIN)
clean:
		$(RM) *.class

