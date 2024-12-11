use rust::get_input;
use std::cmp::Ordering;
use std::ops::{Add, AddAssign, Sub};
use std::{collections::HashSet, env};

enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT,
}

struct Movement {
    direction: Direction,
    distance: usize,
}

#[derive(Debug, Hash, Eq, Clone, Copy)]
struct Point {
    x: i32,
    y: i32,
}

struct Simulation {
    num_of_tails: usize,
}

struct Step {
    head: Point,
    tail: Vec<Point>,
}

fn get_movements(input: &Vec<String>) -> Vec<Movement> {
    input
        .iter()
        .map(|l| {
            let parts = l.split(" ").collect::<Vec<_>>();
            let direction = parts[0].chars().collect::<Vec<char>>()[0];
            let distance = parts[1].parse().unwrap();

            Movement::new(direction, distance)
        })
        .collect()
}

fn part_one(input: &Vec<String>) -> String {
    let movements = get_movements(input);
    let mut simulation = Simulation::new(1);
    let result = simulation.simulate(&movements);
    return format!("{}", result);
}

fn part_two(input: &Vec<String>) -> String {
    let movements = get_movements(input);
    let mut simulation = Simulation::new(9);
    let result = simulation.simulate(&movements);
    return format!("{}", result);
}

fn main() -> Result<(), String> {
    get_input(env::args().collect())
        .map(|lines| (part_one(&lines), part_two(&lines)))
        .map(|(result_one, result_two)| {
            println!("Result One: {}", result_one);
            println!("Result Two: {}", result_two);
        })
}

impl Direction {
    fn of(direction: char) -> Direction {
        match direction {
            'U' => Self::UP,
            'D' => Self::DOWN,
            'L' => Self::LEFT,
            'R' => Self::RIGHT,
            _ => panic!("Unknown value"),
        }
    }
}

impl Movement {
    fn new(direction: char, distance: usize) -> Self {
        Self {
            direction: Direction::of(direction),
            distance,
        }
    }
}

impl Point {
    fn distance(&self, other: &Point) -> u32 {
        let a = i32::pow(self.x as i32 - other.x as i32, 2) as f32;
        let b = i32::pow(self.y as i32 - other.y as i32, 2) as f32;

        f32::sqrt(a + b) as u32
    }

    fn new(x: i32, y: i32) -> Point {
        Point { x, y }
    }
}

impl Simulation {
    fn new(num_of_tails: usize) -> Simulation {
        Simulation { num_of_tails }
    }

    fn simulate(&mut self, movements: &Vec<Movement>) -> usize {
        let mut step = Step {
            head: Point::new(0, 0),
            tail: vec![Point::new(0, 0); self.num_of_tails],
        };
        let mut tail_movements = HashSet::from([Point::new(0, 0)]);

        for movement in movements.iter() {
            let movements: HashSet<Point>;

            let offset = match movement.direction {
                Direction::UP => Point::new(0, 1),
                Direction::DOWN => Point::new(0, -1),
                Direction::LEFT => Point::new(-1, 0),
                Direction::RIGHT => Point::new(1, 0),
            };
            (step, movements) = Simulation::step(&step, offset, movement.distance);
            tail_movements.extend(movements);
        }
        tail_movements.len()
    }

    fn step(current: &Step, offset: Point, distance: usize) -> (Step, HashSet<Point>) {
        let mut tail_movements = HashSet::new();
        let mut next_step = Step {
            head: current.head,
            tail: current.tail.clone(),
        };

        for _ in 0..distance {
            next_step.head += offset;

            let mut prev = &next_step.head;
            for (i, t) in next_step.tail.clone().iter().enumerate() {
                if prev.distance(&t) > 1 {
                    let difference = *prev - *t;

                    next_step.tail[i] = *t
                        + Point::new(
                            cmp_or(difference.x, 0, -1, 1),
                            cmp_or(difference.y, 0, -1, 1),
                        );
                }
                prev = &next_step.tail[i];
            }
            tail_movements.insert(*next_step.tail.last().unwrap());
        }

        (next_step, tail_movements)
    }
}

fn cmp_or(a: i32, b: i32, less: i32, greater: i32) -> i32 {
    match a.cmp(&b) {
        Ordering::Less => less,
        Ordering::Equal => a,
        Ordering::Greater => greater,
    }
}

impl PartialEq for Point {
    fn eq(&self, other: &Point) -> bool {
        self.x == other.x && self.y == other.y
    }
}

impl Add for Point {
    type Output = Self;

    fn add(self, other: Self) -> Self {
        Self {
            x: self.x + other.x,
            y: self.y + other.y,
        }
    }
}

impl Sub for Point {
    type Output = Self;

    fn sub(self, other: Self) -> Self {
        Self {
            x: self.x - other.x,
            y: self.y - other.y,
        }
    }
}

impl AddAssign for Point {
    fn add_assign(&mut self, other: Self) {
        *self = Self {
            x: self.x + other.x,
            y: self.y + other.y,
        };
    }
}
