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

let part_two data =
  let chances =
    parse_input data
    |> List.fold ~init:("", "") ~f:(fun acc race ->
      let time = fst acc in
      let distance = snd acc in
      time ^ Int.to_string race.time, distance ^ Int.to_string race.distance)
    |> fun (time, distance) ->
    { time = Int.of_string time; distance = Int.of_string distance }
    |> fun race ->
    List.range 1 race.time
    |> List.map ~f:(fun i ->
      let remaining = race.time - i in
      let speed = i in
      remaining * speed)
    |> List.filter ~f:(fun chance -> chance > race.distance)
  in
  chances |> List.length
;;

let () =
  (* let input = "./data/day06-test.txt" in *)
  let input = "./data/day06.txt" in
  let data = Advent.read_lines input in
  (* Part one: 1195150 *)
  part_one data |> printf "Part one: %d\n";
  (* Part two: 42550411 *)
  part_two data |> printf "Part two: %d\n"
;;
