RADIAL SPACE-FILLING TREES
--------------------------
The following academic papers are useful references for this technique:

Keith Andrews and Helmut Heidegger. 1998. Information slices: Visualising and
exploring large hierarchies using cascading, semi-circular discs. In Proc. of
IEEE Symp. on Information Visualization (InfoVis), Late Breaking Hot Topics,
pages 9–12.

John Stasko and Eugene Zhang. 2000. Focus+context display and navigation techniques
for enhancing radial, space-filling hierarchy visualizations. In Proc. of
IEEE Symp. on Information Visualization, pages 57–65.

Jing Yang, Matthew O. Ward, and Elke A. Rundensteiner. 2002. InterRing: An
interactive tool for visually navigating and manipulating hierarchical structures.
In Proc. of IEEE Symp. on Information Visualization, pages 77–84.

and this code has been used in the DocuBurst project, a work-in-progress as of
August 2007:

Collins, Christopher. DocuBurst: Radial Space-Filling Visualization of Document 
Content. Technical Report, KMDI, University of Toronto. (KMDI-TR-2007-1).
http://kmdi.utoronto.ca/publications/documents/KMDI-TR-2007-1.pdf

KNOWN BUGS
----------

- for nodes with straight (vs. arched) labels, the font size is sometimes a little too large and the 
  label exceeds the node boundaries
- Sometimes there is a small rendering problem with stray lines extending beyond what should be the 
  end of the straight lines of a node.  I believe this is due to a Java2D bug: 
  http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4253189 but would appreciate any advice to the 
  contrary.
  
VERSION HISTORY
---------------
1.1: Improved calculation of font size for arched labels; removed bug that label sometimes disappeared
     for nodes with ~2pi angular extent (large nodes)
1.2: Fixed dependence on custom prefuse version; no depends on standard prefuse only.
  
Christopher Collins (ccollins@cs.utoronto.ca)