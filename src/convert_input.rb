#!/usr/bin/env ruby

SIZE = gets.chomp.to_i
(X_Origin,Y_Origin) = gets.chomp.split.map { |c| c.to_i }

wumpus_world = []
SIZE.times do |row|

