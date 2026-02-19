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
# RDS/MYSQL VARIABLES
# ============================================

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

# ============================================
# ALB VARIABLES
# ============================================

variable "alb_deletion_protection" {
  description = "Protection against accidental ALB deletion"
  type        = bool
  default     = false
}


