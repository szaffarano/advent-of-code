use rust::get_input;
use std::env;

fn has_repeated(values: &Vec<char>) -> bool {
    if values.len() < 2 {
        false
    } else if values.len() == 2 {
        values[0] == values[1]
    } else {
        let c = values[0];
        let rest = &values[1..values.len()];
        rest.contains(&c) || has_repeated(&rest.to_vec())
    }
}

fn find_marker(line: &String, size: usize) -> usize {
    let mut buf = vec![' '; size];
    let mut seq_idx: usize = 0;

    for (i, ch) in line.chars().enumerate() {
        buf[i % size] = ch;
        if i > (size - 1) && !has_repeated(&buf) {
            seq_idx = i + 1;
            break;
        }
    }
    return seq_idx;
}

fn part_one(input: &Vec<String>) -> String {
    format!("{}", find_marker(&input[0], 4))
}

fn part_two(input: &Vec<String>) -> String {
    format!("{}", find_marker(&input[0], 14))
}

fn main() -> Result<(), String> {
    get_input(env::args().collect())
        .map(|lines| (part_one(&lines), part_two(&lines)))
        .map(|(result_one, result_two)| {
            println!("Result One: {}", result_one);
            println!("Result Two: {}", result_two);
        })
}
