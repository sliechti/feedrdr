#!/bin/bash

echo "p1: $1"

if [[ "$1" == *".css" ]]
then
  file=`echo $1 | sed 's/\.css/\.min.css/'`
elif [[ "$1" == *".js" ]]
then
  file=`echo $1 | sed 's/\.js/\.min.js/'`
else
  exit
fi

echo "minifying file $1 to $file"
java -jar yuicompressor-2.4.8.jar $1 -o $file
