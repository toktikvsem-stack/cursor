#!/usr/bin/env python3
"""
A simple greeting application that says hello to users.
"""

def greet_user(name="World"):
    """
    Greet a user with a hello message.
    
    Args:
        name (str): The name of the user to greet. Defaults to "World".
    
    Returns:
        str: A greeting message.
    """
    return f"Привет, {name}!"


def main():
    """Main function to run the greeting application."""
    print(greet_user())
    
    # Get user input
    user_name = input("Enter your name: ")
    if user_name.strip():
        print(greet_user(user_name))


if __name__ == "__main__":
    main()
