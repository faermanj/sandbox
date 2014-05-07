#!/usr/bin/env ruby

STDIN.each_with_index do |line, index|
  next if index == 0
  val = line
  year,temp = val[14,4],val[103,5]
  puts "#{year}\t#{temp}" if temp.to_f < 300.0
end
