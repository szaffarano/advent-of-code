use regex::Regex;
use rust::get_input;
use std::{collections::HashSet, env};

#[derive(Debug, Clone)]
struct Measure {
    sensor: Point,
    beacon: Point,
}

#[derive(Debug, Clone, PartialEq, Eq, Hash)]
struct Point {
    x: i32,
    y: i32,
}

impl Measure {
    fn distance_to_beacon(&self) -> u32 {
        ((self.sensor.x - self.beacon.x).abs() + (self.sensor.y - self.beacon.y).abs()) as u32
    }
    fn distance_to(&self, point: &Point) -> u32 {
        ((self.sensor.x - point.x).abs() + (self.sensor.y - point.y).abs()) as u32
    }
    fn explored_area(&self) -> Vec<(i32, i32, i32)> {
        let mut points = Vec::new();

        let distance = self.distance_to_beacon() as i32;

        for i in 0..=distance {
            let value = (2 * i) + 1;
            let y = (self.sensor.y - distance) + i;
            let from_x = self.sensor.x - (value / 2);
            let to_x = self.sensor.x + (value / 2);
            points.push((y, from_x, to_x));

            let value = distance - i - 1;
            let y = i + self.sensor.y + 1;
            let from_x = self.sensor.x - value;
            let to_x = self.sensor.x + value;
            points.push((y, from_x, to_x));
        }

        points
    }
}

fn part_one(input: &Vec<String>) -> String {
    let measures = input
        .iter()
        .map(|line| parse_line(line))
        .collect::<Vec<Measure>>();

    let max_x = measures
        .iter()
        .map(|m| m.sensor.x + m.distance_to_beacon() as i32)
        .max()
        .unwrap();
    let min_x = measures
        .iter()
        .map(|m| m.sensor.x - m.distance_to_beacon() as i32)
        .min()
        .unwrap();

    let mut count = HashSet::new();
    let y = 2000000;
    for m in measures {
        for x in min_x..max_x {
            let p = Point { x, y };
            if m.distance_to(&p) <= m.distance_to_beacon() {
                count.insert(p);
            }
        }
    }

    let result = count.len().overflowing_sub(1).0;
    format!("{}", result)
}

fn part_two(input: &Vec<String>) -> String {
    println!("running part 2");
    let measures = input
        .iter()
        .map(|line| parse_line(line))
        .collect::<Vec<Measure>>();

    // let limit = 20;
    let limit = 4000000;

    let mut explored_areas = measures
        .iter()
        .flat_map(|m| {
            m.explored_area()
                .into_iter()
                .filter(|a| a.0 >= 0 && a.0 <= limit)
                .map(|a| (a.0, a.1.max(0), a.2.min(limit)))
                .collect::<Vec<_>>()
        })
        .collect::<Vec<_>>();

    explored_areas.sort();

    let mut y = 0;
    let mut x = 0;
    let mut result = 0;
    for a in explored_areas {
        if a.0 != y {
            if x < limit {
                let x: u64 = x as u64 + 1;
                result = (x * 4000000) + y as u64;
                break;
            }
            x = 0;
            y = a.0;
        }
        if (a.1..=a.2).contains(&x) {
            x = a.2.min(limit);
        }
    }

    format!("{}", result)
}

fn parse_line(line: &str) -> Measure {
    let re =
        Regex::new(r"^Sensor at x=(.+), y=(.+): closest beacon is at x=(.+), y=(.*)$").unwrap();

    let captures = re.captures(line).unwrap();
    assert_eq!(captures.len(), 5);

    Measure {
        sensor: Point {
            x: captures[1].parse::<i32>().unwrap(),
            y: captures[2].parse::<i32>().unwrap(),
        },
        beacon: Point {
            x: captures[3].parse::<i32>().unwrap(),
            y: captures[4].parse::<i32>().unwrap(),
        },
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
