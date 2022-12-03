use rust::get_input;
use std::env;

fn part_one(input: &Vec<String>) -> String {
    let mut acc: u32 = 0;
    for content in input {
        let c1 = &content[0..content.len() / 2];
        let c2 = &content[(content.len() / 2)..content.len()];
        let mut dupes: Vec<char> = Vec::new();

        for x in c1.chars() {
            if c2.contains(x) && !dupes.contains(&x) {
                dupes.push(x);
            }
        }
        // a: 97 -> 1
        // z: 122 -> 26
        // A: 65 -> 27
        // Z: 90 -> 52
        let s: u32 = dupes
            .iter()
            .map(|c| *c as u32)
            .map(|i| if i <= 90 { i - 38 } else { i - 96 })
            .sum();
        acc += s;
    }
    format!("{}", acc)
}

fn part_two(input: &Vec<String>) -> String {
    let mut acc = 0;
    for group in input.chunks(3) {
        let mut sorted = vec![String::new(); group.len()];
        sorted.clone_from_slice(group);
        sorted.sort_by(|a, b| a.len().cmp(&b.len()));

        acc += sorted[2]
            .chars()
            .find(|&c| sorted[0].contains(c) && sorted[1].contains(c))
            .map(|c| c as u32)
            .map(|i| if i <= 90 { i - 38 } else { i - 96 })
            .expect("No common chars");
    }

    format!("{}", acc)
}

fn main() -> Result<(), String> {
    get_input(env::args().collect())
        .map(|lines| (part_one(&lines), part_two(&lines)))
        .map(|(result_one, result_two)| {
            println!("Result One: {}", result_one);
            println!("Result Two: {}", result_two);
        })
}
