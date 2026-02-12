# ============================================
# Variables de entrada del módulo Bastion
# ============================================

variable "project" {}

variable "env" {}

variable "vpc_id" {
  description = "VPC ID"
  type        = string
}

variable "private_subnet_id" {
  description = "Private subnet ID for bastion"
  type        = string
}
