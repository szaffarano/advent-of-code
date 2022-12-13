use rust::get_input;
use std::{collections::VecDeque, env, str::Chars};

#[derive(Debug, Clone, PartialEq, Eq, PartialOrd, Ord)]
enum Value {
    Empty,
    Number(u32),
    Values(Vec<Value>),
}

fn parse_line(chars: Chars) -> Value {
    let mut value = Value::Empty;
    let mut queue = VecDeque::new();
    let mut tmp_num = String::new();

    for ch in chars {
        match ch {
            '[' => {
                if let Value::Values(_) = value {
                    queue.push_back(value);
                }
                value = Value::Values(Vec::new());
            }
            ']' => {
                if tmp_num.len() > 0 {
                    if let Value::Values(ref mut values) = value {
                        let _number = tmp_num.parse::<u32>().unwrap();
                        values.push(Value::Number(_number));
                        tmp_num = String::new();
                    }
                }

                if !queue.is_empty() {
                    let mut tmp = queue.pop_back().unwrap();
                    if let Value::Values(ref mut values) = tmp {
                        values.push(value.clone());
                    }
                    value = tmp;
                }
            }
            ',' => {
                if tmp_num.len() > 0 {
                    if let Value::Values(ref mut values) = value {
                        let _number = tmp_num.parse::<u32>().unwrap();
                        values.push(Value::Number(_number));
                        tmp_num = String::new();
                    }
                }
            }
            _ => tmp_num.push(ch),
        }
    }

    value.clone()
}

impl Value {
    fn cmp(&self, other: &Value) -> std::cmp::Ordering {
        if let (Value::Number(a), Value::Number(b)) = (self, other) {
            // println!("Comparing {} with {}", a, b);
            return a.cmp(b);
        }

        if let (Value::Number(a), Value::Values(_)) = (self, other) {
            // println!("Mixed <{:?}> - {:?}", a, b);
            return Value::Values(vec![Value::Number(*a)]).cmp(other);
        }

        if let (Value::Values(_), Value::Number(b)) = (self, other) {
            // println!("Mixed {:?} - <{:?}>", a, b);
            return self.cmp(&Value::Values(vec![Value::Number(*b)]));
        }

        if let (Value::Values(a), Value::Values(b)) = (self, other) {
            for pair in a.iter().zip(b) {
                match pair.0.cmp(pair.1) {
                    std::cmp::Ordering::Greater => return std::cmp::Ordering::Greater,
                    std::cmp::Ordering::Less => return std::cmp::Ordering::Less,
                    _ => (),
                }
            }

            // println!("{:?} vs {:?}", a, b);
            return a.len().cmp(&b.len());
        }

        std::cmp::Ordering::Greater
    }
}

fn part_one(input: &Vec<String>) -> String {
    let mut sum = 0;
    for (order, chunk) in input.chunks(3).enumerate() {
        let pair_one = parse_line(chunk[0].chars());
        let pair_two = parse_line(chunk[1].chars());

        if let std::cmp::Ordering::Less = pair_one.cmp(&pair_two) {
            sum += order + 1;
        }
    }

    format!("{}", sum)
}

fn part_two(input: &Vec<String>) -> String {
    let divider_one = Value::Values(vec![Value::Values(vec![Value::Number(2)])]);
    let divider_two = Value::Values(vec![Value::Values(vec![Value::Number(6)])]);

    let mut packets = input
        .iter()
        .filter(|s| !s.is_empty())
        .map(|s| s.chars())
        .map(|chars| parse_line(chars))
        .collect::<Vec<Value>>();

    packets.extend(vec![divider_one.clone(), divider_two.clone()]);
    packets.sort_by(|a, b| a.cmp(b));

    let pos_divider_one = packets.iter().position(|i| i == &divider_one).unwrap() + 1;
    let pos_divider_two = packets.iter().position(|i| i == &divider_two).unwrap() + 1;

    format!("{}", pos_divider_one * pos_divider_two)
}

fn main() -> Result<(), String> {
    get_input(env::args().collect())
        .map(|lines| (part_one(&lines), part_two(&lines)))
        .map(|(result_one, result_two)| {
            println!("Result One: {}", result_one);
            println!("Result Two: {}", result_two);
        })
}
