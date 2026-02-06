# Backend S3 para Terraform State
# Este archivo configura el almacenamiento remoto del estado de Terraform
# Previene conflictos y pérdida de state

terraform {
  backend "s3" {
    bucket         = "franchises-api-terraform-state"
    key            = "dev/terraform.tfstate"
    region         = "us-east-1"
    encrypt        = true
    dynamodb_table = "franchises-api-terraform-locks"
  }
}
