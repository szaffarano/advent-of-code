open Advent
open Core

let rec digits_p1 = function
  | [] -> []
  | h :: t ->
    (match h with
     | '0' .. '9' as d -> int_of_string (Char.to_string d) :: digits_p1 t
     | _ -> digits_p1 t)
;;

let rec digits_p2 = function
  | [] -> []
  | 'o' :: 'n' :: 'e' :: t -> 1 :: digits_p2 ('n' :: 'e' :: t)
  | 't' :: 'w' :: 'o' :: t -> 2 :: digits_p2 ('w' :: 'o' :: t)
  | 't' :: 'h' :: 'r' :: 'e' :: 'e' :: t -> 3 :: digits_p2 ('h' :: 'r' :: 'e' :: 'e' :: t)
  | 'f' :: 'o' :: 'u' :: 'r' :: t -> 4 :: digits_p2 ('o' :: 'u' :: 'r' :: t)
  | 'f' :: 'i' :: 'v' :: 'e' :: t -> 5 :: digits_p2 ('i' :: 'v' :: 'e' :: t)
  | 's' :: 'i' :: 'x' :: t -> 6 :: digits_p2 ('i' :: 'x' :: t)
  | 's' :: 'e' :: 'v' :: 'e' :: 'n' :: t -> 7 :: digits_p2 ('e' :: 'v' :: 'e' :: 'n' :: t)
  | 'e' :: 'i' :: 'g' :: 'h' :: 't' :: t -> 8 :: digits_p2 ('i' :: 'g' :: 'h' :: 't' :: t)
  | 'n' :: 'i' :: 'n' :: 'e' :: t -> 9 :: digits_p2 ('i' :: 'n' :: 'e' :: t)
  | h :: t ->
    (match h with
     | '0' .. '9' as d -> int_of_string (Char.to_string d) :: digits_p2 t
     | _ -> digits_p2 t)
;;

let first_element = function
  | [] -> raise (Failure "empty list")
  | h :: _ -> h
;;

let last_element digits = List.rev digits |> first_element

let rec result digits_resolver data =
  let _sum = result digits_resolver in
  match data with
  | [] -> 0
  | "" :: t -> _sum t
  | h :: t ->
    let digits = String.to_list h |> digits_resolver in
    let first = first_element digits |> Int.to_string in
    let last = last_element digits |> Int.to_string in
    let partial = first ^ last |> int_of_string in
    partial + _sum t
;;

let part_one data = result digits_p1 data
let part_two data = result digits_p2 data

let () =
  let input = "./data/day01-a.txt" in
  let data = read_lines input in
  part_one data |> printf "Part one: %d\n";
  part_two data |> printf "Part two: %d\n"
;;
