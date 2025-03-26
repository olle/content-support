JAVA_HOME=$(shell unset JAVA_HOME; /usr/libexec/java_home -v 21)

all: format verify

.PHONY: verify v
verify v:
	./mvnw verify

.PHONY: test t
test t:
	./mvnw test

.PHONY: tidy pretty format f
tidy pretty format f:
	./mvnw formatter:format

.PHONY: clean c
clean c:
	./mvnw clean
