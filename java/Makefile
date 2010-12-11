#%.class: %.java
#	javac -sourcepath ./src/ -d ./bin/ $<

.PHONY: client server clean all

bin/%.class: src/%.java bin
	javac -sourcepath ./src -classpath ./bin -d ./bin $<

client: bin/Job.class bin/JobQueue.class bin/Client.class

server: bin/Job.class bin/JobQueue.class bin/Server.class

all: client server

clean:
	rm bin/*.class

