#!/usr/bin/env python3
"""
Tests for the greeting application.
"""

import sys
import os

# Add the parent directory to the path to import hello
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from hello import greet_user


def test_default_greeting():
    """Test the default greeting."""
    result = greet_user()
    assert result == "Привет, World!", f"Expected 'Привет, World!' but got '{result}'"
    print("✓ test_default_greeting passed")


def test_personalized_greeting():
    """Test personalized greeting with a name."""
    result = greet_user("Alice")
    assert result == "Привет, Alice!", f"Expected 'Привет, Alice!' but got '{result}'"
    print("✓ test_personalized_greeting passed")


def test_greeting_with_spaces():
    """Test greeting with a name that has spaces."""
    result = greet_user("John Doe")
    assert result == "Привет, John Doe!", f"Expected 'Привет, John Doe!' but got '{result}'"
    print("✓ test_greeting_with_spaces passed")


def run_tests():
    """Run all tests."""
    print("Running tests...\n")
    test_default_greeting()
    test_personalized_greeting()
    test_greeting_with_spaces()
    print("\nAll tests passed! ✓")


if __name__ == "__main__":
    run_tests()
