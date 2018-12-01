#!/bin/bash

host=$1
echo "setting hostname to : $host ..."

for file in `ls -1 fapplet*.html`
do 
  sed -e "s/localhost/$host/g" $file > $file.new
  mv $file.new $file
done
