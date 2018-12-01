#!/usr/bin/perl -w

BEGIN {
push @INC , '../';  
push @INC , '../SVG';
}

use strict;
use tnconf;
use Data::UUID;
use Time::Local;

my $tnurl;
my $etnurl;
my $tnfile;
my $tnfilet;
my $tnfiled;
my $tmpfile;
my $error=0;
my $errmsg;
my $command;
my $result;
my $khtml2png;
my $convert;
my $pwd;
my $dataUuid;
my $cmdFile;
my @fileinfos;
my @dateinfos;
my @timeinfos;
my $ftimestamp;
my $atimestamp;
my ( $FData, @Values, %Form, $Field, $name, $value );

if ($ENV{'REQUEST_METHOD'} eq 'GET')
{
   $FData = $ENV{'QUERY_STRING'}
}
else
{
   read(STDIN, $FData, $ENV{'CONTENT_LENGTH'});
}

$name = substr $FData, 0, index($FData, '=');
$value = substr $FData, index($FData, '=')+1;
$Form{$name} = $value;

$tnurl=$Form{'url'};
$etnurl=$tnurl;
$etnurl =~ s/\?/%3F/g;
$etnurl =~ s/=/%3D/g;
$etnurl =~ s/&/%26/g;
$etnurl =~ s/ /%20/g;
$etnurl =~ s/%/%%/g;
if ( ($tnurl eq 'http://na' ) || ($tnurl eq 'na' ) ) 
{
  $tnfiled = $tnnofile;
}
else
{
  $tmpfile = $tnurl;
  $tmpfile =~ s/^http:\/\///;
  $tmpfile =~ s/\./-/g;
  $tmpfile =~ s/\//-/g;
  $tmpfile =~ s/\?/-/g;
  $tmpfile =~ s/=/-/g;
  $tmpfile =~ s/&/-/g;
  $tnfile = 'thumbnails/'.$tmpfile.'.png';
  $tnfilet = 'thumbnails/'.$tmpfile.'-tn.png';

  $command = 'pwd';
  $pwd = `$command`;
  if ( $pwd eq '' )
  {
     $error = 1;
     $errmsg = 'unable to get current directory';
  }
  chop($pwd);
  
  $command = "stat ".$pwd."/".$tnfilet." 2\>/dev/null | grep Change:";
  $result = `$command`;

  if ( $result eq '' )
  {
    $tnfiled = $tnnofile;
    $result = "Change: 1970-01-01 00:00:00.000000000 +0000";
  }
  else
  {
    $tnfiled = $tnfilet;
  }

  #print $tnfiled."\n";

  @fileinfos = split(/ /, $result );
  @dateinfos = split(/-/, $fileinfos[1] );
  @timeinfos = split(/:/, $fileinfos[2] );
  $ftimestamp = timelocal( $timeinfos[2], $timeinfos[1], $timeinfos[0], $dateinfos[2], $dateinfos[1]-1, $dateinfos[0]-1900 );
  $atimestamp = time();

  # snapshot older than $staledelay
  if ( ($atimestamp-$ftimestamp) > $staledelay )
  {

    $command = 'which khtml2png2';
    $khtml2png = `$command`;
    if ( $khtml2png eq '' )
    {
       $error = 1;
       $errmsg = 'you need khml2png version 2 to see thumbnails';
    }
    chop($khtml2png);
  
    $command = 'which convert';
    $convert = `$command`;
    if ( $convert eq '' )
    {
       $error = 1;
       $errmsg = 'you need Image Magick to see thumbnails';
    }
    chop($convert);
  
    $dataUuid = Data::UUID->new();
    $cmdFile = '/tmp/'.$dataUuid->create_str().'.tnsh';
    open CMDFILE, ">".$cmdFile or $error=1; $errmsg = 'Could not create temporary command file!';
  
    printf CMDFILE "#!/bin/sh\n";
    printf CMDFILE "\n";
    printf CMDFILE "export KDEDIR=/tmp\n";
    printf CMDFILE "export HOME=/tmp\n";
    printf CMDFILE "export DISPLAY=".$display."\n";
  
    $command = $khtml2png.' --width '.$gwidth.' --height '.$gheight.' --time '.$ttime.' '.$etnurl.' '.$pwd.'/'.$tnfile."\n";
    printf CMDFILE $command;
  
    $command = $convert.' -resize '.$twidth.'x'.$theight.' '.$pwd.'/'.$tnfile.' '.$pwd.'/'.$tnfilet."\n";
    printf CMDFILE $command;
  
    close CMDFILE;
  
    $command = 'chmod +x '.$cmdFile;
    $result = `$command`;

  }

}

print "Content-Type: text/html;charset=utf-8\n\n";
print "
<html>
<head>
<title>Thumbnail of $tnurl</title>
</head>
<body bgcolor=#FFFFFF text=#000000>
";

if ($tnfiled eq $tnnofile )
{
  print "<img width=$twidth height=$theight src='".$tnfiled."'>";
}
else
{
  if ( $error > 0 )
  {
     print $errmsg;
  }
  else
  {
     print "<a href='".$tnurl."' target='_blank'><img width=$twidth height=$theight src='".$tnfiled."'></a>";
  }
}

print "
</body>
</html>
";

