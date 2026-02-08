# ============================================
# NETWORKING - VPC, Subnets, NAT Gateway
# ============================================
# Usa el módulo oficial de AWS para VPC

module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "~> 5.0"

  name = "${var.project}-${var.env}"
  cidr = var.vpc_cidr

  azs             = var.availability_zones
  public_subnets  = [for k, v in var.availability_zones : cidrsubnet(var.vpc_cidr, 8, k)]
  private_subnets = [for k, v in var.availability_zones : cidrsubnet(var.vpc_cidr, 8, k + 10)]

  enable_nat_gateway   = true
  single_nat_gateway   = true  # Para dev - en prod usar false
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Environment = var.env
    Project     = var.project
  }
}
