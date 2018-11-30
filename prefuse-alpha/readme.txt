prefuse README

--INTRO--

prefuse is a user interface toolkit for building highly interactive visualizations
of structured and unstructured data. This includes any form of data that can be
represented as a set of entities (or nodes) possibly connected by any number of
relations (or edges). Examples of data supported by prefuse include hierarchies
(organization charts, taxonomies, file systems), networks (computer networks,
social networks, web site linkage) and even non-connected collections of data
(timelines, scatterplots). Using this toolkit, developers can create responsive,
animated graphical interfaces for visualizing, exploring, and manipulating these
various forms of data. prefuse is written in the Java programming language using the
Java2D graphics library and is designed to integrate with any application written
using the Java Swing user interface library.

--REQUIREMENTS--

prefuse is written in Java 1.4, using the Java2D graphics library. To compile the
prefuse code, and to build and run prefuse applications, you'll need this version
of Java or later. You can download it from http://java.sun.com/j2se/1.4.2/download.html

--BUILDING--

prefuse uses the Ant system from the Apache Group to compile the files. Ant is bundled
with this distribution, and can be run using the "build.bat" script (in Windows) or
the "build.sh" script (in UNIX). For example, running "build.bat usage" will provide
a list of available commands and running "build.bat api" will generate the html
API documentation for the toolkit.

Alternatively, you can use the Eclipse integrated development environment (available
for free at http://ww.eclipse.org) to load the source files, then Eclipse will compile
the software for you. Within Eclipse, right-click the background of the "Package
Explorer" panel and choose "Import". Then select "Existing Project into Workspace" and
browse for the prefuse distribution on your hard drive. Once prefuse has been loaded as
a project within Eclipse, you can then run various demos directly from within Eclipse 
by right-clicking a class file for a demo and selecting "Run >> Java Application" from
the menu.

--MORE--

Documentation is a bit sparse right now, as this is only the alpha release. Included
with this distribution is a short prefuse tutorial, in powerpoint format, and a
research paper describing prefuse, in pdf format. You can also generate the prefuse
API documentation using the ANY build scripts. We apologize for the lack of a
comprehensive user's manual at this time, but check http://prefuse.sourceforge.net
for more info as it becomes available.