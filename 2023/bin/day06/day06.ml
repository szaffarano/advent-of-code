open Core

let part_one data = List.length data
let part_two data = List.length data

let () =
  let input = "./data/day06-test.txt" in
  (* let input = "./data/day06.txt" in *)
  let data = Advent.read_lines input in
  (* Part one: ?? *)
  part_one data |> printf "Part one: %d\n";
  (* Part two: ?? -> very slow! it takes ~500 secs :( *)
  part_two data |> printf "Part two: %d\n"
;;
