#!/usr/bin/perl -w

use strict;
use DBI;
use HTML::Entities;
use utf8;
use Encode;

my $dbUser = 'root';
my $dbPasswd = '';
my $dbName = 'euromovements';
my $imgFile;
my $nextnode;
my @groupinfos;
my @typeinfos;
my @themeinfos;
my @networkinfos;
my $networkdesc;
my $edgeid=0;
my @lnodes;
my $lnodei;
my %existingnodes = ();
my %nodethemes = ();
my %networks = ();
my $kf;
my $nbnodes=0;
my $seminaryFile=shift;
my @fields;
my @fieldsold;
my $line;
my $organisation;
my $titulo="";
my $nbrecs;
my $orgid;
my $acronym;
my $nborgs;
my @linkedorgs;

print "<?xml version='1.0' encoding='UTF-8'?>
<graphml xmlns='http://graphml.graphdrawing.org/xmlns'>
<graph edgedefault='directed'>

<!-- data schema -->
<key id='nid' for='node' attr.name='nid' attr.type='integer'/>
<key id='name' for='node' attr.name='name' attr.type='string'/>
<key id='acronym' for='node' attr.name='acronym' attr.type='string'/>
<key id='location' for='node' attr.name='location' attr.type='string'/>
<key id='country' for='node' attr.name='country' attr.type='string'/>
<key id='website' for='node' attr.name='website' attr.type='string'/>
<key id='contact' for='node' attr.name='contact' attr.type='string'/>
<key id='description' for='node' attr.name='description' attr.type='string'/>
<key id='type' for='node' attr.name='type' attr.type='string'/>
<key id='image' for='node' attr.name='image' attr.type='string'/>
<key id='theme' for='node' attr.name='theme' attr.type='string'/>
<key id='group' for='node' attr.name='group' attr.type='string'/>
<key id='esf1' for='node' attr.name='esf1' attr.type='string'/>
<key id='esf2' for='node' attr.name='esf2' attr.type='string'/>
<key id='esf3' for='node' attr.name='esf3' attr.type='string'/>
<key id='type' for='edge' attr.name='type' attr.type='string'/>

";

my $dbh = DBI->connect("DBI:mysql:$dbName", $dbUser, $dbPasswd)
          or die "Couldn't connect to database: " . DBI->errstr;

my $sqlquery = "SELECT t_theme1.id_theme1, t_theme1.theme1 FROM t_theme1;";
my $sth = $dbh->prepare($sqlquery) or die "Couldn't prepare statement: " . $dbh->errstr;
$sth->execute() or die "Couldn't execute statement: " . $sth->errstr;

while ( @themeinfos = $sth->fetchrow_array() )
{
  $nodethemes{$themeinfos[0]}=$themeinfos[1];
}

$sth->finish;

$sqlquery = "SELECT t_network1.id_network1, t_network1.network1 FROM t_network1;";
$sth = $dbh->prepare($sqlquery) or die "Couldn't prepare statement: " . $dbh->errstr;
$sth->execute() or die "Couldn't execute statement: " . $sth->errstr;

while ( @networkinfos = $sth->fetchrow_array() )
{
  $networks{$networkinfos[0]}=$networkinfos[1];
}

$sth->finish;

$sqlquery = "SELECT directory.id, directory.name, directory.abbreviation, t_town.town_en, t_state.state, directory.url, directory.email1, directory.description, t_type1.type1, directory.id_theme1, directory.id_network1, directory.esf_florence, directory.esf_paris, directory.esf_london FROM directory, t_town, t_state, t_type1 WHERE directory.id_town=t_town.id_town_en AND directory.id_state=t_state.id_naz AND directory.id_type1=t_type1.id_type1;";
$sth = $dbh->prepare($sqlquery) or die "Couldn't prepare statement: " . $dbh->errstr;
$sth->execute() or die "Couldn't execute statement: " . $sth->errstr;

