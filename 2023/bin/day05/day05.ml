open Core

type almanac_entry =
  { destination_range_start : int
  ; source_range_start : int
  ; range_length : int
  }

type alamanac =
  { seeds : int list
  ; seed_soil : almanac_entry list
  ; soil_fertilizer : almanac_entry list
  ; fertilizer_water : almanac_entry list
  ; water_light : almanac_entry list
  ; light_temperature : almanac_entry list
  ; temperature_humidity : almanac_entry list
  ; humidity_location : almanac_entry list
  }

let parse_almanac data =
  let find_group raw name =
    List.find_exn raw ~f:(fun x ->
      let group = Array.get (List.to_array x) 0 in
      String.is_prefix group ~prefix:name)
  in
  let parse_numbers line =
    String.split line ~on:' '
    |> List.filter ~f:(fun x -> not (String.is_empty x))
    |> List.map ~f:Int.of_string
  in
  let parse_group lines =
    List.slice lines 1 (List.length lines) |> List.map ~f:parse_numbers
  in
  let entry_of_array = function
    | [ destination_range_start; source_range_start; range_length ] ->
      { destination_range_start; source_range_start; range_length }
    | _ -> raise (Failure "Invalid entry")
  in
  let entries_of_array entries = List.map entries ~f:entry_of_array in
  String.split data ~on:'\n'
  |> List.fold ~init:([], []) ~f:(fun acc l ->
    if String.is_empty l
    then (
      let tmp = [] in
      let acc = (snd acc |> List.rev) :: fst acc in
      acc, tmp)
    else (
      let tmp = l :: snd acc in
      let acc = fst acc in
      acc, tmp))
  |> fst
  |> List.filter ~f:(fun x -> not (List.is_empty x))
  |> List.rev
  |> fun raw ->
  let seeds =
    Array.get (find_group raw "seeds" |> List.to_array) 0
    |> String.substr_replace_all ~pattern:"seeds: " ~with_:""
    |> parse_numbers
  in
  let seed_soil = find_group raw "seed-to-soil" |> parse_group in
  let soil_fertilizer = find_group raw "soil-to-fertilizer" |> parse_group in
  let fertilizer_water = find_group raw "fertilizer-to-water" |> parse_group in
  let water_light = find_group raw "water-to-light" |> parse_group in
  let light_temperature = find_group raw "light-to-temperature" |> parse_group in
  let temperature_humidity = find_group raw "temperature-to-humidity" |> parse_group in
  let humidity_location = find_group raw "humidity-to-location" |> parse_group in
  { seeds
  ; seed_soil = entries_of_array seed_soil
  ; soil_fertilizer = entries_of_array soil_fertilizer
  ; fertilizer_water = entries_of_array fertilizer_water
  ; water_light = entries_of_array water_light
  ; light_temperature = entries_of_array light_temperature
  ; temperature_humidity = entries_of_array temperature_humidity
  ; humidity_location = entries_of_array humidity_location
  }
;;

let value_of_entry entries value =
  match
    List.find entries ~f:(fun entry ->
      if entry.source_range_start <= value
         && value <= entry.source_range_start + entry.range_length
      then true
      else false)
  with
  | Some entry ->
    let delta = value - entry.source_range_start in
    entry.destination_range_start + delta
  | None -> value
;;

let part_one data =
  let almanac = parse_almanac data in
  List.map almanac.seeds ~f:(fun seed ->
    value_of_entry almanac.seed_soil seed
    |> value_of_entry almanac.soil_fertilizer
    |> value_of_entry almanac.fertilizer_water
    |> value_of_entry almanac.water_light
    |> value_of_entry almanac.light_temperature
    |> value_of_entry almanac.temperature_humidity
    |> value_of_entry almanac.humidity_location)
  |> Advent.min_in_list
;;

let part_two data =
  let almanac = parse_almanac data in
  almanac.seeds
  |> Advent.group_into_pairs
  |> List.map ~f:(fun l ->
    match l with
    | [ start; length ] ->
      let seeds = Advent.range start (start + length) in
      Seq.fold_left
        (fun acc seed ->
          let location =
            value_of_entry almanac.seed_soil seed
            |> value_of_entry almanac.soil_fertilizer
            |> value_of_entry almanac.fertilizer_water
            |> value_of_entry almanac.water_light
            |> value_of_entry almanac.light_temperature
            |> value_of_entry almanac.temperature_humidity
            |> value_of_entry almanac.humidity_location
          in
          Int.min acc location)
        Int.max_value
        seeds
    | _ -> raise (Failure "Invalid list"))
  |> Advent.min_in_list
;;

let () =
  let input = "./data/day05-test.txt" in
  (* let input = "./data/day05.txt" in *)
  let data = Advent.read input in
  (* Part one: ?? *)
  part_one data |> printf "Part one: %d\n";
  (* Part two: ?? *)
  part_two data |> printf "Part two: %d\n"
;;
