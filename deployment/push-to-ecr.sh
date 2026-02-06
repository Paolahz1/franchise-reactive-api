#!/bin/bash
# Script para subir imagen a AWS ECR
# Uso: ./push-to-ecr.sh [tag] [region]

set -e

TAG=${1:-latest}
REGION=${2:-us-east-1}
IMAGE_NAME="franchise-service"

echo "☁️  Preparando push a AWS ECR..."
echo "   Tag: $TAG"
echo "   Region: $REGION"
echo ""

# Obtener URL del repositorio ECR desde Terraform
cd "$(dirname "$0")/../infra"
ECR_URL=$(terraform output -raw ecr_repository_url 2>/dev/null || echo "")

if [ -z "$ECR_URL" ]; then
    echo "❌ No se pudo obtener la URL del ECR."
    echo "   Verifica que hayas desplegado la infraestructura con Terraform."
    echo ""
    echo "   cd infra && terraform apply"
    exit 1
fi

echo "📍 ECR Repository: $ECR_URL"
echo ""

# Login a ECR
echo "🔐 Autenticando con ECR..."
aws ecr get-login-password --region "$REGION" | \
    docker login --username AWS --password-stdin "$ECR_URL"

echo ""
echo "✅ Autenticación exitosa"
echo ""

# Tag de la imagen para ECR
echo "🏷️  Tageando imagen para ECR..."
docker tag "$IMAGE_NAME:$TAG" "$ECR_URL:$TAG"

# Push a ECR
echo "📤 Subiendo imagen a ECR..."
docker push "$ECR_URL:$TAG"

echo ""
echo "✅ Imagen subida exitosamente a ECR"
echo ""
echo "URL de la imagen: $ECR_URL:$TAG"
echo ""
echo "Para desplegar en ECS/Fargate, usa esta URL en la definición de tareas."