while ( @groupinfos = $sth->fetchrow_array() )
{
  if ( ( $groupinfos[2] eq "" ) ) # special threatment for missing acronym
  {
     $groupinfos[2] = $groupinfos[1];
  }
  if ( $groupinfos[1] eq "" )
  {
     $groupinfos[1] = "na";
  }
  if ( $groupinfos[2] eq "" )
  {
     $groupinfos[2] = $groupinfos[1];
  }
  if ( $groupinfos[5] eq "" )
  {
     $groupinfos[5] = "na";
  }
  if ( $groupinfos[6] eq "" )
  {
     $groupinfos[6] = "na";
  }
  if ( $groupinfos[7] eq "" )
  {
     $groupinfos[7] = "na";
  }
  $groupinfos[5] =~ s/\?/\%3F/g;
  $groupinfos[5] =~ s/=/\%3D/g;
  $groupinfos[5] =~ s/&/\%26/g;
  $groupinfos[5] =~ s/ /\%20/g;
  for ( $kf=0; $kf<=$#groupinfos; $kf++ )
  {
     if ( $groupinfos[$kf] )
     {
        decode_entities($groupinfos[$kf]);
     }
     # Encode::from_to($groupinfos[$kf], "utf8", "iso-8859-1");
  }

  print "<node id='".$nbnodes."'>\n";
  print "<data key='nid'>".$nbnodes."</data>\n";
  print "<data key='name'>".$groupinfos[1]."</data>\n";
  print "<data key='acronym'>".$groupinfos[2]."</data>\n";
  print "<data key='location'>".$groupinfos[3]."</data>\n";
  print "<data key='country'>".$groupinfos[4]."</data>\n";
  print "<data key='website'>".$groupinfos[5]."</data>\n";
  print "<data key='contact'>".$groupinfos[6]."</data>\n";
  print "<data key='description'>".$groupinfos[7]."</data>\n";
  print "<data key='type'>".$groupinfos[8]."</data>\n";
  $imgFile = $groupinfos[8]; 
  $imgFile =~ s/ /-/g;
  $imgFile =~ s/\//-/g;
  $imgFile = "images/".$imgFile.".png";
  `touch $imgFile`; 
  print "<data key='image'>".$imgFile."</data>\n";

  if ( $groupinfos[9] )
  {
    print "<data key='theme'>".$nodethemes{$groupinfos[9]}."</data>\n";
  }
  if ( $groupinfos[10] )
  {
    print "<data key='group'>".$networks{$groupinfos[10]}."</data>\n";
  }
  if ( $groupinfos[11] eq "1" )
  {
    print "<data key='esf1'>Firenze</data>\n";
  }
  if ( $groupinfos[12] eq "1" )
  {
    print "<data key='esf2'>Paris</data>\n";
  }
  if ( $groupinfos[13] eq "1" )
  {
    print "<data key='esf3'>London</data>\n";
  }
  print "</node>\n";

  $existingnodes{$groupinfos[0]}=$nbnodes;
  $nbnodes++;
}

$sth->finish;

$sqlquery = "SELECT t_network1.network1, t_network1.id_network1 FROM t_network1;";
$sth = $dbh->prepare($sqlquery) or die "Couldn't prepare statement: " . $dbh->errstr;
$sth->execute() or die "Couldn't execute statement: " . $sth->errstr;

while ( @networkinfos = $sth->fetchrow_array() )
{
  for ( $kf=0; $kf<=$#networkinfos; $kf++ )
  {
     decode_entities($networkinfos[$kf]);
  }

  $networkdesc = "";
  @lnodes = {};
  $lnodei=0;
  my $sqlqueryb = "SELECT directory.id, directory.name FROM directory WHERE id_network1=".$networkinfos[1].";";
  my $sthb = $dbh->prepare($sqlqueryb) or die "Couldn't prepare statement: " . $dbh->errstr;
  $sthb->execute() or die "Couldn't execute statement: " . $sthb->errstr;

  while ( @groupinfos = $sthb->fetchrow_array() )
  {
     decode_entities($groupinfos[1]);
     $networkdesc .= $groupinfos[1].", ";

     $lnodes[$lnodei]=$groupinfos[0];
     $lnodei++;
  }
  chop( $networkdesc );
  chop( $networkdesc );

  print "<node id='".$nbnodes."'>\n";
  print "<data key='nid'>".$nbnodes."</data>\n";
  print "<data key='name'>".$networkinfos[0]."</data>\n";
  print "<data key='acronym'>".$networkinfos[0]."</data>\n";
  print "<data key='description'>".$networkdesc."</data>\n";
  print "<data key='type'>Network+</data>\n";

  $imgFile = "images/Network.png";
  `touch $imgFile`; 

  print "<data key='image'>".$imgFile."</data>\n";
  print "<data key='group'>".$networkinfos[0]."</data>\n";
  print "<data key='website'>na</data>\n";
  print "<data key='contact'>na</data>\n";
  print "</node>\n";

  for ( $kf=0; $kf<=$#lnodes; $kf++ )
  {
     if ( exists $existingnodes{$lnodes[$kf]} )
     {
       print "<edge source='".$nbnodes."' target='".$existingnodes{$lnodes[$kf]}."'>\n";
       $edgeid++;
       print "<data key='type'>Is Part Of</data>\n";
       print "</edge>\n";
     }
  }

  $nbnodes++;
}

# read seminaries
$nborgs=0;
@linkedorgs={};
open SEMFILE, "<$seminaryFile" or die "Cannot open $seminaryFile for reading!";
binmode SEMFILE;
while ( <SEMFILE> )
{
    $line = $_;
    #print "line : ".$line."\n";
    @fieldsold = @fields;
    @fields = split(/\|/, $line);
    #print "fields0 : ".$fields[0]."\n";
    for ( $kf=0; $kf<$#fields; $kf++ )
    {
       $fields[$kf] =~ s/\"//g;
    }
    if ( ! ( $fields[4] eq $titulo ) )
    {
     # new seminario
     if ( $nborgs>0 )
     {
       print "<node id='".$nbnodes."'>\n";
       print "<data key='nid'>".$nbnodes."</data>\n";
       if ( length($titulo)>0 )
       {
          print "<data key='name'>".$fields[4]."</data>\n";
          print "<data key='acronym'>".$fields[4]."</data>\n";
          print "<data key='description'>".$fields[4]."</data>\n";
       }
       if ( $fields[0] eq "ESF Firenze" )
       {
          print "<data key='location'>Firenze</data>\n";
          print "<data key='country'>Italy</data>\n";
          print "<data key='esf1'>Firenze</data>\n";
       }
       if ( $fields[0] eq "ESF London" )
       {
          print "<data key='location'>London</data>\n";
          print "<data key='country'>England</data>\n";
          print "<data key='esf1'>London</data>\n";
       }
       if ( $fields[0] eq "ESF Paris" )
       {
          print "<data key='location'>Paris</data>\n";
          print "<data key='country'>France</data>\n";
          print "<data key='esf1'>Paris</data>\n";
       }
       print "<data key='website'>na</data>\n";
       print "<data key='contact'>na</data>\n";
       if ( $fields[1] eq "P" )
       {
          print "<data key='type'>Plenary Session</data>\n";
          print "<data key='image'>images/Plenary-Session.png</data>\n";
       }
       if ( $fields[1] eq "S" )
       {
          print "<data key='type'>Seminary</data>\n";
          print "<data key='image'>images/Seminary.png</data>\n";
       }
       if ( $fields[1] eq "W" )
       {
          print "<data key='type'>Workshop</data>\n";
          print "<data key='image'>images/Workshop.png</data>\n";
       }
       print "</node>";
       for ( $kf=0; $kf<$nborgs; $kf++ )
       {
         print "<edge source='".$nbnodes."' target='".$linkedorgs[$kf]."'>\n";
         print "<data key='type'>Participated To</data>\n";
         print "</edge>\n";
       }
       $nbnodes++;
     }
     $nborgs=0;
     @linkedorgs={};
     $titulo = $fields[4];
   }
   $organisation=$fields[8];
   $acronym=$fields[8];
   if ( (length($organisation)>6) && ( substr( $organisation, length($organisation)-1, 1 ) eq ")" ) && 
                                     ( substr( $organisation, length($organisation)-4, 1 ) eq "(" )  )
   {
     $organisation = substr($organisation,0,length($organisation)-5);
   }
   if ( length( $organisation ) > 0  )
   {
      $organisation =~ s/\'/\\'/g;
      $sqlquery = "SELECT directory.id FROM directory, t_town, t_state, t_type1 WHERE ( ( UPPER(name) = UPPER('".$organisation."') ) OR ( UPPER(abbreviation) = UPPER('".$organisation."') ) ) AND directory.id_town=t_town.id_town_en AND directory.id_state=t_state.id_naz AND directory.id_type1=t_type1.id_type1;;";
      $sth = $dbh->prepare($sqlquery) or die "Couldn't prepare statement: " . $dbh->errstr;
      $sth->execute() or die "Couldn't execute statement: " . $sth->errstr;

      $nbrecs = $sth->rows;
      if ( $nbrecs == 1 )
      {
         print STDERR "incredible, i found : ".$organisation."\n";
         @groupinfos = $sth->fetchrow_array();
         $orgid = $groupinfos[0];
      }
      if ( $nbrecs > 1 )
      {
         print STDERR "WARNING : disambiguate : ".$organisation." : count : ".$nbrecs."\n";
         $orgid = -1;
      }
      if ( $nbrecs == 0 )
      {
         $orgid = -1;
      }
   
      if ( $orgid == -1 )
      {
        $acronym =~ s/\'/\\'/g;
        $sqlquery = "SELECT directory.id FROM directory, t_town, t_state, t_type1 WHERE ( ( UPPER(name) = UPPER('".$acronym."') ) OR ( UPPER(abbreviation) = UPPER('".$acronym."') ) ) AND directory.id_town=t_town.id_town_en AND directory.id_state=t_state.id_naz AND directory.id_type1=t_type1.id_type1;;";
        $sth = $dbh->prepare($sqlquery) or die "Couldn't prepare statement: " . $dbh->errstr;
        $sth->execute() or die "Couldn't execute statement: " . $sth->errstr;

        $nbrecs = $sth->rows;
        if ( $nbrecs == 1 )
        {
           print STDERR "incredible, i found : ".$acronym."\n";
           @groupinfos = $sth->fetchrow_array();
           $orgid = $groupinfos[0];
        }
        if ( $nbrecs > 1 )
        {
           print STDERR "WARNING : disambiguate : ".$acronym." : count : ".$nbrecs."\n";
           $orgid = -1;
        }
        if ( $nbrecs == 0 )
        {
           $orgid = -1;
        }
   
      }

      $sth->finish;

      if ( $orgid != -1 )
      {
         $linkedorgs[$nborgs++] = $existingnodes{$orgid};
      }
   }
}
close SEMFILE;

$dbh->disconnect();

print "
</graph>
</graphml>
";
