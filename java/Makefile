#%.class: %.java
#	javac -sourcepath ./src/ -d ./bin/ $<

.PHONY: clean test client server all

bin/%.class: src/%.java bin
	javac -sourcepath ./src -classpath ./bin -d ./bin $<

client: bin bin/Job.class bin/JobQueue.class bin/Client.class

server: bin bin/Job.class bin/JobQueue.class bin/Server.class bin/ServerThread.class

bin:
	mkdir bin/

all: client server

test: bin/JobTest.class bin/JobQueueTest.class
	java -enableassertions -classpath ./bin JobTest
	java -enableassertions -classpath ./bin JobQueueTest

clean:
	rm bin/*.class

