use rust::get_input;
use std::{collections::HashSet, env};

#[derive(Debug, PartialEq, Eq, Hash, Clone)]
struct Point {
    x: usize,
    y: usize,
}

fn part_one(input: &Vec<String>) -> String {
    let points = input
        .iter()
        .flat_map(|l| parse_line(l))
        .collect::<HashSet<Point>>();

    let height = points.iter().map(|p| p.y).max().unwrap() + 2;
    let width = points.iter().map(|p| p.x).max().unwrap() + 2;

    let mut matrix = vec![vec!['.'; width]; height];

    for point in points.iter() {
        matrix[point.y][point.x] = '#';
    }

    let start = Point { x: 500, y: 0 };
    matrix[start.y][start.x] = '+';

    let mut count = 0;
    loop {
        let pos = drop_sand(&mut matrix, start.clone());

        if pos.y == height - 1 {
            break;
        }
        count += 1;
        // _print_matrix(&matrix);
        // println!();
    }

    format!("{}", count)
}

fn part_two(input: &Vec<String>) -> String {
    let points = input
        .iter()
        .flat_map(|l| parse_line(l))
        .collect::<HashSet<Point>>();

    let height = points.iter().map(|p| p.y).max().unwrap() + 3;
    let width = points.iter().map(|p| p.x).max().unwrap() + 300;

    let mut matrix = vec![vec!['.'; width]; height];

    for point in points.iter() {
        matrix[point.y][point.x] = '#';
    }
    for i in 0..width {
        matrix[height - 1][i] = '#';
    }

    let start = Point { x: 500, y: 0 };
    matrix[start.y][start.x] = '+';

    let mut count = 0;
    loop {
        let pos = drop_sand(&mut matrix, start.clone());

        if pos == start {
            break;
        }
        count += 1;
        // _print_matrix(&matrix);
    }

    format!("{}", count + 1)
}

impl Point {
    fn inc_x(&self, n: i32) -> Point {
        Point {
            x: (self.x as i32 + n) as usize,
            y: self.y,
        }
    }
    fn inc_y(&self, n: i32) -> Point {
        Point {
            x: self.x,
            y: (self.y as i32 + n) as usize,
        }
    }
}

fn drop_sand(matrix: &mut Vec<Vec<char>>, start: Point) -> Point {
    let mut position = start;
    while position.y < matrix.len() - 1 {
        if matrix[position.y + 1][position.x] == '.' {
            position = position.inc_y(1);
        } else if matrix[position.y + 1][position.x - 1] == '.' {
            position = position.inc_y(1).inc_x(-1);
        } else if matrix[position.y + 1][position.x + 1] == '.' {
            position = position.inc_y(1).inc_x(1);
        } else {
            break;
        }
    }

    matrix[position.y][position.x] = 'o';

    position
}

fn _print_matrix(matrix: &Vec<Vec<char>>) {
    for row in matrix.iter() {
        for c in row.get(494..).unwrap().iter() {
            print!("{:<1}", c);
        }
        println!();
    }
}

fn main() -> Result<(), String> {
    get_input(env::args().collect())
        .map(|lines| (part_one(&lines), part_two(&lines)))
        .map(|(result_one, result_two)| {
            println!("Result One: {}", result_one);
            println!("Result Two: {}", result_two);
        })
}

fn parse_line(line: &str) -> HashSet<Point> {
    let coords = line
        .split("->")
        .map(|s| s.trim())
        .map(|s| parse_coord(s))
        .collect::<Vec<(usize, usize)>>();

    coords
        .iter()
        .zip(coords.get(1..).unwrap())
        .flat_map(|pair| get_points(pair.0, pair.1))
        .collect::<HashSet<Point>>()
}

/// transform a string with the form nnnnn,mmmm into
/// a pair of (nnnn, mmmm)
fn parse_coord(pair: &str) -> (usize, usize) {
    let coords = pair
        .split(",")
        .map(|s| s.trim())
        .map(|s| s.parse::<usize>().unwrap())
        .collect::<Vec<usize>>();

    assert_eq!(coords.len(), 2);

    (coords[0], coords[1])
}

/// produces a list of points (x,y) based on the range
/// (x1,y1), (x2, y2)
fn get_points(from: &(usize, usize), to: &(usize, usize)) -> Vec<Point> {
    let mut coords = Vec::new();
    let (x1, y1) = from;
    let (x2, y2) = to;
    for x in *x1.min(x2)..=*x2.max(x1) {
        for y in *y1.min(y2)..=*y2.max(y1) {
            coords.push(Point { x, y });
        }
    }
    coords
}

#[cfg(test)]
mod tests {
    use super::*;

    static INPUT: &str = "498,4 -> 498,6 -> 496,6
    503,4 -> 502,4 -> 502,9 -> 494,9";

    #[test]
    fn test_day_14_part_one() {
        let result = part_one(&INPUT.lines().map(|s| String::from(s)).collect());

        assert_eq!(result, "24");
    }

    #[test]
    fn test_day_14_part_two() {
        let result = part_two(&INPUT.lines().map(|s| String::from(s)).collect());

        assert_eq!(result, "93");
    }
}
