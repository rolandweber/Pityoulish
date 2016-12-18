# Pityoulish
A collection of in-class ~~programming~~ debugging exercises to support a course on _Distributed Systems_.

## Notes for...
### ...Students
If you are a student working on an exercise, this is the wrong repository. Check with your instructor on how to access your current exercise.
This repository contains _all_ exercises, and is missing the gaps you are supposed to fill in. Furthermore, it contains the code for clients and servers, while the exercises are about the clients.

If you are a student cheating on an exercises, this is the right repository.
It contains the code for _all_ exercises, and is missing the gaps you are supposed to fill in. But what's the point of cheating on these exercises? You're not getting graded for them, are you?

Consider becoming a [developer](#developers), too. After the exercise, of course :-)

### ...Instructors
_To be written..._
You might be interested in the [background](#background) though.

### ...Developers
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
