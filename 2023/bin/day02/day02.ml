open Core
open Re.Pcre
module StringMap = Map.Make (String)

(*
   Example line:
   Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
*)
let parse_line line =
  let re = regexp "^Game\\s(\\d+):\\s(.*)$" in
  match extract ~rex:re line with
  | [| _; game; body |] -> game, body
  | _ -> raise (Failure "Invalid line")
;;

(*
   Gets a semicolon-separated list of sets and returns a map with the color
   as key and the max occurrences as value, e.g.:
   sets = "3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green"
   result = [("blue", 6); ("red", 4); ("green", 2)]
*)
let parse_game sets =
  let re = regexp "\\s?(\\d+)\\s(\\w+)" in
  String.split ~on:';' sets
  |> List.concat_map ~f:(fun set ->
    String.split ~on:',' set
    |> List.map ~f:(fun sample ->
      match extract ~rex:re sample with
      | [| _; count; color |] -> color, Int.of_string count
      | _ -> raise (Failure "Invalid line")))
  |> List.fold ~init:StringMap.empty ~f:(fun acc set ->
    let color = fst set in
    let cubes = snd set in
    let c =
      match Map.find acc color with
      | Some current -> Int.max current cubes
      | None -> cubes
    in
    Map.set acc ~key:color ~data:c)
;;

(* only 12 red cubes, 13 green cubes, and 14 blue cubes *)
let part_one data =
  List.filter data ~f:(fun s -> String.length s > 0)
  |> List.map ~f:parse_line
  |> List.map ~f:(fun line -> int_of_string (fst line), parse_game (snd line))
  |> List.filter ~f:(fun (_, sets) ->
    let red = Map.find_exn sets "red" in
    let green = Map.find_exn sets "green" in
    let blue = Map.find_exn sets "blue" in
    red <= 12 && green <= 13 && blue <= 14)
  |> List.map ~f:(fun (game, _) -> game)
  |> List.fold ~init:0 ~f:( + )
;;

let part_two data =
  let calculate_score ~key:_ ~data:count acc = acc * count in
  List.filter data ~f:(fun s -> String.length s > 0)
  |> List.map ~f:parse_line
  |> List.map ~f:snd
  |> List.map ~f:parse_game
  |> List.map ~f:(fun game -> Map.fold game ~init:1 ~f:calculate_score)
  |> List.fold ~init:0 ~f:( + )
;;

let () =
  let input = "./data/day02.txt" in
  let data = Advent.read_lines input in
  part_one data |> printf "Part one: %d\n";
  part_two data |> printf "Part two: %d\n"
;;
