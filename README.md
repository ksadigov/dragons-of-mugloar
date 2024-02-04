# Dragons of Mugloar: A Scripting Adventure (Backend)

Welcome to Dragons of Mugloar, a fun game where you complete tricky tasks, figure out messages, and win by using smart
play and coding skills. This project uses the [Dragons of Mugloar API](https://www.dragonsofmugloar.com/doc/) to play
the game automatically.

## Build - Used Technologies

We used a variety of powerful tools and technologies to make the game work smoothly and efficiently:

- **Java**: We use version 17 for many parts of the game.
- **Spring Boot**: We use version 3.2.2 for the game's backend.
- **Build Tools**: We use Maven to manage our game's building blocks and keep everything organized.
- **Additional Tools**:
    - Lombok helps us write less code by hand.
    - JUnit5 tests our code to make sure everything works well with Java 17.
    - MapStruct makes it easier to turn one type of data into another.

## Installation & Running the Game

To set up and run the project, follow these steps:

1. Clone the repository to your local machine.
2. Open the project in your preferred IDE and ensure that Java 17 is set as the SDK.
3. Navigate to the `application.yaml` file to configure project-specific settings, such as `retryLimit` for specifying
   the number of game iterations.
4. Use Maven to build the project by running `mvn clean install`.
5. Start the application by running `java -jar target/mugloar-0.0.1-SNAPSHOT.jar`.

### Algorithmic Insights

Winning the game depends on:

- The strategic selection of tasks, prioritizing those with the best balance of risk and reward.
- Effective resource management, particularly in terms of gold, to ensure the player can afford crucial items when
  needed.
