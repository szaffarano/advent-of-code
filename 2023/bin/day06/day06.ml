open Core

type race =
  { time : int
  ; distance : int
  }

let parse_input a =
  let parse_line prefix line =
    String.substr_replace_all line ~pattern:prefix ~with_:""
    |> String.split ~on:' '
    |> List.filter ~f:(fun s -> String.length s > 0)
  in
  match a with
  | [ raw_time; raw_distance ] ->
    let time = parse_line "Time:" raw_time in
    let distance = parse_line "Distance:" raw_distance in
    List.map2_exn time distance ~f:(fun time distance ->
      { time = time |> Int.of_string; distance = distance |> Int.of_string })
  | _ -> failwith "invalid input"
;;

let part_one data =
  parse_input data
  |> List.map ~f:(fun race ->
    List.range 1 race.time
    |> List.map ~f:(fun i ->
      let remaining = race.time - i in
      let speed = i in
      remaining * speed)
    |> List.filter ~f:(fun chance -> chance > race.distance)
    |> List.length)
  |> List.fold ~init:1 ~f:( * )
;;

let part_two data = List.length data

let () =
  (* let input = "./data/day06-test.txt" in *)
  let input = "./data/day06.txt" in
  let data = Advent.read_lines input in
  (* Part one: ?? *)
  part_one data |> printf "Part one: %d\n";
  (* Part two: ?? *)
  part_two data |> printf "Part two: %d\n"
;;
