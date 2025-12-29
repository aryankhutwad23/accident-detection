# Quick Setup Guide

## Prerequisites Checklist

- [ ] Java 17+ installed
- [ ] Maven installed
- [ ] XAMPP installed and MySQL running
- [ ] Python 3.8+ installed
- [ ] Twilio account (for SMS functionality)

## Step-by-Step Setup

### 1. Database Setup (XAMPP)

1. Start XAMPP Control Panel
2. Start MySQL service
3. Open phpMyAdmin (http://localhost/phpmyadmin)
4. Import `schema.sql` file or run:
   ```sql
   CREATE DATABASE accident_detection_db;
   USE accident_detection_db;
   -- Then copy and paste the contents of schema.sql
   ```

### 2. Configure Application

Edit `src/main/resources/application.properties`:

```properties
# Update MySQL password if you have one
spring.datasource.password=your_mysql_password

# Update Twilio credentials (or use environment variables)
twilio.account.sid=your-twilio-account-sid
twilio.auth.token=your-twilio-auth-token
twilio.phone.number=+1234567890
```

### 3. Start Python ML Service

```bash
cd ml-service
pip install -r requirements.txt
python app.py
```

The ML service should start on `http://localhost:5000`

### 4. Start Spring Boot Application

```bash
mvn clean install
mvn spring-boot:run
```

The backend should start on `http://localhost:8080`

### 5. Test the System

**Test Registration:**
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

**Test Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "phone": "+1234567890",
    "password": "test123"
  }'
```

Save the `token` from the response for authenticated requests.

**Test Sensor Data:**
```bash
curl -X POST http://localhost:8080/api/sensor/sendSensorData \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{
    "accelX": 18.5,
    "accelY": 12.2,
    "accelZ": 8.1,
    "gyroX": 25.3,
    "gyroY": 18.7,
    "gyroZ": 15.4,
    "speed": 3.0,
    "timestamp": "2024-01-15T10:30:00",
    "latitude": 40.7128,
    "longitude": -74.0060
  }'
```

## Troubleshooting

### MySQL Connection Error
- Check if MySQL is running in XAMPP
- Verify username/password in `application.properties`
- Ensure database `accident_detection_db` exists

### ML Service Error
- Check if Python service is running on port 5000
- Verify Flask is installed: `pip list | grep Flask`
- Check firewall settings if connection fails

### Twilio SMS Not Sending
- Verify Twilio credentials are correct
- Check phone number format (must include country code, e.g., +1234567890)
- Ensure Twilio account has sufficient balance
- Check Twilio console for error logs

### JWT Token Issues
- Ensure token is included in Authorization header: `Bearer <token>`
- Check if token has expired (default: 24 hours)
- Verify JWT secret key in `application.properties`

## Environment Variables (Alternative Configuration)

Instead of editing `application.properties`, you can use environment variables:

```bash
export TWILIO_ACCOUNT_SID=your-account-sid
export TWILIO_AUTH_TOKEN=your-auth-token
export TWILIO_PHONE_NUMBER=+1234567890
```

## Port Configuration

- Spring Boot: `8080` (change in `application.properties`)
- ML Service: `5000` (change in `ml-service/app.py`)
- MySQL: `3306` (default XAMPP port)

## Next Steps

1. Test all API endpoints
2. Integrate with Android frontend
3. Configure production database
4. Set up proper logging
5. Add monitoring and health checks

