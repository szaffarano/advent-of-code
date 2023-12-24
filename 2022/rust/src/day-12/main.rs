use rust::get_input;
use std::{collections::VecDeque, env};

#[derive(Debug, Clone, Copy)]
struct Point {
    x: usize,
    y: usize,
}

fn matrix(input: &Vec<String>) -> Vec<Vec<char>> {
    let mut matrix = vec![vec![' '; input.get(0).unwrap().len()]; input.len()];
    for (y, line) in input.iter().enumerate() {
        for (x, c) in line.chars().enumerate() {
            matrix[y][x] = c;
        }
    }
    matrix
}

fn search(matrix: &Vec<Vec<char>>, ch: char) -> Vec<Point> {
    let mut found = Vec::new();
    for (y, row) in matrix.iter().enumerate() {
        for (x, c) in row.iter().enumerate() {
            if c == &ch {
                found.push(Point { x, y });
            }
        }
    }
    found
}

fn movements(matrix: &Vec<Vec<char>>, current: &Point) -> Vec<Point> {
    fn can_move(source: char, target: char) -> bool {
        let source = if source == 'S' { 'a' } else { source };
        let target = if target == 'E' { 'z' } else { target };

        source >= target || source as i32 + 1 == target as i32
    }

    let width = matrix[0].len();
    let height = matrix.len();
    let x = current.x;
    let y = current.y;

    let mut points = Vec::new();
    if x < width - 1 && can_move(matrix[y][x], matrix[y][x + 1]) {
        points.push(Point { x: x + 1, y })
    }

    if x > 0 && can_move(matrix[y][x], matrix[y][x - 1]) {
        points.push(Point { x: x - 1, y })
    }

    if y < height - 1 && can_move(matrix[y][x], matrix[y + 1][x]) {
        points.push(Point { x, y: y + 1 })
    }

    if y > 0 && can_move(matrix[y][x], matrix[y - 1][x]) {
        points.push(Point { x, y: y - 1 })
    }

    points
}

fn shortest(matrix: &Vec<Vec<char>>, start: &Point, end: &Point) -> i32 {
    let width = matrix[0].len();
    let height = matrix.len();

    let mut queue: VecDeque<Point> = VecDeque::new();
    let mut visited: Vec<Vec<bool>> = vec![vec![false; width]; height];
    let mut distances: Vec<Vec<i32>> = vec![vec![0; width]; height];

    queue.push_back(*start);
    visited[start.y][start.x] = true;

    while !queue.is_empty() {
        let current = queue.pop_front().unwrap();
        for point in movements(matrix, &current) {
            if !visited[point.y][point.x] {
                distances[point.y][point.x] = distances[current.y][current.x] + 1;
                visited[point.y][point.x] = true;
                queue.push_back(point);
            }
        }
    }
    distances[end.y][end.x]
}

fn part_one(input: &Vec<String>) -> String {
    let matrix = matrix(input);
    let start = &search(&matrix, 'S')[0];
    let end = &search(&matrix, 'E')[0];

    let s = shortest(&matrix, start, end);
    format!("{:?}", s)
}

fn part_two(input: &Vec<String>) -> String {
    let matrix = matrix(input);
    let starts = search(&matrix, 'a');
    let end = &search(&matrix, 'E')[0];

    let shortest = starts
        .iter()
        .map(|start| shortest(&matrix, start, end))
        .filter(|i| i > &0)
        .min()
        .unwrap_or(-1);

    format!("{:?}", shortest)
}

fn main() -> Result<(), String> {
    get_input(env::args().collect())
        .map(|lines| (part_one(&lines), part_two(&lines)))
        .map(|(result_one, result_two)| {
            println!("Result One: {}", result_one);
            println!("Result Two: {}", result_two);
        })
}
