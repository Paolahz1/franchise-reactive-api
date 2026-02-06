aws_region = "us-east-1"
project    = "franchises-api"
env        = "dev"

# Database credentials
db_master_username = "admin"
db_master_password = "ChangeThisPassword123!"  # TODO: Use AWS Secrets Manager in production

# Database configuration for development
db_skip_final_snapshot = true  # Skip snapshot on destroy in dev
db_deletion_protection = false  # Allow deletion in dev
