#!/bin/bash
# Script para despliegue por fases de infraestructura
# Uso: ./deploy.sh <fase>
# Fases: init, networking, ecr, rds, all, destroy

set -e

PHASE=${1:-help}
cd "$(dirname "$0")"

case $PHASE in
  init)
    echo "🔧 Inicializando Terraform..."
    terraform init
    ;;
    
  networking)
    echo "🌐 Desplegando Networking (VPC, Subnets, NAT)..."
    terraform apply -target=module.networking -auto-approve
    ;;
    
  ecr)
    echo "📦 Desplegando ECR (Docker Repository)..."
    terraform apply -target=module.ecr -auto-approve
    ;;
    
  rds)
    echo "🗄️  Desplegando RDS (MySQL Database)..."
    terraform apply -target=module.rds -auto-approve
    ;;
    
  all)
    echo "🚀 Desplegando toda la infraestructura..."
    terraform apply -auto-approve
    ;;
    
  plan)
    echo "📋 Mostrando plan de ejecución..."
    terraform plan
    ;;
    
  destroy)
    echo "💥 Destruyendo toda la infraestructura..."
    read -p "⚠️  ¿Estás seguro? (yes/no): " confirm
    if [ "$confirm" == "yes" ]; then
      terraform destroy -auto-approve
    else
      echo "Cancelado."
    fi
    ;;
    
  help|*)
    echo "📚 Uso: ./deploy.sh <comando>"
    echo ""
    echo "Comandos disponibles:"
    echo "  init       - Inicializar Terraform"
    echo "  networking - Desplegar solo VPC y subnets"
    echo "  ecr        - Desplegar solo ECR"
    echo "  rds        - Desplegar solo RDS MySQL"
    echo "  all        - Desplegar todo"
    echo "  plan       - Ver plan sin aplicar"
    echo "  destroy    - Destruir toda la infraestructura"
    echo ""
    echo "Ejemplo de flujo completo:"
    echo "  ./deploy.sh init"
    echo "  ./deploy.sh networking"
    echo "  ./deploy.sh ecr"
    echo "  ./deploy.sh rds"
    ;;
esac
