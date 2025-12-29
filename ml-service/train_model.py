import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score
import joblib

# Load dataset
df = pd.read_csv("sensor_data.csv")

X = df.drop("accident", axis=1)
y = df["accident"]

# Train-test split
X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size=0.2, random_state=42
)

# Train model
model = RandomForestClassifier(
    n_estimators=100,
    max_depth=10,
    random_state=42
)

model.fit(X_train, y_train)

# Accuracy
preds = model.predict(X_test)
print("✅ Accuracy:", accuracy_score(y_test, preds))

# Save model
joblib.dump(model, "accident_model.pkl")
print("✅ accident_model.pkl saved")
