# super simple makefile
# call it using 'make NAME=name_of_code_file_without_extension'
# (assumes a .java extension)
NAME = "Fotag"

all:
	javac *.java

run: all
	java $(NAME)

clean:
	rm -rf *.class
