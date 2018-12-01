#!/usr/bin/python

# multidimensional distance to graphviz export
# original version by Luka Frelih
# modified by Yves Degoyon

import sys
import re
from math import sqrt,ceil
from random import random

print """<?xml version="1.0" encoding="UTF-8"?>
<graphml xmlns="http://graphml.graphdrawing.org/xmlns">
<graph edgedefault="directed">
 
<!-- data schema -->
<key id="nid" for="node" attr.name="nid" attr.type="integer"/>
<key id="name" for="node" attr.name="name" attr.type="string"/>
<key id="acronym" for="node" attr.name="acronym" attr.type="string"/>
<key id="location" for="node" attr.name="location" attr.type="string"/>
<key id="website" for="node" attr.name="website" attr.type="string"/>
<key id="contact" for="node" attr.name="contact" attr.type="string"/>
<key id="description" for="node" attr.name="description" attr.type="string"/>
<key id="eid" for="edge" attr.name="eid" attr.type="integer"/>
<key id="type" for="edge" attr.name="type" attr.type="string"/>
"""
nbnodes=0;
file=open(sys.argv[1], 'r');
data=file.readlines();
for i in range(3,len(data)):
   fields=data[i].split('|');
   if ( len(fields[15])>0 ):
     fields[0]=re.sub('"','',fields[0] );
     fields[2]=re.sub('"','',fields[2] );
     fields[5]=re.sub('"','',fields[5] );
     fields[14]=re.sub('"','',fields[14] );
     fields[12]=re.sub('"','',fields[12] );
     fields[15]=re.sub('"','',fields[15] );
     print "<node id=\"%d\">" % (nbnodes);
     print "<data key=\"nid\">%d</data>" % (nbnodes);
     if ( len(fields[0])>0 ):
        print "<data key=\"name\">%s</data>" % (fields[0]);
     else:
        print "<data key=\"name\">na</data>";
     if ( len(fields[2])>0 ):
        print "<data key=\"acronym\">%s</data>" % (fields[2]);
     else:
        print "<data key=\"acronym\">%s</data>" % (fields[0]);
     if ( len(fields[5])>0 ):
        print "<data key=\"location\">%s</data>" % (fields[5]);
     else:
        print "<data key=\"location\">na</data>";
     if ( len(fields[14])>0 ):
        print "<data key=\"website\">%s</data>" % (fields[14]);
     else:
        print "<data key=\"website\">na</data>";
     if ( len(fields[12])>0 ):
        print "<data key=\"contact\">%s</data>" % (fields[12]);
     else:
        print "<data key=\"contact\">na</data>";
     print "<data key=\"description\">%s</data>" % (fields[15]);
     print "</node>";
     nbnodes=nbnodes+1;

# making fake links for now
nbedges=0;
for i in range(0,nbnodes):
   nblinks=1+int(random()*3);
   for j in range(0,nblinks):
     rdvalue=int(random()*nbnodes);
     print "<edge source=\"%d\" target=\"%d\">" % (i,rdvalue);
     print "<data key=\"eid\">%d</data>" % (nbedges);
     tlink=int(random()*3)%2;
     if (tlink == 0):
       print "<data key=\"type\">knows</data>";
     else:
       print "<data key=\"type\">collaborates</data>";
     print "</edge>";
     nbedges=nbedges+1;

print """
</graph>
</graphml>
"""
