variable "aws_region" {
  description = "AWS region"
  type        = string
}

variable "project" {
  description = "Project name"
  type        = string
}

variable "env" {
  description = "Environment (dev, prod)"
  type        = string
}

# ============================================
# NETWORKING VARIABLES
# ============================================

variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "availability_zones" {
  description = "Availability Zones for high availability"
  type        = list(string)
  default     = ["us-east-1a", "us-east-1b"]
}

# ============================================
# RDS/MYSQL VARIABLES
# ============================================

variable "mysql_version" {
  description = "MySQL version"
  type        = string
  default     = "8.0.45"
}

variable "db_instance_class" {
  description = "RDS instance class"
  type        = string
  default     = "db.t3.micro"
}

variable "db_allocated_storage" {
  description = "Initial storage in GB"
  type        = number
  default     = 20
}

variable "db_max_allocated_storage" {
  description = "Maximum storage for autoscaling in GB"
  type        = number
  default     = 100
}

variable "db_name" {
  description = "Database name"
  type        = string
  default     = "franchises_db"
}

variable "db_master_username" {
  description = "Database master username"
  type        = string
  sensitive   = true
}

variable "db_master_password" {
  description = "Database master password"
  type        = string
  sensitive   = true
}

variable "db_backup_retention_period" {
  description = "Backup retention period in days"
  type        = number
  default     = 7
}

variable "db_multi_az" {
  description = "Enable Multi-AZ deployment"
  type        = bool
  default     = false
}

variable "db_skip_final_snapshot" {
  description = "Skip final snapshot on deletion"
  type        = bool
  default     = false
}

variable "db_deletion_protection" {
  description = "Enable deletion protection"
  type        = bool
  default     = true
}

variable "db_publicly_accessible" {
  description = "Make the RDS instance publicly accessible"
  type        = bool
  default     = false
}

# ============================================
# ALB VARIABLES
# ============================================

variable "container_port" {
  description = "Port where the application runs inside the container (Spring Boot)"
  type        = number
  default     = 8080
}

variable "health_check_path" {
  description = "Health check endpoint path"
  type        = string
  default     = "/actuator/health"
}

variable "alb_deletion_protection" {
  description = "Protection against accidental ALB deletion"
  type        = bool
  default     = false
}

# ============================================
# ECS VARIABLES
# ============================================

variable "ecs_task_cpu" {
  description = "CPU units for ECS task (256, 512, 1024, 2048, 4096)"
  type        = string
  default     = "512"
}

variable "ecs_task_memory" {
  description = "Memory for ECS task in MB"
  type        = string
  default     = "1024"
}

variable "container_name" {
  description = "Container name"
  type        = string
  default     = "franchise-api"
}

variable "ecs_desired_count" {
  description = "Desired number of ECS tasks"
  type        = number
  default     = 2
}

variable "ecs_min_capacity" {
  description = "Minimum number of tasks for auto scaling"
  type        = number
  default     = 1
}

variable "ecs_max_capacity" {
  description = "Maximum number of tasks for auto scaling"
  type        = number
  default     = 4
}

variable "ecs_cpu_target" {
  description = "CPU target for auto scaling (%)"
  type        = number
  default     = 70
}

variable "ecs_memory_target" {
  description = "Memory target for auto scaling (%)"
  type        = number
  default     = 80
}

variable "ecs_log_retention_days" {
  description = "Log retention period in CloudWatch (days)"
  type        = number
  default     = 7
}
