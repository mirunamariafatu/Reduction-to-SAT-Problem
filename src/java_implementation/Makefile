SRC = $(wildcard *.java)

TARGET = Main.class

build: $(TARGET)

$(TARGET): $(SRC)
	javac $^

clean:
	rm -f *.class

.PHONY: build clean
