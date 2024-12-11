use rust::get_input;
use std::env;

/// A: Rock
/// B: Paper
/// C: Scissors
///
/// X: Rock
/// Y: Paper
/// Z: Scissors
///
/// Rock: 1
/// Paper: 2
/// Scissors: 3
///
/// Won: 6
/// Draw: 3
/// Lost: 0
fn part_one(input: &Vec<String>) -> String {
    let win = vec!["A Y", "B Z", "C X"];
    let draw = vec!["A X", "B Y", "C Z"];
    let mut sum = 0;
    for line in input {
        let shape_score = match &line[2..3] {
            "X" => 1,
            "Y" => 2,
            "Z" => 3,
            _ => -1,
        };

        let play_score = if win.contains(&line.as_str()) {
            6
        } else if draw.contains(&line.as_str()) {
            3
        } else {
            0
        };

        sum += play_score + shape_score;
    }
    format!("{}", sum)
}

/// X: lose
/// Y: draw
/// Z: win
fn part_two(input: &Vec<String>) -> String {
    let wins = std::collections::HashMap::from([
        ("A", "B"), ("B", "C"), ("C", "A")
    ]);
    let loses = std::collections::HashMap::from([
        ("A", "C"), ("B", "A"), ("C", "B")
    ]);
    let figures = std::collections::HashMap::from([
        ("A", 1), ("B",2), ("C",3)
    ]);

    let mut sum = 0;
    for line in input {
        let oponent = &line[0..1];
        let result = &line[2..3];
        let score = match result {
            "X" => {
                let lose = loses[oponent];
                figures[lose]
            },
            "Y" => {
                let draw = oponent;
                figures[draw] + 3
            },
            "Z" => {
                let win = wins[oponent];
                figures[win] + 6
            },
            _ => -1,
        };

        sum += score;
    }
    format!("{}", sum)
}

fn main() -> Result<(), String> {
    get_input(env::args().collect())
        .map(|lines| (part_one(&lines), part_two(&lines)))
        .map(|(result_one, result_two)| {
            println!("Result One: {}", result_one);
            println!("Result Two: {}", result_two);
        })
}
