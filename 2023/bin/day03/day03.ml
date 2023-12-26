open Core

type part_number =
  { value : string
  ; position : Advent.point
  }

type accumulator =
  { current : part_number option
  ; part_numbers : part_number array
  }

let find_part_numbers y row =
  let acc = { current = None; part_numbers = [||] } in
  let folded =
    Array.foldi row ~init:acc ~f:(fun x acc c ->
      if Char.is_digit c
      then (
        match acc.current with
        | None ->
          let value = String.of_char c in
          { acc with current = Some { value; position = { x; y } } }
        | Some p ->
          { acc with current = Some { p with value = p.value ^ String.of_char c } })
      else (
        match acc.current with
        | None -> acc
        | Some current ->
          { current = None; part_numbers = Array.append acc.part_numbers [| current |] }))
  in
  match folded.current with
  | None -> folded.part_numbers
  | Some current -> Array.append [| current |] folded.part_numbers
;;

(* given a list of parts and a point, returns all the parts overlapping with it *)
let find_parts parts (p : Advent.point) =
  Array.filter parts ~f:(fun part ->
    part.position.y = p.y
    && p.x >= part.position.x
    && p.x <= part.position.x + String.length part.value)
;;

let part_one data =
  let matrix = Advent.matrix_of_array data in
  let part_numbers = Array.concat_mapi matrix ~f:find_part_numbers in
  Array.filter part_numbers ~f:(fun part ->
    not
      (Array.concat_mapi (String.to_array part.value) ~f:(fun i _ ->
         Advent.surroundings matrix ~x:(part.position.x + i) ~y:part.position.y)
       |> Array.filter ~f:(fun c -> (not (Char.is_digit c)) && not Char.(c = '.'))
       |> Array.is_empty))
  |> Array.map ~f:(fun part -> Int.of_string part.value)
  |> Array.fold ~init:0 ~f:( + )
;;

let part_two data =
  let matrix = Advent.matrix_of_array data in
  let part_numbers = Array.concat_mapi matrix ~f:find_part_numbers in
  Array.concat_mapi matrix ~f:(fun y row ->
    Array.mapi row ~f:(fun x c ->
      match c with
      | '*' -> Some Advent.{ x; y }
      | _ -> None))
  |> Array.filter_opt
  |> Array.map ~f:(fun (pos : Advent.point) ->
    Advent.positioned_surroundings matrix ~x:pos.x ~y:pos.y
    |> Array.filter ~f:(fun v -> Char.(snd v <> '.'))
    |> Array.concat_map ~f:(fun c -> find_parts part_numbers (fst c))
    |> Advent.dedup ~cmp:(fun a b ->
      a.position.x = b.position.x && a.position.y = b.position.y)
    |> Array.map ~f:(fun p -> p.value)
    |> Array.map ~f:Int.of_string)
  |> Array.filter ~f:(fun a -> Array.length a >= 2)
  |> Array.map ~f:(fun gears -> Array.fold gears ~init:1 ~f:( * ))
  |> Array.fold ~init:0 ~f:( + )
;;

let () =
  let input = "./data/day03.txt" in
  let data = Advent.read_lines input in
  (* Part one: 514969 *)
  part_one data |> printf "Part one: %d\n";
  (* Part two: 78915902 *)
  part_two data |> printf "Part two: %d\n"
;;
