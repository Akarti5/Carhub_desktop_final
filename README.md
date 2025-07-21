# CarHub Desktop Application

A comprehensive car dealership management system built with Java Swing, Spring Framework, and PostgreSQL.

## Features

- **Modern Apple-Inspired UI**: Dark theme with lime green accents
- **Car Management**: Complete CRUD operations for vehicle inventory
- **Sales Tracking**: Comprehensive sales management with invoicing
- **Client Management**: Customer database with purchase history
- **Analytics Dashboard**: Real-time charts and business metrics
- **Reports**: Detailed business reports and analytics
- **User Management**: Role-based access control
- **Database Integration**: PostgreSQL with Hibernate ORM

## Technology Stack

- **Frontend**: Java Swing with FlatLaf Look and Feel
- **Backend**: Spring Framework with Spring Data JPA
- **Database**: PostgreSQL with Hibernate ORM
- **Charts**: JFreeChart for analytics
- **Build Tool**: Maven
- **Java Version**: 17+

## Prerequisites

- Java 17 or higher
- PostgreSQL 12 or higher
- Maven 3.8 or higher
- IntelliJ IDEA (recommended)

## Quick Start

1. **Clone the repository**
   \`\`\`bash
   git clone <repository-url>
   cd carhub-desktop
   \`\`\`

2. **Setup PostgreSQL Database**
   \`\`\`bash
   # Create database
   psql -U postgres -c "CREATE DATABASE carhub;"
   
   # Run setup script
   psql -U postgres -d carhub -f database/complete_setup.sql
   \`\`\`

3. **Configure Database Connection**
   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/carhub
   spring.datasource.username=carhub_user
   spring.datasource.password=carhub123
   \`\`\`

4. **Build and Run**
   \`\`\`bash
   mvn clean compile
   mvn exec:java -Dexec.mainClass="com.carhub.CarHubApplication"
   \`\`\`

5. **Login**
   - Username: `admin`
   - Password: `password`

## Project Structure

\`\`\`
src/main/java/com/carhub/
├── config/          # Spring configuration
├── entity/          # JPA entities
├── repository/      # Data access layer
├── service/         # Business logic
├── ui/
│   ├── components/  # Custom Swing components
│   ├── dialogs/     # Modal dialogs
│   ├── main/        # Main window and navigation
│   └── panels/      # Content panels
└── CarHubApplication.java
\`\`\`

## Database Schema

The application uses the following main entities:
- **Admin**: User management and authentication
- **Car**: Vehicle inventory management
- **Client**: Customer information
- **Sale**: Sales transactions
- **CarImage**: Vehicle photos
- **SystemSetting**: Application configuration

## Development

### IntelliJ IDEA Setup

1. Import the project as a Maven project
2. Configure database connection in Database Tools
3. Set up run configurations for easy development
4. Use the built-in database console for SQL operations

### Adding New Features

1. Create JPA entities in `entity` package
2. Add repository interfaces in `repository` package
3. Implement business logic in `service` package
4. Create UI components in appropriate `ui` subpackages

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For support and questions, please open an issue in the repository.
