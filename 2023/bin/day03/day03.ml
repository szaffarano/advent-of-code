open Core

type part_number =
  { value : string
  ; origin : int
  }

let to_matrix data = Array.of_list data |> Array.map ~f:String.to_array

let find_part_numbers row =
  let rec find ?(start = 0) = function
    | [||] -> [||]
    | row ->
      (match Array.findi row ~f:(fun _ c -> not (Char.is_digit c)) with
       | Some (i, _) ->
         (match i with
          | 0 ->
            let from = 1 in
            let tto = Array.length row in
            let remaining = Array.slice row from tto in
            let new_start = start + from in

            find remaining ~start:new_start
          | _ ->
            let value = String.of_array (Array.slice row 0 i) in

            let from = i in
            let tto = Array.length row in
            let remaining = Array.slice row from tto in
            let new_start = (start + String.length value) in

            let origin = i + start - String.length value in
            let p = [| { value; origin } |] in
            Array.append p (find remaining ~start:new_start))
       | None ->
         let value = String.of_array (Array.slice row 0 (Array.length row)) in
         let origin = 0 + start in
         [| { value; origin } |])
  in
  find row
;;

let part_one data =
  let x = to_matrix data |> Array.map ~f:find_part_numbers in
  Array.iter x ~f:(fun a ->
    Array.iter
      (Array.map a ~f:(fun p -> p.value ^ ", " ^ Int.to_string p.origin))
      ~f:(printf "%s\n");
    printf "---\n");
  0
;;

let part_two _ = 0

let () =
  let input = "./data/day03-test.txt" in
  let data = Advent.read_lines input in
  part_one data |> printf "Part one: %d\n";
  part_two data |> printf "Part two: %d\n"
;;
