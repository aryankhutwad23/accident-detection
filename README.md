# Accident detection app 🚨

## Our solution: AI that reacts instantly
In India, someone dies in a road accident every 3–4 minutes. That’s 485 deaths every single day. Most are preventable if emergency services arrive just 7–10 minutes faster. Our app closes that gap by automating the alert before a victim can even reach a phone.

A smartphone-based AI system that detects accidents in milliseconds and automatically alerts emergency contacts.

---

## How it works
1. **Setup:** User registers with email and adds 2 emergency contacts.
2. **Detection:** Sensors continuously monitor accelerometer and gyroscope.
3. **ML verification:** Spikes matched against trained RandomForest model.
4. **Alert & action:** 30-sec confirmation popup; SMS + location sent to contacts.

---

## How to start this application
1. **Run the backend** 
2. **Run the frontend** 
3. **Run the Ml model**
---

- Register → User signs up with email & password.
<img width="359" height="795" alt="Screenshot 2025-12-29 073937" src="https://github.com/user-attachments/assets/516cd9bb-de6a-474b-a5eb-98ea680b9911" />

- Login → User logs in to access dashboard.
<img width="351" height="809" alt="Screenshot 2025-12-29 073858" src="https://github.com/user-attachments/assets/90794d97-95c2-4460-8261-f9cbf574b532" />

- Add Emergency Contacts → Save 2 trusted numbers.
<img width="362" height="806" alt="Screenshot 2025-12-29 073659" src="https://github.com/user-attachments/assets/a0e9c66e-1df9-4af9-bf89-4bb503f45ebd" />

- View Profile → Check user info & contacts.
<img width="355" height="804" alt="Screenshot 2025-12-29 073819" src="https://github.com/user-attachments/assets/94c31d7c-1f42-40d0-9f0b-ab308c8404f8" />

- Database Stored → Data saved in MySQL tables.
<img width="1204" height="549" alt="Screenshot 2025-12-29 075421" src="https://github.com/user-attachments/assets/840b5a84-b72d-4daf-b430-0da235bdfa4f" />

- Simulate Accident → Sensors trigger detection popup.
<img width="357" height="808" alt="Screenshot 2025-12-29 073838" src="https://github.com/user-attachments/assets/be596220-f527-4a14-b2b9-2bfad71c0b52" />

- SMS Received → Contacts get alert + live location link.
<img width="540" height="1196" alt="image" src="https://github.com/user-attachments/assets/7f880dc4-a270-44a5-b6d0-3a20fc3561d9" />
<img width="540" height="1196" alt="image" src="https://github.com/user-attachments/assets/cfea32a8-f6e3-4dae-a1c9-fbd97ded4cc2" />

---

## Tech stack
- **Frontend:** Android (Kotlin, Jetpack Compose)
- **Backend:** Spring Boot, MySQL, Twilio
- **ML model:** RandomForest (Python, scikit-learn)

---

## Extra features to add
- **Hospital API:** Auto-route nearest hospital and notify.
- **Voice alerts:** Audible prompts for the user during detection.
- **Messaging system:** In-app chat/status for responders.
- **Backend deployment:** Host on cloud (Render/Heroku/Vercel + Railway/MySQL).

---
