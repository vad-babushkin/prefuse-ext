#!/usr/bin/perl -w

use strict;
use DBI;
use HTML::Entities;
use utf8;
use Encode;

my $dbUser = 'root';
my $dbPasswd = '';
my $dbName = 'nodedges';
my $line;
my $nbnodes;
my $mode;
my $nodeid=-1;
my $nodename="";
my $nodeacr="";
my $nodelocation="";
my $nodecountry="";
my $nodewebsite="";
my $nodecontact="";
my $nodedesc="";
my $nodetype="";
my $nodeimage="";
my $nodetheme="";
my $nodegroup="";
my $nodeesf1="";
my $nodeesf2="";
my $nodeesf3="";
my $edgesource=-1;
my $edgetarget=-1;
my $edgetype="";
my $gmlFile = shift;

my $dbh = DBI->connect("DBI:mysql:$dbName", $dbUser, $dbPasswd)
          or die "Couldn't connect to database: " . DBI->errstr;

my $sqlquery = "DELETE FROM nodes;";
my $sth = $dbh->prepare($sqlquery) or die "Couldn't prepare statement: " . $dbh->errstr;
$sth->execute() or die "Couldn't execute statement: " . $sth->errstr;
$sth->finish;

$sqlquery = "DELETE FROM edges;";
$sth = $dbh->prepare($sqlquery) or die "Couldn't prepare statement: " . $dbh->errstr;
$sth->execute() or die "Couldn't execute statement: " . $sth->errstr;
$sth->finish;

