# Pillpack API Exercise Implementation

## 0. Credit
This implementation uses **[json-simple](https://code.google.com/archive/p/json-simple/)** library from **Google** to decode and encode JSON data.

## 1. Platform
This implementation is completed on Linux operating system. This implementation uses **Java** as the primary programming language. It is implemented in **Eclipse IDE**.

## 2. Configuration
In order to run this program, make sure you have **json-simple** linked properly. The following steps will show how to link it properly to the project in **Eclipse**:
1. Download json-simple using this link: http://www.java2s.com/Code/Jar/j/Downloadjsonsimple11jar.htm
2. Extract **json-simple-1.1.jar** to a location you have access to
3. In **Eclipse**, **Right-Click** on the project, and select **Properties**
4. Select **Java Build Path** from left column
5. Select **Libraries** on top tab list
6. Click **Add External JARs** button on the right
7. Select **json-simple-1.1.jar** previously extracted and add it to the library section

Note: If you are using something other than Eclipse, you need to add **json-simple-1.1.jar** to the **CLASSPATH**

## 3. Additional Information
When running this program in Eclipse, the generated file will **not** be in the same folder as **Main.java**. Instead, it will be generated at root level (the same place as **src** folder). The generated file in this repo is just for reference.
