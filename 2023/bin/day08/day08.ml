open Core
open Re.Pcre

type node =
  { name : string
  ; left : string
  ; right : string
  }
[@@deriving show { with_path = false }]

type directions =
  { movements : char list
  ; coords : node Advent.StringMap.t
  }

let parse_input data =
  let movements : char list = List.hd_exn data |> String.to_list in
  let coords =
    List.slice data 1 (List.length data)
    |> List.filter ~f:(fun s -> String.length s > 0)
    |> List.map ~f:(fun line ->
      let re = regexp "^(\\w+)\\s+=\\s+\\((\\w+),\\s(\\w+)\\)$" in
      match extract ~rex:re line with
      | [| _; name; left; right |] -> name, { name; left; right }
      | _ -> raise (Failure "Invalid line"))
    |> List.fold ~init:Advent.StringMap.empty ~f:(fun acc (root, node) ->
      Map.set acc ~key:root ~data:node)
  in
  { movements; coords }
;;

let part_one data =
  let loop_ids curr size = (curr + 1) mod size in
  let rec walk curr dirs direction =
    let movement = List.nth_exn dirs.movements direction in
    match curr.name with
    | "ZZZ" -> 0
    | _ ->
      let next =
        match movement with
        | 'L' -> Map.find_exn dirs.coords curr.left
        | 'R' -> Map.find_exn dirs.coords curr.right
        | _ -> failwith "Invalid direction"
      in
      1 + walk next dirs (loop_ids direction (List.length dirs.movements))
  in
  let dirs = parse_input data in
  let start =
    match Map.find dirs.coords "AAA" with
    | None -> failwith "No initial node"
    | Some node -> node
  in
  walk start dirs 0
;;

let part_two _ = 0

let () =
  (* let input = "./data/day08-test.txt" in *)
  let input = "./data/day08.txt" in
  let data = Advent.read_lines input in
  (* Part one:  ?? *)
  part_one data |> Fmt.pr "@.Part one: %d@.";
  (* Part two: ?? *)
  part_two data |> Fmt.pr "@.Part two: %d@."
;;
