# Pityoulish
A collection of programming or debugging exercises to support a course on _Distributed Systems_.
The focus is on classroom debugging exercises that can be completed in less than two hours.
(not there yet)

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
* a [contributor](#contributors) to be
* interested in the [background](#background) or [history](#history-and-lessons-learned)

## Notes for...
### ...Students
If you are a student working on an exercise, this is the wrong repository. Check with your instructor on how to access your current exercise.
This repository contains _all_ exercises, and is missing the gaps you are supposed to fill in. Furthermore, it contains the code for clients and servers, while the exercises are about the clients.

If you are a student cheating on an exercises, this is the right repository.
It contains the code for _all_ exercises, and is missing the gaps you are supposed to fill in. But what's the point of cheating on these exercises? You're not getting graded for them, are you?

Consider becoming a [contributor](#contributors), too. After the exercise, of course :-)

### ...Instructors
_To be written..._
You might be interested in the [background](#background) though.

### ...Contributors
There are many ways in which you might contribute to Pityoulish: proof-reading exercises, commenting on or opening new issues, translating program output or exercises into different languages, coding, and more. 
If you're interested, have a look at issues labeled ["help wanted"](https://github.com/rolandweber/pityoulish/issues?q=is%3Aissue+is%3Aopen+label%3A%22help+wanted%22). A second label indicates the expected effort:
* **effort low**: It would take me less than an hour to implement, because I know the code inside out. You might take longer if you still need to get familiar with the codebase.
* **effort medium**: It would take me more than an hour to implement, but could be done in a single session on a long afternoon or evening.
* **effort high**: It would take me more than one programming session to implement. It's probably so complex that I'd like to sleep over the solution at least once before closing the issue.

_Instructions for setting up a dev environment are still missing._
As of 2016, all exercises are coded in Java. My build environment comprises a JDK, Ant, ant-contrib, and JUnit 4.
Expect JMockit and maybe Hamcrest to appear in the dependency list during 2017.

My workflow is based on an editor, command line, and browser. Sometimes [grip](https://github.com/joeyespo/grip) for previewing markdown before committing. When building exercises, the Java sources are pre-processed with Ant to put in gaps that students have to fill. You might find it tricky to use an IDE for that step.
The actual coding and unit-testing during development is done without pre-processing though. It will work smoothly with an IDE.

# Background
I've been teaching a course on _Distributed Systems_ once a year, for over a decade now. It's mostly lectures and theory, so I tried to add some practice in the form of two programming exercises. Both implement basically the same client-server functionality, one with socket connections and a binary protocol, the other with Java RMI. I provided the server component, students could write their own client, and then I'd link the server to a projector and let the students try out their clients.

The programming exercises are completely voluntary and do not affect the grades in any way. Most of the students would try the first exercise, but not all of them successfully. Of those that succeeded, some spent way too much time on it. So the number of students that would attempt the second programming exercise was... low. Despite my attempts to lower the workload with utility classes and better instructions, it remained low.

In 2015, I tried a different approach. Instead of asking the students to program a client, I provided one - from which I had removed a few carefully selected code lines. The students just had to test all available commands of the client and fix the holes. Exceptions pointed them directly to the missing code.
The first programming exercise was done at home and tried out in the classroom. I received feedback that the task was too simple, but might work as an in-class exercise. So I held the second programming exercise in class.

As it turned out, the in-class programming exercise was too complex for most of the students. But I learned a lot about the problems they were facing. Too much information to digest in the exercise description. A lack of proficiency in Java programming. Too much code with an unknown structure to wade through.
These problems, I believe, can be addressed. By an improved collection of programming exercises. With better instructions. Better descriptions of the available code. Better delivery from my side. Gradual skill build-up, with self-paced preparation at home and supervised problem-solving in class. And supplemental programming tasks for those who are interested.

This project, Pityoulish, is the new home of my programming exercises. I had a bunch of code to start with, but didn't want to just dump that here. Instead, I'm building an all-new set of programming exercises, while re-using parts of the old code where appropriate. As of December 2016, there is a Tutorial, and two classroom exercises replacing the old ones. I still got a long way to go regarding the delivery though. And plenty of [enhancements](https://github.com/rolandweber/pityoulish/issues?q=is%3Aissue+is%3Aopen+label%3Aenhancement) in mind for the exercises.

I'm teaching Distributed Systems from October through December each year. You can expect slow progress until summer, and last-minute panic in September. The flurry winds down as I wrap up for the end of the year.

## History and Lessons Learned

### 2016

Feedback:
[Tutorial #37](https://github.com/rolandweber/pityoulish/issues/37),
[Sockets #38](https://github.com/rolandweber/pityoulish/issues/38),
[Java RMI #48](https://github.com/rolandweber/pityoulish/issues/48)

The tutorial worked as intended. Both classroom exercises went way over time. I relied on the written exercise instructions and background. Still too much information to digest. Some students don't even notice the method to copy from when it's right on their screen, just a few lines above the gap to fill in. Some notable feedback:
* A bit of hands-on is good.
* That's debugging, not programming.
* We didn't apply the stuff we learned before.
* We didn't get the big picture.

For 2017, I'm planning more guidance during the exercises. I'll lay out the big picture during the classroom exercise, interleaving my explanations with the programming/debugging tasks. Also, I intend to use the exercises more as introduction to the topics, rather than doing them afterwards.
It's true that students don't get to apply the stuff that I'm teaching. That's because I'm not teaching low-level programming details, and I don't want to. The exercises are meant as an illustration. I need to deliver them differently so that they can serve their purpose.

### 2015
I reworked the exercises to a fill-in-the-gaps style. Handed out instructions, a client with gaps, and a server to run at home.

First exercise, sockets+TLV: Home programming. Feedback from a few students: too easy, we wanted to do more. I asked whether the exercise would have been easy enough to do it in-class. Response: yes.

Second exercise, Java RMI: In-class. Groups of two, everybody tried, most failed. Many were unfamiliar with Java programming. Some didn't know how to interpret stack traces. Others couldn't get their IDE to generate an executable JAR. Some loaded the server JAR into the same IDE project at the client JAR, totally messing up the client-server boundary. In consequence, one group created their own server-side objects instead of calling my server. Feedback: Still way too much information to digest during the exercise.

I created the [Pityoulish](https://github.com/rolandweber/pityoulish) project and wrote the Tutorial. It will help students to set up their development environment and learn the basic steps for debugging at home, before the first classroom exercise.

### before 2015
Program at home, try out in class. Some students tried the first exercise (sockets+TLV), many failed. Those that succeeded spent way more time than I had anticipated or intended. Few even tried the second exercise (RMI).
