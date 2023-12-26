open Core
open Re.Pcre

(* https://adventofcode.com/2020/day/4 *)

type card =
  { winning_numbers : int list
  ; elf_numbers : int list
  }

let part_one data =
  let re = regexp "^Card\\s+\\d+:\\s(.*)\\s\\|\\s(.*)$" in
  let extract_numbers s =
    String.split s ~on:' '
    |> List.filter ~f:(fun x -> String.(x <> ""))
    |> List.map ~f:Int.of_string
  in
  let winner_numbers c =
    c.elf_numbers
    |> List.filter ~f:(fun x -> List.mem c.winning_numbers x ~equal:(fun a b -> a = b))
  in
  let calculate_score c = 2. ** float_of_int (List.length c - 1) |> int_of_float in
  let result =
    List.map data ~f:(fun line ->
      match extract ~rex:re line with
      | [| _; winning_numbers; elf_numbers |] ->
        { winning_numbers = extract_numbers winning_numbers
        ; elf_numbers = extract_numbers elf_numbers
        }
      | _ -> raise (Failure "Bad input"))
    |> List.map ~f:winner_numbers
    |> List.map ~f:calculate_score
    |> List.fold ~init:0 ~f:( + )
  in
  result
;;

let part_two data = List.length data

let () =
  let input = "./data/day04.txt" in
  let data = Advent.read_lines input in
  (* Part one: 21821 *)
  part_one data |> printf "Part one: %d\n";
  (* Part two: ?? *)
  part_two data |> printf "Part two: %d\n"
;;
