# Deadlock Simulator 

A JavaFX desktop application that demonstrates Operating System deadlock concepts through interactive visualizations. The simulator helps users understand how deadlocks occur, how resources are allocated, and how different deadlock prevention and recovery algorithms work.

---

## Features

- Interactive Resource Allocation Graph (RAG)
- Deadlock scenario visualization
- Wait-Die deadlock prevention algorithm
- Priority-based resource allocation
- Victim selection for deadlock recovery

---

## Technologies Used

- Java 17+
- JavaFX 21
- Maven
- CSS
- Object-Oriented Programming (OOP)

---

## Project Structure

```text
deadlock-sim/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── deadlocksim/
│   │   │           ├── DeadlockSimulatorApp.java
│   │   │           ├── DeadlockScenePane.java
│   │   │           ├── DeadlockScenario.java
│   │   │           ├── PriorityAllocationPane.java
│   │   │           ├── VictimSelectionPane.java
│   │   │           ├── WaitDiePane.java
│   │   │           └── UiKit.java
│   │   │
│   │   └── resources/
│   │       └── app.css
│   │
│   └── test/
│
├── pom.xml
├── README.md
└── .gitignore
```

---

## Prerequisites

Before running the application, install:

- Java 17 or later
- Maven 3.9 or later

Verify the installation:

```bash
java -version
mvn -version
```

---

## 🚀 How to Run the Project

1. Clone the repository:  
Open your terminal and run:

```bash
git clone https://github.com/menahals/DeadlockSimulator.git
```

2. Navigate to the project folder

```bash
cd DeadlockSimulator
```

3. Run the application:

```bash
mvn clean javafx:run
```

---

## Screenshots

### Deadlock Scenario

<img width="468" height="287" alt="image" src="https://github.com/user-attachments/assets/83125d1b-0e5f-449a-a1ab-ef9339f3274f" />
<img width="468" height="74" alt="image" src="https://github.com/user-attachments/assets/4c7bc738-b20a-4dbd-9dbd-1d560573086c" />

### Dynamic Priority Allocation

<img width="468" height="152" alt="image" src="https://github.com/user-attachments/assets/1f6c1572-f02f-476c-8458-f317565f11ce" />
<img width="468" height="153" alt="image" src="https://github.com/user-attachments/assets/199f7b84-6d2a-44a2-abea-3987a9a661c8" />
<img width="468" height="175" alt="image" src="https://github.com/user-attachments/assets/c37c4687-953a-4475-bc12-e191cabe3b42" />


### Wait-Die Algorithm

<img width="468" height="218" alt="image" src="https://github.com/user-attachments/assets/4cfc7ccd-237c-457e-9521-31c08e7ead52" />
<img width="468" height="216" alt="image" src="https://github.com/user-attachments/assets/fb677f5e-5bfa-4bc5-80cd-cb67058fbe8c" />
<img width="468" height="236" alt="image" src="https://github.com/user-attachments/assets/3426d48e-6f44-421a-b926-cc11ca8603c3" />

### Victim Selection

<img width="468" height="159" alt="image" src="https://github.com/user-attachments/assets/a4201455-ee14-43f4-90a7-5501fe9443be" />
<img width="468" height="228" alt="image" src="https://github.com/user-attachments/assets/01dd7c00-97f1-41e2-aaa4-0730e63f5e7b" />

---

‼️Note: This project is shared for career-related purposes. If used for academic coursework, please follow your institution’s academic integrity policy.
