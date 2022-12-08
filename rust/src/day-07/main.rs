use rust::get_input;
use std::env;

#[derive(Debug)]
struct Filesystem {
    root: Box<Dir>,
    working_directory: Box<Dir>,
}

    impl Filesystem {
        pub fn new() -> Filesystem {
        let root = Box::from(Dir::new("/"));
        Filesystem {
            root: root,
            working_directory: root,
        }
    }
}

#[derive(Debug)]
struct File {
    name: String,
    size: usize,
}

impl File {
    pub fn new(name: &str, size: usize) -> File {
        File {
            name: String::from(name),
            size,
        }
    }
}

#[derive(Debug)]
struct Dir {
    name: String,
    files: Vec<File>,
    children: Vec<Dir>,
}

impl Dir {
    pub fn new(name: &str) -> Dir {
        Dir {
            name: String::from(name),
            files: Vec::new(),
            children: Vec::new(),
        }
    }
}

fn part_one(input: &Vec<String>) -> String {
    let mut filesystem = Dir::new("/");

    filesystem.files.push(File::new("A", 10));

    format!("Input length: {}", input.len())
}

fn part_two(input: &Vec<String>) -> String {
    format!("Input length: {}", input.len())
}

fn main() -> Result<(), String> {
    get_input(env::args().collect())
        .map(|lines| (part_one(&lines), part_two(&lines)))
        .map(|(result_one, result_two)| {
            println!("Result One: {}", result_one);
            println!("Result Two: {}", result_two);
        })
}
