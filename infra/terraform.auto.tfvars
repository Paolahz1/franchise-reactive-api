aws_region = "us-east-1"
project    = "franchises-api"
env        = "dev"

# Database credentials
db_master_username = "admin"
# db_master_password se pasa desde GitHub Secrets como TF_VAR_db_master_password

# Database configuration for development
db_skip_final_snapshot = true  # Skip snapshot on destroy in dev
db_deletion_protection = false # Allow deletion in dev

# ALB configuration
alb_deletion_protection = false # Allow deletion in dev
