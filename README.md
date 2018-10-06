# Pityoulish
A collection of programming or debugging exercises to support a course on _Distributed Systems_.
The focus is on classroom debugging exercises that can be completed in less than two hours.
(_not there yet_)

The Pityoulish exercises revolve around a common topic:
A server implements a Message Board which holds a limited number of messages.
Clients can fetch messages from the board and put up new ones.
Putting up messages requires a ticket from the server.

It's a fictitious problem with toy implementations. There is no authentication, no security, no high availability.
The initial classroom exercises showcase different communication techniques for the client/server interaction.
Future exercises might add encrypted communication, user authentication, or more.
You're welcome to help getting there :-)

Are you...
* a [student](#students)
* an [instructor](#instructors)
* a [contributor](#contributors) to-be
* a [developer](Development.md) getting started
* interested in the [background](Background.md) or [history](Background.md#history-and-lessons-learned)

## Notes for...
### ...Students
If you are a student working on an exercise, this is the wrong repository. Check with your instructor on how to access your current exercise.
This repository contains _all_ exercises, and is missing the gaps you are supposed to fill in. Furthermore, it contains the code for clients and servers, while the exercises are about the clients.

If you are a student cheating on an exercise, this is the right repository.
It contains the code for _all_ exercises, and is missing the gaps you are supposed to fill in. But what's the point of cheating on these exercises? You're not getting graded for them, are you?

Consider becoming a [contributor](#contributors), too. After the exercise, of course :-)

### ...Instructors
To squeeze the classroom exercises into the planned timeframe, students are provided with an almost-working client program to debug. The instructor is running the server, with output on a public display visible to all students.
Intentional omissions in the client program guide the students to points of interest in the code.
You may fork this project and put in your own omissions, to better match the topics you are teaching.

The current set of exercises comprises:
* Tutorial - IP addresses and hostnames.
  The Tutorial is for preparation at home. It explains how to debug the Java command-line programs of the classroom exercises.
  It takes between 30 minutes and two hours to complete, depending on each student's fluency in Java programming.
* Classroom - Java Sockets and TLVs.
  It's about socket connections and binary messages (PDUs) which need to be created, sent, received, and parsed.
  Shows how tedious it is to implement a protocol.
* Classroom - Java RMI.
  Part 1 is about the caller side: look up stubs and call remote objects.
  Part 2 is about the callee side: implement and register your own object to be called remotely.
  Shows how convenient, yet limiting, it is to use an API with generated stubs and skeletons.

As an instructor, I expect you to build the exercises and server components yourself.
See the [Development Readme](Development.md) for details.

I'm considering various [enhancements](https://github.com/rolandweber/pityoulish/issues?q=is%3Aissue+is%3Aopen+label%3Aenhancement) and additional exercises, both in class and at home.
You're welcome to contribute, see just below :-)
My primary focus is to make the existing exercises work better in class though.
You can read some more about the [background](Background.md) of this project and my [lessons learned](Background.md#history-and-lessons-learned). I'll update those at the end of each term, so you can follow my progress.


### ...Contributors
There are many ways in which you might contribute to Pityoulish: proof-reading exercises, commenting on or opening new issues, translating program output or exercises into different languages, coding, and more. 
If you're interested, have a look at issues labeled ["help wanted"](https://github.com/rolandweber/pityoulish/issues?q=is%3Aissue+is%3Aopen+label%3A%22help+wanted%22). A second label indicates the expected effort:
* **effort low**: It would take me less than an hour to implement, because I know the code inside out. You might take longer if you still need to get familiar with the codebase.
* **effort medium**: It would take me more than an hour to implement, but could be done in a single session on a long afternoon or evening.
* **effort high**: It would take me more than one programming session to implement. It's probably so complex that I'd like to sleep over the solution at least once before closing the issue.

See the [Development Readme](Development.md) for more information about working with the Pityoulish source code.

