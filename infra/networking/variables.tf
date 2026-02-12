# ============================================
# Variables de entrada del módulo Networking
# ============================================

variable "project" {
  description = "Project name"
  type        = string
}

variable "env" {
  description = "Enviroment (dev, prod)"
  type        = string
}

variable "vpc_cidr" {
  description = "CIDR block para la VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "availability_zones" {
  description = "Availability Zones for high availability"
  type        = list(string)
  default     = ["us-east-1a", "us-east-1b"]
}
