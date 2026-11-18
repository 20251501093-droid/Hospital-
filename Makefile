CXX ?= $(shell if command -v clang++ >/dev/null 2>&1; then echo clang++; elif command -v g++ >/dev/null 2>&1; then echo g++; else echo c++; fi)
CXXFLAGS = -std=c++17 -Wall -Wextra -O2 -pthread

TARGET = hms
SRCS = main.cpp

all: $(TARGET)

$(TARGET): $(SRCS)
	$(CXX) $(CXXFLAGS) -o $(TARGET) $(SRCS)
	@echo "Build successful! Run with: ./$(TARGET)"

clean:
	rm -f $(TARGET) hms.exe

run: $(TARGET)
	./$(TARGET)

.PHONY: all clean run
