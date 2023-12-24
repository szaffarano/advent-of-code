use regex::Regex;
use rust::get_input;
use std::env;

#[derive(Clone, Debug)]
struct Monkey {
    items: Vec<u64>,
    test: Test,
    operation: Operation,
    items_inspected: u64,
}

#[derive(Copy, Clone, Debug)]
struct Test {
    divisible_by: u64,
    target_monkey_true: u8,
    target_monkey_false: u8,
}

#[derive(Clone, Debug)]
enum Operation {
    Add(u64),
    Multiply(u64),
    Square,
}

enum ParsingState {
    Monkey,
    Items,
    Operation,
    TestOperation,
    TestTrue,
    TestFalse,
    End,
}

fn parse(input: &Vec<String>) -> Vec<Monkey> {
    let mut monkeys = Vec::new();
    let mut state = ParsingState::Monkey;
    let mut monkey = Monkey::new();

    let re_items = Regex::new(r"^\s*Starting items: (.*)$").unwrap();
    let re_operation = Regex::new(r"^\s*Operation: new = (.*) (.) (.*)$").unwrap();
    let re_test = Regex::new(r"^\s*Test: divisible by (\d*)$").unwrap();
    let re_test_true = Regex::new(r"^\s*If true: throw to monkey (\d*)$").unwrap();
    let re_test_false = Regex::new(r"^\s*If false: throw to monkey (\d?)$").unwrap();

    for line in input.iter() {
        state = match state {
            ParsingState::Monkey => {
                if !line.starts_with("Monkey") {
                    panic!("Invalid format");
                }
                ParsingState::Items
            }
            ParsingState::Items => {
                monkey.items = re_items
                    .captures_iter(line)
                    .next()
                    .expect("Invalid items format")[1]
                    .split(",")
                    .map(|s| s.trim())
                    .map(|s| s.parse::<u64>().unwrap())
                    .collect::<Vec<u64>>();
                ParsingState::Operation
            }
            ParsingState::Operation => {
                let parsed = re_operation
                    .captures_iter(line)
                    .next()
                    .expect("Invalid operation format");
                monkey.operation = Operation::new(&parsed[2], &parsed[3]);

                ParsingState::TestOperation
            }
            ParsingState::TestOperation => {
                monkey.test.divisible_by = re_test
                    .captures_iter(line)
                    .next()
                    .expect("Invalid test format")[1]
                    .parse::<u64>()
                    .unwrap();

                ParsingState::TestTrue
            }
            ParsingState::TestTrue => {
                monkey.test.target_monkey_true = re_test_true
                    .captures_iter(line)
                    .next()
                    .expect("Invalid test true format")[1]
                    .parse::<u8>()
                    .unwrap();

                ParsingState::TestFalse
            }
            ParsingState::TestFalse => {
                monkey.test.target_monkey_false = re_test_false
                    .captures_iter(line)
                    .next()
                    .expect("Invalid test false format")[1]
                    .parse::<u8>()
                    .unwrap();

                monkeys.push(monkey.clone());
                ParsingState::End
            }
            ParsingState::End => {
                if line != "" {
                    panic!("Invalid monkey transition");
                }
                ParsingState::Monkey
            }
        }
    }

    monkeys
}

fn play(input: &Vec<String>, relief_level_divisor: u64, times: u16) -> String {
    let monkeys = &mut parse(input);
    let num_of_monkeys = monkeys.len();

    let cm = monkeys
        .iter()
        .map(|m| m.test.divisible_by)
        .reduce(|a, b| a * b)
        .unwrap();

    for _ in 0..times {
        for idx in 0..num_of_monkeys {
            let monkey = monkeys[idx].clone();
            for item in monkey.items.clone().iter() {
                let new_worried_level = monkey.operation.calculate(item);
                let updated_worried_level = (new_worried_level / relief_level_divisor) % cm;

                if updated_worried_level % monkey.test.divisible_by == 0 {
                    monkeys[monkey.test.target_monkey_true as usize]
                        .items
                        .push(updated_worried_level);
                } else {
                    monkeys[monkey.test.target_monkey_false as usize]
                        .items
                        .push(updated_worried_level);
                }
            }
            monkeys[idx].items.clear();
            monkeys[idx].items_inspected += monkey.items.len() as u64;
        }
    }

    monkeys.sort_by(|m1, m2| m2.items_inspected.cmp(&m1.items_inspected));

    format!(
        "{}",
        monkeys[0].items_inspected * monkeys[1].items_inspected
    )
}

fn part_one(input: &Vec<String>) -> String {
    play(input, 3, 20)
}

fn part_two(input: &Vec<String>) -> String {
    play(input, 1, 10_000)
}

fn main() -> Result<(), String> {
    get_input(env::args().collect())
        .map(|lines| (part_one(&lines), part_two(&lines)))
        .map(|(result_one, result_two)| {
            println!("Result One: {}", result_one);
            println!("Result Two: {}", result_two);
        })
}

impl Monkey {
    fn new() -> Monkey {
        Monkey {
            items: vec![],
            operation: Operation::Square,
            test: Test {
                divisible_by: 0,
                target_monkey_true: 0,
                target_monkey_false: 0,
            },
            items_inspected: 0,
        }
    }
}

impl Operation {
    fn calculate(&self, value: &u64) -> u64 {
        match self {
            Operation::Square => value * value,
            Operation::Multiply(num) => value * num,
            Operation::Add(num) => value + num,
        }
    }

    fn new(op: &str, value: &str) -> Operation {
        match op {
            "+" => Operation::Add(value.parse::<u64>().unwrap()),
            "*" => {
                if value == "old" {
                    Operation::Square
                } else {
                    Operation::Multiply(value.parse::<u64>().unwrap())
                }
            }
            _ => panic!("Not supported operation"),
        }
    }
}
