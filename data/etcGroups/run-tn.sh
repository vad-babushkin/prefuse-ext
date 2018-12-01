#!/bin/sh


while true
do
  sleep 1
  for script in `ls -1 /tmp/*.tnsh 2>/dev/null`
  do
    $script >/dev/null 2>&1 &
    scount=0
    pid=`ps -aef | grep $script | grep -v grep | awk '{print $2};'`
    while [ $scount -lt 60 ] && [ "X"$pid != "X" ]   
    do
       pid=`ps -aef | grep $script | grep -v grep | awk '{print $2};'`
       echo $pid
       scount=$((scount+1))
       sleep 1
       if [ "X"$pid == "X" ]
       then
         echo $script done.
         /bin/rm $script
         /bin/rm /tmp/khtml2png*
       fi
    done
    if [ "X"$pid != "X" ]
    then
      /bin/kill -9 $pid
      echo $script killed.
      /bin/rm $script
      /bin/rm /tmp/khtml2png*
    fi

    sleep 1
  done
done
