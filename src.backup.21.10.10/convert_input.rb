#!/usr/bin/env ruby

(ROWS,COLS) = gets.chomp.split.map { |c| c.to_i }
(X_Origin,Y_Origin) = gets.chomp.split.map { |c| c.to_i }

translate = {
	"0" => "0",
	"B" => "1",
	"S" => "2",
	"G" => "3",
	"BS" => "4",
	"BG" => "5",
	"SG" => "6",
	"BSG" => "7"
}

wumpus_world = []
ROWS.times { wumpus_world << [0]*COLS }
ROWS.times do |i|
	row = gets.chomp.split
	COLS.times do |j|
		wumpus_world[j][i] = translate[row[j]]
	end
end
wumpus_world.map { |col| col.reverse! }

