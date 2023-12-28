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

type result =
  { distance : int
  ; direction : int
  ; node : node
  }
[@@deriving show { with_path = false }]

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

let find_solution ~finish node directions =
  let loop_ids curr = (curr + 1) mod List.length directions.movements in
  let rec walk current_node =
    let movement = List.nth_exn directions.movements current_node.direction in
    let is_end_node = finish current_node in
    match is_end_node with
    | true -> current_node
    | false ->
      let next_node =
        match movement with
        | 'L' -> Map.find_exn directions.coords current_node.node.left
        | 'R' -> Map.find_exn directions.coords current_node.node.right
        | _ -> failwith "Invalid direction"
      in
      let next_direction = loop_ids current_node.direction in
      let result =
        { distance = current_node.distance + 1
        ; direction = next_direction
        ; node = next_node
        }
      in
      walk result
  in
  let start = { distance = 0; direction = 0; node } in
  walk start
;;

let part_one data =
  let directions = parse_input data in
  let start =
    match Map.find directions.coords "AAA" with
    | None -> failwith "No initial node"
    | Some node -> node
  in
  find_solution start directions ~finish:(fun n -> String.equal "ZZZ" n.node.name)
  |> fun solution -> solution.distance
;;

let part_two data =
  let directions = parse_input data in
  Map.filter directions.coords ~f:(fun node -> String.is_suffix node.name ~suffix:"A")
  |> Map.data
  |> List.map ~f:(fun node ->
    find_solution node directions ~finish:(fun n ->
      String.is_suffix ~suffix:"Z" n.node.name))
  |> List.map ~f:(fun r -> r.distance)
  |> Advent.lcm_of_list
;;

let () =
  (* let input = "./data/day08-test.txt" in *)
  let input = "./data/day08.txt" in
  let data = Advent.read_lines input in
  (* Part one:  17621 *)
  part_one data |> Fmt.pr "@.Part one: %d@.";
  (* Part two: 20685524831999 *)
  part_two data |> Fmt.pr "@.Part two: %d@."
;;
