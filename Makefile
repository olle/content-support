JAVA_HOME=$(shell unset JAVA_HOME; /usr/libexec/java_home -v 21)

all:
	./mvnw formatter:format verify

.PHONY: tidy pretty format f
tidy pretty format f:
	./mvnw formatter:format

.PHONY: clean c
clean c:
	./mvnw clean
