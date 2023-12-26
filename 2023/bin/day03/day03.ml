open Core

type point =
  { x : int
  ; y : int
  }

type part_number =
  { value : string
  ; position : point
  }

let to_matrix data = Array.of_list data |> Array.map ~f:String.to_array

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

let part_one data =
  let matrix = to_matrix data in
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

let part_two _ = 0

(* 514969 *)
let () =
  let input = "./data/day03.txt" in
  let data = Advent.read_lines input in
  part_one data |> printf "Part one: %d\n";
  part_two data |> printf "Part two: %d\n"
;;
