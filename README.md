# Complaint Chatbot System

An AI-powered customer complaint management system with intelligent conversation handling.

## 🚀 Features

- Real-time chat interface for customer complaints
- AI-powered sentiment analysis and categorization
- Context-aware conversation handling
- Multi-turn conversation support
- Admin analytics dashboard (coming soon)

## 🛠️ Tech Stack

**Backend:**
- Java 17+ with Spring Boot
- PostgreSQL database
- OpenAI API integration
- Spring Data JPA

**Frontend:**
- React 18 with Vite
- Axios for API calls
- Modern responsive UI

## 📁 Project Structure
```
Complaint Chatbot/
├── complaint-chatbot/        # Spring Boot backend
└── complaint-chatbot-ui/     # React frontend
```

## 🔧 Setup Instructions

### Backend Setup

1. Install PostgreSQL and create database:
```sql
CREATE DATABASE complaint_db;
```

2. Configure application properties:
```bash
cd complaint-chatbot/src/main/resources
cp application.properties.example application.properties
# Edit application.properties with your credentials
```

3. Run the backend:
```bash
cd complaint-chatbot
./mvnw spring-boot:run
```

Backend runs on `http://localhost:8080`

### Frontend Setup

1. Install dependencies:
```bash
cd complaint-chatbot-ui
npm install
```

2. Start development server:
```bash
npm run dev
```

Frontend runs on `http://localhost:5173`

## 🔑 Environment Variables

Required in `application.properties`:
- `spring.datasource.password` - PostgreSQL password
- `openai.api.key` - OpenAI API key

## 📝 License

MIT License

## 👤 Author

Ebubechukwu Nwafor
