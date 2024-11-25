extern crate photon_rs;

use photon_rs::{channels, colour_spaces, conv, effects, filters, multiple};
use photon_rs::native::{open_image_from_bytes, image_to_bytes};
use std::collections::hash_map::DefaultHasher;
use std::hash::Hasher;

#[no_mangle]
#[allow(non_snake_case)]
pub fn benchmarkRun() -> i32 {
    let img_bytes = include_bytes!("test-image.png");
    let mut img = open_image_from_bytes(img_bytes).expect("Image should be loaded.");

    channels::alter_channel(&mut img, 1, -20);
    colour_spaces::hsl(&mut img, "saturate", 0.1);
    effects::adjust_contrast(&mut img, 30.0);
    filters::filter(&mut img, "twenties");
    filters::filter(&mut img, "serenity");
    filters::filter(&mut img, "radio");
    filters::golden(&mut img);
    conv::edge_one(&mut img);
    conv::noise_reduction(&mut img);
    conv::gaussian_blur(&mut img, 3);
    conv::sharpen(&mut img);

    let mask = open_image_from_bytes(img_bytes).expect("Mask should be loaded.");
    multiple::blend(&mut img, &mask, "soft_light");

    let res_bytes = image_to_bytes(img);
    let mut hasher = DefaultHasher::new();
    hasher.write(&res_bytes);
    //save_image(img, "test-result.png");

    return hasher.finish() as i32;
}

pub fn main() {
    benchmarkRun();
}
