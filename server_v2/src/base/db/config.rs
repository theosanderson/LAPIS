use serde::Deserialize;

#[derive(Debug, Deserialize)]
pub struct DatabaseConfig {
    pub host: String,
    pub port: u16,
    pub dbname: String,
    pub username: String,
    pub password: String,
    pub schema: String,
}