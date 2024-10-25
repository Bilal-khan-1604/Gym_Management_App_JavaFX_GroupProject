# Gym Management System

This Gym Management System project is designed to help gym administrators manage essential gym operations, such as member registration, trainer allocation, equipment tracking, and other day-to-day activities. The project is built with a focus on efficient data management and ease of use for both staff and gym members.

## Features

- **Member Management**: Register and manage member information, track memberships, and manage renewals.
- **Trainer Management**: Manage trainer profiles, assign trainers to members, and track trainer schedules.
- **Equipment Tracking**: Track gym equipment details, maintenance schedules, and availability.
- **Login and Registration**: Secure login system for staff and members, with options for account creation and password recovery.

## Technologies Used

- **Frontend**: JavaFX (for the graphical user interface)
- **Backend**: Java (Core logic implementation)
- **Database**: MySQL, connected via JDBC (SQL JDBC connector)
- **Build Tool**: Maven (for dependency management and build automation)
- **Development Environment**: XAMPP for local database hosting

## Class Structure

- **MemberModel**: Manages member details and membership statuses.
- **TrainerModel**: Handles trainer profiles, expertise, and assignment to members.
- **EquipmentModel**: Tracks equipment information, including maintenance schedules.
- **ControllerClasses**: Manages database interactions, including login and account creation and other functionalities.

## Project Structure
The project follows a Maven structure:

```graphql
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── controllers      # Java classes for managing login, members, trainers, and equipment
│   │   │   ├── models           # MemberModel, TrainerModel, EquipmentModel, etc.
│   │   ├── resources            # FXML files for JavaFX layouts
├── pom.xml                      # Maven dependencies and build configuration
```
## Setup and Installation

1. **Database Setup**:
    - Install [XAMPP](https://www.apachefriends.org/index.html) and start MySQL.
    - Create a database for the project and import the provided SQL schema.
    - Update the database configuration in the project files to match your MySQL credentials.

2. **Project Setup**:
    - Clone the repository and open it in your preferred IDE. Ensure Maven is set up.
    - Ensure the SQL JDBC connector is configured in your project.
    - Compile and run the application.

## Usage

1. **Login**: Use the login page to access admin or member features.
2. **Manage Members**: Register new members, update details, and manage membership statuses.
3. **Assign Trainers**: List trainers based on their preferences and trainer availability.
4. **Equipment Management**: Track and update equipment statuses and availability.