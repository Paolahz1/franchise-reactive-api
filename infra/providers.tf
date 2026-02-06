terraform {
  required_version = ">= 1.5.0"
  
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.6"
    }
  }

  # Backend S3 para Terraform State
  # Almacenamiento remoto con bloqueo vía DynamoDB
  backend "s3" {
    bucket         = "franchises-api-terraform-state"
    key            = "dev/terraform.tfstate"
    region         = "us-east-1"
    encrypt        = true
    dynamodb_table = "franchises-api-terraform-locks"
  }
}

provider "aws" {
  region = var.aws_region
}
