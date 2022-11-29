use std::fs;
use std::io;
use std::io::BufRead;

pub fn get_input(args: Vec<String>) -> Result<Vec<String>, String> {
    args.len()
        .eq(&2)
        .then(|| &args[1])
        .ok_or_else(|| format!("Usage {} <input>", &args[0]))
        .and_then(|path| get_lines(path).map_err(|error| format!("{:?}", error)))
}

fn get_lines(path: &str) -> Result<Vec<String>, io::Error> {
    fs::File::open(path)
        .map(|input| io::BufReader::new(input).lines())
        .map(|lines| {
            let mut input: Vec<String> = Vec::new();
            for line in lines {
                if let Ok(l) = line {
                    input.push(l);
                }
            }
            input
        })
}
