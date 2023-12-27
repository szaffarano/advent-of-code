open Core

type hand_type =
  | FiveOfAKind
  | FourOfAKind
  | FullHouse
  | ThreeOfAKind
  | TwoPair
  | OnePair
  | HighCard

type card = { weight : int }

type hand =
  { card_one : card
  ; card_two : card
  ; card_three : card
  ; card_four : card
  ; card_five : card
  }

type game =
  { hand : hand
  ; bid : int
  ; hand_type : hand_type
  }

module type DaySeven = sig
  val card_of_char : char -> card
  val type_of_hand : hand -> hand_type
end

module Game = struct
  let group_by_weight cards =
    cards
    |> List.sort ~compare:(fun card1 card2 -> card1.weight - card2.weight)
    |> List.group ~break:(fun a b -> a.weight <> b.weight)
    |> List.map ~f:(fun group -> List.length group)
    |> List.sort ~compare:(fun a b -> b - a)
  ;;

  let cards_of_hand hand =
    [ hand.card_one; hand.card_two; hand.card_three; hand.card_four; hand.card_five ]
  ;;

  let compare_games g1 g2 =
    let weight_of_game game =
      match game.hand_type with
      | FiveOfAKind -> 7
      | FourOfAKind -> 6
      | FullHouse -> 5
      | ThreeOfAKind -> 4
      | TwoPair -> 3
      | OnePair -> 2
      | HighCard -> 1
    in
    Int.compare (weight_of_game g1) (weight_of_game g2)
  ;;

  let compare_hands h1 h2 =
    let cards_differences =
      List.map2_exn (cards_of_hand h1) (cards_of_hand h2) ~f:(fun c1 c2 ->
        Int.compare c1.weight c2.weight)
    in
    match List.find cards_differences ~f:(fun x -> x <> 0) with
    | Some n -> n
    | None -> failwith "Unexpected equality"
  ;;

  let game_of_string (module M : DaySeven) game =
    let hand_of_string hand =
      let hand = String.to_list hand |> List.map ~f:M.card_of_char in
      { card_one = List.nth_exn hand 0
      ; card_two = List.nth_exn hand 1
      ; card_three = List.nth_exn hand 2
      ; card_four = List.nth_exn hand 3
      ; card_five = List.nth_exn hand 4
      }
    in
    match String.split game ~on:' ' with
    | [ hand; bid ] ->
      let hand = hand_of_string hand in
      let bid = Int.of_string bid in
      let hand_type = M.type_of_hand hand in
      { hand; bid; hand_type }
    | _ -> failwith "Invalid input"
  ;;

  let score hand_result =
    match hand_result with
    (* Five of a kind, where all five cards have the same label: AAAAA *)
    | [ 5 ] -> FiveOfAKind
    (* Four of a kind, where four cards have the same label and one card has a
       different label: AA8AA *)
    | [ 4; _ ] -> FourOfAKind
    (* Full house, where three cards have the same label, and the remaining two
       cards share a different label: 23332 *)
    | [ 3; _ ] -> FullHouse
    (* Three of a kind, where three cards have the same label, and the remaining
       two cards are each different from any other card in the hand: TTT98 *)
    | [ 3; _; _ ] -> ThreeOfAKind
    (* Two pair, where two cards share one label, two other cards share a second
       label, and the remaining card has a third label: 23432 *)
    | [ 2; _; _ ] -> TwoPair
    (* One pair, where two cards share one label, and the other three cards have
       a different label from the pair and each other: A23A4 *)
    | [ 2; _; _; _ ] -> OnePair
    (* High card, where all cards' labels are distinct: 23456 *)
    | [ _; _; _; _; _ ] -> HighCard
    | _ -> failwith "Invalid hand"
  ;;

  let resolve games =
    games
    |> List.sort ~compare:(fun game1 game2 ->
      match compare_games game1 game2 with
      | 0 -> compare_hands game1.hand game2.hand
      | n -> n)
    |> List.foldi ~init:0 ~f:(fun i acc game -> acc + (game.bid * (i + 1)))
  ;;
end

module PartOne : DaySeven = struct
  let card_of_char = function
    | 'A' -> { weight = 13 }
    | 'K' -> { weight = 12 }
    | 'Q' -> { weight = 11 }
    | 'J' -> { weight = 10 }
    | 'T' -> { weight = 9 }
    | '9' -> { weight = 8 }
    | '8' -> { weight = 7 }
    | '7' -> { weight = 6 }
    | '6' -> { weight = 5 }
    | '5' -> { weight = 4 }
    | '4' -> { weight = 3 }
    | '3' -> { weight = 2 }
    | '2' -> { weight = 1 }
    | _ -> failwith "Invalid card"
  ;;

  let type_of_hand hand =
    let cards = Game.cards_of_hand hand in
    Game.group_by_weight cards |> Game.score
  ;;
end

module PartTwo : DaySeven = struct
  let card_of_char = function
    | 'A' -> { weight = 13 }
    | 'K' -> { weight = 12 }
    | 'Q' -> { weight = 11 }
    | 'T' -> { weight = 10 }
    | '9' -> { weight = 9 }
    | '8' -> { weight = 8 }
    | '7' -> { weight = 7 }
    | '6' -> { weight = 6 }
    | '5' -> { weight = 5 }
    | '4' -> { weight = 4 }
    | '3' -> { weight = 3 }
    | '2' -> { weight = 2 }
    | 'J' -> { weight = 1 }
    | _ -> failwith "Invalid card"
  ;;

  let type_of_hand hand =
    let cards = Game.cards_of_hand hand in
    let num_of_jokers =
      List.filter cards ~f:(fun card -> card.weight = 1) |> List.length
    in
    (* exclude jokers to add them to the group with more figures *)
    let groups =
      Game.group_by_weight (cards |> List.filter ~f:(fun card -> card.weight <> 1))
    in
    let hand_result =
      match groups with
      | h :: t -> (h + num_of_jokers) :: t
      (* edge case: `JJJJJ` -> we excluded jockers when grouped *)
      | [] -> if num_of_jokers = 5 then [ 5 ] else failwith "Invalid weight"
    in
    Game.score hand_result
  ;;
end

let part_one data =
  data |> List.map ~f:(Game.game_of_string (module PartOne)) |> Game.resolve
;;

let part_two data =
  data |> List.map ~f:(Game.game_of_string (module PartTwo)) |> Game.resolve
;;

let () =
  (* let input = "./data/day07-test.txt" in *)
  let input = "./data/day07.txt" in
  let data = Advent.read_lines input in
  (* Part one:  253954294 *)
  part_one data |> printf "Part one: %d\n";
  (* Part two: 254837398 *)
  part_two data |> printf "Part two: %d\n"
;;
