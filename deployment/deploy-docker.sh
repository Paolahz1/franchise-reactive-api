#!/bin/bash
# Script completo: build + push en un solo comando
# Uso: ./deploy-docker.sh [tag] [region]

set -e

TAG=${1:-latest}
REGION=${2:-us-east-1}
SCRIPT_DIR="$(dirname "$0")"

echo "🚀 Proceso completo de build y deploy Docker"
echo "=============================================="
echo ""

# Paso 1: Build
echo "📦 PASO 1/2: Construyendo imagen..."
"$SCRIPT_DIR/build-image.sh" "$TAG"

echo ""
echo "=============================================="
echo ""

# Paso 2: Push
echo "📤 PASO 2/2: Subiendo a ECR..."
"$SCRIPT_DIR/push-to-ecr.sh" "$TAG" "$REGION"

echo ""
echo "=============================================="
echo "✅ Deploy completado exitosamente"
echo "=============================================="
