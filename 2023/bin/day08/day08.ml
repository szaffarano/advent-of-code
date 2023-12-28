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

let find_solution ~start ~finish directions =
  let loop_ids curr = (curr + 1) mod List.length directions.movements in
  let rec walk partial =
    let movement = List.nth_exn directions.movements partial.direction in
    let is_end_node = finish partial.node in
    match is_end_node with
    | true -> partial
    | false ->
      let next_node =
        match movement with
        | 'L' -> Map.find_exn directions.coords partial.node.left
        | 'R' -> Map.find_exn directions.coords partial.node.right
        | _ -> failwith "Invalid direction"
      in
      let next_direction = loop_ids partial.direction in
      let result =
        { distance = partial.distance + 1; direction = next_direction; node = next_node }
      in
      walk result
  in
  directions.coords
  |> Map.filter ~f:start
  |> Map.data
  |> List.map ~f:(fun node -> walk { distance = 0; direction = 0; node })
  |> List.map ~f:(fun r -> r.distance)
  |> Advent.lcm_of_list
;;

let part_one data =
  parse_input data
  |> find_solution
       ~start:(fun n -> String.equal "AAA" n.name)
       ~finish:(fun n -> String.equal "ZZZ" n.name)
;;

let part_two data =
  parse_input data
  |> find_solution
       ~start:(fun n -> String.is_suffix ~suffix:"A" n.name)
       ~finish:(fun n -> String.is_suffix ~suffix:"Z" n.name)
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
