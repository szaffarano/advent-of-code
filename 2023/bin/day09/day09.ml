open Core
open Re.Pcre

let rec group = function
  | [] -> []
  | [ a; b ] -> (a, b) :: []
  | a :: b :: t -> (a, b) :: group (b :: t)
  | _ -> failwith "List without even number of elements"
;;

let all_zeroes l = List.for_all l ~f:(Int.equal 0)

let rec upd curr acc =
  let next l =
    group l |> List.fold_right ~init:[] ~f:(fun (a, b) acc -> (b - a) :: acc)
  in
  if all_zeroes curr then curr :: acc else upd (next curr) (curr :: acc)
;;

let part_one data =
  List.map data ~f:(fun l -> String.split l ~on:' ' |> List.map ~f:Int.of_string)
  |> List.map ~f:(fun l -> upd l [])
  |> List.fold ~init:0 ~f:(fun acc partial ->
    let x =
      List.map partial ~f:(fun l -> List.last_exn l)
      |> List.fold ~init:0 ~f:(fun acc l -> acc + l)
    in
    acc + x)
;;

let part_two data =
  List.map data ~f:(fun l -> String.split l ~on:' ' |> List.map ~f:Int.of_string)
  |> List.map ~f:(fun l -> upd l [])
  |> List.fold ~init:0 ~f:(fun acc partial ->
    let x =
      List.map partial ~f:(fun l -> List.hd_exn l)
      |> List.reduce ~f:(fun acc x -> x - acc)
      |> Option.value ~default:0
    in
    acc + x)
;;

let () =
  (* let input = "./data/day09-test.txt" in *)
  let input = "./data/day09.txt" in
  let data = Advent.read_lines input in
  (* Part one:  1987402313 *)
  part_one data |> Fmt.pr "@.Part one: %d@.";
  (* Part two: 900 *)
  part_two data |> Fmt.pr "@.Part two: %d@."
;;
