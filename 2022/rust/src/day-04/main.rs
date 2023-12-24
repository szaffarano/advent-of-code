use rust::get_input;
use std::env;

fn part_one(input: &Vec<String>) -> String {
    let mut counter = 0;
    for line in input {
        let parsed: Vec<_> = line.split(",").collect();
        let left: Vec<_> = parsed[0]
            .split("-")
            .map(|s| s.parse::<u8>().unwrap())
            .collect();
        let right: Vec<_> = parsed[1]
            .split("-")
            .map(|s| s.parse::<u8>().unwrap())
            .collect();

        if (left[0] >= right[0] && left[1] <= right[1])
            || (left[0] <= right[0] && left[1] >= right[1])
        {
            counter += 1;
        }
    }
    format!("{}", counter)
}

fn part_two(input: &Vec<String>) -> String {
    let mut counter = 0;
    for line in input {
        let parsed = line
            .split(",")
            .map(|p| {
                p.split("-")
                    .map(|s| s.parse::<u8>().unwrap())
                    .collect::<Vec<_>>()
            })
            .collect::<Vec<_>>();

        let (left, right) = (&parsed[0], &parsed[1]);

        if (left[0] <= right[1] && right[0] <= left[1])
            || (left[1] <= right[0] && right[1] <= left[0])
        {
            counter += 1;
        }
    }
    format!("{}", counter)
}

fn main() -> Result<(), String> {
    get_input(env::args().collect())
        .map(|lines| (part_one(&lines), part_two(&lines)))
        .map(|(result_one, result_two)| {
            println!("Result One: {}", result_one);
            println!("Result Two: {}", result_two);
        })
}
