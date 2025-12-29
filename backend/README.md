# Accident Detection Backend System

A comprehensive backend system for an Accident Detection Application built with Spring Boot, MySQL, Python ML Service, and Twilio SMS integration.

## Architecture

- **Backend**: Spring Boot (Java)
- **Database**: MySQL (XAMPP)
- **ML Service**: Python Flask (Synthetic Rule-based Model)
- **SMS Service**: Twilio API
- **Authentication**: JWT (JSON Web Tokens)

## Features

- User Registration & Authentication
- Sensor Data Processing (Accelerometer, Gyroscope, Speed)
- Accident Detection using ML Service
- 30-second User Response Timer
- Emergency SMS Alerts via Twilio
- Comprehensive Logging System

## Database Schema

The system uses three main tables:

1. **users**: User information
2. **emergency_contacts**: Emergency contact numbers for each user
3. **accident_logs**: Accident detection logs with location and response status

See `schema.sql` for the complete database schema.

## Setup Instructions

### Prerequisites

1. Java 17 or higher
2. Maven
3. MySQL (XAMPP)
4. Python 3.8+ (for ML service)
5. Twilio Account (for SMS functionality)

### 1. Database Setup

1. Start XAMPP and ensure MySQL is running
2. Open phpMyAdmin or MySQL command line
3. Run the `schema.sql` file to create the database and tables:

```sql
mysql -u root -p < schema.sql
```

Or import it via phpMyAdmin.

### 2. Configuration

Update `src/main/resources/application.properties` with your database credentials:

```properties
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

Update Twilio credentials (or set as environment variables):

```properties
twilio.account.sid=your-twilio-account-sid
twilio.auth.token=your-twilio-auth-token
twilio.phone.number=+1234567890
```

### 3. Build and Run Spring Boot Application

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The Spring Boot application will run on `http://localhost:8080`

### 4. Setup and Run Python ML Service

```bash
cd ml-service

# Install dependencies
pip install -r requirements.txt

# Run the ML service
python app.py
```

The ML service will run on `http://localhost:5000`

## API Endpoints

### Authentication

#### POST /api/auth/register
Register a new user.

**Request Body:**
```json
{
    "name": "John Doe",
    "dob": "1990-01-01",
    "phone": "+1234567890",
    "password": "password123",
    "contactNumber1": "+0987654321",
    "contactNumber2": "+1122334455"
}
```

**Response:**
```json
{
    "success": true,
    "message": "Registration successful",
    "data": {
        "token": "jwt_token_here",
        "userId": 1,
        "message": "Registration successful"
    }
}
```

#### POST /api/auth/login
Login user.

**Request Body:**
```json
{
    "phone": "+1234567890",
    "password": "password123"
}
```

**Response:**
```json
{
    "success": true,
    "message": "Login successful",
    "data": {
        "token": "jwt_token_here",
        "userId": 1,
        "message": "Login successful"
    }
}
```

### Sensor Data

#### POST /api/sensor/sendSensorData
Send sensor data for accident detection.

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Request Body:**
```json
{
    "accelX": 10.5,
    "accelY": 5.2,
    "accelZ": 2.1,
    "gyroX": 15.3,
    "gyroY": 8.7,
    "gyroZ": 3.4,
    "speed": 45.0,
    "timestamp": "2024-01-15T10:30:00",
    "latitude": 40.7128,
    "longitude": -74.0060
}
```

**Response:**
```json
{
    "success": true,
    "message": "Sensor data processed",
    "data": {
        "accident": false,
        "message": "Safe"
    }
}
```

### Accident Detection

#### POST /api/accident/predictAccident
Predict accident from sensor data (direct ML service call).

**Request Body:**
```json
{
    "accelX": 18.5,
    "accelY": 12.2,
    "accelZ": 8.1,
    "gyroX": 25.3,
    "gyroY": 18.7,
    "gyroZ": 15.4,
    "speed": 3.0
}
```

