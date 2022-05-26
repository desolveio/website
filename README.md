# Desolve Website

### Description
This repository contains two separate components. The frontend and the backend. 

### Prerequisites
Folloiwng tools should be installed on your machine:
- Java 11
- Node/NPM/Yarn

### Development
Creating the development environment:
- Create a run configuration for Frontend, with the NPM type
  - Ensure under the scripts section you have `start` selected
- Create a run configuration for Backend, with the Ktor type
  - Ensure under the vm args to add `-Dio.ktor.development=true`

When running the development environment:
- **NOTE:** In the development environment the backend will not serve the React content,
- You'll be wanting to access the frontend by access it at https://localhost:3000.
- If you specifically need to test the backend, you can access it at https://localhost:8080

### Production
To utilize a production build of this project simply do the following:
- In the root directory: `gradle build`

This will create a fat-jar that includes everything needed to run the whole website. In a production environment, React is served via Ktor.

