open Core
open Re.Pcre

let rec group = function
  | [] -> []
  | [ a; b ] -> (a, b) :: []
  | a :: b :: t -> (a, b) :: group (b :: t)
  | _ -> failwith "List without even number of elements"
;;

let all_zeroes l = List.for_all l ~f:(fun x -> x = 0)

let part_one data =
  let next l =
    group l |> List.fold ~init:[] ~f:(fun acc p -> (snd p - fst p) :: acc) |> List.rev
  in
  let rec upd curr acc =
    if all_zeroes curr
    then curr :: acc
    else (
      let u = next curr in
      upd u (curr :: acc))
  in
  List.map data ~f:(fun l -> String.split l ~on:' ' |> List.map ~f:Int.of_string)
  |> List.map ~f:(fun l -> upd l [])
  |> List.fold ~init:0 ~f:(fun acc partial ->
    let x = List.fold partial ~init:0 ~f:(fun acc l -> acc + List.last_exn l) in
    acc + x)
;;

let part_two data = 0

let () =
  (* let input = "./data/day09-test.txt" in *)
  let input = "./data/day09.txt" in
  let data = Advent.read_lines input in
  (* Part one:  1987402313 *)
  part_one data |> Fmt.pr "@.Part one: %d@.";
  (* Part two: ?? *)
  part_two data |> Fmt.pr "@.Part two: %d@."
;;
