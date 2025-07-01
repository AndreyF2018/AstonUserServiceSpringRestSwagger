package org.example;

import org.example.controllers.UserController;
import org.example.models.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserController userController = new UserController();

        while (true) {
            System.out.println("\nOptions:");
            System.out.println("1. Find user by ID");
            System.out.println("2. Find user by email");
            System.out.println("3. Get all users");
            System.out.println("4. Save new user");
            System.out.println("5. Update user");
            System.out.println("6. Delete user");
            System.out.println("7. Exit");
            System.out.print("Enter your option: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1: {
                    System.out.print("User id = ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    if (userController.finById(id) == null) {
                        System.out.println("User not found");
                    } else
                    {
                        System.out.println(userController.finById(id));
                    }
                    break;
                }
                case 2: {
                    System.out.print("User email = ");
                    String email = scanner.nextLine();
                    if (userController.findByEmail(email) == null) {
                        System.out.println("User not found");
                    } else
                    {
                        System.out.println(userController.findByEmail(email));
                    }
                    break;
                }
                case 3: {
                    List<User> users = userController.findAll();
                    System.out.println("Users:");
                    for (User user : users) {
                        System.out.println(user);
                    }
                    break;
                }
                case 4: {
                    String name;
                    String email;
                    int age;
                    try {
                        System.out.print("User name = ");
                        name = scanner.nextLine();
                        System.out.print("User email = ");
                        email = scanner.nextLine().toLowerCase();
                        System.out.print("User age = ");
                        age = scanner.nextInt();
                        scanner.nextLine();
                    } catch (Exception e) {
                        System.out.println("Input error" + e);
                        break;
                    }
                    if (age < 1 || age > 100) {
                        System.out.println("Incorrect age");
                        break;
                    }
                    LocalDateTime createdAt = LocalDateTime.now();
                    User newUser = new User(name, email, age, createdAt);
                    try {
                        userController.save(newUser);
                    } catch (Exception e) {
                        System.out.println("Error: " + e);
                        break;
                    }
                    System.out.println("User added successfully");
                    break;
                }
                case 5: {
                    // Для редактирования пользователя можно найти его по id или по email
                    System.out.print("Enter user id or user email: ");
                    String idOrEmail = scanner.nextLine().toLowerCase();
                    User updatedUser;
                    try {
                        // Если получится запарсить в int - значит введено число,
                        // и поиск по id
                        updatedUser = userController.finById(Integer.parseInt(idOrEmail));
                    } catch (NumberFormatException e) {
                        // В противном случае - поиск по email
                        updatedUser = userController.findByEmail(idOrEmail);
                    }
                    if (updatedUser == null) {
                        System.out.println("User not found");
                        break;
                    }
                    System.out.println("Updated user: " + updatedUser);
                    String name;
                    String email;
                    int age;
                    try {
                        System.out.print("New user name: ");
                        name = scanner.nextLine();
                        System.out.print("New user email: ");
                        email = scanner.nextLine().toLowerCase();
                        System.out.print("New user age: ");
                        age = scanner.nextInt();
                        scanner.nextLine();
                        if (age < 1 || age > 100) {
                            System.out.println("Incorrect age");
                            break;
                        }
                    } catch (Exception e) {
                        System.out.println("Input Error" + e);
                        break;
                    }
                    updatedUser.setName(name);
                    updatedUser.setEmail(email);
                    updatedUser.setAge(age);
                    try {
                        userController.update(updatedUser);
                        System.out.println("User updated successfully");
                    }
                    catch (Exception e) {
                        System.out.println("Error: " + e);
                    }
                    break;
                }
                case 6: {
                    // Для удаления также поиск или по id, или по email
                    System.out.print("Enter user id or user email: ");
                    String idOrEmail = scanner.nextLine().toLowerCase();
                    User deleteddUser;
                    try {
                        deleteddUser = userController.finById(Integer.parseInt(idOrEmail));
                    } catch (NumberFormatException e) {
                        deleteddUser = userController.findByEmail(idOrEmail);
                    }
                    if (deleteddUser == null) {
                        System.out.println("User not found");
                        break;
                    }
                    System.out.println("This user will be deleted:");
                    System.out.println(deleteddUser);
                    System.out.print("Are you sure? (Y/N): ");
                    String input = scanner.nextLine().trim().toUpperCase();
                    if (input.equals("Y")) {
                        userController.delete(deleteddUser);
                        System.out.println("User deleted successfully ");
                    } else {
                        System.out.println("Deletion was cancelled");
                    }
                    break;
                }
                case 7: {
                    System.exit(0);
                }
                default: {
                    System.out.println("Incorrect option");
                    break;
                }
            }
        }
    }
}