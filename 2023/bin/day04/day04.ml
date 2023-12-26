open Core

let part_one data = List.length data
let part_two data = List.length data

let () =
  let input = "./data/day04.txt" in
  let data = Advent.read_lines input in
  (* Part one: ?? *)
  part_one data |> printf "Part one: %d\n";
  (* Part two: ?? *)
  part_two data |> printf "Part two: %d\n"
;;