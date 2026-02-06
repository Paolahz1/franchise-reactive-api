aws_region = "us-east-1"
project    = "franchises-api"
env        = "dev"

# Database credentials
db_master_username = "admin"
db_master_password = "ChangeThisPassword123!"  # TODO: Use AWS Secrets Manager in production

# Database configuration for development
db_skip_final_snapshot = true  # Skip snapshot on destroy in dev
db_deletion_protection = false  # Allow deletion in dev
db_publicly_accessible = false  # DB is PRIVATE - use bastion tunnel for local access

# ALB configuration
container_port          = 8080  # Spring Boot default port
health_check_path       = "/actuator/health"  # Spring Actuator health endpoint
alb_deletion_protection = false  # Allow deletion in dev
