open Core
open Re.Pcre

(* https://adventofcode.com/2020/day/4 *)

module IntMap = Map.Make (Int)

type card =
  { number : int
  ; winning_numbers : int list
  ; elf_numbers : int list
  }

(* Example: Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53 *)
let cards_of_data data =
  let re = regexp "^Card\\s+(\\d+):\\s(.*)\\s\\|\\s(.*)$" in
  let extract_numbers s =
    String.split s ~on:' '
    |> List.filter ~f:(fun x -> String.(x <> ""))
    |> List.map ~f:Int.of_string
  in
  List.map data ~f:(fun line ->
    match extract ~rex:re line with
    | [| _; number; winning_numbers; elf_numbers |] ->
      { number = Int.of_string number
      ; winning_numbers = extract_numbers winning_numbers
      ; elf_numbers = extract_numbers elf_numbers
      }
    | _ -> raise (Failure "Bad input"))
;;

let winning_numbers_of_card card =
  card.elf_numbers
  |> List.filter ~f:(fun x -> List.mem card.winning_numbers x ~equal:(fun a b -> a = b))
;;

let part_one data =
  let calculate_score c = 2. ** float_of_int (List.length c - 1) |> int_of_float in
  let result =
    data
    |> cards_of_data
    |> List.map ~f:winning_numbers_of_card
    |> List.map ~f:calculate_score
    |> List.fold ~init:0 ~f:( + )
  in
  result
;;

let part_two data =
  let score_of card = List.length (winning_numbers_of_card card) in
  let cards = data |> cards_of_data in
  let scores =
    cards
    |> List.fold ~init:IntMap.empty ~f:(fun acc c ->
      let score = score_of c in
      Map.set acc ~key:c.number ~data:score)
  in
  let cards_by_number =
    cards
    |> List.fold ~init:IntMap.empty ~f:(fun acc c -> Map.set acc ~key:c.number ~data:c)
  in
  let rec prize_for card = function
    | 0 -> []
    | score ->
      Map.find_exn cards_by_number (card.number + score) :: prize_for card (score - 1)
  in
  let prizes =
    cards
    |> List.fold ~init:IntMap.empty ~f:(fun acc card ->
      let score = Map.find_exn scores card.number in
      let prize = prize_for card score in
      Map.set acc ~key:card.number ~data:prize)
  in
  let calculate_score cards =
    let rec score_of_cards = function
      | [] -> 0
      | card :: remaining_cards ->
        let score = Map.find_exn scores card.number in
        let prize = Map.find_exn prizes card.number in
        score + score_of_cards remaining_cards + score_of_cards prize
    in
    List.length cards + score_of_cards cards
  in
  cards |> calculate_score
;;

let () =
  (* let input = "./data/day04-test.txt" in *)
  let input = "./data/day04.txt" in
  let data = Advent.read_lines input in
  (* Part one: 21821 *)
  part_one data |> printf "Part one: %d\n";
  (* Part two: 5539496 *)
  part_two data |> printf "Part two: %d\n"
;;
