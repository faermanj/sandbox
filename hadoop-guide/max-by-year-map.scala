#!/usr/bin/env scala

io.Source.stdin.getLines
  .drop(1)
  .map { line => (line.substring(14,18).trim, line.substring(103,108).trim.toDouble) }
  .foreach { case (year,temp) => if (temp < 300) println(s"$year\t$temp") }
