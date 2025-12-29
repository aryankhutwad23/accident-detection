# Accident Detection ML Service

This is a synthetic ML service for accident detection using rule-based algorithms.

## Setup

1. Install Python dependencies:
```bash
pip install -r requirements.txt
```

2. Run the service:
```bash
python app.py
```

The service will run on `http://localhost:5000`

## API Endpoints

### POST /predict
Predicts if an accident has occurred based on sensor data.

**Request Body:**
```json
{
    "accel_x": 10.5,
    "accel_y": 5.2,
    "accel_z": 2.1,
    "gyro_x": 15.3,
    "gyro_y": 8.7,
    "gyro_z": 3.4,
    "speed": 45.0
}
```

**Response:**
```json
{
    "accident": true,
    "accel_magnitude": 12.34,
    "gyro_magnitude": 17.89
}
```

### GET /health
Health check endpoint.

**Response:**
```json
{
    "status": "healthy",
    "service": "ML Accident Detection Service"
}
```

## Rule-Based Detection Logic

The service uses the following rules to detect accidents:

1. **High Acceleration**: If acceleration magnitude > 15 m/s² → accident
2. **High Gyroscope + Speed Drop**: If gyroscope > 20 rad/s AND speed < 5 m/s → accident
3. **Sudden Deceleration**: If acceleration magnitude > 10 m/s² with negative direction → accident
4. **Extreme Rotation**: If gyroscope magnitude > 30 rad/s → accident

