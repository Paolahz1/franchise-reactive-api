# ============================================
# FASE 1: NETWORKING
# ============================================
# VPC, Subnets, Internet Gateway, NAT Gateway, Route Tables

module "networking" {
  source = "./modules/networking"

  project            = var.project
  env                = var.env
  vpc_cidr           = var.vpc_cidr
  availability_zones = var.availability_zones
}

