# Fire Nation Invasion Game

This project is a simple multiplayer game called "Fire Nation Invasion" where players can join a lobby, see each other's characters, bullets, and enemies. The game uses JavaFX for the UI and Datagram Sockets for networking.

Members:

Jet Timothy V. Cerezo
Jazmin Iloso
Jay Gonzales Latigay

## Prerequisites

Before you begin, ensure you have met the following requirements:

- Java Development Kit (JDK) installed (version 8 or above)
- JavaFX SDK installed (for JavaFX dependencies)

## Installation

1. Clone this repository to your local machine:
    ```bash
    git clone https://github.com/jvcerezo/cmsc137-project
    ```
2. Make sure your environment is set up to include the JavaFX library. You can set the `--module-path` and `--add-modules` VM arguments in your IDE or while running the program.

## Running the Game Server

1. Navigate to the `src/application` directory:
    ```bash
    cd src/application
    ```

2. Compile the `GameServer` class:
    ```bash
    javac GameServer.java
    ```

3. Run the `GameServer`:
    ```bash
    java GameServer
    ```

## Running the Game Client

1. Navigate to the `src/application` directory:
    ```bash
    cd src/application
    ```

2. Run the Main.java depending on how many players you want present in the game. 
    ```bash
    javac Main.java
    ```

## How to Play

1. When you start the game, you will see the main menu with options to start the adventure, create a lobby, or join a lobby.

2. To start a game:
    - **Create a Lobby**: Enter a username and a lobby name to host a game.
    - **Join a Lobby**: Enter a username and select an existing lobby to join.

3. Once in the lobby, the host can start the game. Players will then see their heroes and can shoot bullets and encounter enemies.

4. Use the arrow keys to move your hero.

## Troubleshooting

- Ensure that the server is running before starting any clients.
- Check that the correct JavaFX library paths are set when running the client.
- Verify that the server and clients are using the same port (default: 9000).

