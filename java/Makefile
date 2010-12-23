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
	cd bin; jar cfe HBNQServer Server Server.class Job.class JobQueue.class ServerThread.class FileTransfer.class; mv HBNQServer ..
	cd bin; jar cfe HBNQClient Client Client.class Job.class JobQueue.class FileTransfer.class; mv HBNQClient ..

bin:
	mkdir bin/


test: bin/JobTest.class bin/JobQueueTest.class
	java -enableassertions -classpath ./bin JobTest
	java -enableassertions -classpath ./bin JobQueueTest

clean:
	rm bin/*.class
	rm HBNQClient
	rm HBNQServer