# read GraphML input file
$nbnodes=0;
open GMLFILE, "<$gmlFile" or die "Cannot open $gmlFile for reading!";
binmode GMLFILE;
$mode="";
while ( <GMLFILE> )
{
    if ( m/\<edge source=\'(\d+)\' target=\'(\d+)\'\>/ )
    {
       $mode = "edge";
       #print "new edge : source : ".$edgesource." target : ".$edgetarget."\n";
       if ( ( $edgesource != -1 ) && ( $edgetarget != -1 ) )
       {
          $sqlquery = "INSERT INTO edges (source, target, type) VALUES ( $edgesource, $edgetarget, '$edgetype' );";
          #print "sqlquery : ".$sqlquery."\n";
          $sth = $dbh->prepare($sqlquery) or die "Couldn't prepare statement: " . $dbh->errstr;
          $sth->execute() or die "Couldn't execute statement: " . $sth->errstr;
          $sth->finish;

          $edgesource = -1;
          $edgetarget = -1;
          $edgetype = "";
       }
       $edgesource = $1;
       $edgetarget = $2;
    }
    if ( ( m/\<data key=\'type\'\>(.*)\<\/data\>/ ) && ( $mode eq "edge" ) )
    {
       $edgetype = $1;
       $edgetype =~ s/'/\\'/g;
       $edgetype =~ s/!/\\!/g;
       #print "new edge : type : ".$edgetype."\n";
    }
    if ( m/\<data key=\'nid\'\>(\d+)\<\/data\>/ )
    {
       $mode = "node";
       if ( $nodeid != -1 )
       {
          if ( $nodename eq "" )
          {
             $nodename = "na";
          }
          if ( $nodeacr eq "" )
          {
             $nodeacr = $nodename;
          }
          if ( $nodelocation eq "" )
          {
             $nodelocation = "na";
          }
          if ( $nodecountry eq "" )
          {
             $nodecountry = "na";
          }
          if ( $nodewebsite eq "" )
          {
             $nodewebsite = "na";
          }
          if ( $nodecontact eq "" )
          {
             $nodecontact = "na";
          }
          if ( $nodedesc eq "" )
          {
             $nodedesc = "na";
          }
          if ( $nodetype eq "" )
          {
             $nodetype = "na";
          }
          if ( $nodeimage eq "" )
          {
             $nodeimage = "na";
          }
          if ( $nodetheme eq "" )
          {
             $nodetheme = "na";
          }
          if ( $nodegroup eq "" )
          {
             $nodegroup = "na";
          }
          if ( $nodeesf1 eq "" )
          {
             $nodeesf1 = "na";
          }
          if ( $nodeesf2 eq "" )
          {
             $nodeesf2 = "na";
          }
          if ( $nodeesf3 eq "" )
          {
             $nodeesf3 = "na";
          }

          $sqlquery = "INSERT INTO nodes (nid, name, acronym, location, country, website, contact, description, type, image, theme, groups, esf1, esf2, esf3 ) VALUES ( $nodeid, '$nodename', '$nodeacr', '$nodelocation', '$nodecountry', '$nodewebsite', '$nodecontact', '$nodedesc', '$nodetype', '$nodeimage', '$nodetheme', '$nodegroup', '$nodeesf1', '$nodeesf2', '$nodeesf3' );";
          #print "sqlquery : ".$sqlquery."\n";
          $sth = $dbh->prepare($sqlquery) or die "Couldn't prepare statement: " . $dbh->errstr;
          $sth->execute() or die "Couldn't execute statement: " . $sth->errstr;
          $sth->finish;

          $nodename="";
          $nodeacr="";
          $nodelocation="";
          $nodecountry="";
          $nodewebsite="";
          $nodecontact="";
          $nodedesc="";
          $nodetype="";
          $nodeimage="";
          $nodetheme="";
          $nodegroup="";
          $nodeesf1="";
          $nodeesf2="";
          $nodeesf3="";
       }
       $nodeid = $1;
       #print "new node : ".$nodeid."\n";
    } 
    if ( m/\<data key=\'name\'\>(.*)\<\/data\>/ )
    {
       $nodename = $1;
       if ( $nodename eq "" )
       {
          $nodename = "na";
       }
       $nodename =~ s/'/\\'/g;
       $nodename =~ s/!/\\!/g;
       #print "new node : ".$nodename."\n";
    }
    if ( m/\<data key=\'acronym\'\>(.*)\<\/data\>/ )
    {
       $nodeacr = $1;
       if ( $nodeacr eq "" )
       {
          $nodeacr = "na";
       }
       $nodeacr =~ s/'/\\'/g;
       $nodeacr =~ s/!/\\!/g;
       #print "new node : ".$nodeacr."\n";
    }
    if ( m/\<data key=\'location\'\>(.*)\<\/data\>/ )
    {
       $nodelocation = $1;
       if ( $nodelocation eq "" )
       {
          $nodelocation = "na";
       }
       $nodelocation =~ s/'/\\'/g;
       $nodelocation =~ s/!/\\!/g;
       #print "new node : ".$nodelocation."\n";
    }
    if ( m/\<data key=\'country\'\>(.*)\<\/data\>/ )
    {
       $nodecountry = $1;
       if ( $nodecountry eq "" )
       {
          $nodecountry = "na";
       }
       $nodecountry =~ s/'/\\'/g;
       $nodecountry =~ s/!/\\!/g;
       #print "new node : ".$nodecountry."\n";
    }
    if ( m/\<data key=\'website\'\>(.*)\<\/data\>/ )
    {
       $nodewebsite = $1;
       if ( $nodewebsite eq "" )
       {
          $nodewebsite = "na";
       }
       $nodewebsite =~ s/'/\\'/g;
       $nodewebsite =~ s/!/\\!/g;
       #print "new node : ".$nodewebsite."\n";
    }
    if ( m/\<data key=\'contact\'\>(.*)\<\/data\>/ )
    {
       $nodecontact = $1;
       if ( $nodecontact eq "" )
       {
          $nodecontact = "na";
       }
       $nodecontact =~ s/'/\\'/g;
       $nodecontact =~ s/!/\\!/g;
       #print "new node : ".$nodecontact."\n";
    }
    if ( m/\<data key=\'description\'\>(.*)\<\/data\>/ )
    {
       $nodedesc = $1;
       if ( $nodedesc eq "" )
       {
          $nodedesc = "na";
       }
       $nodedesc =~ s/'/\\'/g;
       $nodedesc =~ s/!/\\!/g;
       #print "new node : ".$nodedesc."\n";
    }
    if ( m/\<data key=\'type\'\>(.*)\<\/data\>/ && ( $mode eq "node" ) )
    {
       $nodetype = $1;
       if ( $nodetype eq "" )
       {
          $nodetype = "na";
       }
       $nodetype = encode_entities( $nodetype );
       #print "new node : ".$nodetype."\n";
    }
    if ( m/\<data key=\'image\'\>(.*)\<\/data\>/ )
    {
       $nodeimage = $1;
       if ( $nodeimage eq "" )
       {
          $nodeimage = "na";
       }
       $nodeimage =~ s/'/\\'/g;
       $nodeimage =~ s/!/\\!/g;
       #print "new node : ".$nodeimage."\n";
    }
    if ( m/\<data key=\'theme\'\>(.*)\<\/data\>/ )
    {
       $nodetheme = $1;
       if ( $nodetheme eq "" )
       {
          $nodetheme = "na";
       }
       $nodetheme =~ s/'/\\'/g;
       $nodetheme =~ s/!/\\!/g;
       #print "new node : ".$nodetheme."\n";
    }
    if ( m/\<data key=\'group\'\>(.*)\<\/data\>/ )
    {
       $nodegroup = $1;
       if ( $nodegroup eq "" )
       {
          $nodegroup = "na";
       }
       $nodegroup =~ s/'/\\'/g;
       $nodegroup =~ s/!/\\!/g;
       #print "new node : ".$nodegroup."\n";
    }
    if ( m/\<data key=\'esf1\'\>(.*)\<\/data\>/ )
    {
       $nodeesf1 = $1;
       if ( $nodeesf1 eq "" )
       {
          $nodeesf1 = "na";
       }
       $nodeesf1 =~ s/'/\\'/g;
       $nodeesf1 =~ s/!/\\!/g;
       #print "new node : ".$nodeesf1."\n";
    }
    if ( m/\<data key=\'esf2\'\>(.*)\<\/data\>/ )
    {
       $nodeesf2 = $1;
       if ( $nodeesf2 eq "" )
       {
          $nodeesf2 = "na";
       }
       $nodeesf2 =~ s/'/\\'/g;
       $nodeesf2 =~ s/!/\\!/g;
       #print "new node : ".$nodeesf2."\n";
    }
    if ( m/\<data key=\'esf3\'\>(.*)\<\/data\>/ )
    {
       $nodeesf3 = $1;
       if ( $nodeesf3 eq "" )
       {
          $nodeesf3 = "na";
       }
       $nodeesf3 =~ s/'/\\'/g;
       $nodeesf3 =~ s/!/\\!/g;
       #print "new node : ".$nodeesf3."\n";
    }
}
close GMLFILE;

$dbh->disconnect();
