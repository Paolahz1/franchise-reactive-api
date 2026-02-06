#!/bin/bash
# Script para conectarse a MySQL vía Session Manager Port Forwarding

set -e

cd "$(dirname "$0")"

echo "🔍 Obteniendo información de la infraestructura..."

BASTION_ID=$(terraform output -raw bastion_instance_id)
DB_ENDPOINT=$(terraform output -raw db_instance_address)
DB_PORT=3306
LOCAL_PORT=3307

echo "📦 Bastion Instance: $BASTION_ID"
echo "🗄️  Database Endpoint: $DB_ENDPOINT"
echo ""

# Verificar que Session Manager plugin está instalado
if ! command -v session-manager-plugin &> /dev/null; then
    echo "❌ Session Manager plugin no está instalado"
    echo ""
    echo "Instálalo con:"
    echo "  brew install --cask session-manager-plugin"
    echo ""
    exit 1
fi

echo "✅ Session Manager plugin instalado"
echo ""
echo "🚀 Iniciando port forwarding..."
echo "   Local: localhost:$LOCAL_PORT"
echo "   Remote: $DB_ENDPOINT:$DB_PORT"
echo ""
echo "💡 En otra terminal, conecta con:"
echo "   mysql -h 127.0.0.1 -P $LOCAL_PORT -u admin -p"
echo "   Password: ChangeThisPassword123!"
echo ""
echo "Presiona Ctrl+C para detener el túnel"
echo ""

aws ssm start-session \
    --target "$BASTION_ID" \
    --document-name AWS-StartPortForwardingSessionToRemoteHost \
    --parameters "{\"host\":[\"$DB_ENDPOINT\"],\"portNumber\":[\"$DB_PORT\"],\"localPortNumber\":[\"$LOCAL_PORT\"]}"
