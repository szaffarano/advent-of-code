use rust::get_input;
use std::env;

fn values(input: &Vec<String>) -> Vec<i32> {
    let mut x_reg = 1;
    let mut values = vec![1];

    for code in input {
        let result = if code == "noop" {
            vec![x_reg]
        } else {
            let value = code.get(5..code.len()).unwrap().parse::<i32>().unwrap();
            let old_x_reg = x_reg;
            x_reg += value;
            vec![old_x_reg, x_reg]
        };
        values.extend(result);
    }
    values
}
fn part_one(input: &Vec<String>) -> String {
    let values = values(input);

    let mut result = 0;
    for i in 0..6 {
        let cycle = (i * 40) + 20;
        let value = values[cycle - 1];
        result += cycle as i32 * value;
    }

    format!("{}", result)
}

fn part_two(input: &Vec<String>) -> String {
    let values = values(input);
    let mut screen = vec!['.'; 240];

    for (row, line) in values.chunks(40).enumerate() {
        for (idx, sprite) in line.iter().enumerate() {
            let pixels = vec![*sprite - 1, *sprite, *sprite + 1];
            let pixel = idx as i32;

            if pixels.contains(&pixel) {
                screen[(row * 40) + idx] = '#';
            }
        }
    }

    let mut result = String::new();
    result += "\n";
    for line in screen.chunks(40) {
        result += format!("{}", line.iter().collect::<String>()).as_str();
        result += "\n";
    }
    format!("{}", result)
}

fn main() -> Result<(), String> {
    get_input(env::args().collect())
        .map(|lines| (part_one(&lines), part_two(&lines)))
        .map(|(result_one, result_two)| {
            println!("Result One: {}", result_one);
            println!("Result Two: {}", result_two);
        })
}
