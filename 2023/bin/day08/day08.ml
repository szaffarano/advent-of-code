open Core

let part_one _ = 0
let part_two _ = 0

let () =
  let input = "./data/day08-test.txt" in
  (* let input = "./data/day08.txt" in *)
  let data = Advent.read_lines input in
  (* Part one:  ?? *)
  part_one data |> Fmt.pr "@.Part one: %d@.";
  (* Part two: ?? *)
  part_two data |> Fmt.pr "@.Part two: %d@."
;;
