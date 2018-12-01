#!/usr/bin/perl -w

use strict;
use DBI;
use HTML::Entities;
use utf8;
use Encode;

my $dbUser = 'root';
my $dbPasswd = '';
my $dbName = 'euromovements';
my @groupinfos;

my $orgName=shift;

my $dbh = DBI->connect("DBI:mysql:$dbName", $dbUser, $dbPasswd)
          or die "Couldn't connect to database: " . DBI->errstr;

my $sqlquery = "SELECT directory.id FROM directory, t_town, t_state, t_type1 WHERE ( ( UPPER(name) = UPPER('".$orgName."') ) OR ( UPPER(abbreviation) = UPPER('".$orgName."') ) ) AND directory.id_town=t_town.id_town_en AND directory.id_state=t_state.id_naz AND directory.id_type1=t_type1.id_type1;;";
my $sth = $dbh->prepare($sqlquery) or die "Couldn't prepare statement: " . $dbh->errstr;
$sth->execute() or die "Couldn't execute statement: " . $sth->errstr;

my $nbrecs = $sth->rows;
if ( $nbrecs == 1 )
{
   print STDERR "incredible, i found : ".$orgName."\n";
   @groupinfos = $sth->fetchrow_array();
   print $groupinfos[0]."\n"; 
}
if ( $nbrecs > 1 )
{
   print STDERR "WARNING : disambiguate : ".$orgName." : count : ".$nbrecs."\n";
   print "-1\n"; 
}
if ( $nbrecs == 0 )
{
   print STDERR "ERROR : not found : ".$orgName."\n";
   print "-1\n"; 
}

$sth->finish;
$dbh->disconnect;
