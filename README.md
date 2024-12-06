# Forum

Forum is a feature-rich and intuitive application designed to manage an online forum service. This repository provides a seamless setup using Docker Compose for quick deployment.

---

## 🚀 Getting Started

Follow these instructions to set up and run the Forum application on your local machine.

### Prerequisites
- **Git**: [Install Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
- **Docker**: [Install Docker](https://docs.docker.com/get-docker/)

---

### 📥 Clone the Repository

```bash
git clone https://github.com/ftelega/forum.git
cd forum
```

---


### ☕ Compile the project (make sure docker is running!)

```bash
./mvnw clean package
```

---

### 🐳 Start the Application with Docker Compose

```bash
docker-compose up --build
```

---

### 🌐 Access the Application

```text
http://localhost:8888/swagger-ui.html
```
Admin user prebuilt with: 
1. username: admin
2. password: admin