#!/usr/bin/python

import sys
import os
import re
from math import sqrt,ceil
from random import random

nbnodes=int(sys.argv[2]);
titulo="";
file=open(sys.argv[1], 'r');
data=file.readlines();
for i in range(1,len(data)):
   fields=data[i].split('|');
   for j in range(0,len(fields)-1):
     fields[j]=re.sub('"','',fields[j] );
#     print fields[j];
   if ( fields[4] != titulo ):
     # new seminario
     titulo = fields[4];
     # print "seminario : %s \n" % (titulo);
     print "<node id=\"%d\">" % (nbnodes);
     print "<data key=\"nid\">%d</data>" % (nbnodes);
     if ( len(titulo)>0 ):
        print "<data key=\"name\">%s</data>" % (titulo);
        print "<data key=\"acronym\">%s</data>" % (titulo);
        print "<data key=\"description\">%s</data>" % (titulo);
     if ( fields[0]=="ESF Firenze" ):
        print "<data key=\"location\">Firenze</data>";
        print "<data key=\"country\">Italy</data>";
        print "<data key=\"esf1\">Firenze</data>";
     if ( fields[0]=="ESF London" ):
        print "<data key=\"location\">London</data>";
        print "<data key=\"country\">England</data>";
        print "<data key=\"esf1\">London</data>";
     if ( fields[0]=="ESF Paris" ):
        print "<data key=\"location\">Paris</data>";
        print "<data key=\"country\">France</data>";
        print "<data key=\"esf1\">Paris</data>";
     print "<data key=\"website\">na</data>";
     print "<data key=\"contact\">na</data>";
     if ( fields[1] == "P" ):
        print "<data key=\"type\">Plenary Session</data>";
        print "<data key=\"image\">images/Plenary-Session.png</data>";
     if ( fields[1] == "S" ):
        print "<data key=\"type\">Seminary</data>";
        print "<data key=\"image\">images/Seminary.png</data>";
     if ( fields[1] == "W" ):
        print "<data key=\"type\">Workshop</data>";
        print "<data key=\"image\">images/Workshop.png</data>";
     print "</node>";
     nbnodes=nbnodes+1;
   organisation=fields[8];
   if ( (len(organisation)>6) and organisation[len(organisation)-1]==')' and organisation[len(organisation)-4]=='(' ):
     organisation = organisation[0, len(organisation)-6];
   if ( organisation != "" ):
     command = "./getnodeid.pl \""+organisation+"\"";
     file = os.popen(command);
     orgid = int(file.read());
     file.close();
     #print "command : %s : got : %d\n" % (command, orgid);
     if ( orgid != -1 ):
      print "<edge source='%d' target='%d'>" % (nbnodes-1,orgid);
      print "<data key='type'>Participated To</data>";
      print "</edge>";
