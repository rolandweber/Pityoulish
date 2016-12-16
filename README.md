# Pityoulish
A collection of ~~programming~~ debugging exercises to support a course on _Distributed Systems_.

## Notes for...
### ...Students
If you are a student working on an exercise, this is the wrong repository. Check with your instructor on how to access your current exercise.
This repository contains _all_ exercises, and is missing the gaps you are supposed to fill in. Furthermore, it contains the code for clients and servers, while the exercises are about the clients.

If you are a student cheating on an exercises, this is the right repository.
It contains the code for _all_ exercises, and is missing the gaps you are supposed to fill in. But what's the point of cheating on these exercises? You're not getting graded for them, are you?

### ...Instructors
_To be written..._
You might be interested in the [background](#background) though.

### ...Developers
_Instructions for setting up a dev environment are still missing._
As of 2016, all exercises use Java. My build environment comprises a JDK, Ant, ant-contrib, and JUnit 4.
Expect Hamcrest and JMockit to appear in the dependency during 2017.

## Background
I've been teaching a course on _Distributed Systems_ once a year, for over a decade now. It's mostly lectures and theory, so I tried to add some practice in the form of two programming exercises. Both implement basically the same client-server functionality, one with socket connections and a binary protocol, the other with Java RMI. I provided the server component, students could write their own client, and then I'd link the server to a projector and let the students try out their clients.

The programming exercises are completely voluntary and do not affect the grades in any way. Most of the students would try the first exercise, but not all of them successfully. Of those that succeeded, some spent way too much time on it. So the number of students that would attempt the second programming exercise was... low. Despite my attempts to lower the workload with utility classes and better instructions, it remained low.

In 2015, I tried a different approach. Instead of asking the students to program a client, I provided one - from which I had removed a few carefully selected code lines. The students just had to test all available commands of the client and fix the holes. Exceptions pointed them directly to the missing code.
The first programming exercise was done at home and tried out in the classroom. I received feedback that the task was too simple, but might work as an in-class exercise. So I held the second programming exercise in class.

As it turned out, the in-class programming exercise was too complex for most of the students. But I learned a lot about the problems they were facing. Too much information to digest in the exercise description. A lack of proficiency in Java programming. Too much code with an unknown structure to wade through.
I do believe that these problems can be addressed by an improved collection of programming exercises. Better instructions. Better descriptions of the available code. Gradual skill build-up, with self-paced preparation at home and supervised problem-solving in class. And supplemental programming tasks for those who are interested.

This project, Pityoulish, is the future home of my programming exercises. I already have a bunch of code to start with, but I don't want to just dump that here. Instead, I'm building an all-new set of programming exercises, and refactoring parts of the old ones as I go along. I'm not in a hurry though. The next class of students will face these exercises in October 2016. You can expect slow progress until summer, and last-minute panic in September.
