#!/bin/bash
# Crea un bastion temporal para acceder a RDS

set -e

echo "🔧 Creando instancia EC2 bastion temporal..."

# Obtener VPC y subnet pública
VPC_ID=$(terraform output -raw vpc_id)
PUBLIC_SUBNET=$(terraform output -json public_subnet_ids | jq -r '.[0]')

# Crear security group para bastion
BASTION_SG=$(aws ec2 create-security-group \
  --group-name franchises-bastion-temp \
  --description "Temporary bastion for RDS access" \
  --vpc-id $VPC_ID \
  --query 'GroupId' \
  --output text)

echo "✅ Security group creado: $BASTION_SG"

# Permitir SSH desde tu IP
MY_IP=$(curl -s https://checkip.amazonaws.com)
aws ec2 authorize-security-group-ingress \
  --group-id $BASTION_SG \
  --protocol tcp \
  --port 22 \
  --cidr $MY_IP/32

echo "✅ SSH permitido desde $MY_IP"

# Actualizar RDS security group para permitir conexiones desde bastion
RDS_SG=$(terraform output -raw db_security_group_id)
aws ec2 authorize-security-group-ingress \
  --group-id $RDS_SG \
  --protocol tcp \
  --port 3306 \
  --source-group $BASTION_SG

# Crear instancia EC2
INSTANCE_ID=$(aws ec2 run-instances \
  --image-id resolve:ssm:/aws/service/ami-amazon-linux-latest/al2023-ami-kernel-default-x86_64 \
  --instance-type t2.micro \
  --subnet-id $PUBLIC_SUBNET \
  --security-group-ids $BASTION_SG \
  --associate-public-ip-address \
  --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=franchises-bastion-temp}]' \
  --query 'Instances[0].InstanceId' \
  --output text)

echo "✅ Instancia EC2 creada: $INSTANCE_ID"
echo "⏳ Esperando que esté disponible..."

aws ec2 wait instance-running --instance-ids $INSTANCE_ID

PUBLIC_IP=$(aws ec2 describe-instances \
  --instance-ids $INSTANCE_ID \
  --query 'Reservations[0].Instances[0].PublicIpAddress' \
  --output text)

DB_ENDPOINT=$(terraform output -raw db_instance_address)

echo ""
echo "🎉 Bastion listo!"
echo ""
echo "📝 Para conectarte a MySQL:"
echo ""
echo "1. Instala MySQL client en el bastion (primera vez):"
echo "   aws ec2-instance-connect send-ssh-public-key --instance-id $INSTANCE_ID --instance-os-user ec2-user --ssh-public-key file://~/.ssh/id_rsa.pub"
echo "   ssh ec2-user@$PUBLIC_IP"
echo "   sudo yum install -y mysql"
echo ""
echo "2. Port forwarding local (desde tu Mac):"
echo "   ssh -i ~/.ssh/your-key.pem -L 3307:$DB_ENDPOINT:3306 ec2-user@$PUBLIC_IP -N"
echo ""
echo "3. Conectar desde tu Mac:"
echo "   mysql -h 127.0.0.1 -P 3307 -u admin -p"
echo ""
echo "🗑️  Para eliminar el bastion cuando termines:"
echo "   aws ec2 terminate-instances --instance-ids $INSTANCE_ID"
echo "   aws ec2 delete-security-group --group-id $BASTION_SG"
echo ""
