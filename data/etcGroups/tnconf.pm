#!/bin/perl

package tnconf;
require Exporter;
@ISA = qw(Exporter);

$tnnofile = "thumbnails/blank.png";
$display = "localhost:0.0";
$gwidth = 800;
$gheight = 600;
$twidth = 120;
$theight = 80;
$ttime = 30;
$staledelay = 24*60*60;

@EXPORT = qw($tnnofile $display $gwidth $gheight $twidth $theight $ttime $staledelay);