**Response:**
```json
{
    "success": true,
    "message": "Prediction completed",
    "data": {
        "accident": true,
        "message": "Accident detected"
    }
}
```

#### POST /api/accident/userResponse
Record user response to accident alert.

**Request Body:**
```json
{
    "accidentId": 1,
    "response": "YES"
}
```

**Response Statuses:**
- `YES`: User confirms they are safe (no alert sent)
- `NO`: User confirms accident, alert sent immediately
- `NO_RESPONSE`: No response within 30 seconds, auto-alert sent

#### POST /api/accident/sendAlert
Manually trigger emergency alert.

**Request Body:**
```json
{
    "accidentId": 1
}
```

## Accident Detection Logic

1. **ML Prediction**: When sensor data is received, it's sent to the Python ML service
2. **If ML returns `accident: false`**: System responds with "SAFE"
3. **If ML returns `accident: true`**:
   - An accident log is created with status `NO_RESPONSE`
   - A 30-second timer starts
   - Frontend is notified to show alert to user
   - **If user responds `YES`**: Log updated to `YES`, no alert sent
   - **If user responds `NO`**: Log updated to `NO`, alert sent immediately
   - **If no response**: After 30 seconds, alert sent automatically

## SMS Alert Format

When an alert is sent, emergency contacts receive:

```
🚨 Accident Detected!
Location: https://maps.google.com/?q=40.712800,-74.006000
```

## ML Service Rules

The Python ML service uses rule-based logic:

1. **High Acceleration**: > 15 m/s² → accident
2. **High Gyroscope + Speed Drop**: Gyroscope > 20 rad/s AND speed < 5 m/s → accident
3. **Sudden Deceleration**: Acceleration magnitude > 10 m/s² with negative direction → accident
4. **Extreme Rotation**: Gyroscope magnitude > 30 rad/s → accident

## Project Structure

```
accident-detection-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/accidentdetection/accident/detection/backend/
│   │   │       ├── controller/     # REST Controllers
│   │   │       ├── service/        # Business Logic
│   │   │       ├── repository/     # Data Access Layer
│   │   │       ├── entity/         # JPA Entities
│   │   │       ├── dto/            # Data Transfer Objects
│   │   │       ├── security/       # Security Configuration
│   │   │       ├── exception/      # Exception Handling
│   │   │       └── util/           # Utility Classes
│   │   └── resources/
│   │       └── application.properties
├── ml-service/                     # Python ML Service
│   ├── app.py
│   ├── requirements.txt
│   └── README.md
├── schema.sql                      # Database Schema
└── pom.xml
```

## Security

- JWT-based authentication
- Password encryption using BCrypt
- CORS enabled for frontend integration
- All endpoints except `/api/auth/**` require authentication

## Testing

### Using curl

**Register:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "dob": "1990-01-01",
    "phone": "+1234567890",
    "password": "test123",
    "contactNumber1": "+0987654321"
  }'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "+1234567890",
    "password": "test123"
  }'
```

**Send Sensor Data:**
```bash
curl -X POST http://localhost:8080/api/sensor/sendSensorData \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "accelX": 10.5,
    "accelY": 5.2,
    "accelZ": 2.1,
    "gyroX": 15.3,
    "gyroY": 8.7,
    "gyroZ": 3.4,
    "speed": 45.0,
    "timestamp": "2024-01-15T10:30:00",
    "latitude": 40.7128,
    "longitude": -74.0060
  }'
```

## Environment Variables

You can also configure Twilio credentials using environment variables:

```bash
export TWILIO_ACCOUNT_SID=your-account-sid
export TWILIO_AUTH_TOKEN=your-auth-token
export TWILIO_PHONE_NUMBER=+1234567890
```

## Troubleshooting

1. **Database Connection Error**: Ensure MySQL is running and credentials are correct
2. **ML Service Unavailable**: Ensure Python ML service is running on port 5000
3. **Twilio SMS Not Sending**: Verify Twilio credentials and phone number format
4. **JWT Token Invalid**: Check token expiration and secret key configuration

## License

This project is for educational purposes.

