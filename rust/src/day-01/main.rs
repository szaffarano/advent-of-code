use rust::get_input;
use std::env;

fn part_one(input: &Vec<String>) -> String {
    let mut max = 0;
    let mut partial = 0;
    for line in input {
        if line.is_empty() {
            if partial > max {
                max = partial;
            }
            partial = 0;
        } else {
            let value = line.parse::<u32>().unwrap();
            partial += value;
        }
    }
    format!("{}", max)
}

fn part_two(input: &Vec<String>) -> String {
    let mut max = 0;
    let mut partial = 0;
    for line in input {
        if line.is_empty() {
            if partial > max {
                max = partial;
            }
            partial = 0;
        } else {
            let value = line.parse::<u32>().unwrap();
            partial += value;
        }
    }
    format!("{}", max)
}

fn main() -> Result<(), String> {
    get_input(env::args().collect())
        .map(|lines| (part_one(&lines), part_two(&lines)))
        .map(|(result_one, result_two)| {
            println!("Result One: {}", result_one);
            println!("Result Two: {}", result_two);
        })
}
