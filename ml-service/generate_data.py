import numpy as np
import pandas as pd

np.random.seed(42)
rows = []

for _ in range(5000):
    accel = np.random.normal(0, 2, 3)
    gyro = np.random.normal(0, 1, 3)
    speed = np.random.uniform(30, 80)
    accident = 0

    if np.random.rand() < 0.25:
        accel = np.random.normal(18, 5, 3)
        gyro = np.random.normal(10, 4, 3)
        speed = np.random.uniform(0, 10)
        accident = 1

    rows.append([
        accel[0], accel[1], accel[2],
        gyro[0], gyro[1], gyro[2],
        speed, accident
    ])

df = pd.DataFrame(rows, columns=[
    "accel_x","accel_y","accel_z",
    "gyro_x","gyro_y","gyro_z",
    "speed","accident"
])

df.to_csv("sensor_data.csv", index=False)
print("✅ sensor_data.csv created")
