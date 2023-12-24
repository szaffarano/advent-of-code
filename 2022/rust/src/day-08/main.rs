use rust::get_input;
use std::env;

fn transpose(matrix: &Vec<Vec<i32>>) -> Vec<Vec<i32>> {
    let mut transposed = vec![Vec::new(); matrix[0].len()];

    for l in matrix.iter() {
        for (j, c) in l.iter().enumerate() {
            transposed[j].push(*c);
        }
    }

    transposed
}

fn part_one(input: &Vec<String>) -> String {
    let matrix: Vec<Vec<i32>> = input
        .iter()
        .map(|s| {
            s.chars()
                .map(|c| c.to_digit(10).unwrap() as i32)
                .collect::<Vec<i32>>()
        })
        .collect();

    let transposed = transpose(&matrix);
    let mut counter = 0;

    for (i, l) in matrix.iter().enumerate() {
        for (j, x) in l.iter().enumerate() {
            let left = l[0..j].iter().max().unwrap_or(&-1_i32);
            let right = l[j + 1..l.len()].iter().max().unwrap_or(&-1_i32);

            let up = transposed[j][0..i].iter().max().unwrap_or(&-1_i32);
            let down = transposed[j][i + 1..transposed[j].len()]
                .iter()
                .max()
                .unwrap_or(&-1_i32);

            if x > left || x > right || x > up || x > down {
                counter += 1;
            }
        }
    }

    format!("{}", counter)
}

fn part_two(input: &Vec<String>) -> String {
    let matrix: Vec<Vec<i32>> = input
        .iter()
        .map(|s| {
            s.chars()
                .map(|c| c.to_digit(10).unwrap() as i32)
                .collect::<Vec<i32>>()
        })
        .collect();

    let transposed = transpose(&matrix);

    let values: Vec<_> = matrix
        .iter()
        .enumerate()
        .map(|(x, row)| {
            row.iter()
                .enumerate()
                .map(|(y, cell)| {
                    let left = row[0..y]
                        .iter()
                        .rev()
                        .enumerate()
                        .find(|(_, value)| *value >= cell)
                        .map(|(idx, _)| idx + 1)
                        .unwrap_or(y);

                    let right = row[y + 1..row.len()]
                        .iter()
                        .enumerate()
                        .find(|(_, value)| *value >= cell)
                        .map(|(idx, _)| idx + 1)
                        .unwrap_or(row.len() - y - 1);

                    let up = transposed[y][0..x]
                        .iter()
                        .rev()
                        .enumerate()
                        .find(|(_, value)| *value >= cell)
                        .map(|(idx, _)| idx + 1)
                        .unwrap_or(x);

                    let down = transposed[y][x + 1..row.len()]
                        .iter()
                        .clone()
                        .enumerate()
                        .find(|(_, value)| *value >= cell)
                        .map(|(idx, _)| idx + 1)
                        .unwrap_or(row.len() - x - 1);

                    // println!(
                    //     "({}, {}): {} -> up: {}, down: {}, left: {}, right: {}",
                    //     i, j, x, up_num, down_num, left_num, right_num
                    // );
                    //
                    left * right * up * down
                })
                .collect::<Vec<usize>>()
                .iter()
                .max()
                .unwrap()
                .clone()
        })
        .collect();

    let max = values.iter().max().unwrap();

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
