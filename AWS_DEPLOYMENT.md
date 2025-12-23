# AWS Deployment Guide – Shadow Ledger System

## Service Deployed
**Shadow Ledger Service**

This deployment demonstrates cloud deployment capability by running a single microservice on AWS EC2 using Docker and Amazon ECR.

---

## Architecture Overview

```
Client
|
| HTTP (8082)
v
AWS EC2 (Docker Container)
|
| JDBC / Kafka
v
PostgreSQL + Kafka (local / external)
```

- Deployed service: Shadow Ledger Service
- Port exposed: 8082
- Runtime: Docker container
- Cloud provider: AWS (EC2 + ECR)

---

## Step 1: Build Docker Image Locally

```bash
cd shadow-ledger-service

# Build JAR
./mvnw clean package -DskipTests

# Build Docker image
docker build -t shadow-ledger-service:latest .
```

---

## Step 2: Push Image to Amazon ECR

### Create ECR Repository

```bash
aws ecr create-repository \
  --repository-name shadow-ledger-service \
  --region us-east-1
```

### Authenticate Docker to ECR

```bash
aws ecr get-login-password --region us-east-1 | \
docker login --username AWS --password-stdin \
YOUR_AWS_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com
```

### Tag and Push Image

```bash
docker tag shadow-ledger-service:latest \
YOUR_AWS_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/shadow-ledger-service:latest

docker push YOUR_AWS_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/shadow-ledger-service:latest
```

---

## Step 3: Launch EC2 Instance

**Instance Configuration**
- AMI: Amazon Linux 2023
- Instance Type: t3.micro (Free Tier)
- Storage: 8 GB
- Key Pair: Existing or new
- Security Group:
  - Port 22 (SSH) – your IP
  - Port 8082 (HTTP) – 0.0.0.0/0

---

## Step 4: Connect to EC2 & Install Docker

```bash
ssh -i YOUR_KEY.pem ec2-user@YOUR_EC2_PUBLIC_IP

sudo yum update -y
sudo yum install docker -y
sudo systemctl start docker
sudo usermod -aG docker ec2-user

exit
ssh -i YOUR_KEY.pem ec2-user@YOUR_EC2_PUBLIC_IP
```

---

## Step 5: Pull Image and Run Container

### Login to ECR from EC2

```bash
aws ecr get-login-password --region us-east-1 | \
docker login --username AWS --password-stdin \
YOUR_AWS_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com
```

### Pull Image

```bash
docker pull YOUR_AWS_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/shadow-ledger-service:latest
```

### Run Container

```bash
docker run -d \
  --name shadow-ledger-service \
  -p 8082:8082 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://HOST:5432/postgres \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=HOST:9092 \
  YOUR_AWS_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/shadow-ledger-service:latest
```

---

## Step 6: Verify Deployment

### Health Check

```bash
curl http://YOUR_EC2_PUBLIC_IP:8082/actuator/health
```
**Expected:**
```json
{"status":"UP"}
```

### Shadow Balance API

```bash
curl http://YOUR_EC2_PUBLIC_IP:8082/accounts/A10/shadow-balance
```
**Expected:**
```json
{
  "accountId": "A10",
  "balance": 750.0,
  "lastEvent": "E1003"
}
```

---

## Environment Variables Used

| Variable                      | Description                |
|-------------------------------|----------------------------|
| SPRING_DATASOURCE_URL         | PostgreSQL JDBC URL        |
| SPRING_DATASOURCE_USERNAME    | Database username          |
| SPRING_DATASOURCE_PASSWORD    | Database password          |
| SPRING_KAFKA_BOOTSTRAP_SERVERS| Kafka broker address       |

---

## Monitoring & Logs

```bash
docker ps
docker logs -f shadow-ledger-service
docker stats shadow-ledger-service
```

---

## Cleanup

```bash
docker stop shadow-ledger-service
docker rm shadow-ledger-service

aws ec2 terminate-instances --instance-ids i-xxxxx

aws ecr delete-repository \
  --repository-name shadow-ledger-service \
  --force
```

---



