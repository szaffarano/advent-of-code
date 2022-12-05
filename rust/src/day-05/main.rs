use regex::Regex;
use rust::get_input;
use std::{env, usize};

fn part_one(input: &Vec<String>) -> String {
    let _idx: Vec<_> = input
        .iter()
        .position(|i| i.is_empty())
        .map(|idx| {
            let no_stacks = (input[idx - 1].len() + 1) / 4;
            // let stacks: &mut Vec<Vec<_>> = &vec![Vec::<char>::new(); no_stacks];
            let mut stacks: Vec<Vec<char>> = Vec::new();
            for _ in 0..no_stacks {
                stacks.push(Vec::new());
            }
            for l in input.get(..idx - 1).unwrap().iter().rev() {
                for i in 0..no_stacks {
                    let c: char = if i == 0 {
                        l.chars().nth(1).unwrap()
                    } else {
                        l.chars().nth((i * 4) + 1 as usize).unwrap()
                    };
                    if c != ' ' {
                        stacks[i].push(c);
                    }
                }
            }
            (stacks, input.get(idx + 1..).unwrap())
        })
        .map(|(mut stacks, procedure)| {
            let re = Regex::new(r"^move (\d*) from (\d*) to (\d*)$").unwrap();
            for command in procedure {
                let captures = re.captures_iter(command).next().unwrap();
                let amount = captures[1].parse::<u8>().unwrap();
                let from = captures[2].parse::<usize>().unwrap();
                let to = captures[3].parse::<usize>().unwrap();

                for _ in 0..amount {
                    let v = stacks[from - 1].pop().unwrap();
                    stacks[to - 1].push(v);
                }
            }
            stacks
                .iter()
                .map(|stack| stack.last().unwrap_or(&' ').clone().to_string())
                .filter(|c| !c.is_empty())
                .collect()
        })
        .unwrap();
    format!("Input length: {:?}", _idx.join(""))
}

fn part_two(input: &Vec<String>) -> String {
    let _idx: Vec<_> = input
        .iter()
        .position(|i| i.is_empty())
        .map(|idx| {
            let no_stacks = (input[idx - 1].len() + 1) / 4;
            // let stacks: &mut Vec<Vec<_>> = &vec![Vec::<char>::new(); no_stacks];
            let mut stacks: Vec<Vec<char>> = Vec::new();
            for _ in 0..no_stacks {
                stacks.push(Vec::new());
            }
            for l in input.get(..idx - 1).unwrap().iter().rev() {
                for i in 0..no_stacks {
                    let c: char = if i == 0 {
                        l.chars().nth(1).unwrap()
                    } else {
                        l.chars().nth((i * 4) + 1 as usize).unwrap()
                    };
                    if c != ' ' {
                        stacks[i].push(c);
                    }
                }
            }
            (stacks, input.get(idx + 1..).unwrap())
        })
        .map(|(mut stacks, procedure)| {
            let re = Regex::new(r"^move (\d*) from (\d*) to (\d*)$").unwrap();
            for command in procedure {
                let captures = re.captures_iter(command).next().unwrap();
                let amount = captures[1].parse::<u8>().unwrap();
                let from = captures[2].parse::<usize>().unwrap();
                let to = captures[3].parse::<usize>().unwrap();

                // let stack_from = &mut stacks[from-1];
                // let stack_to = &mut stacks[to-1];
                // let stack_from_length = stacks[from-1].len();
                let tempo: &mut Vec<char> = &mut Vec::new();
                for _ in 0..amount {
                    // let idx = stack_from_length - i as usize - 1 as usize;
                    // let v = stacks[from-1].remove(idx);

                    // let element = stack_from.remove(stack_from.len()-i as usize);
                    let v = stacks[from - 1].pop().unwrap();
                    tempo.push(v);
                }
                tempo.reverse();
                    stacks[to - 1].append(tempo);
            }
            stacks
                .iter()
                .map(|stack| stack.last().unwrap_or(&' ').clone().to_string())
                .filter(|c| !c.is_empty())
                .collect()
        })
        .unwrap();
    format!("Input length: {:?}", _idx.join(""))
}

fn main() -> Result<(), String> {
    get_input(env::args().collect())
        .map(|lines| (part_one(&lines), part_two(&lines)))
        .map(|(result_one, result_two)| {
            println!("Result One: {}", result_one);
            println!("Result Two: {}", result_two);
        })
}
