# ============================================
# Variables de entrada del módulo ALB
# ============================================

variable "project" {
  description = "Project name"
  type        = string
}

variable "env" {
  description = "Enviroment (dev, prod)"
  type        = string
}


variable "vpc_id" {
  description = "ID of the VPC where the ALB will be created"
  type        = string
}

variable "public_subnet_ids" {
  description = "IDs of the public subnets where the ALB will be deployed"
  type        = list(string)
}

variable "container_port" {
  description = "Port where the application runs inside the container"
  type        = number
  default     = 8080
}

variable "health_check_path" {
  description = "Health check endpoint path"
  type        = string
  default     = "/actuator/health"
}

variable "enable_deletion_protection" {
  description = "Protection against accidental ALB deletion"
  type        = bool
}
