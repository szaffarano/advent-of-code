open Core
open Re.Pcre

let part_one data = 0
let part_two data = 0

let () =
  let input = "./data/day09-test.txt" in
  (* let input = "./data/day09.txt" in *)
  let data = Advent.read_lines input in
  (* Part one:  ?? *)
  part_one data |> Fmt.pr "@.Part one: %d@.";
  (* Part two: ?? *)
  part_two data |> Fmt.pr "@.Part two: %d@."
;;
