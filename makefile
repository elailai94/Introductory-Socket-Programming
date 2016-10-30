##==============================================================================
## Introductory Socket Programming
##
## @description: Makefile for client.java and server.java
## @author: Elisha Lai
## @version: 1.0 10/11/2016
##==============================================================================

all:
	@echo "Compiling..."
	javac *.java

clean:
	@echo "Cleaning..."
	rm *.class
