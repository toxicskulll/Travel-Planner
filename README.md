# AI Travel Planner

A Java-based desktop application that combines AI-powered travel planning with user profile management. The application features a cyberpunk-inspired UI design with a focus on user experience.

## Features

### 1. User Authentication
- Secure login/signup system
- User profile management
- Password protection
- SQLite database integration

### 2. Travel Planning
- AI-powered itinerary generation
- Customizable travel preferences:
  - Destination selection
  - Date range specification
  - Budget planning (slider: $500-$5000)
  - Interest selection (Culture, Food, Shopping, Nature, Adventure)
- Real-time itinerary generation using Groq AI

### 3. User Profile Management
- Comprehensive profile information:
  - Personal details (Name, Gender, Age)
  - Contact information (Email)
  - Social media links (Instagram, LinkedIn)
- Profile completion tracking
- Stats dashboard showing:
  - Trips planned
  - Places visited
  - Reviews
  - Badges

### 4. Modern UI Features
- Cyberpunk-themed design
- Animated backgrounds
- Responsive components
- Custom styled elements
- Progress indicators

## Setup Requirements

1. Java Development Kit (JDK) 11 or higher
2. SQLite JDBC Driver
   - Download from: [SQLite JDBC Driver](https://github.com/xerial/sqlite-jdbc/releases/download/3.42.0.0/sqlite-jdbc-3.42.0.0.jar)
3. Groq API Key
   - Sign up at Groq to get your API key
4. Python 3.x
   - Required for AI integration

## Installation

1. Clone the repository
2. Add SQLite JDBC driver to your project's lib folder
3. Update `app.py` with your Groq API key:
   ```python
   self.groq_client = Groq(api_key="your-api-key-here")
   ```
4. Update PythonConnector.java with your Python script path:
   ```java
   ProcessBuilder pb = new ProcessBuilder("python", "path/to/app.py", prompt);
   ```

## Usage

1. Run App.java to start the application
2. Create new account using Sign Up
3. Access travel planner features
4. Generate Itenary
5. Manage your profile

## Database Structure

The application uses SQLite with the following schema:

```sql
CREATE TABLE users (
    username TEXT PRIMARY KEY,
    password TEXT NOT NULL,
    name TEXT,
    gender TEXT,
    age INTEGER,
    email TEXT,
    instagram TEXT,
    linkedin TEXT
);
```

## Technical Stack

- Frontend: Java Swing
- Backend: Java
- Database: SQLite
- AI Integration: Python + Groq
- UI Theme: Custom Cyberpunk

## Contributing

Feel free to fork the project and submit pull requests for any improvements.

## License

This project is open source and available under the MIT License.