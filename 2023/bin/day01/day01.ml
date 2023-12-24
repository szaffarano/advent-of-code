open Advent
open Core

let rec digits_part_one = function
  | [] ->
      []
  | h :: t -> (
    match h with
    | '0' .. '9' as d ->
        int_of_string (Char.to_string d) :: digits_part_one t
    | _ ->
        digits_part_one t )

let rec digits_part_two = function
  | [] ->
      []
  | 'o' :: 'n' :: 'e' :: t ->
      1 :: digits_part_two ('n' :: 'e' :: t)
  | 't' :: 'w' :: 'o' :: t ->
      2 :: digits_part_two ('w' :: 'o' :: t)
  | 't' :: 'h' :: 'r' :: 'e' :: 'e' :: t ->
      3 :: digits_part_two ('h' :: 'r' :: 'e' :: 'e' :: t)
  | 'f' :: 'o' :: 'u' :: 'r' :: t ->
      4 :: digits_part_two ('o' :: 'u' :: 'r' :: t)
  | 'f' :: 'i' :: 'v' :: 'e' :: t ->
      5 :: digits_part_two ('i' :: 'v' :: 'e' :: t)
  | 's' :: 'i' :: 'x' :: t ->
      6 :: digits_part_two ('i' :: 'x' :: t)
  | 's' :: 'e' :: 'v' :: 'e' :: 'n' :: t ->
      7 :: digits_part_two ('e' :: 'v' :: 'e' :: 'n' :: t)
  | 'e' :: 'i' :: 'g' :: 'h' :: 't' :: t ->
      8 :: digits_part_two ('i' :: 'g' :: 'h' :: 't' :: t)
  | 'n' :: 'i' :: 'n' :: 'e' :: t ->
      9 :: digits_part_two ('i' :: 'n' :: 'e' :: t)
  | h :: t -> (
    match h with
    | '0' .. '9' as d ->
        int_of_string (Char.to_string d) :: digits_part_two t
    | _ ->
        digits_part_two t )

let first_element = function [] -> raise (Failure "empty list") | h :: _ -> h

let last_element digits = List.rev digits |> first_element

let rec sum_up digits_resolver data =
  let _sum = sum_up digits_resolver in
  match data with
  | [] ->
      0
  | "" :: t ->
      _sum t
  | h :: t ->
      let digits = String.to_list h |> digits_resolver in
      let first = first_element digits |> Int.to_string in
      let last = last_element digits |> Int.to_string in
      (first ^ last |> int_of_string) + _sum t

let part_one input = read_lines input |> sum_up digits_part_one

let part_two input = read_lines input |> sum_up digits_part_two

let () =
  let input = "./data/day01-a.txt" in
  part_one input |> printf "Part one: %d\n" ;
  part_two input |> printf "Part two: %d\n"
