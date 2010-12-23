#%.class: %.java
#	javac -sourcepath ./src/ -d ./bin/ $<

.PHONY: clean test client server all

.DEFAULT: all

bin/%.class: src/%.java bin
	javac -sourcepath ./src -classpath ./bin -d ./bin $<

all: client server jar

client: bin bin/Job.class bin/JobQueue.class bin/Client.class bin/FileTransfer.class

server: bin bin/Job.class bin/JobQueue.class bin/Server.class bin/ServerThread.class bin/FileTransfer.class 

jar: client server
	jar cfe HBNQServer Server bin/Server.class bin/Job.class bin/JobQueue.class bin/ServerThread.class bin/FileTransfer.class
	jar cfe HBNQClient Client bin/Client.class bin/Job.class bin/JobQueue.class bin/FileTransfer.class

bin:
	mkdir bin/


test: bin/JobTest.class bin/JobQueueTest.class
	java -enableassertions -classpath ./bin JobTest
	java -enableassertions -classpath ./bin JobQueueTest

clean:
	rm bin/*.class
	rm HBNQClient
	rm HBNQServer

