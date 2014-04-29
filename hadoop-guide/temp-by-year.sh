#!/usr/bin/env bash

for yearDir in /ncdc/gsod/*
do
  year=$(basename $yearDir)
  maxTemp=$(awk '{ temp = substr($0,103,6) + 0;
		   if (temp < 300 && temp > max) max = temp;
                 } END {print max}' $yearDir/*)
  echo "$year $maxTemp"
done
