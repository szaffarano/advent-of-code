use rust::get_input;
use std::env;

fn part_one(input: &Vec<String>) -> String {
    format!("Input length: {}", input.len())
}

fn part_two(input: &Vec<String>) -> String {
    format!("Input length: {}", input.len())
}

fn main() -> Result<(), String> {
    get_input(env::args().collect())
        .map(|lines| (part_one(&lines), part_two(&lines)))
        .map(|(result_one, result_two)| {
            println!("Result One: {}", result_one);
            println!("Result Two: {}", result_two);
        })
}
