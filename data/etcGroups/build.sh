#!/bin/bash

rm *.class
javac -classpath $CLASSPATH -deprecation DescriptionMouseListener.java
javac -classpath $CLASSPATH:./prefuse.jar -deprecation AggregateDragControl.java
javac -classpath $CLASSPATH:./prefuse.jar -deprecation NodeDragControl.java
javac -classpath $CLASSPATH:./prefuse.jar:. -deprecation AggregateLayout.java
javac -classpath $CLASSPATH:./prefuse.jar:. -deprecation etcGroupsML.java
javac -classpath $CLASSPATH:./prefuse.jar:. -deprecation etcGroupsQL.java
javac -classpath $CLASSPATH:./prefuse.jar:. -deprecation etcGroupsApplet.java
