#!/bin/bash
# Script para construir imagen Docker localmente
# Uso: ./build-image.sh [tag]

set -e

TAG=${1:-latest}
IMAGE_NAME="franchise-service"
DOCKERFILE_PATH="$(dirname "$0")/Dockerfile"
PROJECT_ROOT="$(dirname "$0")/.."

echo "🐳 Construyendo imagen Docker..."
echo "   Imagen: $IMAGE_NAME:$TAG"
echo "   Dockerfile: $DOCKERFILE_PATH"
echo ""

cd "$PROJECT_ROOT"

# Opción 1: Multi-stage build (build dentro de Docker)
echo "📦 Ejecutando multi-stage build..."
docker build \
  -f "$DOCKERFILE_PATH" \
  -t "$IMAGE_NAME:$TAG" \
  .

echo ""
echo "✅ Imagen construida exitosamente: $IMAGE_NAME:$TAG"
echo ""
echo "Para ejecutar localmente:"
echo "  docker run -p 8080:8080 $IMAGE_NAME:$TAG"
echo ""
echo "Para ver la imagen:"
echo "  docker images | grep $IMAGE_NAME"
