# ============================================
# Variables de entrada del módulo Bastion
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
  description = "VPC ID"
  type        = string
}

variable "private_subnet_id" {
  description = "Private subnet ID for bastion"
  type        = string
}
