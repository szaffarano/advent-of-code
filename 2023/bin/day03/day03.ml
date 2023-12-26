open Core

type part_number =
  { value : string
  ; x : int
  ; y : int
  }

let to_matrix data = Array.of_list data |> Array.map ~f:String.to_array

type accumulator =
  { current : part_number option
  ; part_numbers : part_number array
  }

let find_part_numbers y row =
  let acc = { current = None; part_numbers = [||] } in
  let folded =
    Array.foldi row ~init:acc ~f:(fun i acc c ->
      if Char.is_digit c
      then (
        match acc.current with
        | None ->
          let value = String.of_char c in
          let x = i in
          { acc with current = Some { value; x; y } }
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

let safe_get arr x =
  if x >= 0 && x < Array.length arr then Some (Array.get arr x) else None
;;

let part_one data =
  let matrix = to_matrix data in
  let part_numbers = Array.concat_mapi matrix ~f:find_part_numbers in
  let f =
    Array.filter part_numbers ~f:(fun part ->
      let row = Array.get matrix part.y in
      let matrix_width = Array.length row in
      let x_from = Int.max 0 (part.x - 1) in
      let x_to = Int.min matrix_width (part.x + String.length part.value + 1) in
      let left =
        match safe_get row (part.x - 1) with
        | None -> '.'
        | Some value -> value
      in
      let right =
        match safe_get row (part.x + String.length part.value) with
        | None -> '.'
        | Some value -> value
      in
      let up =
        match safe_get matrix (part.y - 1) with
        | None -> [||]
        | Some row_up ->
          Array.slice row_up x_from x_to |> Array.filter ~f:(fun c -> Char.(c <> '.'))
      in
      let down =
        match safe_get matrix (part.y + 1) with
        | None -> [||]
        | Some row_down ->
          Array.slice row_down x_from x_to |> Array.filter ~f:(fun c -> Char.(c <> '.'))
      in
      let found =
        (not (Array.is_empty up))
        || (not (Array.is_empty down))
        || Char.(left <> '.')
        || Char.(right <> '.')
      in
      found)
  in
  let result =
    Array.map f ~f:(fun part -> Int.of_string part.value) |> Array.fold ~init:0 ~f:( + )
  in
  result
;;

let part_two _ = 0

(* 514969 *)
let () =
  let input = "./data/day03.txt" in
  let data = Advent.read_lines input in
  part_one data |> printf "Part one: %d\n";
  part_two data |> printf "Part two: %d\n"
;;
