#%.class: %.java
#	javac -sourcepath ./src/ -d ./bin/ $<

.PHONY: client server clean all test

bin/%.class: src/%.java bin
	javac -sourcepath ./src -classpath ./bin -d ./bin $<

client: bin/Job.class bin/JobQueue.class bin/Client.class

server: bin/Job.class bin/JobQueue.class bin/Server.class

all: client server

test: bin/JobTest.class bin/JobQueueTest.class
	java -enableassertions -classpath ./bin JobTest
	java -enableassertions -classpath ./bin JobQueueTest

clean:
	rm bin/*.class

