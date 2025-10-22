#!/bin/bash

echo "Downloading JUnit 4.13.2 and Hamcrest..."
wget -q https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar
wget -q https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar

echo ""
echo "Compiling test classes..."
javac -cp .:junit-4.13.2.jar:hamcrest-core-1.3.jar *.java

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

echo ""
echo "Running CustomerRecordTest..."
java -cp .:junit-4.13.2.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore CustomerRecordTest

echo ""
echo "Running MergeSortExampleTest..."
java -cp .:junit-4.13.2.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore MergeSortExampleTest

echo ""
echo "Tests completed!"
