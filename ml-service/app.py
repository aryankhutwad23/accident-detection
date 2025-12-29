from flask import Flask, request, jsonify
from flask_cors import CORS
import numpy as np
import joblib

app = Flask(__name__)
CORS(app)

# Load trained ML model
model = joblib.load("accident_model.pkl")
hep
@app.route("/predict", methods=["POST"])
def predict():
    data = request.json

    features = np.array([[
        data["accel_x"],
        data["accel_y"],
        data["accel_z"],
        data["gyro_x"],
        data["gyro_y"],
        data["gyro_z"],
        data["speed"]
    ]])

    prediction = model.predict(features)[0]
    probability = model.predict_proba(features)[0][1]

    return jsonify({
        "accident": bool(prediction),
        "confidence": round(float(probability), 2)
    })

@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "OK"})

if __name__ == "__main__":
    app.run(port=5000, debug=True)
